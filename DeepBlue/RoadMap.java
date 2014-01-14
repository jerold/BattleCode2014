package DeepBlue;

import battlecode.common.*;

/**
 * Created by Jerold Albertson on 1/12/14.
 *
 */
public class RoadMap {

    RobotController rc;
    UnitCache cache;

    static int[][] neighborTileOffsets = new int[][]{ {-1,-1}, {0,-1}, {1,-1}, {1,0}, {1,1}, {0,1}, {-1,1}, {-1,0} };

    static int MAX_WIDTH = 100; // Prevent heavy processing on larger maps
    int MAP_WIDTH;
    int MAP_HEIGHT;
    int[][] roadMap;
    int[][] flowMap;

    public enum PathingStrategy {
        DefaultBug,
        SmartBug,
        FlowBug
    }

    PathingStrategy pathingStrat;
    boolean mapUploaded;
    boolean flowUploaded;
    int flowprogress;

    RoadMap(RobotController inRc, UnitCache inCache) throws GameActionException
    {
        rc = inRc;
        cache = inCache;

        MAP_HEIGHT = rc.getMapHeight();
        MAP_WIDTH = rc.getMapWidth();
        roadMap = new int[MAP_WIDTH][MAP_HEIGHT];
        flowMap = new int[MAP_WIDTH][MAP_HEIGHT];
        mapUploaded = false;
        flowUploaded = false;
        pathingStrat = PathingStrategy.DefaultBug;

        if (rc.getType() == RobotType.HQ)
            resetMapLoadedFlags();
    }

    public void checkForUpdates() throws GameActionException
    {
        if (rc.getType() == RobotType.HQ)
            assessMap();
        else if (shouldCheckBroadCasts())
            readBroadcastForNewMapAndFlow();
    }

    public boolean shouldCheckBroadCasts() throws GameActionException
    {
        return !mapUploaded || !flowUploaded;
    }

    public void resetMapLoadedFlags() throws GameActionException
    {
        mapUploaded = false;
        flowUploaded = false;
        flowprogress = 0;
        broadcastLoadedFlags();
    }

    private void assessMap() throws GameActionException
    {
        if (mapUploaded && flowUploaded) return;

        int maxDistanceFromRoad = 5;

        // Map Details are read in from the game board
        if (!mapUploaded) {
            rc.setIndicatorString(1, "Working On Map");
            for (int y=0; y<MAP_HEIGHT;y++) {
                for (int x=0; x<MAP_WIDTH;x++) {
                    int ordValue = rc.senseTerrainTile(new MapLocation(x,y)).ordinal();//0 NORMAL, 1 ROAD, 2 VOID, 3 OFF_MAP
                    int flowValue = 0;
                    switch (ordValue)
                    {
                        case 0:
                            flowValue = maxDistanceFromRoad;
                            break;
                        case 1:
                            flowValue = 0;
                            break;
                        default:
                            flowValue = 999;
                            break;
                    }
                    roadMap[y][x] = ordValue;
                    flowMap[y][x] = flowValue;
                }
            }

            rc.setIndicatorString(1, "Done with Map");
            mapUploaded = true;
            broadcastMapAndFlow();
            return;
        }

        // Flow Gradient leads to roads and somewhat away from void space
        if (!flowUploaded) {
            rc.setIndicatorString(1, "Working On Flow");
            for (int j=flowprogress; j<maxDistanceFromRoad;j++) {
                for (int y=0; y<MAP_HEIGHT-0;y++) {
                    for (int x=0; x<MAP_WIDTH-0;x++) {
                        if (flowMap[y][x] == j) {
                            for (int i=0; i<neighborTileOffsets.length;i++) {
                                if (y+neighborTileOffsets[i][0] > 0
                                        && y+neighborTileOffsets[i][0] < MAP_HEIGHT
                                        && x+neighborTileOffsets[i][1] > 0
                                        && x+neighborTileOffsets[i][1] < MAP_WIDTH) {

                                    if (flowMap[y+neighborTileOffsets[i][0]][x+neighborTileOffsets[i][1]] > j && flowMap[y+neighborTileOffsets[i][0]][x+neighborTileOffsets[i][1]] != 999)
                                        flowMap[y+neighborTileOffsets[i][0]][x+neighborTileOffsets[i][1]] = j+1;
                                }
                            }
                        } else if (j == 0 && flowMap[y][x] == 999) {
                            for (int i=0; i<neighborTileOffsets.length;i++) {
                                if (y+neighborTileOffsets[i][0] > 0
                                        && y+neighborTileOffsets[i][0] < MAP_HEIGHT
                                        && x+neighborTileOffsets[i][1] > 0
                                        && x+neighborTileOffsets[i][1] < MAP_WIDTH) {

                                    if (flowMap[y+neighborTileOffsets[i][0]][x+neighborTileOffsets[i][1]] != 999 && flowMap[y+neighborTileOffsets[i][0]][x+neighborTileOffsets[i][1]] != 0)
                                        flowMap[y+neighborTileOffsets[i][0]][x+neighborTileOffsets[i][1]] = maxDistanceFromRoad+1;
                                }
                            }
                        }
                    }
                }

                flowprogress++;
                return;
            }

            rc.setIndicatorString(1, "Done with Flow");
            flowUploaded = true;
            broadcastMapAndFlow();
        }
    }

    public void readBroadcastForNewMapAndFlow() throws GameActionException
    {
        MapLocation loadStatus = VectorFunctions.intToLoc(rc.readBroadcast(Utilities.mapLoadedChannel));

        boolean pullMapDetails = false;
        if (mapUploaded = loadStatus.x == 1 && pathingStrat == PathingStrategy.DefaultBug) {
            pathingStrat = PathingStrategy.SmartBug;
            pullMapDetails = true;
        }

        if (flowUploaded = loadStatus.y == 1 && pathingStrat == PathingStrategy.SmartBug) {
            pathingStrat = PathingStrategy.FlowBug;
            pullMapDetails = true;
        }

        if(pullMapDetails) {
            System.out.println("Soldier Pulling Map");
            for (int y=0; y<MAP_HEIGHT;y++) {
                for (int x=0; x<MAP_WIDTH;x++) {
                    MapLocation mapDetails = VectorFunctions.intToLoc(rc.readBroadcast(y*MAP_HEIGHT+x));
                    roadMap[y][x] = mapDetails.x;
                    flowMap[y][x] = mapDetails.y;
                }
            }
        }

        rc.setIndicatorString(1, "Finished With Reading In Map and Flow");
    }

    public void broadcastMapAndFlow() throws GameActionException
    {
        if (flowUploaded) {
            System.out.println("Write");
            printMap();
        }

        for (int y=0; y<MAP_HEIGHT;y++) {
            for (int x=0; x<MAP_WIDTH;x++) {
                rc.broadcast(y*MAP_HEIGHT+x, VectorFunctions.locToInt(new MapLocation(roadMap[y][x], flowMap[y][x])));
            }
        }
        rc.setIndicatorString(1, "Finished With Broadcasting Map and Flow");
        broadcastLoadedFlags();
    }

    // x:(Map Loaded) y:(Flow Loaded)
    public void broadcastLoadedFlags() throws GameActionException
    {
        rc.broadcast(Utilities.mapLoadedChannel, VectorFunctions.locToInt(new MapLocation(mapUploaded ? 1 : 0, flowUploaded ? 1 : 0)));
    }

    public void printMap()
    {
        for (int i=0; i<MAP_WIDTH;i++) {
            for (int j=0; j<MAP_HEIGHT;j++) {
                if (flowMap[i][j] != 999)
                    System.out.print(flowMap[i][j]);
                else
                    System.out.print(" ");
            }
            System.out.println("");
        }
        System.out.println("");
    }
}
