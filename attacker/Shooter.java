package attacker;

import battlecode.common.*;

public class Shooter
{
	private RobotController rc;
	private boolean hq;
	
	public Shooter(RobotController rc)
	{
		this.rc = rc;
		if(rc.getType() == RobotType.HQ)
		{
			hq = true;
		}
		else
		{
			hq = false;
		}
	}
	
	public void fire()
	{
		int radius;
		
		try
		{
			if(hq)
			{
				radius = 16;
				Robot[] enemies = rc.senseNearbyGameObjects(Robot.class, radius, rc.getTeam().opponent());
				Direction[] dirs = Direction.values();
				Robot target = null;
				int maxValue = 0;
				
				for(int k = 0; k < enemies.length; k++)
				{
					MapLocation loc = rc.senseRobotInfo(enemies[k]).location;
					int value = 2;
					for(int a = 0; a < 8; a++)
					{
						try
						{
							if(rc.senseObjectAtLocation(loc.add(dirs[a])).getTeam() == rc.getTeam().opponent())
							{
								value++;
							}
							else if(rc.senseObjectAtLocation(loc.add(dirs[a])).getTeam() == rc.getTeam())
							{
								value--;
							}
						}
						catch(Exception e){}
					}
					
					if(value > maxValue)
					{
						maxValue = value;
						target = enemies[k];
					}
				}
				
				if(target != null)
				{
					rc.attackSquare(rc.senseRobotInfo(target).location);
				}
				
			}
			else
			{
				radius = 10;
				Robot[] enemies = rc.senseNearbyGameObjects(Robot.class, radius, rc.getTeam().opponent());
				Robot target = null;
				
				for(int k = 0; k < enemies.length; k++)
				{
					if(rc.senseRobotInfo(enemies[k]).type != RobotType.HQ)
					{
						if(target == null)
						{
							target = enemies[k];
						}
						else if(rc.senseRobotInfo(enemies[k]).health < rc.senseRobotInfo(target).health)
						{
							target = enemies[k];
						}
					}
				}
				
				if(target != null)
				{
					rc.attackSquare(rc.senseRobotInfo(target).location);
				}
			}
		}
		catch(Exception e){}
	}
}
