package UED2;

import battlecode.common.*;

import java.util.Random;

/**
 * Created by fredkneeland on 1/9/14.
 */
public class SensorTower
{
    RobotController rc;
    int width, height, corner;
    int[] radii;
    MapLocation target;
    Random rand = new Random();
    Direction[] directions = {Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST, Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST};
    boolean corner1;

    public SensorTower(RobotController rc, boolean corner1)
    {
        this.rc = rc;
        this.corner1 = corner1;
        corner = Utilities.findBestCorner(rc);
        width = rc.getMapWidth();
        height = rc.getMapHeight();
        int[] radii = {15, 13, 11, 9, 7};
        this.radii = radii;
        rc.setIndicatorString(0, "SensorTower");
    }

    public void run()
    {
        while(true)
        {
            if(rc.getType() == RobotType.SOLDIER)
            {

                    try
                    {
                        target = Utilities.spotOfSensorTower(rc, corner1);

                        //Utilities.AvoidEnemiesMoveMapLocation(rc, target, true);
                        Utilities.MoveMapLocation(rc, target, true);

                        if(rc.isActive())
                        {
                            rc.construct(RobotType.NOISETOWER);
                        }
                    }
                    catch(Exception e){}

            }
            else
            {
                try
                {
                    rc.setIndicatorString(0, "Tower");
                    Robot[] enemies = rc.senseNearbyGameObjects(Robot.class, 30, rc.getTeam().opponent());
                    Robot[] allies = rc.senseNearbyGameObjects(Robot.class, 30, rc.getTeam());
                    MapLocation pastrA = null;
                    MapLocation pastrE = null;
                    boolean enemyPastr = false;
                    boolean allyPastr = false;

                    for(int k = 0; k < enemies.length; k++)
                    {
                        if(rc.senseRobotInfo(enemies[k]).type == RobotType.PASTR)
                        {
                            enemyPastr = true;
                            pastrA = rc.senseRobotInfo(enemies[k]).location;
                        }
                    }
                    for(int k = 0; k < allies.length; k++)
                    {
                        if(rc.senseRobotInfo(allies[k]).type == RobotType.PASTR)
                        {
                            allyPastr = true;
                            pastrA = rc.senseRobotInfo(allies[k]).location;
                        }
                    }
                    if(!enemyPastr && allyPastr)
                    {
                        for(int k = 20; k > 4; k -= 2)
                        {
                            Utilities.fireCircle(rc, k, pastrA);
                        }
                    }
                    else if(enemyPastr)
                    {
                        rc.attackSquare(pastrE);
                    }
                }
                catch(Exception e){}
            }

            rc.yield();
        }
    }

    private void fireArcs()
    {
        for(int k = 0; k < radii.length; k++)
        {
            for(int a = 0; a <= radii[k]; a+= 4)
            {
                while(!rc.isActive()){}
                try
                {
                    switch(corner)
                    {
                        case 1:
                            rc.attackSquare(new MapLocation(a, radii[k]));
                            break;
                        case 2:
                            rc.attackSquare(new MapLocation(width - a + 1, radii[k]));
                            break;
                        case 3:
                            rc.attackSquare(new MapLocation(a, height - radii[k] + 1));
                            break;
                        default:
                            rc.attackSquare(new MapLocation(width - a + 1, height - radii[k] + 1));
                            break;
                    }
                }
                catch(Exception e){}

                rc.yield();

                while(!rc.isActive()){}
                try
                {
                    switch(corner)
                    {
                        case 1:
                            rc.attackSquare(new MapLocation(radii[k], a));
                            break;
                        case 2:
                            rc.attackSquare(new MapLocation(width - radii[k] + 1, a));
                            break;
                        case 3:
                            rc.attackSquare(new MapLocation(radii[k], height - a + 1));
                            break;
                        default:
                            rc.attackSquare(new MapLocation(width - radii[k] + 1, height - a + 1));
                            break;
                    }
                }
                catch(Exception e){}

                rc.yield();
            }

            while(!rc.isActive()){}
            try
            {
                switch(corner)
                {
                    case 1:
                        rc.attackSquare(new MapLocation(radii[k], radii[k]));
                        break;
                    case 2:
                        rc.attackSquare(new MapLocation(width - radii[k] + 1, radii[k]));
                        break;
                    case 3:
                        rc.attackSquare(new MapLocation(radii[k], height - radii[k] + 1));
                        break;
                    default:
                        rc.attackSquare(new MapLocation(width - radii[k] + 1, height - radii[k] + 1));
                        break;
                }
            }
            catch(Exception e){}

            rc.yield();
        }
    }
}
