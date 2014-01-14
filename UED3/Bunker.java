package UED3;

import battlecode.common.Clock;
import battlecode.common.Robot;
import battlecode.common.RobotController;


/**
 * Created by Jerold Albertson on 1/8/14.
 */
public class Bunker {
    RobotController rc;
    boolean moved;
    public static final int visBoxWidth = 5;

    public Bunker(RobotController rc)
    {
        this.rc = rc;
        moved = false;
    }

    public void run()
    {
        while(true)
        {
            try
            {
                if (true || rc.getLocation().distanceSquaredTo(rc.senseHQLocation()) > 10) {
                    int[] details = new int[5];

                    details[0] = Clock.getRoundNum();

                    Robot[] friendly = rc.senseNearbyGameObjects(Robot.class, 35, rc.getTeam());
                    Robot[] friendlyTroops = Utilities.findSoldiers(rc, friendly);
                    details[1] = friendly.length;

                    Robot[] enemy = rc.senseNearbyGameObjects(Robot.class, 35, rc.getTeam().opponent());
                    Robot[] enemyTroops = Utilities.findSoldiers(rc, enemy);
                    details[2] = enemyTroops.length;

                    double cowCount = 0.0;
                    for (int i = 0; i < visBoxWidth; i++) {
                        for (int j = 0; j < visBoxWidth; j++) {
                            cowCount += rc.senseCowsAtLocation(rc.getLocation().add(i-2, j-2));
                        }
                    }
                    details[3] = (int)cowCount;

                    details[4] = Utilities.convertMapLocationToInt(rc.getLocation());

                    Utilities.setDetailsForPastr(rc, details);
                }
            }
            catch(Exception e){}
            rc.yield();
        }
    }
}
