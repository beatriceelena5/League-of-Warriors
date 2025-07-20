package src.java.org.example.game;

import src.java.org.example.entities.characters.Characters;
import src.java.org.example.entities.exceptions.ImpossibleMoveException;
import src.java.org.example.enums.CellEntityType;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class Grid extends ArrayList<ArrayList<Cell>> {
    public int length;
    public int width;

    public Characters currentCharacter;
    public Cell currentCell;

    private Grid (int length, int width, Characters currentCharacter, Cell currentCell) {
        this.length = length;
        this.width = width;
        this.currentCharacter = currentCharacter;
        this.currentCell = currentCell;

        for (int i = 0; i < length; i++) {
            ArrayList<Cell> row = new ArrayList<>();
            for (int j = 0; j < width; j++) {
                row.add(new Cell(i, j, CellEntityType.VOID, false));
            }
            add(row);
        }
    }

    public static Grid generatePredefinedGrid(Characters player) {
        int length = 5;
        int width = 5;
        Grid grid = new Grid(length, width, player, null);

        grid.get(0).get(0).setType(CellEntityType.PLAYER);
        grid.get(0).get(3).setType(CellEntityType.SANCTUARY);
        grid.get(1).get(3).setType(CellEntityType.SANCTUARY);
        grid.get(2).get(0).setType(CellEntityType.SANCTUARY);
        grid.get(4).get(3).setType(CellEntityType.SANCTUARY);
        grid.get(3).get(4).setType(CellEntityType.ENEMY);
        grid.get(4).get(4).setType(CellEntityType.PORTAL);

        grid.currentCell = grid.get(0).get(0);

        return grid;
    }

    public static Grid generateGrid (int length, int width, Characters character) {
        if (length <= 0 || width <=0 || length > 10 || width > 10) {
            throw new IllegalArgumentException("Dimensiuni gresite");
        }

        Grid grid = new Grid(length, width, character, null);
        Random random = new Random();

        int sanctuaryNumber = 2 + random.nextInt( Math.max(1,(length*width - 8)/3) );
        int enemyNumber =  4 + random.nextInt(Math.max(1, (length*width - 8)/3 ));

        placeEntities(grid, CellEntityType.SANCTUARY, sanctuaryNumber);
        placeEntities(grid, CellEntityType.ENEMY, enemyNumber);
        placeEntities(grid, CellEntityType.PORTAL, 1);


        while (true) {
            int x = random.nextInt(length);
            int y = random.nextInt(width);
            Cell cell = grid.get(x).get(y);

            if(cell.getType() == CellEntityType.VOID) {
                cell.setType(CellEntityType.PLAYER);
                grid.currentCell = cell;
                break;
            }
        }

        return grid;
    }

    private static void placeEntities (Grid grid, CellEntityType type, int number) {
        Random random = new Random();

        for (int i = 0; i < number; i++) {
            int x = random.nextInt(grid.length);
            int y = random.nextInt(grid.width);
            Cell cell = grid.get(x).get(y);

            if(cell.getType() == CellEntityType.VOID) {
                cell.setType(type);
            }
            else {
                i--;
            }
        }

    }

    public void goNorth() throws ImpossibleMoveException {
        int x = currentCell.getX() - 1;
        int y = currentCell.getY();

        if (x < 0) {
            throw new ImpossibleMoveException ("Impossible move");
        }

        currentCell.setVisited(true);
        currentCell = getCell(x,y);
    }

    public void goSouth() throws ImpossibleMoveException {
        int x = currentCell.getX() + 1;
        int y = currentCell.getY();

        if (x >= length) {
            throw new ImpossibleMoveException ("Impossible move");
        }

        currentCell.setVisited(true);
        currentCell = getCell(x, y);
    }

    public void goWest() throws ImpossibleMoveException {
        int x = currentCell.getX();
        int y = currentCell.getY() - 1;

        if (y < 0) {
            throw new ImpossibleMoveException ("Impossible move");
        }

        currentCell.setVisited(true);
        currentCell = getCell(x, y);
    }

    public void goEast() throws ImpossibleMoveException {
        int x = currentCell.getX();
        int y = currentCell.getY() + 1;

        if (y >= width) {
            throw new ImpossibleMoveException ("Impossible move");
        }

        currentCell.setVisited(true);
        currentCell = getCell(x, y);
    }

    public Cell getCell (int x, int y) {
        return this.get(x).get(y);
    }

    public void displayDetailed() {
        for (int i = 0; i < size(); i++) {
            for (int j = 0; j < get(i).size(); j++) {
                Cell cell = get(i).get(j);

                if (cell.equals(currentCell)) {
                    System.out.print("[P] ");
                } else {
                    switch (cell.getType()) {
                        case PORTAL:
                            System.out.print("[F] ");
                            break;
                        case ENEMY:
                            System.out.print("[E] ");
                            break;
                        case SANCTUARY:
                            System.out.print("[S] ");
                            break;
                        case VOID:
                        default:
                            if (!cell.getVisited()) {
                                System.out.print("[N] ");
                            } else {
                                System.out.print("[V] ");
                            }
                            break;
                    }
                }
            }
            System.out.println();
        }
    }


    public void display() {
        for (int i = 0; i < size(); i++) {
            for (int j = 0; j < get(i).size(); j++) {
                Cell cell = get(i).get(j);

                if (cell.equals(currentCell)) {
                    System.out.print("[P] ");
                } else {
                    if (!cell.getVisited()) {
                        System.out.print("[N] ");
                    } else {
                        System.out.print("[V] ");
                    }
                }
            }
            System.out.println();
        }
    }
}
