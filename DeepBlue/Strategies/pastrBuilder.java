package DeepBlue.Strategies;

import DeepBlue.*;
import battlecode.common.*;

/**
 * Created by fredkneeland on 1/28/14.
 */
public abstract class pastrBuilder extends UnitStrategy {
    static MapLocation pastrSpot;
    static towerPastrRequest request;
    static RobotController rc;
    static int type;

    public static void initialize(RobotController rcIn, int[] get) throws GameActionException
    {
        rc = rcIn;
        request = new towerPastrRequest(rc);
        pastrSpot = TowerUtil.convertIntToMapLocation(get[0]);
        Soldiers.nav.setDestination(pastrSpot);
        type = get[1];
    }

    public static void upDate()
    {

    }

    public static void run() throws GameActionException
    {
        if (rc.getLocation().equals(pastrSpot))
        {
            if(rc.senseTeamMilkQuantity(rc.getTeam()) > 9000000){}
            else if(type == 2)
            {
                while(!towerNear(rc)){rc.yield();}
            }
            else if(type > 3)
            {
                while(!towerNear(rc)){rc.yield();}
                for(int k = 0; k < type; k++){rc.yield();}
            }
            else
            {
                while(!towerNear(rc)){rc.yield();}
                for(int k = 0; k < type; k++){rc.yield();}
            }
            while(rc.senseNearbyGameObjects(Robot.class, 100, rc.getTeam().opponent()).length > 0){rc.yield();}
            request.madeIt(true);
            rc.construct(RobotType.PASTR);
        }
        else
        {

        }
    }

    private static boolean towerNear(RobotController rc)
    {
        Robot[] bots = rc.senseNearbyGameObjects(Robot.class, 2, rc.getTeam());
        for(Robot bot : bots)
        {
            try
            {
                if(rc.senseRobotInfo(bot).type == RobotType.NOISETOWER)
                {
                    return true;
                }
            }
            catch(Exception e){}
        }

        return false;
    }
}
