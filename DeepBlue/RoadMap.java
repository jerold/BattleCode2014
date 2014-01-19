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

    static int MAX_WIDTH = 100; // Prevent heavy processing on larger maps
    static final double MAX_VOID_DENSITY = .6;

    static final int TILE_VOID = 999;

    int MAP_WIDTH;
    int MAP_HEIGHT;
    int[][] roadMap;
    int[][] cowGrowthMap;

    static final int NO_PATH_EXISTS = -1;
    static final int MAX_NODE_SPACING = 10;
    int nodesInLine;
    int nodeSpacing;
    int[][] macroNextNode;
    boolean[][] macroPathChecked;
    int[][] macroPathDistance;
    int[] neighborIdOffeset;


    public enum PathingStrategy {
        DefaultBug,
        SmartBug
    }

    PathingStrategy pathingStrat;
    double voidDensity;
    boolean mapUploaded;

    RoadMap(RobotController inRc, UnitCache inCache) throws GameActionException
    {
        rc = inRc;
        cache = inCache;

        MAP_HEIGHT = rc.getMapHeight();
        MAP_WIDTH = rc.getMapWidth();
        roadMap = new int[MAP_WIDTH][MAP_HEIGHT];
        voidDensity = 0.0;
        mapUploaded = false;
        pathingStrat = PathingStrategy.DefaultBug;

        if (rc.getType() == RobotType.HQ)
            resetMapUploadedFlag();
    }

    //================================================================================
    // Facilitator Methods
    //================================================================================

    public int valueForLocation(MapLocation loc) throws GameActionException
    {
        if (loc.y < MAP_HEIGHT && loc.y > 0 && loc.x < MAP_WIDTH && loc.x > 0)
            return roadMap[loc.x][loc.y];
        return TILE_VOID;
    }

    public Direction directionTo(MapLocation origin, MapLocation destination) throws GameActionException
    {
//        System.out.print("\nDirection To: ");
        int ordinalDir = origin.directionTo(destination).ordinal();
//        System.out.println(". ");
        if (valueForLocation(origin.add(Utilities.directionByOrdinal[ordinalDir])) != TILE_VOID) return Utilities.directionByOrdinal[ordinalDir];

        Direction[] dirs = Utilities.directionByOrdinal;

        // Rotate 1 Left and Right, 3 Checked
//        System.out.println("... ");
        Direction dirLeft = dirs[ordinalDir].rotateLeft();
        Direction dirRight = dirs[ordinalDir].rotateRight();
        if (valueForLocation(origin.add(dirLeft)) != TILE_VOID) {
            if (valueForLocation(origin.add(dirRight)) != TILE_VOID && valueForLocation(origin.add(dirRight)) < valueForLocation(origin.add(dirLeft))) return dirRight;
            else return dirLeft;
        } else if (valueForLocation(origin.add(dirRight)) != TILE_VOID) return dirRight;

        // Rotate 1 Left and Right, 5 Checked
//        System.out.println("..... ");
        dirLeft = dirLeft.rotateLeft();
        dirRight = dirRight.rotateRight();
        if (valueForLocation(origin.add(dirLeft)) != TILE_VOID) {
            if (valueForLocation(origin.add(dirRight)) != TILE_VOID && valueForLocation(origin.add(dirRight)) < valueForLocation(origin.add(dirLeft))) return dirRight;
            else return dirLeft;
        } else if (valueForLocation(origin.add(dirRight)) != TILE_VOID) return dirRight;

        // Rotate 1 Left and Right, 7 Checked
//        System.out.println("....... ");
        dirLeft = dirLeft.rotateLeft();
        dirRight = dirRight.rotateRight();
        if (valueForLocation(origin.add(dirLeft)) != TILE_VOID) {
            if (valueForLocation(origin.add(dirRight)) < TILE_VOID && valueForLocation(origin.add(dirRight)) < valueForLocation(origin.add(dirLeft))) return dirRight;
            else return dirLeft;
        } else if (valueForLocation(origin.add(dirRight)) != TILE_VOID) return dirRight;

//        System.out.println("........ ");
        return dirs[ordinalDir].opposite();
    }

    //================================================================================
    // Macro Path Assessments Methods
    //================================================================================

    private void assessMacroPathing() throws GameActionException
    {
        nodesInLine = 1;
        int divVal = 2;
        while (MAP_WIDTH/divVal > MAX_NODE_SPACING) {
            nodesInLine = divVal;
            divVal++;
        }
        nodeSpacing = MAP_WIDTH/nodesInLine;
        neighborIdOffeset = new int[]{-nodesInLine-1, -nodesInLine, -nodesInLine+1, -1, +2, nodesInLine-1, nodesInLine, nodesInLine+1};

        int nodeCount = nodesInLine * nodesInLine;
        System.out.println("Map Size: " + MAP_WIDTH + ", " + MAP_HEIGHT);
        System.out.println("Macro Matrix Even Split Size: " + nodesInLine + "x" + nodesInLine + " (" + nodeCount + " nodes spaced by " + nodeSpacing + ")");
        System.out.println("START MACRO ASSESSMENT:  " + Clock.getRoundNum());

        // Initialize Direction Arrays
        macroNextNode = new int[nodeCount][nodeCount];
        macroPathChecked = new boolean[nodeCount][nodeCount];
        macroPathDistance = new int[nodeCount][nodeCount];
        for (int origNodeId = 0; origNodeId<nodeCount; origNodeId++) {
            for (int destNodeId = 0; destNodeId<nodeCount; destNodeId++) {
                if (origNodeId != destNodeId) {
                    int oppOrigNodeId = nodeCount - origNodeId - 1;
                    int oppDestNodeId = nodeCount - destNodeId - 1;

                    macroNextNode[origNodeId][destNodeId] = destNodeId;
                    macroPathChecked[origNodeId][destNodeId] = false;
                    macroPathDistance[origNodeId][destNodeId] = TILE_VOID;

                    macroNextNode[destNodeId][origNodeId] = origNodeId;
                    macroPathChecked[destNodeId][origNodeId] = false;
                    macroPathDistance[destNodeId][origNodeId] = TILE_VOID;
                } else {
                    macroNextNode[origNodeId][destNodeId] = NO_PATH_EXISTS;
                    macroPathDistance[origNodeId][destNodeId] = 0;
                    macroPathChecked[origNodeId][destNodeId] = true;
                }
            }
        }

        // Initialize Neighbor Distances
        for (int origNodeId = 0; origNodeId<nodeCount; origNodeId++) {
            int oppOrigNodeId = nodeCount - origNodeId - 1;
            MapLocation originNodeLocation = locationForNode(origNodeId);

            // Update Distance to neighbors this is typically as close as we're going to get
            System.out.print("Node[" + origNodeId + "] ");
            for (int nOffset:neighborIdOffeset) {
                int neighborNodeId = origNodeId+nOffset;
                int oppNeighborNodeId = nodeCount - neighborNodeId - 1;


                if (neighborNodeId >= 0 && neighborNodeId < nodeCount) {
                    System.out.print(" (" + neighborNodeId + ")");
                    MapLocation neighborNodeLocation = locationForNode(neighborNodeId);

                    MapLocation[] path = Path.simplePath(rc, this, originNodeLocation, neighborNodeLocation);
                    if (path != null && path.length > 0) {
                        macroNextNode[origNodeId][neighborNodeId] = neighborNodeId;
                        macroPathDistance[origNodeId][neighborNodeId] = path.length;
                        macroPathChecked[origNodeId][neighborNodeId] = true;

                        macroNextNode[neighborNodeId][origNodeId] = origNodeId;
                        macroPathDistance[neighborNodeId][origNodeId] = path.length;
                        macroPathChecked[neighborNodeId][origNodeId] = true;

                        macroNextNode[oppOrigNodeId][oppNeighborNodeId] = oppNeighborNodeId;
                        macroPathDistance[oppOrigNodeId][oppNeighborNodeId] = path.length;
                        macroPathChecked[oppOrigNodeId][oppNeighborNodeId] = true;

                        macroNextNode[oppNeighborNodeId][oppOrigNodeId] = oppOrigNodeId;
                        macroPathDistance[oppNeighborNodeId][oppOrigNodeId] = path.length;
                        macroPathChecked[oppNeighborNodeId][oppOrigNodeId] = true;

                        for (MapLocation step:path) {
                            cowGrowthMap[step.x][step.y] = 3;
                            cowGrowthMap[MAP_WIDTH - step.x - 1][MAP_HEIGHT - step.y - 1] = 3;
                        }
                        System.out.print(". ");
                    } else {
                        macroNextNode[origNodeId][neighborNodeId] = NO_PATH_EXISTS;
                        macroPathDistance[origNodeId][neighborNodeId] = TILE_VOID;
                        macroPathChecked[origNodeId][neighborNodeId] = true;

                        macroNextNode[neighborNodeId][origNodeId] = NO_PATH_EXISTS;
                        macroPathDistance[neighborNodeId][origNodeId] = TILE_VOID;
                        macroPathChecked[neighborNodeId][origNodeId] = true;

                        macroNextNode[oppOrigNodeId][oppNeighborNodeId] = NO_PATH_EXISTS;
                        macroPathDistance[oppOrigNodeId][oppNeighborNodeId] = TILE_VOID;
                        macroPathChecked[oppOrigNodeId][oppNeighborNodeId] = true;

                        macroNextNode[oppNeighborNodeId][oppOrigNodeId] = NO_PATH_EXISTS;
                        macroPathDistance[oppNeighborNodeId][oppOrigNodeId] = TILE_VOID;
                        macroPathChecked[oppNeighborNodeId][oppOrigNodeId] = true;
                        System.out.print("  ");
                    }
                }
            }
            System.out.println("");
        }

        for (int origNodeId = 0; origNodeId<nodeCount; origNodeId++) {
            MapLocation nodeLoc = locationForNode(origNodeId);
            cowGrowthMap[nodeLoc.x][nodeLoc.y] = 9;
        }

        System.out.println("FINISH MACRO ASSESSMENT: " + Clock.getRoundNum());

        printMap();
    }

    private MapLocation locationForNode(int nodeId) {
        return new MapLocation((nodeId/nodesInLine) * nodeSpacing, (nodeId%nodesInLine) * nodeSpacing);
    }

    //================================================================================
    // Map Updates, Checks, and Assessments Methods
    //================================================================================

    public void checkForUpdates() throws GameActionException
    {
        if (rc.getType() == RobotType.HQ)
            assessMap();
        else if (shouldCheckBroadCasts())
            readBroadcastForNewMap();
    }

    public boolean shouldCheckBroadCasts() throws GameActionException
    {
        return !mapUploaded;
    }

    public void resetMapUploadedFlag() throws GameActionException
    {
        mapUploaded = false;
        broadcastUploadedFlag();
    }

    private void assessMap() throws GameActionException
    {
        if (mapUploaded) return;

        // Map Details are read in from the game board
        double[][] cgMap = rc.senseCowGrowth();
        cowGrowthMap = new int[MAP_WIDTH][MAP_HEIGHT];

        if (!mapUploaded) {
            rc.setIndicatorString(1, "Working On Map");
            for (int x=0; x<=MAP_WIDTH/2;x++) {
                for (int y=0; y<MAP_HEIGHT;y++) {
                    int ordValue = rc.senseTerrainTile(new MapLocation(x,y)).ordinal();//0 NORMAL, 1 ROAD, 2 VOID, 3 OFF_MAP
                    roadMap[x][y] = ordValue;
                    // cowGrowthMap[x][y] = (int)cgMap[x][y];
                    cowGrowthMap[x][y] = 0;

                    roadMap[MAP_WIDTH-x-1][MAP_HEIGHT-y-1] = ordValue;
                    // cowGrowthMap[MAP_WIDTH-x-1][MAP_HEIGHT-y-1] = (int)cgMap[MAP_WIDTH-x-1][MAP_HEIGHT-y-1];
                    cowGrowthMap[MAP_WIDTH-x-1][MAP_HEIGHT-y-1] = 0;

                    if (ordValue > 1) {
                        voidDensity++;
                        roadMap[x][y] = TILE_VOID;
                        cowGrowthMap[x][y] = 0;

                        roadMap[MAP_WIDTH-x-1][MAP_HEIGHT-y-1] = TILE_VOID;
                        cowGrowthMap[MAP_WIDTH-x-1][MAP_HEIGHT-y-1] = 0;
                    }
                }
            }
            voidDensity/=(MAP_HEIGHT*MAP_WIDTH);

            MapLocation[] path =  Path.simplePath(rc, this, cache.MY_HQ, cache.ENEMY_HQ);
            Path.printPath(path, this);

            assessMacroPathing();

            System.out.println("Finished Map: " + Clock.getRoundNum());
            rc.setIndicatorString(1, "Done with Map");
            mapUploaded = true;
            broadcastMap();
            return;
        }
    }

    public void readBroadcastForNewMap() throws GameActionException
    {
        MapLocation loadStatus = VectorFunctions.intToLoc(rc.readBroadcast(Utilities.mapLoadedChannel));

        boolean pullMapDetails = false;
        if (mapUploaded = loadStatus.x == 1 && pathingStrat == PathingStrategy.DefaultBug) {
            pathingStrat = PathingStrategy.SmartBug;
            pullMapDetails = true;
        }

        roadMap = new int[MAP_WIDTH][MAP_HEIGHT];
        cowGrowthMap = new int[MAP_WIDTH][MAP_HEIGHT];
        if(pullMapDetails) {
            System.out.println("Soldier Pulling Map");
            for (int x=0; x<MAP_WIDTH;x++) {
                for (int y=0; y<MAP_HEIGHT;y++) {
                    MapLocation mapDetails = VectorFunctions.intToLoc(rc.readBroadcast(y*MAP_HEIGHT+x));
                    roadMap[x][y] = mapDetails.x;
                    cowGrowthMap[x][y] = mapDetails.y;
                }
            }
            // printMap();
        }
    }

    public void broadcastMap() throws GameActionException
    {
        for (int y=0; y<MAP_HEIGHT;y++) {
            for (int x=0; x<MAP_WIDTH;x++) {
                rc.broadcast(y*MAP_HEIGHT+x, VectorFunctions.locToInt(new MapLocation(roadMap[y][x], cowGrowthMap[y][x])));
            }
        }
        rc.setIndicatorString(1, "Finished With Broadcasting Map and Flow");
        broadcastUploadedFlag();
    }

    // x:(Map Loaded) y:(Flow Loaded)
    public void broadcastUploadedFlag() throws GameActionException
    {
        rc.broadcast(Utilities.mapLoadedChannel, VectorFunctions.locToInt(new MapLocation(mapUploaded ? 1 : 0, (int)(voidDensity*100))));
    }

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
                    System.out.print("  ");
            }
            System.out.println("");
        }
        System.out.println("");
    }
}
