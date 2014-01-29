package DeepBlue;

import DeepBlue.Strategies.UnitStratFrontLiner;
import DeepBlue.Strategies.UnitStratReinforcement;
import DeepBlue.Strategies.noiseTowerBuilder;
import DeepBlue.Strategies.pastrBuilder;
import battlecode.common.*;
import theSwarm.*;

/**
 * Created by Jerold Albertson on 1/12/14.
 *
 */
public class Soldiers {
    static public RobotController rc;
    static public UnitCache cache;
    static public RoadMap map;
    static public Navigator nav;
    static public Engine engine;
    static public UnitStrategyType strategy;
    static public towerPastrRequest request;

    public static enum UnitStrategyType {
        NoType(0),
        Reinforcement(1),
        FrontLiner(2),
        PastrDefense(3),
        PastrBuilder(4),
        NoiseTowerBuilder(5),
        Defector(6);

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
        engine = new Engine(rc, cache, map, nav);
        changeStrategy(UnitStrategyType.Reinforcement);

        request = new towerPastrRequest(rc);
        int[] get = request.checkForNeed();
        if(get[0] != -1)
        {
            if(get[2] == 0)
            {
                noiseTowerBuilder.initialize(rc, get);
                changeStrategy(UnitStrategyType.NoiseTowerBuilder);
            }
            else
            {
                pastrBuilder.initialize(rc, get);
                changeStrategy(UnitStrategyType.PastrBuilder);
            }
        }

        while (true) {
            if (!rc.isActive()) { rc.yield(); continue; }



            updateStrategy();

            cache.reset();
            map.checkForUpdates();

            // Do unit strategy picker
            // strategy picks destinations and performs special tasks


            if (FightMicro.fightMode(rc, null));
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
            case PastrBuilder:
                pastrBuilder.run();
                break;
            case NoiseTowerBuilder:
                noiseTowerBuilder.run();
                break;
            case Defector:
                break;
        }
        rc.setIndicatorString(2, "Strategy ["+strategy+"]");
    }
}
