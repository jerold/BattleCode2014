package UED2;

import battlecode.common.*;

public class CenterTower
{
    RobotController rc;
    int corner;
    MapLocation target;
    
    public CenterTower(RobotController rc)
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
                        target = new MapLocation(rc.getMapWidth() / 2, rc.getMapHeight() / 2);
                        while(rc.senseTerrainTile(target) == TerrainTile.VOID)
                        {
                        	target = target.add(rc.getLocation().directionTo(rc.senseHQLocation()));
                        }
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
