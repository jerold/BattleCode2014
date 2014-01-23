package DeepBlue;

import battlecode.common.*;

/**
 * Created by Jerold Albertson on 1/22/14.
 */
public abstract class UnitStrategy {
    abstract UnitStrategy update() throws GameActionException;
    abstract void run() throws GameActionException;
}