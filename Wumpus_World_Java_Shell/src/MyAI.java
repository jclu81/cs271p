// ======================================================================
// FILE:        MyAI.java
//
// AUTHOR:      Abdullah Younis
//
// DESCRIPTION: This file contains your agent class, which you will
//              implement. You are responsible for implementing the
//              'getAction' function and any helper methods you feel you
//              need.
//
// NOTES:       - If you are having trouble understanding how the shell
//                works, look at the other parts of the code, as well as
//                the documentation.
//
//              - You are only allowed to make changes to this portion of
//                the code. Any changes to other portions of the code will
//                be lost when the tournament runs your code.
// ======================================================================

import java.util.*;

public class MyAI extends Agent {
    private class Tile {
        boolean wumpus = true;
        boolean pit = true;
        boolean isKnown = false;
        boolean isVisited = false;
        int[] front = {-1, -1};

    }

    public Tile[][] mapOfWorld;

    public enum Move {
        RIGHT,
        DOWN,
        LEFT,
        UP,

    }

    private List<Action> actionList;
    private List<Move> historyList;
    private int agentX;
    private int agentY;
    private int agentDir;
    private int dimensionX;
    private int dimensionY;
    private static final int[][] DIRS = {{1, 0}, {0, -1}, {-1, 0}, {0, 1}};
    private Stack<int[]> stack;
    private List<int[]> list;
    private Set<String> set;


//    private Random rand = new Random();

    public MyAI() {
        // ======================================================================
        // YOUR CODE BEGINS
        // ======================================================================
        actionList = new ArrayList<Action>();
        historyList = new ArrayList<Move>();

        agentX = 0;
        agentY = 0;
        agentDir = 0;
        dimensionX = Integer.MAX_VALUE;
        dimensionY = Integer.MAX_VALUE;

        stack = new Stack<>();
        list = new ArrayList<>();
        set = new HashSet<>();

        mapOfWorld = new Tile[7][7];
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                mapOfWorld[i][j] = new Tile();
            }
        }

        mapOfWorld[0][0].isKnown = true;
        mapOfWorld[0][0].isVisited = true;
        mapOfWorld[0][0].wumpus = false;
        mapOfWorld[0][0].pit = false;
//        knownMap = new Integer[7][7];
//        knownMap[0][0]=1;
//        kbForWWP = new KbForWWP();
//        kbForWWP.add("not(B[0,0])","not(S[0,0])","not(W[0,0])","not()");
        // ======================================================================
        // YOUR CODE ENDS
        // ======================================================================
    }

    public Action getAction
            (
                    boolean stench,
                    boolean breeze,
                    boolean glitter,
                    boolean bump,
                    boolean scream
            ) {
        // ======================================================================
        // YOUR CODE BEGINS
        // ======================================================================


        // execute rest action of move
        if (!actionList.isEmpty()) {
            return actionList.remove(0);
        }

        // if bump
        if (bump) {
            Move lastMove = this.historyList.remove(this.historyList.size() - 1);
            switch (lastMove) {
                case RIGHT:
                    this.agentX--;
                    break;
                case DOWN:
                    this.agentY++;
                    break;
                case LEFT:
                    this.agentX++;
                    break;
                case UP:
                    this.agentY--;
                    break;
            }
            if (this.agentDir == 0) {
                this.dimensionX = 1 + agentX;
            }
            if (this.agentDir == 3) {
                this.dimensionY = 1 + agentY;
            }

        } else {
            // check if node been visited
            Integer[] poz = new Integer[2];
            poz[0] = agentX;
            poz[1] = agentY;
            if (!mapOfWorld[poz[0]][poz[1]].isVisited) {
                mapOfWorld[poz[0]][poz[1]].isVisited = true;
            }

            if (glitter) {
                actionList.add(Action.GRAB);
                this.backToBeginning();
                return actionList.remove(0);
            }

            if (stench) {
                labelWumpus(poz, true);
            } else {
                labelWumpus(poz, false);
            }

            if (breeze) {
                labelPit(poz, true);
            } else {
                labelPit(poz, false);
            }

            for (int i = 0; i < DIRS.length; i++) {
                int x = agentX + DIRS[i][0];
                int y = agentY + DIRS[i][1];
                if (x >= 0 && y >= 0 && x < mapOfWorld.length && y < mapOfWorld[0].length && x < dimensionX && y < dimensionY) {
                    if (!mapOfWorld[x][y].isVisited && !mapOfWorld[x][y].wumpus && !mapOfWorld[x][y].pit && !set.contains(String.valueOf(x) + String.valueOf(y))) {
                        list.add(new int[]{x, y});
                        set.add(String.valueOf(x) + String.valueOf(y));
                    }
                }

            }
        }

//        // random move
//        this.exeMove(Move.values()[rand.nextInt(Move.values().length)]);




        if (!list.isEmpty()) {
            int tmpMin = Integer.MAX_VALUE;
            Stack<int[]> nextMoveStack = new Stack<>();
            int removeId = -1;
            for (int i = 0; i < list.size(); i++) {
                Stack<int[]> tmpStack = ucs(mapOfWorld, new int[]{agentX, agentY}, list.get(i), agentDir);
                int cost = tmpStack.pop()[0];
                if (cost < tmpMin) {
                    tmpMin = cost;
                    nextMoveStack = tmpStack;
                    removeId = i;
                }
            }
            if (removeId != -1) list.remove(removeId);
            nextMoveStack.pop();
            while (!nextMoveStack.isEmpty()) {
                moveStep(new int[]{agentX, agentY}, nextMoveStack.pop());
            }
        } else {
            backToBeginning();
        }


        return actionList.remove(0);

//        // Print Command Menu
//        System.out.println("Press 'w' to Move Forward  'a' to 'Turn Left' 'd' to 'Turn Right'");
//        System.out.println("Press 's' to Shoot         'g' to 'Grab'      'c' to 'Climb'");
//
//        Scanner in = new Scanner(System.in);
//        // Get Input
//        System.out.print("Please input: ");
//        String userInput = in.next();
//
//        // Return Action Associated with Input
//        if (userInput.charAt(0) == 'w') {
//            this.moveUp();
//            return actionList.remove(0);
//        }
//        if (userInput.charAt(0) == 'a') {
//            this.moveLeft();
//            return actionList.remove(0);
//        }
//        if (userInput.charAt(0) == 'd') {
//            this.moveRight();
//            return actionList.remove(0);
//        }
//        if (userInput.charAt(0) == 's') {
//            this.moveDown();
//            return actionList.remove(0);
//        }
//        if (userInput.charAt(0) == 'b') {
//            this.backToBeginning();
//            return actionList.remove(0);
//        }
//        return Action.CLIMB;

        // ======================================================================
        // YOUR CODE ENDS
        // ======================================================================
    }


    // ======================================================================
    // YOUR CODE BEGINS
    // ======================================================================
    // 计算最短路线
//    public void bfs(Tile[][] map, int[] start, int[] end) {
//        Queue<int[]> queue = new LinkedList<>();
//        // {up down left right}
//        int[][] visited = new int[map.length][map[0].length];
//        map[end[0]][end[1]].next = new int[]{-1, -1};
//        queue.add(end);
//        while (!queue.isEmpty()) {
//            int[] poz = queue.poll();
//            visited[poz[0]][poz[1]] = 1;
//            for (int i = 0; i < DIRS.length; i++) {
//                int x = poz[0] + DIRS[i][0];
//                int y = poz[1] + DIRS[i][1];
//                if (x >= 0 && y >= 0 && x < map.length && y < map[0].length && x < dimension && y < dimension) {
//                    if (visited[x][y] == 0 && map[x][y].isSafe) {
//                        if (x == start[0] && y == start[1]) {
//                            map[x][y].next = poz;
//                            return;
//                        }
//                        queue.add(new int[]{x, y});
//                        map[x][y].next = poz;
//                    }
//                }
//
//            }
//        }
//    }
    public void backToBeginning() {
//        List<Move> tmp = new ArrayList<>(this.historyList);
//        while ((this.agentX != 0 || this.agentY != 0) && !tmp.isEmpty()) {
//            Move move = tmp.remove(tmp.size() - 1);
//            move = Move.values()[(move.ordinal() + 2) % 4];
//            this.exeMove(move);
//        }
//        this.actionList.add(Action.CLIMB);
//        bfs(mapOfWorld, new Integer[]{0, 0}, new Integer[]{agentX, agentY});
//        int i = agentX;
//        int j = agentY;
//        Integer[] poz;
//        poz = mapOfWorld[i][j].front;
//        while (poz[0] != -1 && poz[1] != -1) {
//            moveStep(new Integer[]{agentX, agentY}, poz);
//            poz = mapOfWorld[poz[0]][poz[1]].front;
//        }
        move(new int[]{agentX, agentY}, new int[]{0, 0}, agentDir);
        this.actionList.add(Action.CLIMB);
    }


    public Stack<int[]> ucs(Tile[][] map, int[] start, int[] end, int agentDir) {

        PriorityQueue<int[]> queue = new PriorityQueue<>(costComparator);
        // {up down left right}
        int[][] visited = new int[map.length][map[0].length];
        map[start[0]][start[1]].front = new int[]{-1, -1};
        int[] tmpStart = new int[4];
        tmpStart[0] = start[0];
        tmpStart[1] = start[1];
        tmpStart[2] = 0;
        tmpStart[3] = agentDir;


        queue.add(tmpStart);
        while (!queue.isEmpty()) {
            int[] poz = queue.poll();
            if (poz[0] == end[0] && poz[1] == end[1]) {
                Stack<int[]> stack = new Stack<>();
                int[] tmpPoz = poz;
                int cost = poz[2];
                while (tmpPoz[0] != -1 && tmpPoz[1] != -1) {
                    stack.push(tmpPoz);
                    tmpPoz = mapOfWorld[tmpPoz[0]][tmpPoz[1]].front;
                }
                stack.push(new int[]{cost});
                return stack;
            }
            visited[poz[0]][poz[1]] = 1;
            for (int i = 0; i < DIRS.length; i++) {
                int x = poz[0] + DIRS[i][0];
                int y = poz[1] + DIRS[i][1];
                if (x >= 0 && y >= 0 && x < map.length && y < map[0].length && x < dimensionX && y < dimensionY) {
                    if (visited[x][y] == 0 && !map[x][y].wumpus && !map[x][y].pit) {
                        queue.add(new int[]{x, y, poz[2] + calStepCost(i, poz[3]), i});
                        map[x][y].front = poz;
                    }
                }

            }
        }
        return null;
    }


    public int calStepCost(int dir, int agentDir) {
        // {{0, 1}up,3 {0, -1}down,1 {-1, 0}left,2 {1, 0}right,0};
        switch (Math.abs(dir - agentDir)) {
            case 0:
                return 1;
            case 1:
                return 2;
            case 2:
                return 3;
            case 3:
                return 2;
        }
        return 0;
    }

    public static Comparator<int[]> costComparator = new Comparator<int[]>() {
        @Override
        public int compare(int[] c1, int[] c2) {
            if (c1[2] > c2[2]) return 1;
            else if (c1[2] < c2[2]) return -1;
            return 0;
        }
    };

    public void labelPit(Integer[] poz, boolean pit) {
        for (int i = 0; i < DIRS.length; i++) {
            int x = poz[0] + DIRS[i][0];
            int y = poz[1] + DIRS[i][1];

            if (x >= 0 && y >= 0 && x < mapOfWorld.length && y < mapOfWorld[0].length && x < dimensionX && y < dimensionY) {

                if (!mapOfWorld[x][y].isKnown) {
                    mapOfWorld[x][y].pit = pit;
                    mapOfWorld[x][y].isKnown = true;
                } else if (!pit) {
                    mapOfWorld[x][y].pit = pit;
                }

            }
        }
    }

    public void labelWumpus(Integer[] poz, boolean wumpus) {
        for (int i = 0; i < DIRS.length; i++) {
            int x = poz[0] + DIRS[i][0];
            int y = poz[1] + DIRS[i][1];

            if (x >= 0 && y >= 0 && x < mapOfWorld.length && y < mapOfWorld[0].length && x < dimensionX && y < dimensionY) {
                if (!wumpus) {
                    mapOfWorld[x][y].wumpus = wumpus;
                }else {
                    if (!mapOfWorld[x][y].isKnown) {
                        mapOfWorld[x][y].wumpus = wumpus;
                        mapOfWorld[x][y].isKnown = true;
                    } else {
                        
                    }
                }

            }
        }
    }


    public void exeMove(Move move) {
        switch (move) {
            case RIGHT:
                this.moveRight();
                break;
            case DOWN:
                this.moveDown();
                break;
            case LEFT:
                this.moveLeft();
                break;
            case UP:
                this.moveUp();
                break;
        }
    }


    public void move(int[] start, int[] end, int agentDir) {

        Stack<int[]> stack = ucs(mapOfWorld, start, end, agentDir);
        stack.pop();
        while (!stack.isEmpty()) {
            moveStep(new int[]{agentX, agentY}, stack.pop());
        }
    }

    public void moveStep(int[] start, int[] end) {

        if (start[0] == end[0]) {
            if (start[1] + 1 == end[1]) {
                moveUp();
            }

            if (start[1] - 1 == end[1]) {
                moveDown();
            }
        }

        if (start[1] == end[1]) {
            if (start[0] + 1 == end[0]) {
                moveRight();
            }

            if (start[0] - 1 == end[0]) {
                moveLeft();
            }
        }

        return;

    }


    public void moveUp() {
        switch (this.agentDir) {
            case 0:
                this.actionList.add(Action.TURN_LEFT);
                this.actionList.add(Action.FORWARD);
                break;
            case 1:
                this.actionList.add(Action.TURN_LEFT);
                this.actionList.add(Action.TURN_LEFT);
                this.actionList.add(Action.FORWARD);
                break;
            case 2:
                this.actionList.add(Action.TURN_RIGHT);
                this.actionList.add(Action.FORWARD);
                break;
            case 3:
                this.actionList.add(Action.FORWARD);
                break;
            default:
                break;
        }
        this.historyList.add(Move.UP);
        this.agentY++;
        this.agentDir = 3;
    }

    public void moveDown() {
        switch (this.agentDir) {
            case 0:
                this.actionList.add(Action.TURN_RIGHT);
                this.actionList.add(Action.FORWARD);
                break;
            case 1:
                this.actionList.add(Action.FORWARD);
                break;
            case 2:
                this.actionList.add(Action.TURN_LEFT);
                this.actionList.add(Action.FORWARD);
                break;
            case 3:
                this.actionList.add(Action.TURN_LEFT);
                this.actionList.add(Action.TURN_LEFT);
                this.actionList.add(Action.FORWARD);
                break;
            default:
                break;
        }
        this.historyList.add(Move.DOWN);
        this.agentY--;
        this.agentDir = 1;
    }

    public void moveRight() {
        switch (this.agentDir) {
            case 0:
                this.actionList.add(Action.FORWARD);
                break;
            case 1:
                this.actionList.add(Action.TURN_LEFT);
                this.actionList.add(Action.FORWARD);
                break;
            case 2:
                this.actionList.add(Action.TURN_LEFT);
                this.actionList.add(Action.TURN_LEFT);
                this.actionList.add(Action.FORWARD);
                break;
            case 3:
                this.actionList.add(Action.TURN_RIGHT);
                this.actionList.add(Action.FORWARD);
                break;
            default:
                break;
        }
        this.historyList.add(MyAI.Move.RIGHT);
        this.agentX++;
        this.agentDir = 0;
    }

    public void moveLeft() {
        switch (this.agentDir) {
            case 0:
                this.actionList.add(Action.TURN_LEFT);
                this.actionList.add(Action.TURN_LEFT);
                this.actionList.add(Action.FORWARD);
                break;
            case 1:
                this.actionList.add(Action.TURN_RIGHT);
                this.actionList.add(Action.FORWARD);
                break;
            case 2:
                this.actionList.add(Action.FORWARD);
                break;
            case 3:
                this.actionList.add(Action.TURN_LEFT);
                this.actionList.add(Action.FORWARD);
                break;
            default:
                break;
        }
        this.historyList.add(MyAI.Move.LEFT);
        this.agentX--;
        this.agentDir = 2;
    }


    // ======================================================================
    // YOUR CODE ENDS
    // ======================================================================
}