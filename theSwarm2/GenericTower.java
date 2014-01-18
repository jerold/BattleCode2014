package theSwarm2;

import battlecode.common.Clock;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotType;

/**
 * Created by fredkneeland on 1/13/14.
 */
public class GenericTower {

    RobotController rc;
    boolean troll;
    MapLocation target;
    boolean pull;
    MapLocation[] pastrSpots;
    MapLocation loc;

    public GenericTower(RobotController rc, boolean troll)
    {
        this.rc = rc;
        this.troll = troll;
        pull = true;

    }

    public void run()
    {
        while(true)
        {

            if(rc.getType() == RobotType.NOISETOWER)
            {
                if (target == null || Clock.getRoundNum() % 25 == 0)
                {
                    if (troll)
                    {
                        //rc.setIndicatorString(0, "Troll");
                        pastrSpots = rc.sensePastrLocations(rc.getTeam().opponent());

                        for (int i = 0; i < pastrSpots.length; i++)
                        {
                            if (pastrSpots[i].distanceSquaredTo(rc.senseEnemyHQLocation()) < 10 && pastrSpots[i].distanceSquaredTo(rc.getLocation()) < 400)
                            {
                                target = pastrSpots[i];
                            }
                        }

                        if(target == null)
                        {
                            target = rc.senseEnemyHQLocation();
                        }
                    }
                    else
                    {
                        //rc.setIndicatorString(0, "Tower");
                        pastrSpots = rc.sensePastrLocations(rc.getTeam());

                        if (pastrSpots.length > 0)
                        {
                            int dist = 400;
                            int tempDist;

                            for (int i = 0; i < pastrSpots.length; i++)
                            {
                                tempDist = rc.getLocation().distanceSquaredTo(pastrSpots[i]);
                                if (dist > tempDist)
                                {
                                    target = pastrSpots[i];
                                    dist = tempDist;
                                }
                            }
                        }
                    }

                    //rc.setIndicatorString(1, ""+target);
                }
                if(target == null){}
                else if(troll)
                {
                    try
                    {
                        if(rc.isActive())
                        {
                            rc.attackSquare(target);
                        }
                    }
                    catch(Exception e){}
                }
                else
                {
                    try
                    {
                        //rc.setIndicatorString(0, "Tower");
                        MapLocation[] enemies = rc.sensePastrLocations(rc.getTeam().opponent());
                        MapLocation[] allies = rc.sensePastrLocations(rc.getTeam());
                        MapLocation pastrE = null;
                        boolean enemyPastr = false;
                        boolean allyPastr = false;

                        for(int k = 0; k < enemies.length; k++)
                        {
                            if(enemies[k].distanceSquaredTo(target) < 400)
                            {
                                enemyPastr = true;
                                pastrE = enemies[k];
                            }
                        }
                        for(int k = 0; k < allies.length; k++)
                        {
                            if(allies[k].distanceSquaredTo(target) < 5)
                            {
                                allyPastr = true;
                            }
                        }
                        if(!enemyPastr && allyPastr)
                        {
                            if(pull)
                            {
                                Utilities.pullInto(rc, 20, target);
                                pull = false;
                            }
                            else
                            {
                                for(int k = 20; k > 4; k -= 1)
                                {
                                    Utilities.fireCircle(rc, k, target);
                                }
                                pull = true;
                            }
                        }
                        else if(enemyPastr && allyPastr)
                        {
                            if(pull)
                            {
                                Utilities.pullInto(rc, 20, target);
                                pull = false;
                            }
                            else
                            {
                                for(int k = 20; k > 4; k -= 1)
                                {
                                    Utilities.fireCircle(rc, k, target);
                                    while(!rc.isActive()){rc.yield();}
                                    rc.attackSquare(pastrE);
                                }
                                pull = true;
                            }
                        }
                        else if(enemyPastr)
                        {
                            rc.attackSquare(pastrE);
                        }
                    }
                    catch(Exception e){}
                }
            }

            rc.yield();
        }
    }
}

