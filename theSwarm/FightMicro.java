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

    // These channels are for broadcasting information
    static final int engagingEnemy = 10000;
    static final int engagingEnemy2 = 10001;
    static final int engagingEnemy3 = 10002;

    //static final long IDOffset = 100000;
    //static final long HealthOffset = 10000;
    //static final long ActionDelayOffset = 10000;

    //========================================================================================================================================================\\
    //
    /////////////////////////////////////////////  The following methods deal with broadcasting information about visible bots \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    //
    //========================================================================================================================================================\\

    /**
     * This function reads all of the enemy bots off the messaging board and returns them as an array of ints
     */
	public static int[] AllEnemyBots(RobotController rc)
	{
		int[] allEnemies = new int[25];
		try
		{
			int index = StartEnemyChannel;
			int currentInfo = rc.readBroadcast(index);
			//valueSet = ConvertBitsToInts(currentInfo);
			int arrayIndex = 0;
			
			// basically we gather all of the information for the bots and put it into an array
			while (currentInfo != 0 && (index-StartEnemyChannel) < 26)
			{
				allEnemies[arrayIndex] = currentInfo;
				index++;
				arrayIndex++;
				currentInfo = rc.readBroadcast(index);
				//valueSet = ConvertBitsToInts(currentInfo);	
			}
		} catch (Exception e) {}
		
		return allEnemies;
	}

    /**
     * This function reads all of the enemy Noise Towers off the messaging board and returns them as an array of ints
     */
	public static int[] AllEnemyNoiseTowers(RobotController rc)
	{
		int[] enemyNoiseTowers = new int[10];
		
		try
		{
			int index = StartEnemyNoiseTower;
			int currentInfo = rc.readBroadcast(index);
			int arrayIndex = 0;
			//long data = ConvertBitsToInts(currentInfo);
			
			// basically we gather all of the information for the bots and put it into an array
			while (currentInfo != 0 && (index-StartEnemyNoiseTower) < 11)
			{
				enemyNoiseTowers[arrayIndex] = currentInfo;
				index++;
				arrayIndex++;
				currentInfo = rc.readBroadcast(index);
				//data = ConvertBitsToInts(currentInfo);
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
				health /= 100;
                health *= 8;
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
    public static void recordAEnemyBot(RobotController rc, int[] AllEnemyBots, int outBot)
    {
        try
        {
            //long botID = outBot / IDOffset;
        	int botID = getBotID(outBot);
            boolean foundInList = false;
            int index = 0;

            while (AllEnemyBots[index] != 0 && index < AllEnemyBots.length && !foundInList)
            {
                    if ((getBotID(AllEnemyBots[index])) == botID)
                    {
                        rc.broadcast((index+StartEnemyChannel), outBot); //ConvertLongToBits(outBot));
                        foundInList = true;
                    }
                    index++;  
            }

            if (!foundInList)
            {
                rc.broadcast(index+StartEnemyChannel, outBot);//ConvertLongToBits(outBot));
            }


        } catch(Exception e) {}
    }

    /**
     * This function takes in information about an enemy Noise Tower and records it to the messaging board if it is
     * new information
     */
    public static void recordEnemyNoiseTower(RobotController rc, int[] AllEnemyNoiseTowers, int noiseTower)
    {
        try
        {
            int botID =  getBotID(noiseTower); //noiseTower % IDOffset;
            boolean foundInList = false;
            int index = 0;

            while (AllEnemyNoiseTowers[index] != 0 && index < AllEnemyNoiseTowers.length)
            {
                
                if (getBotID(AllEnemyNoiseTowers[index]) == botID)
                {
                    rc.broadcast((index+StartEnemyNoiseTower), noiseTower);//ConvertLongToBits(noiseTower));
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
                rc.broadcast(index+StartEnemyNoiseTower, noiseTower);//ConvertLongToBits(noiseTower));
            }

        } catch (Exception e) {}
    }

    /**
     * This function takes all of the game objects from the enemy that a robot can see
     * and posts information about them to the messaging board
     */
    public static void FindAndRecordAllEnemies(RobotController rc, Robot[] enemyRobots, int[] AllEnemyBots, int[] AllEnemyTowers)
    {
        try
        {
            int info;
            int bitInfo;
            for (int i = 0; i < enemyRobots.length; i++)
            {
                if (rc.canSenseObject(enemyRobots[i]))
                {
                    bitInfo = CreateBotInfo(rc, enemyRobots[i]);
                    info = bitInfo;//ConvertBitsToInts(bitInfo);
                    if (rc.senseRobotInfo(enemyRobots[i]).type == RobotType.SOLDIER)
                    {
                        recordAEnemyBot(rc, AllEnemyBots, info);
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
    public static void recordEnemyBotKilled(RobotController rc, int[] enemyRobots, Robot bot)
    {
        int info = CreateBotInfo(rc, bot);
        int id = getBotID(info); //ConvertBitsToInts(info) / IDOffset;

        for (int i = 0; i < enemyRobots.length; i++)
        {
            if (id == getBotID(enemyRobots[i]))
            {
                ShiftEnemyBotsArray(rc, enemyRobots, i);
            }
        }
    }

    /**
     * This function updates the message board when we kill a noise tower
     */
    public static void recordEnemyNoiseTowerKilled(RobotController rc, int[] enemyNoiseTowers, Robot bot)
    {
        int info = CreateBotInfo(rc, bot);
        int  id = getBotID(info);

        for (int i = 0; i < enemyNoiseTowers.length; i++)
        {
            if (id == getBotID(enemyNoiseTowers[i]))
            {
                ShiftEnemyNoiseTowersArray(rc, enemyNoiseTowers, i);
            }
        }
    }

    /**
     * This function takes a location to erase from the array of enemy robots and shifts everything over it
     */
    public static void ShiftEnemyBotsArray(RobotController rc, int[] enemyRobots, int index)
    {
        try
        {
            for (int i = index; i < (enemyRobots.length+1); i++)
            {
                rc.broadcast(i+StartEnemyChannel, enemyRobots[i+1]); //ConvertLongToBits(enemyRobots[i+1]));
            }

            rc.broadcast(25+StartEnemyChannel, 0);

        } catch (Exception e) {}
    }

    /**
     * This function shifts the location of noise towers in the array when we kill an enemy noise tower
     */
    public static void ShiftEnemyNoiseTowersArray(RobotController rc, int[] enemyRobots, int index)
    {
        try
        {
            for (int i = index; i < (enemyRobots.length+1); i++)
            {
                rc.broadcast(i+StartEnemyNoiseTower, enemyRobots[i+1]); //ConvertLongToBits(enemyRobots[i+1]));
            }

            rc.broadcast(25+StartEnemyNoiseTower, 0);

        } catch (Exception e) {}
    }

    /**
     * This function returns the number of Enemies we can see on the board at any given time
     */
    public static int NumbOfKnownEnemyBots(int[] enemyRobots)
    {
        int index = 0;
        int i = 0;

        while ((index < enemyRobots.length) && ( enemyRobots[index]  != 0))
        {
            index++;

        }

        return index;
    }

    /**
     * This method takes all of the information about a bot and packs it into an integer using bits
     */
    public static int ConvertBotInfoToBits(int id, int health, int x, int y, int actionDelay)
    {
    	int combo = 0;

    	combo |= ((id                                   << 19)  & 0xFFF80000);  // ID
        combo |= (((health * 7 / 100)                   << 16)  & 0x00070000);  // Health
        combo |= (((actionDelay > 3 ? 3 : actionDelay)  << 14)  & 0x0000D000);  // Action Delay
        combo |= ((x                                    << 7)   & 0x00003F80);  // X
        combo |= ((y                                    << 0)   & 0x0000007F);  // Y
    	
    	
    	return combo;
    }
    
    public static int getBotID(int combo)
    {
    	int data = 0;
    	data = (combo & 0xfff80000) >> 19;
        
        if (data < 0)
        {
        	data += 4096;
        }
    	return data;
    }

    public static int getBotHealth(int combo)
    {
        int data = 0;

        data = (combo & 0x00070000) >> 16;
        data = data * 100;
        data = data / 7;

        return data;
    }

    public static MapLocation getBotLocation(int combo)
    {
        MapLocation loc;

        int x = (combo & 0x00003F80) >> 7;
        int y = (combo & 0x0000007F) >> 0;

        loc = new MapLocation(x, y);

        return loc;
    }

    public static int getActionDelay(int combo)
    {
        int data;

        data = (combo & 0x0000D000) >> 14;

        return data;
    }

    /**
     * This method takes an int of bits and converts it into a long of digits
     */
    /*
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
    */

    /**
     * This method takes a long packed with various information about bots and converts it into an integer of bits that can be broadcasted
     */
    /*
    public static int ConvertLongToBits(long data)
    {
    	int id = (int) (data / IDOffset);
    	int health = (int) (data % IDOffset);
    	health /= health/HealthOffset;
    	int x = (int) (data % IDOffset);
    	x = x % (int) HealthOffset;
    	x = x % (int) ActionDelayOffset;
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
    */

    //////// These methods deal with posting information about our bot

    /**
     * This method returns an array of all Allied Robots
     */

    public static int[] AllAlliedBotsInfo(RobotController rc)
    {
        int[] allAllies = new int[25];
        try
        {
            int index = StartOurBotChannel;
            int currentInfo = rc.readBroadcast(index);
            int arrayIndex = 0;

            // basically we gather all of the information for the bots and put it into an array
            while (currentInfo != 0 && (index-StartOurBotChannel) < 26)
            {
                allAllies[arrayIndex] = currentInfo;
                index++;
                arrayIndex++;
                currentInfo = rc.readBroadcast(index);
            }
        } catch (Exception e) {}

        return allAllies;
    }

    /**
     * This method is designed for a bot to call once and then use continually to update his personal information
     */
    public static int ourSlotInMessaging(RobotController rc)
    {
        int index = 0;

        try
        {
            while (rc.readBroadcast(index+StartOurBotChannel) != 0)
            {
                index++;
            }

        } catch (Exception e) {}

        return index;
    }

    /**
     * This method looks through all allied bots and goes until it either sees us or it gets to the end of the list
     */
    public static int ourSlotInMessaging2(RobotController rc, int[] AllAlliedBots)
    {
        int index = 0;

        try
        {
            int ourID = rc.getRobot().getID();
            int alliedID = (int) (getBotID(AllAlliedBots[0]));

            while (ourID != alliedID && alliedID != 0)
            {
                index++;
                alliedID = (int) (getBotID(AllAlliedBots[index]));
            }

        } catch (Exception e) {}

        return index;
    }

    /**
     * This method takes us out of the channel and moves all of our allies up
     */
    public static void removeOurSelvesFromBoard(RobotController rc, int[] AllAlliedBots, int index)
    {
        try
        {
            for (int i = index; i < (AllAlliedBots.length + 1); i++)
            {
                rc.broadcast(i+StartOurBotChannel, AllAlliedBots[i+1]); //ConvertLongToBits(AllAlliedBots[i+1]));

                if (AllAlliedBots[i+1] == 0)
                {
                    i = AllAlliedBots.length;
                }
            }

        } catch (Exception e) {}
    }

    /**
     * This method posts our information to the wall
     */
    public static void PostOurInfoToWall(RobotController rc, int index)
    {
        int ourBitInfo = ConvertBotInfoToBits(rc.getRobot().getID(), (int)rc.getHealth(), rc.getLocation().x, rc.getLocation().y, (int)rc.getActionDelay());
        try
        {
            rc.broadcast(index+StartOurBotChannel, ourBitInfo);
        } catch (Exception e) {}
    }

    public static int NumbOfAllies(int[] AllAlliedBots)
    {
        int numb = 0;

        while (numb < AllAlliedBots.length && AllAlliedBots[numb] != 0)
        {
            numb++;
        }

        return numb;
    }



    //==================================================================================================\\
    //
    /////////////// These methods manage our actual fighting based on the broadcasted information \\\\\\\\\
    //
    //==================================================================================================\\

    public static boolean offensiveFightMicro(RobotController rc, Robot[] seenEnemies, boolean inTransit, int[] AllEnemyBots, int[] AllAlliedBots)
    {
        // we will move towards our target if there are no visible enemies
        if (inTransit)
        {
            // if we are in transit then we will fight if we can see enemy soldiers
            if (seenEnemies.length > 0)
            {
                // now we want to attack if we have more troops
                if (numbOfNearByBots(rc, AllAlliedBots) > numbOfNearByBots(rc, AllEnemyBots))
                {

                }
                // otherwise we will retreat to strong position hopefully getting the enemy to overextend his reach and get destroyed
                else
                {

                }
            }

            return false;
        }
        else
        {

        }

        return false;
    }

    /**
     * This method returns the number of enemies with a squared distance of 50 that we know of
     */
    public static int numbOfNearByBots(RobotController rc, int[] AllBots)
    {
        int numb = 0;

        for (int i = 0; i < AllBots.length; i++)
        {
            if (rc.getLocation().distanceSquaredTo(getBotLocation(AllBots[i])) <= 50)
            {
                numb++;
            }
        }

        return numb;
    }

    /**
     * This method finds the number of soldiers at a specific distance
     */
    public static Robot[] findSoldiersAtDistance(RobotController rc, Robot[] gameObjects, int distance)
    {
        try
        {
            int[] index = new int[gameObjects.length];
            int numb = 0;

            for (int i = 0; i < gameObjects.length; i++)
            {
                if (rc.getLocation().distanceSquaredTo(rc.senseLocationOf(gameObjects[i])) <= distance)
                {
                    index[i] = 1;
                    numb++;
                }
                else
                {
                    index[i] = 0;
                }
            }
            Robot[] soldiers = new Robot[numb];
            int k = 0;

            for (int j = 0; j < gameObjects.length; j++)
            {
                if (index[j] == 1)
                {
                    soldiers[k] = gameObjects[j];
                    k++;
                }
            }

            return soldiers;

        } catch (Exception e)
        {
            // tell the console we through an exception in utility object for debug purposes
            e.printStackTrace();
        }
        return null;
    }

    /**
     * This method finds the number of soldiers in the array of objects we have seen
     */
    public static Robot[] findSoldiers(RobotController rc, Robot[] gameObjects)
    {
        Robot[] emptySet = null;
        try
        {
            int numbOfSoldiers = 0;
            int[] index = new int[gameObjects.length];
            for (int i = 0; i < gameObjects.length; i++)
            {
                if (rc.senseRobotInfo(gameObjects[i]).type == RobotType.SOLDIER)
                {
                    numbOfSoldiers++;
                    index[i] = 1;
                }
                else
                {
                    index[i] = 0;
                }
            }
            Robot[] soldiers = new Robot[numbOfSoldiers];
            int k = 0;
            for (int j = 0; j < gameObjects.length; j++)
            {
                if (index[j] == 1)
                {
                    soldiers[k] = gameObjects[j];
                    k++;
                }
            }
            return soldiers;

        }  catch (Exception e)
        {
            // tell the console we through an exception in utility object for debug purposes
            e.printStackTrace();
        }
        return null;
    }

    /**
     * This method finds the number of non soldiers in an array of objects we have seen
     */
    public static Robot[] findNonSoldiers(RobotController rc, Robot[] gameObjects)
    {
        Robot[] emptySet = null;
        try
        {
            int numbOfSoldiers = 0;
            int[] index = new int[gameObjects.length];
            for (int i = 0; i < gameObjects.length; i++)
            {
                if (rc.senseRobotInfo(gameObjects[i]).type != RobotType.SOLDIER && rc.senseRobotInfo(gameObjects[i]).type != RobotType.HQ)
                {
                    numbOfSoldiers++;
                    index[i] = 1;
                }
                else
                {
                    index[i] = 0;
                }
            }
            Robot[] soldiers = new Robot[numbOfSoldiers];
            int k = 0;
            for (int j = 0; j < gameObjects.length; j++)
            {
                if (index[j] == 1)
                {
                    soldiers[k] = gameObjects[j];
                    k++;
                }
            }
            return soldiers;

        }  catch (Exception e)
        {
            // tell the console we through an exception in utility object for debug purposes
            e.printStackTrace();
        }
        return null;
    }

    /**
     * This function finds the location that we can move to in range of an enemy that is in range
     * of the fewest enemy soldiers
     *
     * Note this method is intended to be called by a soldier who is responding to a message that an ally is in combat
     */
    public static MapLocation bestSupportAdvanceSpot(RobotController rc, int[] enemySoldiers, MapLocation target, int[] allAllies)
    {
        MapLocation loc = rc.getLocation();
        Direction direction = rc.getLocation().directionTo(target);
        loc = loc.add(direction);

        try
        {
            loc = loc.add(direction);
            // in this case we are in range of enemy
            if (rc.getLocation().distanceSquaredTo(target) < 11)
            {
                // if the only enemy soldier in range of the space is the one we are targeting
                if (numbOfEnemiesInRange(rc, enemySoldiers, loc) == 1)
                {
                    // then we fire! and overwhelming the enemy with our superior numbers
                    Movement.fire(rc);
                }
            }
            // In this case we will need to move at least once to get to our target location
            else if (rc.getLocation().distanceSquaredTo(target) < 24)
            {
                MapLocation[] options = mapLocationsWithinDistance(rc, target, 10);

                int fewestNumbOfSoldiers = 25;
                int numbOfSoldiers;
                MapLocation moveSpot = null;
                MapLocation tie = null;
                MapLocation tie2 = null;

                // we now find the location in range of the fewest enemies
                // and if there are multiple locations with the same number of enemies then
                for (int i = 0; i < options.length; i++)
                {
                    numbOfSoldiers = numbOfEnemiesInRange(rc, enemySoldiers, options[i]);
                    if (numbOfSoldiers < fewestNumbOfSoldiers)
                    {
                        fewestNumbOfSoldiers = numbOfSoldiers;
                        moveSpot = options[i];
                        tie = tie2 = null;
                    }
                    else if (numbOfSoldiers == fewestNumbOfSoldiers && tie == null)
                    {
                        tie = options[i];
                    }
                    else if (numbOfSoldiers == fewestNumbOfSoldiers)
                    {
                        tie2 = options[i];
                    }
                }

                // first we need to check if there was a tie
                if (tie != null)
                {
                    boolean tieBlocking;
                    boolean tie2Blocking;
                    boolean firstBlocking;
                    // if there is a tie then we should pick the location which doesn't block close allies
                    // and if that isn't a problem then should move where it is furthest out of range
                    // of upcomming bots
                    if (tie2 != null)
                    {

                    }
                    else
                    {
                         tieBlocking = positionBlockingAllies(rc, tie, allAllies);
                         firstBlocking = positionBlockingAllies(rc, tie, allAllies);
                        // if either position is blocking allies then we go to the other one
                        if (tieBlocking && !firstBlocking)
                        {
                            // we leave moveSpot alone as that is where we want to go
                        }
                        else if (firstBlocking && !tieBlocking)
                        {
                            moveSpot = tie;
                        }
                        else if (numbOfEnemiesJustOutsideOfRange(rc, tie, enemySoldiers) < numbOfEnemiesJustOutsideOfRange(rc, moveSpot, enemySoldiers))
                        {
                            moveSpot = tie;
                        }
                    }
                }

                // now we move to target location assuming it is good
                // if their is a spot we can move towards
                if (moveSpot != null)
                {
                    
                }

            }
            // In this case we will need to move at least twice to get to our target location
            else if (rc.getLocation().distanceSquaredTo(target) < 37)
            {

            }
            // In this case we will need to move multiple times to get to our target location
            else
            {

            }

        } catch (Exception e) {}

        return loc;
    }

    /**
     * This function returns the number of enemies who can shoot at a target space
     */
    public static int numbOfEnemiesInRange(RobotController rc, int[] enemySoldiers, MapLocation location)
    {
        int numb = 0;

        if (enemySoldiers.length > 0)
        {
            for (int i = 0; i < enemySoldiers.length; i++)
            {
                if (getBotLocation(enemySoldiers[i]).distanceSquaredTo(location) <= 10)
                {
                    numb++;
                }
            }
        }

        return numb;
    }

    /**
     * This function returns all MapLocations adjacent to us
     * within a specified distance of another location
     */
    public static MapLocation[] mapLocationsWithinDistance(RobotController rc, MapLocation target, int distance)
    {
        MapLocation[] spots = null;

        Direction dir = rc.getLocation().directionTo(target);
        int[] dirs = new int[8];
        int numbOfDirs = 0;

        // here we find the number of directions that we can move and be within the target
        for (int i = 0; i < 8; i++)
        {
            if (rc.getLocation().add(dir).distanceSquaredTo(target) <= distance)
            {
                dirs[i] = 1;
                numbOfDirs++;
            }
            else
            {
                dirs[i] = 0;
            }
            dir = dir.rotateRight();
        }

        dir = rc.getLocation().directionTo(target);
        spots = new MapLocation[numbOfDirs];
        int index = 0;

        for (int j = 0; j < 8; j++)
        {
            if (dirs[j] == 1)
            {
                spots[index] = rc.getLocation().add(dir);
            }
            dir = dir.rotateRight();
        }

        return spots;
    }

    /**
     * This method looks to see if their are any allies adjacent to a location to see if moving there would
     * prevent them from advancing
     */
    public static boolean positionBlockingAllies(RobotController rc, MapLocation target, int[] AllAllies)
    {
        for (int i = 0; i < AllAllies.length; i++)
        {
            if (getBotLocation(AllAllies[i]).distanceSquaredTo(target) < 4)
            {
                if (getBotID(AllAllies[i]) != rc.getRobot().getID())
                {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * This method returns the number of enemy soldiers that are within one turn of being in range
     * of a target location
     */
    public static int numbOfEnemiesJustOutsideOfRange(RobotController rc, MapLocation target, int[] AllEnemies)
    {
        int numb = 0;

        for (int i = 0; i < AllEnemies.length; i++)
        {
            if (getBotLocation(AllEnemies[i]).distanceSquaredTo(target) < 24)
            {
                numb++;
            }
        }

        return numb;
    }

    /**
     * This method returns the number of enemy soldiers that can only hit us
     */
    public static int numbOfEnemiesOnlyInRangeOfUs(RobotController rc, int[] enemySoldiers, int[] AllAllies)
    {
        int numb = 0;
        int numb2;

        for (int i = 0; i < enemySoldiers.length; i++)
        {
            MapLocation enemySpot = getBotLocation(enemySoldiers[i]);
            numb2 = 0;

            if (rc.getLocation().distanceSquaredTo(enemySpot) <= 10)
            {
                for (int j = 0; j < AllAllies.length; j++)
                {
                    if (getBotLocation(AllAllies[j]).distanceSquaredTo(enemySpot) <= 10)
                    {
                        j = AllAllies.length;
                        numb2 = 1;
                    }
                }

                // if we didn't see any allies in range of that enemy then we add him to our total
                if (numb2 == 0)
                {
                    numb++;
                }
            }
        }

        return numb;
    }
}
