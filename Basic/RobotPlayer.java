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

// Created by Matt Johnerson on 1/8/2014

public class RobotPlayer {
	
	static RobotController rc;
	static int soldiers = 0;
	static int myType = 0;
	static final int PASTR = 1;
	static final int ATTACKER = 2;
	static final int GUARD = 3;
	
	
	public static void run(RobotController rcin){
		rc = rcin;
		
		while(true){
			try{
				if(rc.getType() == RobotType.HQ){
					//all Headquarters methods
					tryToShoot();
					tryToSpawn();
					broadcast();
					
				}else if(rc.getType() == RobotType.SOLDIER){
					//all Soldier methods
					sTryToShoot();
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
	private static void readBroadcast() {
		// TODO Auto-generated method stub
		
	}
	private static void broadcast() throws GameActionException {
		// HQ method. broadcast messages to Soldiers
		if(soldiers < 3){
			rc.broadcast(1, PASTR);
		}
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
						rc.spawn(spawnDir);
						soldiers++;
					}
				}
			}
		}
	}
}
