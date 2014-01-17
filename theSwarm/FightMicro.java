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
    // this is if we are defending
    static final int holdingTheLine = 11000;

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

    /**
     * The idea behind this micro is that the first soldier to the battle will pick which ever flank of the enemy it considers weakest
     * and move towards it.  Then it will broadcast the location of the enemy it targets and all the other bots will head
     * towards it
     */
    public static boolean offensiveFightMicro(RobotController rc, Robot[] seenEnemies, boolean inTransit, int[] AllEnemyBots, int[] AllAlliedBots)
    {
        try
        {
            // we will move towards our target if there are no visible enemies
            if (inTransit)
            {
                // this channel has the x y coordinates of the enemy we are attacking
                int EnTaroTassadar = rc.readBroadcast(engagingEnemy);

                // we must join our brethren in the field of battle
                if (EnTaroTassadar != 0)
                {
                    int x = EnTaroTassadar / 100;
                    int y = EnTaroTassadar % 100;
                    MapLocation target = new MapLocation(x, y);

                    if (rc.getLocation().distanceSquaredTo(target) < 50)
                    {
                        Robot[] enemySoldiers = findSoldiers(rc, seenEnemies);
                        Robot[] inRangeSoldiers = findSoldiersAtDistance(rc, enemySoldiers, 10);
                        MapLocation goToSpot = bestSupportAdvanceSpot(rc, AllEnemyBots, target, AllAlliedBots);

                        // we must not tolerate the enemy breaking the bubble of our shooting range
                        if (inRangeSoldiers.length > 0)
                        {
                            // if we can move towards the target location and get out of range of the enemy
                            // then by all means do so
                            if  (numbOfEnemiesOnlyInRangeOfTarget(rc, AllEnemyBots, AllAlliedBots, goToSpot) == 0)//(numbOfEnemiesInRange(rc, AllEnemyBots, goToSpot) == 0)
                            {
                                if (rc.canMove(rc.getLocation().directionTo(goToSpot)))
                                {
                                    rc.move(rc.getLocation().directionTo(goToSpot));
                                }
                                else
                                {
                                    Movement.fire(rc);
                                }
                            }
                            // in this case if we retreat then we will take at least one enemy soldier out of the fight
                            else if (numbOfEnemiesOnlyInRangeOfUs(rc, AllEnemyBots, AllAlliedBots) > 0)
                            {
                                Direction dir;
                                dir = retreatFromBattle(rc, AllEnemyBots, AllAlliedBots);

                                if (dir != null)
                                {
                                    if (rc.canMove(dir))
                                    {
                                        rc.move(dir);
                                    }
                                }
                                // if there isn't a good location to fire then let us take our punishment as a man
                                else
                                {
                                    Movement.fire(rc);
                                }
                            }
                            else
                            {
                                Movement.fire(rc);
                            }
                            // other wise if we can move to a location where all the enemies that can shoot at us can
                            // already shoot at an opponent then no worries
                        }
                        // if there are no enemies in shooting range then we will go ahead and move towards our ally in the field
                        // of combat
                        else
                        {

                            if (rc.canMove(rc.getLocation().directionTo(goToSpot)))
                            {
                                rc.move(rc.getLocation().directionTo(goToSpot));
                            }
                            else
                            {
                                Movement.fire(rc);
                            }
                            return true;
                        }

                    }
                    else
                    {
                        return false;
                    }
                }
                // here we will see if there are any enemies in our sight range and if there are then we will react violently
                else
                {
                    if (seenEnemies.length > 0)
                    {
                        // first we will check to see if there are more of us or more of the enemy
                        
                    }
                    // if we haven't seen any enemies then we will just continue on our merry way
                    else
                    {
                        return false;
                    }
                }

                return false;
            }
            // we have an end goal to reach and will slaughter any enemies on the way but will not pursue them
            else
            {

            }
        } catch (Exception e) {}

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
                        tieBlocking = positionBlockingAllies(rc, tie, allAllies);
                        tie2Blocking = positionBlockingAllies(rc, tie2, allAllies);
                        firstBlocking = positionBlockingAllies(rc, moveSpot, allAllies);

                        if (tieBlocking && !firstBlocking && tie2Blocking)
                        {
                            // we leave moveSpot alone as that is where we want to go
                        }
                        else if (firstBlocking && !tieBlocking && tie2Blocking)
                        {
                            moveSpot = tie;
                        }
                        else if (firstBlocking && tieBlocking && !tie2Blocking)
                        {

                        }
                        else
                        {
                            int tieEnemies = numbOfEnemiesJustOutsideOfRange(rc, tie, enemySoldiers);
                            int tie2Enemies = numbOfEnemiesJustOutsideOfRange(rc, tie2, enemySoldiers);
                            int firstEnemies = numbOfEnemiesJustOutsideOfRange(rc, moveSpot, enemySoldiers);

                            if (tieEnemies > tie2Enemies)
                            {
                                if (tieEnemies > firstEnemies)
                                {
                                    moveSpot = tie;
                                }
                                else
                                {
                                    // don't need to do anything as moveSpot is correct
                                }
                            }
                            else
                            {
                                if (tie2Enemies > firstEnemies)
                                {
                                    moveSpot = tie2;
                                }
                                else
                                {
                                    // don't need to do anything as we use moveSpot
                                }
                            }
                        }
                    }
                    else
                    {
                         tieBlocking = positionBlockingAllies(rc, tie, allAllies);
                         firstBlocking = positionBlockingAllies(rc, moveSpot, allAllies);
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

                //int numbOfEnemySoldiers = numbOfEnemiesJustOutsideOfRange(rc, moveSpot, enemySoldiers);

                // now we move to target location assuming it is good
                // if their is a spot we can move towards
                if (moveSpot != null)
                {
                    return moveSpot;
                }

            }
            // In this case we will need to move at least twice to get to our target location
            else if (rc.getLocation().distanceSquaredTo(target) < 37)
            {
                MapLocation moveSpot = rc.getLocation().add(rc.getLocation().directionTo(target));

                int nearBybots = numbOfEnemiesJustOutsideOfRange(rc, moveSpot, enemySoldiers);

                // if our target location isn't in range of any new enemies then we can go there
                if (nearBybots < 1)
                {
                    return moveSpot;
                }
                else
                {
                    MapLocation[] options = mapLocationsWithinDistance(rc, target, 24);

                    int fewestEnemies = 25;

                    for (int i = 0; i < options.length; i++)
                    {
                        int numbOfEnemies = numbOfEnemiesJustOutsideOfRange(rc, options[i], enemySoldiers);

                        if (numbOfEnemies < fewestEnemies)
                        {
                            fewestEnemies = numbOfEnemies;
                            moveSpot = options[i];
                        }
                    }

                }
                return moveSpot;
            }
            // In this case we will need to move multiple times to get to our target location
            // so to save bit codes we will assume we are far enough away that we will count on the first bot to have
            // attacked in a smart way
            else
            {
                return rc.getLocation().add(rc.getLocation().directionTo(target));
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
            if ((rc.getLocation().add(dir).distanceSquaredTo(target) <= distance) && !rc.senseTerrainTile(rc.getLocation().add(dir)).equals(TerrainTile.VOID))
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

    public static int numbOfEnemiesOnlyInRangeOfTarget(RobotController rc, int[] enemySoldiers, int[] AllAllies, MapLocation target)
    {
        int numb = 0;

        for (int i = 0; i < enemySoldiers.length; i++)
        {
            MapLocation enemySpot = getBotLocation(enemySoldiers[i]);
            if (enemySpot.distanceSquaredTo(target) <= 10)
            {
                boolean AlliesInEnemyRange = false;
                for (int j =0; j < AllAllies.length; j++)
                {
                    if (getBotLocation(AllAllies[j]).distanceSquaredTo(target) <= 10)
                    {
                        AlliesInEnemyRange = true;
                        j = AllAllies.length;
                    }
                }

                if (!AlliesInEnemyRange)
                {
                    numb++;
                }
            }
        }

        return numb;
    }

    /**
     * In this method the bot will look around it to the location that can be hit by the fewest enemy soldiers
     * that it can move to.  If multiple locations have the same number of enemies targeting them then it will move to the location
     * where the closest enemy soldier is the furthest away
     */
    public static Direction retreatFromBattle(RobotController rc, int[] enemySoldiers, int[] alliedSoldiers)
    {
        Direction dir = Direction.NORTH;
        MapLocation target;
        int[] numbOfEnemies = new int[8];

        for (int i = 0; i < 8; i++)
        {
            target = rc.getLocation().add(dir);
            for (int j = 0; j < enemySoldiers.length; j++)
            {
                if (getBotLocation(enemySoldiers[j]).distanceSquaredTo(target) <= 10 && rc.canMove(rc.getLocation().directionTo(target)))
                {
                    numbOfEnemies[i]++;
                }
            }
            dir = dir.rotateRight();
        }

        int lowest = 25;
        int first = 0;
        int tie = -1;
        int tie2 = -1;

        for (int i = 0; i < 8; i++)
        {
            if (lowest > numbOfEnemies[i])
            {
                lowest = numbOfEnemies[i];
                first = i;
                tie = -1;
                tie2 = -1;
            }
            else if (lowest == numbOfEnemies[i] && tie == -1)
            {
                tie = i;
            }
            else if (lowest == numbOfEnemies[i] && tie2 == -1)
            {
                tie2 = i;
            }
        }

        Direction retreat = Direction.NORTH;

        for (int i = 0; i < first; i++)
        {
            retreat = retreat.rotateRight();
        }

        // if multiple locations were tied then we go to the spot that is closest to the nearest ally
        if (tie != -1)
        {
            int tieAllyDist = 100;
            int tieAllyDist2 = 0;
            int firstAllyDist = 100;
            int firstAllyDist2 = 0;

            Direction tieDir = Direction.NORTH;


            for (int i = 0; i < tie; i++)
            {
                tieDir = tieDir.rotateRight();
            }


            for (int i = 0; i < alliedSoldiers.length; i++)
            {
                tieAllyDist2 = (rc.getLocation().add(tieDir)).distanceSquaredTo(getBotLocation(alliedSoldiers[i]));
                firstAllyDist2 = (rc.getLocation().add(retreat)).distanceSquaredTo(getBotLocation(alliedSoldiers[i]));
                if (tieAllyDist2 < tieAllyDist)
                {
                    tieAllyDist = tieAllyDist2;
                }

                if (firstAllyDist2 < firstAllyDist)
                {
                    firstAllyDist = tieAllyDist;
                }
            }

            if (tie2 != -1)
            {
                int tie2AllyDist = 100;
                int tie2AllyDist2 = 0;

                Direction tie2Dir = Direction.NORTH;
                for (int k = 0; k < tie2; k++)
                {
                    tieDir = tieDir.rotateRight();
                }

                for (int i = 0; i < alliedSoldiers.length; i++)
                {
                    tie2AllyDist2 = (rc.getLocation().add(tie2Dir)).distanceSquaredTo(getBotLocation(alliedSoldiers[i]));

                    if (tie2AllyDist2 < tie2AllyDist)
                    {
                        tie2AllyDist = tie2AllyDist2;
                    }
                }

                if (tieAllyDist < tie2AllyDist)
                {
                    if (tieAllyDist < firstAllyDist)
                    {
                        retreat = tieDir;
                    }
                    else
                    {
                        // retreat already set correctly
                    }
                }
                else
                {
                    if (tie2AllyDist < firstAllyDist)
                    {
                        retreat = tie2Dir;
                    }
                    else
                    {
                        // retreat already properly set
                    }
                }
            }
            else
            {
                if (tieAllyDist < firstAllyDist)
                {
                    retreat = tieDir;
                }
            }
        }

        dir = retreat;

        return dir;
    }

    /**
     * In this method we pick a direction to go in our battle against the enemy
     */
    public static Direction enemyWeakSide(RobotController rc, int[] enemySoldiers)
    {
        Direction dir = Direction.NORTH;

        MapLocation centerOfEnemies = centerOfEnemyBots(rc, enemySoldiers);
        dir = rc.getLocation().directionTo(centerOfEnemies);

        MapLocation rightSide = centerOfEnemies.add(dir.rotateRight().rotateRight(), 5);
        MapLocation leftSide = centerOfEnemies.add(dir.rotateLeft().rotateLeft(), 5);

        int left = 0;
        int right = 0;

        // if a bot is closer to the rightSide location then it is on the right side
        // otherwise it is on the left side
        for (int i = 0; i < enemySoldiers.length; i++)
        {
            MapLocation enemySpot = getBotLocation(enemySoldiers[i]);

            if (enemySpot.distanceSquaredTo(rightSide) < enemySpot.distanceSquaredTo(leftSide))
            {
                right++;
            }
            else
            {
                left++;
            }
        }

        if (left > right)
        {
            dir = dir.rotateRight();
        }
        else
        {
            dir = dir.rotateLeft();
        }

        return dir;
    }

    /**
     * this method returns the center of a group of enemy bots that are relatively close to us
     */
    public static MapLocation centerOfEnemyBots(RobotController rc, int[] enemySoldiers)
    {
        MapLocation center = null;

        int totalX = 0;
        int totalY = 0;
        int numbOfEnemies = 0;

        for (int i = 0; i < enemySoldiers.length; i++)
        {
            MapLocation enemySpot = getBotLocation(enemySoldiers[i]);

            if (rc.getLocation().distanceSquaredTo(enemySpot) < 50)
            {
                totalX += enemySpot.x;
                totalY += enemySpot.y;
                numbOfEnemies++;
            }
        }

        totalX /= numbOfEnemies;
        totalY /= numbOfEnemies;

        center = new MapLocation(totalX, totalY);

        return center;
    }
}
