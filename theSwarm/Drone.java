package theSwarm;

import battlecode.common.*;

/**
 * Different types correspond to different times to turn into pastures
 * 1 means that it will turn immediately into a pasture when it reaches a destination
 * 2 means it will turn into a pasture once it is next to a noise tower
 * 3 means the bot waits for another pasture to be created before starting construction
 * Any other number, that is how long the bot will wait before beginning construction of a pasture
 * A corresponding negative number will go to the opposite corner.
 */
public class Drone {
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
    static final int morphZergling = 11;
    static final int morphHydralisk = 12;
    static final int hydraliskCount = 13;
    static final int towerLoc = 14;
    static final int towerBuilt = 15;
    static final int pastrBuilt = 16;

    RobotController rc;
    MapLocation pastrSpot;
    int type;

    public Drone(RobotController rc, int type)
    {
        this.rc = rc;
        this.type = type;

        try
        {
            int loc = rc.readBroadcast(pastLoc);
            if (loc == 0)
            {
                pastrSpot = TowerUtil.bestSpot3(rc);
            }
            else
            {
                pastrSpot = Movement.convertIntToMapLocation(loc);
            }

            int towerInt = rc.readBroadcast(towerLoc);
            MapLocation towerLocation = Movement.convertIntToMapLocation(towerInt);

            if(this.type < 0 || (towerInt != 0 && towerLocation.distanceSquaredTo(pastrSpot) > 10))
            {
                pastrSpot = TowerUtil.getOppositeSpot(rc, pastrSpot);
                this.type *= -1;
            }




            rc.broadcast(needPastr, 0);
            if (rc.readBroadcast(pastLoc) == 0)
            {
                rc.broadcast(pastLoc, Movement.convertMapLocationToInt(pastrSpot));
            }
        } catch (Exception e) { rc.setIndicatorString(0, "Error");}
        
        rc.setIndicatorString(0, "Drone");
    }

    public void run()
    {
        while (true)
        {
            try
            {
                rc.setIndicatorString(2, "Pastr: "+pastrSpot+ ", Tower: "+rc.readBroadcast(towerLoc));
                if (rc.isActive())
                {

                    MapLocation[] ourPastrs = rc.sensePastrLocations(rc.getTeam());
                    if (ourPastrs != null)
                    {
                        for (int i = ourPastrs.length; --i>=0;)
                        {
                            if (pastrSpot.distanceSquaredTo(ourPastrs[i]) <= 50)
                            {
                                Hydralisk hydralisk = new Hydralisk(rc);
                                hydralisk.run();
                            }
                        }
                    }

                    if (rc.canSenseSquare(pastrSpot))
                    {
                        Robot bot = (Robot) rc.senseObjectAtLocation(pastrSpot);
                        if (bot != null && rc.senseRobotInfo(bot).team == rc.getTeam() && rc.senseRobotInfo(bot).isConstructing)
                        {
                            Hydralisk hydralisk = new Hydralisk(rc);
                            hydralisk.run();
                        }
                    }

                    if (rc.getLocation().equals(Movement.convertIntToMapLocation(rc.readBroadcast(towerLoc))))
                    {
                        Extractor extractor = new Extractor(rc, 1);
                        extractor.run();
                    }

                    if (rc.getLocation().equals(pastrSpot))
                    {
                    	if(type == 2)
                    	{
                            int i = 0;
                    		while(!towerNear(rc)){
                                i++;
                                if (i > 250)
                                {
                                    pastrSpot = TowerUtil.getOppositeSpot(rc, pastrSpot);
                                    break;
                                }
                                rc.yield();
                            }
                    	}
                    	else if(type == 3)
                    	{
                    		for(int k = 0; k < 200 && rc.sensePastrLocations(rc.getTeam()).length == 0; k++){
                                FightMicro.defenseMicro(rc, rc.getLocation());
                            }
                    	}
                    	else
                    	{
                            boolean calledForHelp = false;
                    		while(!towerNear(rc)) {
                                Robot[] allies = rc.senseNearbyGameObjects(Robot.class, 35, rc.getTeam());
                                if (allies.length == 0 && !calledForHelp)
                                {
                                    rc.broadcast(needNoiseTower, 1);
                                }

                                if (Clock.getRoundNum() % 100 == 0)
                                {
                                    calledForHelp = true;
                                }

                                rc.yield();
                            }
                    		for(int k = 0; k < type; k++){FightMicro2.defenseMicro(rc, rc.getLocation());}
                    	}
                    	while(rc.senseNearbyGameObjects(Robot.class, 100, rc.getTeam().opponent()).length > 0){FightMicro2.defenseMicro(rc, rc.getLocation());}

                        ourPastrs = rc.sensePastrLocations(rc.getTeam());
                        if (ourPastrs != null)
                        {
                            for (int i = ourPastrs.length; --i>=0;)
                            {
                                if (pastrSpot.distanceSquaredTo(ourPastrs[i]) <= 50)
                                {
                                    Hydralisk hydralisk = new Hydralisk(rc);
                                    hydralisk.run();
                                }
                            }
                        }

                        Robot[] nearByAllies = rc.senseNearbyGameObjects(Robot.class, 35, rc.getTeam());
                        for (int i = nearByAllies.length; --i>=0;)
                        {
                            if (rc.senseRobotInfo(nearByAllies[i]).isConstructing)
                            {
                                Hydralisk hydralisk = new Hydralisk(rc);
                                hydralisk.run();
                            }
                        }

                        if (rc.getLocation().equals(pastrSpot))
                        {
                            rc.construct(RobotType.PASTR);
                        }

                    }
                    else
                    {
                        Movement.MoveMapLocation(rc, pastrSpot, false, false);
                    }
                }

            } catch (Exception e) {}
        }
    }
    
    private boolean towerNear(RobotController rc)
    {
    	Robot[] bots = rc.senseNearbyGameObjects(Robot.class, 10, rc.getTeam());
    	for(Robot bot : bots)
    	{
    		try
    		{
	    		if(rc.senseRobotInfo(bot).type == RobotType.NOISETOWER)
	    		{
	    			return true;
	    		}

    		}
    		catch(Exception e){}
    	}
    	
    	return false;
    }
}
