package smartbot;

/**
 * Created by fredkneeland on 1/7/14.
 */
import java.util.Random;

import battlecode.common.*;

public class Utilities
{
    static Direction[] directions = {Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST, Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST};
    static Random rand;
    static boolean goneRight = false;

    // In this function the HQ spawns a soldier ideally toward the enemy base but in any direction otherwise
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

    public static MapLocation[] BestPastureSpots(RobotController rc)
    {
        MapLocation[] empty = new MapLocation[1];
        try
        {
            //Check if a robot is spawnable and spawn one if it is
            double growthRates[][] = rc.senseCowGrowth();
            int countofBestSpots = 0;
            double best = 0.0001;
            for (int i = 0; i < rc.getMapWidth(); i++)
            {
                for (int k = 0; k < rc.getMapHeight(); k++)
                {
                    if (growthRates[i][k] > best)
                    {
                        best = growthRates[i][k];
                        countofBestSpots = 1;
                    }
                    else if (growthRates[i][k] == best)
                    {
                        countofBestSpots++;
                    }

                    SpawnSoldiers(rc);
                }
            }

            MapLocation[] bestSpots = new MapLocation[countofBestSpots];
            int index = 0;
            for (int i = 0; i < rc.getMapWidth(); i++)
            {
                for (int k = 0; k < rc.getMapHeight(); k++)
                {
                    if (growthRates[i][k] == best)
                    {
                        bestSpots[index] = new MapLocation(i,k);
                        index++;
                    }

                    SpawnSoldiers(rc);
                }
            }

            for (int i = 1; i < countofBestSpots; i++)
            {
                for (int j = i; j < countofBestSpots; j++)
                {
                    int dist1 = rc.getLocation().distanceSquaredTo(bestSpots[j]);
                    int dist2 = rc.getLocation().distanceSquaredTo(bestSpots[j]);

                    if (dist1 < dist2)
                    {
                        MapLocation temp = bestSpots[j];
                        bestSpots[j] = bestSpots[j-1];
                        bestSpots[j-1] = temp;
                    }

                    SpawnSoldiers(rc);
                }
            }
            return bestSpots;
        } catch (Exception e)
        {
        	e.printStackTrace();
            System.out.println("Utility Exception");
        }
        return empty;
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

    public static void MoveMapLocation(RobotController rc, MapLocation target, boolean sneak)
    {
        MapLocation[] pastLocations = new MapLocation[10];
        int side = 45;
        Direction dir;
        Direction newDir;
        rand = new Random();
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
                dir = rc.getLocation().directionTo(target);
                newDir = Direction.NONE;

                // simple shoot at an enemy if we see one will need to be improved later
                Robot[] nearbyEnemies = rc.senseNearbyGameObjects(Robot.class,10,rc.getTeam().opponent());
                if (nearbyEnemies.length > 0 && rc.senseRobotInfo((Robot) nearbyEnemies[0]).health < 201)
                {
                    fire(rc);
                }
                // if we can move towards target and we haven't been on the square recently then lets move
                else if (rc.canMove(dir) && !MapLocationInArray(rc, rc.getLocation().add(dir), pastLocations))
                {
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
                					//rc.setIndicatorString(1, "Trying to Avoid");
	                				//rc.setIndicatorString(2, ""+side);
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
                				rc.setIndicatorString(0, "Dir: "+ dir +" Dir2: " + dir2);
                			} catch (Exception e)
                            {
                                // tell the console we through an exception in utility object for debug purposes
                                //System.out.println("Utility Exception");
                                //System.out.println(e.toString());
                				e.printStackTrace();
                                rc.yield();
                            }
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

                			rc.yield();
                		}
                		rc.setIndicatorString(1, "Not trying to Avoid");
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

                rc.yield();
            }
            catch (Exception e)
            {
                // tell the console we through an exception in utility object for debug purposes
                System.out.println("Utility Exception");
                e.printStackTrace();
                //System.out.println(e.toString());
                rc.yield();
            }
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
    public static void fire(RobotController rc)
    {
        int radius;

        try
        {
            if(rc.getType() == RobotType.HQ)
            {
                radius = 15;
                Robot[] enemies = rc.senseNearbyGameObjects(Robot.class, radius, rc.getTeam().opponent());
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
                //Nuke nuker = new Nuke(rc, nearBySpots[index]);
                //nuker.run();
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
}

