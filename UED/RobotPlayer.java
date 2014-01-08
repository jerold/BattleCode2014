package UED;

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


    public static void run(RobotController rc)
    {
        while(true) {
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
            if (rc.getType() == RobotType.SOLDIER)
            {
                try
                {
                    if (rc.isActive())
                    {
                        if (myType == 0)
                        {
                            myType = rc.readBroadcast(1);
                        }
                        // powerful squad to out muscle enemy
                        if (myType == GOLIATH)
                        {

                        }
                        // trooops to support Duran
                        else if (myType == GHOST)
                        {
                            Ghost ghost = new Ghost(rc);
                            ghost.run();

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
