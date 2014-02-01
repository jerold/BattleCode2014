package DeepBlue;

import battlecode.common.*;

public class FightMicro 
{
    // these are the channels that we will use to communicate
    static final int enemyHQ = 1;
    static final int ourHQ = 2;
    static final int rallyPoint = 3;
    static final int needNoiseTower = 4;
    static final int needPastr = 5;
    static final int takeDownEnemyPastr = 6;
    static final int enemyPastrInRangeOfHQ = 7;
    static final int rallyPoint2 = 8;

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
        } catch (Exception e) {
            e.printStackTrace();
        }

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
            e.printStackTrace();
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
        } catch (Exception e) {
            e.printStackTrace();
        }

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


            while (index < AllEnemyBots.length && AllEnemyBots[index] != 0 && !foundInList)
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


        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This function takes in information about an enemy Noise Tower and records it to the messaging board if it is
     * new information
     */
    public static void recordEnemyNoiseTower(RobotController rc, int[] AllEnemyNoiseTowers, int noiseTower)
    {/*
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

        } catch (Exception e) {
            e.printStackTrace();
        }*/
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
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            for (int i = index; i < (enemyRobots.length-1); i++)
            {
                rc.broadcast(i+StartEnemyChannel, enemyRobots[i+1]); //ConvertLongToBits(enemyRobots[i+1]));
            }

            rc.broadcast(25+StartEnemyChannel, 0);

        } catch (Exception e) {
            e.printStackTrace();
        }
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

        } catch (Exception e) {
            e.printStackTrace();
        }
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
        } catch (Exception e) {
            e.printStackTrace();
        }

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

        } catch (Exception e) {
            e.printStackTrace();
        }

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

        } catch (Exception e) {
            e.printStackTrace();
        }

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

        } catch (Exception e) {
            e.printStackTrace();
        }
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
        } catch (Exception e) {
            e.printStackTrace();
        }
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
     * This method is for a soldier to fire in the best location possible
     */
    public static void fire(RobotController rc, Robot[] enemies, MapLocation[] allyBots)
    {
        rc.setIndicatorString(2, "Shooting");
        int radius;

        try
        {
            radius = 10;
            //Robot[] enemies2 = rc.senseNearbyGameObjects(Robot.class, 35, rc.getTeam().opponent());
            //enemies = findSoldiersAtDistance(rc, enemies, radius);
            enemies = rc.senseNearbyGameObjects(Robot.class, 10, rc.getTeam().opponent());
            Robot target = null;

            if (enemies != null && allyBots != null)
            {
                rc.setIndicatorString(1, "allyBots present");
                if (allyBots.length > 1)
                {
                    int[] alliedBotsCount = new int[enemies.length];
                    for (int i = enemies.length; --i>=0;)
                    {
                        if (rc.senseRobotInfo(enemies[i]).type != RobotType.HQ)
                        {
                            MapLocation target2 = rc.senseRobotInfo(enemies[i]).location;
                            for (int j = allyBots.length; --j >=0;)
                            {
                                if (target2.distanceSquaredTo(allyBots[j]) <= 10)
                                {
                                    alliedBotsCount[i]++;
                                }
                            }
                        }
                    }

                    int bestVal = 0;
                    int bestIndex = -1;
                    int tieIndex = -1;
                    int tie2Index = -1;

                    for (int i = enemies.length; --i>=0;)
                    {
                        if (alliedBotsCount[i] > bestVal)
                        {
                            bestVal = alliedBotsCount[i];
                            bestIndex = i;
                            tie2Index = -1;
                            tieIndex = -1;
                        }
                        else if (alliedBotsCount[i] == bestVal && tieIndex == -1)
                        {
                            tieIndex = i;
                        }
                        else if (alliedBotsCount[i] == bestVal)
                        {
                            tie2Index = i;
                        }
                    }

                    if (bestIndex != -1)
                    {
                        if (tieIndex != -1)
                        {
                            if (tie2Index != -1)
                            {
                                double bestVal2 = rc.senseRobotInfo(enemies[bestIndex]).health;
                                double tieVal = rc.senseRobotInfo(enemies[tieIndex]).health;
                                double tie2Val = rc.senseRobotInfo(enemies[tie2Index]).health;

                                if (bestVal2 < tieVal)
                                {
                                    if (bestVal2 < tie2Val)
                                    {
                                        target = enemies[bestIndex];
                                    }
                                    else
                                    {
                                        target = enemies[tie2Index];
                                    }
                                }
                                else
                                {
                                    if (tieVal < tie2Val)
                                    {
                                        target = enemies[tieIndex];
                                    }
                                    else
                                    {
                                        target = enemies[tie2Index];
                                    }
                                }
                            }
                            else
                            {
                                if (rc.senseRobotInfo(enemies[bestIndex]).health < rc.senseRobotInfo(enemies[tieIndex]).health)
                                {
                                    target = enemies[bestIndex];
                                }
                                else
                                {
                                    target = enemies[tieIndex];
                                }
                            }
                        }
                        else
                        {
                            target = enemies[bestIndex];
                        }
                    }
                }
            }

            if (enemies != null && target == null || (target != null && rc.getLocation().distanceSquaredTo(rc.senseLocationOf(target)) > 10))
            {
                rc.setIndicatorString(1, "No allies present");
                for(int k = 0; k < enemies.length; k++)
                {
                    if(target == null && !rc.senseRobotInfo(enemies[k]).isConstructing)
                    {
                        target = enemies[k];
                    }
                    // we don't target noise towers unless they are the only unit left in our range of ifre
                    else if (rc.senseRobotInfo(enemies[k]).type == RobotType.NOISETOWER && enemies.length > 1)
                    {
                    }
                    // near the end of the game we target enemy pastrs
                    else if (Clock.getRoundNum() > 1970 && rc.senseRobotInfo(enemies[k]).type == RobotType.PASTR)
                    {
                        target = enemies[k];
                        k = enemies.length;
                    }
                    else if(target != null && rc.senseRobotInfo(enemies[k]).health < rc.senseRobotInfo(target).health && !rc.senseRobotInfo(target).isConstructing && rc.senseRobotInfo(enemies[k]).location.distanceSquaredTo(rc.getLocation()) <= 10)
                    {
                        target = enemies[k];
                    }
                }
            }

            if(target != null)
            {
                if (rc.canSenseObject(target))
                {
                    if (rc.canAttackSquare(rc.senseRobotInfo(target).location))
                    {
                        rc.attackSquare(rc.senseRobotInfo(target).location);
                        if (rc.senseRobotInfo(target).health <= 10)
                        {
                            int[] enemyRobots = AllEnemyBots(rc);
                            recordEnemyBotKilled(rc, enemyRobots, target);
                        }
                    }
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * This method is for the hq shooting
     */
    public static void hqFire(RobotController rc) throws GameActionException
    {
        if (rc.isActive())
        {
            int radius = 15;
            Robot[] enemies = rc.senseNearbyGameObjects(Robot.class, radius, rc.getTeam().opponent());
            Robot[] enemies2 = rc.senseNearbyGameObjects(Robot.class, 24, rc.getTeam().opponent());
            Direction[] dirs = Direction.values();
            Robot target = null;
            int maxValue = 0;

            for(int k = 0; k < enemies.length; k++)
            {
                MapLocation loc = rc.senseRobotInfo(enemies[k]).location;
                int value = 2;
                for (int a = 0; a < 8; a++)
                {
                    try
                    {
                        if (rc.canSenseSquare(loc.add(dirs[a])))
                        {
                            if (rc.senseObjectAtLocation(loc.add(dirs[a])) != null)
                            {
                                if(rc.senseObjectAtLocation(loc.add(dirs[a])).getTeam() == rc.getTeam().opponent())
                                {
                                    value++;
                                }
                                else if(rc.senseObjectAtLocation(loc.add(dirs[a])).getTeam() == rc.getTeam())
                                {
                                    value--;
                                }
                            }
                        }
                    }
                    catch(Exception e){
                        e.printStackTrace();
                    }
                }

                if(value > maxValue)
                {
                    maxValue = value;
                    target = enemies[k];
                }
            }

            if(target != null)
            {
                if (rc.canAttackSquare(rc.senseRobotInfo(target).location))
                {
                    rc.attackSquare(rc.senseRobotInfo(target).location);
                }

            }
            else if (enemies2.length > 0)
            {
                MapLocation location = null;
                MapLocation loc = null;
                maxValue = 0;
                for (int j = 0; j < enemies2.length; j++)
                {

                    int value = 0;
                    MapLocation loc2 = rc.senseRobotInfo(enemies2[j]).location;
                    Direction dir = rc.getLocation().directionTo(loc2).rotateRight().rotateRight();

                    for (int l = 0; l < 3; l++)
                    {
                        loc = loc2.subtract(dir);
                        dir = dir.rotateLeft();
                        if (rc.getLocation().distanceSquaredTo(loc) <= 15)
                        {
                            for (int k = 0; k < 8; k++)
                            {
                                try
                                {
                                    if (rc.canSenseSquare(loc.add(dirs[k])))
                                    {
                                        GameObject enemy = rc.senseObjectAtLocation(loc.add(dirs[k]));

                                        if (enemy != null)
                                        {
                                            if(enemy.getTeam() == rc.getTeam().opponent())
                                            {
                                                value++;
                                            }
                                            else if(enemy.getTeam() == rc.getTeam())
                                            {
                                                value--;
                                            }
                                        }
                                    }
                                }
                                catch(Exception e)
                                {
                                    e.printStackTrace();
                                }
                            }
                        }

                        if (value > 0)
                        {
                            location = loc;
                            l = 3;
                            j = enemies2.length;
                        }
                    }
                    if (maxValue < value)
                    {
                        maxValue = value;
                        location = loc;
                    }
                }

                if (location != null && rc.canAttackSquare(location))
                {
                    rc.attackSquare(location);
                }
            }
        }
    }

    public static boolean MapLocationInRangeOfEnemyHQ(RobotController rc, MapLocation target)
    {
        if (target.distanceSquaredTo(rc.senseEnemyHQLocation()) < 24)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * This method takes a MapLocation and returns it as an int
     */
    public static MapLocation convertIntToMapLocation(int value)
    {
        int x = value / 100;
        int y = value % 100;
        MapLocation loc = new MapLocation(x, y);
        return loc;
    }

    /**
     * This method causes us to move in the closest direction to the one that we want to go
     */
    public static void MoveDirection(RobotController rc, Direction dir, boolean sneak)
    {
        Direction newDir = Direction.NONE;
        int counter = 1;
        try
        {
            // here we do some checks to make sure that we don't throw any exceptions
            if (rc.isActive())
            {

                if (dir != Direction.NONE && dir != Direction.OMNI)
                {
                    if (!rc.canMove(dir) || MapLocationInRangeOfEnemyHQ(rc, rc.getLocation().add(dir)))
                    {
                        // now we loop through the other 7 directions to find one that works
                        for (int i = 0; i < 7; i++)
                        {
                            newDir = dir;
                            // first look right
                            if (i % 2 == 0)
                            {
                                // now we rotate 45 right a certain numb of times
                                for (int j = 0; j < counter; j++)
                                {
                                    newDir = newDir.rotateRight();
                                }
                            }
                            // the look left
                            else
                            {
                                // now we rotate 45 left a certain numb of times
                                for (int j = 0; j < counter; j++)
                                {
                                    newDir = newDir.rotateLeft();
                                }
                                // now after we have looked both ways we update counter
                                counter++;
                            }
                            // at end of for loop we check to see if we can move or if we need to keep looking
                            if (rc.canMove(newDir) && !MapLocationInRangeOfEnemyHQ(rc, rc.getLocation().add(dir)))
                            {
                                i = 48;
                            }
                            // if we have gone through all our options and can't move then we will wait
                            else if (i == 5 && (!rc.canMove(newDir)) || MapLocationInRangeOfEnemyHQ(rc, rc.getLocation().add(dir)))
                            {
                                newDir = Direction.NONE;
                            }
                        }
                    }
                    // we are going to move in the direction of newDir and as we can move in direction dir we assign newDir to it
                    else
                    {
                        newDir = dir;
                    }

                    if (newDir != Direction.NONE)
                    {

                        // now we decide if we are going to sneak or run
                        if (sneak)
                        {
                            // another check to make sure we don't throw any exceptions
                            if (rc.isActive() && rc.canMove(newDir))
                            {
                                rc.sneak(newDir);
                            }
                        }

                        else
                        {
                            // another check to make sure we don't throw any exceptions
                            if (rc.isActive() && rc.canMove(newDir))
                            {
                                rc.move(newDir);
                            }
                        }

                    }

                }

            }
        } catch (Exception e)
        {
            // tell the console we through an exception in utility object for debug purposes
            e.printStackTrace();
        }
    }

    /**
     * This method tells us if our allies are fighting
     */
    public static boolean AlliesEngaged(RobotController rc, MapLocation[] enemies, MapLocation[] allies)
    {
        boolean alliesEngaged = false;
        try
        {
            if (enemies != null && allies != null)
            {
                for (int i = enemies.length; --i >= 0; )
                {
                    MapLocation enemySpot = enemies[i];
                    if (enemySpot != null)
                    {
                        for (int j = allies.length; --j >= 0; )
                        {

                            if (enemySpot.distanceSquaredTo(allies[j]) <= 10)
                            {
                                alliesEngaged = true;
                                j = -1;
                                i = -1;
                            }
                        }
                    }
                }
            }
        } catch(Exception e){
            e.printStackTrace();
        }

        return alliesEngaged;
    }

    /**
     * This method tells us if a location we want to go to is in range of enemy soldiers
     */
    public static boolean MapLocationOutOfRangeOfEnemies(RobotController rc, MapLocation[] SeenEnemies, MapLocation location)
    {
        try
        {
            if (!rc.canMove(rc.getLocation().directionTo(location)))
            {
                return false;
            }
            // we loop through all enemies and if any of them are close enough to shoot this spot then we don't move
            for (int i = SeenEnemies.length; --i >= 0; )
            {
                MapLocation enemySpot = SeenEnemies[i];
                if (enemySpot != null)
                {
                    if (enemySpot.distanceSquaredTo(location) < 11)
                    {
                        return false;
                    }
                }
            }
            return true;
        } catch(Exception e)
        {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * This method tells us how many allies are behind us in case we should wait to advance
     */
    public static int AlliesBehindUs(RobotController rc, Robot[] allies, MapLocation target)
    {
        int numbOfAlliesBehind = 0;
        try
        {
            int distanceToTarget = rc.getLocation().distanceSquaredTo(target);

            for (int i = allies.length; --i>=0;)
            {
                if (distanceToTarget < rc.senseLocationOf(allies[i]).distanceSquaredTo(target))
                {
                    numbOfAlliesBehind++;
                }
            }
        } catch(Exception e)
        {
            e.printStackTrace();
        }
        return numbOfAlliesBehind;
    }

    /**
     * This method tells us if there are allies ahead of us so we can know if we should advance
     */
    public static int AlliesAhead(RobotController rc, Robot[] allies, MapLocation target)
    {
        int numbOfAlliesAhead = 0;
        try
        {
            int distanceToTarget = rc.getLocation().distanceSquaredTo(target);

            for (int i = allies.length; --i>=0;)
            {
                if (distanceToTarget > rc.senseLocationOf(allies[i]).distanceSquaredTo(target))
                {
                    numbOfAlliesAhead++;
                }
            }
        } catch(Exception e)
        {
            e.printStackTrace();
        }
        return numbOfAlliesAhead;
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

                for (int i = gameObjects.length; --i>=0;)
                {
                    // if (rc.canSenseObject(gameObjects[i]))
                    // {
                    MapLocation spot = rc.senseLocationOf(gameObjects[i]);
                    if (rc.getLocation().distanceSquaredTo(spot) <= distance)
                    {
                        index[i] = 1;
                        numb++;
                    }
                    else
                    {
                        index[i] = 0;
                    }
                    //}
                }
                Robot[] soldiers = new Robot[numb];
                int k = numb-1;

                for (int j = gameObjects.length; --j>=0;)
                {
                    if (index[j] == 1)
                    {
                        soldiers[k] = gameObjects[j];
                        k--;
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
     * This method finds the number of soldiers at a particular distance
     */
    public static int findNumbSoldiersAtDist(RobotController rc, Robot[] gameObjects, int distance)
    {
        try
        {
            int numb = 0;
            MapLocation us = rc.getLocation();
            if (gameObjects != null)
            {
                for (int i = gameObjects.length; --i>=0;)
                {
                    if (rc.senseRobotInfo(gameObjects[i]).location.distanceSquaredTo(us) <= distance)
                    {
                        numb++;
                    }
                }
                return numb;
            }

        } catch (Exception e) {}

        return 0;
    }

    /**
     * This method finds the number of soldiers in the array of objects we have seen
     */
    public static Robot[] findSoldiers(RobotController rc, Robot[] gameObjects)
    {
        Robot[] emptySet = null;
        try
        {
            if (gameObjects != null)
            {
                int numbOfSoldiers = 0;
                int[] index = new int[gameObjects.length];
                for (int i = gameObjects.length; --i>=0;)
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
                int k = numbOfSoldiers - 1;
                for (int j = gameObjects.length; --j>=0; )
                {
                    if (index[j] == 1)
                    {
                        soldiers[k] = gameObjects[j];
                        k--;
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
     * This method finds the number of non soldiers in an array of objects we have seen
     */
    public static Robot[] findNonSoldiers(RobotController rc, Robot[] gameObjects)
    {
        Robot[] emptySet = null;
        try
        {
            int numbOfSoldiers = 0;
            int[] index = new int[gameObjects.length];
            for (int i = gameObjects.length; --i>=0; )
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
            int k = numbOfSoldiers - 1;
            for (int j = gameObjects.length; --j>=0;)
            {
                if (index[j] == 1)
                {
                    soldiers[k] = gameObjects[j];
                    k--;
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
     * This method takes in seen robots and returns an array of the desired team
     */
    public static Robot[] findUnitsOnTeam(RobotController rc, Robot[] gameObjects, Team team)
    {
        Robot[] units = null;
        try
        {

            int numbOfUnits = 0;
            int[] index = new int[gameObjects.length];

            for (int i = gameObjects.length; --i>=0;)
            {
                if (rc.senseRobotInfo(gameObjects[i]).team == team)
                {
                    index[i] = 1;
                    numbOfUnits++;
                }
            }
            units = new Robot[numbOfUnits];

            int k = numbOfUnits-1;
            for (int j = index.length; --j>=0;)
            {
                if (index[j] == 1)
                {
                    units[k] = gameObjects[j];
                    k--;
                }
            }

        } catch (Exception e) {}
        return units;
    }

    /**
     * This method returns the number of soldiers that can only attack a certain location
     */
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
                if (enemySpot != null)
                {
                    if (enemySpot.distanceSquaredTo(us) <= 10)
                    {
                        for (int j = alliedBots.length; --j >= 0;)
                        {
                            MapLocation alliedSpot = alliedBots[j];
                            if (alliedSpot != null)
                            {
                                if (alliedSpot.distanceSquaredTo(enemySpot) <= 10)
                                {
                                    j = -1;
                                    engaged = 0;
                                }
                            }
                        }
                        if (engaged == 1)
                        {
                            numb++;
                        }
                    }
                }
            }
        }

        return numb;
    }

    /**
     * This method returns the number of enemy soldiers that are not attacking and would be able to attack us if we advanced
     */
    public static int numbOfRobotsAttackingTarget(RobotController rc, MapLocation goal, MapLocation[] enemyBots, MapLocation[] alliedBots)
    {
        int numb = 0;
        int inRangeOfAlly = 0;
        int test = 0;

        if (goal != null)
        {
            if (enemyBots != null)
            {
                for (int i = enemyBots.length; --i >= 0; )
                {

                    inRangeOfAlly = 0;
                    MapLocation enemy = enemyBots[i];
                    if (enemy != null)
                    {
                        if (goal.distanceSquaredTo(enemy) <= 10)
                        {

                            if (alliedBots != null)
                            {
                                for (int j = alliedBots.length; --j >= 0;)
                                {
                                    MapLocation ally = alliedBots[j];

                                    if (ally != null)
                                    {
                                        if (ally.distanceSquaredTo(enemy) <= 10)
                                        {
                                            inRangeOfAlly = 1;
                                            j = -1;
                                        }
                                    }
                                    test++;
                                }
                            }
                            if (inRangeOfAlly == 0)
                            {
                                numb++;
                            }
                        }
                    }
                }
            }
        }


        return numb;
    }

    /**
     * This method takes an array of robots and turns them into an array of MapLocations
     */
    public static MapLocation[] locationOfBots(RobotController rc, Robot[] bots)
    {
        if (bots != null)
        {
            MapLocation[] botSpots = new MapLocation[bots.length];
            try
            {
                for (int i = bots.length; --i >= 0;)
                {
                    botSpots[i] = rc.senseLocationOf(bots[i]);
                }
            } catch (Exception e)
            {
                e.printStackTrace();
            }

            return botSpots;
        }

        return null;
    }

    /**
     * This method returns the difference in health between our army and the enemies
     */
    public static int ourHealthAdvantage(RobotController rc, Robot[] alliedBots, Robot[] enemyBots)
    {
        int numb = (int) rc.getHealth();
        try
        {
            for (int i = alliedBots.length; --i >=0;)
            {
                if (alliedBots[i] != null)
                {
                    if (rc.canSenseObject(alliedBots[i]))
                    {
                        numb += (int) rc.senseRobotInfo(alliedBots[i]).health;
                    }
                }
            }

            for (int i = enemyBots.length; --i >=0; )
            {
                if (enemyBots[i] != null)
                {
                    if (rc.canSenseObject(enemyBots[i]))
                    {
                        numb -= (int) rc.senseRobotInfo(enemyBots[i]).health;
                    }
                }
            }


        } catch( Exception e) {
            e.printStackTrace();
        }
        return numb;
    }

    /**
     * This returns the number of allies almost in range of enemies
     */
    public static int numbOfAlliesOneSpaceAwayFromAttacking(RobotController rc, MapLocation[] enemyBots, MapLocation[] alliedBots)
    {
        int numb = 0;
        try
        {

            for (int i = alliedBots.length; --i >= 0; )
            {
                MapLocation alliedSpot = alliedBots[i];

                if (alliedSpot != null)
                {
                    for (int j = enemyBots.length; --j >= 0;)
                    {
                        MapLocation enemySpot = enemyBots[j];
                        if ((alliedSpot.distanceSquaredTo(enemySpot)) <= 20 && (rc.senseRobotInfo((Robot) rc.senseObjectAtLocation(alliedBots[j])).actionDelay < 2))
                        {
                            numb++;
                            j = -1;
                        }
                    }
                }
            }
        } catch (Exception e) {}

        return numb;
    }

    /**
     * This method returns the best space to advance into range of the enemy
     *
     * Note: this method is for when we are within a distance of 24 of the nearest enemy
     */
    public static void moveToBestAdvanceLoc(RobotController rc, MapLocation[] enemyBots, MapLocation[] alliedBots)
    {
        try
        {
            int numbOfSpots = 0;
            MapLocation target = null;
            int bestDist = 100;
            Direction dir = null;
            MapLocation[] spotsOpen = new MapLocation[8];
            MapLocation[] secondChoice = new MapLocation[8];

            for (int i = enemyBots.length; --i >= 0; )
            {
                int currentDist = rc.getLocation().distanceSquaredTo(enemyBots[i]);
                if (currentDist < bestDist)
                {
                    bestDist = currentDist;
                    target = enemyBots[i];
                }
            }

            if (target != null)
            {
                dir = rc.getLocation().directionTo(target);
            }

            if (dir != null)
            {
                for (int i = 8; --i >= 0;)
                {
                    MapLocation next = rc.getLocation().add(dir);
                    int nextDist = next.distanceSquaredTo(target);
                    if (nextDist <= 10 && !MapLocationInRangeOfEnemyHQ(rc, next))
                    {
                        spotsOpen[i] = next;
                        numbOfSpots++;
                    }
                    else if (nextDist < rc.getLocation().distanceSquaredTo(target) && !MapLocationInRangeOfEnemyHQ(rc, next))
                    {
                        secondChoice[i] = next;
                    }
                    else
                    {
                        spotsOpen[i] = null;
                    }
                    dir = dir.rotateLeft();
                }

                if (numbOfSpots > 1)
                {
                    // first we will check to see if any allies can only approach the enemy from a certain angle
                    for (int j = alliedBots.length; --j >= 0; )
                    {
                        // here we will throw out locations if they are bad for our buds
                        int alliedDist = alliedBots[j].distanceSquaredTo(target);
                        if (alliedDist > 17 && alliedDist <= 25)
                        {

                            MapLocation onlyAllySpot = alliedBots[j].add(alliedBots[j].directionTo(target));

                            for (int i = 8; --i >= 0;)
                            {
                                if (spotsOpen[i] != null)
                                {
                                    if (spotsOpen[i].equals(onlyAllySpot))
                                    {
                                        spotsOpen[i] = null;
                                        numbOfSpots--;
                                    }
                                }
                            }
                        }
                        // otherwise our bot has multiple paths in so we won't worry about it for now
                    }


                    MapLocation[] leftLocs = new MapLocation[numbOfSpots];
                    int index = 0;

                    for (int i = 8; --i >= 0;)
                    {
                        if (spotsOpen[i] != null && !MapLocationInRangeOfEnemyHQ(rc, spotsOpen[i]))
                        {
                            leftLocs[index] = spotsOpen[i];
                            index++;
                        }
                    }

                    for (int i = numbOfSpots; --i>=0;)
                    {
                        if (leftLocs[i] == null)
                        {
                            leftLocs[i] = rc.getLocation();
                        }
                    }
                    boolean done = false;
                    MapLocation target2 = null;

                    for (int k = leftLocs.length; --k >= 0; )
                    {
                        if (leftLocs != null)
                        {
                            if (rc.senseTerrainTile(leftLocs[k]).equals(TerrainTile.ROAD))
                            {
                                if (rc.canMove(rc.getLocation().directionTo(leftLocs[k])))
                                {
                                    if (rc.isActive())
                                    {
                                        target2 = leftLocs[k];
                                        rc.move(rc.getLocation().directionTo(leftLocs[k]));
                                        done = true;
                                        k = -1;
                                    }
                                }
                            }
                        }
                    }

                    // if we are still not done then we will attempt to move in any othogonal direction
                    if (!done)
                    {
                        for (int k = leftLocs.length; --k >= 0; )
                        {
                            if (leftLocs != null)
                            {
                                MapLocation ourSpot = rc.getLocation();
                                Direction direction = ourSpot.directionTo(leftLocs[k]);
                                if (direction.equals(Direction.NORTH) || direction.equals(Direction.EAST) || direction.equals(Direction.SOUTH) || direction.equals(Direction.WEST))
                                {
                                    if (rc.isActive())
                                    {
                                        if (rc.canMove(direction))
                                        {
                                            target2 = leftLocs[k];
                                            rc.move(direction);
                                            done = true;
                                            k = -1;
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (!done)
                    {
                        if (rc.isActive())
                        {
                            if (leftLocs.length < 1)
                            {
                                target2 = rc.getLocation().add(rc.getLocation().directionTo((target)));
                                Direction direction = rc.getLocation().directionTo(target2);
                                if (rc.canMove(direction))
                                {
                                    rc.move(direction);
                                }
                            }
                            else if (rc.canMove(rc.getLocation().directionTo(leftLocs[0])))
                            {
                                target2 = leftLocs[0];
                                rc.move(rc.getLocation().directionTo(leftLocs[0]));
                            }
                            else if (leftLocs.length > 1 && rc.canMove(rc.getLocation().directionTo(leftLocs[1])))
                            {
                                target2 = leftLocs[1];
                                rc.move(rc.getLocation().directionTo(leftLocs[1]));
                            }
                        }
                    }

                    if (numbOfSpots == 1)
                    {
                        for (int i = 8; --i >= 0; )
                        {
                            if (spotsOpen[i] != null)
                            {
                                if (rc.isActive())
                                {
                                    if (rc.canMove(rc.getLocation().directionTo(spotsOpen[i])))
                                    {
                                        target2 = leftLocs[i];
                                        rc.move(rc.getLocation().directionTo(spotsOpen[i]));
                                    }
                                }
                            }
                        }
                    }
                    else
                    {

                    }
                }
                else if (numbOfSpots == 1)
                {
                    MapLocation target2 = null;
                    for (int i = 8; --i>=0; )
                    {
                        if (spotsOpen[i] != null)
                        {
                            target2 = spotsOpen[i];
                        }
                    }

                    if (target2 != null)
                    {
                        if (rc.isActive())
                        {
                            Direction dir2 = rc.getLocation().directionTo(target);
                            if (rc.canMove(dir2))
                            {
                                try
                                {
                                    rc.move(dir2);
                                } catch ( Exception e) {}
                            }
                        }
                    }
                }
            }

            if (rc.isActive())
            {
                for (int i = 0; i < 8; i++)
                {
                    if (secondChoice[i] != null)
                    {
                        Direction direction = rc.getLocation().directionTo(secondChoice[i]);
                        if (rc.canMove(direction))
                        {
                            if (rc.isActive())
                            {
                                rc.move(direction);
                                i = 989;
                            }
                        }
                    }
                }
            }

        } catch ( Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This function returns a true if the bot should retreat and a false otherwise
     */
    public static boolean retreat(RobotController rc, Robot[] closeEnemySoldiers, MapLocation[] enemyBots, MapLocation[] alliedBots)
    {

        try
        {
            if (rc.readBroadcast(takeDownEnemyPastr) == 0)
            {
                if (closeEnemySoldiers.length > 1 && ((rc.getHealth() <= 50 && rc.getHealth() <= (double) ((closeEnemySoldiers.length * 10) + 10))) || (((rc.getHealth()) % 10 != 0) && rc.getHealth() < 50))
                {
                    Direction move = null;
                    Direction dir;
                    for (int i = enemyBots.length; --i>=0;)
                    {
                        int adjacentEnemies = 0;
                        dir = rc.getLocation().directionTo(enemyBots[i]).opposite();
                        MapLocation next = rc.getLocation().add(dir);
                        for (int j = enemyBots.length; --j >=0; )
                        {
                            if (enemyBots[j].distanceSquaredTo(next) <= 10)
                            {
                                adjacentEnemies = 1;
                                j = -1;
                            }

                        }
                        // then we have our location!!!!!!
                        if (adjacentEnemies == 0 && rc.canMove(dir))
                        {
                            i = -1;
                            move = dir;
                        }
                    }

                    if (move != null)
                    {
                        if (rc.isActive())
                        {
                            if (rc.canMove(move))
                            {

                                rc.move(move);
                            }
                        }
                    }
                    else
                    {
                        for (int i = enemyBots.length; -- i>=0;)
                        {
                            if (rc.getLocation().distanceSquaredTo(enemyBots[i]) <= 10)
                            {
                                SuicideSoldier suicideSoldier = new SuicideSoldier(rc);
                                suicideSoldier.run();
                            }
                        }

                        fire(rc, closeEnemySoldiers, alliedBots);
                    }
                    return true;
                }
            }
        } catch (Exception e) {}

        return false;
    }

    /**
     * This method will finish killing an almost dead enemy before retreating if possible
     */
    public static boolean finishKill(RobotController rc, Robot[] closeEnemySoldiers, MapLocation[] alliedBots)
    {
        try
        {
            if (closeEnemySoldiers.length > 0)
            {
                int lowestHealth = 100;

                for (int i = closeEnemySoldiers.length; --i >=0; )
                {
                    int currentHealth = (int) rc.senseRobotInfo(closeEnemySoldiers[i]).health;
                    if (currentHealth < lowestHealth)
                    {
                        lowestHealth = currentHealth;
                    }
                    if (lowestHealth <= 10)
                    {
                        i = 0;
                    }
                }

                if (lowestHealth < 30 && rc.getHealth() > 10 * closeEnemySoldiers.length)
                {
                    fire(rc, closeEnemySoldiers, alliedBots);
                    return true;
                }
                else
                {
                    return false;
                }
            }
        } catch (Exception e) {}


        return false;
    }

    /**
     * This method returns the center of mass of all known enemies
     */
    public static MapLocation centerOfEnemies(MapLocation[] enemyBots)
    {
        int x = 0;
        int y = 0;

        for (int i = enemyBots.length; --i >=0;)
        {
            if (enemyBots[i] != null)
            {
                x += enemyBots[i].x;
                y += enemyBots[i].y;
            }
        }

        x /= enemyBots.length;
        y /= enemyBots.length;

        if (x != 0)
        {
            return new MapLocation(x, y);
        }

        return null;
    }

    /**
     * This method returns the enemy bot furthest away from center of enemy mass
     */
    public static MapLocation isolatedEnemy(MapLocation[] enemybots)
    {
        MapLocation center = centerOfEnemies(enemybots);
        MapLocation target = null;

        int greatestDist = 0;

        for (int i = enemybots.length; -- i>=0;)
        {
            int currentDist = enemybots[i].distanceSquaredTo(center);
            if (currentDist > greatestDist)
            {
                target = enemybots[i];
                greatestDist = currentDist;
            }
        }

        if (target != null)
        {
            return target;
        }

        return null;
    }

    /**
     * This function moves us further along the flank staying out of range of enemy until we are ready to attack
     */
    public static void moveAroundFlank(RobotController rc, MapLocation[] enemyBots, MapLocation[] alliedBots, MapLocation target)
    {
        try
        {
            MapLocation center = centerOfEnemies(enemyBots);

            Direction dirToCenter = rc.getLocation().directionTo(center);
            Direction dirToTarget = rc.getLocation().directionTo(target);
            Direction dirToMove = dirToTarget;
            boolean left = false;

            if (dirToCenter.rotateLeft().equals(dirToTarget) || dirToCenter.rotateLeft().rotateLeft().equals(dirToTarget))
            {
                left = true;
                dirToMove = dirToTarget.rotateLeft();

                while (rc.getLocation().add(dirToMove).distanceSquaredTo(target) <= 10)
                {
                    dirToMove = dirToMove.rotateLeft();
                }
            }
            else
            {
                dirToMove = dirToTarget.rotateRight();

                while (rc.getLocation().add(dirToMove).distanceSquaredTo(target) <= 10)
                {
                    dirToMove = dirToMove.rotateRight();
                }
            }

            for (int i = enemyBots.length; --i>=0; )
            {
                if (rc.getLocation().add(dirToMove).distanceSquaredTo(enemyBots[i]) <= 10)
                {
                    if (left)
                    {
                        dirToTarget = dirToTarget.rotateLeft();
                    }
                    else
                    {
                        dirToTarget = dirToTarget.rotateRight();
                    }
                }
            }

            if (rc.isActive())
            {
                if (rc.canMove(dirToTarget))
                {
                    rc.move(dirToTarget);
                }
            }
        } catch (Exception e) {}
    }

    /**
     * This method moves the bot into position to attack the enemy flank
     */
    public static void AttackFlank(RobotController rc, MapLocation[] enemyBots, MapLocation[] alliedBots)
    {
        try
        {
            MapLocation target = isolatedEnemy(enemyBots);
            int ourDist = rc.getLocation().distanceSquaredTo(target);
            boolean alliesEngaged = false;

            if (ourDist <= 17)
            {
                int alliesInRange = 0;
                // first we will count the number of allied troops in range of enemy
                for (int i = alliedBots.length; --i >= 0;)
                {
                    int alliedDist = alliedBots[i].distanceSquaredTo(target);
                    if (alliedDist <= 10)
                    {
                        alliesEngaged = true;
                        i = -1;
                    }
                    else if (alliedDist <= 17)
                    {
                        alliesInRange++;
                    }
                }

                if (alliesEngaged)
                {
                    moveToBestAdvanceLoc(rc, enemyBots, alliedBots);
                }
                else if (alliesInRange >= 3)
                {
                    rc.yield();
                    MoveDirection(rc, rc.getLocation().directionTo(target), false);
                }
                else
                {
                    moveAroundFlank(rc, enemyBots, alliedBots, target);
                }
            }
            else
            {
                boolean left = false;
                MapLocation center = centerOfEnemies(enemyBots);
                Direction dirToCenter = rc.getLocation().directionTo(center);
                Direction dirToTarget = rc.getLocation().directionTo(target);
                Direction dir = dirToTarget;
                if (dirToCenter.rotateLeft().equals(dirToTarget) || dirToCenter.rotateLeft().rotateLeft().equals(dirToTarget))
                {
                    left = true;
                }

                for (int i = 0; i < 5; i++)
                {
                    boolean inRange = false;
                    for (int j = enemyBots.length; --j>=0;)
                    {
                        if (enemyBots[j].distanceSquaredTo(rc.getLocation().add(dir)) <= 10)
                        {
                            inRange = true;
                            j = -1;
                        }
                    }
                    if (!inRange)
                    {
                        i = 35;
                    }
                    else
                    {
                        if (left)
                        {
                            dir = dir.rotateLeft();
                        }
                        else
                        {
                            dir = dir.rotateRight();
                        }
                    }
                }

                if (rc.canMove(dir))
                {
                    rc.move(dir);
                }
                else
                {
                    if (left)
                    {
                        dir = dir.rotateLeft();
                    }
                    else
                    {
                        dir = dir.rotateRight();
                    }

                    if (rc.canMove(dir))
                    {
                        rc.move(dir);
                    }
                }
            }
        } catch (Exception e) {}
    }

    /**
     * This method determines if we should morph into a baneling in an attempt to destroy the enemy FOR THE SWARM!
     */
    public static boolean morphBaneling(RobotController rc, MapLocation[] enemyBots, MapLocation[] alliedBots)
    {
        try
        {
            // we will only morph to Banelings if a enemy pastr must be taken down
            if (rc.readBroadcast(takeDownEnemyPastr) == 1)
            {
                if (enemyBots.length > 3)
                {
                    int enemiesShootingUs = 0;

                    for (int i = enemyBots.length; --i>=0;)
                    {
                        if (enemyBots[i].distanceSquaredTo(rc.getLocation()) <= 10)
                        {
                            enemiesShootingUs++;
                        }
                    }
                    boolean banelingAlreadyMorphed = false;

                    // we will only morph if there are a lot of enemy soldiers that are probably attacking us
                    if (enemiesShootingUs > 2)
                    {
                        for (int j = alliedBots.length; --j>=0;)
                        {
                            MapLocation alliedSpot = alliedBots[j];
                            for (int k = enemyBots.length; --k>=0;)
                            {
                                if (alliedSpot.distanceSquaredTo(enemyBots[k]) < 5)
                                {
                                    banelingAlreadyMorphed = true;
                                    k = -1;
                                    j = -1;
                                }
                            }
                        }

                        if (rc.getHealth() < 100 && !banelingAlreadyMorphed)
                        {
                            // the time has come. let each man do his duty
                            SuicideSoldier suicideSoldier = new SuicideSoldier(rc);
                            suicideSoldier.run();
                        }
                    }
                }
            }

        } catch (Exception e) {}

        return false;
    }

    /**
     * This method will attempt to take down an enemy pastr near the hq or die bravely in the attempt
     */
    public static boolean takeDownPastrNearHQ(RobotController rc, MapLocation[] alliedBots, Robot[] AllEnemies)
    {
        try
        {
            if (rc.readBroadcast(enemyPastrInRangeOfHQ) == 1)
            {
                if (rc.getLocation().distanceSquaredTo(rc.senseEnemyHQLocation()) < 49)
                {
                    MapLocation target = null;

                    for (int j = AllEnemies.length; --j >=0; )
                    {
                        if (rc.canSenseObject(AllEnemies[j]))
                        {
                            if (rc.senseRobotInfo(AllEnemies[j]).type == RobotType.PASTR)
                            {
                                target = rc.senseRobotInfo(AllEnemies[j]).location;
                            }
                        }
                    }

                    if (target != null)
                    {
                        if (rc.getLocation().distanceSquaredTo(target) <= 10)
                        {
                            fire(rc, AllEnemies, alliedBots);
                            return true;
                        }
                        else
                        {
                            Direction dir = rc.getLocation().directionTo(target);
                            if (rc.isActive())
                            {
                                if (rc.canMove(dir))
                                {
                                    rc.move(dir);
                                }
                                else if (rc.canMove(dir.rotateLeft()))
                                {
                                    rc.move(dir.rotateLeft());
                                }
                                else if (rc.canMove(dir.rotateRight()))
                                {
                                    rc.move(dir.rotateRight());
                                }
                                else
                                {
                                    fire(rc, AllEnemies, alliedBots);
                                }
                                return true;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {}

        return false;
    }

    /**
     * This method determines if the enemy is split up and/or seperated by void spaces and moves towards the weaker group if so
     */
    public static boolean splitUpEnemy(RobotController rc, MapLocation[] enemyBots, MapLocation[] alliedBots)
    {
        MapLocation center = centerOfEnemies(enemyBots);
        boolean noEnemiesAtCenter = true;
        boolean enemySplit = false;

        for (int i = enemyBots.length; --i >=0; )
        {
            if (enemyBots[i].distanceSquaredTo(center) <= 6)
            {
                noEnemiesAtCenter = false;
                i = -1;
            }
        }

        if (noEnemiesAtCenter)
        {
            enemySplit = true;
        }
        else
        {
            for (int i = enemyBots.length; --i>=0;)
            {
                if (enemyBots[i].distanceSquaredTo(center) > (enemyBots.length * enemyBots.length * 4))
                {
                    enemySplit = true;
                    i = -1;
                }
            }
        }

        if (enemySplit)
        {
            // now we will determine the weaker side based on how many soldiers are closer to a location on the right
            MapLocation rightOfCenter;
            MapLocation leftOfCenter;
            Direction dir = rc.getLocation().directionTo(center);

            rightOfCenter = center.add(dir.rotateRight().rotateRight());
            leftOfCenter = center.add(dir.rotateLeft().rotateLeft());

            int numbOfTroopsLeft = 0;
            int numbOfTroopsRight = 0;

            for (int j = enemyBots.length; --j >= 0;)
            {
                if (enemyBots[j].distanceSquaredTo(rightOfCenter) < enemyBots[j].distanceSquaredTo(leftOfCenter))
                {
                    numbOfTroopsRight++;
                }
                else
                {
                    numbOfTroopsLeft++;
                }
            }

            if (numbOfTroopsLeft > numbOfTroopsRight)
            {
                dir = dir.rotateRight().rotateRight();
                rightOfCenter = rightOfCenter.add(dir, 5);
                MoveDirection(rc, rc.getLocation().directionTo(rightOfCenter), false);
            }
            else
            {
                dir = dir.rotateLeft().rotateLeft();
                leftOfCenter = leftOfCenter.add(dir, 5);
                MoveDirection(rc, rc.getLocation().directionTo(leftOfCenter), false);
            }

            return true;
        }

        return false;
    }

    /**
     * This function returns true if we have a mapLocation to go to and if it isn't in range of any enemy soldiers
     */
    public static boolean advanceToTarget(RobotController rc, MapLocation[] enemyBots, MapLocation target)
    {
        try
        {
            if (target != null)
            {
                boolean straight = true;
                boolean right = true;
                boolean left = true;
                Direction dir = rc.getLocation().directionTo(target);
                Direction rightDir = dir.rotateRight();
                Direction leftDir = dir.rotateLeft();
                MapLocation goal = rc.getLocation().add(dir);
                MapLocation goalRight = rc.getLocation().add(rightDir);
                MapLocation goalLeft = rc.getLocation().add(leftDir);
                for (int i = enemyBots.length; --i >=0; )
                {
                    //boolean pastr = rc.senseRobotInfo((Robot) rc.senseObjectAtLocation(enemyBots[i])).type != RobotType.PASTR;
                    if (true)
                    {
                        if (enemyBots[i].distanceSquaredTo(goal) <= 10)
                        {
                            straight =  false;
                        }
                        if (enemyBots[i].distanceSquaredTo(goalRight) <= 10)
                        {
                            right = false;
                        }
                        if (enemyBots[i].distanceSquaredTo(goalLeft) <= 10)
                        {
                            left = false;
                        }
                    }

                    if (!right && !left && !straight)
                    {
                        return false;
                    }
                }

                if (straight && rc.canMove(dir))
                {
                    rc.move(dir);
                    return true;
                }
                else if (right && rc.canMove(rightDir))
                {
                    rc.move(rightDir);
                    return true;
                }
                else if (left && rc.canMove(leftDir))
                {
                    rc.move(leftDir);
                    return true;
                }
            }
        } catch (Exception e) {}

        return false;
    }

    /**
     * This function gets us away from the hq if we are near it and there is no enemy pastr near it
     */
    public static boolean runFromEnemyHQ(RobotController rc)
    {
        try
        {
            MapLocation enemyHQ = rc.senseEnemyHQLocation();
            if (rc.getLocation().distanceSquaredTo(enemyHQ) < 35)
            {
                if (rc.readBroadcast(enemyPastrInRangeOfHQ) == 0)
                {
                    Direction direction = rc.getLocation().directionTo(enemyHQ).opposite();
                    if (rc.isActive())
                    {
                        if (rc.canMove(direction))
                        {
                            rc.move(direction);
                        }
                        else if (rc.canMove(direction.rotateLeft()))
                        {
                            rc.move(direction.rotateLeft());
                        }
                        else if (rc.canMove(direction.rotateRight()))
                        {
                            rc.move(direction.rotateRight());
                        }
                        else if (rc.canMove(direction.rotateLeft().rotateLeft()))
                        {
                            rc.move(direction.rotateLeft().rotateLeft());
                        }
                        else if (rc.canMove(direction.rotateRight().rotateRight()))
                        {
                            rc.canMove(direction.rotateRight().rotateRight());
                        }
                        else
                        {
                            return false;
                        }
                        return true;
                    }
                }
            }
        } catch (Exception e) {}

        return false;
    }


    /**
     * This function returns true if there is a good retreat location to go to and false otherwise
     */
    public static boolean retreatToAllies(RobotController rc, Robot[] inRangeEnemies, MapLocation[] enemyBots, MapLocation[] alliedBots)
    {
        try
        {
            if (rc.isActive())
            {
                MapLocation closestAlly = null;
                int closestDist = 100;

                for (int i = alliedBots.length; --i>=0;)
                {
                    int currentDist = rc.getLocation().distanceSquaredTo(alliedBots[i]);
                    if (currentDist < closestDist)
                    {
                        closestAlly = alliedBots[i];
                        closestDist = currentDist;
                    }
                }

                if (closestAlly != null)
                {
                    Direction dirToAlly = rc.getLocation().directionTo(closestAlly);
                    if (MapLocationOutOfRangeOfEnemies(rc, enemyBots, rc.getLocation().add(dirToAlly)))
                    {
                        if (rc.canMove(dirToAlly))
                        {
                            rc.move(dirToAlly);
                            return true;
                        }
                    }
                }
            }
        } catch (Exception e) {}

        return false;
    }

    /**
     * This method will return true and react accoridingly if enemies get between us our defense postion
     */
    public static boolean enemyInfiltration(RobotController rc, MapLocation[] enemyBots, MapLocation[] alliedBots, Robot[] inRangeEnemies, MapLocation defenseLoc)
    {
        if (rc.isActive())
        {

            if (enemyBots.length > 0)
            {
                boolean enemiesInOurWay = false;
                //int ourDistToDefenseLoc = rc.getLocation().distanceSquaredTo(defenseLoc);
                for (int i = enemyBots.length; --i>=0;)
                {
                    int enemyDistToDefenseLoc = enemyBots[i].distanceSquaredTo(defenseLoc);
                    if (15 >= enemyDistToDefenseLoc)
                    {
                        i = -1;
                        enemiesInOurWay = true;
                    }
                }

                if (enemiesInOurWay)
                {
                    if (inRangeEnemies.length > 0)
                    {
                        fire(rc, inRangeEnemies, alliedBots);
                    }
                    else
                    {
                        Direction dir = rc.getLocation().directionTo(defenseLoc);
                        MoveDirection(rc, dir, false);

                    }
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * This method is for defense micro where we defend a target location by holding ground until the enemy outnumbers us then retreating
     * in an attempt to get the enemy to attack us piecemeal and get destroyed upon our lines
     */
    public static boolean defenseMicro(RobotController rc, MapLocation defenseLoc)
    {
        if (rc.isActive())
        {
            if (rc.getLocation().distanceSquaredTo(defenseLoc) < 50)
            {
                Robot[] allVisibleEnemies = rc.senseNearbyGameObjects(Robot.class, 35, rc.getTeam().opponent());
                if (allVisibleEnemies.length > 0)
                {
                    Robot[] inRangeEnemies = findSoldiers(rc, allVisibleEnemies);
                    inRangeEnemies = findSoldiersAtDistance(rc, inRangeEnemies, 10);
                    Robot[] allVisibleAllies = rc.senseNearbyGameObjects(Robot.class, 35, rc.getTeam());
                    Robot[] allVisibleAlliedSoldiers = findSoldiers(rc, allVisibleAllies);
                    MapLocation[] enemyBotLoc = locationOfBots(rc, allVisibleEnemies);
                    MapLocation[] alliedBots = locationOfBots(rc, allVisibleAllies);

                    if (enemyInfiltration(rc, enemyBotLoc, alliedBots, inRangeEnemies, defenseLoc))
                    {
                    }
                    else if (inRangeEnemies.length > 0)
                    {
                        // if a bunch of enemies have positioned themselves to just attack us and we have friends then we should retreat
                        if (numbOfRobotsOnlyAttackingUs(rc, enemyBotLoc, alliedBots) > 1 && allVisibleAlliedSoldiers.length > 1)
                        {
                            if (retreatToAllies(rc, allVisibleEnemies, enemyBotLoc, alliedBots))
                            {
                            }
                            else
                            {
                                fire(rc, allVisibleEnemies, alliedBots);
                            }
                        }
                        else
                        {
                            fire(rc, allVisibleEnemies, alliedBots);
                        }
                    }
                    else if (allVisibleEnemies.length > 0)
                    {
                        // ideally we move toward our allies to strengthen our position
                        if (retreatToAllies(rc, allVisibleEnemies, enemyBotLoc, alliedBots))
                        {

                        }

                        else
                        {
                            fire(rc, allVisibleEnemies, alliedBots);
                        }
                    }
                    return true;
                }
            }
        }
        else
        {

        }
        return false;
    }

    /**
     * This method tells us if we should ignore enemies because their is a wall in between us and them
     */
    public static boolean enemiesWalledOff(RobotController rc, MapLocation[] enemyBots)
    {
        try
        {
            if (enemyBots != null)
            {

                boolean enemyWalledOff = false;
                MapLocation current;
                MapLocation us = rc.getLocation();
                MapLocation enemy;

                for (int i = enemyBots.length; --i>=0;)
                {
                    enemyWalledOff = false;
                    enemy = enemyBots[i];
                    current = us;
                    while (!current.equals(enemy))
                    {
                        current = current.add(current.directionTo(enemy));
                        if (rc.senseTerrainTile(current).equals(TerrainTile.VOID))
                        {
                            current = enemy;
                            enemyWalledOff = true;
                        }
                    }

                    if (!enemyWalledOff)
                    {
                        i = -1;
                        return false;
                    }
                }
                return true;
            }
        } catch (Exception e) {}

        return false;
    }

    /**
     * This method goes to a target location trying to avoid enemy soldiers until it has to engage to get to its goal
     */
    public static boolean cloakedMove(RobotController rc, MapLocation target)
    {
        if (rc.isActive())
        {
            try
            {
                Robot[] enemies = rc.senseNearbyGameObjects(Robot.class, 35, rc.getTeam().opponent());
                //enemies = findSoldiers(rc, enemies);

                if (enemies.length > 0)
                {
                    rc.setIndicatorString(2, ""+enemies[0]);
                    Robot[] nearByEnemies = rc.senseNearbyGameObjects(Robot.class, 10, rc.getTeam().opponent());

                    if (nearByEnemies.length > 0)
                    {
                        // if we can see enemies then if we have the advantage we attack
                        // other wise we will retreat
                        Robot[] nearByAllies = rc.senseNearbyGameObjects(Robot.class, 10, rc.getTeam());
                        MapLocation[] alliedLocs = locationOfBots(rc, nearByAllies);

                        if (nearByAllies.length >= nearByEnemies.length)
                        {
                            fire(rc, nearByEnemies, alliedLocs);
                        }
                        else if (nearByEnemies.length == 1)
                        {
                            fire(rc, nearByEnemies, alliedLocs);
                        }
                        else
                        {
                            MoveDirection(rc, rc.getLocation().directionTo(rc.senseLocationOf(nearByEnemies[0])).opposite(), false);
                        }
                    }
                    else
                    {
                        if (enemies.length == 1)
                        {
                            MoveDirection(rc, rc.getLocation().directionTo(rc.senseLocationOf(enemies[0])), false);
                        }
                        else
                        {
                            MapLocation closestSpot = rc.senseLocationOf(nearByEnemies[nearByEnemies.length-1]);
                            int smallestDistance = rc.getLocation().distanceSquaredTo(closestSpot);
                            for (int i = nearByEnemies.length-1; --i>=0;)
                            {
                                MapLocation current = rc.senseLocationOf(nearByEnemies[i]);
                                int dist = rc.getLocation().distanceSquaredTo(current);
                                if (dist < smallestDistance)
                                {
                                    smallestDistance = dist;
                                    closestSpot = current;
                                }
                            }

                            // now we have the closest enemy bot to our location
                            Direction dir = rc.getLocation().directionTo(closestSpot).opposite();
                            boolean left;
                            if (rc.getLocation().add(dir.rotateLeft()).distanceSquaredTo(target) < rc.getLocation().add(dir.rotateRight()).distanceSquaredTo(target))
                            {
                                left = true;
                            }
                            else
                            {
                                left = false;
                            }

                            if (left)
                            {
                                MoveDirection(rc, dir.rotateLeft(), false);
                            }
                            else
                            {
                                MoveDirection(rc, dir.rotateRight(), false);
                            }
                            rc.setIndicatorString(2, ""+dir+", "+left);
                        }
                    }
                    return true;
                }
            } catch (Exception e) {}
        }

        return false;
    }

    /**
     * This is our old fight micro which is under major renovation
     */
    public static boolean fightMode(RobotController rc, MapLocation endGoal)
    {
        try
        {
            Robot[] nearbyEnemies = null;
            Robot[] nearByEnemies2 = null;
            Robot[] nearByEnemies3 = null;
            Robot[] nearByEnemies4 = null;
            Robot[] nearByEnemies10 = null;
            Robot[] nearByAllies = null;
            //Robot[] gameObjects;

            //gameObjects = rc.senseNearbyGameObjects(Robot.class, 35);
            //nearByEnemies3 = findUnitsOnTeam(rc, gameObjects, rc.getTeam().opponent());
            nearByEnemies3 = rc.senseNearbyGameObjects(Robot.class, 35, rc.getTeam().opponent());
            //nearByEnemies4 = nearByEnemies3;
            nearByEnemies10 = nearByEnemies3;
            nearByEnemies3 = findSoldiers(rc, nearByEnemies4);
            //nearByEnemies4 = findNonSoldiers(rc, nearByEnemies4);
            //nearByEnemies4 = findSoldiersAtDistance(rc, nearByEnemies4, 10);
            nearByEnemies4 = rc.senseNearbyGameObjects(Robot.class, 10, rc.getTeam().opponent());

            if (nearByEnemies3 != null && nearByEnemies3.length > 0)
            {
                if (rc.isActive())
                {
                    //nearByAllies = findUnitsOnTeam(rc, gameObjects, rc.getTeam());
                    nearByAllies = rc.senseNearbyGameObjects(Robot.class, 35, rc.getTeam());
                    //nearByAllies = findSoldiers(rc, nearByAllies);
                    //nearbyEnemies = findSoldiersAtDistance(rc, nearByEnemies3, 10);
                    nearbyEnemies = rc.senseNearbyGameObjects(Robot.class, 10, rc.getTeam().opponent());

                    MapLocation[] enemyBotLoc = locationOfBots(rc, nearByEnemies3);
                    MapLocation[] alliedBots = locationOfBots(rc, nearByAllies);

                    if (nearbyEnemies.length > 0)
                    {
                    }
                    else
                    {
                        nearByEnemies2 = rc.senseNearbyGameObjects(Robot.class, 24, rc.getTeam().opponent());
                    }


                    /**
                     * We see an enemy in our range so either we shoot it or we run outside of its range
                     */
                    if (nearbyEnemies.length > 0)
                    {
                        rc.setIndicatorString(0, "FightMicro");
                        if (nearbyEnemies.length == 1 && rc.getHealth() > 50)
                        {
                            rc.setIndicatorString(0, "Fire!");
                            fire(rc, nearByEnemies10, alliedBots);
                        }
                        // based on our health it may be advantageous to retreat so we can fight another day
                        else if (retreat(rc, nearbyEnemies, enemyBotLoc, alliedBots))
                        {
                        }
                        else if (runFromEnemyHQ(rc))
                        {
                        }
                        else if (finishKill(rc, nearbyEnemies, alliedBots))
                        {
                        }
                        else if (morphBaneling(rc, enemyBotLoc, alliedBots))
                        {
                        }/*
                        else if (rc.readBroadcast(takeDownEnemyPastr) == 1)
                        {
                            fire(rc, nearByEnemies10, alliedBots);
                        }*/
                        // if there are many enemy bots attacking us then we should retreat
                        else if (numbOfRobotsOnlyAttackingUs(rc, enemyBotLoc, alliedBots) > 1 && findNumbSoldiersAtDist(rc, nearByAllies, 4) < 2)
                        {
                            Direction dir = rc.getLocation().directionTo(enemyBotLoc[0]).opposite();

                            if (MapLocationOutOfRangeOfEnemies(rc, enemyBotLoc, rc.getLocation().add(dir)))
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
                                    fire(rc, nearByEnemies10, alliedBots);
                                }
                            }
                            else
                            {
                                fire(rc, nearByEnemies10, alliedBots);
                            }
                        }
                        else if (AlliesEngaged(rc, enemyBotLoc, alliedBots))
                        {
                            fire(rc, nearByEnemies10, alliedBots);
                        }
                        // if there are multiple enemies attacking us and we don't have support then we need to get out
                        // of there if possible
                        else if (nearbyEnemies.length > 1  && nearByAllies.length == 0)
                        {
                            MapLocation enemy = rc.getLocation().subtract(rc.getLocation().directionTo(enemyBotLoc[0]));
                            if (MapLocationOutOfRangeOfEnemies(rc, enemyBotLoc, enemy))
                            {
                                if (!MapLocationInRangeOfEnemyHQ(rc, enemy))
                                {
                                    MoveDirection(rc, rc.getLocation().directionTo(enemy), false);
                                }
                                else
                                {
                                    fire(rc, nearByEnemies10, alliedBots);
                                }
                            }
                            else
                            {
                                fire(rc, nearByEnemies10, alliedBots);
                            }
                        }
                        else if (nearbyEnemies.length == 1 && rc.getHealth() < 50 && rc.getHealth() < rc.senseRobotInfo(nearbyEnemies[0]).health)
                        {
                            MoveDirection(rc, rc.getLocation().directionTo(rc.senseLocationOf(nearbyEnemies[0])).opposite(), false);
                        }
                        // in this case we have lower health than our opponent and will be killed so we should retreat
                        else
                        {
                            MapLocation enemy = rc.getLocation().subtract(rc.getLocation().directionTo(enemyBotLoc[0]));
                            if (MapLocationOutOfRangeOfEnemies(rc, enemyBotLoc, enemy))
                            {
                                if (!MapLocationInRangeOfEnemyHQ(rc, enemy))
                                {
                                    if (!MapLocationInRangeOfEnemyHQ(rc, enemy))
                                    {
                                        MoveDirection(rc, rc.getLocation().directionTo(enemy), false);
                                    }
                                    else
                                    {
                                        fire(rc, nearByEnemies10, alliedBots);
                                    }
                                }
                                else
                                {
                                    fire(rc, nearByEnemies10, alliedBots);
                                }
                            }
                            else
                            {
                                fire(rc, nearByEnemies10, alliedBots);
                            }
                        }
                    }
                    /**
                     * If we can see enemies but they are one move away then ...
                     */
                    else if (nearByEnemies2.length > 0)
                    {
                        //MapLocation enemySlot = rc.senseLocationOf(nearByEnemies2[0]);
                        //nearByAllies3 = rc.senseNearbyGameObjects(Robot.class, enemySlot, 10, rc.getTeam());
                        //nearByAllies3 = findSoldiers(rc, nearByAllies3);
                        //alliesEngaged = AlliesEngaged(rc, enemyBotLoc, alliedBots);
                        //nearByAllies2 = findSoldiersAtDistance(rc, nearByAllies, 9);
                        //GameObject[] nearByAllies4 = findSoldiersAtDistance(rc, nearByAllies, 24);
                        Robot[] nearByAllies5 = findSoldiersAtDistance(rc, nearByAllies, 10);
                        //Direction dir = rc.getLocation().directionTo(enemyBotLoc[0]);
                        if (rc.senseRobotInfo(nearByEnemies2[0]).type == RobotType.NOISETOWER || rc.senseRobotInfo(nearByEnemies2[0]).type == RobotType.PASTR)
                        {
                            MoveDirection(rc, rc.getLocation().directionTo(rc.senseLocationOf(nearByEnemies2[0])), false);
                        }
                        else if (retreat(rc, nearByEnemies3, enemyBotLoc, alliedBots))
                        {
                            rc.setIndicatorString(1, "Fly you fools");
                        }
                        else if (nearByEnemies4.length > 0)
                        {
                            fire(rc, nearByEnemies10, alliedBots);
                        }
                        else if (enemiesWalledOff(rc, enemyBotLoc))
                        {
                            rc.setIndicatorString(1, "Trapped");
                            return false;
                        }
                        else if (takeDownPastrNearHQ(rc, alliedBots, nearByEnemies10))
                        {
                            rc.setIndicatorString(1, "take down hq pastr");
                        }
                        else if (runFromEnemyHQ(rc))
                        {
                        }
                        else if (endGoal != null && numbOfRobotsAttackingTarget(rc, rc.getLocation().add(rc.getLocation().directionTo(endGoal)), enemyBotLoc, alliedBots) == 0)//rc.getLocation().add(rc.getLocation().directionTo(endGoal)).distanceSquaredTo(rc.senseLocationOf(nearByEnemies2[0])) > 10)
                        {
                            return false;
                        }
                        else if (advanceToTarget(rc, enemyBotLoc, endGoal))
                        {
                        }
                        else if (rc.readBroadcast(takeDownEnemyPastr) == 1)// && !alliesEngaged)
                        {
                            MapLocation center = centerOfEnemies(enemyBotLoc);
                            if (!MapLocationInRangeOfEnemyHQ(rc, center))
                            {
                                MoveDirection(rc, rc.getLocation().directionTo(center), false);
                            }
                        }
                        else if (nearByEnemies3.length == 1 && (numbOfAlliesOneSpaceAwayFromAttacking(rc, enemyBotLoc, alliedBots) > 1 || rc.getHealth() > rc.senseRobotInfo(nearByEnemies2[0]).health))
                        {
                            moveToBestAdvanceLoc(rc, enemyBotLoc, alliedBots);
                        }
                        // if our brethern are in the field of action we must join them!
                        else if (AlliesEngaged(rc, enemyBotLoc, alliedBots) && (enemyBotLoc.length <= (alliedBots.length + 1)) && numbOfRobotsAttackingTarget(rc, rc.getLocation().add(rc.getLocation().directionTo(enemyBotLoc[0])), enemyBotLoc, alliedBots) < 2)
                        {
                            moveToBestAdvanceLoc(rc, enemyBotLoc, alliedBots);
                        }
                        else if ((numbOfAlliesOneSpaceAwayFromAttacking(rc, enemyBotLoc, alliedBots) > (nearByEnemies3.length)) && ourHealthAdvantage(rc, nearByAllies5, nearByEnemies3) > 50)
                        {
                            moveToBestAdvanceLoc(rc, enemyBotLoc, alliedBots);
                            //Movement.MoveDirection(rc, rc.getLocation().directionTo(rc.senseLocationOf(nearByEnemies2[0])), false);
                        }
                        else if (nearByAllies5 != null && nearByAllies5.length > 0)
                        {
                            // if a nearby ally has high health we will join him
                            if (rc.senseRobotInfo(nearByAllies5[0]).health > 75)
                            {
                                MapLocation target = rc.senseLocationOf(nearByAllies5[0]);
                                Direction dir2 = rc.getLocation().directionTo(target);

                                if (MapLocationOutOfRangeOfEnemies(rc, enemyBotLoc, rc.getLocation().add(dir2)))
                                {
                                    if (rc.canMove(dir2))
                                    {
                                        if (rc.isActive())
                                        {
                                            rc.move(dir2);
                                        }
                                    }
                                    else
                                    {
                                        fire(rc, nearByEnemies10, alliedBots);
                                    }
                                }
                            }
                        }
                        else if (nearByEnemies2.length == 1 && rc.getHealth() > (rc.senseRobotInfo(nearByEnemies2[0]).health + 10) && nearByEnemies3.length == 1)
                        {
                            moveToBestAdvanceLoc(rc, enemyBotLoc, alliedBots);
                            //Movement.MoveDirection(rc, rc.getLocation().directionTo(rc.senseLocationOf(nearByEnemies2[0])), false);
                        }
                        else
                        {
                            if (nearByEnemies4.length > 0)
                            {
                                fire(rc, nearByEnemies10, alliedBots);
                            }
                        }

                        return true;

                    }
                    /**
                     * We can see enemies in the distance
                     */
                    else if (nearByEnemies3.length > 0)
                    {
                        MapLocation target = rc.senseLocationOf(nearByEnemies3[0]);
                        if (retreat(rc, nearByEnemies3, enemyBotLoc, alliedBots))
                        {
                        }
                        else if (nearByEnemies4.length > 0)
                        {
                            fire(rc, nearByEnemies10, alliedBots);
                        }
                        else if (runFromEnemyHQ(rc))
                        {
                        }
                        else if (advanceToTarget(rc, enemyBotLoc, endGoal))
                        {
                        }
                        else if (takeDownPastrNearHQ(rc, alliedBots, nearByEnemies10))
                        {
                        }
                        // if we have friends ahead then we must join them
                        else if (AlliesAhead(rc, nearByAllies, target) > 0)
                        {
                            if (!MapLocationInRangeOfEnemyHQ(rc, target))
                            {
                                MoveDirection(rc, rc.getLocation().directionTo(target), false);
                            }
                        }
                        // if there are allies coming up then wait for them
                        else if (AlliesBehindUs(rc, nearByAllies, target) > 0)
                        {
                            fire(rc, nearByEnemies3, alliedBots);
                        }
                        // otherwise advance to death or glory
                        else
                        {
                            if (!MapLocationInRangeOfEnemyHQ(rc, target))
                            {
                                MoveDirection(rc, rc.getLocation().directionTo(target), false);
                            }
                            else
                            {
                                fire(rc, nearByEnemies3, alliedBots);
                            }
                        }
                        return true;
                    }
                    else if (rc.getLocation().distanceSquaredTo(convertIntToMapLocation(rc.readBroadcast(enemyHQ))) <= 35)
                    {
                        runFromEnemyHQ(rc);
                        return true;
                    }
                    else
                    {
                        return false;
                    }
                }
                else
                {
                    int[] AllEnemyNoiseTowers;
                    int[] AllEnemyBots;

                    if (nearByEnemies3.length > 0)
                    {
                        AllEnemyBots = AllEnemyBots(rc);
                        AllEnemyNoiseTowers = null;
                        FindAndRecordAllEnemies(rc, nearByEnemies3, AllEnemyBots, AllEnemyNoiseTowers);

                    }
                    return true;
                }
            }
            else if (nearByEnemies10.length > 0)
            {
                if (rc.isActive())
                {
                    int hq = 0;
                    for (int i = nearByEnemies10.length; --i>=0;)
                    {
                        if (rc.senseRobotInfo(nearByEnemies10[i]).type != RobotType.HQ && rc.getLocation().distanceSquaredTo(rc.senseLocationOf(nearByEnemies10[i])) <= 10)
                        {
                            //nearByAllies = findUnitsOnTeam(rc, gameObjects, rc.getTeam());
                            nearByAllies = rc.senseNearbyGameObjects(Robot.class, 35, rc.getTeam());
                            nearByAllies = findSoldiers(rc, nearByAllies);
                            MapLocation[] alliedBots = locationOfBots(rc, nearByAllies);
                            fire(rc, nearByEnemies10, alliedBots);
                            return true;
                        }
                    }
                    return false;
                }
            }
        } catch(Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }
}