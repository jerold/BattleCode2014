package DeepBlue;

import battlecode.common.*;

/**
 * Created by fredkneeland on 1/25/14.
 */
public class SuicideSoldier {
    RobotController rc;
    public SuicideSoldier(RobotController rc)
    {
        this.rc = rc;
    }

    public void run()
    {
        while (true)
        {
            try
            {
                Robot[] nearByEnemies = rc.senseNearbyGameObjects(Robot.class, 2, rc.getTeam().opponent());
                Robot[] nearByAllies = rc.senseNearbyGameObjects(Robot.class, 2, rc.getTeam());
                Robot[] enemiesInRange = rc.senseNearbyGameObjects(Robot.class, 24, rc.getTeam().opponent());
                int[] allEnemies = FightMicro.AllEnemyBots(rc);

                double totalDamage = 0;

                for (int i = nearByEnemies.length; --i>=0;)
                {
                    double enemyHealth = rc.senseRobotInfo(nearByEnemies[i]).health;
                    if (enemyHealth >= 40 + (rc.getHealth()/2))
                    {
                        totalDamage += 40 + (rc.getHealth()/2);
                    }
                    else
                    {
                        totalDamage += enemyHealth;
                    }
                }

                for (int j = nearByAllies.length; --j>=0; )
                {
                    double alliedHealth = rc.senseRobotInfo(nearByAllies[j]).health;
                    if (alliedHealth >= 40 + (rc.getHealth()/2))
                    {
                        totalDamage -= 40 + (rc.getHealth()/2);
                    }
                    else
                    {
                        totalDamage -= alliedHealth;
                    }
                }

                if (totalDamage > 40+rc.getHealth())
                {
                    for (int i = nearByEnemies.length; --i>=0;)
                    {
                        if (rc.senseRobotInfo(nearByEnemies[i]).health <= 40 +rc.getHealth()/2)
                        {
                            FightMicro.recordEnemyBotKilled(rc, allEnemies, nearByEnemies[i]);
                        }
                    }
                    rc.selfDestruct();
                }

                else if ((enemiesInRange.length * 10 >= (int) rc.getHealth()) && totalDamage > 50)
                {
                    for (int i = nearByEnemies.length; --i>=0;)
                    {
                        if (rc.senseRobotInfo(nearByEnemies[i]).health <= 40 +rc.getHealth()/2)
                        {
                            FightMicro.recordEnemyBotKilled(rc, allEnemies, nearByEnemies[i]);
                        }
                    }
                    rc.selfDestruct();
                }
                else if (enemiesInRange.length == 1 && rc.senseRobotInfo(enemiesInRange[0]).health > rc.getHealth() && totalDamage > 0)
                {
                    rc.selfDestruct();
                }
                else if (enemiesInRange.length == 1 && rc.senseRobotInfo(enemiesInRange[0]).health <= rc.getHealth())
                {
                    Robot[] nearByAllies3 = null;
                    nearByAllies3 = rc.senseNearbyGameObjects(Robot.class, 35, rc.getTeam());
                    MapLocation[] alliedLocs = FightMicro.locationOfBots(rc, nearByAllies3);
                    FightMicro.fire(rc, enemiesInRange, alliedLocs);
                }

                if (rc.isActive())
                {
                    Robot[] allVisibleEnemies = rc.senseNearbyGameObjects(Robot.class, 35, rc.getTeam().opponent());

                    // if we see no enemy soldiers morph back into larva
                    if (allVisibleEnemies.length == 0 || (allVisibleEnemies.length == 1 && rc.senseRobotInfo(allVisibleEnemies[0]).health < 50))
                    {
                        Soldiers soldiers = new Soldiers();
                        soldiers.run(rc);
                    }

                    MapLocation[] enemies = FightMicro.locationOfBots(rc, allVisibleEnemies);

                    MapLocation center = FightMicro.centerOfEnemies(enemies);

                    FightMicro.MoveDirection(rc, rc.getLocation().directionTo(center), false);
                }
            } catch (Exception e) {}
            rc.yield();
        }
    }

}
