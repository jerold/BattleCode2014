package DeepBlue;

import battlecode.common.*;
import java.util.ArrayList;

/**
 * Created by AfterHours on 1/18/14.
 */
public class Path {

    public static boolean canSimplyPath(RoadMap map, MapLocation origin, MapLocation destination) throws GameActionException
    {
        if (origin.x > 0 && origin.x < map.MAP_WIDTH && origin.y > 0 && origin.y < map.MAP_HEIGHT)
            return false;

        if (destination.x > 0 && destination.x < map.MAP_WIDTH && destination.y > 0 && destination.y < map.MAP_HEIGHT)
            return false;

        if (map.roadMap[origin.x][origin.y] == RoadMap.TILE_VOID || map.roadMap[destination.x][destination.y] == RoadMap.TILE_VOID)
            return false;

        MapLocation stepLocation = origin;
        MapLocation lastStep = destination;
        while (!(stepLocation.x == destination.x && stepLocation.y == destination.y)) {
            MapLocation nextStepLocation = stepLocation.add(map.directionTo(stepLocation, destination));

            // Check for Doubling back
            if (nextStepLocation.x == lastStep.x && nextStepLocation.y == lastStep.y)
                return false;

            lastStep = stepLocation;
            stepLocation = nextStepLocation;
        }

        return true;
    }

    public static MapLocation[] getSimplePath(RoadMap map, MapLocation origin, MapLocation destination) throws GameActionException
    {
        if (map.roadMap[origin.x][origin.y] == RoadMap.TILE_VOID || map.roadMap[destination.x][destination.y] == RoadMap.TILE_VOID)
            return null;

        ArrayList<MapLocation> roughPath = new ArrayList<MapLocation>();
        MapLocation stepLocation = origin;
        roughPath.add(stepLocation);
        while (!(stepLocation.x == destination.x && stepLocation.y == destination.y)) {
            MapLocation nextStepLocation = stepLocation.add(map.directionTo(stepLocation, destination));

            for (MapLocation loc:roughPath)
                if (loc.x == nextStepLocation.x && loc.y == nextStepLocation.y)
                    return null;

            stepLocation = nextStepLocation;
            roughPath.add(stepLocation);
        }

        MapLocation[] returnPath = new MapLocation[roughPath.size()];
        for (int i=0; i<roughPath.size();i++)
            returnPath[i] = roughPath.get(i);

        return returnPath;
    }

    public static MapLocation[] getMacroPath(RoadMap map, MapLocation origin, MapLocation destination) throws GameActionException
    {
        if (map.pathingStrat != RoadMap.PathingStrategy.MacroPath || map.locationIsVoid(origin) || map.locationIsVoid(destination))
            return null;

        System.out.print("Macro Path:");
        int nextNode = map.idForNearestNode(origin);
        int finalNode = map.idForNearestNode(destination);


        MapLocation[] simplePath = getSimplePath(map, origin, destination);
        if (simplePath != null && simplePath.length > 0) {
            System.out.println(" -> " + finalNode);
            return new MapLocation[]{destination};
        }

        if (map.nextStep(nextNode, finalNode) == RoadMap.NO_PATH_EXISTS) {
            System.out.println(" [Cannot Path from " + nextNode + " to " + finalNode + "]");
            return null;
        }

        ArrayList<MapLocation> roughPath = new ArrayList<MapLocation>();
        roughPath.add(origin);
        roughPath.add(map.locationForNode(nextNode));
        while (nextNode != finalNode) {
            System.out.print(" -> "+nextNode);
            if (nextNode >= map.nodeCount)
                return null;
            nextNode = map.nextStep(nextNode, finalNode);
            roughPath.add(map.locationForNode(nextNode));
        }
        System.out.print(" -> "+nextNode);
        roughPath.add(map.locationForNode(nextNode));
        roughPath.add(destination);
        System.out.println("");

        MapLocation[] returnPath = new MapLocation[roughPath.size()];
        for (int i=0; i<roughPath.size();i++)
            returnPath[i] = roughPath.get(i);

        return returnPath;
    }

//    public static MapLocation[] compoundPath(RoadMap map, MapLocation origin, MapLocation destination) throws GameActionException
//    {
//        if (map.roadMap[origin.x][origin.y] == RoadMap.TILE_VOID || map.roadMap[destination.x][destination.y] == RoadMap.TILE_VOID)
//            return null;
//
//        MapLocation[] straight = straightPath(map, origin, destination);
//        int sI = 0;
//        ArrayList<MapLocation> roughPath = new ArrayList<MapLocation>();
//        while (sI < straight.length) {
//            if (map.roadMap[straight[sI].x][straight[sI].y] != RoadMap.TILE_VOID) {
//                roughPath.add(straight[sI]);
//            } else {
//                MapLocation bugStart = straight[sI-1];
//                while (map.roadMap[straight[sI].x][straight[sI].y] == RoadMap.TILE_VOID) {
//                    sI++;
//                }
//                MapLocation bugEnd = straight[sI];
//                MapLocation[] bugLeft = bugPath(map, bugStart, bugEnd, true);
//                MapLocation[] bugRight = bugPath(map, bugStart, bugEnd, false);
//                if (bugLeft.length < bugRight.length)
//                    for (MapLocation step:bugLeft)
//                        roughPath.add(step);
//                else
//                    for (MapLocation step:bugRight)
//                        roughPath.add(step);
//            }
//            sI++;
//        }
//
//        MapLocation[] returnPath = new MapLocation[roughPath.size()];
//        for (int i=0; i<roughPath.size();i++)
//            returnPath[i] = roughPath.get(i);
//
//        return returnPath;
//    }

//    public static MapLocation[] straightPath(RoadMap map, MapLocation origin, MapLocation destination) throws GameActionException
//    {
//        if (map.roadMap[origin.x][origin.y] == RoadMap.TILE_VOID || map.roadMap[destination.x][destination.y] == RoadMap.TILE_VOID)
//            return null;
//
//        ArrayList<MapLocation> roughPath = new ArrayList<MapLocation>();
//        MapLocation stepLocation = origin;
//        roughPath.add(stepLocation);
//        while (!(stepLocation.x == destination.x && stepLocation.y == destination.y)) {
//            stepLocation = stepLocation.add(stepLocation.directionTo(destination));
//            roughPath.add(stepLocation);
//        }
//
//        MapLocation[] returnPath = new MapLocation[roughPath.size()];
//        for (int i=0; i<roughPath.size();i++)
//            returnPath[i] = roughPath.get(i);
//
//        return returnPath;
//    }

    public static boolean shouldBugLeft(RoadMap map, MapLocation origin, MapLocation destination) throws GameActionException
    {
        if (map.roadMap[origin.x][origin.y] == RoadMap.TILE_VOID || map.roadMap[destination.x][destination.y] == RoadMap.TILE_VOID)
            return false;
        MapLocation bugEnd = origin.add(origin.directionTo(destination));

        while (map.roadMap[bugEnd.x][bugEnd.y] == RoadMap.TILE_VOID) {
            bugEnd = bugEnd.add(bugEnd.directionTo(destination));

        }

        MapLocation[] bugLeft = getBugPath(map, origin, bugEnd, true);
        MapLocation[] bugRight = getBugPath(map, origin, bugEnd, false);
        if (bugLeft.length < bugRight.length)
            return true;
        return false;
    }

    public static MapLocation[] getBugPath(RoadMap map, MapLocation origin, MapLocation destination, boolean leftWall) throws GameActionException
    {
        if (map.roadMap[origin.x][origin.y] == RoadMap.TILE_VOID || map.roadMap[destination.x][destination.y] == RoadMap.TILE_VOID)
            return null;

        ArrayList<MapLocation> roughPath = new ArrayList<MapLocation>();
        MapLocation stepLocation = origin;
        roughPath.add(stepLocation);
        Direction direction = stepLocation.directionTo(destination);
        int failIn = 30;
        while (failIn>0 && !(stepLocation.x == destination.x && stepLocation.y == destination.y)) {
//            System.out.print("VOID "+failIn);
            if (leftWall) {
                // If we run into a wall
                while (!validMove(map, stepLocation, direction))
                    direction = direction.rotateRight();

                // keep in contact with left wall
                if (validMove(map, stepLocation, direction.rotateLeft()))
                    direction = direction.rotateLeft();
            } else {
                // If we run into a wall
                while (!validMove(map, stepLocation, direction))
                    direction = direction.rotateLeft();

                // keep in contact with left wall
                if (validMove(map, stepLocation, direction.rotateRight()))
                    direction = direction.rotateRight();
            }

            stepLocation = stepLocation.add(direction);
            roughPath.add(stepLocation);
            failIn--;
        }

        MapLocation[] returnPath = new MapLocation[roughPath.size()];
        for (int i=0; i<roughPath.size();i++)
            returnPath[i] = roughPath.get(i);

        return returnPath;
    }

    private static boolean validMove(RoadMap map, MapLocation loc, Direction dir)
    {
        return map.roadMap[loc.add(dir).x][loc.add(dir).y] != RoadMap.TILE_VOID;
    }

    public static void printPath(MapLocation[] path, RoadMap map)
    {
        if (path != null && path.length > 0) {
            System.out.println("Macro Path");
            boolean pathTile;
            for (int y=0; y<map.MAP_HEIGHT;y++) {
                for (int x=0; x<map.MAP_WIDTH;x++) {
                    pathTile = false;
                    for (MapLocation loc:path) {
                        if (loc.x == x && loc.y == y)
                            pathTile = true;
                    }
                    if (pathTile) {
                        if (map.roadMap[x][y] == map.TILE_VOID)
                            System.out.print("XX");
                        else
                            System.out.print("++");
                    } else if (map.roadMap[x][y] == map.TILE_VOID)
                        System.out.print("[]");
                    else
                        System.out.print("  ");
                }
                System.out.println("");
            }
            System.out.println("");
        }
    }
}
