package UED;

import battlecode.common.*;

/**
 * Created by fredkneeland on 1/9/14.
 */
public class Hellion {
    RobotController rc;
    MapLocation target;
    boolean kamikaze;

    public Hellion(RobotController rc, boolean kamikaze)
    {
        this.rc = rc;
        this.kamikaze = kamikaze;
        rc.setIndicatorString(0, "Hellion");
        MapLocation enemyHQ = rc.senseEnemyHQLocation();
        Direction direction = rc.getLocation().directionTo(enemyHQ);
        MapLocation target2 = enemyHQ;

        if (kamikaze)
        {
            for (int i = 0; i < 6; i++)
            {
                target2 = target2.add(direction);
            }

            if (rc.senseTerrainTile(target2).equals(TerrainTile.VOID))
            {
                target2 = enemyHQ;
                direction = direction.rotateLeft();
                for (int i = 0; i < 6; i++)
                {
                    target2 = target2.add(direction);
                }
            }

            if (rc.senseTerrainTile(target2).equals(TerrainTile.VOID))
            {
                target2 = enemyHQ;
                direction = direction.rotateLeft();
                for (int i = 0; i < 6; i++)
                {
                    target2 = target2.add(direction);
                }
            }

            if (rc.senseTerrainTile(target2).equals(TerrainTile.VOID))
            {
                target2 = enemyHQ;
                direction = direction.rotateRight().rotateRight().rotateRight();
                for (int i = 0; i < 6; i++)
                {
                    target2 = target2.add(direction);
                }
            }

            if (rc.senseTerrainTile(target2).equals(TerrainTile.VOID))
            {
                target2 = enemyHQ;
                direction = direction.rotateRight();
                for (int i = 0; i < 6; i++)
                {
                    target2 = target2.add(direction);
                }
            }
            target = target2;
        }
        else
        {
            target = rc.senseEnemyHQLocation().subtract(rc.getLocation().directionTo(rc.senseHQLocation()));
        }

    }

    public void run()
    {
        while (true)
        {
            try
            {
                if (kamikaze)
                {
                    MapLocation enemyHQ = rc.senseEnemyHQLocation();
                    int distanceToEnemyHQ = rc.getLocation().distanceSquaredTo(enemyHQ);
                    Direction dir = rc.getLocation().directionTo(enemyHQ);
                    if (distanceToEnemyHQ < 4)
                    {
                        if (rc.canMove(dir))
                        {
                            Utilities.MoveDirection(rc, dir, false);
                            if (rc.senseNearbyGameObjects(Robot.class, 2, rc.getTeam().opponent()).length > 0)
                            {
                                rc.selfDestruct();
                            }
                        }
                    }
                    else if (distanceToEnemyHQ < 24 && rc.getHealth() < 50)
                    {
                        Utilities.fire(rc);
                    }
                    else if (distanceToEnemyHQ < 30)
                    {
                        Utilities.MoveDirection(rc, dir, false);
                    }
                    else
                    {
                        Utilities.MoveMapLocation(rc, target, false);
                    }
                }
                else
                {
                    if (rc.isActive())
                    {
                        Utilities.MoveMapLocation(rc, target, false);
                    }
                }
            } catch (Exception e)
            {
                rc.setIndicatorString(1, "Error");
                e.printStackTrace();
                System.out.println("Hellion Exception");
            }
            rc.yield();
        }
    }

}
