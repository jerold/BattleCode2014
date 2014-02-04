package DeepBlue;

/**
 * Created by fredkneeland on 1/7/14.
 */

import battlecode.common.*;

public class Utilities
{
    static Direction[] directions = Direction.values();

    // channels for communication
    static final public int startMapChannels = 0;
    static final public int mapUploadedChannel = 10000;
    static final public int macroUploadedChannel = 10001;
    static final public int macroExpectChannel = 10002;
    static final public int startMacroChannels = 10003;

    static final public int unitNeededChannel = 20000;

    static final public int unitNeededScout = 1;
    static final public int unitNeededPastrKiller = 2;
    static final public int unitNeededPastrDefense = 3;
    static final public int unitNeededDarkTemplar = 4;
    static final public int unitNeededHQSurround = 5;
    static final public int unitNeededOurPastrKiller = 6;
    static final public int unitNeededHQPastr = 7;
    static final public int unitNeededHQTower = 8;
    static final public int unitNeededReinforcement = 9;
    static final public int unitNeededSurroundTester = 10;
    static final public int unitNeededBlockadeRunner = 11;

    static final public int startRallyPointChannels = 20001;
    static final public int FrontLineRally = 0;
    static final public int ReinforcementRally = 1;

    static final public int bestPastrLocationChannel1 = 20200;
    static final public int bestPastrLocationChannel2 = 20201;

    static final public int startPastrChannels = 30002;
    static final public int PastrDetailCount = 5; // [LastActiveRound, DefenderCount, EnemyCount, CowCount, PastrLocation]

    static final public int ourPastrKillerStart = 40000;

    //================================================================================
    // PASTR Communication Methods
    //================================================================================

    public static int[] getDetailsForPastr(RobotController rc)
    {
        int pastrNumber = pastrNumberFromId(rc);
        return getDetailsForPastrNumber(rc, pastrNumber);
    }

    public static int[] getDetailsForPastrNumber(RobotController rc, int pastrNumber)
    {
        int pastrDetails[] = new int[PastrDetailCount];
        for (int i = 0; i < PastrDetailCount; i++)
            pastrDetails[i] = -1;
        if (pastrNumber >= 0) {
            try {
                int pastrCount = rc.readBroadcast(startPastrChannels);
                for (int i = 0; i < PastrDetailCount; i++) {
                    pastrDetails[i] = rc.readBroadcast(startPastrChannels+1+pastrCount+PastrDetailCount*pastrNumber+i);
                }
                if (pastrIsResponsive(pastrDetails[0], Clock.getRoundNum()))
                    removeDetailsForPastrToChannels(rc, pastrCount, pastrNumber);
            } catch (Exception e) {}
        }
        return pastrDetails;
    }

    public static void setDetailsForPastr(RobotController rc, int[] details)
    {
        System.out.print("Round:          " + details[0]);
        System.out.print("Defender Count: " + details[1]);
        System.out.print("Enemy Count:    " + details[2]);
        System.out.print("Cow Count:      " + details[3]);
        System.out.print("Location:       " + details[4]);

        int pastrNumber = pastrNumberFromId(rc);
        setDetailsForPastrNumber(rc, pastrNumber, details);
    }

    public static void setDetailsForPastrNumber(RobotController rc, int pastrNumber, int[] details)
    {
        if (details.length != PastrDetailCount)
            return;

        try {
            int pastrCount = rc.readBroadcast(startPastrChannels);
            if (pastrNumber >= 0) {
                for (int i = 0; i < PastrDetailCount; i++) {
                    rc.broadcast(startPastrChannels+1+pastrCount+PastrDetailCount*pastrNumber+i, details[i]);
                }
            } else {
                addDetailsForPastrToChannels(rc, pastrCount, details);
            }
        } catch (Exception e) {}
    }

    public static int pastrNumberFromId(RobotController rc)
    {
        try {
            int pastrCount = rc.readBroadcast(startPastrChannels);
            for (int i = 0; i < pastrCount; i++) {
                int idAtIndex = rc.readBroadcast(1+startPastrChannels+i);
                if (idAtIndex == rc.getRobot().getID())
                    return i;
            }
        } catch (Exception e) {}
        return -1;
    }

    public static void addDetailsForPastrToChannels(RobotController rc, int pastrCount, int[] details)
    {
        if (details.length != PastrDetailCount)
            return;

        int newPastrCount = pastrCount + 1;
        try {
            rc.broadcast(startPastrChannels, newPastrCount);
            rc.broadcast(startPastrChannels+newPastrCount, rc.getRobot().getID());
            setDetailsForPastrNumber(rc, newPastrCount, details);
        } catch (Exception e) {}
    }

    public static void removeDetailsForPastrToChannels(RobotController rc, int pastrCount, int pastrNumber)
    {
        int newPastrCount = pastrCount - 1;
        try {
            for (int i = 0; i < pastrCount-pastrNumber; i++) {
                int laterPastrId = rc.readBroadcast(startPastrChannels+1+pastrNumber+i+1);
                int[] laterPastrDetails = getDetailsForPastrNumber(rc, pastrNumber+i+1);
                rc.broadcast(startPastrChannels+1+pastrNumber+i, laterPastrId);
                setDetailsForPastrNumber(rc, pastrNumber+i, laterPastrDetails);
            }
            rc.broadcast(startPastrChannels, newPastrCount);
        } catch (Exception e) {}
    }

    public static boolean pastrIsResponsive(int lastRound, int round)
    {
        if (round - lastRound > 10)
            return false;
        return true;
    }





    //================================================================================
    // Packing Methods
    //================================================================================

//    /*
//    Packs a set of boolean values into a single int
//     */
//    public static int packedMap(boolean[] values)
//    {
//        if (values.length != 32) System.out.println("PACKING THE WRONG NUMBER OF VALUES!!!");
//        int packet = 0;
//        for (int i=0; i < values.length; i++) {
//            packet = (packet<<1) + (values[i] ? 1 : 0);
//        }
//        return packet;
//    }
//
//    /*
//    Unpacks a set of boolean values from within an single int
//     */
//    public static boolean[] unpackedMap(int packet)
//    {
//        boolean[] values = new boolean[32];
//        for (int i=0; i < values.length; i++) {
//            values[values.length-i-1] = packet%2 == 1 ? true : false;
//            packet = packet>>1;
//        }
//        return values;
//    }

    public static int packetPush(int packet, boolean value)
    {
        return (packet<<1) + (value ? 1 : 0);
    }

    public static boolean packetPeek(int packet)
    {
//        int packetCopy = packet;
//        for (int i=0; i < RoadMap.MAX_PACKED_VALUES; i++) {
//            System.out.print(packetCopy%2 == 1 ? 1 : 0);
//            packetCopy = packetCopy>>1;
//        }
//        System.out.println("");
        return packet%2 == 1;
    }

    public static int packetPitch(int packet)
    {
//        int packetCopy = packet;
//        for (int i=0; i < RoadMap.MAX_PACKED_VALUES; i++) {
//            System.out.print(packetCopy%2 == 1 ? 1 : 0);
//            packetCopy = packetCopy>>1;
//        }
//        System.out.println("");
        return packet>>1;
    }



    //================================================================================
    // Helper Methods
    //================================================================================

    public static final boolean directionIsLeftOf(Direction inQuestion, Direction heading)
    {
        switch (heading) {
            case NORTH:
                if (inQuestion == Direction.NORTH_WEST || inQuestion == Direction.WEST || inQuestion == Direction.SOUTH_WEST) return true;
                return false;
            case NORTH_EAST:
                if (inQuestion == Direction.NORTH || inQuestion == Direction.NORTH_WEST || inQuestion == Direction.WEST) return true;
                return false;
            case EAST:
                if (inQuestion == Direction.NORTH_EAST || inQuestion == Direction.NORTH || inQuestion == Direction.NORTH_WEST) return true;
                return false;
            case SOUTH_EAST:
                if (inQuestion == Direction.EAST || inQuestion == Direction.NORTH_EAST || inQuestion == Direction.NORTH) return true;
                return false;
            case SOUTH:
                if (inQuestion == Direction.SOUTH_EAST || inQuestion == Direction.EAST || inQuestion == Direction.NORTH_EAST) return true;
                return false;
            case SOUTH_WEST:
                if (inQuestion == Direction.SOUTH || inQuestion == Direction.SOUTH_EAST || inQuestion == Direction.EAST) return true;
                return false;
            case WEST:
                if (inQuestion == Direction.SOUTH_WEST || inQuestion == Direction.SOUTH || inQuestion == Direction.SOUTH_EAST) return true;
                return false;
            case NORTH_WEST:
                if (inQuestion == Direction.WEST || inQuestion == Direction.SOUTH_WEST || inQuestion == Direction.SOUTH) return true;
                return false;
        }
        return false;
    }

    public static final Direction directionByOrdinal[] = Direction.values();

    public static double distanceBetweenTwoPoints(MapLocation p, MapLocation q){
        return Math.sqrt((p.x - q.x)*(p.x-q.x) + (p.y-q.y)*(p.y-q.y));
    }

    public static double distanceBetweenTwoPoints(
            double p_x, double p_y, double q_x, double q_y){
        return Math.sqrt((p_x - q_x)*(p_x-q_x) + (p_y-q_y)*(p_y-q_y));
    }

    public static boolean checkHQTower(RobotController rc){//returns true if an HQ tower should be set up
        try{
            MapLocation HQ = rc.senseHQLocation();
            if(TowerUtil.getHQSpotScore(rc, HQ) >= 10){
                rc.setIndicatorString(0, "HQ Tower");
                return true;

            } else {
                return false;
            }
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }
}

