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
    Direction[] dirs = Direction.values();
    int[] closest;

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
		        		else if(rc.senseRobotInfo(bot).isConstructing)
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
	            		if(rc.isActive())
	            		{
			            	MapLocation[] enemies = rc.sensePastrLocations(rc.getTeam().opponent());
			            	Robot[] allies = rc.senseNearbyGameObjects(Robot.class, 16, rc.getTeam());
			            	MapLocation pastrE = null;
			            	boolean enemyPastr = false;
			            	boolean allyPastr = false;
			            	boolean ally = false;
			            	
			            	for(MapLocation pastr : enemies)
			            	{
			            		if(pastr.distanceSquaredTo(target) < 300)
			            		{
			            			enemyPastr = true;
			            			pastrE = pastr;
			            		}
			            	}
			            	if(allies.length > 0)
			            	{
			            		RobotInfo info;
			            		for(Robot bot : allies)
			            		{
			            			info = rc.senseRobotInfo(bot);
			            			if(info.isConstructing || info.type == RobotType.PASTR)
			            			{
			            				allyPastr = true;
			            			}
			            			if(info.location.equals(target))
			            			{
			            				ally = true;
			            			}
			            		}
			            	}
			            	if(allyPastr || first || (ally && !enemyPastr))
			            	{
			            		if(type == 1)
			                	{
			                		for(int t = 0; t < 8; t++)
			                		{
			                			getClosest();
			                			MapLocation start = lines[t * 2];
			                			while(closest[t] != 0 && start.distanceSquaredTo(target) > closest[t])
			                			{
			                				start = start.add(start.directionTo(target));
			                			}
			                			TowerUtil.fireLine(rc, start, lines[t * 2 + 1], 1, request);
			                			if(t == 4)
			                			{
			                				getClosest();
	                                        start = lines[(t + 2) * 2];
	                                        while(closest[t + 2] != 0 && start.distanceSquaredTo(target) > closest[t + 2])
	                                        {
	                                            start = start.add(start.directionTo(target));
	                                        }
			                				TowerUtil.fireLine(rc, start, lines[(t + 2) * 2 + 1], 1, request);
			                			}
			                		}
			                	}
			            		first = false;
			            	}
			            	else if(enemyPastr)
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
    
    private void getClosest()
    {
    	MapLocation[] enemyPastrLocs = rc.sensePastrLocations(rc.getTeam().opponent());
		Robot[] seenEnemies = rc.senseNearbyGameObjects(Robot.class, 100, rc.getTeam().opponent());
		closest = new int[8];
		for(int k = 0; k < enemyPastrLocs.length; k++)
		{
			for(int a = 0; a < 8; a++)
			{
				if(target.directionTo(enemyPastrLocs[k]) == dirs[a])
				{
					int dist = target.distanceSquaredTo(enemyPastrLocs[k]);
					if(closest[a] == 0 || dist < closest[a])
					{
						closest[a] = dist;
					}
					break;
				}
			}
		}
		for(int k = 0; k < seenEnemies.length; k++)
		{
			for(int a = 0; a < 8; a++)
			{
				try
				{
					if(target.directionTo(rc.senseRobotInfo(seenEnemies[k]).location) == dirs[a])
					{
						int dist = target.distanceSquaredTo(rc.senseRobotInfo(seenEnemies[k]).location);
						if(closest[a] == 0 || dist < closest[a])
						{
							closest[a] = dist;
						}
						break;
					}
				}
				catch(Exception e){}
			}
		}
    }
}
