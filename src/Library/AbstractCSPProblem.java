/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Library;

import java.util.ArrayList;

/**
 *
 * @author arman
 */
public abstract class AbstractCSPProblem {

    public boolean useMRV;
    public boolean useForwardChecking;

    public ArrayList<Variable> variables = new ArrayList<>();
    public ArrayList<IConstraint> constraints = new ArrayList<>();

    public Variable getNextVarToAssignMRV(AbstractAssignment assignment) {
        Variable targetVar = null;
        if (useMRV) {
            int min = Integer.MAX_VALUE;
            for (Variable nextVar : assignment.variables) {
                if (assignment.getAssignedValue(nextVar) == null) {//not assign yet
                    if (nextVar.domain.size() < min) {
                        targetVar = nextVar;
                        min = nextVar.domain.size();
                    }
                }
            }

        } else {
            for (Variable nextVar : assignment.variables) {
                if (assignment.getAssignedValue(nextVar) == null) {//not assign yet
                    targetVar = nextVar;
                    break;
                }
            }

        }
        return targetVar;   //null means all vars are assigned
    }

    public boolean isGoal(AbstractAssignment assignment) {
        return (doesSatisfyConstraints(assignment) && assignment.getUnAssignedVarsNum() == 0);
    }

    public abstract void initialConstrains();

    public abstract void initialVariables();

    public abstract AbstractAssignment getInitialAssignments();

    public abstract boolean ArcConsistency(AbstractAssignment assignment);

    public abstract boolean forwardChecking(AbstractAssignment assignment, Variable lastAssignedVariable);

    public abstract boolean doesSatisfyConstraints(AbstractAssignment assignment);

}
