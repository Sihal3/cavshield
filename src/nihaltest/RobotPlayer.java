package nihaltest;
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

    static int turnCount;
    static int purpose = -1;
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
        if(turnCount<100) {
	        spawn(1, 35);
        }
        else {
        	if(turnCount%20==0) {
        		spawn(2, 20);
        	} else if(turnCount%10==1){
        		spawn(1, 73);
        	} else {
        	}
        }
     
        if(influence > 50) {
        	int willing_influence = (int)(0.25*influence*(1-0.8*(rc.getTeamVotes()/rc.getRoundNum())));
        	if (rc.canBid(willing_influence)) {
        		rc.bid(willing_influence);
        		System.out.println("Bid " + willing_influence);
        	}
        }
    }

    static void runPolitician() throws GameActionException {
        Team enemy = rc.getTeam().opponent();
        int actionRadius = rc.getType().actionRadiusSquared;
        RobotInfo[] attackable = rc.senseNearbyRobots(actionRadius, enemy);
        if (attackable.length != 0 && rc.canEmpower(actionRadius)) {
            System.out.println("empowering...");
            rc.empower(actionRadius);
            System.out.println("empowered");
            return;
        }
        if (tryMove(randomDirection()))
            System.out.println("I moved!");
    }

    static void runSlanderer() throws GameActionException {
    	if(purpose == -1) {
    		purpose = randomDirection();
    	}
    	Team enemy = rc.getTeam().opponent();
        RobotInfo[] robots = rc.senseNearbyRobots(rc.getLocation(), -1, enemy);
        if(robots.length != 0) {
        	tryMove((dirToward(robots[0].location.x, robots[0].location.y)+4)%8);
        } else {
        	tryMove(purpose);
        }
    }

    static void runMuckraker() throws GameActionException {
    	if(purpose == -1) {
    		purpose = randomDirection();
    	}
        Team enemy = rc.getTeam().opponent();
        int actionRadius = rc.getType().actionRadiusSquared;
        for (RobotInfo robot : rc.senseNearbyRobots(actionRadius, enemy)) {
            if (robot.type.canBeExposed()) {
                // It's a sooolanderer... go get them!
                if (rc.canExpose(robot.location)) {
                    System.out.println("e x p o s e d");
                    rc.expose(robot.location);
                    return;
                }
            }
        }
        RobotInfo[] robots = rc.senseNearbyRobots(rc.getLocation(), -1, enemy);
        if(robots.length != 0) {
        	tryMove(dirToward(robots[0].location.x, robots[0].location.y));
        } else {
        	tryMove(purpose);
        }
    }
    static void spawn(int type, int influence) throws GameActionException {
		for (Direction dir : directions) {
            if (rc.canBuildRobot(spawnableRobot[type], dir, influence)) {
                rc.buildRobot(spawnableRobot[type], dir, influence);
                break;
            }
        }
    	
    }
    /**
     * Returns a random Direction.
     *
     * @return a random Direction
     */
    static int randomDirection() {
        return (int) (Math.random() * 8);
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
    static boolean tryMove(int dir) throws GameActionException {
        if (rc.canMove(directions[dir])) {
            rc.move(directions[dir]);
            return true;
        } else {
        	int one = (int) Math.round(Math.random());
        	if(one == 1) {
	        	if (rc.canMove(directions[dir+1])) {
	                rc.move(directions[dir+1]);
	                return true;
	            } else if (rc.canMove(directions[dir-1])) {
	                rc.move(directions[dir-1]);
	                return true;
	            } else {
	            	tryMove(randomDirection());
	            	return true;
	            }
        	}
        	if(one == 0) {
	        	if (rc.canMove(directions[dir+1])) {
	                rc.move(directions[dir+1]);
	                return true;
	            } else if (rc.canMove(directions[dir-1])) {
	                rc.move(directions[dir-1]);
	                return true;
	            } else {
	            	tryMove(randomDirection());
	            	return true;
	            }
        	}
        }
        return false;
    }
    static int dirToward(int x, int y) throws GameActionException {
    	MapLocation myloc = rc.getLocation();
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
