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
public class CrossWordVariable extends Variable{

    public enum Direction {
        horizental, vertical;
    }

    public int length;
    public Direction direction;
    public int startIndex;
    public int rowColumnNumber; //row or column base on direction
    
    @Override
    protected Object clone(){
        Variable parentV=(Variable) super.clone();
        CrossWordVariable newV=new CrossWordVariable();
        newV.name=parentV.name;
        newV.domain=parentV.domain;
        newV.length=length;
        newV.direction=direction;
        newV.startIndex=startIndex;
        newV.rowColumnNumber=rowColumnNumber;
        return newV;
    }
    
}
