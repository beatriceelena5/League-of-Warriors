package src.java.org.example.game;

import src.java.org.example.enums.CellEntityType;

public class Cell {
    private int x;
    private int y;

    private CellEntityType type;
    private boolean visited;

    public Cell (int x, int y, CellEntityType type, boolean visited) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.visited = visited;
    }

    public int getX () {
        return x;
    }

    public void setX (int x) {
        this.x = x;
    }

    public int getY () {
        return y;
    }

    public void setY (int y) {
        this.y = y;
    }

    public CellEntityType getType () {
        return type;
    }

    public void setType (CellEntityType type) {
        this.type = type;
    }

    public boolean getVisited () {
        return visited;
    }

    public void setVisited (boolean visited) {
        this.visited = visited;
    }

    public String toString() {
        return "x = " + x + " y = " + y + " Type = " + type + " Visited = " + visited;
    }
}
