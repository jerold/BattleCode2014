package DeepBlue;

import battlecode.common.*;

import javax.sound.midi.SysexMessage;
import java.util.ArrayList;

/**
 * Created by Jerold Albertson on 1/12/14.
 *
 */
public class RoadMap {

    RobotController rc;
    UnitCache cache;

    static final int TILE_VOID = 999;

    int MAP_WIDTH;
    int MAP_HEIGHT;
    int[][] roadMap;
    double[][] cgMap;
    int[][] cowGrowthMap;

    static final double MACRO_PATH_MAX_VOID_DENSITY = .75;
    static final int NO_PATH_EXISTS = -1;
    static final int MIN_NODE_SPACING = 6;
    static final int MAX_NODES_IN_LINE = 8;

    int nodeCount;
    int usableNodeCount;
    int nodesInLine;
    int nodeSpacing;
    int nodePadding;
    int[][] macroNextNode;
    boolean[] macroUsableNode;
    int[][] macroPathDistance;

    public enum PathingStrategy {
        DefaultBug,
        SmartBug
    }

    PathingStrategy pathingStrat;
    double voidDensity;

    boolean mapUploaded;
    int mapProgress;
    boolean macroPathingUploaded;
    int macroPathingProgress;
    boolean expectMacroPathing;

    RoadMap(RobotController inRc, UnitCache inCache) throws GameActionException
    {
        rc = inRc;
        cache = inCache;

        MAP_HEIGHT = rc.getMapHeight();
        MAP_WIDTH = rc.getMapWidth();
        roadMap = new int[MAP_WIDTH][MAP_HEIGHT];
        cowGrowthMap = new int[MAP_WIDTH][MAP_HEIGHT];
        voidDensity = 0.0;
        pathingStrat = PathingStrategy.DefaultBug;

        // Initialize Upload Flags and indicators
        mapUploaded = false;
        mapProgress = 0;
        macroPathingUploaded = false;
        macroPathingProgress = 0;
        expectMacroPathing = true;

        // Initialize Macro Path Variables and Direction Arrays
        usableNodeCount = 0;
        calibrateNodeStructure();
        macroNextNode = new int[nodeCount][nodeCount];
        macroUsableNode = new boolean[nodeCount];
        macroPathDistance = new int[nodeCount][nodeCount];

        // Initialize Map and Pathing signal Flags
        if (rc.getType() == RobotType.HQ)
            broadcastFlags();
    }




    //================================================================================
    // Facilitator Methods
    //================================================================================

    public void checkForUpdates() throws GameActionException
    {
        if (rc.getType() == RobotType.HQ) {
            if (!mapUploaded)
                assessMap();
            if (mapUploaded && expectMacroPathing && !macroPathingUploaded)
                assessMacroPathing();
        } else {
            if (!mapUploaded)
                readBroadcastMap();
            if (mapUploaded && expectMacroPathing && !macroPathingUploaded)
                readBroadcastMacro();
        }
    }

    public void broadcastFlags() throws GameActionException
    {
        broadcastMapUploadedFlag();
        broadcastMacroUploadedFlag();
    }

    public int valueForLocation(MapLocation loc) throws GameActionException
    {
        if (loc.y < MAP_HEIGHT && loc.y > 0 && loc.x < MAP_WIDTH && loc.x > 0)
            return roadMap[loc.x][loc.y];
        return TILE_VOID;
    }

    public Direction directionTo(MapLocation origin, MapLocation destination) throws GameActionException
    {
        int ordinalDir = origin.directionTo(destination).ordinal();
        if (valueForLocation(origin.add(Utilities.directionByOrdinal[ordinalDir])) != TILE_VOID) return Utilities.directionByOrdinal[ordinalDir];

        Direction[] dirs = Utilities.directionByOrdinal;

        // Rotate 1 Left and Right, 3 Checked
        Direction dirLeft = dirs[ordinalDir].rotateLeft();
        Direction dirRight = dirs[ordinalDir].rotateRight();
        if (valueForLocation(origin.add(dirLeft)) != TILE_VOID) {
            if (valueForLocation(origin.add(dirRight)) != TILE_VOID && valueForLocation(origin.add(dirRight)) < valueForLocation(origin.add(dirLeft))) return dirRight;
            else return dirLeft;
        } else if (valueForLocation(origin.add(dirRight)) != TILE_VOID) return dirRight;

        // Rotate 1 Left and Right, 5 Checked
        dirLeft = dirLeft.rotateLeft();
        dirRight = dirRight.rotateRight();
        if (valueForLocation(origin.add(dirLeft)) != TILE_VOID) {
            if (valueForLocation(origin.add(dirRight)) != TILE_VOID && valueForLocation(origin.add(dirRight)) < valueForLocation(origin.add(dirLeft))) return dirRight;
            else return dirLeft;
        } else if (valueForLocation(origin.add(dirRight)) != TILE_VOID) return dirRight;

        // Rotate 1 Left and Right, 7 Checked
        dirLeft = dirLeft.rotateLeft();
        dirRight = dirRight.rotateRight();
        if (valueForLocation(origin.add(dirLeft)) != TILE_VOID) {
            if (valueForLocation(origin.add(dirRight)) < TILE_VOID && valueForLocation(origin.add(dirRight)) < valueForLocation(origin.add(dirLeft))) return dirRight;
            else return dirLeft;
        } else if (valueForLocation(origin.add(dirRight)) != TILE_VOID) return dirRight;

        return dirs[ordinalDir].opposite();
    }

    private MapLocation locationForNode(int nodeId) {
        if ((nodeId%nodesInLine) >= nodesInLine/2) {
            MapLocation mirrorLocation = locationForNode(oppositeNodeId(nodeId));
            return new MapLocation(MAP_WIDTH-mirrorLocation.x-1, MAP_HEIGHT-mirrorLocation.y-1);
        }
        return new MapLocation((nodeId/nodesInLine) * nodeSpacing + nodePadding, (nodeId%nodesInLine) * nodeSpacing + nodePadding);
    }

    private int oppositeNodeId(int nodeId) {
        //System.out.println(nodeId + "< - >" + (nodeCount - nodeId - 1));
        return nodeCount - nodeId - 1;
    }

    private int nodeForLocation(MapLocation loc) {
        int nodeId = ((loc.x - nodePadding)/nodeSpacing) * nodesInLine; // THIS ISNT FINISHED
        return nodeId;
    }

    private void calibrateNodeStructure()
    {
        nodesInLine = MAX_NODES_IN_LINE;
        nodeSpacing = (MAP_WIDTH-4)/(nodesInLine-1);
        while (nodeSpacing < MIN_NODE_SPACING) {
            nodesInLine-=2;
            nodeSpacing = (MAP_WIDTH-4)/(nodesInLine-1);
        }
        nodeCount = nodesInLine * nodesInLine;
        nodePadding = (MAP_WIDTH - nodeSpacing*(nodesInLine-1))/2;
    }

    private void setMacroInfoForNode(int origNodeId, boolean usable)
    {
        macroUsableNode[origNodeId] = usable;

        macroUsableNode[oppositeNodeId(origNodeId)] = usable;
    }

    private void setMacroInfoForNode(int origNodeId, int destNodeId, int nextNodeId, int dist)
    {
        macroNextNode[origNodeId][destNodeId] = nextNodeId;
        macroPathDistance[origNodeId][destNodeId] = dist;

        macroNextNode[oppositeNodeId(origNodeId)][oppositeNodeId(destNodeId)] = oppositeNodeId(nextNodeId);
        macroPathDistance[oppositeNodeId(origNodeId)][oppositeNodeId(destNodeId)] = dist;

    }

    private void setMacroInfoForNode(int origNodeId, int destNodeId, int nextNodeId, boolean usable, int dist)
    {
        macroUsableNode[origNodeId] = usable;
        macroNextNode[origNodeId][destNodeId] = nextNodeId;
        macroPathDistance[origNodeId][destNodeId] = dist;

        macroUsableNode[oppositeNodeId(origNodeId)] = usable;
        macroNextNode[oppositeNodeId(origNodeId)][oppositeNodeId(destNodeId)] = oppositeNodeId(nextNodeId);
        macroPathDistance[oppositeNodeId(origNodeId)][oppositeNodeId(destNodeId)] = dist;

    }

    //================================================================================
    // Macro Path Assessments Methods
    //================================================================================

    private void initMacroArrays() throws GameActionException
    {
        if (macroPathingProgress < nodeCount) {
            for (int origNodeId = 0; origNodeId<nodeCount/2; origNodeId++) {
                MapLocation originNodeLocation = locationForNode(origNodeId);
                if (initOriginOnlyMacroArraysAndCheckIfUsable(origNodeId, originNodeLocation)) {
                    for (int destNodeId = 0; destNodeId<nodeCount; destNodeId++) {
                        initOriginAndDestinationMacroArrays(origNodeId, destNodeId);
                    }
                    initNeighborEdgesForOrigin(origNodeId, originNodeLocation);
                    if (macroUsableNode[origNodeId])
                        usableNodeCount++;
                }
            }
            for (int origNodeId = 0; origNodeId<nodeCount/2; origNodeId++) {
                updatePathsToIncludeNodesReachableByAccessibleNeighbors(origNodeId);
            }
        }
    }

    /*
    Returns false if the node is a void node and therefore unusable
     */
    private boolean initOriginOnlyMacroArraysAndCheckIfUsable(int origNodeId, MapLocation originNodeLocation)
    {
        setMacroInfoForNode(origNodeId, origNodeId, origNodeId, false, 0);

        if (roadMap[originNodeLocation.x][originNodeLocation.y] != TILE_VOID)
            return true;
        return false;
    }

    private void initOriginAndDestinationMacroArrays(int origNodeId, int destNodeId)
    {
        // If the arrays have already been initialized or a distance calculated by something else don't overwrite
        if (origNodeId != destNodeId) {
            if (macroPathDistance[origNodeId][destNodeId] == 0)
                setMacroInfoForNode(origNodeId, destNodeId, NO_PATH_EXISTS, TILE_VOID);
            if (macroPathDistance[destNodeId][origNodeId] == 0)
                setMacroInfoForNode(destNodeId, origNodeId, NO_PATH_EXISTS, TILE_VOID);
        }

    }

    private void initNeighborEdgesForOrigin(int origNodeId, MapLocation originNodeLocation) throws GameActionException
    {
        int[] neighborIdOffeset = neighborOffsetsForNode(origNodeId);

        // Update Distance to neighbors this is typically as close as we're going to get
        for (int nOffset:neighborIdOffeset) {
            int neighborNodeId = origNodeId+nOffset;

            if (neighborNodeId >= 0 && neighborNodeId < nodeCount) {
                MapLocation neighborNodeLocation = locationForNode(neighborNodeId);

                if (roadMap[neighborNodeLocation.x][neighborNodeLocation.y] != TILE_VOID) {
                    MapLocation[] path = Path.simplePath(rc, this, originNodeLocation, neighborNodeLocation);
                    if (path != null && path.length > 0) {
                        // System.out.print(".");
                        setMacroInfoForNode(origNodeId, true);
                        setMacroInfoForNode(neighborNodeId, true);
                        setMacroInfoForNode(origNodeId, neighborNodeId, neighborNodeId, path.length);

                        // This section is only for testing and must be removed so as not to effect tower placement
                        for (MapLocation step:path) {
                            cowGrowthMap[step.x][step.y] += 1;
                            cowGrowthMap[MAP_WIDTH - step.x - 1][MAP_HEIGHT - step.y - 1] += 1;
                        }
                    }
                }
            }
        }
    }

    private void updatePathsToIncludeNodesReachableByAccessibleNeighbors(int origNodeId) throws GameActionException
    {
        ArrayList<Integer> nodeIdToCheck = new ArrayList<Integer>();
        int[] neighborIdOffeset = neighborOffsetsForNode(origNodeId);
        for (int nOffset:neighborIdOffeset) {
            int neighborNodeId = origNodeId+nOffset;
            if (neighborNodeId >= 0 && neighborNodeId < nodeCount && macroUsableNode[neighborNodeId] && macroNextNode[origNodeId][neighborNodeId] != NO_PATH_EXISTS)
                nodeIdToCheck.add(neighborNodeId);
        }

        while (!nodeIdToCheck.isEmpty()) {
            int queueNodeId = nodeIdToCheck.get(0);
            int[] queueNodeNeighborIdOffeset = neighborOffsetsForNode(queueNodeId);
            for (int qnOffset:queueNodeNeighborIdOffeset) {
                int queueNodeNeighborId = queueNodeId+qnOffset;
                if (queueNodeNeighborId >= 0 && queueNodeNeighborId < nodeCount && macroUsableNode[queueNodeNeighborId]) {
                    if (macroNextNode[origNodeId][queueNodeNeighborId] != NO_PATH_EXISTS) {
                        int distanceFromOriginToQueueNodeToQueueNodeNeighbor = macroPathDistance[origNodeId][queueNodeId] + macroPathDistance[queueNodeId][queueNodeNeighborId];
                        int distanceFromOriginToQueueNodeNeighbor = macroPathDistance[origNodeId][queueNodeNeighborId];
                        if (distanceFromOriginToQueueNodeToQueueNodeNeighbor < distanceFromOriginToQueueNodeNeighbor) {
                            setMacroInfoForNode(origNodeId, queueNodeNeighborId, macroNextNode[origNodeId][queueNodeId], distanceFromOriginToQueueNodeToQueueNodeNeighbor);
                            nodeIdToCheck.add(queueNodeNeighborId);
                        }
                    } else {
                        int distanceFromOriginToQueueNodeToQueueNodeNeighbor = macroPathDistance[origNodeId][queueNodeId] + macroPathDistance[queueNodeId][queueNodeNeighborId];
                        setMacroInfoForNode(origNodeId, queueNodeNeighborId, macroNextNode[origNodeId][queueNodeId], distanceFromOriginToQueueNodeToQueueNodeNeighbor);
                        nodeIdToCheck.add(queueNodeNeighborId);
                    }
                }
            }
            nodeIdToCheck.remove(0);
        }
    }

    private int[] neighborOffsetsForNode(int nodeId)
    {
        if (nodeId%nodesInLine == 0) // First Rows need not check spaces to the left
            return new int[]{-nodesInLine, (-nodesInLine)+1, +1, nodesInLine, nodesInLine+1};
        else if ((nodeId+1)%nodesInLine == 0) // Last Rows need not check to the right
            return new int[]{(-nodesInLine)-1, -nodesInLine, -1, nodesInLine-1, nodesInLine};
        return new int[]{(-nodesInLine)-1, -nodesInLine, (-nodesInLine)+1, -1, +1, nodesInLine-1, nodesInLine, nodesInLine+1};
    }

    private void assessMacroPathing() throws GameActionException
    {

        if (macroPathingProgress == 0) {
            System.out.println("Map Size: " + MAP_WIDTH + ", " + MAP_HEIGHT);
            System.out.println("Macro Matrix Even Split Size: " + nodesInLine + "x" + nodesInLine + " (" + nodeCount + " nodes spaced by " + nodeSpacing + ")");
            System.out.println("START MACRO ASSESSMENT");
            rc.setIndicatorString(1, "Working On Macro");
        }

        initMacroArrays();

        for (int origNodeId = 0; origNodeId<nodeCount; origNodeId++) {
            MapLocation nodeLoc = locationForNode(origNodeId);
            cowGrowthMap[nodeLoc.x][nodeLoc.y] = 9;

//            if (macroUsableNode[origNodeId]) {
//                for (int destNodeId = 0; destNodeId<nodeCount; destNodeId++) {
//                    if (macroUsableNode[destNodeId]) {
//                        System.out.println("Node[" + origNodeId + "][" + destNodeId + "] -> " + macroNextNode[origNodeId][destNodeId] + " (" + macroPathDistance[origNodeId][destNodeId] + ")");
//                    }
//                }
//            }
//            System.out.println("");
        }

        System.out.println("FINISH MACRO ASSESSMENT");
        rc.setIndicatorString(1, "Done with Macro");

        macroPathingUploaded = true;
        BroadcastMacro();

        printMap();
    }

    public void readBroadcastMacro() throws GameActionException
    {
        macroPathingUploaded = rc.readBroadcast(Utilities.macroUploadedChannel) == 1;

        if (macroPathingUploaded) {
            pathingStrat = PathingStrategy.SmartBug;

//            System.out.println("Soldier Pulling Macro Pathing");
            rc.setIndicatorString(1, "Pulling Macro Pathing");

            // Do it

            rc.setIndicatorString(1, "Finished Pulling Macro Pathing");
//            printMap();
        }
    }

    public void BroadcastMacro() throws GameActionException
    {
        rc.setIndicatorString(1, "Broadcasting Macro");

        // Do it

        rc.setIndicatorString(1, "Finished Broadcasting Macro");
        broadcastMapUploadedFlag();
    }

    public void broadcastMacroUploadedFlag() throws GameActionException
    {
        rc.broadcast(Utilities.macroUploadedChannel, VectorFunctions.locToInt(new MapLocation(macroPathingUploaded ? 1 : 0, expectMacroPathing ? 1 : 0)));
    }




    //================================================================================
    // Map Updates, Checks, and Assessments Methods
    //================================================================================

    private void assessMap() throws GameActionException
    {
        System.out.println("START MAP ASSESSMENT");
        rc.setIndicatorString(1, "Working On Map");
        cgMap = rc.senseCowGrowth();

        // Map Details are read in from the game board
        for (int x=0; x<=MAP_WIDTH/2;x++) {
            for (int y=0; y<MAP_HEIGHT;y++) {
                int ordValue = rc.senseTerrainTile(new MapLocation(x,y)).ordinal();//0 NORMAL, 1 ROAD, 2 VOID, 3 OFF_MAP
                roadMap[x][y] = ordValue;
                cowGrowthMap[x][y] = (int)cgMap[x][y];

                roadMap[MAP_WIDTH-x-1][MAP_HEIGHT-y-1] = ordValue;
                cowGrowthMap[MAP_WIDTH-x-1][MAP_HEIGHT-y-1] = (int)cgMap[MAP_WIDTH-x-1][MAP_HEIGHT-y-1];

                if (ordValue > 1) {
                    voidDensity++;
                    roadMap[x][y] = TILE_VOID;
                    cowGrowthMap[x][y] = 0;

                    roadMap[MAP_WIDTH-x-1][MAP_HEIGHT-y-1] = TILE_VOID;
                    cowGrowthMap[MAP_WIDTH-x-1][MAP_HEIGHT-y-1] = 0;
                }
            }

            // Our seconds unit can typically spawn at around round 20 so we start trying... Hacky right.
            if (Clock.getRoundNum() > 20 && rc.senseRobotCount() < 3)
                Headquarter.tryToSpawn();
        }

//        printMap();

        voidDensity/=(MAP_HEIGHT*MAP_WIDTH);
        System.out.println("Void Density: " + voidDensity);

        System.out.println("FINISH MAP ASSESSMENT");
        rc.setIndicatorString(1, "Done with Map");

        mapUploaded = true;
        broadcastMap();
        return;
    }

    public void readBroadcastMap() throws GameActionException
    {
        mapUploaded = rc.readBroadcast(Utilities.mapUploadedChannel) == 1;

        if (mapUploaded) {
            pathingStrat = PathingStrategy.SmartBug;

//            System.out.println("Soldier Pulling Map");
            rc.setIndicatorString(1, "Pulling Map");

            roadMap = new int[MAP_WIDTH][MAP_HEIGHT];
            cowGrowthMap = new int[MAP_WIDTH][MAP_HEIGHT];
            for (int x=0; x<MAP_WIDTH;x++) {
                for (int y=0; y<MAP_HEIGHT;y++) {
                    MapLocation mapDetails = VectorFunctions.intToLoc(rc.readBroadcast(Utilities.startMapChannels + y*MAP_HEIGHT+x));
                    roadMap[x][y] = mapDetails.x;
                    cowGrowthMap[x][y] = mapDetails.y;
                }
            }

            rc.setIndicatorString(1, "Finished Pulling Map");
//            printMap();
        }
    }

    public void broadcastMap() throws GameActionException
    {
        rc.setIndicatorString(1, "Broadcasting Map");
        for (int y=0; y<MAP_HEIGHT;y++) {
            for (int x=0; x<MAP_WIDTH;x++) {
                rc.broadcast(y*MAP_HEIGHT+x, VectorFunctions.locToInt(new MapLocation(roadMap[y][x], cowGrowthMap[y][x])));
            }
        }
        rc.setIndicatorString(1, "Finished Broadcasting Map");
        broadcastMapUploadedFlag();
    }

    public void broadcastMapUploadedFlag() throws GameActionException
    {
        rc.broadcast(Utilities.mapUploadedChannel, mapUploaded ? 1 : 0);
    }




    //================================================================================
    // Print Methods
    //================================================================================

    public char asciiValue(int fVal)
    {
        switch (fVal) {
            case 0:
                return ' ';
            case 1:
                return '.';
            case 2:
                return ':';
            case 3:
                return ';';
            case 4:
                return '+';
            case 5:
                return '=';
            case 6:
                return 'x';
            case 7:
                return 'X';
            case 8:
                return '$';
            default:
                return '&';
        }
    }

    public void printMap()
    {
        for (int y=0; y<MAP_WIDTH;y++) {
            for (int x=0; x<MAP_HEIGHT;x++) {
                MapLocation tileValue = VectorFunctions.intToLoc(roadMap[x][y]);
                if (roadMap[x][y] != TILE_VOID)
                    System.out.print(asciiValue(cowGrowthMap[x][y]) + "" + asciiValue(cowGrowthMap[x][y])); // System.out.print(asciiValue(roadMap[x][y]+cowGrowthMap[x][y]) + "" + asciiValue(roadMap[x][y]+cowGrowthMap[x][y]));
                else
                    System.out.print("[]");
            }
            System.out.println("");
        }
        System.out.println("");
    }
}
