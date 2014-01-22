package theSwarm5;

import battlecode.common.*;

import java.util.Random;

public class Movement
{

    static final int enemyHQ = 1;
    static final int ourHQ = 2;
    static final int rallyPoint = 3;
    static final int needNoiseTower = 4;
    static final int needPastr = 5;
    static final int takeDownEnemyPastr = 6;
    static final int enemyPastrInRangeOfHQ = 7;
    static final int rallyPoint2 = 8;

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

    public static void MoveMapLocation(RobotController rc, MapLocation target, boolean sneak, boolean larva)
    {
        MapLocation[] pastLocations = new MapLocation[10];
        int side = 45;
        Direction dir;
        Direction newDir;
        rand = new Random();
        boolean didNotShoot = false;
        int[] alliedBots = FightMicro.AllAlliedBotsInfo(rc);
        int[] AllEnemyBots = FightMicro.AllEnemyBots(rc);
        int[] AllEnemyNoiseTowers = FightMicro.AllEnemyNoiseTowers(rc);
        Robot[] nearByEnemies;
        int ourIndex = FightMicro.ourSlotInMessaging2(rc, alliedBots);
        // we initialize pastLocations to hold our current location 5 times
        for (int i = 0; i < pastLocations.length; i++)
        {
            pastLocations[i] = rc.getLocation();
        }

        // this method will run until we get to our target location
        while (!rc.getLocation().equals(target) || rc.getLocation().isAdjacentTo(target))
        {

            // we put the try block inside of the while loop so an exception won't terminate the method
            try
            {
                if (FightMicro.fightMode(rc, target))
                {
                    rc.setIndicatorString(2, "Running fight micro 2");
                    //fire(rc);
                }
                else if (rc.isActive())
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
                    nearByEnemies = rc.senseNearbyGameObjects(Robot.class, 35, rc.getTeam().opponent());

                    if (alliedBots.length == 0)
                    {
                        alliedBots = FightMicro.AllAlliedBotsInfo(rc);
                    }
                    if (AllEnemyBots.length == 0 && nearByEnemies.length > 0)
                    {
                        FightMicro.FindAndRecordAllEnemies(rc, nearByEnemies, AllEnemyBots, AllEnemyNoiseTowers);
                        AllEnemyBots = FightMicro.AllEnemyBots(rc);
                    }

                    rc.setIndicatorString(2, "Not Running fight micro 2");


                    if (rc.getLocation().distanceSquaredTo(rc.senseEnemyHQLocation()) < 30)
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


                                try
                                {
                                    boolean ranFight = false;
                                    if (FightMicro.fightMode(rc, target))
                                    {
                                        ranFight = true;
                                        rc.setIndicatorString(2, "Running fight micro 3");
                                    }
                                    else if (rc.isActive())
                                    {
                                        dir2 = rc.getLocation().directionTo(target);

                                        rc.setIndicatorString(2, "Not Running fight micro 3");



                                        if (rc.getLocation().distanceSquaredTo(rc.senseEnemyHQLocation()) < 30)
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

                                        if (!ranFight)
                                        {
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
                                    }
                                    else
                                    {
                                        MapLocation[] enemyPastrs = rc.sensePastrLocations(rc.getTeam().opponent());
                                        if (enemyPastrs.length == 0)
                                        {
                                            int location = rc.readBroadcast(HQFunctions.rallyPoint2Channel());
                                            if (location != 0)
                                            {
                                                target = Movement.convertIntToMapLocation(location);
                                            }
                                            else if (larva)
                                            {
                                                int point = rc.readBroadcast(rallyPoint);
                                                if (!target.equals(convertIntToMapLocation(point)))
                                                {
                                                    target = convertIntToMapLocation(point);
                                                }
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
                else
                {
                    MapLocation[] enemyPastrs = rc.sensePastrLocations(rc.getTeam().opponent());
                    if (enemyPastrs.length == 0)
                    {
                        int location = rc.readBroadcast(HQFunctions.rallyPoint2Channel());
                        if (location != 0)
                        {
                            target = Movement.convertIntToMapLocation(location);
                        }
                        else if (larva)
                        {
                            int point = rc.readBroadcast(rallyPoint);
                            if (!target.equals(convertIntToMapLocation(point)))
                            {
                                target = convertIntToMapLocation(point);
                            }
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

    public static void fire(RobotController rc, Robot[] enemies, MapLocation[] allyBots)
    {
        int radius;

        try
        {
            if(rc.getType() == RobotType.HQ)
            {
                if (rc.isActive())
                {
                    radius = 15;
                    //Robot[] enemies = rc.senseNearbyGameObjects(Robot.class, radius, rc.getTeam().opponent());
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
                        if (target != null)
                        {
                            if (rc.canAttackSquare(rc.senseRobotInfo(target).location))
                            {
                                rc.attackSquare(rc.senseRobotInfo(target).location);
                            }
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
            }
            // In this case we are a soldier
            else
            {

                radius = 10;
                Robot[] enemies2 = rc.senseNearbyGameObjects(Robot.class, 35, rc.getTeam().opponent());
                enemies = FightMicro.findSoldiersAtDistance(rc, enemies2, radius);
                Robot target = null;

                if (rc.getLocation().distanceSquaredTo(rc.senseEnemyHQLocation()) > 50 && enemies2.length > 0)
                {
                    rc.broadcast(rallyPoint2, convertMapLocationToInt(rc.getLocation()));
                }


                if (enemies != null && allyBots != null)
                {
                    if (allyBots.length > 1)
                    {
                        int[] alliedBotsCount = new int[enemies.length];
                        for (int i = enemies.length; --i>=0;)
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

                if (enemies != null)
                {
                    for(int k = 0; k < enemies.length; k++)
                    {
                        if(target == null)
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
                        else if(rc.senseRobotInfo(enemies[k]).health < rc.senseRobotInfo(target).health && !rc.senseRobotInfo(target).isConstructing)
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
                                int[] enemyRobots = FightMicro.AllEnemyBots(rc);
                                FightMicro.recordEnemyBotKilled(rc, enemyRobots, target);
                            }
                    	}
                    }


                    
                }
            }
        }
        catch(Exception e){
            rc.setIndicatorString(0, "Error 2");
        	e.printStackTrace();
        }
    }



}
