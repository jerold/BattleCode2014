package DeepBlue;

import battlecode.common.*;

/**
 * Created by Jerold Albertson on 1/12/14.
 * Modified from code in BattleCode 2014 Lectures to use fixed length array Vs. ArrayList<MapLocation>
 */
public class MicroPathing {

    static int MAX_TRAIL_LENGTH = 8;
    static MapLocation[] trail = new MapLocation[MAX_TRAIL_LENGTH];
    static int headIndex = 0;
    static int trailLength = 0;

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

    public static void tryToMove(Direction chosenDirection,boolean selfAvoiding,RobotController rc, int[] directionalLooks, Direction[] allDirections) throws GameActionException{
        while(trailLength<2)
            addLocationToTrail(new MapLocation(-1, -1));
        if(rc.isActive()){
            addLocationToTrail(rc.getLocation());
            for(int directionalOffset:directionalLooks){
                int forwardInt = chosenDirection.ordinal();
                Direction trialDir = allDirections[(forwardInt+directionalOffset+8)%8];
                if(canMove(trialDir,selfAvoiding,rc)){
                    rc.move(trialDir);
                    break;
                }
            }
        }
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
