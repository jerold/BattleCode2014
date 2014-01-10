package smartbot;

import battlecode.common.*;

public class SmartHQ
{
	private RobotController rc;
	public static final int MULE = 1;
	public static final int TOWER = 2;
	public static final int DURAN = 3;
	public static final int GHOST = 4;
	public static final int MARINE = 5;
	public static final int GOLIATH = 6;
	private final int NUMGHOST = 2;
	private final int NUMGOLIATH = 5;
	private final int[] initial = {MULE, TOWER, MARINE, MARINE, DURAN, GHOST, GHOST, GOLIATH, GOLIATH, GOLIATH};
	private final int[] defensive = {MARINE, MARINE, MARINE, GOLIATH};
	private final int[] standard = {GOLIATH, GOLIATH, DURAN, GHOST, GHOST, MARINE};
	private final int[] offensive = {GOLIATH, GOLIATH, GOLIATH, DURAN, GHOST, GHOST};
	
	private int goliaths;
	private int bots;
	private int strat;
	private int[] current;
	
	public SmartHQ(RobotController rc)
	{
		this.rc = rc;
		goliaths = 0;
		bots = 0;
		strat = 2;
		current = initial;
		try
		{
			rc.broadcast(2, NUMGHOST);
		}
		catch (Exception e){}
	}
	
	public void run()
	{
		while(true)
		{
			try
			{
				Utilities.fire(rc);
				if(rc.isActive())
				{
					if(bots == current.length)
					{
						bots = 0;
						switch(strat)
						{
							case 0:
								current = initial;
								break;
							case 1:
								current = defensive;
								break;
							case 2:
								current = standard;
								break;
							case 3:
								current = offensive;
								break;
						}
					}
					
					rc.broadcast(0, current[bots]);
					Utilities.SpawnSoldiers(rc);
					bots++;
					if(current[bots] == GOLIATH)
					{
						goliaths++;
					}
					if(goliaths >= NUMGOLIATH)
					{
						rc.broadcast(3, 5);
						goliaths = 0;
					}
				}
			}
			catch(Exception e){}
		}
	}
}
