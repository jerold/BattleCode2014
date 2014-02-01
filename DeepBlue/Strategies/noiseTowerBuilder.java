package DeepBlue.Strategies;

import DeepBlue.*;
import DeepBlue.Soldiers.UnitStrategyType;
import battlecode.common.*;

/**
 * Created by fredkneeland on 1/28/14.
 */
public abstract class noiseTowerBuilder extends UnitStrategy {
    static RobotController rc;
    static MapLocation towerSpot;
    static towerPastrRequest request;

    public static void initialize(RobotController rcIn, int get[]) throws GameActionException
    {
        rc = rcIn;
        request = new towerPastrRequest(rc);
        towerSpot = TowerUtil.convertIntToMapLocation(get[0]);
        rc.setIndicatorString(0, "" + towerSpot.toString());
        Soldiers.nav.setDestination(towerSpot);
    }

    public static void run() throws GameActionException
    {
        if (rc.getLocation().equals(towerSpot))
        {
        	if(rc.isActive() && rc.senseNearbyGameObjects(Robot.class, 100, rc.getTeam().opponent()).length == 0)
        	{
        		request.madeIt(false);
        		rc.construct(RobotType.NOISETOWER);
        	}
        	else
        	{
        		pastrBuilder.simpleFight(rc);
        	}
        }
        if(rc.getHealth() < 50)
        {
        	rc.setIndicatorString(0, "Help");
        	request.sendRequest(towerSpot, false);
    		Soldiers.changeStrategy(UnitStrategyType.Reinforcement);
        }
    }
}
