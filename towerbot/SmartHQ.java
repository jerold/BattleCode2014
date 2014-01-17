package towerbot;

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
	public static final int TROLL = 7;
	private final int NUMGHOST = 2;
	private final int NUMGOLIATH = 5;
	
	private Analysis a;
	private int[] currentStrat;
	private int goliaths;
	private int bots;
	
	public SmartHQ(RobotController rc)
	{
		this.rc = rc;
		a = new Analysis(rc);
		currentStrat = a.getStrat();
		goliaths = 0;
		bots = 0;
		
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
					if(bots == currentStrat.length)
					{
						//bots = 0;
						currentStrat = a.getStrat();
					}
					else
					{
						rc.broadcast(0, currentStrat[bots]);
						Utilities.SpawnSoldiers(rc);
						bots++;
						if(currentStrat[bots] == GOLIATH)
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
                rc.yield();
			}
			catch(Exception e){}
		}
	}
}
