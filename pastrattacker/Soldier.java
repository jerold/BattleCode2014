package pastrattacker;

import battlecode.common.*;

public class Soldier
{
	int channels = 30;
	public void run(RobotController rc)
	{
		if(rc.getRobot().getID() % 10 < 4)
		{
			scout(rc);
		}
		else
		{
			attack(rc);
		}
	}
	
	private void attack(RobotController rc)
	{
		rc.setIndicatorString(0, "attacker");
		Shooter shooter = new Shooter(rc);
		MapLocation target = null;
		while(true)
		{
			if(target == null)
			{
				try
				{
					for(int k = 0; k < channels && target == null; k += 2)
					{
						int x = rc.readBroadcast(k);
						int y = rc.readBroadcast(k + 1);
						if(x != 0 || y !=0)
						{
							target = new MapLocation(x, y);
						}
					}
				}
				catch(Exception e){}
			}
			else
			{
				Utilities.MoveMapLocation(rc, target, false);
				try
				{
					while(rc.senseObjectAtLocation(target).getTeam() == rc.getTeam().opponent())
					{
						if(rc.isActive())
						{
							shooter.fire();
						}
					}
					
					for(int k = 0; k < channels; k += 2)
					{
						if(rc.readBroadcast(k) == target.x && rc.readBroadcast(k + 1) == target.y)
						{
							rc.broadcast(k, 0);
							rc.broadcast(k + 1, 0);
						}
					}
					target = null;
				}
				catch(Exception e){}
			}
		}
	}
	
	private void scout(RobotController rc)
	{
		rc.setIndicatorString(0, "scout");
		int width = rc.getMapWidth() / 2;
		int height = rc.getMapHeight() / 2;
		MapLocation[] patrolRoute = new MapLocation[1000];
		int offsetX, offsetY;
		
		if(rc.getRobot().getID() % 10 < 2)
		{
			offsetX = 0;
		}
		else
		{
			offsetX = width;
		}
		if(rc.getRobot().getID() % 10 == 0 || rc.getRobot().getID() % 10 == 3)
		{
			offsetY = 0;
		}
		else
		{
			offsetY = height;
		}
		
		int a = 0;
		for(int k = 5; k < width; k += 10)
		{
			for(int t = 5; t < height; t += 5)
			{
				patrolRoute[a] = new MapLocation(k + offsetX, t + offsetY);
				a++;
			}
			
			for(int t = height - 5; t > 0; t -= 5)
			{
				patrolRoute[a] = new MapLocation(k + 5 + offsetX, t + offsetY);
				a++;
			}
		}
		
		while(true)
		{
			try
			{
				for(int k = 0; patrolRoute[k] != null && k < patrolRoute.length; k++)
				{
					rc.setIndicatorString(0, "" + k);
					Utilities.MoveMapLocation(rc, patrolRoute[k], true);
					Robot[] enemies = rc.senseNearbyGameObjects(Robot.class, 10, rc.getTeam().opponent());
					for(int i = 0; i < enemies.length; i++)
					{
						if(rc.senseRobotInfo(enemies[i]).type == RobotType.PASTR)
						{
							MapLocation enemy = rc.senseRobotInfo(enemies[i]).location;
							boolean found = false;
							for(int t = 0; t < channels && !found; t += 2)
							{
								if(rc.readBroadcast(t) == enemy.x && rc.readBroadcast(t + 1) == enemy.y)
								{
									found = true;
								}
							}
							
							if(!found)
							{
								for(int t = 0; t < channels && !found; t += 2)
								{
									if(rc.readBroadcast(t) == 0 && rc.readBroadcast(t + 1) == 0)
									{
										rc.broadcast(t, enemy.x);
										rc.broadcast(t + 1, enemy.y);
										found = true;
									}
								}
							}
						}
					}
				}
			}
			catch(Exception e){System.out.println("Scout Exception");}
		}
	}
}
