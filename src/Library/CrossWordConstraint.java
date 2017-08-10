/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Library;

import Problem.CrossWordsPuzzleProblem;

/**
 *
 * @author arman
 */
public class CrossWordConstraint implements IConstraint {

    @Override
    public boolean satisfy(AbstractAssignment assignhment, AbstractCSPProblem problem) {
        CrossWordsPuzzleProblem cProblem = (CrossWordsPuzzleProblem) problem;
        SetableBoolean result = new SetableBoolean(true);

        for (int i = 0; i < assignhment.variables.size(); i++) {
            String value = (String) assignhment.getAssignedValue(assignhment.variables.get(i));
            if (value != null) {
                if (!cProblem.words.contains(value)) {
                    result.setValue(false);
                    break;
                }
            }
        }

        return result.getValue();
    }
}

class SetableBoolean {

    private boolean value;

    public SetableBoolean(boolean value) {
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }
}
