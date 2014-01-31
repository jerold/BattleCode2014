package DeepBlue;

import battlecode.common.*;
import com.sun.corba.se.spi.activation._InitialNameServiceImplBase;

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
    static boolean surround = false;
    static int count = 0;
    static boolean setUp = false;
    static int setUpCount = 0;
    static boolean inefficient = false;
    static boolean criticalMass = false;

    static MapLocation[] rallyPoints = new MapLocation[2];

    static Direction allDirections[] = Direction.values();
    static int directionalLooks[] = new int[]{0,1,-1,2,-2,3,-3,4};
    static int allIndex = 0;
	static MapLocation[] allPastrs = new MapLocation[100];
	static MapLocation[] currentPastrs;
	static MapLocation[] previousPastrs;
	static MapLocation sameLoc;
	static boolean waitOutSideRange = false;
	static boolean found = false;
	static final int samePastr = 35675;
	
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
            while (!rc.isActive()) rc.yield();
            Direction dir = Direction.NORTH;
            while (!rc.canMove(dir)) dir = dir.rotateRight();
            rc.spawn(dir);

            while (true) {
                try
                {
                    if (rc.isActive())
                    {
                    	MapLocation[] currentPastrs = rc.sensePastrLocations(rc.getTeam().opponent());
                        if(currentPastrs.length > 0 && currentPastrs.length > previousPastrs.length){
            				if(previousPastrs.length == 0){
            					for(int k = 0; k < currentPastrs.length; k++){
            						allPastrs[allIndex] = currentPastrs[k];
            						System.out.println("pastr added at loc: " + allPastrs[allIndex].x + ", " + allPastrs[allIndex].y);
            						allIndex++;
            					}
            					previousPastrs = currentPastrs;
            				} else {
            					for(int i = previousPastrs.length; i < currentPastrs.length; i++){
            						allPastrs[allIndex] = currentPastrs[i];
            						System.out.println("PASTR added at loc: " + allPastrs[allIndex].x + ", " + allPastrs[allIndex].y);
            						allIndex++;
            					}
            					previousPastrs = currentPastrs;
            				}		
            			} else {
            				previousPastrs = currentPastrs;
            			}
            			
            			if(allPastrs.length > 0 && sameLoc == null){
            				for(int j = 0; j < allIndex; j++){
            					MapLocation search = allPastrs[j];
            					for(int n = 0; n < allIndex; n++){
            						if(search.equals(allPastrs[n]) && j != n){
            							sameLoc = search;
            						}
            					}
            				}
            			}
            			if(sameLoc != null && !found){
            				System.out.println("Found same pastr loc @: " + sameLoc.x + ", " + sameLoc.y);
            				found = true;
            				rc.setTeamMemory(0, 1);
            				rc.broadcast(samePastr, 1);
            			}
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
        if (rallyPoints[rpNumber] != null) System.out.println("New Rally: "+map.idForNearestNode(rally)+"["+rally+"]  Old Rally: "+map.idForNearestNode(rallyPoints[rpNumber])+"["+rallyPoints[rpNumber]+"]");
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


            Robot[] nearByEnemies = rc.senseNearbyGameObjects(Robot.class, 35, rc.getTeam().opponent());

            // we must not let the enemy pull off a hq surround
            if (nearByEnemies.length > 1 || surround)
            {
                // our first unit tries to draw the enemy in to around our hq
                if (count == 0)
                {
                    type = Utilities.unitNeededBlockadeRunner;
                }
                // the rest build up a force to run the blockade
                else
                {
                    type = Utilities.unitNeededBlockadeRunner;
                }
                count++;
                if (count < 8)
                {
                    surround = true;
                }
                else
                {
                    surround = false;
                }

            }
            // if we are going for hq tower build
            else if (Utilities.checkHQTower(rc))
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
                // small map
                else if (rc.getMapHeight() * rc.getMapWidth() < 900)
                {
                    type = Utilities.unitNeededHQSurround;
                }
                // medium map
                else if (rc.getMapWidth() * rc.getMapHeight() < 25000)
                {
                    type = Utilities.unitNeededPastrKiller;
                }
                // large map
                else if (rc.getMapWidth() * rc.getMapHeight() < 10000)
                {
                    // we set up a pastr noise tower combo
                    if (numbOfSoldiers < 4)
                    {
                        towerPastrRequest.startBuilding(rc);
                    }
                    else
                    {
                        towerPastrRequest.endBuilding(rc);
                        type = Utilities.unitNeededPastrKiller;
                    }
                }
                else
                {
                    type = Utilities.unitNeededPastrKiller;
                }
            }
            // small map no hq tower
            else if (rc.getMapHeight() * rc.getMapWidth() < 900)
            {
                // set up troops until we hit critical mass
                if (rc.senseRobotCount() < 10)
                {
                    type = Utilities.unitNeededHQSurround;
                }
                else if (!setUp)
                {
                    setUpCount = numbOfSoldiers;
                    setUp = true;
                    towerPastrRequest.startBuilding(rc);
                }
                else if (setUp && (setUpCount-numbOfSoldiers > 1))
                {
                    towerPastrRequest.endBuilding(rc);
                }
            }
            // medium map no hq tower
            else if (rc.getMapHeight() * rc.getMapWidth() < 2500)
            {
                // first we build a group of troops
                if (numbOfSoldiers < 6)
                {
                    type = Utilities.unitNeededReinforcement;
                }
                // if the enemy has set up pastrs kill them
                else if (rc.sensePastrLocations(rc.getTeam().opponent()).length > 0)
                {
                    type = Utilities.unitNeededPastrKiller;
                }
                // otherwise we set up pastrs
                else if (!setUp)
                {
                    setUp = true;
                    towerPastrRequest.startBuilding(rc);
                }
                else if (inefficient)
                {
                    towerPastrRequest.endBuilding(rc);
                }
                else
                {
                    type = Utilities.unitNeededPastrKiller;
                }

            }
            // large map no hq tower
            else if (rc.getMapHeight() * rc.getMapWidth() < 10001)
            {
                if (!setUp)
                {
                    towerPastrRequest.startBuilding(rc);
                    setUp = true;
                }
                if (inefficient)
                {
                    towerPastrRequest.endBuilding(rc);
                }
                else if (inefficient && rc.senseRobotCount() > 10 && !criticalMass)
                {
                    towerPastrRequest.startBuilding(rc);
                    criticalMass = true;
                }

                type = Utilities.unitNeededPastrKiller;
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
            
            //type = Utilities.unitNeededPastrKiller;
            rc.broadcast(Utilities.unitNeededChannel, type);
        }
    }
}
