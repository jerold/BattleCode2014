package DeepBlue;

import battlecode.common.*;

//type is what pattern to fire. 1: pull
public class GenericTower
{
	private RobotController rc;
    private MapLocation target;
    private int type;
    private boolean troll, first;
    private towerPastrRequest request;
    MapLocation[] lines;

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
		        		if(rc.senseRobotInfo(bot).type == RobotType.PASTR)
		        		{
		        			target = rc.senseRobotInfo(bot).location;
		        			foundPastr = true;
		        		}
		        		else if(rc.senseRobotInfo(bot).type == RobotType.SOLDIER)
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
        
        lines = TowerUtil.generatePullLines(rc, target);
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
		            	}
		            	if(allyPastr || first)
		            	{
		            		if(type == 1)
		                	{
		                		for(int k = 0; k < 8; k++)
		                		{
		                			TowerUtil.fireLine(rc, lines[k * 2], lines[k * 2 + 1], 1, request);
		                			if(k == 4)
		                			{
		                				TowerUtil.fireLine(rc, lines[(k + 2) * 2], lines[(k + 2) * 2 + 1], 1, request);
		                			}
		                		}
		                	}
		            		first = false;
		            	}
		            	else
		            	{
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
