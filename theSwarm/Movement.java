package theSwarm;

import java.util.Random;

import battlecode.common.*;

public class Movement {
	static Random rand;
	
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

    public static int convertMapLocationToInt(MapLocation loc)
    {
        int x = loc.x;
        int y = loc.y;
        int total = (x*100) + y;
        return total;
    }

    public static MapLocation convertIntToMapLocation(int value)
    {
        int x = value / 100;
        int y = value % 100;
        MapLocation loc = new MapLocation(x, y);
        return loc;
    }

    public static void MoveMapLocation(RobotController rc, MapLocation target, boolean sneak)
    {
        MapLocation[] pastLocations = new MapLocation[10];
        int side = 45;
        Direction dir;
        Direction newDir;
        rand = new Random();
        boolean didNotShoot = false;
        // we initialize pastLocations to hold our current location 5 times
        for (int i = 0; i < pastLocations.length; i++)
        {
            pastLocations[i] = rc.getLocation();
        }

        // this method will run until we get to our target location
        while (!rc.getLocation().equals(target) || rc.getLocation().isAdjacentTo(target))
        {
            Robot[] nearByEnemies = rc.senseNearbyGameObjects(Robot.class, 35, rc.getTeam().opponent());

            if (nearByEnemies.length > 0)
            {
                long[] AllEnemyBots = FightMicro.AllEnemyBots(rc);
                long[] AllEnemyNoiseTowers = FightMicro.AllEnemyNoiseTowers(rc);

                FightMicro.FindAndRecordAllEnemies(rc, nearByEnemies, AllEnemyBots, AllEnemyNoiseTowers);
            }
            // we put the try block inside of the while loop so an exception won't terminate the method
            try
            {
                if (rc.isActive())
                {
                    if (rc.getLocation().isAdjacentTo(target))
                    {
                        if (rc.senseObjectAtLocation(target) != null || rc.senseTerrainTile(target).equals(TerrainTile.VOID))
                        {
                            break;
                        }
                    }

                    dir = rc.getLocation().directionTo(target);
                    newDir = Direction.NONE;

                    /*if (fightMode(rc))
                    {
                    }
                    else*/
                    if (rc.senseNearbyGameObjects(Robot.class, 10, rc.getTeam().opponent() ).length > 0)
                    {
                    	fire(rc);
                    }		
                    else if (rc.getLocation().distanceSquaredTo(rc.senseEnemyHQLocation()) < 30)
                    {
                        MoveDirection(rc, rc.getLocation().directionTo(rc.senseEnemyHQLocation()).opposite(), false);
                    }
                    //
                    // if we can move towards target and we haven't been on the square recently then lets move
                    else if (rc.canMove(dir) && !MapLocationInArray(rc, rc.getLocation().add(dir), pastLocations))
                    {
                        didNotShoot = true;
                        newDir = dir;
                        int distanceToEnemyHQ = rc.getLocation().distanceSquaredTo(rc.senseEnemyHQLocation());
                        // if we found a direction to move then we go to it
                        if (distanceToEnemyHQ < 40)
                        {
                            while (rc.getLocation().add(newDir).distanceSquaredTo(rc.senseEnemyHQLocation()) > distanceToEnemyHQ)
                            {
                                newDir = newDir.rotateLeft();
                            }
                            MoveDirection(rc, newDir, sneak);
                        }
                        else if (newDir != Direction.NONE)
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
                        side = 45;
                    }
                    else
                    {
                        didNotShoot = true;
                        // if their is a robot blocking our way then we just move in a random direction
                        if (rc.senseObjectAtLocation(rc.getLocation().add(dir)) != null)
                        {
                            //newDir = directions[rand.nextInt(8)];
                            MoveDirection(rc, dir, sneak);
                        }
                        else
                        {
                            Direction dir2 = dir;
                            MapLocation right;
                            MapLocation left;
                            dir2 = (dir.rotateRight());
                            while (!rc.canMove(dir2))
                            {
                                dir2 = dir2.rotateRight();
                            }
                            right = rc.getLocation().add(dir2);

                            dir2 = dir.rotateLeft();
                            while (!rc.canMove(dir2))
                            {
                                dir2 = dir2.rotateLeft();
                            }

                            left = rc.getLocation().add(dir2);

                            // left seems better so lets go that way
                            if (left.distanceSquaredTo(target) < right.distanceSquaredTo(target))
                            {
                                side = 1;
                            }
                            // right seems better so lets try that way
                            else
                            {
                                side = 0;
                            }

                            // we will go hugging one side of obstacle until we get back on our original line
                            while (!dir2.equals(dir) && !rc.getLocation().equals(target) && !rc.getLocation().isAdjacentTo(target))// && rc.canMove(dir2))
                            {
                                nearByEnemies = rc.senseNearbyGameObjects(Robot.class, 35, rc.getTeam().opponent());

                                if (nearByEnemies.length > 0)
                                {
                                    long[] AllEnemyBots = FightMicro.AllEnemyBots(rc);
                                    long[] AllEnemyNoiseTowers = FightMicro.AllEnemyNoiseTowers(rc);

                                    FightMicro.FindAndRecordAllEnemies(rc, nearByEnemies, AllEnemyBots, AllEnemyNoiseTowers);
                                }
                                try
                                {
                                    if (rc.isActive())
                                    {

                                        dir2 = rc.getLocation().directionTo(target);


                                        /*if (fightMode(rc))
                                        {

                                        }
                                        else*/ if (rc.getLocation().distanceSquaredTo(rc.senseEnemyHQLocation()) < 30)
                                        {
                                            MoveDirection(rc, rc.getLocation().directionTo(rc.senseEnemyHQLocation()).opposite(), false);
                                        }
                                        else if (rc.canMove(dir2) && !MapLocationInArray(rc, rc.getLocation().add(dir2), pastLocations))//  && !rc.senseTerrainTile(rc.getLocation().add(dir2).add(dir2)).equals(TerrainTile.VOID))
                                        {
                                        }
                                        else
                                        {
                                            for (int i = 0; i < 4; i++)
                                            {
                                                if (side == 1)
                                                {
                                                    dir2 = dir2.rotateLeft();
                                                }
                                                else
                                                {
                                                    dir2 = dir2.rotateRight();
                                                }
                                                if (rc.senseTerrainTile(rc.getLocation().add(dir2)).equals(TerrainTile.OFF_MAP))
                                                {
                                                    dir2 = Direction.NONE;
                                                    i = 48;
                                                }
                                                else if ((rc.canMove(dir2) || (rc.senseObjectAtLocation(rc.getLocation().add(dir2)) != null)))// && !MapLocationInArray(rc, rc.getLocation().add(dir2), pastLocations))// && !rc.senseTerrainTile(rc.getLocation().add(dir2).add(dir2)).equals(TerrainTile.VOID))
                                                {
                                                    i = 48;
                                                }
                                                else if (i == 3)
                                                {
                                                    dir2 = Direction.NONE;
                                                }
                                            }
                                        }

                                        // if we can move
                                        if (dir2 != Direction.NONE)
                                        {
                                            if (rc.isActive())
                                            {
                                                int distanceToEnemyHQ = rc.getLocation().distanceSquaredTo(rc.senseEnemyHQLocation());
                                                // if we found a direction to move then we go to it
                                                if (distanceToEnemyHQ < 40)
                                                {
                                                    while (rc.getLocation().add(newDir).distanceSquaredTo(rc.senseEnemyHQLocation()) > distanceToEnemyHQ)
                                                    {
                                                        if (side == 1)
                                                        {
                                                            newDir = newDir.rotateLeft();
                                                        }
                                                        else
                                                        {
                                                            newDir = newDir.rotateRight();
                                                        }

                                                    }
                                                    MoveDirection(rc, newDir, sneak);
                                                }
                                                else if (rc.canMove(dir2))
                                                {
                                                    if (sneak)
                                                    {
                                                        rc.sneak(dir2);
                                                    }
                                                    else
                                                    {
                                                        rc.move(dir2);
                                                    }
                                                }
                                                else
                                                {
                                                    MoveDirection(rc, dir2, sneak);
                                                }
                                            }
                                        }
                                        else
                                        {
                                            if (side == 1)
                                            {
                                                side = 0;
                                            }
                                            else
                                            {
                                                side = 1;
                                            }
                                        }
                                    }
                                } catch (Exception e)
                                {
                                    // tell the console we through an exception in utility object for debug purposes
                                    e.printStackTrace();
                                    rc.yield();
                                }
                                if (didNotShoot)
                                {
                                    if (!rc.getLocation().equals(pastLocations[(pastLocations.length-1)]))
                                    {
                                        for (int j = 0; j < (pastLocations.length-1); j++)
                                        {
                                            pastLocations[j] = pastLocations[j+1];
                                        }
                                        // stick current local into array
                                        pastLocations[(pastLocations.length-1)] = rc.getLocation();
                                    }
                                }

                                rc.yield();
                            }
                        }
                    }

                    if (didNotShoot)
                    {
                        // now we  shift everything up one in pastLocations
                        if (rc.getLocation() != pastLocations[(pastLocations.length-1)])
                        {
                            for (int j = 0; j < (pastLocations.length-1); j++)
                            {
                                pastLocations[j] = pastLocations[j+1];
                            }
                            // stick current local into array
                            pastLocations[(pastLocations.length-1)] = rc.getLocation();
                        }
                    }
                }
            }
            catch (Exception e)
            {
                // tell the console we through an exception in utility object for debug purposes
                e.printStackTrace();

            }
            rc.yield();
        }
    }

    public static boolean MapLocationOutOfRangeOfEnemies(RobotController rc, Robot[] SeenEnemies, MapLocation location)
    {
        try
        {
            if (!rc.canMove(rc.getLocation().directionTo(location)))
            {
                return false;
            }
            // we loop through all enemies and if any of them are close enough to shoot this spot then we don't move
            for (int i = 0; i < SeenEnemies.length; i++)
            {
                if (rc.senseLocationOf(SeenEnemies[i]).distanceSquaredTo(location) < 11)
                {
                    return false;
                }
            }
            return true;
        } catch(Exception e)
        {
            e.printStackTrace();
        }
        return true;
    }

    public static int AlliesBehindUs(RobotController rc, Robot[] allies, MapLocation target)
    {
        int numbOfAlliesBehind = 0;
        try
        {
            int distanceToTarget = rc.getLocation().distanceSquaredTo(target);

            for (int i = 0; i < allies.length; i++)
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

    public static int AlliesAhead(RobotController rc, Robot[] allies, MapLocation target)
    {
        int numbOfAlliesAhead = 0;
        try
        {
            int distanceToTarget = rc.getLocation().distanceSquaredTo(target);

            for (int i = 0; i < allies.length; i++)
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

    public static boolean MapLocationInRangeOfEnemyHQ(RobotController rc, MapLocation target)
    {
        if (target.distanceSquaredTo(rc.senseEnemyHQLocation()) < 30)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

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
            nearByEnemies3 = findSoldiers(rc, nearByEnemies4);
            nearByEnemies4 = findNonSoldiers(rc, nearByEnemies4);
            nearByEnemies4 = findSoldiersAtDistance(rc, nearByEnemies4, 10);

            // here we only do necessary scans to reduce bitcode usage

            /*if (rc.getHealth() < 20)
            {

                SCV scv = new SCV(rc);
                scv.run();
                return true;

            }
            else*/ if (nearByEnemies3.length > 0)
            {
                nearByAllies = rc.senseNearbyGameObjects(Robot.class, 35, rc.getTeam());
                nearByAllies = findSoldiers(rc, nearByAllies);
                nearbyEnemies = findSoldiersAtDistance(rc, nearByEnemies3, 10);//rc.senseNearbyGameObjects(Robot.class,10,rc.getTeam().opponent());
                if (nearbyEnemies.length > 0)
                {
                }
                else
                {
                    nearByEnemies2 = findSoldiersAtDistance(rc, nearByEnemies3, 24);//rc.senseNearbyGameObjects(Robot.class, 24, rc.getTeam().opponent());
                    //nearByAllies2 = findSoldiersAtDistance(rc, nearByAllies, 10);//rc.senseNearbyGameObjects(Robot.class, 10, rc.getTeam());
                }


                /**
                 * We see an enemy in our range so either we shoot it or we run outside of its range
                 */
                if (nearbyEnemies.length > 0)
                {
                    MapLocation enemySlot = rc.senseLocationOf(nearbyEnemies[0]);

                    //nearByAllies3 = rc.senseNearbyGameObjects(Robot.class, enemySlot, 10, rc.getTeam());
                    //nearByAllies3 = findSoldiers(rc, nearByAllies3);
                    alliesEngaged = AlliesEngaged(rc, nearByEnemies3, nearByAllies);
                    // if there are other bots in range then we should fire
                    if (alliesEngaged)//= nearbyEnemies.length)
                    {
                        fire(rc);
                    }
                    // if our allies haven't gotten to battle and our opponent isn't almost dead yet wait for them to arrive
                    // assuming we can move to a location our enemy can't hit
                    else if ((nearByAllies.length > 0  && (rc.senseRobotInfo(nearbyEnemies[0]).health > 30)))
                    {
                        MapLocation ally = rc.getLocation().add(rc.getLocation().directionTo(rc.senseLocationOf(nearByAllies[0])));
                        if (MapLocationOutOfRangeOfEnemies(rc, nearByEnemies3, ally))
                        {
                            if (MapLocationInRangeOfEnemyHQ(rc, ally))
                            {
                                fire(rc);
                            }
                            else
                            {
                                MoveDirection(rc, rc.getLocation().directionTo(ally), false);
                            }
                        }
                        else
                        {
                            fire(rc);
                        }
                    }
                    // if there are multiple enemies attacking us and we don't have support then we need to get out
                    // of there if possible
                    else if (nearbyEnemies.length > 1)
                    {
                        MapLocation enemy = rc.getLocation().subtract(rc.getLocation().directionTo(rc.senseLocationOf(nearbyEnemies[0])));
                        if (MapLocationOutOfRangeOfEnemies(rc, nearByEnemies3, enemy))
                        {
                            if (!MapLocationInRangeOfEnemyHQ(rc, enemy))
                            {
                                MoveDirection(rc, rc.getLocation().directionTo(enemy), false);
                            }
                            else
                            {
                                fire(rc);
                            }
                        }
                        else
                        {
                            fire(rc);
                        }
                    }
                    else if ((rc.getHealth() >= rc.senseRobotInfo(nearbyEnemies[0]).health))
                    {
                        fire(rc);
                    }
                    // in this case we have lower health than our opponent and will be killed so we should retreat
                    else
                    {
                        MapLocation enemy = rc.getLocation().subtract(rc.getLocation().directionTo(rc.senseLocationOf(nearbyEnemies[0])));
                        if (MapLocationOutOfRangeOfEnemies(rc, nearByEnemies3, enemy))
                        {
                            if (!MapLocationInRangeOfEnemyHQ(rc, enemy))
                            {
                                if (!MapLocationInRangeOfEnemyHQ(rc, enemy))
                                {
                                    MoveDirection(rc, rc.getLocation().directionTo(enemy), false);
                                }
                                else
                                {
                                    fire(rc);
                                }
                            }
                            else
                            {
                                fire(rc);
                            }
                        }
                        else
                        {
                            fire(rc);
                        }
                    }
                }
                // if there is an enemy close to use to where if we move they can hit us but not far away for us to shoot then we stop
                else if (nearByEnemies2.length > 0)//<= (nearByAllies.length + 1) && (rc.getHealth() >= rc.senseRobotInfo(nearByEnemies3[0]).health || nearByAllies.length >= nearByEnemies2.length))
                {
                    MapLocation enemySlot = rc.senseLocationOf(nearByEnemies2[0]);
                    //nearByAllies3 = rc.senseNearbyGameObjects(Robot.class, enemySlot, 10, rc.getTeam());
                    //nearByAllies3 = findSoldiers(rc, nearByAllies3);
                    alliesEngaged = AlliesEngaged(rc, nearByEnemies3, nearByAllies);
                    nearByAllies2 = findSoldiersAtDistance(rc, nearByAllies, 9);
                    GameObject[] nearByAllies4 = findSoldiersAtDistance(rc, nearByAllies, 24);
                    // if our brethern are in the field of action we must join them!
                    if (alliesEngaged)
                    {
                        if (!MapLocationInRangeOfEnemyHQ(rc, enemySlot))
                        {
                            MoveDirection(rc, rc.getLocation().directionTo(enemySlot), false);
                        }
                        else
                        {
                            fire(rc);
                        }
                    }
                    else if (nearByAllies4.length > (nearByEnemies2.length))
                    {
                        MoveDirection(rc, rc.getLocation().directionTo(rc.senseLocationOf(nearByEnemies2[0])), false);
                    }
                    else if (nearByAllies2.length > 0)
                    {
                        MapLocation target = rc.senseLocationOf(nearByAllies2[0]);

                        if (MapLocationOutOfRangeOfEnemies(rc, nearByEnemies2, target))
                        {
                            if (!MapLocationInRangeOfEnemyHQ(rc, target))
                            {
                                MoveDirection(rc, rc.getLocation().directionTo(target), false);
                            }
                            else
                            {
                                fire(rc);
                            }
                        }
                    }
                    else if (nearByEnemies2.length == 1 && rc.getHealth() > (rc.senseRobotInfo(nearByEnemies2[0]).health + 10) && nearByEnemies3.length == 1)
                    {
                        MoveDirection(rc, rc.getLocation().directionTo(rc.senseLocationOf(nearByEnemies2[0])), false);
                    }
                    else
                    {
                        if (nearByEnemies4.length > 0)
                        {
                            Robot[] nearByEnemies5 = findSoldiersAtDistance(rc, nearByEnemies4, 10);
                            if (nearByEnemies5.length > 0)
                            {
                                MapLocation spot = rc.senseLocationOf(nearByEnemies5[0]);
                                if (MapLocationOutOfRangeOfEnemies(rc, nearByEnemies3, spot))
                                {
                                    if (!MapLocationInRangeOfEnemyHQ(rc, spot))
                                    {
                                        MoveDirection(rc, rc.getLocation().directionTo(spot), false);
                                    }
                                    else
                                    {
                                        fire(rc);
                                    }
                                }
                                else
                                {
                                    fire(rc);
                                }
                            }
                            else
                            {
                                fire(rc);
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
                    if (AlliesAhead(rc, nearByAllies, target) > 0)
                    {
                        /*if (!MapLocationInRangeOfEnemyHQ(rc, target))
                        {*/
                            MoveDirection(rc, rc.getLocation().directionTo(target), false);
                        /*}
                        else
                        {
                            fire(rc);
                        }*/
                    }
                    else if (rc.senseNearbyGameObjects(Robot.class,  target, 10, rc.getTeam()).length > 0)
                    {
                        MoveDirection(rc, rc.getLocation().directionTo(target), false);
                    }
                    // if we see enemy pastrs then kill them!
                    else if (nearByEnemies4.length > 0)
                    {
                        Robot[] nearByEnemies5 = findSoldiersAtDistance(rc, nearByEnemies4, 10);
                        if (nearByEnemies5.length > 0)
                        {
                            fire(rc);
                        }
                        else
                        {
                            MapLocation targeter = rc.senseLocationOf(nearByEnemies4[0]);
                            if (!MapLocationInRangeOfEnemyHQ(rc, targeter))
                            {
                                MoveDirection(rc, rc.getLocation().directionTo(targeter), false);
                            }
                            else
                            {
                                fire(rc);
                            }
                        }
                    }
                    // if there are allies coming up then wait for them
                    else if (AlliesBehindUs(rc, nearByAllies, target) > 0)
                    {
                        fire(rc);
                    }
                    // if our enemies have higher health than us also wait
                    else if (rc.senseRobotInfo(nearByEnemies3[0]).health > rc.getHealth())
                    {
                        fire(rc);
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
                            fire(rc);
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
                    MoveDirection(rc, rc.getLocation().directionTo(target2), false);
                }
                else
                {
                    fire(rc);
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

    public static boolean AlliesEngaged(RobotController rc, GameObject[] enemies, GameObject[] allies)
    {
        boolean alliesEngaged = false;
        try
        {
            for (int i = 0; i < enemies.length; i++)
            {
                for (int j = 0; j < allies.length; j++)
                {
                    if (rc.senseLocationOf(enemies[i]).distanceSquaredTo(rc.senseLocationOf(allies[j])) <= 10)
                    {
                        alliesEngaged = true;
                    }
                }
            }
        } catch(Exception e){
            e.printStackTrace();
        }

        return alliesEngaged;
    }

    // this method returns true if a MapLocation is inside of an array false otherwise
    public static boolean MapLocationInArray(RobotController rc, MapLocation target, MapLocation[] array)
    {

        for (int i = 0; i < array.length; i++)
        {
            if (array[i].equals(target))
            {
                return true;
            }
        }

        return false;
    }

    public static void fire(RobotController rc)
    {
        int radius;

        try
        {
            if(rc.getType() == RobotType.HQ)
            {
                radius = 15;
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
                            if(rc.senseObjectAtLocation(loc.add(dirs[a])).getTeam() == rc.getTeam().opponent())
                            {
                                value++;
                            }
                            else if(rc.senseObjectAtLocation(loc.add(dirs[a])).getTeam() == rc.getTeam())
                            {
                                value--;
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
                    if (rc.senseRobotInfo(target).health <= 50)
                    {
                        long[] AllEnemyBots = FightMicro.AllEnemyBots(rc);

                        FightMicro.recordEnemyBotKilled(rc, AllEnemyBots, target);

                    }
                    rc.attackSquare(rc.senseRobotInfo(target).location);
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
                                        rc.setIndicatorString(0, "Error");
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

                    if (rc.canAttackSquare(location))
                    {
                        rc.attackSquare(location);
                    }
                }
            }
            else
            {
                radius = 10;
                Robot[] enemies = rc.senseNearbyGameObjects(Robot.class, radius, rc.getTeam().opponent());
                Robot target = null;

                for(int k = 0; k < enemies.length; k++)
                {
                    if(target == null)
                    {
                        target = enemies[k];
                    }
                    else if(rc.senseRobotInfo(enemies[k]).health < rc.senseRobotInfo(target).health)
                    {
                        target = enemies[k];
                    }
                }

                if(target != null)
                {
                    if (rc.senseRobotInfo(target).health <= 10)
                    {
                        if (rc.senseRobotInfo(target).type == RobotType.SOLDIER)
                        {
                            long[] AllEnemyBots = FightMicro.AllEnemyBots(rc);
                            FightMicro.recordEnemyBotKilled(rc, AllEnemyBots, target);

                        }
                        else if (rc.senseRobotInfo(target).type == RobotType.NOISETOWER)
                        {
                            long[] AllEnemyNoiseTowers = FightMicro.AllEnemyNoiseTowers(rc);
                            FightMicro.recordEnemyBotKilled(rc, AllEnemyNoiseTowers, target);
                        }
                    }
                    rc.attackSquare(rc.senseRobotInfo(target).location);
                }
            }
        }
        catch(Exception e){
            rc.setIndicatorString(0, "Error 2");
        	e.printStackTrace();
        }
    }

}
