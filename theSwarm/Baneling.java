package theSwarm;

import battlecode.common.*;

/**
 * Created by fredkneeland on 1/19/14.
 *
 * As its name suggests this bot morphs from a larva in the heat of battle and all it does is run toward the enemy line and
 * destroys as many enemies as possible sacrificing itself in the process...
 *
 * FOR THE SWARM
 *
 */
public class Baneling {
     RobotController rc;

    public Baneling(RobotController rc)
    {
        this.rc = rc;
        rc.setIndicatorString(0, "Baneling");
    }

    public  void run()
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
                        if (rc.senseRobotInfo(nearByEnemies[i]).health <= (40 +rc.getHealth()/2))
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
                        if (rc.senseRobotInfo(nearByEnemies[i]).health <= (40 +rc.getHealth()/2))
                        {
                            FightMicro.recordEnemyBotKilled(rc, allEnemies, nearByEnemies[i]);
                        }
                    }
                    rc.selfDestruct();
                }
                else if (nearByEnemies.length == 1 && rc.senseRobotInfo(enemiesInRange[0]).health > rc.getHealth() && totalDamage > 0)
                {
                    rc.selfDestruct();
                }
                else if (enemiesInRange.length == 1 && rc.senseRobotInfo(enemiesInRange[0]).health <= rc.getHealth())
                {
                    Robot[] nearByAllies3 = null;
                    nearByAllies3 = rc.senseNearbyGameObjects(Robot.class, 35, rc.getTeam());
                    MapLocation[] alliedLocs = FightMicro.locationOfBots(rc, nearByAllies3);
                    Movement.fire(rc, enemiesInRange, alliedLocs);
                }

                if (rc.isActive())
                {
                    Robot[] allVisibleEnemies = rc.senseNearbyGameObjects(Robot.class, 35, rc.getTeam().opponent());

                    // if we see no enemy soldiers morph back into larva
                    if (allVisibleEnemies.length == 0 || (allVisibleEnemies.length == 1 && rc.senseRobotInfo(allVisibleEnemies[0]).health < 50))
                    {
                        Larva larva = new Larva(rc);
                        larva.run();
                    }

                    MapLocation[] enemies = FightMicro.locationOfBots(rc, allVisibleEnemies);

                    MapLocation center = FightMicro.centerOfEnemies(enemies);

                    Movement.MoveDirection(rc, rc.getLocation().directionTo(center), false);
                }
            } catch (Exception e) {}
            rc.yield();
        }
    }
}
