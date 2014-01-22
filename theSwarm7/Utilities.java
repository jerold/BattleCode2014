package theSwarm7;

/**
 * Created by fredkneeland on 1/7/14.
 */

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;

public class Utilities
{
    static Direction[] directions = Direction.values();

    // channels for communication
    static final int startMapChannels = 0;
    static final int mapUploadedChannel = 10000;
    static final int macroUploadedChannel = 10001;
    static final int macroExpectChannel = 10002;
    static final int startMacroChannels = 10003;

    static final int startPastrChannels = 30002;
    static final int PastrDetailCount = 5; // [LastActiveRound, DefenderCount, EnemyCount, CowCount, PastrLocation]




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
    // Helper Methods
    //================================================================================

    public static final Direction directionByOrdinal[] = Direction.values();

    public static double distanceBetweenTwoPoints(MapLocation p, MapLocation q){
        return Math.sqrt((p.x - q.x)*(p.x-q.x) + (p.y-q.y)*(p.y-q.y));
    }

    public static double distanceBetweenTwoPoints(
            double p_x, double p_y, double q_x, double q_y){
        return Math.sqrt((p_x - q_x)*(p_x-q_x) + (p_y-q_y)*(p_y-q_y));
    }
}
