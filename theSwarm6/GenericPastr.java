package theSwarm6;

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
				if(rc.getHealth() < 50)
				{
					request.sendRequest(rc.getLocation(), true);
				}
			}
			catch(Exception e){}
			rc.yield();
		}
	}
}
