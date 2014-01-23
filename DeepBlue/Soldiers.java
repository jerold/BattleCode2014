package DeepBlue;

import DeepBlue.Strategies.UnitStratFrontLiner;
import DeepBlue.Strategies.UnitStratReinforcement;
import battlecode.common.*;

/**
 * Created by Jerold Albertson on 1/12/14.
 *
 */
public class Soldiers {
    static public RobotController rc;
    static public UnitCache cache;
    static public RoadMap map;
    static public Navigator nav;
    static public UnitStrategyType strategy;

    public static enum UnitStrategyType {
        NoType(0),
        Reinforcement(1),
        FrontLiner(2),
        PastrDefense(3),
        Constructor(4),
        Defector(5);

        private final int value;
        private UnitStrategyType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public static void run(RobotController inRc) throws GameActionException
    {
        rc = inRc;
        cache = new UnitCache(rc);
        map = new RoadMap(rc, cache);
        nav = new Navigator(rc,cache, map);
        changeStrategy(UnitStrategyType.Reinforcement);

        while (true) {
            if (!rc.isActive()) { rc.yield(); continue; }

            updateStrategy();

            cache.reset();
            map.checkForUpdates();

            // Do unit strategy picker
            // strategy picks destinations and performs special tasks

            if (nav.engaging())
                nav.adjustFire(true); // Micro Movements based on enemy contact
            else
                nav.maneuver(); // Goes forward with Macro Pathing to destination, and getting closer to friendly units

            rc.yield();
        }
    }

    public static void changeStrategy(UnitStrategyType newStrategy)
    {
        strategy = newStrategy;
    }

    public static void updateStrategy() throws GameActionException
    {
        rc.setIndicatorString(2, "Strategy ["+strategy+"]");
        switch (strategy) {
            case Reinforcement:
                UnitStratReinforcement.update();
                break;
            case FrontLiner:
                UnitStratFrontLiner.update();
                UnitStratFrontLiner.run();
                break;
            case PastrDefense:
                break;
            case Constructor:
                break;
            case Defector:
                break;
        }
    }
}
