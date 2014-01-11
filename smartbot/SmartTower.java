package smartbot;

import battlecode.common.*;

import java.util.Random;

public class SmartTower
{
    RobotController rc;
    int width, height, corner;
    int[] radii;
    MapLocation target;
    Random rand = new Random();
    Direction[] directions = {Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST, Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST};

    public SmartTower(RobotController rc)
    {
        this.rc = rc;
        corner = Utilities.findBestCorner(rc);
        width = rc.getMapWidth();
        height = rc.getMapHeight();
        int[] radii = {15, 13, 11, 9, 7};
        this.radii = radii;
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
	            	MapLocation pastr = null;
	            	boolean enemyPastr = false;
	            	boolean allyPastr = false;
	            	
	            	for(int k = 0; k < enemies.length; k++)
	            	{
	            		if(rc.senseRobotInfo(enemies[k]).type == RobotType.PASTR)
	            		{
	            			enemyPastr = true;
	            		}
	            	}
	            	for(int k = 0; k < allies.length; k++)
	            	{
	            		if(rc.senseRobotInfo(allies[k]).type == RobotType.PASTR)
	            		{
	            			rc.setIndicatorString(1, "found pastr");
	            			allyPastr = true;
	            			pastr = rc.senseRobotInfo(allies[k]).location;
	            		}
	            	}
	            	if(!enemyPastr && allyPastr)
	            	{
	            		rc.setIndicatorString(0, "");
	            		for(int k = 20; k > 4; k -= 1)
	            		{
	            			Utilities.fireCircle(rc, k, pastr);
	            		}
	            	}
	            	else
	            	{
	            		rc.setIndicatorString(0, "do not function");
	            	}
            	}
            	catch(Exception e){}
            }

            rc.yield();
        }
    }
}
