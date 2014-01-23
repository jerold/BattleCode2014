package theSwarm;

import battlecode.common.*;
import java.util.Random;

/**
 * Created by fredkneeland on 1/22/14.
 */
public class Hydralisk {
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
    boolean defense = false;
    MapLocation pastr;

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

    public Hydralisk(RobotController rc)
    {
        this.rc = rc;
        try {
            target = Movement.convertIntToMapLocation(rc.readBroadcast(HQFunctions.rallyPointChannel()));
            int count = rc.readBroadcast(hydraliskCount);
            rc.broadcast(hydraliskCount, ++count);
        } catch (GameActionException e)
        {
            e.printStackTrace();
        }
        rc.setIndicatorString(0, "Hydralisk");
        rc.setIndicatorString(2, ""+target);
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
                    pastr = Movement.convertIntToMapLocation(pastrLoc);
                }
                else if (towerLocation == 0)
                {
                    towerLocation = rc.readBroadcast(towerLoc);
                }

                if (pastrLoc != 0)
                {
                    defense = true;
                }



                if (rc.getLocation().equals(Movement.convertIntToMapLocation(pastrLoc)))
                {
                    Drone drone = new Drone(rc, 1);
                    drone.run();
                }
                else if (rc.getLocation().equals(Movement.convertIntToMapLocation(towerLocation)))
                {
                    rc.setIndicatorString(2, "Building tower: "+towerLocation);
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
                    if (rc.readBroadcast(needPastr) == 1)
                    {
                        rc.broadcast(needPastr, 0);
                        Drone drone = new Drone(rc, 1);
                        drone.run();
                    }
                    else if (rc.readBroadcast(needPastr) == -1)
                    {
                        rc.broadcast(needPastr, 0);
                        Drone drone = new Drone(rc, -1);
                        drone.run();
                    }
                    else if (rc.readBroadcast(needNoiseTower) == 1)
                    {
                        rc.broadcast(needNoiseTower, 0);
                        Extractor extractor = new Extractor(rc, 1);
                        extractor.run();
                    }
                    else if (rc.readBroadcast(needNoiseTower) == -1)
                    {
                        rc.broadcast(needNoiseTower, 0);
                        Extractor extractor = new Extractor(rc, -1);
                        extractor.run();
                    }

                    if (rc.getLocation().equals(target) || rc.getLocation().distanceSquaredTo(target) < 10)
                    {
                        MapLocation newTarget = Movement.convertIntToMapLocation(rc.readBroadcast(HQFunctions.rallyPointChannel()));
                        if (defense && newTarget.distanceSquaredTo(pastr) < 25)
                        {
                            target = newTarget;
                        }
                        else
                        {
                            target = newTarget;
                        }
                        MapLocation towerSlot = Movement.convertIntToMapLocation(towerLocation);
                        MapLocation pastrSlot = Movement.convertIntToMapLocation(pastrLoc);
                        if (rc.getLocation().distanceSquaredTo(towerSlot) <= 10 && rc.senseObjectAtLocation(towerSlot) == null)
                        {
                            target = towerSlot;
                        }
                        else if (rc.getLocation().distanceSquaredTo(pastrSlot) <= 10 && rc.senseObjectAtLocation(pastrSlot) == null)
                        {
                            target = pastrSlot;
                        }

                        rc.setIndicatorString(2, ""+target);
                    }

                    if (rc.senseCowsAtLocation(rc.getLocation()) > 500 && rc.sensePastrLocations(rc.getTeam()).length > 0)
                    {
                        Direction dir = directions[rand.nextInt(8)];
                        Movement.MoveDirection(rc, dir, true);
                    }

                    if (defense)
                    {
                        Movement.MoveMapLocation(rc, target, true, false);
                    }
                    else
                    {
                        Movement.MoveMapLocation(rc, target, false, false);
                    }


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
