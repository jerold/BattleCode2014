package distract;

import battlecode.common.*;

public class HQ
{
	RobotController rc;
	int count;
	boolean start;
	Direction otherHQ;
	Direction[] initialDirs, repeatDirs;
	
	public HQ(RobotController rc)
	{
		this.rc = rc;
		count = 0;
		start = true;
		otherHQ = rc.getLocation().directionTo(rc.senseEnemyHQLocation());
		Direction[] initialDirs = {otherHQ.opposite(), otherHQ.opposite().rotateLeft(), otherHQ.opposite().rotateRight()};
		Direction[] repeatDirs = {otherHQ.rotateLeft().rotateLeft(), otherHQ.rotateLeft(), otherHQ,
								  otherHQ.rotateRight(), otherHQ.rotateRight().rotateRight()};
		this.initialDirs = initialDirs;
		this.repeatDirs = repeatDirs;
	}
	
	public void run()
	{
		while(true)
		{
			try
			{
				Utilities.fire(rc);
				if(rc.isActive())
				{
					if(start)
					{
						if(count < initialDirs.length)
						{
							rc.spawn(initialDirs[count]);
							count++;
							rc.broadcast(0, count);
						}
						else
						{
							start = false;
						}
					}
					if(!start)
					{
						rc.spawn(repeatDirs[count % repeatDirs.length]);
						count++;
						rc.broadcast(0, count);
					}
				}
				
			}
			catch(Exception e){}
			rc.yield();
		}
	}
}
