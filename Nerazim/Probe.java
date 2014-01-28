package Nerazim;

import battlecode.common.*;

/**
 * Created by fredkneeland on 1/26/14.
 */
public class Probe {
    RobotController rc;
    static final int pastr1 = 1;
    static final int pastr2 = 2;
    static final int pastr3 = 3;
    static final int pastr1Count = 4;
    static final int pastr2Count = 5;
    static final int pastr3Count = 6;

    MapLocation pastrSpot;
    MapLocation towerSpot;
    boolean madeItToSpot = false;
    Direction[] dirs = Direction.values();

    public Probe(RobotController rc)
    {
        rc.setIndicatorString(0, "Probe");

        this.rc = rc;
        try
        {
            rc.setIndicatorString(1, "Pastr1 Count: "+rc.readBroadcast(pastr1Count)+" Pastr 2 Count: "+rc.readBroadcast(pastr2Count));
            if (rc.readBroadcast(pastr1) == 0 || rc.readBroadcast(pastr1Count) < 2)
            {
                pastrSpot = TowerUtil.bestSpot3(rc);
                rc.broadcast(pastr1, Movement.convertMapLocationToInt(pastrSpot));
                rc.broadcast(pastr1Count, rc.readBroadcast(pastr1Count)+1);
            }
            else if (rc.readBroadcast(pastr2) == 0 || rc.readBroadcast(pastr2Count) < 2)
            {
                pastrSpot = TowerUtil.bestSpot3(rc);
                pastrSpot = TowerUtil.getOppositeSpot(rc, pastrSpot);
                rc.broadcast(pastr2, Movement.convertMapLocationToInt(pastrSpot));
                rc.broadcast(pastr2Count, rc.readBroadcast(pastr2Count)+1);
            }
        } catch (Exception e) {}
    }

    public void run()
    {
        while (true)
        {
            try
            {

                if (pastrSpot == null)
                {
                    Zealot zealot = new Zealot(rc);
                    zealot.run();
                }
                if (FightMicro2.fightMode(rc, pastrSpot))
                {
                }
                else if (rc.isActive())
                {
                    if (!madeItToSpot)
                    {
                        if (rc.getLocation().isAdjacentTo(pastrSpot) || rc.getLocation().equals(pastrSpot))
                        {
                            madeItToSpot = true;
                        }
                        else
                        {
                            Movement.MoveMapLocation(rc, pastrSpot, false, false);
                        }
                    }
                    else
                    {
                        Robot[] nearByAllies = rc.senseNearbyGameObjects(Robot.class, 35, rc.getTeam());
                        if (nearByAllies.length > 0 && (rc.senseRobotInfo(nearByAllies[0]).type == RobotType.NOISETOWER || rc.senseRobotInfo(nearByAllies[0]).isConstructing))
                        {
                            while (rc.senseRobotInfo(nearByAllies[0]).isConstructing)
                            {
                                rc.yield();
                                nearByAllies = rc.senseNearbyGameObjects(Robot.class, 35, rc.getTeam());
                            }
                            for (int i = 0; i < 50; i++)
                            {
                                rc.yield();
                            }
                            while (rc.senseNearbyGameObjects(Robot.class, 35, rc.getTeam().opponent()).length > 0)
                            {
                                FightMicro2.fightMode(rc, pastrSpot);
                            }
                            Movement.MoveMapLocation(rc, pastrSpot, false, false);
                            MapLocation[] ourPastrs = rc.sensePastrLocations(rc.getTeam());

                            if (ourPastrs.length > 0)
                            {
                                if (rc.getLocation().distanceSquaredTo(ourPastrs[0]) < 50 || ourPastrs.length > 1)
                                {
                                    Stalker stalker = new Stalker(rc);
                                    stalker.run();
                                }
                            }
                            Robot[] allies = rc.senseNearbyGameObjects(Robot.class, 35, rc.getTeam());

                            if (allies.length > 1)
                            {
                                int numbConstructing = 0;
                                int noiseTower = 0;

                                for (int i = allies.length; --i>=0;)
                                {
                                    if (rc.senseRobotInfo(allies[i]).type == RobotType.NOISETOWER)
                                    {
                                        noiseTower++;
                                    }
                                    else if (rc.senseRobotInfo(allies[i]).isConstructing)
                                    {
                                        numbConstructing++;
                                    }
                                }
                                if ((numbConstructing + noiseTower) > 1)
                                {
                                    Stalker stalker = new Stalker(rc);
                                    stalker.run();
                                }
                            }
                            rc.construct(RobotType.PASTR);
                        }
                        else
                        {
                            towerSpot = pastrSpot;
                            for(int k = 0; k < dirs.length; k++)
                            {
                                if(rc.senseTerrainTile(towerSpot.add(dirs[k])) != TerrainTile.VOID)
                                {
                                    towerSpot = towerSpot.add(dirs[k]);
                                    break;
                                }
                            }

                            if (rc.getLocation().equals(towerSpot))
                            {
                                rc.construct(RobotType.NOISETOWER);
                            }
                            else if (rc.senseObjectAtLocation(towerSpot) != null)
                            {
                                while (rc.senseNearbyGameObjects(Robot.class, 35, rc.getTeam().opponent()).length > 0)
                                {
                                    FightMicro2.fightMode(rc, pastrSpot);
                                    rc.yield();
                                }
                                MapLocation[] ourPastrs = rc.sensePastrLocations(rc.getTeam());

                                if (ourPastrs.length > 0)
                                {
                                    if (rc.getLocation().distanceSquaredTo(ourPastrs[0]) < 50 || ourPastrs.length > 1)
                                    {
                                        Stalker stalker = new Stalker(rc);
                                        stalker.run();
                                    }
                                }
                                Robot[] allies = rc.senseNearbyGameObjects(Robot.class, 35, rc.getTeam());

                                if (allies.length > 1)
                                {
                                    int numbConstructing = 0;
                                    int noiseTower = 0;

                                    for (int i = allies.length; --i>=0;)
                                    {
                                        if (rc.senseRobotInfo(allies[i]).type == RobotType.NOISETOWER)
                                        {
                                            noiseTower++;
                                        }
                                        else if (rc.senseRobotInfo(allies[i]).isConstructing)
                                        {
                                            numbConstructing++;
                                        }
                                    }

                                    if ((numbConstructing + noiseTower) > 1)
                                    {
                                        Stalker stalker = new Stalker(rc);
                                        stalker.run();
                                    }
                                }
                                rc.construct(RobotType.PASTR);
                            }
                            else
                            {
                                Movement.MoveDirection(rc, rc.getLocation().directionTo(towerSpot), false);
                            }
                        }

                    }
                }
            } catch (Exception e) {}
        }

    }
}
