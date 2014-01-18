package theSwarm3;

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

    public static int numbOfRobotsOnlyAttackingUs(RobotController rc, MapLocation[] enemyBots, MapLocation[] alliedBots)
    {
        int numb = 0;
        int engaged;
        MapLocation us = rc.getLocation();

        if (enemyBots != null && alliedBots != null)
        {
            for (int i = enemyBots.length; --i >=0 ; )
            {
                engaged = 1;
                MapLocation enemySpot = enemyBots[i];
                if (enemySpot.distanceSquaredTo(us) <= 10)
                {
                    for (int j = alliedBots.length; --j >= 0;)
                    {
                        if (alliedBots[j].distanceSquaredTo(enemySpot) <= 10)
                        {
                            j = -1;
                            engaged = 0;
                        }
                    }
                    if (engaged == 1)
                    {
                        numb++;
                    }
                }
            }
        }

        return numb;
    }

    public static MapLocation[] locationOfBots(RobotController rc, Robot[] bots)
    {
        MapLocation[] botSpots = new MapLocation[bots.length];

        try
        {
            for (int i = bots.length; --i <= 0;)
            {
                botSpots[i] = rc.senseLocationOf(bots[i]);
            }
        } catch (Exception e) {}

        return botSpots;
    }


    /**
     * This is our old fight micro
     */
    public static boolean fightMode(RobotController rc)
    {
        try
        {
            Robot[] nearbyEnemies = null;
            Robot[] nearByEnemies2 = null;
            Robot[] nearByEnemies3 = null;
            Robot[] nearByEnemies4 = null;
            Robot[] nearByAllies = null;
            Robot[] nearByAllies2 = null;
            Robot[] nearByAllies3 = null;

            boolean alliesEngaged = false;

            // simple shoot at an enemy if we see one will need to be improved later
            nearByEnemies3 = rc.senseNearbyGameObjects(Robot.class, 35, rc.getTeam().opponent());
            nearByEnemies4 = nearByEnemies3;
            //nearByEnemies3 = findSoldiers(rc, nearByEnemies4);
            nearByEnemies4 = findNonSoldiers(rc, nearByEnemies4);
            nearByEnemies4 = findSoldiersAtDistance(rc, nearByEnemies4, 10);

            // here we only do necessary scans to reduce bitcode usage

            if (nearByEnemies3.length > 0)
            {
                nearByAllies = rc.senseNearbyGameObjects(Robot.class, 35, rc.getTeam());
                nearByAllies = findSoldiers(rc, nearByAllies);
                nearbyEnemies = findSoldiersAtDistance(rc, nearByEnemies3, 10);

                MapLocation[] enemyBotLoc = locationOfBots(rc, nearByEnemies3);
                MapLocation[] alliedBots = locationOfBots(rc, nearByAllies);

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
                    alliesEngaged = Utilities.AlliesEngaged(rc, enemyBotLoc, alliedBots);
                    // if there are other bots in range then we should fire
                    if (numbOfRobotsOnlyAttackingUs(rc, enemyBotLoc, alliedBots) > 1)
                    {
                        Direction dir = rc.getLocation().directionTo(enemyBotLoc[0]).opposite();

                        if (Utilities.MapLocationOutOfRangeOfEnemies(rc, enemyBotLoc, rc.getLocation().add(dir)))
                        {
                            if (rc.canMove(dir))
                            {
                                if (rc.isActive())
                                {
                                    rc.move(dir);
                                }
                            }
                            else
                            {
                                Movement.fire(rc, nearByEnemies3);
                            }
                        }
                        else
                        {
                            Movement.fire(rc, nearByEnemies3);
                        }
                    }
                    else if (alliesEngaged)//= nearbyEnemies.length)
                    {
                        Movement.fire(rc, nearByEnemies3);
                    }
                    // if our allies haven't gotten to battle and our opponent isn't almost dead yet wait for them to arrive
                    // assuming we can move to a location our enemy can't hit
                    else if ((nearByAllies.length > 0  && (rc.senseRobotInfo(nearbyEnemies[0]).health > 30)))
                    {
                        MapLocation ally = rc.getLocation().add(rc.getLocation().directionTo(alliedBots[0]));
                        if (Utilities.MapLocationOutOfRangeOfEnemies(rc, enemyBotLoc, ally))
                        {
                            if (Utilities.MapLocationInRangeOfEnemyHQ(rc, ally))
                            {
                                Movement.fire(rc, nearByEnemies3);
                            }
                            else
                            {
                                Movement.MoveDirection(rc, rc.getLocation().directionTo(ally), false);
                            }
                        }
                        else
                        {
                            Movement.fire(rc, nearByEnemies3);
                        }
                    }
                    // if there are multiple enemies attacking us and we don't have support then we need to get out
                    // of there if possible
                    else if (nearbyEnemies.length > 1)
                    {
                        MapLocation enemy = rc.getLocation().subtract(rc.getLocation().directionTo(enemyBotLoc[0]));
                        if (Utilities.MapLocationOutOfRangeOfEnemies(rc, enemyBotLoc, enemy))
                        {
                            if (!Utilities.MapLocationInRangeOfEnemyHQ(rc, enemy))
                            {
                                Movement.MoveDirection(rc, rc.getLocation().directionTo(enemy), false);
                            }
                            else
                            {
                                Movement.fire(rc, nearByEnemies3);
                            }
                        }
                        else
                        {
                            Movement.fire(rc, nearByEnemies3);
                        }
                    }
                    else if ((rc.getHealth() >= rc.senseRobotInfo(nearbyEnemies[0]).health))
                    {
                        Movement.fire(rc, nearByEnemies3);
                    }
                    // in this case we have lower health than our opponent and will be killed so we should retreat
                    else
                    {
                        MapLocation enemy = rc.getLocation().subtract(rc.getLocation().directionTo(enemyBotLoc[0]));
                        if (Utilities.MapLocationOutOfRangeOfEnemies(rc, enemyBotLoc, enemy))
                        {
                            if (!Utilities.MapLocationInRangeOfEnemyHQ(rc, enemy))
                            {
                                if (!Utilities.MapLocationInRangeOfEnemyHQ(rc, enemy))
                                {
                                    Movement.MoveDirection(rc, rc.getLocation().directionTo(enemy), false);
                                }
                                else
                                {
                                    Movement.fire(rc, nearByEnemies3);
                                }
                            }
                            else
                            {
                                Movement.fire(rc, nearByEnemies3);
                            }
                        }
                        else
                        {
                            Movement.fire(rc, nearByEnemies3);
                        }
                    }
                }
                // if there is an enemy close to use to where if we move they can hit us but not far away for us to shoot then we stop
                else if (nearByEnemies2.length > 0)
                {
                    MapLocation enemySlot = rc.senseLocationOf(nearByEnemies2[0]);
                    //nearByAllies3 = rc.senseNearbyGameObjects(Robot.class, enemySlot, 10, rc.getTeam());
                    //nearByAllies3 = findSoldiers(rc, nearByAllies3);
                    alliesEngaged = Utilities.AlliesEngaged(rc, enemyBotLoc, alliedBots);
                    nearByAllies2 = findSoldiersAtDistance(rc, nearByAllies, 9);
                    GameObject[] nearByAllies4 = findSoldiersAtDistance(rc, nearByAllies, 24);
                    // if our brethern are in the field of action we must join them!
                    if (alliesEngaged && nearByAllies3.length <= nearByAllies.length)
                    {
                        if (!Utilities.MapLocationInRangeOfEnemyHQ(rc, enemySlot))
                        {
                            Movement.MoveDirection(rc, rc.getLocation().directionTo(alliedBots[0]), false);
                        }
                        else
                        {
                            Movement.fire(rc, nearByEnemies3);
                        }
                    }
                    else if (nearByAllies4.length > (nearByEnemies2.length))
                    {
                        Movement.MoveDirection(rc, rc.getLocation().directionTo(rc.senseLocationOf(nearByEnemies2[0])), false);
                    }
                    else if (nearByAllies2.length > 0)
                    {
                        MapLocation target = rc.senseLocationOf(nearByAllies2[0]);

                        if (Utilities.MapLocationOutOfRangeOfEnemies(rc, enemyBotLoc, target))
                        {
                            if (!Utilities.MapLocationInRangeOfEnemyHQ(rc, target))
                            {
                                Movement.MoveDirection(rc, rc.getLocation().directionTo(target), false);
                            }
                            else
                            {
                                Movement.fire(rc, nearByEnemies3);
                            }
                        }
                    }
                    else if (nearByEnemies2.length == 1 && rc.getHealth() > (rc.senseRobotInfo(nearByEnemies2[0]).health + 10) && nearByEnemies3.length == 1)
                    {
                        Movement.MoveDirection(rc, rc.getLocation().directionTo(rc.senseLocationOf(nearByEnemies2[0])), false);
                    }
                    else
                    {
                        if (nearByEnemies4.length > 0)
                        {
                            Robot[] nearByEnemies5 = findSoldiersAtDistance(rc, nearByEnemies4, 10);
                            if (nearByEnemies5.length > 0)
                            {
                                MapLocation spot = rc.senseLocationOf(nearByEnemies5[0]);
                                if (Utilities.MapLocationOutOfRangeOfEnemies(rc, enemyBotLoc, spot))
                                {
                                    if (!Utilities.MapLocationInRangeOfEnemyHQ(rc, spot))
                                    {
                                        Movement.MoveDirection(rc, rc.getLocation().directionTo(spot), false);
                                    }
                                    else
                                    {
                                        Movement.fire(rc, nearByEnemies3);
                                    }
                                }
                                else
                                {
                                    Movement.fire(rc, nearByEnemies3);
                                }
                            }
                            else
                            {
                                Movement.fire(rc, nearByEnemies3);
                            }
                        }
                    }
                }
                /**
                 * We can see enemies in the distance
                 */
                else if (nearByEnemies3.length > 0)
                {

                    MapLocation target = rc.senseLocationOf(nearByEnemies3[0]);

                    // if we have friends ahead then we must join them
                    if (Utilities.AlliesAhead(rc, nearByAllies, target) > 0)
                    {
                        /*if (!MapLocationInRangeOfEnemyHQ(rc, target))
                        {*/
                        Movement.MoveDirection(rc, rc.getLocation().directionTo(target), false);
                        /*}
                        else
                        {
                            fire(rc);
                        }*/
                    }
                    else if (rc.senseNearbyGameObjects(Robot.class,  target, 10, rc.getTeam()).length > 0)
                    {
                        Movement.MoveDirection(rc, rc.getLocation().directionTo(target), false);
                    }
                    // if we see enemy pastrs then kill them!
                    else if (nearByEnemies4.length > 0)
                    {
                        Robot[] nearByEnemies5 = findSoldiersAtDistance(rc, nearByEnemies4, 10);
                        if (nearByEnemies5.length > 0)
                        {
                            Movement.fire(rc, nearByEnemies3);
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
                    else if (Utilities.AlliesBehindUs(rc, nearByAllies, target) > 0)
                    {
                        Movement.fire(rc, nearByEnemies3);
                    }
                    // if our enemies have higher health than us also wait
                    else if (rc.senseRobotInfo(nearByEnemies3[0]).health > rc.getHealth())
                    {
                        Movement.fire(rc, nearByEnemies3);
                    }
                    // otherwise advance to death or glory
                    else
                    {
                        if (!Utilities.MapLocationInRangeOfEnemyHQ(rc, target))
                        {
                            Movement.MoveDirection(rc, rc.getLocation().directionTo(target), false);
                        }
                        else
                        {
                            Movement.fire(rc, nearByEnemies3);
                        }
                    }
                }
                return true;
            }
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
                    Movement.fire(rc, nearByEnemies3);
                }
                return true;
            }
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
