package hqtowerbuild;

import battlecode.common.*;

public class SmartHQ
{
	private RobotController rc;
	public static final int MULE = 1;
	public static final int TOWER = 2;
	public static final int DURAN = 3;
	public static final int GHOST = 4;
	
	private int[] initial = {TOWER, MULE};
	private int[] next = {DURAN, GHOST};
	
	private int[] currentStrat;
	private int bots;
	
	public SmartHQ(RobotController rc)
	{
		this.rc = rc;
		bots = 0;
		currentStrat = initial;
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
						bots = 0;
						currentStrat = next;
					}
					else
					{
						rc.broadcast(0, currentStrat[bots]);
						Utilities.SpawnSoldiers(rc);
						bots++;
					}
				}
			}
			catch(Exception e){}
		}
	}
}
