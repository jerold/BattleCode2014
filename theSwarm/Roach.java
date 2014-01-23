package theSwarm;

import battlecode.common.*;
import java.util.Random;

/**
 * Created by fredkneeland on 1/22/14.
 */
public class Roach {
    RobotController rc;
    MapLocation target;
    int ourIndex;

    Robot[] nearByEnemies;
    int[] AllEnemyNoiseTowers;
    int[] AllEnemyBots;
    int[] AllAlliedBots;
    int pastrLoc = 0;
    int towerLocation = 0;
    Direction[] directions = Direction.values();
    Random rand = new Random();


    // these are the channels that we will use to communicate to our bots
    static final int enemyHQ = 1;
    static final int ourHQ = 2;
    static final int rallyPoint = 3;
    static final int needNoiseTower = 4;
    static final int needPastr = 5;
    static final int takeDownEnemyPastr = 6;
    static final int enemyPastrInRangeOfHQ = 7;
    static final int rallyPoint2 = 8;
    static final int defendPastr = 9;
    static final int pastLoc = 10;
    static final int morphZergling = 11;
    static final int morphHydralisk = 12;
    static final int hydraliskCount = 13;
    static final int towerLoc = 14;
    static final int towerBuilt = 15;
    static final int pastrBuilt = 16;

    public Roach(RobotController rc)
    {
        this.rc = rc;
        try {
            target = Movement.convertIntToMapLocation(rc.readBroadcast(HQFunctions.rallyPointChannel()));

        } catch (GameActionException e) {
            e.printStackTrace();
        }
        rc.setIndicatorString(0, "Roach");
    }

    public void run()
    {
        while (true)
        {
            try
            {
                rc.setIndicatorString(1, "Pastr: "+pastrLoc+", Tower: "+towerLocation);
                if (pastrLoc == 0)
                {
                    pastrLoc = rc.readBroadcast(pastLoc);
                }
                else if (towerLocation == 0)
                {
                    towerLocation = rc.readBroadcast(towerLoc);
                }
                //System.out.println("Hello world");
                // we will only do stuff if we are active

                if (pastrLoc != 0 && rc.getLocation().equals(Movement.convertIntToMapLocation(pastrLoc)))
                {
                    rc.broadcast(pastrBuilt, 1);
                    Drone drone = new Drone(rc, 1);
                    drone.run();
                }
                else if (towerLocation != 0 && rc.getLocation().equals(Movement.convertIntToMapLocation(towerLocation)))
                {
                    rc.broadcast(towerBuilt, 1);
                    Extractor extractor = new Extractor(rc, 1);
                    extractor.run();
                }

                else if ((rc.readBroadcast(defendPastr) == 1) && FightMicro.defenseMicro(rc, Movement.convertIntToMapLocation(rc.readBroadcast(pastLoc))))
                {

                }
                else if (FightMicro.fightMode(rc, target))
                {
                }
                else if (rc.isActive())
                {
                    if (rc.getLocation().equals(target) || rc.getLocation().distanceSquaredTo(target) < 10)
                    {
                        target = Movement.convertIntToMapLocation(rc.readBroadcast(HQFunctions.rallyPointChannel()));
                    }

                    if (rc.senseCowsAtLocation(rc.getLocation()) > 500)
                    {
                        Direction dir = directions[rand.nextInt(8)];
                        Movement.MoveDirection(rc, dir, true);
                    }

                    Movement.MoveMapLocation(rc, target, false, true);

                }


            } catch (Exception e)
            {
                e.printStackTrace();
                System.out.println("RobotPlayer Exception");
            }
            rc.yield();

        }
    }
}
