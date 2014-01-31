package DeepBlue;

import battlecode.common.*;

/**
 * Created by Jerold Albertson on 1/12/14.
 *
 */
public class Headquarter {
    static RobotController rc;
    static UnitCache cache;
    static RoadMap map;
    static int numbOfSoldiers = 0;
    static boolean started = false;
    static int round = 0;

    static MapLocation[] rallyPoints = new MapLocation[2];

    static Direction allDirections[] = Direction.values();
    static int directionalLooks[] = new int[]{0,1,-1,2,-2,3,-3,4};

    public static void run(RobotController inRc)
    {
        try
        {
            rc = inRc;
            cache = new UnitCache(rc);
            map = new RoadMap(rc, cache);
            System.out.println("My HQ:" + cache.MY_HQ + ", Enemy HQ:" + cache.ENEMY_HQ);


            // TEST RALLY POINT
            setRallyPoint(new MapLocation(map.MAP_WIDTH/2, map.MAP_HEIGHT/2), Utilities.FrontLineRally);
            setRallyPoint(cache.ENEMY_HQ, Utilities.ReinforcementRally);

            //setUnitNeeded(Soldiers.UnitStrategyType.PastrBuilder);

            while (true) {
                try
                {
                    if (rc.isActive())
                    {
                        Robot[] allVisibleEnemies = rc.senseNearbyGameObjects(Robot.class, 35, rc.getTeam().opponent());
                        int counter = 0;

                        if (allVisibleEnemies.length > 0)
                        {
                            while (counter < 5)
                            {
                                FightMicro.hqFire(rc);
                                counter++;
                                rc.yield();
                            }
                        }


                        cache.reset();
                        map.checkForUpdates();

                        if (rc.isActive())
                        {
                            tryToSpawn();
                        }
                    }

                } catch (Exception e) {e.printStackTrace();}

                rc.yield();
            }
        } catch (Exception e) {e.printStackTrace();}
    }

    public static void tryToSpawn() throws GameActionException {
        if(rc.isActive()&&rc.senseRobotCount()<GameConstants.MAX_ROBOTS){ // if(rc.isActive()&&rc.senseRobotCount()<2){
            for(int i=0;i<8;i++){
                Direction trialDir = allDirections[(cache.MY_HQ.directionTo(cache.ENEMY_HQ).ordinal()+i)%8];
                if(rc.canMove(trialDir)){
                    setUnitNeeded(null, rc);
                    rc.spawn(trialDir);
                    break;
                }
            }
        }
    }

    public static void setRallyPoint(MapLocation rally, int rpNumber) throws GameActionException
    {
        //if (rallyPoints[rpNumber] != null) System.out.println("New Rally: "+map.idForNearestNode(rally)+"["+rally+"]  Old Rally: "+map.idForNearestNode(rallyPoints[rpNumber])+"["+rallyPoints[rpNumber]+"]");
        rc.broadcast(Utilities.startRallyPointChannels+rpNumber*2, VectorFunctions.locToInt(rally));
        rallyPoints[rpNumber] = rally;
    }

    public static void setUnitNeeded(Soldiers.UnitStrategyType unitType, RobotController rc) throws GameActionException
    {
        int type = 0;

        if (rc!= null)
        {
            rc.setIndicatorString(0, ""+numbOfSoldiers);
            rc.setIndicatorString(2, ""+TowerUtil.getHQSpotScore(rc, rc.senseHQLocation()));


            if (Utilities.checkHQTower(rc))
            {
                if (numbOfSoldiers == 0 || (Clock.getRoundNum() - round > 100 && rc.senseNearbyGameObjects(Robot.class, 2, rc.getTeam()).length < 2))
                {
                    type = Utilities.unitNeededHQTower;
                }
                else if (numbOfSoldiers == 1 || (Clock.getRoundNum() - round > 100 && rc.senseNearbyGameObjects(Robot.class, 2, rc.getTeam()).length < 3))
                {
                    type = Utilities.unitNeededHQPastr;
                    round = Clock.getRoundNum();
                }
                else if (numbOfSoldiers % 2 == 0)
                {
                    type = Utilities.unitNeededHQSurround;
                }
                else
                {
                    type = Utilities.unitNeededPastrKiller;
                }
            }
            else
            {
                if (numbOfSoldiers < 5)
                {
                    type = Utilities.unitNeededScout;
                }
                else
                {
                    //type = Utilities.unitNeededHQSurround;
                    type = Utilities.unitNeededPastrKiller;
                }

                if (numbOfSoldiers > 10)
                {
                    if (!started)
                    {
                        started = true;
                        towerPastrRequest.startBuilding(rc);
                    }
                    type = Utilities.unitNeededOurPastrKiller;
                }
            }

            if (rc.senseRobotCount() > 0)
            {
                numbOfSoldiers++;
            }
            rc.broadcast(Utilities.unitNeededChannel, type);
        }
    }
}
