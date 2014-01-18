package theSwarm2;

import battlecode.common.*;

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

                if (seenEnemies.length > 0)
                {
                    Robot[] enemySoldiers = findSoldiers(rc, seenEnemies);

                    if (enemySoldiers.length > 0)
                    {
                        // we see enemy soldiers
                        Robot[] inRangeEnemySoldiers = findSoldiersAtDistance(rc, seenEnemies, 10);
                        Robot[] oneMoveAwayEnemies = findSoldiersAtDistance(rc, seenEnemies, 24);

                        // if there are enemy soldiers inside of our sight range
                        if (inRangeEnemySoldiers.length > 0)
                        {
                            // if there are more allied troops engaged then enemy bots engaged then we fire
                            if (numbofEngagedEnemies(rc, AllAlliedBots, AllEnemyBots) > numbOfEngagedAllies(rc, AllAlliedBots, AllEnemyBots))
                            {
                                Movement.fire(rc, seenEnemies);
                            }
                            // if there are a bunch of allied soldiers who are almost in range of the enemy then we continue fighting
                            else if (numbOfAlliesAlmostArrived(rc, AllAlliedBots, AllEnemyBots) > numbOfEnemiesAlmostArrived(rc, AllAlliedBots, AllEnemyBots))
                            {
                                Movement.fire(rc, seenEnemies);
                            }
                            // otherwise if there is a location out of range of enemy soldiers that we can retreat to then we do
                            else if (retreatFromBattle(rc, AllEnemyBots, AllAlliedBots) != null)
                            {
                                Direction direction = retreatFromBattle(rc, AllEnemyBots, AllAlliedBots);
                                if (rc.canMove(direction))
                                {
                                    rc.move(direction);
                                }
                                else
                                {
                                    Movement.fire(rc, seenEnemies);
                                }
                            }
                            // for Auir!
                            else
                            {
                                Movement.fire(rc, seenEnemies);
                            }
                        }
                        // if we are one space away from enemy soldiers
                        else if (oneMoveAwayEnemies.length > 0)
                        {
                            // if we outnumber the enemy then we should advance
                            if (numbOfAlliesAlmostArrived(rc, AllAlliedBots, AllEnemyBots) > numbOfEnemiesAlmostArrived(rc, AllAlliedBots, AllEnemyBots))
                            {
                                MapLocation target = enemyWeakSide(rc, AllEnemyBots);
                                MapLocation goTo = bestSupportAdvanceSpot(rc, AllEnemyBots, target, AllAlliedBots);

                                if (rc.canMove(rc.getLocation().directionTo(goTo)))
                                {
                                    rc.move(rc.getLocation().directionTo(goTo));
                                }
                                else
                                {
                                    return false;
                                }
                            }
                            // we won't do anything so we return false and keep moving toward our target location
                            else
                            {
                                return false;
                            }
                        }
                        // otherwise we are a long ways away from the combat
                        else
                        {
                            if (numbOfNearByBots(rc, AllAlliedBots) > numbOfNearByBots(rc, AllEnemyBots))
                            {
                                MapLocation target = enemyWeakSide(rc, AllEnemyBots);
                                MapLocation goTo = bestSupportAdvanceSpot(rc, AllEnemyBots, target, AllAlliedBots);

                                if (rc.canMove(rc.getLocation().directionTo(goTo)))
                                {
                                    rc.move(rc.getLocation().directionTo(goTo));
                                }
                                else
                                {
                                    return false;
                                }
                            }
                            else
                            {
                                return false;
                            }
                        }
                    }
                    // in this case we can see enemy pastrs or noise towers or hq but not soldiers
                    else
                    {
                        MapLocation enemyPastr = rc.senseLocationOf(seenEnemies[0]);
                        Direction dir = rc.getLocation().directionTo(enemyPastr);

                        if (rc.getLocation().distanceSquaredTo(enemyPastr) > 10)
                        {
                            Movement.MoveDirection(rc, dir, false);
                        }
                        else
                        {
                            Movement.fire(rc, seenEnemies);
                        }
                    }
                    return true;
                }
                else
                {

                }
            }
            // we have nothing to do but kill our enemies
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
        	if (gameObjects != null)
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
        	}
            

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
        	if (gameObjects != null)
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
        	}

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
                    //Movement.fire(rc);
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

    public static int[] botsInBattle(RobotController rc, int[] bots)
    {
        int[] robots;
        int[] index = new int[bots.length];
        int numb = 0;

        for (int i = 0; i < bots.length; i++)
        {
            if (rc.getLocation().distanceSquaredTo(getBotLocation(bots[i])) < 75)
            {
                numb++;
                index[i] = 1;
            }
            else
            {
                index[i] = 0;
            }
        }

        robots = new int[numb];
        int index2 = 0;

        for (int j = 0; j < index.length; j++)
        {
            if (index[j] == 1)
            {
                robots[index2] = bots[j];
                index2++;
            }
        }

        return robots;
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

    /**
     * This method returns the number of enemy soldiers that can hit a target location and no other bots of ours
     */
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
                    j = enemySoldiers.length;
                }
            }
            dir = dir.rotateRight();
        }
        int first = 0;

        for (int i = 0; i < 8; i++)
        {
            if (numbOfEnemies[i] == 0)
            {
                first = i;
                i = 8;
            }
        }

        Direction retreat = Direction.NORTH;

        for (int i = 0; i < first; i++)
        {
            retreat = retreat.rotateRight();
        }

        return retreat;
    }

    /**
     * In this method we pick a direction to go in our battle against the enemy
     */
    public static MapLocation enemyWeakSide(RobotController rc, int[] enemySoldiers)
    {
        Direction dir;
        MapLocation target;

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
            target = rightSide;
            /*
            dir = dir.rotateRight();
            target = rc.getLocation().add(dir, 5);
            */
        }
        else
        {
            target = leftSide;
            /*
            dir = dir.rotateLeft();
            target = rc.getLocation().add(dir, 5);*/
        }

        return target;
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

            totalX += enemySpot.x;
            totalY += enemySpot.y;
            numbOfEnemies++;
        }

        if (numbOfEnemies > 0)
        {
        	totalX /= numbOfEnemies;
        	totalY /= numbOfEnemies;
        }
        else
        {
        	totalX = rc.getLocation().x;
        	totalY = rc.getLocation().y;
        }
        

        center = new MapLocation(totalX, totalY);

        return center;
    }

    /**
     * This method returns the number of allied soldiers close to us who are in combat
     */
    public static int numbOfEngagedAllies(RobotController rc, int[] AlliedSoldiers, int[] enemySoldiers)
    {
        int numb = 0;

        for (int i = 0; i < AlliedSoldiers.length; i++)
        {
            MapLocation alliedSpot = getBotLocation(AlliedSoldiers[i]);
            // if the allied soldier is relatively close to us
            for (int j = 0; j < enemySoldiers.length; j++)
            {
                if (alliedSpot.distanceSquaredTo(getBotLocation(enemySoldiers[j])) <= 10)
                {
                    numb++;
                    j = enemySoldiers.length;
                }
            }
        }

        return numb;
    }

    /**
     * This method returns the number of enemy soldiers close to us who are in combat
     */
    public static int numbofEngagedEnemies(RobotController rc, int[] AlliedSoldiers, int[] enemySoldiers)
    {
        int numb = 0;

        for (int i = 0; i < enemySoldiers.length; i++)
        {
            MapLocation spot = getBotLocation(enemySoldiers[i]);
            for (int j = 0; j < AlliedSoldiers.length; j++)
            {
                if (spot.distanceSquaredTo(getBotLocation(AlliedSoldiers[j])) <= 10)
                {
                    j = AlliedSoldiers.length;
                    numb++;
                }
            }
        }

        return numb;
    }

    /**
     * This method returns the number of allied troops one move away from joining the battle
     */
    public static int numbOfAlliesAlmostArrived(RobotController rc, int[] AlliedSoldiers, int[] enemySoldiers)
    {
        int numb = 0;

        for (int i = 0; i < AlliedSoldiers.length; i++)
        {
            MapLocation alliedSpot = getBotLocation(AlliedSoldiers[i]);
            // if the allied soldier is relatively close to us
            if (rc.getLocation().distanceSquaredTo(alliedSpot) < 75)
            {
                for (int j = 0; j < enemySoldiers.length; j++)
                {
                    if (alliedSpot.distanceSquaredTo(getBotLocation(enemySoldiers[j])) <= 24)
                    {
                        numb++;
                        j = enemySoldiers.length;
                    }
                }
            }
        }

        return numb;
    }

    /**
     * This method returns the total health of all bots close to us
     */
    public static int healthOfBots(RobotController rc, int[] bots)
    {
        int totalHealth = 0;

        for (int i = 0; i < bots.length; i++)
        {
            totalHealth += getBotHealth(bots[i]);
        }

        return totalHealth;
    }

    /**
     * This method returns the location that goes towards the closest ally soldier
     */
    public static MapLocation closetestSpotToAllies(RobotController rc, int[] AlliedSoldiers)
    {
        MapLocation target = null;
        int shortestDist = 100;

        if (AlliedSoldiers.length > 0)
        {
            for (int i = 0; i < AlliedSoldiers.length; i++)
            {
                MapLocation alliedSpot = getBotLocation(AlliedSoldiers[i]);
                int dist = rc.getLocation().distanceSquaredTo(alliedSpot);
                if (dist < shortestDist && rc.canMove(rc.getLocation().directionTo(alliedSpot)))
                {
                    shortestDist = dist;
                    target = alliedSpot;
                }
            }
        }

        return target;
    }

    /**
     * This returns the number of enemy soldiers who are almost to the battle
     */
    public static int numbOfEnemiesAlmostArrived(RobotController rc, int[] AlliedSoldiers, int[] enemySoldiers)
    {
        int numb = 0;

        for (int i = 0; i < enemySoldiers.length; i++)
        {
            MapLocation spot = getBotLocation(enemySoldiers[i]);

            if (rc.getLocation().distanceSquaredTo(spot) < 75)
            {
                for (int j = 0; j < AlliedSoldiers.length; j++)
                {
                    if (spot.distanceSquaredTo(getBotLocation(AlliedSoldiers[j])) <= 10)
                    {
                        j = AlliedSoldiers.length;
                        numb++;
                    }
                }
            }
        }

        return numb;
    }

    /**
     * This is the old fight micro with all of the new functons added in
     */
    public static boolean fightMode(RobotController rc, Robot[] nearByEnemies3, int[] AlliedSoldiers, int[] enemySoldiers)
    {
        try
        {
            if (nearByEnemies3 != null && nearByEnemies3.length > 0)
            {
            	nearByEnemies3 = rc.senseNearbyGameObjects(Robot.class, 35, rc.getTeam().opponent());
            	
            	Robot[] nearbyEnemies = null;
                Robot[] nearByEnemies2 = null;
                Robot[] nearByEnemies4 = null;
                Robot[] nearByEnemies5;

                nearByEnemies4 = nearByEnemies3;
                //nearByEnemies5 = nearByEnemies3;
                nearByEnemies3 = findSoldiers(rc, nearByEnemies4);
                //nearByEnemies4 = findNonSoldiers(rc, nearByEnemies4);
                //nearByEnemies4 = findSoldiersAtDistance(rc, nearByEnemies4, 10);

                // here we shrink the array down to just the enemy soldiers who are relatively close
                //AlliedSoldiers = botsInBattle(rc, AlliedSoldiers);
                //enemySoldiers = botsInBattle(rc, enemySoldiers);
                
                
                nearbyEnemies = findSoldiersAtDistance(rc, nearByEnemies3, 10);
                if (nearbyEnemies.length > 0)
                {
                }
                else
                {
                    nearByEnemies2 = findSoldiersAtDistance(rc, nearByEnemies3, 24);
                }


                /**
                 * We see an enemy in our range so either we shoot it or we run outside of its range
                 */
                if (nearbyEnemies.length > 0)
                {
                    Movement.fire(rc, nearByEnemies3);
                    /*
                    // if there are multiple enemies that can only shoot us then we should retreat
                    if (numbOfEnemiesOnlyInRangeOfUs(rc, enemySoldiers, AlliedSoldiers) > 1)
                    {
                        Direction direction = retreatFromBattle(rc, enemySoldiers, AlliedSoldiers);

                        if ( direction != null && rc.canMove(direction))
                        {
                            rc.move(direction);
                        }
                        else
                        {
                            Movement.fire(rc, nearByEnemies3);
                        }
                    }
                    // if there are more allies fighting then enemies then we should attack
                    else if (numbOfEngagedAllies(rc, AlliedSoldiers, enemySoldiers) > numbofEngagedEnemies(rc, AlliedSoldiers, enemySoldiers))
                    {
                        Movement.fire(rc, nearByEnemies3);
                    }
                    // if the total health of our army is significantly greater than our opponents we should
                    // crush them quickly
                    else if (healthOfBots(rc, AlliedSoldiers) > (healthOfBots(rc, enemySoldiers) + 50))
                    {
                        Movement.fire(rc, nearByEnemies3);
                    }
                    // if are facing a single enemy and have higher health then we should kill him
                    else if (nearbyEnemies.length == 1 && (rc.senseRobotInfo(nearbyEnemies[0]).health < rc.getHealth()))
                    {
                        Movement.fire(rc, nearByEnemies3);
                    }
                    // in this case we should retreat as either we have lower health than our opponent
                    // or there are fewer of our troops than of the enemy
                    else
                    {
                        Direction direction = retreatFromBattle(rc, enemySoldiers, AlliedSoldiers);
                        if (rc.canMove(direction))
                        {
                            rc.move(direction);
                        }
                        else
                        {
                            Movement.fire(rc, nearByEnemies3);
                        }
                    }*/
                }
                // if there is an enemy close to use to where if we move they can hit us but not far away for us to shoot then we stop
                else if (nearByEnemies2.length > 0)
                {
                    Direction dir = rc.getLocation().directionTo(rc.senseLocationOf(nearByEnemies2[0]));
                    Movement.MoveDirection(rc, dir, false);
                    /*
                    // if our brethern are in the field of action we must join them!
                    if (numbOfEngagedAllies(rc, AlliedSoldiers, enemySoldiers) > numbofEngagedEnemies(rc, AlliedSoldiers, enemySoldiers))
                    {
                        MapLocation target = enemyWeakSide(rc, enemySoldiers);
                        MapLocation goTo = bestSupportAdvanceSpot(rc, enemySoldiers, target, AlliedSoldiers);

                        Movement.MoveDirection(rc, rc.getLocation().directionTo(goTo), false);
                    }
                    // if we have more troops moving up then we should advance towards the closest allied soldier
                    // to give them support
                    else if (numbOfAlliesAlmostArrived(rc, AlliedSoldiers, enemySoldiers) > numbOfEnemiesAlmostArrived(rc, AlliedSoldiers, enemySoldiers))
                    {
                        MapLocation target;
                        target = enemyWeakSide(rc, enemySoldiers);
                        MapLocation goTo = bestSupportAdvanceSpot(rc, enemySoldiers, target, AlliedSoldiers);

                        if (goTo != null)
                        {
                            Movement.MoveDirection(rc, rc.getLocation().directionTo(goTo), false);
                        }
                    }
                    // if we only sense one enemy soldier then we will attack if we have more health
                    else if (nearByEnemies2.length == 1 && (rc.getHealth() > (rc.senseRobotInfo(nearByEnemies2[0]).health + 20)))
                    {
                        Movement.MoveDirection(rc, rc.getLocation().directionTo(rc.senseLocationOf(nearByEnemies2[0])), false);
                    }
                    // otherwise we are best off shooting any pastrs near us and waiting for the enemy to attack
                    // or reinforcements to arrive
                    else
                    {
                        if (nearByEnemies4.length > 0)
                        {
                            Robot[] nearByEnemies6 = findSoldiersAtDistance(rc, nearByEnemies4, 10);
                            if (nearByEnemies6.length > 0)
                            {
                                Movement.fire(rc, nearByEnemies5);
                            }
                            else
                            {
                            }
                        }
                    }
                    */
                }
                /**
                 * We can see enemies in the distance
                 */
                else if (nearByEnemies3.length > 0)
                {
                    Direction dir = rc.getLocation().directionTo(rc.senseLocationOf(nearByEnemies2[0]));
                    Movement.MoveDirection(rc, dir, false);

                    /*
                    // if we have friends ahead then we must join them
                    if (numbOfEngagedAllies(rc, AlliedSoldiers, enemySoldiers) > 0)
                    {
                        MapLocation target = enemyWeakSide(rc, enemySoldiers);
                        MapLocation goTo = bestSupportAdvanceSpot(rc, enemySoldiers, target, AlliedSoldiers);

                        Movement.MoveDirection(rc, rc.getLocation().directionTo(goTo), false);
                    }
                    // if there are more of us then we should also advance towards the enemy
                    else if (numbOfAlliesAlmostArrived(rc, AlliedSoldiers, enemySoldiers) > numbOfEnemiesAlmostArrived(rc, AlliedSoldiers, enemySoldiers))
                    {
                        MapLocation target = enemyWeakSide(rc, enemySoldiers);
                        MapLocation goTo = bestSupportAdvanceSpot(rc, enemySoldiers, target, AlliedSoldiers);

                        Movement.MoveDirection(rc, rc.getLocation().directionTo(goTo), false);
                    }
                    // if we see enemy pastrs then kill them!
                    else if (nearByEnemies4.length > 0)
                    {
                        Robot[] nearByEnemies6 = findSoldiersAtDistance(rc, nearByEnemies4, 10);
                        if (nearByEnemies6.length > 0)
                        {
                            Movement.fire(rc, nearByEnemies5);
                        }
                        else
                        {
                            MapLocation targeter = rc.senseLocationOf(nearByEnemies4[0]);
                            if (!Utilities.MapLocationInRangeOfEnemyHQ(rc, targeter))
                            {
                                Movement.MoveDirection(rc, rc.getLocation().directionTo(targeter), false);
                            }
                            else
                            {
                                Movement.fire(rc, nearByEnemies3);
                            }
                        }
                    }
                    // if there are allies coming up then wait for them
                    else
                    {
                        return false;
                    }*/
                }

                return true;
            }
            /*
            // here we deal with none soldier enemies like pastrs and noise towers
            else if (nearByEnemies4.length > 0)
            {
                MapLocation target2 = rc.senseLocationOf(nearByEnemies4[0]);
                if (rc.getLocation().distanceSquaredTo(target2) > 10)
                {
                    Movement.MoveDirection(rc, rc.getLocation().directionTo(target2), false);
                }
                else
                {
                    Movement.fire(rc, nearByEnemies5);
                }
                return true;
            }
            */
            else
            {
                return false;
            }
        } catch(Exception e)
        {
            e.printStackTrace();
        }

        return false;

    }
}
