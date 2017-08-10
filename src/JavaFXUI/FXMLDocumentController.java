/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package JavaFXUI;

import Library.AbstractAssignment;
import Library.AbstractCSPProblem;
import Library.BackTrackingSearch;
import Library.Coordinate;
import Library.CrossWordAssignment;
import Library.Variable;
import Problem.CrossWordsPuzzleProblem;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author arman
 */
public class FXMLDocumentController implements Initializable {

    @FXML
    private Label lbl_time;
    @FXML
    private GridPane grid_pane;
    @FXML
    private Button btn_stopSearch;
    @FXML
    private Label lbl_speed;
    @FXML
    private Button btn_startPauseResume;
    @FXML
    private Slider slider_searchSpeed;
    @FXML
    private RadioButton radio_mrv;
    @FXML
    private RadioButton radio_forwardChecking;
    @FXML
    private Button btn_loadWords;
    @FXML
    private Button btn_createTable;
    @FXML
    private TextField txt_height;
    @FXML
    private TextField txt_width;
    @FXML
    private RadioButton radio_showProgress;

    private BackTrackingSearch backTrackingSearch;
    private AbstractCSPProblem cspProblem;
    private Thread solverThread;
    private String SOLVER_THREAD_NAME = "thread_solver";
    private SolverThreadState solverThreadState;
    private ArrayList<String> words;
    private ArrayList<Coordinate> blackTiles;
    private int tableWidth, tableHeight;
    private String tileBckgroundColor = "#55ffff";
    private String blackTileBckgroundColor = "#000000";
    @FXML
    private RadioButton radio_persianWords;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        blackTiles = new ArrayList<>();
        solverThreadState = SolverThreadState.stoped;
        backTrackingSearch = new BackTrackingSearch(null, this);
        lbl_speed.setText("Speed: " + slider_searchSpeed.getValue());

        slider_searchSpeed.valueProperty().addListener((ObservableValue<? extends Number> ov, Number old_val, Number new_val) -> {
            int speed = new_val.intValue();
            backTrackingSearch.delayOnSteps = slider_searchSpeed.maxProperty().intValue() + 2 - speed;
            lbl_speed.setText("Speed: " + speed);
        });

        radio_showProgress.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            backTrackingSearch.showProgressInUI = newValue;
        });

        radio_mrv.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            if (newValue) {
                radio_forwardChecking.setSelected(true);
            }
        });
        radio_forwardChecking.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            if (!newValue) {
                radio_mrv.setSelected(false);
            }
        });

    }

    @FXML
    private void onClickBtn_StopSearch(ActionEvent event) {
        btn_startPauseResume.setText("Start Search");
        solverThread.stop();
        try {
            Thread.sleep(100);
        } catch (InterruptedException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
        solverThreadState = SolverThreadState.stoped;
        clearTilesText();
    }

    @FXML
    private void onClickBtn_startPauseResumeSearch(ActionEvent event) {
        CrossWordsPuzzleProblem problem = null;
        switch (solverThreadState) {
            case stoped:
                btn_startPauseResume.setText("Pause Search");
                clearTilesText();
                problem = new CrossWordsPuzzleProblem(tableWidth, tableHeight, words, blackTiles, radio_mrv.isSelected(), radio_forwardChecking.isSelected());
                cspProblem = problem;
                solveCspProblem(problem);
                break;
            case running:
                btn_startPauseResume.setText("Resume Search");
                solverThread.suspend();
                solverThreadState = SolverThreadState.suspend;
                break;
            case suspend:
                btn_startPauseResume.setText("Pause Search");
                solverThread.resume();
                solverThreadState = SolverThreadState.running;
                break;
        }
    }

    @FXML
    private void onClickBtn_loadWords(ActionEvent event) {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Text", "*.txt"));
            File file = fileChooser.showOpenDialog(null);
            if (file != null) {
                words = (ArrayList<String>) FileUtils.readLines(file, StandardCharsets.UTF_8);
//                if(radio_persianWords.isSelected()){
//                    ArrayList<String> reversedWords=new ArrayList<>();
//                    for (String word : words) {
//                        String rWord = new StringBuilder(word).reverse().toString();
//                        reversedWords.add(rWord);
//                    }
//                    words = reversedWords;
//                }
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText(words.size() + " Words Loaded");
                alert.show();
            }
        } catch (IOException x) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Cant read File");
            alert.show();
            x.printStackTrace();
        }
    }

    @FXML
    private void onClickBtn_createTable(ActionEvent event) {
        clearTable();
        tableWidth = Integer.parseInt(txt_width.getText());
        tableHeight = Integer.parseInt(txt_height.getText());
        setGrid_paneDimensions(tableWidth, tableHeight);
    }

    private void clearTable() {
        blackTiles=new ArrayList<>();
        grid_pane.getChildren().clear();
        grid_pane.getRowConstraints().clear();
        grid_pane.getColumnConstraints().clear();
        lbl_time.setText("Time");
    }

    private void setGrid_paneDimensions(int columnNum, int rowNum) {
        ColumnConstraints cConstraint = new ColumnConstraints();
        cConstraint.setHgrow(Priority.ALWAYS);
        for (int i = 0; i < columnNum; i++) {
            grid_pane.getColumnConstraints().add(cConstraint);
        }

        RowConstraints rConstraint = new RowConstraints();
        rConstraint.setVgrow(Priority.ALWAYS);
        for (int i = 0; i < rowNum; i++) {
            grid_pane.getRowConstraints().add(rConstraint);
        }

        grid_pane.setHgap(10);
        grid_pane.setVgap(10);

        int count = 0;
        for (int i = 0; i < rowNum; i++) {
            for (int j = 0; j < columnNum; j++) {
                Label lable = new Label("-");
                lable.setId("Tile_" + count++);
                lable.setFont(new Font("Monospaced", 40));
                lable.setAlignment(Pos.CENTER);
                lable.getProperties().put("coordinate", new Coordinate(i, j));

                lable.setOnMouseClicked((MouseEvent event) -> {
                    if (!blackTiles.contains(lable.getProperties().get("coordinate"))) {
                        lable.setStyle("-fx-background-color: " + blackTileBckgroundColor + ";");
                        blackTiles.add((Coordinate) lable.getProperties().get("coordinate"));
                    } else {
                        lable.setStyle("-fx-background-color: " + tileBckgroundColor + ";");
                        blackTiles.remove((Coordinate) lable.getProperties().get("coordinate"));
                    }
                });

                lable.setOnMouseEntered((MouseEvent event) -> {
                    if (event.isShiftDown()) {
                        if (!blackTiles.contains(lable.getProperties().get("coordinate"))) {
                            lable.setStyle("-fx-background-color: " + blackTileBckgroundColor + ";");
                            blackTiles.add((Coordinate) lable.getProperties().get("coordinate"));
                        } else {
                            lable.setStyle("-fx-background-color: " + tileBckgroundColor + ";");
                            blackTiles.remove((Coordinate) lable.getProperties().get("coordinate"));
                        }
                    }
                });

                lable.setStyle("-fx-background-color: " + tileBckgroundColor + ";");
                AnchorPane ancPane = new AnchorPane();
                AnchorPane.setTopAnchor(lable, 0d);
                AnchorPane.setBottomAnchor(lable, 0d);
                AnchorPane.setLeftAnchor(lable, 0d);
                AnchorPane.setRightAnchor(lable, 0d);
                ancPane.getChildren().add(lable);
                grid_pane.add(ancPane, j, i);
            }
        }
    }

    private void solveCspProblem(AbstractCSPProblem problem) {
        ((CrossWordsPuzzleProblem) problem).isRTL = radio_persianWords.isSelected();
        backTrackingSearch = new BackTrackingSearch(problem, this);
        backTrackingSearch.showProgressInUI = radio_showProgress.isSelected();
        int speed = (int) slider_searchSpeed.getValue();
        backTrackingSearch.delayOnSteps = slider_searchSpeed.maxProperty().intValue() + 1 - speed;
        lbl_speed.setText("Speed: " + speed);

        Thread solvingThread = new Thread(getSolverRunable(backTrackingSearch), SOLVER_THREAD_NAME);
        solverThread = solvingThread;
        solvingThread.start();
        solverThreadState = SolverThreadState.running;
    }

    private Runnable getSolverRunable(final BackTrackingSearch solver) {
        return () -> {
            long s = System.currentTimeMillis();
            AbstractAssignment goal = solver.solve();
            long e = System.currentTimeMillis();
            solverThreadState = SolverThreadState.stoped;
            Platform.runLater(() -> {
                setAssignmentProgress(goal);
                btn_startPauseResume.setText("Start Search");
                lbl_time.setText((e - s) + " Miils");
            });

            if (goal == null) {
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("No Answer");
                    alert.show();
                });
            }
        };
    }

    public void setAssignmentProgress(AbstractAssignment assignment) {
        if (assignment == null) {
            assignment = new CrossWordAssignment(cspProblem);
        }
        CrossWordAssignment cAssignment = (CrossWordAssignment) assignment;
        for (int i = 0; i < cAssignment.assignmentsPuzzle.length; i++) {
            for (int j = 0; j < cAssignment.assignmentsPuzzle[0].length; j++) {
                String s = "#Tile_" + (i * cAssignment.assignmentsPuzzle[0].length + j);
                Scene ss = grid_pane.getScene();
                Label lbl = (Label) ss.lookup(s);
                lbl.setText(cAssignment.assignmentsPuzzle[i][j] + "");
            }
        }
    }

    public void clearTilesText() {
        for (int i = 0; i < tableHeight; i++) {
            for (int j = 0; j < tableWidth; j++) {
                Label lable = (Label) grid_pane.getScene().lookup("#Tile_" + (i * tableWidth + j));
                lable.setText("-");
            }
        }
        lbl_time.setText("Time");
    }

}

enum SolverThreadState {
    suspend, stoped, running;
}
