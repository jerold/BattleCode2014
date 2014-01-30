package DeepBlue.Strategies;

import DeepBlue.*;
import battlecode.common.*;

/**
 * Created by fredkneeland on 1/29/14.
 */
public abstract class UnitStratOurPastrKillers extends UnitStrategy {
    static RobotController rc;
    static boolean madeIt;
    static MapLocation pastrToDefend;
    static MapLocation target;
    static boolean killedPastr = false;
    static MapLocation waitSpot;
    static Direction[] dirs = Direction.values();
    static MapLocation blinkSpot;
    static boolean mePastr1;
    static int pastr1Count = 0;
    static int pastr2Count = 0;
    static MapLocation[] ourPastrs;
    static int type;

    public static void initialize(RobotController rcIn, MapLocation pastr, int ourType)
    {
        rc = rcIn;
        pastrToDefend = pastr;
        type = ourType;
    }

    public static void upDate() throws GameActionException
    {
        if (rc.getHealth() < 10)
        {
            rc.broadcast(type, 0);
        }
        else
        {
            rc.broadcast(type, 1);
        }

        target = rc.getLocation();
        if (rc.isActive())
        {
            if (!madeIt)
            {
                if (rc.isActive())
                {
                    if (rc.getLocation().isAdjacentTo(pastrToDefend) || rc.getLocation().equals(pastrToDefend))
                    {
                        madeIt = true;
                    }
                    else
                    {
                        target = pastrToDefend;
                    }
                }
            }
            else
            {
                rc.setIndicatorString(0, "stalker");
                if (!killedPastr)
                {
                    if (waitSpot == null)
                    {
                        Direction dir = pastrToDefend.directionTo(rc.senseEnemyHQLocation()).opposite();
                        waitSpot = pastrToDefend.add(dir);
                        if (rc.senseObjectAtLocation(waitSpot) != null)
                        {
                            waitSpot = waitSpot.add(dir);
                        }

                        if (rc.senseTerrainTile(waitSpot).equals(TerrainTile.VOID))
                        {
                            for(int k = 0; k < dirs.length; k++)
                            {
                                if(rc.senseTerrainTile(waitSpot.add(dirs[k])) != TerrainTile.VOID)
                                {
                                    waitSpot = waitSpot.add(dirs[k]);
                                    break;
                                }
                            }
                        }
                    }

                    rc.setIndicatorString(2, ""+waitSpot);

                    Robot[] inRangeEnemies = rc.senseNearbyGameObjects(Robot.class, 10, rc.getTeam().opponent());

                    if (inRangeEnemies.length == 1 && (rc.senseRobotInfo( (Robot) rc.senseObjectAtLocation(pastrToDefend)).health > 10 || rc.senseRobotInfo(inRangeEnemies[0]).location.distanceSquaredTo(pastrToDefend) > 10))
                    {
                        rc.attackSquare(rc.senseRobotInfo(inRangeEnemies[0]).location);
                    }

                    if (rc.getLocation().equals(waitSpot))
                    {
                        Robot[] allies = rc.senseNearbyGameObjects(Robot.class, 35, rc.getTeam());
                        Robot pastr = null;
                        if (allies.length > 0)
                        {
                            for (int i = allies.length; --i>=0;)
                            {
                                if (rc.senseRobotInfo(allies[i]).type == RobotType.PASTR)
                                {
                                    pastr = allies[i];
                                    i = -1;
                                }
                            }
                            if (rc.senseTeamMilkQuantity(rc.getTeam()) > 9000000)
                            {

                            }
                            else if ( pastr != null && rc.senseRobotInfo(pastr).health > 10)
                            {
                                if (rc.getLocation().distanceSquaredTo(rc.senseLocationOf(pastr)) <= 10)
                                {
                                    if (rc.senseRobotInfo(pastr).health == 200)
                                    {
                                        rc.yield();
                                        rc.yield();
                                        rc.yield();
                                        rc.yield();
                                        rc.yield();
                                    }
                                    rc.attackSquare(rc.senseLocationOf(pastr));
                                }
                                else
                                {
                                    waitSpot = rc.getLocation().add(rc.getLocation().directionTo(rc.senseLocationOf(pastr)));
                                }
                            }
                            else if (pastr != null && rc.senseNearbyGameObjects(Robot.class, 35, rc.getTeam().opponent()).length > 0)
                            {
                                if (rc.senseRobotInfo(pastr).health <= 10)
                                {
                                    if (mePastr1)
                                    {
                                        rc.broadcast(pastr1Count, 0);
                                    }
                                    else
                                    {
                                        rc.broadcast(pastr2Count, 0);
                                    }
                                }
                                if (rc.getLocation().distanceSquaredTo(rc.senseLocationOf(pastr)) <= 10)
                                {
                                    if (rc.senseRobotInfo(pastr).health <= 10)
                                    {
                                        killedPastr = true;
                                    }
                                    rc.attackSquare(rc.senseLocationOf(pastr));
                                }
                                else
                                {
                                    waitSpot = rc.getLocation().add(rc.getLocation().directionTo(rc.senseLocationOf(pastr)));
                                }
                            }
                            else if (rc.sensePastrLocations(rc.getTeam()).length < 2 && rc.senseObjectAtLocation(pastrToDefend) == null)
                            {
                                if (rc.sensePastrLocations(rc.getTeam()).length > 0)
                                {
                                    if (rc.getLocation().distanceSquaredTo(rc.sensePastrLocations(rc.getTeam())[0]) > 100)
                                    {
                                        killedPastr = true;
                                    }

                                }
                                else
                                {
                                    killedPastr = true;
                                }
                            }
                        }
                    }
                    else
                    {
                        target = waitSpot;
                    }
                }
                else
                {
                    rc.setIndicatorString(1, "Blink");
                    if (blinkSpot == null)
                    {
                        blinkSpot = waitSpot.add(waitSpot.directionTo(rc.senseEnemyHQLocation()).opposite());
                        blinkSpot = blinkSpot.add(blinkSpot.directionTo(rc.senseEnemyHQLocation()).opposite());
                        blinkSpot = blinkSpot.add(blinkSpot.directionTo(rc.senseEnemyHQLocation()).opposite());
                        blinkSpot = blinkSpot.add(blinkSpot.directionTo(rc.senseEnemyHQLocation()).opposite());
                        blinkSpot = blinkSpot.add(blinkSpot.directionTo(rc.senseEnemyHQLocation()).opposite());
                        blinkSpot = blinkSpot.add(blinkSpot.directionTo(rc.senseEnemyHQLocation()).opposite());
                        blinkSpot = blinkSpot.add(blinkSpot.directionTo(rc.senseEnemyHQLocation()).opposite());

                        while (rc.senseTerrainTile(blinkSpot).equals(TerrainTile.OFF_MAP))
                        {
                            blinkSpot = blinkSpot.add(waitSpot.directionTo(rc.senseEnemyHQLocation()).rotateRight());
                        }

                        for(int k = 0; k < dirs.length; k++)
                        {
                            if(rc.senseTerrainTile(blinkSpot.add(dirs[k])) != TerrainTile.VOID)
                            {
                                blinkSpot = blinkSpot.add(dirs[k]);
                                break;
                            }
                        }
                    }

                    Robot[] nearByEnemies = rc.senseNearbyGameObjects(Robot.class, 10, rc.getTeam().opponent());

                    if (nearByEnemies.length > 0)
                    {

                    }
                    else if (rc.getLocation().equals(blinkSpot) || (rc.getLocation().distanceSquaredTo(blinkSpot) < 10 && rc.senseTerrainTile(blinkSpot).equals(TerrainTile.VOID) && rc.getLocation().distanceSquaredTo(waitSpot) >= 35))
                    {
                        ourPastrs = rc.sensePastrLocations(rc.getTeam());

                        for (int i = ourPastrs.length; --i>=0;)
                        {
                            if (ourPastrs[i].distanceSquaredTo(rc.getLocation()) < 400)
                            {
                                killedPastr = false;
                            }
                        }
                        FightMicro.defenseMicro(rc, rc.getLocation());
                    }
                    else
                    {
                        target = blinkSpot;
                    }
                }
            }
        }

        Soldiers.nav.setDestination(target);
    }
}
