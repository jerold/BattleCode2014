package DeepBlue;

import battlecode.common.*;
import java.util.ArrayList;

/**
 * Created by Jerold Albertson on 1/12/14.
 *
 */
public class RoadMap {

    RobotController rc;
    UnitCache cache;

    public int MAP_WIDTH;
    public int MAP_HEIGHT;
    int[][] roadMap;
    int[][] lowResCowMap;
    double[][] cowGrowthMap;
    int[][] pathingTestMap;

    static final double MACRO_PATH_MAX_VOID_DENSITY = .45;
    public static final int TILE_VOID = 999;
    public static final int NO_PATH_EXISTS = -1;
    static final int MIN_NODE_SPACING = 6;
    static final int MAX_NODES_IN_LINE = 8;
    static final int MAP_PADDING = 1;
    static final int COW_GROWTH_MAP_RESOLUTION = 4;

    int nodeCount;
    int usableNodeCount;
    int nodesInLine;
    int nodeSpacing;
    int nodePadding;
    int[][] macroNextNode;
    boolean[] macroUsableNode;
    int[][] macroPathDistance;

    public static enum PathingStrategy {
        DefaultBug,
        SmartBug,
        MacroPath
    }

    public static enum SymmetryType {
        Horizontal,
        Vertical,
        Diagonal
    }

    SymmetryType symmetry;
    PathingStrategy pathingStrat;
    Navigator observingNavigator;
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
        observingNavigator = null;

        MAP_HEIGHT = rc.getMapHeight();
        MAP_WIDTH = rc.getMapWidth();
        roadMap = new int[MAP_WIDTH][MAP_HEIGHT];
        lowResCowMap = new int[COW_GROWTH_MAP_RESOLUTION][COW_GROWTH_MAP_RESOLUTION];
        cowGrowthMap = new double[MAP_WIDTH][MAP_HEIGHT];
        pathingTestMap = new int[MAP_WIDTH][MAP_HEIGHT];
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
        if (rc.getType() == RobotType.HQ) {
            discoverSymmetry();
            broadcastFlags();
        }
    }




    //================================================================================
    // Facilitator Methods
    //================================================================================

    public void discoverSymmetry() throws GameActionException
    {
        if (cache.MY_HQ.x == MAP_WIDTH-cache.ENEMY_HQ.x-1 && cache.MY_HQ.y == MAP_HEIGHT-cache.ENEMY_HQ.y-1) symmetry = SymmetryType.Diagonal;
        if (cache.MY_HQ.x == cache.ENEMY_HQ.x) {
            if (MAP_WIDTH%2==0) symmetry = SymmetryType.Horizontal;
        }
        if (cache.MY_HQ.y == cache.ENEMY_HQ.y) {
            if (MAP_HEIGHT%2==0) symmetry = SymmetryType.Vertical;
        }
        System.out.println("Symmetry: "+symmetry);
    }

    public void checkForUpdates() throws GameActionException
    {
        if (cache.rcType == RobotType.HQ) {
            if (!mapUploaded)
                assessMap();
            else if (expectMacroPathing && !macroPathingUploaded)
                assessMacroPathing();
        } else {
            if (!mapUploaded)
                readBroadcastMap();
            else if (expectMacroPathing && !macroPathingUploaded)
                readBroadcastMacro();
        }
    }

    public void broadcastFlags() throws GameActionException
    {
        broadcastMapFlag();
        broadcastMacroFlag();
    }

    public int valueForLocation(MapLocation loc) throws GameActionException
    {
        if (loc.y < MAP_HEIGHT && loc.y > 0 && loc.x < MAP_WIDTH && loc.x > 0) {
            if (mapUploaded) return roadMap[loc.x][loc.y];
            else return rc.readBroadcast(Utilities.startMapChannels+loc.x*MAP_WIDTH+loc.y) > 1 ? TILE_VOID : 0;
        } else return TILE_VOID;
    }

    public Direction directionTo(MapLocation origin, MapLocation destination) throws GameActionException
    {
        Direction dir = origin.directionTo(destination);
        if (valueForLocation(origin.add(dir)) != TILE_VOID) return dir;

        // Rotate 1 Left and Right, 3 Checked
        Direction dirLeft = dir.rotateLeft();
        Direction dirRight = dir.rotateRight();
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

        return dir.opposite();
    }

    public MapLocation locationForNode(int nodeId) {
//        if ((nodeId%nodesInLine) >= nodesInLine/2) {
//            MapLocation mirrorLocation = locationForNode(oppositeNodeId(nodeId));
//            return new MapLocation(MAP_WIDTH-mirrorLocation.x-1, MAP_HEIGHT-mirrorLocation.y-1);
//        }
        return new MapLocation((nodeId/nodesInLine) * nodeSpacing + nodePadding, (nodeId%nodesInLine) * nodeSpacing + nodePadding);
    }

//    public int oppositeNodeId(int nodeId) {
//        //System.out.println(nodeId + "< - >" + (nodeCount - nodeId - 1));
//        return nodeId == NO_PATH_EXISTS ? NO_PATH_EXISTS : nodeCount - nodeId - 1;
//    }

    public int idForNearestNode(MapLocation loc)
    {
//        int nodeX = (loc.x-nodePadding)/(MAP_WIDTH/nodesInLine);
//        int nodeY = (loc.y-nodePadding)/(MAP_HEIGHT/nodesInLine);
        return ((loc.x-nodePadding)/(MAP_WIDTH/nodesInLine))*nodesInLine+((loc.y-nodePadding)/(MAP_HEIGHT/nodesInLine));

//        if (Path.canSimplyPath(this, loc, locationForNode(nearestNodeId)))
//            return nearestNodeId;
//
//        // Nearest Node could not be easily pathed to.  Grab best pathable neighbor
//        int nearestNeighborNodeId = NO_PATH_EXISTS;
//        int nearestNeighborDistance = TILE_VOID;
//        int[] neighborIdOffeset = neighborOffsetsForNode(nearestNodeId);
//        for (int nOffset:neighborIdOffeset) {
//            int neighborNodeId = nearestNodeId+nOffset;
//            if (neighborNodeId >= 0 && neighborNodeId < nodeCount && Path.canSimplyPath(this, loc, locationForNode(neighborNodeId))) {
//                return neighborNodeId;
//            }
//        }
//        return nearestNeighborNodeId;

//        int nodeId = NO_PATH_EXISTS;
//        int nodeDist = TILE_VOID;
//        for (int i=0;i<nodeCount;i++) {
//            MapLocation nodeLoc = locationForNode(i);
//            if (!locationIsVoid(nodeLoc)) {
//                int dist = (int)Utilities.distanceBetweenTwoPoints(loc, nodeLoc);
//                if (dist < nodeDist) {
//                    MapLocation[] path = Path.getSimplePath(this, loc, nodeLoc);
//                    if (path != null && path.length < nodeDist) {
//                        nodeDist = path.length;
//                        nodeId = i;
//                    }
//                }
//            }
//        }
//        System.out.println(loc.x+","+loc.y+" near node["+nodeId+"] ("+nodeDist+") "+locationForNode(nodeId).x+", "+locationForNode(nodeId).y+", guess="+(nodeX*nodesInLine+nodeY));
//        return nodeId;
    }

    public int idForNodeNearOldRallyPointInDirectionOfNewRallyPoint(MapLocation oldloc, MapLocation newLoc) throws GameActionException
    {
        int nearestNodeIdForOldLoc = idForNearestNode(oldloc);
        int nearestNodeIdForNewLoc = idForNearestNode(newLoc);
        if (Path.canSimplyPath(this, oldloc, locationForNode(nearestNodeIdForOldLoc)))
            return nearestNodeIdForOldLoc;

        // Nearest Node could not be easily pathed to.  Grab best pathable neighbor
        int nearestNeighborNodeId = NO_PATH_EXISTS;
        int nearestNeighborDistance = TILE_VOID;
        int[] neighborIdOffeset = neighborOffsetsForNode(nearestNodeIdForOldLoc);
        for (int nOffset:neighborIdOffeset) {
            int neighborNodeId = nearestNodeIdForOldLoc+nOffset;
            int dist = macroPathDistance[neighborNodeId][nearestNodeIdForNewLoc];
            if (neighborNodeId >= 0 && neighborNodeId < nodeCount && dist < nearestNeighborDistance && Path.canSimplyPath(this, oldloc, locationForNode(nearestNeighborNodeId))) {
                nearestNeighborDistance = dist;
                nearestNeighborNodeId = neighborNodeId;
            }
        }
        return nearestNeighborNodeId;
    }

    private void calibrateNodeStructure()
    {
        nodesInLine = MAX_NODES_IN_LINE;
        nodeSpacing = (MAP_WIDTH-MAP_PADDING)/(nodesInLine-1);
        while (nodeSpacing < MIN_NODE_SPACING) {
            nodesInLine-=2;
            nodeSpacing = (MAP_WIDTH-MAP_PADDING)/(nodesInLine-1);
        }
        nodeCount = nodesInLine * nodesInLine;
        nodePadding = (MAP_WIDTH - nodeSpacing*(nodesInLine-1))/2;
    }

    public boolean locationIsVoid(MapLocation loc)
    {
        return roadMap[loc.x][loc.y] == TILE_VOID;
    }

    public int nextStep(int origNodeId, int destNodeId)
    {
        return macroNextNode[origNodeId][destNodeId];
    }

    public int distanceBetweenNodes(int origNodeId, int destNodeId)
    {
        return macroPathDistance[origNodeId][destNodeId];
    }

    private void setMacroInfoForNode(int origNodeId, boolean usable)
    {
        if (usable && !macroUsableNode[origNodeId])
            usableNodeCount++; // To count both the node and it's opposite

        macroUsableNode[origNodeId] = usable;

//        macroUsableNode[oppositeNodeId(origNodeId)] = usable;
    }

    private void setMacroInfoForNode(int origNodeId, int destNodeId, int nextNodeId, int dist)
    {
        macroNextNode[origNodeId][destNodeId] = nextNodeId;
        macroPathDistance[origNodeId][destNodeId] = dist;

//        macroNextNode[oppositeNodeId(origNodeId)][oppositeNodeId(destNodeId)] = oppositeNodeId(nextNodeId);
//        macroPathDistance[oppositeNodeId(origNodeId)][oppositeNodeId(destNodeId)] = dist;

    }

    private void setMacroInfoForNode(int origNodeId, int destNodeId, int nextNodeId, boolean usable, int dist)
    {
        macroUsableNode[origNodeId] = usable;
        macroNextNode[origNodeId][destNodeId] = nextNodeId;
        macroPathDistance[origNodeId][destNodeId] = dist;

//        macroUsableNode[oppositeNodeId(origNodeId)] = usable;
//        macroNextNode[oppositeNodeId(origNodeId)][oppositeNodeId(destNodeId)] = oppositeNodeId(nextNodeId);
//        macroPathDistance[oppositeNodeId(origNodeId)][oppositeNodeId(destNodeId)] = dist;
    }




    //================================================================================
    // Macro Path Assessments Methods
    //================================================================================

    private void initMacroArrays() throws GameActionException
    {
        for (int origNodeId = 0; origNodeId<nodeCount; origNodeId++) {
            MapLocation originNodeLocation = locationForNode(origNodeId);
            initOriginOnlyMacroArraysAndCheckIfUsable(origNodeId);
            for (int destNodeId = 0; destNodeId<nodeCount; destNodeId++) {
                initOriginAndDestinationMacroArrays(origNodeId, destNodeId);
            }
            if (!locationIsVoid(originNodeLocation))
                initNeighborEdgesForOrigin(origNodeId, originNodeLocation);
            Headquarter.tryToSpawn();
        }
        for (int origNodeId = 0; origNodeId<nodeCount; origNodeId++) {
            MapLocation originNodeLocation = locationForNode(origNodeId);
            if (!locationIsVoid(originNodeLocation))
                updatePathsToIncludeNodesReachableByAccessibleNeighbors(origNodeId);
            Headquarter.tryToSpawn();
        }
        System.out.println("Usable Nodes: " + usableNodeCount + "/" + nodeCount);
    }

    /*
    Returns false if the node is a void node and therefore unusable
     */
    private void initOriginOnlyMacroArraysAndCheckIfUsable(int origNodeId)
    {
        setMacroInfoForNode(origNodeId, origNodeId, origNodeId, false, TILE_VOID);
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

                if (!locationIsVoid(neighborNodeLocation)) {
                    MapLocation[] path = Path.getSimplePath(this, originNodeLocation, neighborNodeLocation);
                    if (path != null && path.length > 0) {
                        setMacroInfoForNode(origNodeId, true);
                        setMacroInfoForNode(origNodeId, neighborNodeId, neighborNodeId, true, path.length);
                        addPathToMap(path);
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
        System.out.println("Map Size: " + MAP_WIDTH + ", " + MAP_HEIGHT);
        System.out.println("Macro Matrix Even Split Size: " + nodesInLine + "x" + nodesInLine + " (" + nodeCount + " nodes spaced by " + nodeSpacing + ")");
        System.out.println("START MACRO ASSESSMENT");
        rc.setIndicatorString(1, "Working On Macro");

        initMacroArrays();

        System.out.println("FINISH MACRO ASSESSMENT");
        rc.setIndicatorString(1, "Done with Macro");

//        printMacro();
//        printMap();

        macroPathingUploaded = true;
        BroadcastMacro();
    }

    public void readBroadcastMacro() throws GameActionException
    {
        macroPathingUploaded = rc.readBroadcast(Utilities.macroUploadedChannel) == 1;
        expectMacroPathing = rc.readBroadcast(Utilities.macroExpectChannel) == 1;

        if (macroPathingUploaded) {
            pathingStrat = PathingStrategy.MacroPath;

            if (observingNavigator != null)
                observingNavigator.pathingStrategyChanged();
        }
    }

    public void BroadcastMacro() throws GameActionException
    {
        rc.setIndicatorString(1, "Broadcasting Macro");
        for (int originId=0; originId<nodeCount;originId++) {
            for (int destinationId=0; destinationId<nodeCount;destinationId++) {
                int channel = Utilities.startMacroChannels + originId*nodeCount+destinationId;
                int signal = VectorFunctions.locToInt(new MapLocation(macroNextNode[originId][destinationId] == NO_PATH_EXISTS ? TILE_VOID : macroNextNode[originId][destinationId], macroPathDistance[originId][destinationId]));
                rc.broadcast(channel, signal);
                Headquarter.tryToSpawn();
            }
        }

        // TEST RALLY POINT
        Headquarter.setRallyPoint(cache.ENEMY_HQ, 0);

        rc.setIndicatorString(1, "Finished Broadcasting Macro");
        broadcastFlags();
    }

    public void broadcastMacroFlag() throws GameActionException
    {
        rc.broadcast(Utilities.macroUploadedChannel, macroPathingUploaded ? 1 : 0);
        rc.broadcast(Utilities.macroExpectChannel, expectMacroPathing ? 1 : 0);
    }




    //================================================================================
    // Map Updates, Checks, and Assessments Methods
    //================================================================================

    private void assessMap() throws GameActionException
    {
        System.out.println("START MAP ASSESSMENT");
        rc.setIndicatorString(1, "Working On Map");
        cowGrowthMap = rc.senseCowGrowth();

        // Map Details are read in from the game board
        for (int x=0; x<MAP_WIDTH;x++) {
            int lowResX = (int)(x/((float)MAP_WIDTH/COW_GROWTH_MAP_RESOLUTION));

            for (int y=0; y<MAP_HEIGHT;y++) {
                int lowResY = (int)(y/((float)MAP_HEIGHT/COW_GROWTH_MAP_RESOLUTION));

                int ordValue = rc.senseTerrainTile(new MapLocation(x,y)).ordinal();//0 NORMAL, 1 ROAD, 2 VOID, 3 OFF_MAP
                roadMap[x][y] = 1-ordValue; // Roads become 0, normals become 1 (higher gets TILE_VOID below
                lowResCowMap[lowResX][lowResY] += (int)cowGrowthMap[x][y];

//                roadMap[MAP_WIDTH-x-1][MAP_HEIGHT-y-1] = 1-ordValue;
//                cowGrowthMap[MAP_WIDTH-x-1][MAP_HEIGHT-y-1] = (int)cgMap[MAP_WIDTH-x-1][MAP_HEIGHT-y-1];

                if (ordValue > 1) {
                    voidDensity++;
                    roadMap[x][y] = TILE_VOID;
                    lowResCowMap[lowResX][lowResY]--;

//                    roadMap[MAP_WIDTH-x-1][MAP_HEIGHT-y-1] = TILE_VOID;
//                    cowGrowthMap[MAP_WIDTH-x-1][MAP_HEIGHT-y-1] = 0;
                }
            }

            Headquarter.tryToSpawn();
        }

        printCows();

        voidDensity=(voidDensity*2)/(MAP_HEIGHT*MAP_WIDTH);
        System.out.println("Void Density: " + voidDensity);

        if (voidDensity > MACRO_PATH_MAX_VOID_DENSITY)
            expectMacroPathing = false;

        System.out.println("FINISH MAP ASSESSMENT");
        rc.setIndicatorString(1, "Done with Map");

        mapUploaded = true;
        broadcastMap();
    }

    public void readBroadcastMap() throws GameActionException
    {
        mapUploaded = rc.readBroadcast(Utilities.mapUploadedChannel) == 1;

        if (mapUploaded) {
            pathingStrat = PathingStrategy.SmartBug;
            rc.setIndicatorString(1, "Pulling Map");

            for (int x=0; x<MAP_WIDTH;x++) {
                for (int y=0; y<MAP_HEIGHT;y++) {
                    int channel = Utilities.startMapChannels + y*MAP_HEIGHT+x;
                    MapLocation signal = VectorFunctions.intToLoc(rc.readBroadcast(channel));
                    roadMap[x][y] = signal.x;
                    cowGrowthMap[x][y] = signal.y;
                }
            }

            rc.setIndicatorString(1, "Finished Pulling Map");
            if (observingNavigator != null)
                observingNavigator.pathingStrategyChanged();
        }
    }

    public void broadcastMap() throws GameActionException
    {
        rc.setIndicatorString(1, "Broadcasting Map");
        for (int y=0; y<MAP_HEIGHT;y++) {
            for (int x=0; x<MAP_WIDTH;x++) {
                int channel = Utilities.startMapChannels + y*MAP_HEIGHT+x;
                int signal = VectorFunctions.locToInt(new MapLocation(roadMap[x][y], (int)cowGrowthMap[x][y]));
                rc.broadcast(channel, signal);
                Headquarter.tryToSpawn();
            }
        }
        rc.setIndicatorString(1, "Finished Broadcasting Map");
        broadcastFlags();
    }

    public void broadcastMapFlag() throws GameActionException
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

    public void addPathToMap(MapLocation[] path)
    {
        for (MapLocation step:path) {
            cowGrowthMap[step.x][step.y]++;
            cowGrowthMap[MAP_WIDTH-step.x-1][MAP_HEIGHT-step.y-1]++;
        }
    }

    public void printMacro()
    {
        for (int originId=0; originId<nodeCount;originId++) {
            for (int destinationId=0; destinationId<nodeCount;destinationId++) {
                System.out.println("Node["+originId+"] -> ["+macroNextNode[originId][destinationId]+"] -> ["+destinationId+"]  ~"+(Utilities.startMacroChannels+originId*nodeCount+destinationId)+"~");
            }
        }
    }

    public void printCows()
    {
        System.out.println("Cow Map:");
        for (int y=0; y<COW_GROWTH_MAP_RESOLUTION;y++) {
            for (int x=0; x<COW_GROWTH_MAP_RESOLUTION;x++) {
                System.out.print("["+lowResCowMap[x][y]+"]");
            }
            System.out.println("");
        }
        System.out.println("");
    }

    public void printMap()
    {
        for (int y=0; y<MAP_HEIGHT;y++) {
            for (int x=0; x<MAP_WIDTH;x++) {
                MapLocation tileValue = VectorFunctions.intToLoc(roadMap[x][y]);
                if (roadMap[x][y] != TILE_VOID)
                    System.out.print(asciiValue((int)cowGrowthMap[x][y]) + "" + asciiValue((int)cowGrowthMap[x][y])); // System.out.print(asciiValue(roadMap[x][y]+cowGrowthMap[x][y]) + "" + asciiValue(roadMap[x][y]+cowGrowthMap[x][y]));
                else
                    System.out.print("[]");
            }
            System.out.println("");
        }
        System.out.println("");
    }
}
