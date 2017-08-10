/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Library;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author arman
 */
public class Variable implements Cloneable, Serializable {

    public String name;
    public ArrayList<Object> domain;

    @Override
    protected Object clone() {
        Variable newV = new Variable();
        newV.name = name;
        newV.domain = (ArrayList<Object>) domain.clone();
        return newV;
    }

    @Override
    public boolean equals(Object obj) {
        return ((Variable) obj).name.equals(name);
    }

}
