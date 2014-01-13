package hqtowerbuild;

import battlecode.common.*;

/*
 * This class will keep track of milk and pastures of both teams and give
 * the next strategy.
 */
public class Analysis
{
	RobotController rc;
	boolean first;
	
	public Analysis(RobotController rc)
	{
		this.rc = rc;
		first = true;
	}
	
	//chooses next strategy to implement.
	public void analyze()
	{
	}
	
	public int[] getStrat()
	{
		//analyze();
		if(first)
		{
			first = false;
			int[] strategy = {SmartHQ.TOWER, SmartHQ.MULE};
			return strategy;
		}
		else
		{
			int[] strategy = {SmartHQ.DURAN, SmartHQ.GHOST};
			return strategy;
		}
	}
}
