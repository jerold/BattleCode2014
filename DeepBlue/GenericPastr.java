package DeepBlue;

import battlecode.common.*;

public class GenericPastr
{
	RobotController rc;
	towerPastrRequest request;
	
	public GenericPastr(RobotController rc)
	{
		this.rc = rc;
		request = new towerPastrRequest(rc);
	}
	
	public void run()
	{
		while(true)
		{
			try
			{
				Robot[] bots = rc.senseNearbyGameObjects(Robot.class, 100, rc.getTeam());
				boolean tower = false;
				
				for(Robot bot : bots)
				{
					if(rc.senseRobotInfo(bot).type == RobotType.NOISETOWER)
					{
						tower = true;
					}
				}
				
				if(!tower)
				{
					request.sendRequest();
				}
			}
			catch(Exception e){}
		}
	}
}
