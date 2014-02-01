package DeepBlue.Strategies;

import DeepBlue.*;
import DeepBlue.Soldiers.UnitStrategyType;
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
                while(!towerNear(rc))
                {
                	if(rc.getHealth() < 50)
                	{
                		rc.setIndicatorString(0, "Help");
                		request.sendRequest(pastrSpot, true);
                		Soldiers.changeStrategy(UnitStrategyType.Reinforcement);
                        UnitStratReinforcement.initialize(rc);
                		break;
                	}
                	simpleFight(rc);
                	rc.yield();
                }
            }
            else if(type > 3)
            {
                while(!towerNear(rc))
                {
                	if(rc.getHealth() < 50)
                	{
                		rc.setIndicatorString(0, "Help");
                		request.sendRequest(pastrSpot, true);
                		Soldiers.changeStrategy(UnitStrategyType.Reinforcement);
                        UnitStratReinforcement.initialize(rc);
                		break;
                	}
                	simpleFight(rc);
                	rc.yield();
                }
                for(int k = 0; k < type; k++){rc.yield();}
            }
            else
            {
                while(!towerNear(rc))
                {
                	if(rc.getHealth() < 50)
                	{
                		rc.setIndicatorString(0, "Help");
                		request.sendRequest(pastrSpot, true);
                		Soldiers.changeStrategy(UnitStrategyType.Reinforcement);
                        UnitStratReinforcement.initialize(rc);
                		break;
                	}
                	simpleFight(rc);
                	rc.yield();
                }
                for(int k = 0; k < type; k++)
                {
                	if(rc.getHealth() < 50)
                	{
                		rc.setIndicatorString(0, "Help");
                		request.sendRequest(pastrSpot, true);
                		Soldiers.changeStrategy(UnitStrategyType.Reinforcement);
                		break;
                	}
                	simpleFight(rc);
                	rc.yield();
                }
            }
            while(rc.senseNearbyGameObjects(Robot.class, 100, rc.getTeam().opponent()).length > 0)
            {
            	if(rc.getHealth() < 50)
            	{
            		rc.setIndicatorString(0, "Help");
            		request.sendRequest(pastrSpot, true);
            		Soldiers.changeStrategy(UnitStrategyType.Reinforcement);
                    UnitStratReinforcement.initialize(rc);
            		break;
            	}
            	simpleFight(rc);
            	rc.yield();
            }
            request.madeIt(true);
            if(rc.isActive() && rc.senseNearbyGameObjects(Robot.class, 100, rc.getTeam().opponent()).length == 0)
            {
                rc.construct(RobotType.PASTR);
            }
            else
            {
            	simpleFight(rc);
            }
        }
        else if (rc.getLocation().isAdjacentTo(pastrSpot))
        {
            if (rc.canMove(rc.getLocation().directionTo(pastrSpot)))
            {
                rc.move(rc.getLocation().directionTo(pastrSpot));
            }
        }
        else
        {
        	if(rc.getHealth() < 50)
        	{
        		rc.setIndicatorString(0, "Help");
        		//request.sendRequest(pastrSpot, true);
        		//Soldiers.changeStrategy(UnitStrategyType.Reinforcement);
                //UnitStratReinforcement.initialize(rc);
        	}
        	//rc.yield();
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
    
    public static void simpleFight(RobotController rc)
    {
    	Robot[] enemies = rc.senseNearbyGameObjects(Robot.class, 10, rc.getTeam().opponent());
    	
    	if(enemies.length > 0)
    	{
    		try
    		{
    			if(rc.isActive() && rc.canAttackSquare(rc.senseRobotInfo(enemies[0]).location))
    			{
    				rc.attackSquare(rc.senseRobotInfo(enemies[0]).location);
    			}
			}
    		catch(GameActionException e){}
    	}
    }
}
