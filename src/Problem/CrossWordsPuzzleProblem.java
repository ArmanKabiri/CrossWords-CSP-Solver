/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Problem;

import Library.AbstractAssignment;
import Library.AbstractCSPProblem;
import Library.Coordinate;
import Library.CrossWordAssignment;
import Library.CrossWordConstraint;
import Library.CrossWordVariable;
import Library.IConstraint;
import Library.Variable;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author arman
 */
public final class CrossWordsPuzzleProblem extends AbstractCSPProblem {

    public ArrayList<String> words = new ArrayList<>();
    public int width, height;
    public boolean[][] blackTiles;
    public boolean[][] variablesAdjacency;
    public boolean isRTL = false;

    public CrossWordsPuzzleProblem(String fileName, boolean useMRV, boolean useForwardChecking) {
        this.useForwardChecking = useForwardChecking;
        this.useMRV = useMRV;
        initializeFromFile(fileName);
        initialConstrains();
        initialVariablesAdjacency();
    }

    public CrossWordsPuzzleProblem(int width, int height, ArrayList<String> words, ArrayList<Coordinate> blackTiles, boolean useMRV, boolean useForwardChecking) {
        this.useForwardChecking = useForwardChecking;
        this.useMRV = useMRV;
        this.words = words;
        this.width = width;
        this.height = height;
        this.blackTiles = new boolean[height][width];
        blackTiles.stream().forEach((blackTile) -> {
            this.blackTiles[blackTile.row][blackTile.column] = true;
        });
        initialVariables(this.blackTiles);
        initialConstrains();
        initialVariablesAdjacency();
    }

    public final void initializeFromFile(String fileName) {
        try {
            List<String> lines = Files.readAllLines(Paths.get("src/Resources/" + fileName), Charset.defaultCharset());
            int lastExploredIndex = 0;

            ///////// Words:
            while (true) {
                int sepratorIndex = lines.get(1).indexOf(',', lastExploredIndex);
                if (sepratorIndex == (-1)) {
                    break;
                }
                String word = lines.get(1).substring(lastExploredIndex, sepratorIndex);
                lastExploredIndex = sepratorIndex + 1;
                words.add(word);
            }
            words.add(new String(lines.get(1).substring(lastExploredIndex)));

            //////width-height:
            int sepratorIndex = lines.get(3).indexOf(',');
            height = Integer.parseInt(lines.get(3).substring(1, sepratorIndex));
            width = Integer.parseInt(lines.get(3).substring(sepratorIndex + 1, lines.get(3).length() - 1));

            ///// initial Table
            blackTiles = new boolean[height][width];//true means tile is black

            lastExploredIndex = 0;
            while (true) {
                int tileRow, tileColumn;
                int openBracket = lines.get(5).indexOf('[', lastExploredIndex);
                if (openBracket == (-1)) {
                    break;
                }
                int commaIndex = lines.get(5).indexOf(',', openBracket);
                int closeBracket = lines.get(5).indexOf(']', commaIndex);
                tileRow = Integer.parseInt(lines.get(5).substring(openBracket + 1, commaIndex));
                tileColumn = Integer.parseInt(lines.get(5).substring(commaIndex + 1, closeBracket));
                lastExploredIndex = closeBracket + 2;
                blackTiles[tileRow][tileColumn] = true;
            }
            //initial Variables
            initialVariables(blackTiles);

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void initialVariables(boolean[][] blackTiles) {
        ///// Horizental Vars
        for (int i = 0; i < height; i++) {
            int lastBlackTileColumn = -1;
            int numVarInRow = 0;
            for (int j = 0; j < width; j++) {
                if (blackTiles[i][j] == true) {
                    int varLength = j - lastBlackTileColumn - 1;
                    if (varLength > 1) {
                        CrossWordVariable var = new CrossWordVariable();
                        var.length = varLength;
                        var.name = "H_" + i + "_" + numVarInRow;
                        var.direction = CrossWordVariable.Direction.horizental;
                        var.rowColumnNumber = i;
                        var.startIndex = lastBlackTileColumn + 1;
                        var.domain = (ArrayList<Object>) (Object) filterWordsByLength(varLength);
                        variables.add(var);
                        numVarInRow++;
                    }
                    lastBlackTileColumn = j;
                }
            }
            int varLength = width - lastBlackTileColumn - 1;
            if (varLength > 1) {
                CrossWordVariable var = new CrossWordVariable();
                var.length = varLength;
                var.name = "H_" + i + "_" + numVarInRow;
                var.direction = CrossWordVariable.Direction.horizental;
                var.rowColumnNumber = i;
                var.startIndex = lastBlackTileColumn + 1;
                var.domain = (ArrayList<Object>) (Object) filterWordsByLength(varLength);
                variables.add(var);
                numVarInRow++;
            }
        }

        /////Vertical Vars:
        for (int j = 0; j < width; j++) {
            int lastBlackTileRow = -1;
            int numVarInColumn = 0;
            for (int i = 0; i < height; i++) {
                if (blackTiles[i][j] == true) {
                    int varLength = i - lastBlackTileRow - 1;
                    if (varLength > 1) {
                        CrossWordVariable var = new CrossWordVariable();
                        var.length = varLength;
                        var.name = "V_" + j + "_" + numVarInColumn;
                        var.direction = CrossWordVariable.Direction.vertical;
                        var.rowColumnNumber = j;
                        var.startIndex = lastBlackTileRow + 1;
                        var.domain = (ArrayList<Object>) (Object) filterWordsByLength(varLength);
                        variables.add(var);
                        numVarInColumn++;
                    }
                    lastBlackTileRow = i;
                }
            }
            int varLength = height - lastBlackTileRow - 1;
            if (varLength > 1) {
                CrossWordVariable var = new CrossWordVariable();
                var.length = varLength;
                var.name = "V_" + j + "_" + numVarInColumn;
                var.direction = CrossWordVariable.Direction.vertical;
                var.rowColumnNumber = j;
                var.startIndex = lastBlackTileRow + 1;
                var.domain = (ArrayList<Object>) (Object) filterWordsByLength(varLength);
                variables.add(var);
                numVarInColumn++;
            }
        }
    }

    public ArrayList<String> filterWordsByLength(int len) {
        ArrayList<String> result = new ArrayList<>();
        words.stream().filter((word) -> (word.length() == len)).forEach((word) -> {
            result.add(word);
        });
        return result;
    }

    @Override
    public void initialVariables() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void initialConstrains() {
        CrossWordConstraint constraint = new CrossWordConstraint();
        constraints.add(constraint);
    }

    @Override
    public AbstractAssignment getInitialAssignments() {
        return new CrossWordAssignment(this);
    }

    @Override
    public boolean ArcConsistency(AbstractAssignment assignment) {
        return true;
    }

    @Override
    public boolean doesSatisfyConstraints(AbstractAssignment assignment) {
        boolean result = true;
        for (IConstraint constraint : constraints) {
            if (!constraint.satisfy(assignment, this)) {
                result = false;
                break;
            }
        }
        return result;
    }

    @Override
    public boolean forwardChecking(AbstractAssignment assignment, Variable lastAssignedVariable) {
        if (useForwardChecking) {
            CrossWordVariable cVar = (CrossWordVariable) lastAssignedVariable;
            ArrayList<CrossWordVariable> AdjacentVars = getAdjacentVars(cVar, assignment.variables);
            boolean isContinueable = ((CrossWordAssignment) assignment).updateDomain(AdjacentVars);
            return isContinueable;
        }
        else{
            return true;
        }
    }

    public void initialVariablesAdjacency() {
        variablesAdjacency = new boolean[variables.size()][variables.size()];
        for (int i = 0; i < variables.size(); i++) {
            if (((CrossWordVariable) variables.get(i)).direction == CrossWordVariable.Direction.horizental) {
                for (int j = 0; j < variables.size(); j++) {
                    if (((CrossWordVariable) variables.get(j)).direction == CrossWordVariable.Direction.vertical) {
                        if (isAdjacent_Slow(variables.get(i), variables.get(j))) {
                            variablesAdjacency[i][j] = true;
                            variablesAdjacency[j][i] = true;
                        }
                    }
                }
            }
        }
    }

    private boolean isAdjacent_Slow(Variable varH, Variable varV) {
        CrossWordVariable cVarH = (CrossWordVariable) varH;
        CrossWordVariable cVarV = (CrossWordVariable) varV;
        ArrayList<Integer> varHTilesNumbers = new ArrayList<>();
        ArrayList<Integer> varVTilesNumbers = new ArrayList<>();
        for (int j = cVarH.startIndex; j < cVarH.length + cVarH.startIndex; j++) {
            varHTilesNumbers.add(cVarH.rowColumnNumber * width + j);
        }
        for (int i = cVarV.startIndex; i < cVarV.length + cVarV.startIndex; i++) {
            varVTilesNumbers.add(cVarV.rowColumnNumber + width * i);
        }
        if (!Collections.disjoint(varHTilesNumbers, varVTilesNumbers)) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isAdjacent_Fast(Variable var1, Variable var2) {
        CrossWordVariable cVar1 = (CrossWordVariable) var1;
        CrossWordVariable cVar2 = (CrossWordVariable) var2;
        if (cVar1.direction.equals(cVar2.direction)) {
            return false;
        } else {
            return (variablesAdjacency[variables.indexOf(var1)][variables.indexOf(var2)]);
        }
    }

    private ArrayList<CrossWordVariable> getAdjacentVars(CrossWordVariable var, ArrayList<Variable> totalVars) {
        ArrayList<CrossWordVariable> result = new ArrayList<>();
        for (int i = 0; i < totalVars.size(); i++) {
            if (isAdjacent_Fast(var, totalVars.get(i))) {
                result.add((CrossWordVariable) totalVars.get(i));
            }
        }
        return result;
    }

}
