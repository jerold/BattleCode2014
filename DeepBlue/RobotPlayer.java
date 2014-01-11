package DeepBlue;

import battlecode.common.RobotController;
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
                    AlexeiStukov.run(rc);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    System.out.println("HQ Exception");
                }
            }
            else if(rc.getType() == RobotType.NOISETOWER)
            {
                new SensorTower(rc).run();
            }
            if (rc.getType() == RobotType.SOLDIER)
            {
                try
                {
                    if (rc.isActive())
                    {
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
                            MULE mule = new MULE(rc);
                            mule.run();
                        }
                        else if (myType == SENSORTOWER)
                        {
                            SensorTower sensorTower = new SensorTower(rc);
                            sensorTower.run();
                        }
                        else if (myType == MARINES)
                        {
                            Marines marines = new Marines(rc);
                            marines.run();
                        }
                        else if (myType == HELLION)
                        {
                            Hellion hellion = new Hellion(rc);
                            hellion.run();
                        }
                        else if (myType == THOR)
                        {
                            Thor thor = new Thor(rc);
                            thor.run();
                        }
                        else if (myType == BATTLECRUISER)
                        {
                            BattleCruiser battleCruiser = new BattleCruiser(rc);
                            battleCruiser.run();
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
            }
        }
    }
}
