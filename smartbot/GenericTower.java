package smartbot;

import battlecode.common.*;

public class GenericTower
{
    RobotController rc;
    boolean troll;
    MapLocation target;
    boolean pull;
    
    public GenericTower(RobotController rc, boolean troll, MapLocation target)
    {
        this.rc = rc;
        this.troll = troll;
        this.target = target;
        pull = true;
    }

    public void run()
    {
        while(true)
        {
            
            if(rc.getType() == RobotType.NOISETOWER)
            {
            	if(troll)
            	{
            		try
    				{
    					if(rc.isActive())
    					{
    						rc.attackSquare(target);
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
		            	MapLocation[] allies = rc.sensePastrLocations(rc.getTeam());
		            	MapLocation pastrE = null;
		            	boolean enemyPastr = false;
		            	boolean allyPastr = false;
		            	
		            	for(int k = 0; k < enemies.length; k++)
		            	{
		            		if(rc.senseRobotInfo(enemies[k]).type == RobotType.PASTR)
		            		{
		            			enemyPastr = true;
		            			pastrE = rc.senseRobotInfo(enemies[k]).location;
		            		}
		            	}
		            	for(int k = 0; k < allies.length; k++)
		            	{
		            		if(allies[k].distanceSquaredTo(target) < 5)
		            		{
		            			allyPastr = true;
		            		}
		            	}
		            	if(!enemyPastr && allyPastr)
		            	{
		            		if(pull)
							{
								Utilities.pullInto(rc, 15, target);
								pull = false;
							}
							else
							{
								for(int k = 20; k > 4; k -= 1)
			            		{
			            			Utilities.fireCircle(rc, k, target);
			            		}
							}
		            	}
		            	else if(enemyPastr && allyPastr)
		            	{
		            		if(pull)
							{
								Utilities.pullInto(rc, 15, target);
								pull = false;
							}
							else
							{
								for(int k = 20; k > 4; k -= 1)
			            		{
			            			Utilities.fireCircle(rc, k, target);
			            			while(!rc.isActive()){}
			            			rc.attackSquare(pastrE);
			            		}
							}
		            	}
		            	else if(enemyPastr)
		            	{
		            		rc.attackSquare(pastrE);
		            	}
	            	}
	            	catch(Exception e){}
	            }
            }

            rc.yield();
        }
    }
}
