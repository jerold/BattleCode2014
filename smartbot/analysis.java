package smartbot;

import battlecode.common.*;

/*
 * This class will keep track of milk and pastures of both teams and give a suggestion
 * of the next strategy.
 */
public class analysis
{
	RobotController rc;
	int checkFrequency = 100;
	int[] milkY, milkO, pastrY, pastrO;
	
	public analysis(RobotController rc)
	{
		this.rc = rc;
		milkY = new int[GameConstants.ROUND_MAX_LIMIT / checkFrequency];
		milkO = new int[GameConstants.ROUND_MAX_LIMIT / checkFrequency];
		pastrY = new int[GameConstants.ROUND_MAX_LIMIT / checkFrequency];
		pastrO = new int[GameConstants.ROUND_MAX_LIMIT / checkFrequency];
	}
	
	/*
	 * Each int is a different suggestion
	 * 1: attack pastures
	 * 2: defend pastures
	 * 3: attack other soldiers
	 * 4: swarm other hq
	 * 5: build pastures
	 */
	public int analyze()
	{
		milkY[Clock.getRoundNum() / 100] = (int)rc.senseTeamMilkQuantity(rc.getTeam());
		milkO[Clock.getRoundNum() / 100] = (int)rc.senseTeamMilkQuantity(rc.getTeam().opponent());
		pastrY[Clock.getRoundNum() / 100] = rc.sensePastrLocations(rc.getTeam()).length;
		pastrO[Clock.getRoundNum() / 100] = rc.sensePastrLocations(rc.getTeam().opponent()).length;
		
		return 0;
	}
}
