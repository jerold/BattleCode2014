package theSwarm;

import battlecode.common.*;
import battlecode.common.Robot;

public class FightMicro 
{
    // These are broadcasting channels for information about enemy bots
	static final int StartEnemyChannel = 20000;
	static final int StartEnemyNoiseTower = 20025;
	static final int StartOurBotChannel = 21000;
	static final int StartOurNoiseTower = 21025;

    static final long IDOffset = 1000000;
    static final long HealthOffset = 100000;
    static final long ActionDelayOffset = 10000;

    /**
     * This function reads all of the enemy bots off the messaging board and returns them as an array of ints
     */
	public static long[] AllEnemyBots(RobotController rc)
	{
		long[] allEnemies = new long[25];
		try
		{
			int index = StartEnemyChannel;
			int currentInfo = rc.readBroadcast(index);
			long valueSet = 0;
			valueSet = ConvertBitsToInts(currentInfo);
			int arrayIndex = 0;
			
			// basically we gather all of the information for the bots and put it into an array
			while (currentInfo != 0 && (index-StartEnemyChannel) < 26)
			{
				allEnemies[arrayIndex] = valueSet;
				index++;
				arrayIndex++;
				currentInfo = rc.readBroadcast(index);
				valueSet = ConvertBitsToInts(currentInfo);	
			}
		} catch (Exception e) {}
		
		return allEnemies;
	}

    /**
     * This function reads all of the enemy Noise Towers off the messaging board and returns them as an array of ints
     */
	public static long[] AllEnemyNoiseTowers(RobotController rc)
	{
		long[] enemyNoiseTowers = new long[10];
		
		try
		{
			int index = StartEnemyNoiseTower;
			int currentInfo = rc.readBroadcast(index);
			int arrayIndex = 0;
			long data = ConvertBitsToInts(currentInfo);
			
			// basically we gather all of the information for the bots and put it into an array
			while (currentInfo != 0 && (index-StartEnemyNoiseTower) < 11)
			{
				enemyNoiseTowers[arrayIndex] = data;
				index++;
				arrayIndex++;
				currentInfo = rc.readBroadcast(index);
				data = ConvertBitsToInts(currentInfo);
			}
			
		} catch (Exception e) {
			
		}
		
		return enemyNoiseTowers;
	}

    /**
     * This function takes in a bot and returns its information packed into an int that can be broadcasted
     */
	public static int CreateBotInfo(RobotController rc, Robot bot)
	{
		int info = 0;
		int helper = 0;
		
		try
		{
			if (rc.canSenseObject(bot))
			{
				double health = rc.senseRobotInfo(bot).health;
				MapLocation loc = rc.senseRobotInfo(bot).location;
				int health2;
				if (health > 70)
				{
					health = 0;
				}
				else if (health < 10)
				{
					health = 10;
				}
				health /= 10;
				health2 = (int) health;
				info = ConvertBotInfoToBits(bot.getID(), health2, loc.x, loc.y, ((int)rc.senseRobotInfo(bot).actionDelay));
				/*info += bot.getID() * IDOffset;
				helper =(int) (rc.senseRobotInfo(bot).health/10);
				if (helper < 1)
				{
					helper = 1;
				}
				if (helper == 10)
				{
					helper = 0;
				}
				helper *= HealthOffset;
				info += helper;
				info += Movement.convertMapLocationToInt(rc.senseRobotInfo(bot).location);
				*/
			}
		} catch (Exception e) {}
		
		return info;
	}

    /**
     * This function takes in the information for an enemy bot and checks it to what is on the messaging board and updates
     * It if necessary
     */
    public static void recordAEnemyBot(RobotController rc, long[] AllEnemyBots, long outBot)
    {
        try
        {
            long botID = outBot / IDOffset;
            boolean foundInList = false;
            int index = 0;

            while (AllEnemyBots[index] != 0 && index < AllEnemyBots.length)
            {
                    if ((AllEnemyBots[index] / IDOffset) == botID)
                    {
                        rc.broadcast((index+StartEnemyChannel), ConvertLongToBits(outBot));
                        foundInList = true;
                    }
                    index++;  
            }

            if (!foundInList)
            {
                rc.broadcast(index+StartEnemyChannel, ConvertLongToBits(outBot));
            }


        } catch(Exception e) {}
    }

    /**
     * This function takes in information about an enemy Noise Tower and records it to the messaging board if it is
     * new information
     */
    public static void recordEnemyNoiseTower(RobotController rc, long[] AllEnemyNoiseTowers, int noiseTower)
    {
        try
        {
            long botID =  ConvertBitsToInts(noiseTower) % IDOffset;
            boolean foundInList = false;
            int index = 0;

            while (AllEnemyNoiseTowers[index] != 0 && index < AllEnemyNoiseTowers.length)
            {
                
                if ((AllEnemyNoiseTowers[index] % IDOffset) == botID)
                {
                    rc.broadcast((index+StartEnemyNoiseTower), noiseTower);
                    foundInList = true;
                }
                else if (AllEnemyNoiseTowers[index] == 0)
                {
                    index = AllEnemyNoiseTowers.length;
                }
                index++;
            }

            if (!foundInList)
            {
                rc.broadcast(index+StartEnemyNoiseTower, noiseTower);
            }

        } catch (Exception e) {}
    }

    /**
     * This function takes all of the game objects from the enemy that a robot can see
     * and posts information about them to the messaging board
     */
    public static void FindAndRecordAllEnemies(RobotController rc, Robot[] enemyRobots, long[] AllEnemyBots, long[] AllEnemyTowers)
    {
        try
        {
            int info;
            for (int i = 0; i < enemyRobots.length; i++)
            {
                if (rc.canSenseObject(enemyRobots[i]))
                {
                    info = CreateBotInfo(rc, enemyRobots[i]);
                    if (rc.senseRobotInfo(enemyRobots[i]).type == RobotType.SOLDIER)
                    {
                        //recordAEnemyBot(rc, AllEnemyBots, info);
                    }
                    else if (rc.senseRobotInfo(enemyRobots[i]).type == RobotType.NOISETOWER)
                    {
                        recordEnemyNoiseTower(rc, AllEnemyTowers, info);
                    }
                }
            }
        } catch (Exception e) {}
    }

    /**
     * This function updates the board when we have killed an enemy bot as it will never appear again
     */
    public static void recordEnemyBotKilled(RobotController rc, long[] enemyRobots, Robot bot)
    {
        long info = CreateBotInfo(rc, bot);
        long id = info / IDOffset;

        for (int i = 0; i < enemyRobots.length; i++)
        {
            if (id == enemyRobots[i]/IDOffset)
            {
                ShiftEnemyBotsArray(rc, enemyRobots, i);
            }
        }
    }

    /**
     * This function updates the message board when we kill a noise tower
     */
    public static void recordEnemyNoiseTowerKilled(RobotController rc, long[] enemyNoiseTowers, Robot bot)
    {
        long info = CreateBotInfo(rc, bot);
        long  id = info / IDOffset;

        for (int i = 0; i < enemyNoiseTowers.length; i++)
        {
            if (id == enemyNoiseTowers[i]/IDOffset)
            {
                ShiftEnemyNoiseTowersArray(rc, enemyNoiseTowers, i);
            }
        }
    }

    /**
     * This function takes a location to erase from the array of enemy robots and shifts everything over it
     */
    public static void ShiftEnemyBotsArray(RobotController rc, long[] enemyRobots, int index)
    {
        try
        {
            for (int i = index; i < (enemyRobots.length+1); i++)
            {
                rc.broadcast(i+StartEnemyChannel, ConvertLongToBits(enemyRobots[i+1]));
            }

            rc.broadcast(25+StartEnemyChannel, 0);

        } catch (Exception e) {}
    }

    /**
     * This function shifts the location of noise towers in the array when we kill an enemy noise tower
     */
    public static void ShiftEnemyNoiseTowersArray(RobotController rc, long[] enemyRobots, int index)
    {
        try
        {
            for (int i = index; i < (enemyRobots.length+1); i++)
            {
                rc.broadcast(i+StartEnemyNoiseTower, ConvertLongToBits(enemyRobots[i+1]));
            }

            rc.broadcast(25+StartEnemyNoiseTower, 0);

        } catch (Exception e) {}
    }

    /**
     * This function returns the number of Enemies we can see on the board at any given time
     */
    public static int NumbOfKnownEnemyBots(RobotController rc, long[] enemyRobots)
    {
        int index = 0;
        int i = 0;

        while ( enemyRobots[i] != 0 && i < enemyRobots.length)
        {
            index++;
        }

        return index;
    }

    public static int ConvertBotInfoToBits(int id, int health, int x, int y, int actionDelay)
    {
    	int combo;
    	
    	combo = (id << 20) | (health << 17) | (actionDelay << 13) | (x << 7) | y;
    	
    	return combo;
    }
    
    public static long ConvertBitsToInts(int combo)
    {
    	long[] values = new long[5];
    	long data;
    	
    	values[0] = (combo & 0xfff00000) >> 20;
    	values[1] = (combo & 0x000e0000) >> 17;
    	values[2] = (combo & 0x0001e000) >> 13;
    	values[3] = (combo & 0x00001f80) >> 7;
    	values[4] = (combo & 0x0000003f);
    	
    	data = (values[0] * IDOffset) + (values[1] * HealthOffset) + (values[2] * ActionDelayOffset) + (values[3] * 100) + values[4];
    	
    	return data;
    }

    public static int ConvertLongToBits(long data)
    {
    	
    	int id = (int) (data / IDOffset);
    	int health = (int) (data % IDOffset);
    	health /= health/HealthOffset;
    	int x = (int) (data % IDOffset);
    	x = x% (int) HealthOffset;
    	x = x% (int) ActionDelayOffset;
    	x /= 100;
    	int y = (int) (data % IDOffset);
    	y = y % (int) HealthOffset;
    	y = y% (int)ActionDelayOffset;
    	y = y % 100;
    	int actionDelay = (int) (data % IDOffset);
    	actionDelay = actionDelay % ((int)HealthOffset);
    	actionDelay /= (int) ActionDelayOffset;
    	
    	return ConvertBotInfoToBits(id, health, x, y, actionDelay);
    }
}
