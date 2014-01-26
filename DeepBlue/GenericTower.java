package DeepBlue;

import battlecode.common.Clock;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotController;
import battlecode.common.RobotType;

//type is what pattern to fire. 1: pull
public class GenericTower
{
	private RobotController rc;
    private towerPastrRequest request;
    private MapLocation target;
    private int type, lastPastr;
    private boolean troll, first, pastrLast;

    public GenericTower(RobotController rc, boolean troll)
    {
    	this.troll = troll;
    	first = true;
        this.rc = rc;
        target = rc.getLocation();
        boolean foundPastr = false;
        request = new towerPastrRequest(rc);
        
        if(troll)
        {
        	int minDist = 301;
        	MapLocation[] pastrs = rc.sensePastrLocations(rc.getTeam().opponent());
        	for(MapLocation pastr : pastrs)
        	{
        		if(minDist > pastr.distanceSquaredTo(rc.getLocation()))
        		{
        			minDist = pastr.distanceSquaredTo(rc.getLocation());
        			target = pastr;
        		}
        	}
        }
        else
        {
	        while(!foundPastr)
	        {
	        	Robot[] bots = rc.senseNearbyGameObjects(Robot.class, 8, rc.getTeam());
	        	for(Robot bot : bots)
	        	{
	        		try
	        		{
		        		if(rc.senseRobotInfo(bot).type == RobotType.PASTR || rc.senseRobotInfo(bot).type == RobotType.SOLDIER)
		        		{
		        			target = rc.senseRobotInfo(bot).location;
		        			foundPastr = true;
		        		}
	        		}
	        		catch(Exception e){}
	        	}
	            rc.yield();
	        }
        }
        
        type = 1;
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
		            	MapLocation[] enemies = rc.sensePastrLocations(rc.getTeam().opponent());
		            	Robot[] allies = rc.senseNearbyGameObjects(Robot.class, 16, rc.getTeam());
		            	MapLocation pastrE = null;
		            	boolean enemyPastr = false;
		            	boolean allyPastr = false;
		            	
		            	for(int k = 0; k < enemies.length; k++)
		            	{
		            		if(enemies[k].distanceSquaredTo(target) < 300)
		            		{
		            			enemyPastr = true;
		            			pastrE = enemies[k];
		            		}
		            	}
		            	if(allies.length > 0)
		            	{
		            		allyPastr = true;
		            		pastrLast = true;
		            	}
		            	if(allyPastr || first)
		            	{
		            		if(type == 1)
		                	{
		                		TowerUtil.pullInto(rc, 17, target);
		                	}
		            		first = false;
		            	}
		            	else
		            	{
		            		if(pastrLast == true)
		            		{
		            			pastrLast = false;
		            			request.sendRequest();
		            			lastPastr = Clock.getRoundNum();
		            		}
		            		else
		            		{
		            			if(Clock.getRoundNum() - lastPastr >= 300 && !request.isPending())
		            			{
		            				request.sendRequest();
		            			}
		            		}
			            	if(enemyPastr)
			            	{
			            		rc.attackSquare(pastrE);
			            	}
		            	}
	            	}
	            	catch(Exception e){}
	            }
            }

            rc.yield();
        }
    }
}
