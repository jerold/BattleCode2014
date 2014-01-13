package UED;

import battlecode.common.*;
import battlecode.common.RobotType;

/**
 * Created by fredkneeland on 1/7/14.
 *
 * This bot solely focuses on killing the enemy may implement adding one pastr in late game if necessary
 *
 */
public class RobotPlayer
{
    static int myType = 0;
    static final int GOLIATH = 1;
    static final int GHOST = 2;
    static final int DURAN = 3;
    static final int SUPPLY_DEPOT = 4;
    static final int MULECALLDOWN = 5;
    static final int SENSORTOWER = 6;
    static final int MARINES = 7;
    static final int HELLION = 8;
    static final int THOR = 9;
    static final int BATTLECRUISER = 10;
    static final int THOR2 = 11;
    static final int HELLION2 = 12;
    static final int SCV2 = 13;
    static final int MARAUDER = 14;
    static final int CENTERMULE = 15;
    static final int CENTERTOWER = 16;
    static final int HQTOWER = 17;
    static final int BANSHEE = 18;
    static final int MISSILETURRET = 19;

    static MapLocation[] ourPastrs;

    // here are all of the channels
    // channels for communication
    static final int EnemyHQChannel = 0;
    static final int OurHQChannel = 1;
    static final int TroopType = 2;
    static final int GhostNumb = 3;
    static final int GoliathOnline = 4;
    static final int GhostReady = 5;
    static final int BattleCruiserLoc = 6;
    static final int BattleCruiserLoc2 = 7;
    static final int BattleCruiserArrived = 8;
    static final int startBattleCruiserArray = 9;
    static final int endBattleCruiserArray = 59;
    static final int BattleCruiserInArray = 60;

    public static void run(RobotController rc)
    {
        while(true)
        {
            if (rc.getType() == RobotType.HQ)
            {
                try
                {
                    AlexeiStukov alexeiStukov = new AlexeiStukov((rc));
                    alexeiStukov.run();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    System.out.println("HQ Exception");
                }
            }
            else if(rc.getType() == RobotType.NOISETOWER)
            {
                if (rc.getLocation().distanceSquaredTo(Utilities.spotOfTrollTower(rc, rc.senseEnemyHQLocation())) < 10)
                {
                    new GenericTower(rc, true).run();
                }
                if (rc.getLocation().distanceSquaredTo(rc.senseHQLocation()) < 20)
                {

                    new GenericTower(rc, false).run();

                }
                else
                {

                   new GenericTower(rc, false).run();
                }
            }
            else if(rc.getType() == RobotType.PASTR)
            {
                new Bunker(rc).run();
            }
            else if (rc.getType() == RobotType.SOLDIER)
            {
                try
                {
                    if (rc.isActive())
                    {
                        if (true)
                        {
                            Firebat firebat = new Firebat(rc);
                            firebat.run();
                        }
                        if (myType == 0)
                        {
                            myType = rc.readBroadcast(TroopType);
                        }
                        // powerful squad to out muscle enemy
                        if (myType == GOLIATH)
                        {
                        	Goliath goliath = new Goliath(rc);
                        	goliath.run();
                        }
                        // trooops to support Duran
                        else if (myType == GHOST)
                        {
                            Ghost ghost = new Ghost(rc);
                            ghost.run();

                        }
                        else if (myType == SUPPLY_DEPOT)
                        {
                            SupplyDepot supplyDepot = new SupplyDepot(rc);
                            supplyDepot.run();
                        }
                        else if (myType == MULECALLDOWN)
                        {
                            MULE mule = new MULE(rc, true);
                            mule.run();
                        }
                        else if (myType == SENSORTOWER)
                        {
                            SensorTower sensorTower = new SensorTower(rc, true);
                            sensorTower.run();
                        }
                        else if (myType == MARINES)
                        {
                            Marines marines = new Marines(rc);
                            marines.run();
                        }
                        else if (myType == HELLION)
                        {
                            Hellion hellion = new Hellion(rc, false);
                            hellion.run();
                        }
                        else if (myType == THOR)
                        {
                            Thor thor = new Thor(rc, true);
                            thor.run();
                        }
                        else if (myType == BATTLECRUISER)
                        {
                            BattleCruiser battleCruiser = new BattleCruiser(rc);
                            battleCruiser.run();
                        }
                        else if (myType == THOR2)
                        {
                            Thor thor = new Thor(rc, false);
                            thor.run();
                        }
                        else if (myType == HELLION2)
                        {
                            Hellion hellion = new Hellion(rc, true);
                            hellion.run();
                        }
                        else if (myType == SCV2)
                        {
                            SCV scv = new SCV(rc);
                            scv.run();
                        }
                        else if (myType == MARAUDER)
                        {
                            Marauder marauder = new Marauder(rc);
                            marauder.run();
                        }
                        else if (myType == CENTERMULE)
                        {
                            CenterMULE centerMULE = new CenterMULE((rc));
                            centerMULE.run();
                        }
                        else if (myType == CENTERTOWER)
                        {
                            CenterTower centerTower = new CenterTower(rc);
                            centerTower.run();
                            //Hellion hellion = new Hellion(rc, true);
                            //hellion.run();
                        }
                        else if (myType == HQTOWER)
                        {
                            HQTower hqTower = new HQTower(rc);
                            hqTower.run();
                        }
                        else if (myType == BANSHEE)
                        {
                            Banshee banshee = new Banshee(rc);
                            banshee.run();
                        }
                        else if (myType == MISSILETURRET)
                        {
                            if (rc.sensePastrLocations(rc.getTeam().opponent()).length > 0)
                            {
                                MissileTurret missileTurret = new MissileTurret(rc, rc.sensePastrLocations(rc.getTeam().opponent())[0]);
                                missileTurret.run();
                            }
                            else
                            {
                                MissileTurret missileTurret = new MissileTurret(rc, rc.senseEnemyHQLocation());
                                missileTurret.run();
                            }
                        }
                        //Duran leader to kill enemy pastr
                        else
                        {
                            Duran samir = new Duran(rc);
                            samir.run();
                        }
                    }
                } catch (Exception e)
                {
                    e.printStackTrace();
                    System.out.println("Soldier Exception");
                }
                rc.yield();
            }
        }
    }
}
