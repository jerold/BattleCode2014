package towerbot;

import battlecode.common.*;

/*
 * This class will keep track of milk and pastures of both teams and give
 * the next strategy.
 */
public class Analysis
{
	RobotController rc;
	int type;
	boolean first, done;
	int[] strat;
	
	public Analysis(RobotController rc)
	{
		this.rc = rc;
		first = true;
		done = false;
		type = 2;
	}
	
	//chooses next strategy to implement.
	public void analyze()
	{
		if(type == 1)
		{
			if(first)
			{
				int[] strategy = {SmartHQ.MULE, SmartHQ.TOWER};
				strat = strategy;
				first = false;
			}
			else if(!done)
			{
				int[] strategy = {SmartHQ.OPMULE, SmartHQ.OPTOWER};
				strat = strategy;
				done = true;
			}
			else
			{
				strat = null;
			}
		}
		else if(type == 2)
		{
			if(!done)
			{
				int[] strategy = {SmartHQ.MULE, SmartHQ.TOWER};
				strat = strategy;
				done = true;
			}
			else
			{
				strat = null;
			}
		}
		else
		{
			strat = null;
		}
	}
	
	public int[] getStrat()
	{
		analyze();
		return strat;
	}
}
