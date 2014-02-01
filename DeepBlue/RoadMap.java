package DeepBlue;

import battlecode.common.*;

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

    int mapPacket;
    int packedValues;
    int packetNumber;

    public static final int TILE_VOID = 999;
    static final int COW_GROWTH_MAP_RESOLUTION = 4;
    static final int MIN_SAFE_ENEMY_HQ_RANGE = 8;
    public static int MAX_PACKED_VALUES = 31;

    public static enum TileType {
        TTOpen,
        TTVoid,
        TTOffMap
    }

    public static enum PathingStrategy {
        DefaultBug,
        SmartBug
    }

    public static enum SymmetryType {
        Horizontal,
        Vertical,
        Diagonal,
        Unknown
    }

    SymmetryType symmetry;
    PathingStrategy pathingStrat;
    Navigator observingNavigator;

    boolean mapUploaded;
    int mapProgress;

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

        packetNumber = 0;
        newPacket();

        pathingStrat = PathingStrategy.DefaultBug;

        // Initialize Upload Flags and indicators
        mapUploaded = false;
        mapProgress = 0;


        // Initialize Map and Pathing signal Flags
        if (rc.getType() == RobotType.HQ) {
            System.out.println("Map Size: " + MAP_WIDTH + ", " + MAP_HEIGHT + "  (" + MAP_WIDTH * MAP_HEIGHT + ")");
            discoverSymmetry();
            broadcastMapFlag();
        }
    }






    //================================================================================
    // Facilitator Methods
    //================================================================================

    public void discoverSymmetry() throws GameActionException
    {
        if (cache.MY_HQ.x == MAP_WIDTH-cache.ENEMY_HQ.x-1 && cache.MY_HQ.y == MAP_HEIGHT-cache.ENEMY_HQ.y-1 && cache.MY_HQ.x != MAP_WIDTH/2 && cache.MY_HQ.y != MAP_HEIGHT/2) symmetry = SymmetryType.Diagonal;
        else if (cache.MY_HQ.x == cache.ENEMY_HQ.x && (MAP_WIDTH%2==0 || cache.MY_HQ.x != MAP_WIDTH/2)) symmetry = SymmetryType.Horizontal;
        else if (cache.MY_HQ.y == cache.ENEMY_HQ.y && (MAP_HEIGHT%2==0 || cache.MY_HQ.y != MAP_HEIGHT/2)) symmetry = SymmetryType.Vertical;
        else symmetry = SymmetryType.Unknown;
        System.out.println("Symmetry: "+symmetry);
    }

    public void checkForUpdates() throws GameActionException
    {
        if (cache.rcType == RobotType.HQ) {
            if (!mapUploaded)
                assessMap();
        } else {
            if (!mapUploaded)
                readBroadcastMap();
        }
    }

    public TileType getTileType(MapLocation loc)
    {
        if (loc.y >= MAP_HEIGHT || loc.y < 0 || loc.x >= MAP_WIDTH || loc.x < 0) return TileType.TTOffMap;
        if (mapUploaded) return roadMap[loc.x][loc.y] == TILE_VOID ? TileType.TTVoid : TileType.TTOpen;
        return rc.senseTerrainTile(loc).ordinal() > 1 ? TileType.TTVoid : TileType.TTOpen;
    }






    //================================================================================
    // Symmetry Based Reading & Broadcasting Methods
    //================================================================================

    private void readMapWithHorizontalSymmetry() throws GameActionException {}

    private void readMapWithVerticalSymmetry() throws GameActionException {}

    private void readMapWithDiagonalSymmetry() throws GameActionException {}

    private void readMapWithUnknownSymmetry() throws GameActionException
    {
        int totalTiles = MAP_WIDTH*MAP_HEIGHT;
        for (int p=0; p<=(totalTiles/MAX_PACKED_VALUES); p++) {
            int packet = readPacket(p);
            for (int i=0; i<MAX_PACKED_VALUES; i++) {
                int tileNumber = p*MAX_PACKED_VALUES+(MAX_PACKED_VALUES-i-1);
                if (tileNumber < totalTiles) roadMap[tileNumber%MAP_WIDTH][tileNumber/MAP_WIDTH] = Utilities.packetPeek(packet) ? 0 : TILE_VOID;
                else break;
                packet = Utilities.packetPitch(packet);
            }
        }
    }

    private void broadcastMapWithHorizontalSymmetry() throws GameActionException {}

    private void broadcastMapWithVerticalSymmetry() throws GameActionException {}

    private void broadcastMapWithDiagonalSymmetry() throws GameActionException {}

    private void broadcastMapWithUnknownSymmetry() throws GameActionException
    {
        for (int y=0; y<MAP_HEIGHT; y++) {
            for (int x=0; x<MAP_WIDTH; x++) {
                addToPacket(roadMap[x][y]);
            }
        }
        sendLastPacket();
    }






    //================================================================================
    // Map Updates, Checks, and Assessments Methods
    //================================================================================

    private void assessMap() throws GameActionException
    {
        rc.setIndicatorString(0, "Working On Map");
//        System.out.println("START MAP ASSESSMENT");

        // Map Details are read in from the game board
        cowGrowthMap = rc.senseCowGrowth();
        for (int x=0; x<MAP_WIDTH;x++) {
            int lowResX = (int)(x/((float)MAP_WIDTH/COW_GROWTH_MAP_RESOLUTION));

            for (int y=0; y<MAP_HEIGHT;y++) {
                int lowResY = (int)(y/((float)MAP_HEIGHT/COW_GROWTH_MAP_RESOLUTION));

                int ordValue = rc.senseTerrainTile(new MapLocation(x,y)).ordinal();//0 NORMAL, 1 ROAD, 2 VOID, 3 OFF_MAP
                roadMap[x][y] = 1-ordValue; // Roads become 0, normals become 1 (higher gets TILE_VOID below
                lowResCowMap[lowResX][lowResY] += (int)cowGrowthMap[x][y];

                if (ordValue > 1) {
                    roadMap[x][y] = TILE_VOID;
                    lowResCowMap[lowResX][lowResY]--;
                }
            }

            Headquarter.tryToSpawn();
        }

        roadMap[cache.MY_HQ.x][cache.MY_HQ.y] = TILE_VOID;
        roadMap[cache.ENEMY_HQ.x][cache.ENEMY_HQ.y] = TILE_VOID;

        printCows();

//        System.out.println("FINISH MAP ASSESSMENT");
        rc.setIndicatorString(0, "Done with Map");

        mapUploaded = true;
        broadcastMap();
    }

    public void readBroadcastMap() throws GameActionException
    {
        mapUploaded = rc.readBroadcast(Utilities.mapUploadedChannel) == 1;
        if (mapUploaded) {
            pathingStrat = PathingStrategy.SmartBug;
            rc.setIndicatorString(0, "Pulling Map");
//            System.out.println("START PULLING MAP");

            readMapWithUnknownSymmetry();

//            printMap();

//            System.out.println("FINISH PULLING MAP");
            rc.setIndicatorString(0, "Finished Pulling Map");
            if (observingNavigator != null) observingNavigator.pathingStrategyChanged();
        }
    }

    public void broadcastMap() throws GameActionException
    {
        rc.setIndicatorString(0, "Broadcasting Map");
//        System.out.println("START BROADCASTING MAP");

        broadcastMapWithUnknownSymmetry();

//        System.out.println("FINISH BROADCASTING MAP");
        rc.setIndicatorString(0, "Finished Broadcasting Map");
        broadcastMapFlag();
    }

    public void broadcastMapFlag() throws GameActionException
    {
        rc.broadcast(Utilities.mapUploadedChannel, mapUploaded ? 1 : 0);
    }






    //================================================================================
    // Packing Methods
    //================================================================================

    public void addToPacket(int tileValue) throws GameActionException
    {
        mapPacket = Utilities.packetPush(mapPacket, tileValue != TILE_VOID);
        packedValues++;
        if (packedValues == MAX_PACKED_VALUES) sendPacket();
    }

    public void newPacket()
    {
        mapPacket = 0;
        packedValues = 0;
    }

    public void sendPacket() throws GameActionException
    {
        rc.broadcast(Utilities.startMapChannels+packetNumber, mapPacket);
        packetNumber++;
        newPacket();
    }

    public void sendLastPacket() throws GameActionException
    {
        while (packedValues < MAX_PACKED_VALUES-1) addToPacket(TILE_VOID);
        addToPacket(TILE_VOID);
    }

    public int readPacket(int packetNumber) throws GameActionException
    {
        return rc.readBroadcast(Utilities.startMapChannels+packetNumber);
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

    public void printCows()
    {
        System.out.println("Cow Map:");
        for (int y=0; y<COW_GROWTH_MAP_RESOLUTION;y++) {
            for (int x=0; x<COW_GROWTH_MAP_RESOLUTION;x++) {
                System.out.print("["+lowResCowMap[x][y]+"]");
            }
            System.out.println("");
        }
    }

    public void printMap()
    {
        for (int y=0; y<MAP_HEIGHT;y++) {
            for (int x=0; x<MAP_WIDTH;x++) {
                if (roadMap[x][y] != TILE_VOID)
                    System.out.print(asciiValue(roadMap[x][y])+""+asciiValue(roadMap[x][y])); // System.out.print(asciiValue(roadMap[x][y]+cowGrowthMap[x][y]) + "" + asciiValue(roadMap[x][y]+cowGrowthMap[x][y]));
                else
                    System.out.print("[]");
            }
            System.out.println("");
        }
        System.out.println("");
    }
}
