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
    static public UnitStrategyType strategy;
    static public towerPastrRequest request;
    static public boolean mainFightMicro = true;
    static public MapLocation ourPastr;
    static int type;
    static public boolean defenseMicro = false;
    static MapLocation defenseSpot;

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
        DarkTemplar(9),
        HQSurround(10),
        OurPastrKiller(11),
        HQPastr(12),
        HQTower(13),
        BlockadeRunner(14);

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
        //changeStrategy(UnitStrategyType.Reinforcement);

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
            int type = rc.readBroadcast(Utilities.unitNeededChannel);
            switch (type)
            {
                case Utilities.unitNeededScout:
                    changeStrategy(UnitStrategyType.Scout);
                    UnitStratScout.initialize(rc);
                    break;
                case Utilities.unitNeededDarkTemplar:
                    changeStrategy(UnitStrategyType.DarkTemplar);
                    UnitStratDarkTemplar.initialize(rc);
                    break;
                case Utilities.unitNeededHQSurround:
                    changeStrategy(UnitStrategyType.HQSurround);
                    UnitStratHqSurround.initialize(rc);
                    break;
                case Utilities.unitNeededPastrDefense:
                    changeStrategy(UnitStrategyType.PastrDefense);
                    UnitStratPastrDefense.initialize(rc);
                    break;
                case Utilities.unitNeededOurPastrKiller:
                    changeStrategy(UnitStrategyType.OurPastrKiller);
                    // here we must set our pastr which we should watch over to kill
                    if (rc.readBroadcast(60002) != 0 && rc.readBroadcast(Utilities.ourPastrKillerStart) == 0)
                    {
                        ourPastr = TowerUtil.convertIntToMapLocation(rc.readBroadcast(60002));
                        rc.broadcast(Utilities.ourPastrKillerStart, 1);
                        type = Utilities.ourPastrKillerStart;
                    }
                    else if (rc.readBroadcast(60005) != 0 && rc.readBroadcast(Utilities.ourPastrKillerStart+1) == 0)
                    {
                        ourPastr = TowerUtil.convertIntToMapLocation(rc.readBroadcast(60005));
                        rc.broadcast(Utilities.ourPastrKillerStart+1, 1);
                        type = Utilities.ourPastrKillerStart+1;
                    }
                    else
                    {
                        changeStrategy(UnitStrategyType.PastrDestroyer);
                        UnitStratPastrKiller.initialize(rc);
                    }
                    UnitStratOurPastrKillers.initialize(rc, ourPastr, type);
                    break;
                case Utilities.unitNeededHQPastr:
                    changeStrategy(UnitStrategyType.HQPastr);
                    UnitStratHQPastr.initialize(rc);
                    break;
                case Utilities.unitNeededHQTower:
                    changeStrategy(UnitStrategyType.HQTower);
                    UnitStratHQTower.initialize(rc);
                    break;
                case Utilities.unitNeededBlockadeRunner:
                    BlockadeRunner.initialize(rc);
                    changeStrategy(UnitStrategyType.BlockadeRunner);
                    break;
                default:
                    changeStrategy(UnitStrategyType.PastrDestroyer);
                    UnitStratPastrKiller.initialize(rc);
            }
        }

        rc.setIndicatorString(1, ""+strategy);

        if (type == Utilities.unitNeededPastrKiller)
        {
            UnitStratPastrKiller.initialize(rc);
        }

        while (true) {
            rc.setIndicatorString(1, ""+strategy);
            if (rc == null)
            {
                rc.setIndicatorString(2, "Rc is null");
            }
            try
            {
            	if(strategy == UnitStrategyType.NoiseTowerBuilder)
            	{
            		if(rc.getHealth() < 50)
                    {
                    	rc.setIndicatorString(0, "Help");
                    	request.sendRequest(TowerUtil.convertIntToMapLocation(get[0]), false);
                		Soldiers.changeStrategy(UnitStrategyType.Reinforcement);
                    }
            	}
            	else if(strategy == UnitStrategyType.PastrBuilder)
            	{
            		if(rc.getHealth() < 50)
                    {
                    	rc.setIndicatorString(0, "Help");
                    	request.sendRequest(TowerUtil.convertIntToMapLocation(get[0]), true);
                		Soldiers.changeStrategy(UnitStrategyType.Reinforcement);
                    }
            	}
                if (rc.isActive())
                {

                    updateStrategy();

                    cache.reset();
                    map.checkForUpdates();
                    
                    if(strategy == UnitStrategyType.PastrBuilder || strategy == UnitStrategyType.NoiseTowerBuilder)
                    {
                    	if(rc.getLocation().equals(TowerUtil.convertIntToMapLocation(get[0])))
                    	{
                    		if(strategy == UnitStrategyType.PastrBuilder)
                    		{
                    			request.madeIt(true);
                    		}
                    		else
                    		{
                    			request.madeIt(false);
                    		}
                    	}
                    }

                    // Do unit strategy picker
                    // strategy picks destinations and performs special tasks


                    //rc.setIndicatorString(0, "Not fighting");
                    if (mainFightMicro && FightMicro.fightMode(rc, null))
                    {
                        //rc.setIndicatorString(0, "Fighting");
                    }
                    else if (!mainFightMicro && defenseMicro && FightMicro.defenseMicro(rc, defenseSpot))
                    {

                    }
                    else if (rc.isActive())
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
        switch (strategy)
        {
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
            case HQSurround:
                UnitStratHqSurround.upDate();
                break;
            case HQPastr:
                UnitStratHQPastr.upDate();
                break;
            case HQTower:
                UnitStratHQTower.upDate();
                break;
            case BlockadeRunner:
                BlockadeRunner.upDate();
                break;

        }
        rc.setIndicatorString(2, "Strategy ["+strategy+"]");
    }
}
