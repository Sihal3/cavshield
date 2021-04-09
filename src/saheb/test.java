package saheb;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotType;

public class test {
	static RobotController rc;

    static final RobotType[] spawnableRobot = {
        RobotType.POLITICIAN,
        RobotType.SLANDERER,
        RobotType.MUCKRAKER,
    };

    static final Direction[] directions = {
    	Direction.NORTHWEST,
        Direction.NORTH,
        Direction.NORTHEAST,
        Direction.EAST,
        Direction.SOUTHEAST,
        Direction.SOUTH,
        Direction.SOUTHWEST,
        Direction.WEST,
        Direction.NORTHWEST,
        Direction.NORTH,
    };

    static int turnCount;
    static int purpose = -1;
	public static void main(String[] args) throws GameActionException {
		System.out.println((dirToward(100,50)+4)%8);
	}
	static int dirToward(int x, int y) throws GameActionException {
    	MapLocation myloc = new MapLocation(50,50);
    	if(myloc.x>x&&myloc.y>y) return(4);
    	else if(myloc.x<x&&myloc.y==y) return(7);
    	else if(myloc.x<x&&myloc.y>y) return(6);
    	else if(myloc.x>x&&myloc.y<y) return(2);
    	else if(myloc.x<x&&myloc.y<y) return(0);
    	else if(myloc.x==x&&myloc.y>y) return(5);
    	else if(myloc.x==x&&myloc.y<y) return(1);
    	else if(myloc.x>x&&myloc.y==y) return(3);
    	else return(4);
    }
}
