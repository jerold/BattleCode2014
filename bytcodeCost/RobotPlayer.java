package bytcodeCost;

import battlecode.common.*;

public class RobotPlayer {
	public static void run(RobotController rc)
	{
		if (rc.getType() == RobotType.HQ)
		{
			while (true)
			{
				System.out.println(fib(1000));
				rc.yield();
			}
		}
	}
	
	public static int fib(long n)
	{
		if (n <= 0)
		{
			return 0;
		}
		else if (n == 1)
		{
			return 1;
		}
		return fib(n-1) + fib(n-2);
	}
}
