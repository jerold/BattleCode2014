package UED2;

import battlecode.common.*;

/**
 * Created by fredkneeland on 1/12/14.
 */
public class Banshee {
    RobotController rc;
    MapLocation target;
    Direction direction;
    MapLocation enemyHQ;
    MapLocation ourHQ;
    boolean gotToTarget = false;
    boolean foundNoiseTower = false;
    Robot[] nearByEnemies;
    Robot[] enemyTowers;
    Robot enemyTower;
    MapLocation newTarget;
    MapLocation towerLoc;
    MapLocation attemptedTarget;


    public Banshee(RobotController rc)
    {
        this.rc = rc;
        ourHQ = rc.senseHQLocation();
        enemyHQ = rc.senseEnemyHQLocation();
        direction = enemyHQ.directionTo(ourHQ);


        target = enemyHQ;

        for (int i = 0; i < 5; i++)
        {
            target = target.add(direction);
            if (rc.senseTerrainTile(target).equals(TerrainTile.VOID))
            {
                i = 45;
            }
        }

        if (rc.senseTerrainTile(target).equals(TerrainTile.VOID))
        {
            direction = direction.rotateLeft();

            target = enemyHQ;

            for (int i = 0; i < 5; i++)
            {
                target = target.add(direction);
                if (rc.senseTerrainTile(target).equals(TerrainTile.VOID))
                {
                    i = 45;
                }
            }
        }

        if (rc.senseTerrainTile(target).equals(TerrainTile.VOID))
        {
            direction = enemyHQ.directionTo(ourHQ);
            direction = direction.rotateRight();

            for (int i = 0; i < 5; i++)
            {
                target = target.add(direction);
                if (rc.senseTerrainTile(target).equals(TerrainTile.VOID))
                {
                    i = 45;
                }
            }
        }

    }

    public void run()
    {
        while (true)
        {
            try
            {

                if (rc.isActive())
                {
                    if (!gotToTarget)
                    {
                        rc.setIndicatorString(1, "Going To Target: "+target);
                        rc.setIndicatorString(0, "Our location: "+ rc.getLocation());
                        if (rc.getLocation().isAdjacentTo(target) || rc.getLocation().equals(target))
                        {
                            gotToTarget = true;
                        }
                        else
                        {
                            Utilities.MoveMapLocation(rc, target, false);
                        }
                    }
                    else
                    {
                        //rc.setIndicatorString(1, "Got To Target");
                        if (!foundNoiseTower)
                        {
                            rc.setIndicatorString(2, "Looking for Noise Tower");
                            nearByEnemies = rc.senseNearbyGameObjects(Robot.class, 35, rc.getTeam().opponent());
                            int numbOfEnemyTowers = 0;

                            for (int i = 0; i < nearByEnemies.length; i++)
                            {
                                if (rc.senseRobotInfo(nearByEnemies[i]).type == RobotType.NOISETOWER)
                                {
                                    numbOfEnemyTowers++;
                                }
                            }

                            if (numbOfEnemyTowers > 0)
                            {
                                foundNoiseTower = true;
                                enemyTowers = new Robot[numbOfEnemyTowers];
                                int index = 0;

                                for (int i = 0; i < nearByEnemies.length; i++)
                                {
                                    if (rc.senseRobotInfo(nearByEnemies[i]).type == RobotType.NOISETOWER && rc.senseLocationOf(nearByEnemies[i]).distanceSquaredTo(enemyHQ) > 1)
                                    {
                                        enemyTowers[index] = nearByEnemies[i];
                                        index++;
                                    }
                                }
                                if (index > 0)
                                {
                                    int shortestDist = rc.getLocation().distanceSquaredTo(rc.senseLocationOf(enemyTowers[0]));
                                    enemyTower = enemyTowers[0];
                                    for (int j = 1; j < enemyTowers.length; j++)
                                    {
                                        if (rc.getLocation().distanceSquaredTo(rc.senseLocationOf(enemyTowers[j])) < shortestDist)
                                        {
                                            shortestDist = rc.getLocation().distanceSquaredTo(rc.senseLocationOf(enemyTowers[j]));
                                            enemyTower = enemyTowers[j];
                                        }
                                    }

                                    towerLoc = rc.senseLocationOf(enemyTower);
                                    direction = enemyHQ.directionTo(towerLoc);

                                    newTarget = towerLoc.add(direction).add(direction);
                                }

                                for (int i = 0; i < nearByEnemies.length; i++)
                                {
                                    if (rc.senseRobotInfo(nearByEnemies[i]).type == RobotType.PASTR)
                                    {
                                        if (rc.senseLocationOf(nearByEnemies[i]).distanceSquaredTo(enemyHQ) > 1)
                                        {
                                            towerLoc = rc.senseLocationOf(enemyTower);
                                            direction = enemyHQ.directionTo(towerLoc);

                                            newTarget = towerLoc.add(direction).add(direction);
                                            i = 56;
                                        }
                                    }
                                }

                                Utilities.fire(rc);
                            }
                        }
                        else
                        {
                            rc.setIndicatorString(2, ""+newTarget);
                            if (rc.getLocation().equals(newTarget))
                            {
                                if (rc.isActive())
                                {
                                    direction = rc.getLocation().directionTo(enemyHQ);

                                    // this means we have killed the noise tower so retreat
                                    if (rc.senseObjectAtLocation(towerLoc) == null)
                                    {
                                        Utilities.MoveDirection(rc, rc.getLocation().directionTo(enemyHQ).opposite(), false);
                                        foundNoiseTower = false;
                                    }
                                    else
                                    {
                                        if (rc.canAttackSquare(towerLoc))
                                        {
                                            rc.setIndicatorString(0, "Distance: "+rc.getLocation().distanceSquaredTo(towerLoc));
                                            rc.setIndicatorString(1, "Distance To HQ: "+rc.getLocation().distanceSquaredTo(enemyHQ));
                                            rc.attackSquare(towerLoc);
                                        }
                                        else
                                        {
                                            rc.setIndicatorString(0, "Distance: "+rc.getLocation().distanceSquaredTo(towerLoc));
                                        }
                                    }
                                }
                            }
                            else if (rc.getLocation().isAdjacentTo(newTarget))
                            {
                                if (rc.isActive())
                                {
                                    direction = rc.getLocation().directionTo(newTarget);
                                    if (rc.canMove(direction))
                                    {
                                        rc.move(direction);
                                    }
                                    else
                                    {
                                        Utilities.fire(rc);
                                    }
                                }
                            }
                            else
                            {
                                direction = rc.getLocation().directionTo(towerLoc);
                                attemptedTarget = rc.getLocation().add(direction);

                                if (attemptedTarget.distanceSquaredTo(enemyHQ) > 24)
                                {
                                    if (rc.canMove(direction))
                                    {
                                        rc.move(direction);
                                    }
                                    else
                                    {
                                        Utilities.fire(rc);
                                    }
                                }
                                else
                                {
                                    direction = rc.getLocation().directionTo(newTarget).rotateRight();
                                    attemptedTarget = rc.getLocation().add(direction);
                                    int distanceRight = attemptedTarget.distanceSquaredTo(newTarget);
                                    direction = rc.getLocation().directionTo(newTarget).rotateLeft();
                                    attemptedTarget = rc.getLocation().add(direction);
                                    int distanceLeft = attemptedTarget.distanceSquaredTo(newTarget);

                                    //if (distanceLeft == distanceRight)
                                    if (distanceLeft < distanceRight)
                                    {

                                        while (attemptedTarget.distanceSquaredTo(enemyHQ) < 24)
                                        {
                                            direction = direction.rotateLeft();
                                            attemptedTarget = rc.getLocation().add(direction);
                                        }

                                        if (rc.canMove(direction))
                                        {
                                            if (rc.isActive())
                                            {
                                                rc.move(direction);
                                            }
                                        }
                                        else
                                        {
                                            if (rc.isActive())
                                            {
                                                Utilities.fire(rc);
                                            }

                                        }
                                    }
                                    else
                                    {
                                        direction = rc.getLocation().directionTo(newTarget).rotateRight();
                                        while (attemptedTarget.distanceSquaredTo(enemyHQ) < 24)
                                        {
                                            direction = direction.rotateRight();
                                            attemptedTarget = rc.getLocation().add(direction);
                                        }

                                        if (rc.canMove(direction))
                                        {
                                            if (rc.isActive())
                                            {
                                                rc.move(direction);
                                            }
                                        }
                                        else
                                        {
                                            if (rc.isActive())
                                            {
                                                Utilities.fire(rc);
                                            }

                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Exception e)
            {
                rc.setIndicatorString(1, "Error");
                e.printStackTrace();
                System.out.println("Banshee Exception");
            }
            rc.yield();
        }

    }
}
