package towerbot;

import battlecode.common.*;

/*
 * This class will keep track of milk and pastures of both teams and give
 * the next strategy.
 */
public class Analysis
{
	RobotController rc;
	int checkFrequency = 100;
	int[] milkY, milkO, pastrY, pastrO;
	int[] strat;
	
	public Analysis(RobotController rc)
	{
		this.rc = rc;
		milkY = new int[GameConstants.ROUND_MAX_LIMIT / checkFrequency];
		milkO = new int[GameConstants.ROUND_MAX_LIMIT / checkFrequency];
		pastrY = new int[GameConstants.ROUND_MAX_LIMIT / checkFrequency];
		pastrO = new int[GameConstants.ROUND_MAX_LIMIT / checkFrequency];
	}
	
	//chooses next strategy to implement.
	public void analyze()
	{
		milkY[Clock.getRoundNum() / 100] = (int)rc.senseTeamMilkQuantity(rc.getTeam());
		milkO[Clock.getRoundNum() / 100] = (int)rc.senseTeamMilkQuantity(rc.getTeam().opponent());
		pastrY[Clock.getRoundNum() / 100] = rc.sensePastrLocations(rc.getTeam()).length;
		pastrO[Clock.getRoundNum() / 100] = rc.sensePastrLocations(rc.getTeam().opponent()).length;
	}
	
	public int[] getStrat()
	{
		//analyze();
		int[] strategy = {SmartHQ.TOWER, SmartHQ.MULE};
		return strategy;
	}
}
