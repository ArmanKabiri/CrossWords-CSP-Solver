/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Library;

import Problem.CrossWordsPuzzleProblem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 *
 * @author arman
 */
public class CrossWordAssignment extends AbstractAssignment implements Serializable {

    public char[][] assignmentsPuzzle;
    private CrossWordsPuzzleProblem cspProblem;

    public CrossWordAssignment(CrossWordAssignment ass) {
        super(ass);
        cspProblem = ass.cspProblem;
        assignmentsPuzzle = new char[ass.assignmentsPuzzle.length][ass.assignmentsPuzzle[0].length];
        for (int i = 0; i < ass.assignmentsPuzzle.length; i++) {
            assignmentsPuzzle[i] = Arrays.copyOf(ass.assignmentsPuzzle[i], ass.assignmentsPuzzle[i].length);
        }
    }

    public CrossWordAssignment(AbstractCSPProblem problem) {
        super(problem);
        cspProblem = (CrossWordsPuzzleProblem) problem;
        CrossWordsPuzzleProblem cProblem = (CrossWordsPuzzleProblem) problem;
        assignmentsPuzzle = new char[cProblem.height][cProblem.width];

        ///black tiles and white tiles in puzzle
        for (int i = 0; i < cProblem.height; i++) {
            for (int j = 0; j < cProblem.width; j++) {
                if (cProblem.blackTiles[i][j]) {
                    assignmentsPuzzle[i][j] = '#';
                } else {
                    assignmentsPuzzle[i][j] = '-';
                }
            }
        }
    }

    @Override
    public void assign(Variable var, Object val) {
        CrossWordVariable cVar = (CrossWordVariable) var;
        switch (cVar.direction) {
            case horizental:
                int row = cVar.rowColumnNumber;
                int startJ = cVar.startIndex;
                if (cspProblem.isRTL) { //persian
                    for (int j = startJ; j < cVar.length + startJ; j++) {
                        assignmentsPuzzle[row][j] = ((String) val).charAt(cVar.length - (j - startJ) - 1);
                    }
                } else {
                    for (int j = startJ; j < cVar.length + startJ; j++) {
                        assignmentsPuzzle[row][j] = ((String) val).charAt(j - startJ);
                    }
                }
                break;
            case vertical:
                int column = cVar.rowColumnNumber;
                int startI = cVar.startIndex;
                for (int i = startI; i < cVar.length + startI; i++) {
                    assignmentsPuzzle[i][column] = ((String) val).charAt(i - startI);
                }
                break;
        }
    }

    @Override
    public int getUnAssignedVarsNum() {
        int count = 0;
        for (Variable variable : variables) {
            if (getAssignedValue(variable) == null) {
                count++;
            }
        }
        return count;
    }

    @Override
    public Object getAssignedValue(Variable var) {
        CrossWordVariable cVar = (CrossWordVariable) var;
        String value = "";
        StringBuilder strBuilder = new StringBuilder();
        switch (cVar.direction) {
            case horizental:    //here fa modify
                int row = cVar.rowColumnNumber;
                int startJ = cVar.startIndex;
                value = String.copyValueOf(assignmentsPuzzle[row], startJ, cVar.length);
                if (cspProblem.isRTL) {
                    value = new StringBuilder(value).reverse().toString();
                }
                break;
            case vertical:
                int column = cVar.rowColumnNumber;
                int startI = cVar.startIndex;
                for (int i = startI; i < cVar.length + startI; i++) {
                    strBuilder.append(assignmentsPuzzle[i][column]);
                }
                value = strBuilder.toString();
                break;
        }
        if (value.contains("-")) {
            return null;
        } else {
            return value;
        }
    }

    private String getCurrentCreatedValue(CrossWordVariable cVar) {
        String value = "";
        StringBuilder strBuilder = new StringBuilder();
        switch (cVar.direction) {
            case horizental:
                int row = cVar.rowColumnNumber;
                int startJ = cVar.startIndex;
                value = String.copyValueOf(assignmentsPuzzle[row], startJ, cVar.length);
//                if (cspProblem.isRTL) {
//                    Collections.reverse(Arrays.asList(value.toCharArray()));
//                }
                break;
            case vertical:
                int column = cVar.rowColumnNumber;
                int startI = cVar.startIndex;
                for (int i = startI; i < cVar.length + startI; i++) {
                    strBuilder.append(assignmentsPuzzle[i][column]);
                }
                value = strBuilder.toString();
                break;
        }
        return value;
    }

    @Override
    public String toString() {
        StringBuilder strBuilder = new StringBuilder();
        for (int i = 0; i < assignmentsPuzzle.length; i++) {
            for (int j = 0; j < assignmentsPuzzle[i].length; j++) {
                strBuilder.append(assignmentsPuzzle[i][j]);
                strBuilder.append(" ");
            }
            strBuilder.append("\n");
        }
        return strBuilder.toString();
    }

    public boolean updateDomain(ArrayList<CrossWordVariable> variablesToUpdate) {
        for (int i = 0; i < variablesToUpdate.size(); i++) { //here modify fa
            for (int j = 0; j < variablesToUpdate.get(i).domain.size(); j++) {
                String testValue = (String) variablesToUpdate.get(i).domain.get(j);
                if (cspProblem.isRTL && variablesToUpdate.get(i).direction == CrossWordVariable.Direction.horizental) {
                    testValue = new StringBuilder(testValue).reverse().toString();
                }
                if (!match(testValue, getCurrentCreatedValue(variablesToUpdate.get(i)))) {
                    variablesToUpdate.get(i).domain.remove(j);
                    j--;
                }
            }
            if (variablesToUpdate.get(i).domain.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private boolean match(String s1, String s2) {
        if (s1.length() != s2.length()) {
            return false;
        } else {
            for (int i = 0; i < s1.length(); i++) {
                if (s2.charAt(i) != '-' && (s1.charAt(i) != s2.charAt(i))) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    protected Object clone() {
        CrossWordAssignment s = new CrossWordAssignment(this);
        return s;
    }

}
