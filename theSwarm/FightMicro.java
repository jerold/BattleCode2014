package theSwarm;

import battlecode.common.*;

public class FightMicro 
{
	static final int StartEnemyChannel = 20000;
	static final int StartEnemyNoiseTower = 20025;
	static final int StartOurBotChannel = 21000;
	static final int StartOurNoiseTower = 21025;
	
	public static int[] AllEnemyBots(RobotController rc)
	{
		int[] allEnemies = new int[25];
		try
		{
			int index = StartEnemyChannel;
			int currentInfo = rc.readBroadcast(index);
			int arrayIndex = 0;
			
			// basically we gather all of the information for the bots and put it into an array
			while (currentInfo != 0 && (index-StartEnemyChannel) < 26)
			{
				allEnemies[arrayIndex] = currentInfo;
				index++;
				arrayIndex++;
				currentInfo = rc.readBroadcast(index);
			}
		} catch (Exception e) {
		}
		
		return allEnemies;
	}

	public static int[] AllEnemyNoiseTowers(RobotController rc)
	{
		int[] enemyNoiseTowers = new int[10];
		
		try
		{
			int index = StartEnemyNoiseTower;
			int currentInfo = rc.readBroadcast(index);
			int arrayIndex = 0;
			
			// basically we gather all of the information for the bots and put it into an array
			while (currentInfo != 0 && (index-StartEnemyNoiseTower) < 11)
			{
				enemyNoiseTowers[arrayIndex] = currentInfo;
				index++;
				arrayIndex++;
				currentInfo = rc.readBroadcast(index);
			}
			
		} catch (Exception e) {
			
		}
		
		return enemyNoiseTowers;
	}

	public static int CreateBotInfo(RobotController rc, Robot bot)
	{
		int info = -1;
		int helper = 0;
		
		try
		{
			if (rc.canSenseObject(bot))
			{
				info += bot.getID() * 1000000;
				helper =(int) (rc.senseRobotInfo(bot).health/10);
				helper *= 10000;
				info += helper;
				info += Movement.convertMapLocationToInt(rc.senseRobotInfo(bot).location);
			}
		} catch (Exception e) {}
		
		return info;
	}
}
