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
public abstract class AbstractAssignment implements Cloneable {

    public int totalVariablesNum;
    public ArrayList<Variable> variables;

    public AbstractAssignment(AbstractAssignment ass) {
        variables=new ArrayList<>();
        totalVariablesNum = ass.totalVariablesNum;
//        variables = (ArrayList<Variable>) ass.variables.clone();
        for(int i=0;i<ass.variables.size();i++){
            variables.add((Variable) ass.variables.get(i).clone());
        }
    }

    public AbstractAssignment(AbstractCSPProblem problem) {
        this.totalVariablesNum = problem.variables.size();
        variables = (ArrayList<Variable>) problem.variables.clone();
    }

    public abstract void assign(Variable var, Object val);

    public abstract int getUnAssignedVarsNum();

    public abstract Object getAssignedValue(Variable var);

    @Override
    protected abstract Object clone();
    
}
