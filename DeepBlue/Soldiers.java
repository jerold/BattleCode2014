package DeepBlue;

import DeepBlue.Strategies.*;
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
    static public Engine engine;
    static public UnitStrategyType strategy;
    static public towerPastrRequest request;
    static public boolean mainFightMicro = true;

    public static enum UnitStrategyType {
        NoType(0),
        Reinforcement(1),
        FrontLiner(2),
        PastrDefense(3),
        PastrBuilder(4),
        NoiseTowerBuilder(5),
        Defector(6),
        PastrDestroyer(7),
        Scout(8),
        DarkTemplar(9);

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
        //changeStrategy(UnitStrategyType.Reinforcement);

<<<<<<< HEAD
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
        else
        {
            UnitStratPastrKiller.initialize(rc);
            //changeStrategy(UnitStrategyType.PastrDestroyer);
            changeStrategy(UnitStrategyType.Scout);
            //changeStrategy(UnitStrategyType.DarkTemplar);
        }

        // here we initialize certain types of bots
        switch (strategy)
        {
            case PastrDefense:
                UnitStratPastrDefense.initialize(rc);
                break;
            case Scout:
                UnitStratScout.initialize(rc);
                break;
            case DarkTemplar:
                mainFightMicro = false;
                UnitStratDarkTemplar.initialize(rc);
                break;
        }
=======
//        request = new towerPastrRequest(rc);
//        int[] get = request.checkForNeed();
//        if(get[0] != -1)
//        {
//            if(get[2] == 0)
//            {
//                noiseTowerBuilder.initialize(rc, get);
//                changeStrategy(UnitStrategyType.NoiseTowerBuilder);
//            }
//            else
//            {
//                pastrBuilder.initialize(rc, get);
//                changeStrategy(UnitStrategyType.PastrBuilder);
//            }
//        }
//        else
//        {
//            UnitStratPastrKiller.initialize(rc);
//            changeStrategy(UnitStrategyType.PastrDestroyer);
//        }
//
//        switch (strategy)
//        {
//            case PastrDefense:
//                UnitStratPastrDefense.initialize(rc);
//                break;
//        }
>>>>>>> 09048093e88764a9b9899d8f96a24a49a9932fd7

        while (true) {
            try
            {
                if (rc.isActive())
                {

                    updateStrategy();

                    cache.reset();
                    map.checkForUpdates();

                    // Do unit strategy picker
                    // strategy picks destinations and performs special tasks


                    rc.setIndicatorString(0, "Not fighting");
                    if (mainFightMicro && FightMicro.fightMode(rc, null))
                    {
                        rc.setIndicatorString(0, "Fighting");
                    }
                    else
                        nav.maneuver(); // Goes forward with Macro Pathing to destination, and getting closer to friendly units
                }
            } catch (Exception e) {}

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
                UnitStratPastrDefense.update();
                break;
            case PastrBuilder:
                pastrBuilder.run();
                break;
            case NoiseTowerBuilder:
                noiseTowerBuilder.run();
                break;
            case Defector:
                break;
            case PastrDestroyer:
                UnitStratPastrKiller.upDate();
                break;
            case Scout:
                UnitStratScout.upDate();
                break;
            case DarkTemplar:
                UnitStratDarkTemplar.upDate();
                break;
        }
        rc.setIndicatorString(2, "Strategy ["+strategy+"]");
    }
}
