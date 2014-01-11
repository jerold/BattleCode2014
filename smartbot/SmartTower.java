package smartbot;

import battlecode.common.*;

import java.util.Random;

public class SmartTower
{
    RobotController rc;
    int corner;
    MapLocation target;
    
    public SmartTower(RobotController rc)
    {
        this.rc = rc;
        corner = Utilities.findBestCorner(rc);
    }

    public void run()
    {
        while(true)
        {
            if(rc.getType() == RobotType.SOLDIER)
            {
                    try
                    {
                        target = Utilities.spotOfSensorTower(rc);
                        Utilities.MoveMapLocation(rc, target, true);

                        if(rc.isActive())
                        {
                            rc.construct(RobotType.NOISETOWER);
                        }
                    }
                    catch(Exception e){}

            }
            else
            {
            	try
            	{
            		rc.setIndicatorString(0, "Tower");
	            	Robot[] enemies = rc.senseNearbyGameObjects(Robot.class, 30, rc.getTeam().opponent());
	            	Robot[] allies = rc.senseNearbyGameObjects(Robot.class, 30, rc.getTeam());
	            	MapLocation pastrA = null;
	            	MapLocation pastrE = null;
	            	boolean enemyPastr = false;
	            	boolean allyPastr = false;
	            	
	            	for(int k = 0; k < enemies.length; k++)
	            	{
	            		if(rc.senseRobotInfo(enemies[k]).type == RobotType.PASTR)
	            		{
	            			enemyPastr = true;
	            			pastrA = rc.senseRobotInfo(enemies[k]).location;
	            		}
	            	}
	            	for(int k = 0; k < allies.length; k++)
	            	{
	            		if(rc.senseRobotInfo(allies[k]).type == RobotType.PASTR)
	            		{
	            			allyPastr = true;
	            			pastrA = rc.senseRobotInfo(allies[k]).location;
	            		}
	            	}
	            	if(!enemyPastr && allyPastr)
	            	{
	            		for(int k = 20; k > 4; k -= 2)
	            		{
	            			Utilities.fireCircle(rc, k, pastrA);
	            		}
	            	}
	            	else if(enemyPastr)
	            	{
	            		rc.attackSquare(pastrE);
	            	}
            	}
            	catch(Exception e){}
            }

            rc.yield();
        }
    }
}
