package theSwarm6;

import battlecode.common.Direction;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import battlecode.common.TerrainTile;

/**
 * Created by fredkneeland on 1/16/14.
 */
public class Extractor {
    RobotController rc;
    MapLocation towerSpot;

    public Extractor(RobotController rc, int type)
    {
        this.rc = rc;
        towerSpot = TowerUtil.bestSpot3(rc);
        Direction[] dirs = Direction.values();
        for(int k = 0; k < dirs.length; k++)
        {
        	if(rc.senseTerrainTile(towerSpot.add(dirs[k])) != TerrainTile.VOID)
        	{
        		towerSpot = towerSpot.add(dirs[k]);
        		break;
        	}
        }
        if(type < 0)
        {
        	towerSpot = TowerUtil.getOppositeSpot(rc, towerSpot);
        }
        
        rc.setIndicatorString(0, "Extractor");
    }

    public void run()
    {
        while (true)
        {
            try
            {
                if (rc.isActive())
                {
                    if (rc.getLocation().x == towerSpot.x && rc.getLocation().y == towerSpot.y)
                    {
                        rc.construct(RobotType.NOISETOWER);
                    }
                    else
                    {
                        Movement.MoveMapLocation(rc, towerSpot, false, false);
                    }
                }
            } catch (Exception e) {}
        }
    }
}
