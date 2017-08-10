/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Library;

import JavaFXUI.FXMLDocumentController;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.fxml.Initializable;

/**
 *
 * @author arman
 */
public class BackTrackingSearch {

    private AbstractCSPProblem cspProblem;
    private Initializable uiController;
    public long delayOnSteps = 1;
    public boolean showProgressInUI = true;

    public BackTrackingSearch(AbstractCSPProblem cspProblem, Initializable controller) {
        this.uiController = controller;
        this.cspProblem = cspProblem;
    }

    public AbstractAssignment solve() {
        AbstractAssignment initialAssignment = cspProblem.getInitialAssignments();
        AbstractAssignment goalAssignment = backtrackSolve(initialAssignment);
        return goalAssignment;
    }

    private AbstractAssignment backtrackSolve(AbstractAssignment assignment) {

        AbstractAssignment goalAssignment = null;
        Variable nextVar = cspProblem.getNextVarToAssignMRV(assignment);
        
        if (showProgressInUI) {
            try {
                Thread.sleep(delayOnSteps);
            } catch (InterruptedException ex) {
                Logger.getLogger(BackTrackingSearch.class.getName()).log(Level.SEVERE, null, ex);
            }
            Platform.runLater(() -> {
                ((FXMLDocumentController) uiController).setAssignmentProgress(assignment);
            });
        }
        
        if (nextVar == null) {
            if (cspProblem.isGoal(assignment)) {
                return assignment;
            } else {
                return null;
            }
        }
        if (!cspProblem.doesSatisfyConstraints(assignment)) {
            return null;
        }
//        else if (showProgressInUI) {
//            try {
//                Thread.sleep(delayOnSteps);
//            } catch (InterruptedException ex) {
//                Logger.getLogger(BackTrackingSearch.class.getName()).log(Level.SEVERE, null, ex);
//            }
//            Platform.runLater(() -> {
//                ((FXMLDocumentController) uiController).setAssignmentProgress(assignment);
//            });
//        }

        for (Object assValue : nextVar.domain) {
            AbstractAssignment newAssignment = (AbstractAssignment) assignment.clone();
            newAssignment.assign(nextVar, assValue);
            boolean forwardCheck = cspProblem.forwardChecking(newAssignment, nextVar);
            if (forwardCheck == true) {
                goalAssignment = backtrackSolve(newAssignment);
                if (goalAssignment != null) {
                    break;
                }
            }
        }
        return goalAssignment;
    }
}
