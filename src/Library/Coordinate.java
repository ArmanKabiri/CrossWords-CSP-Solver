/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Library;

/**
 *
 * @author arman
 */
public class Coordinate {

    public int row, column;

    public Coordinate(int row, int column) {
        this.row = row;
        this.column = column;
    }

    @Override
    public boolean equals(Object obj) {
        Coordinate c = (Coordinate) obj;
        return (row == c.row && column == c.column);
    }

}
