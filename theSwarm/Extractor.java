package theSwarm;

import battlecode.common.Direction;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import battlecode.common.TerrainTile;

/**
 * Created by fredkneeland on 1/16/14.
 */
public class Extractor
{
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

    RobotController rc;
    MapLocation towerSpot;

    public Extractor(RobotController rc, int type)
    {
        this.rc = rc;
        try
        {
            int loc = 0;//rc.readBroadcast(towerLoc);
            if (loc == 0)
            {
                towerSpot = TowerUtil.bestSpot3(rc);
                Direction[] dirs = Direction.values();
                for(int k = 0; k < dirs.length; k++)
                {
                	if(rc.senseTerrainTile(towerSpot.add(dirs[k])) != TerrainTile.VOID)
                	{
                		towerSpot = towerSpot.add(dirs[k]);
                		break;
                	}
                }
            }
            else
            {
                towerSpot = Movement.convertIntToMapLocation(loc);
            }

            int pastrSpotInt = rc.readBroadcast(pastLoc);
            MapLocation pastrSpot = Movement.convertIntToMapLocation(pastrSpotInt);


            if(type < 0 || (pastrSpotInt != 0 && pastrSpot.distanceSquaredTo(towerSpot) > 10))
            {
                towerSpot = TowerUtil.getOppositeSpot(rc, towerSpot);
            }

            if (rc.readBroadcast(towerLoc) == 0)
            {
                rc.broadcast(towerLoc, Movement.convertMapLocationToInt(towerSpot));
            }


            rc.broadcast(needNoiseTower, 0);
        } catch (Exception e) {}
        
        rc.setIndicatorString(0, "Extractor");
    }

    public void run()
    {
        while (true)
        {
            try
            {
            	rc.setIndicatorString(1, " " + towerSpot);
                if (rc.isActive())
                {
                    Robot[] nearByAllies = rc.senseNearbyGameObjects(Robot.class, 35, rc.getTeam());

                    for (int i = nearByAllies.length; --i>=0;)
                    {
                        if (rc.senseRobotInfo(nearByAllies[i]).type == RobotType.NOISETOWER)
                        {
                            Hydralisk hydralisk = new Hydralisk(rc);
                            hydralisk.run();
                        }
                        else if (rc.senseRobotInfo(nearByAllies[i]).isConstructing)
                        {
                            Hydralisk hydralisk = new Hydralisk(rc);
                            hydralisk.run();
                        }
                    }

                    if (rc.canSenseSquare(towerSpot))
                    {
                        Robot bot = (Robot) rc.senseObjectAtLocation(towerSpot);
                        if (bot != null && rc.senseRobotInfo(bot).team == rc.getTeam() && rc.senseRobotInfo(bot).isConstructing)
                        {
                            Hydralisk hydralisk = new Hydralisk(rc);
                            hydralisk.run();
                        }
                    }

                    Robot[] enemies = rc.senseNearbyGameObjects(Robot.class, 35, rc.getTeam().opponent());
                    MapLocation[] alliedBots = FightMicro.locationOfBots(rc, nearByAllies);
                    if (enemies.length > 0)
                    {
                        Movement.fire(rc, enemies, alliedBots);
                    }
                    else if (rc.getLocation().distanceSquaredTo(towerSpot) < 1)
                    {
                        rc.construct(RobotType.NOISETOWER);
                    }
                    else
                    {
                        Movement.MoveMapLocation(rc, towerSpot, false, false);
                    }
                }
            } catch (Exception e) {}
        }
    }
}
