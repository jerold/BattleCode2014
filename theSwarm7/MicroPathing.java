package theSwarm7;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;

/**
 * Created by Jerold Albertson on 1/12/14.
 * Modified from code in BattleCode 2014 Lectures to use fixed length array Vs. ArrayList<MapLocation>
 */
public class MicroPathing {

    static int MAX_TRAIL_LENGTH = 4;
    static MapLocation[] trail = new MapLocation[MAX_TRAIL_LENGTH];
    static int headIndex = 0;
    static int trailLength = 0;
    static Direction allDirections[] = Direction.values();
    static int directionalLooks[] = new int[]{0,1,-1,2,-2,3,-3,4};

    public static boolean canMove(Direction dir, boolean selfAvoiding,RobotController rc){
        //include both rc.canMove and the snail Trail requirements
        if(selfAvoiding){
            MapLocation resultingLocation = rc.getLocation().add(dir);
            for(int i=0;i<trailLength;i++){
                MapLocation m = getLocationFromTrail(i);
                if(!m.equals(rc.getLocation())){
                    if(resultingLocation.isAdjacentTo(m)||resultingLocation.equals(m)){
                        return false;
                    }
                }
            }
        }
        return rc.canMove(dir);
    }

    public static Direction getNextDirection(Direction chosenDirection,boolean selfAvoiding,RobotController rc) throws GameActionException{
        while(trailLength<2)
            addLocationToTrail(new MapLocation(-1, -1));
        if(rc.isActive()){
            addLocationToTrail(rc.getLocation());
            for(int directionalOffset:directionalLooks){
                int forwardInt = chosenDirection.ordinal();
                Direction trialDir = allDirections[(forwardInt+directionalOffset+8)%8];
                if(canMove(trialDir,selfAvoiding,rc)){
                    return trialDir;
                }
            }
        }
        return chosenDirection;
    }

    static MapLocation getLocationFromTrail(int index)
    {
        return trail[(headIndex-index)%MAX_TRAIL_LENGTH];
    }

    static void addLocationToTrail(MapLocation loc)
    {
        trailLength = trailLength+1 < MAX_TRAIL_LENGTH ? trailLength+1 : MAX_TRAIL_LENGTH;
        headIndex++;
        trail[headIndex%MAX_TRAIL_LENGTH] = loc;
    }
}
