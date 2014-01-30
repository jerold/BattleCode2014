package Basic;


import java.util.Random;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.TerrainTile;

// Created by Matt Johnerson on 1/8/2014

public class RobotPlayer {
	
	static RobotController rc;
	static int soldiers = 0;
	static int myType = 0;
	static final int PASTR = 1;
	static final int ATTACKER = 2;
	static final int GUARD = 3;
	static MapLocation[] roads = new MapLocation[100];
	static MapLocation[] tempRoads = new MapLocation[100];
	static MapLocation traveledOn;
	static boolean initializeRoads = false;
	static MapLocation cRoad;
	static int roadsIndex = 0;
	static boolean onRoad = false;
	
	//Need these variables for the methods I wrote
	static int allIndex = 0;
	static MapLocation[] allPastrs = new MapLocation[100];
	static MapLocation[] currentPastrs;
	static MapLocation[] previousPastrs;
	static MapLocation sameLoc;
	static boolean waitOutSideRange = false;
	static boolean found = false;
	static long[] teamMemory;
	
	
	public static void run(RobotController rcin){
		rc = rcin;
		
		while(true){
			try{
				if(rc.getType() == RobotType.HQ){
					//will also need these variables
					long[] teamMemory = rc.getTeamMemory();
					MapLocation test = Basic.TowerUtil.bestSpot3(rc);
					MapLocation testOpposite = Basic.TowerUtil.getOppositeSpot(rc, test);
					MapLocation[] currentPastrs = rc.sensePastrLocations(rc.getTeam().opponent());
					Robot[] robots  = rc.senseBroadcastingRobots();
					if(robots.length > 0){
						//System.out.println(robots[0].getID());
					}
					
					if(Basic.Utilities.checkRush(rc) == true)
                    {
						//System.out.println("RUSH!");
					}else
                    {
						//System.out.println("Dont rush");
					}
					if(Basic.Utilities.checkDoublePastr(rc, test, testOpposite) == true){
						//System.out.println("Double Pastr: Best spot: " + test.x + ", " + test.y);
					} else {
						//System.out.println("No double pastr: Best spot: " + test.x + ", " + test.y);
					}
					int score = Basic.TowerUtil.getSpotScore(rc, rc.senseHQLocation());
					if(Basic.Utilities.checkHQTower(rc) == true){
						//System.out.println("HQ tower!");
					} else {
						//System.out.println("No HQ tower, score = " + score);
					}
					//PASTR finder
					if(currentPastrs.length > 0 && currentPastrs.length > previousPastrs.length){
						if(previousPastrs.length == 0){
							for(int k = 0; k < currentPastrs.length; k++){
								allPastrs[allIndex] = currentPastrs[k];
								//System.out.println("pastr added at loc: " + allPastrs[allIndex].x + ", " + allPastrs[allIndex].y);
								allIndex++;
							}
							previousPastrs = currentPastrs;
						} else {
							for(int i = previousPastrs.length; i < currentPastrs.length; i++){
								allPastrs[allIndex] = currentPastrs[i];
								//System.out.println("PASTR added at loc: " + allPastrs[allIndex].x + ", " + allPastrs[allIndex].y);
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
						//System.out.println("Found same pastr loc @: " + sameLoc.x + ", " + sameLoc.y);
						found = true;
						rc.setTeamMemory(0, 1);
					}
					if(teamMemory[0] == 1){
						//System.out.println("Setting Location...");
						waitOutSideRange = true;
					}
					//end PASTR finder/ other useful stuff
					
					tryToShoot();
					tryToSpawn();
					
					
					cRoad = rc.getLocation();
					if(initializeRoads == false){
						for(int i = rc.getLocation().x - 5; i < rc.getLocation().x + 5; i++){//For all X values
							for(int j = rc.getLocation().y - 5; j < rc.getLocation().y + 5; j++){
								MapLocation current = new MapLocation(i,j);
								if(rc.senseTerrainTile(current).equals(TerrainTile.ROAD))
                                {
									roads[roadsIndex] = current;
									roadsIndex++;
								}
							}
						}
						if(roads[0] != null){
							cRoad = roads[0];
						}
						for(int k = 0; k < roadsIndex; k++){
							MapLocation current = roads[k];
							if(current.distanceSquaredTo(rc.senseHQLocation()) < cRoad.distanceSquaredTo(rc.senseHQLocation())){
								cRoad = current;
							}
						}
						int intRoadLocation = Basic.Utilities.convertMapLocationToInt(cRoad);
						rc.broadcast(1, intRoadLocation);
						initializeRoads = true;
					}
					
				}else if(rc.getType() == RobotType.SOLDIER){
					
					//all Soldier methods
					sTryToShoot();
					//if(Basic.Utilities.checkHQTower(rc) == true){
					//	System.out.println("HQ Tower!");
					//}
					readBroadcast();
					
				}
				rc.yield();
			}catch(Exception e){
				e.printStackTrace();
				System.out.println("RobotPlayer ERROR");
			}
			
			
		}
	}
	private static void sTryToShoot() {
		// TODO Auto-generated method stub
		
	}
	private static void readBroadcast() throws GameActionException {
		if(rc.isActive()){
			MapLocation[] targetPastrs = rc.sensePastrLocations(rc.getTeam().opponent());
			Direction dir = rc.getLocation().directionTo(rc.senseEnemyHQLocation());
			
			Direction toHQ = rc.getLocation().directionTo(rc.senseHQLocation());
			
			if(waitOutSideRange == true){
				System.out.println("Waiting for prey");
				dir = rc.getLocation().directionTo(targetPastrs[0].add(toHQ).add(toHQ).add(toHQ).add(toHQ).add(toHQ));
			} else if(targetPastrs.length > 0){
				MapLocation target = targetPastrs[0];
				dir = rc.getLocation().directionTo(target);
			} else {
				dir = rc.getLocation().directionTo(rc.senseHQLocation());
			}
			
			int roadLocation = rc.readBroadcast(1);
			if(Basic.Utilities.fightMode(rc)){
				
			} else if(dir != Direction.OMNI && dir != Direction.NONE){
					if(rc.canMove(dir)){
						rc.move(dir);
					}
				}
			}
			
			/*
			else if(onRoad == false){
				MapLocation road = Basic.Utilities.convertIntToMapLocation(roadLocation);
				Direction dir = rc.getLocation().directionTo(road);
				if(dir != Direction.OMNI && dir != Direction.NONE){
					if(rc.canMove(dir)){
						rc.move(dir);
					}
				}
				if(rc.senseTerrainTile(rc.getLocation()) == TerrainTile.ROAD){
					onRoad = true;
				}
			} 
			else{	
				MapLocation fRoad = rc.getLocation();
				int tempRoadsIndex = 0;
				for(int i = rc.getLocation().x - 5; i < rc.getLocation().x + 5; i++){//For all X values
					for(int j = rc.getLocation().y - 5; j < rc.getLocation().y + 5; j++){//For all Y values
						MapLocation current = new MapLocation(i,j);
						if(rc.senseTerrainTile(current).equals(TerrainTile.ROAD)){
							tempRoads[tempRoadsIndex] = current;
							tempRoadsIndex++;
						}
					}
				}
				if(tempRoads[0] != null){//tests if tempRoads[] is empty
					fRoad = tempRoads[0];
				}
				for(int k = 0; k < tempRoadsIndex; k++){//finds the farthest road location from the current soldier, that is also towards the target destination
					MapLocation current = tempRoads[k];
					if(current.distanceSquaredTo(rc.getLocation()) > fRoad.distanceSquaredTo(rc.getLocation()) && current.distanceSquaredTo(target) < fRoad.distanceSquaredTo(target)){
						fRoad = current;
					}
				}
				if(rc.getLocation() == fRoad){//Leave the road and go to the target destination
					Direction diverge = rc.getLocation().directionTo(target);
					if(diverge!= Direction.OMNI && diverge != Direction.NONE){
						if(rc.canMove(diverge)){
							rc.move(diverge);
						}
					}
				}
				else{
					Direction findNext = rc.getLocation().directionTo(rc.getLocation().add(Direction.NORTH));
					MapLocation search = rc.getLocation().add(findNext);
					while(rc.senseTerrainTile(search)!=(TerrainTile.ROAD) && rc.getLocation().add(findNext) != traveledOn){
						findNext = findNext.rotateLeft();
						search = rc.getLocation().add(findNext);
					}
					if(findNext != Direction.OMNI && findNext != Direction.NONE){
						if(rc.canMove(findNext) && rc.getLocation().add(findNext) != traveledOn){
							traveledOn = rc.getLocation();
							rc.move(findNext);
						}
					}
				}
			}
		}
		*/
	}


	
	private static void tryToShoot() throws GameActionException {
		// HQ method. shoot anyone in range
		Robot[] enemyRobots = rc.senseNearbyGameObjects(Robot.class, 10000, rc.getTeam().opponent());
		if(enemyRobots.length>0){
			Robot anEnemy = enemyRobots[0];
			RobotInfo anEnemyInfo;
			anEnemyInfo = rc.senseRobotInfo(anEnemy);
			if(anEnemyInfo.location.distanceSquaredTo(rc.getLocation())<rc.getType().attackRadiusMaxSquared){
				rc.attackSquare(anEnemyInfo.location);
			}
		}
		
		
	}
	private static void tryToSpawn() throws GameActionException {
		// HQ method. spawns robots in the spawn direction. if the HQ cannot spawn, rotate direction left and try again.
		// Also keeps track of how many robots have been spawned.
		if(rc.isActive()){
			Direction spawnDir = rc.getLocation().directionTo(rc.senseEnemyHQLocation());
			if(rc.canMove(spawnDir)){
				if(rc.senseRobotCount() <=25){
					rc.spawn(spawnDir);
					soldiers++;
				}
			} else {
				for(int i = 0; i < 7; i++){
					spawnDir = spawnDir.rotateLeft();
					if(rc.canMove(spawnDir)){
						if(rc.isActive() && rc.senseRobotCount() <=25){
							rc.spawn(spawnDir);
							soldiers++;
						}
					}
				}
			}
		}
	}
}
