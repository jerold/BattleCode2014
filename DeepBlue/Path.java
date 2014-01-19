package DeepBlue;

import battlecode.common.*;

import javax.sound.midi.SysexMessage;
import java.util.ArrayList;

/**
 * Created by AfterHours on 1/18/14.
 */
public class Path {

    public static MapLocation[] simplePath(RobotController rc, RoadMap map, MapLocation origin, MapLocation destination) throws GameActionException
    {
//        System.out.println("START PATH");
        ArrayList<MapLocation> roughPath = new ArrayList<MapLocation>();
        MapLocation stepLocation = origin;
        roughPath.add(stepLocation);
        int stepNumber = 1;
        boolean blocked = false;
        while (!blocked && stepLocation.x != destination.x && stepLocation.y != destination.y) {
            // MapLocation nextStepLocation = stepLocation.add(stepLocation.directionTo(destination));
            MapLocation nextStepLocation = stepLocation.add(map.directionTo(stepLocation, destination));

            for (MapLocation loc:roughPath)
                if (loc.x == nextStepLocation.x && loc.y == nextStepLocation.y)
                    blocked = true;

            if (!blocked) {
                stepLocation = nextStepLocation;
                roughPath.add(stepLocation);
            }
        }

        MapLocation[] returnPath = null;
        if (!blocked) {
            returnPath = new MapLocation[roughPath.size()];
            for (int i=0; i<roughPath.size();i++)
                returnPath[i] = roughPath.get(i);
        }
//        System.out.println("FINISH PATH");
        return returnPath;
    }

    public static void printPath(MapLocation[] path, RoadMap map)
    {
        if (path == null || path.length < 1)
            return;

        System.out.println("Printing Path");
        boolean pathBlank;
        for (int x=0; x<map.MAP_WIDTH;x++) {
            for (int y=0; y<map.MAP_HEIGHT;y++) {
                pathBlank = false;
                for (MapLocation loc:path) {
                    if (loc.x == x && loc.y == y)
                        pathBlank = true;
                }
                if (pathBlank)
                    System.out.print("XX");
                else if (map.roadMap[x][y] == map.TILE_VOID)
                    System.out.print("..");
                else
                    System.out.print("  ");
            }
            System.out.println("");
        }
        System.out.println("");
    }
}
