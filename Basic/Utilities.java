package Basic;

/**
 * Created by fredkneeland on 1/7/14.
 */
import java.util.Random;

import battlecode.common.*;
import battlecode.world.GameMap;

public class Utilities
{
    static Direction[] directions = {Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST, Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST};
    static Random rand;
    static boolean goneRight = false;

    // channels for communication
    static final int EnemyHQChannel = 0;
    static final int OurHQChannel = 1;
    static final int TroopType = 2;
    static final int GhostNumb = 3;
    static final int GoliathOnline = 4;
    static final int GhostReady = 5;
    static final int BattleCruiserLoc = 6;
    static final int BattleCruiserLoc2 = 7;
    static final int BattleCruiserArrived = 8;
    static final int startBattleCruiserArray = 9;
    static final int endBattleCruiserArray = 59;
    static final int BattleCruiserInArray = 60;

    public static boolean BattleCruiserReady(RobotController rc)
    {
        try
        {
            if (rc.readBroadcast(BattleCruiserArrived) == 1)
            {
                return true;
            }

            return false;
        } catch (Exception e)
        {
            e.printStackTrace();
            System.out.println("Utility Exception");
        }
        return false;
    }

    public static void SpawnSoldiers(RobotController rc)
    {
        try
        {
            if (rc.isActive() && rc.getType() == RobotType.HQ)
            {
                Direction toEnemy = rc.getLocation().directionTo(rc.senseEnemyHQLocation());
                if (rc.senseObjectAtLocation(rc.getLocation().add(toEnemy)) == null) {}
                else
                {
                    for (int i = 0; i < 7; i++)
                    {
                        toEnemy = toEnemy.rotateLeft();

                        if (rc.senseObjectAtLocation(rc.getLocation().add(toEnemy)) == null)
                        {
                            i = 47;
                        }
                        else if (i == 6)
                        {
                            toEnemy = Direction.NONE;
                        }
                    }
                }

                if (toEnemy != Direction.NONE)
                {
                    if (rc.isActive())
                    {
                        if (rc.getType() == RobotType.HQ)
                        {
                            rc.spawn(toEnemy);
                        }
                    }
                }
            }
        } catch (Exception e)
        {
        	e.printStackTrace();
            System.out.println("Utility Exception");
        }
    }

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
                    if (!rc.canMove(dir))
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
                            if (rc.canMove(newDir))
                            {
                                i = 48;
                            }
                            // if we have gone through all our options and can't move then we will wait
                            else if (i == 5 && !rc.canMove(newDir))
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
            System.out.println("Utility Exception");
            e.printStackTrace();
            //System.out.println(newDir);
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
            System.out.println("Utility Exception");
            e.printStackTrace();
            //System.out.println(newDir);
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
            System.out.println("Utility Exception");
            e.printStackTrace();
            //System.out.println(newDir);
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
            System.out.println("Utility Exception");
            e.printStackTrace();
            //System.out.println(newDir);
        }
        return null;
    }

    public static int convertMapLocationToInt(MapLocation loc)
    {
        int x = loc.x;
        int y = loc.y;
        int total = (x*1000) + y;
        return total;
    }

    public static MapLocation convertIntToMapLocation(int value)
    {
        int x = value / 1000;
        int y = value % 1000;
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
        while (!rc.getLocation().equals(target))
        {
            // we put the try block inside of the while loop so an exception won't terminate the method
            try
            {
                if (rc.isActive())
                {

                    dir = rc.getLocation().directionTo(target);
                    newDir = Direction.NONE;

                    if (fightMode(rc))
                    {
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
                                    //System.out.println(newDir);
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
                            rc.setIndicatorString(2, "Looking elswhere");
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
                            while (!dir2.equals(dir) && !rc.getLocation().equals(target))// && rc.canMove(dir2))
                            {
                                try
                                {
                                    if (rc.isActive())
                                    {

                                        dir2 = rc.getLocation().directionTo(target);


                                        if (fightMode(rc))
                                        {

                                        }
                                        else if (rc.getLocation().distanceSquaredTo(rc.senseEnemyHQLocation()) < 30)
                                        {
                                            MoveDirection(rc, rc.getLocation().directionTo(rc.senseEnemyHQLocation()).opposite(), false);
                                        }
                                        else if (rc.canMove(dir2) && !MapLocationInArray(rc, rc.getLocation().add(dir2), pastLocations))//  && !rc.senseTerrainTile(rc.getLocation().add(dir2).add(dir2)).equals(TerrainTile.VOID))
                                        {
                                            //rc.setIndicatorString(0, "Going straight");
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
                                    //rc.setIndicatorString(0, "Dir: "+ dir +" Dir2: " + dir2);
                                } catch (Exception e)
                                {
                                    // tell the console we through an exception in utility object for debug purposes
                                    //System.out.println("Utility Exception");
                                    //System.out.println(e.toString());
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
                                            //System.out.println(pastLocations[j]);
                                        }
                                        // stick current local into array
                                        pastLocations[(pastLocations.length-1)] = rc.getLocation();
                                    }
                                }

                                rc.yield();
                            }
                            //rc.setIndicatorString(1, "Not trying to Avoid");
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
                                //System.out.println(pastLocations[j]);
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
                System.out.println("Utility Exception");
                e.printStackTrace();
                //System.out.println(e.toString());

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


            // simple shoot at an enemy if we see one will need to be improved later
            nearByEnemies3 = rc.senseNearbyGameObjects(Robot.class, 35, rc.getTeam().opponent());
            nearByEnemies4 = nearByEnemies3;
            nearByEnemies3 = findSoldiers(rc, nearByEnemies4);
            nearByEnemies4 = findNonSoldiers(rc, nearByEnemies4);

            // here we only do necessary scans to reduce bitcode usage
            if (nearByEnemies3.length > 0)
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

                    nearByAllies3 = rc.senseNearbyGameObjects(Robot.class, enemySlot, 10, rc.getTeam());
                    // if there are other bots in range then we should fire
                    if (nearByAllies3.length > 0)//= nearbyEnemies.length)
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
                                Utilities.MoveDirection(rc, rc.getLocation().directionTo(ally), false);
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
                                Utilities.MoveDirection(rc, rc.getLocation().directionTo(enemy), false);
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
                                    Utilities.MoveDirection(rc, rc.getLocation().directionTo(enemy), false);
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
                    nearByAllies3 = rc.senseNearbyGameObjects(Robot.class, enemySlot, 10, rc.getTeam());
                    nearByAllies2 = findSoldiersAtDistance(rc, nearByAllies, 9);
                    // if our brethern are in the field of action we must join them!
                    if (nearByAllies3.length > 0)
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
                    else if (nearByAllies2.length > 0)
                    {
                        MapLocation target = rc.senseLocationOf(nearByAllies2[0]);

                        if (MapLocationOutOfRangeOfEnemies(rc, nearByEnemies2, target))
                        {
                            if (!MapLocationInRangeOfEnemyHQ(rc, target))
                            {
                                Utilities.MoveDirection(rc, rc.getLocation().directionTo(target), false);
                            }
                            else
                            {
                                fire(rc);
                            }
                        }
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
                                        Utilities.MoveDirection(rc, rc.getLocation().directionTo(spot), false);
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
                        if (!MapLocationInRangeOfEnemyHQ(rc, target))
                        {
                            Utilities.MoveDirection(rc, rc.getLocation().directionTo(target), false);
                        }
                        else
                        {
                            fire(rc);
                        }
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
                                Utilities.MoveDirection(rc, rc.getLocation().directionTo(targeter), false);
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
                            Utilities.MoveDirection(rc, rc.getLocation().directionTo(target), false);
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
                    for(int a = 0; a < 8; a++)
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

                    rc.setIndicatorString(0, "" + value);

                    if(value > maxValue)
                    {
                        maxValue = value;
                        target = enemies[k];
                    }
                }

                if(target != null)
                {
                    rc.attackSquare(rc.senseRobotInfo(target).location);
                }
                else if (enemies2.length > 0)
                {
                    MapLocation location = null;
                    maxValue = 0;
                    for (int j = 0; j < enemies2.length; j++)
                    {

                        int value = 0;
                        MapLocation loc = rc.senseRobotInfo(enemies2[j]).location;
                        loc = loc.subtract(rc.getLocation().directionTo(loc));
                        for (int k = 0; k < 8; k++)
                        {
                            try
                            {
                                if(rc.senseObjectAtLocation(loc.add(dirs[k])).getTeam() == rc.getTeam().opponent())
                                {
                                    value++;
                                }
                                else if(rc.senseObjectAtLocation(loc.add(dirs[k])).getTeam() == rc.getTeam())
                                {
                                    value--;
                                }
                            }
                            catch(Exception e)
                            {
                                e.printStackTrace();
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
                    rc.attackSquare(rc.senseRobotInfo(target).location);
                }
            }
        }
        catch(Exception e){
        	e.printStackTrace();
        }
    }

    // this method will advance one square towards a target and try to avoid enemies as much as possible
    // this method doesn't work right now
    public static void avoidEnemiesMove(RobotController rc, MapLocation target)
    {
        try
        {
            // first we will find all enemy bots near us
            GameObject[] nearByBots = rc.senseNearbyGameObjects(Robot.class, 15, rc.getTeam().opponent());
            Direction direction = rc.getLocation().directionTo(target);

            // if we don't see anything then lets head towards target
            if (nearByBots.length == 0)
            {
                rc.setIndicatorString(2, "No enemies detected");
                rc.setIndicatorString(1, "x: "+target.x + " y: " + target.y);
                direction = rc.getLocation().directionTo(target);
                if (rc.canMove(direction))// && !rc.senseTerrainTile(rc.getLocation().add(direction).add(direction)).equals(TerrainTile.VOID))
                {
                    if (rc.isActive())
                    {
                        rc.move(direction);
                    }
                }
                else
                {
                	MapLocation target2 = rc.getLocation().add(direction);
                	if (rc.senseTerrainTile(target2).equals(TerrainTile.VOID))
                	{
                		int j = 0;
                		while (rc.senseTerrainTile(target2).equals(TerrainTile.VOID))
                		{
                			rc.setIndicatorString(0, ""+j);
                			j++;
                			
                			target2 = target2.add(direction);
                		}
                		Utilities.MoveMapLocation(rc, target2, false);
                	}
                	/*
                	int distanceRight = 0;
                	int distanceLeft = 0;
                    direction = direction.rotateRight();
                    while (!rc.canMove(direction) && rc.senseTerrainTile(rc.getLocation().add(direction)).equals(TerrainTile.VOID))
                    {
                        direction = direction.rotateRight();
                    }
                    if (rc.isActive())
                    {
                        if (rc.canMove(direction))
                        {
                            rc.move(direction);
                        }
                    }
                    */
                }
            }
            // otherwise we need to avoid them
            else
            {
                rc.setIndicatorString(2, "Avoiding enemies");
                rc.setIndicatorString(1, "Numb of Enemies: "+nearByBots.length);
                // now we will calculate the distance form all 5 spots towards are target and the distance from that spot to all enemies we can see
                // we will pick the one with the greatest distance
                int[] distancesToLocations = new int[5];

                for (int k = 0; k < distancesToLocations.length; k++)
                {
                    distancesToLocations[k] = 0;
                }
                MapLocation spot;
                Direction newDir;

                // first we look 90 to our right
                newDir = direction.rotateRight().rotateRight();
                for (int j = 0; j < 5; j++)
                {
                    if (rc.canMove(newDir))
                    {
                        spot = rc.getLocation().add(newDir);
                        for (int i = 0; i < nearByBots.length; i++)
                        {
                            //System.out.println("entering for loop");
                            distancesToLocations[j] += spot.distanceSquaredTo(rc.senseLocationOf(nearByBots[i]));
                        }
                    }
                    else
                    {
                        distancesToLocations[j] = -123;
                    }
                    // every time through the loop we look one further to the left
                    newDir.rotateLeft();
                }

                int indexOfLargest = 0;
                int largest = distancesToLocations[0];
                for (int j = 1; j < distancesToLocations.length; j++)
                {
                    if (largest < distancesToLocations[j])
                    {
                        indexOfLargest = j;
                        largest = distancesToLocations[j];
                    }
                }

                // now we orientate newDir to the right spot
                newDir = direction.rotateRight().rotateRight();
                for (int i = 0; i <= indexOfLargest; i++)
                {
                    newDir = newDir.rotateLeft();
                }

                while (!rc.isActive())
                {
                    rc.yield();
                }

                // now we can finally move
                if (rc.isActive())
                {
                    if (rc.canMove(newDir))
                    {
                        rc.move(newDir);
                    }
                }
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }
/*
    public static boolean turnNuke(RobotController rc)
    {
        boolean nuke = false;

        GameObject[] nearByEnemies = rc.senseNearbyGameObjects(Robot.class, 35, rc.getTeam().opponent());
        GameObject[] nearByFriends;

        if (nearByEnemies.length == 0)
        {

        }
        else
        {
            MapLocation[] nearBySpots = new MapLocation[8];

            Direction dir = rc.getLocation().directionTo(rc.senseHQLocation());

            for (int i = 0; i < nearBySpots.length; i++)
            {
                nearBySpots[i] = rc.getLocation().add(dir);
                dir.rotateLeft();
            }

            int[] damage = new int[8];

            for (int i = 0; i < damage.length; i++)
            {
                nearByEnemies = rc.senseNearbyGameObjects(Robot.class, nearBySpots[i], 2, rc.getTeam().opponent());
                nearByFriends = rc.senseNearbyGameObjects(Robot.class, nearBySpots[i], 2, rc.getTeam());

                int total = nearByEnemies.length - nearByFriends.length;
                damage[i] = total;
            }

            int largest = damage[0];
            int index = 0;

            for (int k = 1; k < damage.length; k++)
            {
                if (largest < damage[k])
                {
                    largest = damage[k];
                    index = k;
                }
            }

            if (largest > 1)
            {
                Nuke nuker = new Nuke(rc, nearBySpots[index]);
                nuker.run();
                return true;
            }
            else
            {
                return false;
            }


        }
        return nuke;
    }

    //finds best corner to collect milk where the return is an int as follows:
    //1  2
    //3  4
     */
    public static int findBestCorner(RobotController rc)
    {
        double[][] pasture = rc.senseCowGrowth();

        double max = 0;
        int corner = 0;
        double total = 0;

        //top left corner
        for(int k = 0; k < 10; k++)
        {
            for(int a = 0; a < 10; a++)
            {
                total += pasture[k][a];
            }
        }
        if(total > max)
        {
            max = total;
            corner = 1;
        }
        total = 0;

        //top right corner
        for(int k = rc.getMapWidth() - 11; k < rc.getMapWidth(); k++)
        {
            for(int a = 0; a < 10; a++)
            {
                total += pasture[k][a];
            }
        }
        if(total > max)
        {
            max = total;
            corner = 2;
        }
        total = 0;

        //bottom left corner
        for(int k = 0; k < 10; k++)
        {
            for(int a = rc.getMapHeight() - 11; a < rc.getMapHeight(); a++)
            {
                total += pasture[k][a];
            }
        }
        if(total > max)
        {
            max = total;
            corner = 3;
        }
        total = 0;

        //bottom right corner
        for(int k = rc.getMapWidth() - 11; k < rc.getMapWidth(); k++)
        {
            for(int a = rc.getMapHeight() - 11; a < rc.getMapHeight(); a++)
            {
                total += pasture[k][a];
            }
        }
        if(total > max)
        {
            max = total;
            corner = 4;
        }

        return corner;
    }

    // this method is the same as MoveMapLocation until an enemy bot is seen where the robot will move in the opposite direction of the closest enemy it sees that is a soldier
    public static void AvoidEnemiesMoveMapLocation(RobotController rc, MapLocation target, boolean sneak)
    {
        MapLocation[] pastLocations = new MapLocation[10];
        int side = 45;
        Direction dir;
        Direction newDir;
        rand = new Random();
        boolean enemySeen = false;
        // we initialize pastLocations to hold our current location 5 times
        for (int i = 0; i < pastLocations.length; i++)
        {
            pastLocations[i] = rc.getLocation();
        }

        // this method will run until we get to our target location
        while (!rc.getLocation().equals(target))
        {
            // we put the try block inside of the while loop so an exception won't terminate the method
            try
            {
                if (rc.isActive())
                {
                    dir = rc.getLocation().directionTo(target);
                    newDir = Direction.NONE;


                    // we will look and move away from the closest enemy bot we can see

                    Robot[] nearbyEnemies = rc.senseNearbyGameObjects(Robot.class,35,rc.getTeam().opponent());

                    Robot[][] arrayOfNearByEnemies = new Robot[1][1];
                    arrayOfNearByEnemies[0][0] = null;

                    // now we will put all close enemies into a two dimensional array if there are any
                    if (nearbyEnemies.length > 0)
                    {
                        arrayOfNearByEnemies = new Robot[6][nearbyEnemies.length];

                        for (int i = 0; i < 6; i++)
                        {
                            nearbyEnemies = rc.senseNearbyGameObjects(Robot.class,(35 - (5*i)),rc.getTeam().opponent());
                            for (int j = 0; j < nearbyEnemies.length; j++)
                            {
                                // if this level has a robot then we stick it into the array
                                if (nearbyEnemies.length > j)
                                {
                                    rc.setIndicatorString(2, "Analyzing enemies");
                                    arrayOfNearByEnemies[i][j] = nearbyEnemies[j];
                                }
                                // otherwise we put null into the array
                                else
                                {
                                    arrayOfNearByEnemies[i][j] = null;
                                }
                            }

                        }
                    }

                    enemySeen = false;
                    //rc.setIndicatorString(2, "Done Analyzing");

                    // if there are enemies nearby do this code
                    if (arrayOfNearByEnemies[0][0] != null)
                    {
                        enemySeen = true;
                        rc.setIndicatorString(2, "Time to Run");

                        Robot enemy;
                        // we will start with the closest enemy bots and work our way out
                        for (int k = 5; k >= 0; k--)
                        {
                            //rc.setIndicatorString(0, "Entering for loop");
                            enemy = arrayOfNearByEnemies[k][0];

                            // if there is actually an enemy here then we need to do something about it
                            if (enemy != null)
                            {
                                rc.setIndicatorString(1, "See real enemy");
                                // if we can one shot kill the enemy then we do else we run away
                                if ((rc.senseRobotInfo((Robot) nearbyEnemies[0]).health > 10) && rc.senseRobotInfo((Robot) nearbyEnemies[0]).type == RobotType.SOLDIER)
                                {
                                    newDir = rc.getLocation().directionTo(rc.senseLocationOf(nearbyEnemies[0])).opposite();
                                    MoveDirection(rc, newDir, false);
                                }
                                else
                                {
                                    if (rc.canAttackSquare(rc.senseLocationOf(nearbyEnemies[0])))
                                    {
                                        fire(rc);
                                    }
                                    // if the weak enemy or non soldier enemy is too far away to shoot move towards it
                                    else
                                    {
                                        newDir = rc.getLocation().directionTo(rc.senseLocationOf(nearbyEnemies[0]));
                                        MoveDirection(rc, newDir, false);
                                    }
                                }
                                // we have done our action and should terminate the loop
                                k = -1;
                            }
                        }
                    }
                    // if we can move towards target and we haven't been on the square recently then lets move
                    else if (rc.canMove(dir) && !MapLocationInArray(rc, rc.getLocation().add(dir), pastLocations))
                    {
                        rc.setIndicatorString(2, "No enemies detected");
                        newDir = dir;
                        // if we found a direction to move then we go to it
                        if (newDir != Direction.NONE)
                        {
                            // now we decide if we are going to sneak or run
                            if (sneak)
                            {
                                // another check to make sure we don't throw any exceptions
                                if (rc.isActive() && rc.canMove(newDir))
                                {
                                    //System.out.println(newDir);
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
                        // if their is a robot blocking our way then we just move as close as we can to target
                        if (rc.senseObjectAtLocation(rc.getLocation().add(dir)) != null)
                        {
                            //newDir = directions[rand.nextInt(8)];
                            MoveDirection(rc, dir, sneak);
                        }
                        else
                        {
                            rc.setIndicatorString(2, "Looking elswhere");
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
                            while (!dir2.equals(dir) && !rc.getLocation().equals(target))// && rc.canMove(dir2))
                            {
                                try
                                {
                                    if (rc.isActive())
                                    {
                                        //rc.setIndicatorString(1, "Trying to Avoid");
                                        //rc.setIndicatorString(2, ""+side);

                                        nearbyEnemies = rc.senseNearbyGameObjects(Robot.class,35,rc.getTeam().opponent());

                                        arrayOfNearByEnemies = new Robot[1][1];
                                        arrayOfNearByEnemies[0][0] = null;

                                        // now we will put all close enemies into a two dimensional array if there are any
                                        if (nearbyEnemies.length > 0)
                                        {
                                            arrayOfNearByEnemies = new Robot[6][nearbyEnemies.length];

                                            for (int i = 0; i < 6; i++)
                                            {
                                                nearbyEnemies = rc.senseNearbyGameObjects(Robot.class,(35 - (5*i)),rc.getTeam().opponent());
                                                for (int j = 0; j < nearbyEnemies.length; j++)
                                                {
                                                    // if this level has a robot then we stick it into the array
                                                    if (nearbyEnemies.length > j)
                                                    {
                                                        rc.setIndicatorString(2, "Analyzing enemies");
                                                        arrayOfNearByEnemies[i][j] = nearbyEnemies[j];
                                                    }
                                                    // otherwise we put null into the array
                                                    else
                                                    {
                                                        arrayOfNearByEnemies[i][j] = null;
                                                    }
                                                }

                                            }
                                        }

                                        // if there are enemies nearby do this code
                                        if (arrayOfNearByEnemies[0][0] != null)
                                        {
                                            enemySeen = true;
                                            rc.setIndicatorString(2, "Time to Run");

                                            Robot enemy;
                                            // we will start with the closest enemy bots and work our way out
                                            for (int k = 5; k >= 0; k--)
                                            {
                                                //rc.setIndicatorString(0, "Entering for loop");
                                                enemy = arrayOfNearByEnemies[k][0];

                                                // if there is actually an enemy here then we need to do something about it
                                                if (enemy != null)
                                                {
                                                    rc.setIndicatorString(1, "See real enemy");
                                                    // if we can one shot kill the enemy then we do else we run away
                                                    if ((rc.senseRobotInfo((Robot) nearbyEnemies[0]).health > 10) && rc.senseRobotInfo((Robot) nearbyEnemies[0]).type == RobotType.SOLDIER)
                                                    {
                                                        newDir = rc.getLocation().directionTo(rc.senseLocationOf(nearbyEnemies[0])).opposite();
                                                        MoveDirection(rc, newDir, false);
                                                    }
                                                    else
                                                    {
                                                        if (rc.canAttackSquare(rc.senseLocationOf(nearbyEnemies[0])))
                                                        {
                                                            fire(rc);
                                                        }
                                                        // if the weak enemy or non soldier enemy is too far away to shoot move towards it
                                                        else
                                                        {
                                                            newDir = rc.getLocation().directionTo(rc.senseLocationOf(nearbyEnemies[0]));
                                                            MoveDirection(rc, newDir, false);
                                                        }
                                                    }
                                                    // we have done our action and should terminate the loop
                                                    k = -1;
                                                }
                                            }
                                        }
                                        // otherwise we just do our thing
                                        else
                                        {
                                            enemySeen = false;
                                            dir2 = rc.getLocation().directionTo(target);
                                            if (rc.canMove(dir2) && !MapLocationInArray(rc, rc.getLocation().add(dir2), pastLocations))//  && !rc.senseTerrainTile(rc.getLocation().add(dir2).add(dir2)).equals(TerrainTile.VOID))
                                            {
                                                //rc.setIndicatorString(0, "Going straight");
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
                                                        //rc.setIndicatorString(1, "We failed to find a spot");
                                                    }
                                                }
                                            }

                                            // if we can move
                                            if (dir2 != Direction.NONE)
                                            {
                                                if (rc.isActive())
                                                {
                                                    if (rc.canMove(dir2))
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
                                    }
                                    ///rc.setIndicatorString(0, "Dir: "+ dir +" Dir2: " + dir2);
                                } catch (Exception e)
                                {
                                    rc.setIndicatorString(0, "Catching Errors");
                                    // tell the console we through an exception in utility object for debug purposes
                                    //System.out.println("Utility Exception");
                                    //System.out.println(e.toString());
                                    e.printStackTrace();
                                    rc.yield();
                                }
                                if (!rc.getLocation().equals(pastLocations[(pastLocations.length-1)]) && !enemySeen)
                                {
                                    for (int j = 0; j < (pastLocations.length-1); j++)
                                    {
                                        pastLocations[j] = pastLocations[j+1];
                                        //System.out.println(pastLocations[j]);
                                    }
                                    // stick current local into array
                                    pastLocations[(pastLocations.length-1)] = rc.getLocation();
                                }

                                rc.yield();
                            }
                            //rc.setIndicatorString(1, "Not trying to Avoid");
                        }
                    }

                    // now we  shift everything up one in pastLocations
                    if (rc.getLocation() != pastLocations[(pastLocations.length-1)])
                    {
                        for (int j = 0; j < (pastLocations.length-1); j++)
                        {
                            pastLocations[j] = pastLocations[j+1];
                            //System.out.println(pastLocations[j]);
                        }
                        // stick current local into array
                        pastLocations[(pastLocations.length-1)] = rc.getLocation();
                    }
                }
                rc.yield();
            }
            catch (Exception e)
            {
                rc.setIndicatorString(0, "Catching Errors");
                // tell the console we through an exception in utility object for debug purposes
                System.out.println("Utility Exception");
                e.printStackTrace();
                //System.out.println(e.toString());
                rc.yield();
            }
        }
    }

    public static MapLocation spotOfSensorTower(RobotController rc)
    {
        rand = new Random();
        MapLocation target = null;

        int corner = Utilities.findBestCorner(rc);

        switch(corner)
        {
            case 1:
                target = new MapLocation(5, 5);
                break;
            case 2:
                target = new MapLocation(rc.getMapWidth() - 6, 5);
                break;
            case 3:
                target = new MapLocation(5, rc.getMapHeight() - 6);
                break;
            default:
                target = new MapLocation(rc.getMapWidth() - 6, rc.getMapHeight() - 6);
                break;
        }

        Direction dir = directions[rand.nextInt(8)];
        // make sure we don't try to build on a void space
        while (rc.senseTerrainTile(target).equals(TerrainTile.VOID))
        {
            target = target.add(dir);
            dir = directions[rand.nextInt(8)];
        }

        return target;
    }

    public static MapLocation spotOfPastr(RobotController rc)
    {
        MapLocation target;
        int[] lookPlaces = {1,1,0,3,6,7,4,5,2,3,0,2,2,3,1,4,5,3,2,5,6};
        int counter = 0;
        Direction dir;
        int corner = findBestCorner(rc);
        rand = new Random();
        switch(corner)
        {
            case 1:
                target = new MapLocation(2, 2);
                break;
            case 2:
                target = new MapLocation(rc.getMapWidth() - 3, 2);
                break;
            case 3:
                target = new MapLocation(2, rc.getMapHeight() - 3);
                break;
            default:
                target = new MapLocation(rc.getMapWidth() - 3, rc.getMapHeight() - 3);
                break;
        }

        while (rc.senseTerrainTile(target).equals(TerrainTile.VOID))
        {

            dir = directions[rand.nextInt(8)];
            target = target.add(dir);
            counter++;
        }
        return target;
    }

    public static MapLocation determineBestWayAround(RobotController rc, MapLocation target)
    {
        MapLocation[] leftSide = new MapLocation[50];
        MapLocation[] rightSide = new MapLocation[50];
        try
        {
            int counter = 0;
            int rightCounter = 0;
            MapLocation currentLoc = rc.getLocation();
            Direction direction;
            while (!currentLoc.equals(target) && counter < 50)
            {
                direction = currentLoc.directionTo(target);
                while (rc.senseTerrainTile(currentLoc.add(direction)).equals(TerrainTile.VOID))
                {
                    direction = direction.rotateLeft();
                }
                currentLoc = currentLoc.add(direction);
                leftSide[counter] = currentLoc;

                counter++;
            }

            currentLoc = rc.getLocation();
            while (!currentLoc.equals(target) && rightCounter < 50)
            {
                direction = currentLoc.directionTo(target);
                while (rc.senseTerrainTile(currentLoc.add(direction)).equals(TerrainTile.VOID))
                {
                    direction = direction.rotateRight();
                }
                currentLoc = currentLoc.add(direction);
                leftSide[rightCounter] = currentLoc;

                rightCounter++;
            }

            if (counter < rightCounter)
            {
                int j = 1;
                while (j < 50 && leftSide[j] != null)
                {
                    rc.broadcast(startBattleCruiserArray+j, convertMapLocationToInt(leftSide[j]));
                    j++;
                }
                while (j < 50)
                {
                    rc.broadcast(startBattleCruiserArray+j, -5);
                }
                return leftSide[0];
            }
            int j = 1;
            while (j < 50 && rightSide[j] != null)
            {
                rc.broadcast(startBattleCruiserArray+j, convertMapLocationToInt(rightSide[j]));
                j++;
            }
            while (j < 50)
            {
                rc.broadcast(startBattleCruiserArray+j, -5);
            }
            return rightSide[0];

        } catch (Exception e)
        {
            e.printStackTrace();
            rc.yield();
        }

        return rightSide[0];
    }

    public static void BattleCruiserMovement(RobotController rc)
    {
        try
        {
            boolean amLeader = false;
            MapLocation target = null;
            int currentChannel;
            Direction direction;
            MapLocation enemyHQ = rc.senseEnemyHQLocation();

            // we alternate between two channels based on the round number
            if (Clock.getRoundNum() % 2 == 0)
            {
                currentChannel = BattleCruiserLoc;
                if (rc.readBroadcast(BattleCruiserLoc) == 0)
                {
                    amLeader = true;
                    rc.broadcast(BattleCruiserLoc2, 0);
                }
                else
                {
                    target = convertIntToMapLocation(rc.readBroadcast(BattleCruiserLoc));
                }
            }
            else
            {
                currentChannel = BattleCruiserLoc2;
                if (rc.readBroadcast(BattleCruiserLoc2) == 0)
                {
                    amLeader = true;
                    rc.broadcast(BattleCruiserLoc, 0);
                }
                else
                {
                    target = convertIntToMapLocation(rc.readBroadcast(BattleCruiserLoc2));
                }
            }

            if (amLeader)
            {
                rc.setIndicatorString(2, "Leader");
                if (fightMode(rc))
                {
                    rc.broadcast(currentChannel, convertMapLocationToInt(rc.getLocation()));
                }
                else
                {
                    direction = rc.getLocation().directionTo(enemyHQ);
                    if (rc.readBroadcast(BattleCruiserInArray) == 5)
                    {
                        int j = startBattleCruiserArray;
                        while (rc.readBroadcast(j) == -5 && j < endBattleCruiserArray)
                        {
                            j++;
                        }
                        if (j == endBattleCruiserArray)
                        {
                            rc.broadcast(BattleCruiserInArray, 0);
                            rc.broadcast(currentChannel, convertMapLocationToInt(rc.getLocation()));
                        }
                        else
                        {
                            MapLocation spot = convertIntToMapLocation(rc.readBroadcast(j));
                            rc.broadcast(currentChannel, convertMapLocationToInt(spot));
                            rc.broadcast(j, -5);
                            MoveDirection(rc, rc.getLocation().directionTo(spot), false);
                        }
                    }
                    if (rc.senseTerrainTile(rc.getLocation().add(direction)).equals(TerrainTile.VOID))
                    {
                        MapLocation spot = rc.getLocation().add(direction);
                        while (rc.senseTerrainTile(spot).equals(TerrainTile.VOID))
                        {
                            spot.add(direction);
                        }
                        spot = determineBestWayAround(rc, spot);
                        rc.broadcast(currentChannel, convertMapLocationToInt(spot));
                        rc.broadcast(BattleCruiserInArray, 5);
                        MoveDirection(rc, rc.getLocation().directionTo(spot), false);
                    }
                    else
                    {
                        rc.broadcast(currentChannel, convertMapLocationToInt(rc.getLocation().add(direction)));
                        Utilities.MoveDirection(rc, direction, false);
                    }
                }
            }
            else
            {
                rc.setIndicatorString(2, "Not Leader");
                if (fightMode(rc))
                {
                }
                else
                {
                    MoveDirection(rc, rc.getLocation().directionTo(target), false);
                }
            }
        } catch (Exception e)
        {
            e.printStackTrace();
            rc.yield();
        }
    }
    public static int getMapSize(RobotController rc){
    	int width = rc.getMapWidth();
		int height = rc.getMapHeight();
		int mapSize = width*height;
		return mapSize;
    }
    public static boolean checkRush(RobotController rc){//returns true if it is a rushable map
    	try{
    		MapLocation enemy = rc.senseEnemyHQLocation();
    		MapLocation check = rc.getLocation();
    		int mapSize = Basic.Utilities.getMapSize(rc);
    		Direction dir = rc.getLocation().directionTo(enemy);
    		double dist = rc.getLocation().distanceSquaredTo(enemy);
    		int voidSpace = 0;
    		while(!check.equals(enemy)){
    			if(rc.senseTerrainTile(check) == TerrainTile.VOID){
    				voidSpace++;
    				check = check.add(dir);
    			} else{
    				check = check.add(dir);
    			}
    			dir = check.directionTo(enemy);
    		}
    		if(dist <= 550){
    			if(voidSpace == 0){
    				rc.setIndicatorString(1, "Rush!");
    				return true;
    			} else {
    				return false;
    			}
    		} else if(dist <= mapSize/2.2){
    			if(voidSpace == 0){
    				rc.setIndicatorString(1, "Rush!");
    				return true;
    			} else {
    				return false;
    			}
    		}else{
    			return false;
    		}
    	} catch (Exception e){
    		e.printStackTrace();
    		return false;
    	}
    }
    public static boolean checkHQTower(RobotController rc){//returns true if an HQ tower should be set up
    	try{
    		MapLocation HQ = rc.senseHQLocation();
    		if(Basic.TowerUtil.getSpotScore(rc, HQ) >= 50){
    			return true;
    		} else {
    			return false;
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    		return false;
    	}
    }
    public static boolean checkDoublePastr(RobotController rc, MapLocation m1, MapLocation m2){
    	try{
    		int mapSize = Basic.Utilities.getMapSize(rc);
    		MapLocation enemy = rc.senseEnemyHQLocation();
    		MapLocation pastr1 = m1;
    		MapLocation pastr2 = m2;
    		int distToEnemy1 = m1.distanceSquaredTo(enemy);
    		if(mapSize >= 2000){
    			if(Basic.TowerUtil.getSpotScore(rc, pastr1) > 50){
    				if(pastr1.distanceSquaredTo(pastr2) > mapSize/1.25 && distToEnemy1 > 1000){
    					return true;
    				} else {
    					return false;
    				}
    			} else {
    				return false;
    			}
    		} else {
    			return false;
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    		return false;
    	}
    }
    /*public static long checkSamePastrLoc(RobotController rc, MapLocation firstPastr){
    	long[] pastrInfo = new long[30];
    	MapLocation[] pastrs = rc.sensePastrLocations(rc.getTeam().opponent());
    	if(pastrs[0] != null){
    		
    	}
    }*/
}

