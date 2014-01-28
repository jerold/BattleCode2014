package Nerazim;

import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotController;
import battlecode.common.RobotType;

//type is what pattern to fire. 1: pull, 2: spoke pull, 3: circle
public class GenericTower
{
    // these are the channels that we will use to communicate to our bots
    static final int enemyHQ = 1;
    static final int ourHQ = 2;
    static final int rallyPoint = 3;
    static final int needNoiseTower = 4;
    static final int needPastr = 5;
    static final int takeDownEnemyPastr = 6;
    static final int enemyPastrInRangeOfHQ = 7;
    static final int rallyPoint2 = 8;
    static final int defendPastr = 9;
    static final int pastLoc = 10;
    static final int morphZergling = 11;
    static final int morphHydralisk = 12;
    static final int hydraliskCount = 13;
    static final int towerLoc = 14;
    static final int towerBuilt = 15;
    static final int pastrBuilt = 16;
    static final int morphRoach = 17;

    RobotController rc;
    MapLocation target;
    MapLocation[] lines1, lines2, lines3, lines4;
    int type;
    boolean troll, first;

    public GenericTower(RobotController rc, boolean troll)
    {
    	this.troll = troll;
    	first = true;
    	//int width = rc.getMapWidth();
    	//int height = rc.getMapHeight();
    	//double[][] cows = rc.senseCowGrowth();
        this.rc = rc;
        target = rc.getLocation();
        //int voidTotal = 0;
        //int cowTotal = 0;
        //int offMap = 0;
        boolean foundPastr = false;
        
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
            Robot[] allAllies = rc.senseNearbyGameObjects(Robot.class, 35, rc.getTeam());

            if (allAllies.length == 0)
            {
                try
                {
                    rc.broadcast(towerLoc, Movement.convertMapLocationToInt(rc.getLocation()));
                } catch (Exception e) {}
            }
            rc.yield();
        }

        /*for(int k = target.x - 10; k < target.x + 10; k++)
        {
        	for(int a = target.y - 10; a < target.y + 10; a++)
        	{
        		if(k < 0 || k >= width || a < 0 || a >= height)
        		{
        			offMap++;
        		}
        		else
        		{
	        		if(rc.senseTerrainTile(new MapLocation(k, a)) == TerrainTile.VOID)
	        		{
	        			if(target.distanceSquaredTo(new MapLocation(k, a)) < 10)
	        			{
	        				voidTotal++;
	        			}
	        		}
	        		else
	        		{
	        			cowTotal += (int)cows[k][a];
	        		}
        		}
        	}
        }

        if(offMap > 250)
        {
        	if(cowTotal > 200)
        	{
        		type = 1;
        	}
        	else
        	{
        		type = 2;
        		lines1 = TowerUtil.generateSpokeLines(rc, target, 1);
                lines2 = TowerUtil.generateSpokeLines(rc, target, 2);
                lines3 = TowerUtil.generateSpokeLines(rc, target, 3);
                lines4 = TowerUtil.generateSpokeLines(rc, target, 4);
        	}
        }
        else if(offMap > 150)
        {
        	if(cowTotal > 400)
        	{
        		type = 1;
        	}
        	else
        	{
        		type = 2;
        		lines1 = TowerUtil.generateSpokeLines(rc, target, 1);
                lines2 = TowerUtil.generateSpokeLines(rc, target, 2);
                lines3 = TowerUtil.generateSpokeLines(rc, target, 3);
                lines4 = TowerUtil.generateSpokeLines(rc, target, 4);
        	}
        }
        else
        {
        	if(voidTotal > 2)
        	{
        		type = 1;
        	}
        	else
        	{
	    		type = 2;
	    		lines1 = TowerUtil.generateSpokeLines(rc, target, 1);
	            lines2 = TowerUtil.generateSpokeLines(rc, target, 2);
	            lines3 = TowerUtil.generateSpokeLines(rc, target, 3);
	            lines4 = TowerUtil.generateSpokeLines(rc, target, 4);
        	}
        }*/
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
                        if (rc.getHealth() < 30)
                        {
                            rc.broadcast(towerBuilt, 0);
                        }
                        else if (rc.readBroadcast(towerBuilt) == 0)
                        {
                            rc.broadcast(towerBuilt, 1);
                        }
		            	MapLocation[] enemies = rc.sensePastrLocations(rc.getTeam().opponent());
		            	Robot[] allies = rc.senseNearbyGameObjects(Robot.class, 100, rc.getTeam());
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
		            	if(allyPastr)
		            	{
		            		if(type == 1)
		                	{
		                		TowerUtil.pullInto(rc, 17, target);
		                	}
		                	else if(type == 2)
		                	{
		                		boolean[] spokes = TowerUtil.goodSpokeDirs(rc, target);

		                		if(spokes[0])
		                		{
		    		            	for(int k = 0; k < lines1.length; k += 2)
		    		            	{
		    		            		if(lines1[k] != null)
		    		            		{
		    		            			TowerUtil.fireLine(rc, lines1[k], lines1[k + 1], 1);
		    		            		}
		    		            	}
		                		}
		                		if(spokes[1])
		                		{
		    		            	for(int k = 0; k < lines2.length; k += 2)
		    		            	{
		    		            		if(lines2[k] != null)
		    		            		{
		    		            			TowerUtil.fireLine(rc, lines2[k], lines2[k + 1], 1);
		    		            		}
		    		            	}
		                		}
		                		if(spokes[3])
		                		{
		    		            	for(int k = 0; k < lines4.length; k += 2)
		    		            	{
		    		            		if(lines4[k] != null)
		    		            		{
		    		            			TowerUtil.fireLine(rc, lines4[k], lines4[k + 1], 1);
		    		            		}
		    		            	}
		                		}
		                		if(spokes[2])
		                		{
		    		            	for(int k = 0; k < lines3.length; k += 2)
		    		            	{
		    		            		if(lines3[k] != null)
		    		            		{
		    		            			TowerUtil.fireLine(rc, lines3[k], lines3[k + 1], 1);
		    		            		}
		    		            	}
		                		}
		                	}
		            		
		            		first = false;
		            	}
		            	else if(enemyPastr && !allyPastr)
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
