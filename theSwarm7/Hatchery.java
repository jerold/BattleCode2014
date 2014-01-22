package theSwarm7;

import battlecode.common.*;

public class Hatchery {
	
	static RobotController rc;
	MapLocation target;
    boolean goneForPastr = false;
    int roundNum = 0;
    int fightZone;
    int roundSet = 0;
    static UnitCache cache;
    static RoadMap map;

    static Direction allDirections[] = Direction.values();
    static int directionalLooks[] = new int[]{0,1,-1,2,-2,3,-3,4};

    // these are the channels that we will use to communicate to our bots
    static final int enemyHQ = 1;
    static final int ourHQ = 2;
    static final int rallyPoint = 3;
    static final int needNoiseTower = 4;
    static final int needPastr = 5;
    static final int takeDownEnemyPastr = 6;
    static final int enemyPastrInRangeOfHQ = 7;
    static final int rallyPoint2 = 8;
    static final int defendPastr = 9;
    static final int pastLoc = 10;
	
	public Hatchery(RobotController rc)
	{
		this.rc = rc;
		
		HQFunctions.InitialLocationBroadcasts(rc);

		HQFunctions.findInitialRally(rc);

        try {
            cache = new UnitCache(rc);
            map = new RoadMap(rc, cache);
        } catch (Exception e)
        {
            e.printStackTrace();
        }

	}

	public void run()
	{
		while (true)
		{
            try
            {
                Robot[] enemies = rc.senseNearbyGameObjects(Robot.class, 35, rc.getTeam().opponent());

                if (rc.isActive())
                {
                    Movement.fire(rc, enemies, null);
                    tryToSpawn();


                }



                int broadcast = rc.readBroadcast(rallyPoint2);
                if (broadcast != 0)
                {
                    if (broadcast != fightZone)
                    {
                        fightZone = broadcast;
                        roundSet = Clock.getRoundNum();
                    }
                    // now it is time for us to move on
                    else if (roundSet + 75 < Clock.getRoundNum())
                    {
                        fightZone = 0;
                        rc.broadcast(rallyPoint2, 0);
                    }
                }


                if (Clock.getRoundNum() % 5 == 0 && Clock.getRoundNum() > 100)
                {
                    HQFunctions.setTargetLocation(rc, true);

                }

                cache.reset();
                map.checkForUpdates();

            } catch (Exception e) {}
			rc.yield();
		}
	}

    public static void tryToSpawn() throws GameActionException {
        if(rc.isActive()&&rc.senseRobotCount()<GameConstants.MAX_ROBOTS){ // if(rc.isActive()&&rc.senseRobotCount()<2){
            for(int i=0;i<8;i++){
                Direction trialDir = allDirections[i];
                if(rc.canMove(trialDir)){
                    rc.spawn(trialDir);
                    break;
                }
            }
        }
    }
}
