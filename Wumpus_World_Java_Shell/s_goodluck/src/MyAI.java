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
        boolean isSafe = false;
        boolean isKnown = false;
        boolean isVisited = false;
        int[] next = {-1, -1};

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
    private int dimension;
    private static final int[][] DIRS = {{0, 1}, {0, -1}, {-1, 0}, {1, 0}};
    private Stack<int[]> stack;
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
        dimension = Integer.MAX_VALUE;

        stack = new Stack<>();
        set = new HashSet<>();

        mapOfWorld = new Tile[7][7];
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                mapOfWorld[i][j] = new Tile();
            }
        }
        mapOfWorld[0][0].isSafe = true;
        mapOfWorld[0][0].isKnown = true;
        mapOfWorld[0][0].isVisited = true;

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
            if (this.agentDir == 0 || this.agentDir == 3) {
                this.dimension = 1+Math.max(agentX, agentY);
            }

        }
        else {
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

            if (breeze || stench) {
                //label the round poz with danger
                labelRound(poz, false);
//                this.backToBeginning();
//                return actionList.remove(0);
            } else {
                labelRound(poz, true);
            }
        }

//        // random move
//        this.exeMove(Move.values()[rand.nextInt(Move.values().length)]);

        for (int i = 0; i < DIRS.length; i++) {
            int x = agentX + DIRS[i][0];
            int y = agentY + DIRS[i][1];
            if (x >= 0 && y >= 0 && x < mapOfWorld.length && y < mapOfWorld[0].length&&x<dimension&&y<dimension) {
                if (!mapOfWorld[x][y].isVisited && mapOfWorld[x][y].isSafe &&!set.contains(String.valueOf(x)+String.valueOf(y))) {
                    stack.push(new int[]{x, y});
                    set.add(String.valueOf(x)+String.valueOf(y));
                }
            }

        }

        if (!stack.isEmpty()) {
            move(new int[]{agentX,agentY},stack.pop());
        }else {
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
    public void bfs(Tile[][] map, int[] start, int[] end) {
        int[][] dirs = {{0, 1}, {0, -1}, {-1, 0}, {1, 0}};
        Queue<int[]> queue = new LinkedList<>();
        // {up down left right}
        int[][] visited = new int[map.length][map[0].length];
        map[end[0]][end[1]].next = new int[]{-1,-1};
        queue.add(end);
        while (!queue.isEmpty()) {
            int[] poz = queue.poll();
            visited[poz[0]][poz[1]]=1;
            for (int i = 0; i < dirs.length; i++) {
                int x = poz[0] + dirs[i][0];
                int y = poz[1] + dirs[i][1];
                if (x >= 0 && y >= 0 && x < map.length && y < map[0].length&&x<dimension&&y<dimension) {
                    if (visited[x][y] == 0 && map[x][y].isSafe) {
                        if (x == start[0] && y == start[1]) {
                            map[x][y].next = poz;
                            return;
                        }
                        queue.add(new int[]{x, y});
                        map[x][y].next = poz;
                    }
                }

            }
        }
    }


    public void labelRound(Integer[] poz, boolean isSafe) {
        for (int i = 0; i < DIRS.length; i++) {
            int x = poz[0] + DIRS[i][0];
            int y = poz[1] + DIRS[i][1];

            if (x >= 0 && y >= 0 && x < mapOfWorld.length && y < mapOfWorld[0].length&&x<dimension&&y<dimension) {

                if (!mapOfWorld[x][y].isKnown) {
                    mapOfWorld[x][y].isSafe = isSafe;
                    mapOfWorld[x][y].isKnown = true;
                } else if (isSafe) {
                    mapOfWorld[x][y].isSafe = isSafe;
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
        move(new int[]{agentX,agentY},new int[]{0,0});
        this.actionList.add(Action.CLIMB);
    }

    public void move(int[] start, int[] end) {
        bfs(mapOfWorld, start, end);
        int[] poz;
        poz = mapOfWorld[start[0]][start[1]].next;
        while (poz[0] != -1 && poz[1] != -1) {
            moveStep(new int[]{agentX, agentY}, poz);
            poz = mapOfWorld[poz[0]][poz[1]].next;
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