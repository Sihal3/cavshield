package cavShieldv1;
import battlecode.common.*;

public strictfp class RobotPlayer {
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
    static final int[] slanderer = {
    	41, 63, 85, 107, 130, 154, 178, 203, 228, 256, 282,
    	310, 339, 368, 399, 431, 463, 497, 532, 568, 605, 643,
    	683, 724, 766, 810, 855, 902, 949
    };

    static int turnCount;
    static int purpose = -1;
    static int ecid = -1;
    static MapLocation ecloc;
    /**
     * run() is the method that is called when a robot is instantiated in the Battlecode world.
     * If this method returns, the robot dies!
     **/
    @SuppressWarnings("unused")
    public static void run(RobotController rc) throws GameActionException {

        // This is the RobotController object. You use it to perform actions from this robot,
        // and to get information on its current status.
        RobotPlayer.rc = rc;

        turnCount = 0;

        System.out.println("I'm a " + rc.getType() + " and I just got created!");
        while (true) {
            turnCount += 1;
            // Try/catch blocks stop unhandled exceptions, which cause your robot to freeze
            try {
                // Here, we've separated the controls into a different method for each RobotType.
                // You may rewrite this into your own control structure if you wish.
                System.out.println("I'm a " + rc.getType() + "! Location " + rc.getLocation());
                switch (rc.getType()) {
                    case ENLIGHTENMENT_CENTER: runEnlightenmentCenter(); break;
                    case POLITICIAN:           runPolitician();          break;
                    case SLANDERER:            runSlanderer();           break;
                    case MUCKRAKER:            runMuckraker();           break;
                }

                // Clock.yield() makes the robot wait until the next turn, then it will perform this loop again
                Clock.yield();

            } catch (Exception e) {
                System.out.println(rc.getType() + " Exception");
                e.printStackTrace();
            }
        }
    }

    static void runEnlightenmentCenter() throws GameActionException {
        int influence = rc.getInfluence();
        if(rc.isReady()) {
	        RobotInfo[] robots = rc.senseNearbyRobots(-1, rc.getTeam().opponent());
	        if (robots.length != 0) {
	        	spawn(0, (int)(influence*0.25));
	        }
	
	        if(turnCount%6==0) {
	        	int r = (int)(Math.random() * 5);
	        	if(r==0) {
	        		spawn(0, (int)(influence*0.25));
	        	} else {
	        		int scount = 0;
	        		for (RobotInfo robot : rc.senseNearbyRobots(-1, rc.getTeam())) {
	                    if (robot.type.canBeExposed()) {
	                        scount++;
	                    }
	                }
	        		if(scount < 25) {
	        			int sinf = 0;
	        			for(int i : slanderer) {
	        				if (influence > i) {
	        					sinf = i;
	        				}
	        			}
	        			if(sinf==0) {
	        				spawn(0, 30);
	        			} else {
	        				spawn(1, sinf);
	        			}
	        		} else {
	        			spawn(0, (int)(influence*0.25));
	        		}
	        	}
	        } else if(turnCount%6==3) {
	        	spawn(2, 1);
	        }
        }
        if(rc.getTeamVotes() < 751) {
	        if(rc.getTeamVotes()/rc.getRoundNum()<0.4) {
	        	if (rc.canBid((int)(0.1*influence))) {
	        		rc.bid((int)(0.1*influence));
	        		System.out.println("Bid " + (int)(0.1*influence));
	        	}
	        } else {
	        	if (rc.canBid((int)(0.05*influence))) {
	        		rc.bid((int)(0.05*influence));
	        		System.out.println("Bid " + (int)(0.05*influence));
	        	}
	        }
        }
    }

    static void runPolitician() throws GameActionException {
    	if(!rc.isReady()) {
    		return;
    	}
    	if (ecid == -1) {
            for (RobotInfo robot : rc.senseNearbyRobots(-1, rc.getTeam())) {
                if (robot.type == RobotType.ENLIGHTENMENT_CENTER) {
                    ecid = robot.ID;
                    ecloc = robot.location;
                }
            }
        }
    	MapLocation loc = rc.getLocation();
        Team enemy = rc.getTeam().opponent();
        int actionRadius = rc.getType().actionRadiusSquared;
        RobotInfo[] robots = rc.senseNearbyRobots();
        for(RobotInfo robot : robots) {
        	if(robot.type == RobotType.ENLIGHTENMENT_CENTER && !robot.getTeam().equals(rc.getTeam())) {
        		if(robot.location.isWithinDistanceSquared(loc, actionRadius) && rc.canEmpower(actionRadius)) {
        			rc.empower(actionRadius);
        			return;
        		} else {
        			tryMove(loc.directionTo(robot.location));
                	return;
        		}
        	}
        }
        robots = rc.senseNearbyRobots(loc, -1, enemy);
        for(RobotInfo robot : robots) {
        	if(robot.type == spawnableRobot[2]) {
        		if(robot.location.isWithinDistanceSquared(loc, actionRadius) && rc.canEmpower(actionRadius)) {
        			rc.empower(actionRadius);
        			return;
        		} else {
        			tryMove(loc.directionTo(robot.location));
                	return;
        		}
        	}
        }
        for(RobotInfo robot : robots) {
        	if(robot.type == spawnableRobot[0]) {
        		if(robot.location.isWithinDistanceSquared(loc, actionRadius) && rc.canEmpower(actionRadius)) {
        			rc.empower(actionRadius);
        			return;
        		} 
        	}
        }
        if(rc.getLocation().isWithinDistanceSquared(ecloc, 40)) {
        	tryMove(loc.directionTo(ecloc).opposite());
        	return;
        }
        robots = rc.senseNearbyRobots(loc, -1, rc.getTeam());
        for(RobotInfo robot : robots) {
        	if(robot.type == spawnableRobot[0]) {
        		tryMove(loc.directionTo(robot.location).opposite());
            	return;
        	}
        }
        if(turnCount%2==0) {
        	tryMove(loc.directionTo(ecloc).rotateRight().rotateRight());
        	return;
        }
        tryMove(loc.directionTo(ecloc).rotateLeft().rotateLeft());
        return;
    }

    static void runSlanderer() throws GameActionException {
    	if(!rc.isReady()) {
    		return;
    	}
    	if (ecid == -1) {
            for (RobotInfo robot : rc.senseNearbyRobots(-1, rc.getTeam())) {
                if (robot.type == RobotType.ENLIGHTENMENT_CENTER) {
                    ecid = robot.ID;
                    ecloc = robot.location;
                }
            }
        }
    	MapLocation loc = rc.getLocation();
    	Team enemy = rc.getTeam().opponent();
        RobotInfo[] robots = rc.senseNearbyRobots(loc, -1, enemy);
        for(RobotInfo robot : robots) {
        	if(robot.type == spawnableRobot[2]) {
        		tryMove(loc.directionTo(robot.location).opposite());
            	return;
        	}
        }
        tryMove(randomDirection());
        return;
        
    }

    static void runMuckraker() throws GameActionException {
    	if(!rc.isReady()) {
    		return;
    	}
    	if (ecid == -1) {
            for (RobotInfo robot : rc.senseNearbyRobots(-1, rc.getTeam())) {
                if (robot.type == RobotType.ENLIGHTENMENT_CENTER) {
                    ecid = robot.ID;
                    ecloc = robot.location;
                }
            }
        }
    	MapLocation loc = rc.getLocation();
        Team enemy = rc.getTeam().opponent();
        RobotInfo[] robots = rc.senseNearbyRobots(rc.getLocation(), -1, enemy);
        for(RobotInfo robot : robots) {
        	if(robot.type.canBeExposed()) {
        		if (rc.canExpose(robot.location)) {
                    System.out.println("e x p o s e d");
                    rc.expose(robot.location);
                    return;
                } else {
                	tryMove(rc.getLocation().directionTo(robot.location));
                	return;
                }
        	}
        }
        if(rc.getLocation().isWithinDistanceSquared(ecloc, 40)) {
        	tryMove(loc.directionTo(ecloc).opposite());
        	return;
        }
        	
        robots = rc.senseNearbyRobots(loc, -1, rc.getTeam());
        for(RobotInfo robot : robots) {
        	if(robot.type == spawnableRobot[2]) {
        		tryMove(loc.directionTo(robot.location).opposite());
            	return;
        	}
        }
        tryMove(randomDirection());
        return;
    }
    static void spawn(int type, int influence) throws GameActionException {
    	if(type == 0 && influence < 11) {
    		return;
    	}
		for (Direction dir : directions) {
            if (rc.canBuildRobot(spawnableRobot[type], dir, influence)) {
                rc.buildRobot(spawnableRobot[type], dir, influence);
                return;
            }
        }
    	
    }
    /**
     * Returns a random Direction.
     *
     * @return a random Direction
     * @throws GameActionException 
     */
    static Direction randomDirection() throws GameActionException {
        return directions[(int) (Math.random() * 8)];
    }

    /**
     * Returns a random spawnable RobotType
     *
     * @return a random RobotType
     */
    static RobotType randomSpawnableRobotType() {
        return spawnableRobot[(int) (Math.random() * spawnableRobot.length)];
    }

    /**
     * Attempts to move in a given direction.
     *
     * @param dir The intended direction of movement
     * @return true if a move was performed
     * @throws GameActionException
     */
    static boolean tryMove(Direction dir) throws GameActionException {
        if (hmove(dir)) {
            return true;
        } else {
        	int one = (int) (Math.random()*2);
        	if(one == 1) {
        		if (hmove(dir.rotateRight()))  return true;
	        	else if (hmove(dir.rotateLeft())) return true;
	            else if (hmove(randomDirection())) return true;
        	}
        	if(one == 0) {
        		if (hmove(dir.rotateLeft()))  return true;
	        	else if (hmove(dir.rotateRight())) return true;
	            else if (hmove(randomDirection())) return true;
        	}
        }
        return false;
    }
    static boolean hmove(Direction dir) throws GameActionException {
        if (rc.canMove(dir)) {
            rc.move(dir);
            return true;
        }
        return false;
    }
    /*static boolean edgeavoid(int x, int y) throws GameActionException {
    	MapLocation loc = rc.getLocation();
    	if(!rc.canSenseLocation(loc.translate(x, y))) {
        	tryMove(loc.directionTo(loc.translate(x, y)).opposite());
        	return true;
        }
    	return false;
    }*/
}
