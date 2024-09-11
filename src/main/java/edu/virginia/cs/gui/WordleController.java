package edu.virginia.cs.gui;

import edu.virginia.cs.wordle.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WordleController {
    //    @FXML
//    private GridPane wordleGrid;
    int numRows = 6;
    int numCols = 5;

    int count = 6;
    //Try making a new pane in the controller and setting that background to a color and then adding the text as a child so it appears on top of the colored pane. 
//
//Editthanks!
    @FXML
    private GridPane wordleGrid = new GridPane();
    @FXML
    private Label check = new Label();
    @FXML
    private Label error = new Label();

    WordleImplementation wordle = new WordleImplementation();
    LetterResult[] letterResults;
    String guess = "";

    @FXML
    public void initialize() {
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < 5; col++) {
                TextField textField = new TextField();
                textField.setPrefWidth(80);
                wordleGrid.add(textField, col, row);
                //textField.setStyle("-fx-background-color: Gray; ");
                textField.setStyle("-fx-background-color: Gray; -fx-text-fill: white;");

                textField.setOnKeyPressed(event -> {
                    int currentRow = GridPane.getRowIndex(textField);
                    int currentCol = GridPane.getColumnIndex(textField);
                    if (event.getCode() == KeyCode.BACK_SPACE && textField.getText().isEmpty() && currentCol > 0 && currentCol != 4) {
                        //move back to previoud cell in the same row
                        movePrevCell(currentRow, currentCol);
                        //clear the textfield
                        TextField thisText = (TextField) wordleGrid.getChildren().get(currentRow * numCols + currentCol);
                        thisText.clear();
                    }
                    if (event.getCode() == KeyCode.BACK_SPACE && textField.getText().isEmpty() &&  currentCol == 4) {
                        //move back to previoud cell in the same row
                        movePrevCell(currentRow, currentCol);
                        //clear the textfield
                        TextField thisText = (TextField) wordleGrid.getChildren().get(currentRow * numCols + currentCol);
                        thisText.clear();
                    }
                });

                textField.setOnKeyTyped(event -> {
                    String text = event.getCharacter();
                    NotALetter(textField, text);

                    if (event.getCode() == KeyCode.BACK_SPACE || event.getCode() == KeyCode.DELETE) {
                        int currentRow = getCurrentRow(textField);
                        int currentCol = getCurrentCol(textField);

                        if (!textField.getText().isEmpty()) {
                            textField.clear();
                            movePreviousCell(currentRow, currentCol);

                        }


                    }

                    if (!text.isEmpty() && Character.isLetter(text.charAt(0))) {
                        // Get the row and column indices of the current TextField
                        // Get the row and column indices of the current TextField
                        int currentRow = getCurrentRow(textField);
                        int currentCol = getCurrentCol(textField);

                        if (currentRow == numRows - 1 && currentCol == numCols - 1) {
                            // Last cell, check if it already has a value
                            if (!textField.getText().isEmpty()) {
                                event.consume(); // Consume event to prevent further input
                                // System.out.println("Event consumed");
                                textField.setEditable(false);
                                //check.setText("event");

                                concatenateRow(currentRow, currentCol);

                                // call is valid function
                                // if its not valid make this text field valid or maybe i can do that in the
                                // method where i check if valid
                            }
                        }
                        if (currentCol < numCols - 1) {
                            // Move to the next cell in the same row
                            moveNextCell(currentRow, currentCol);
                        } else if (currentRow < numRows - 1) {
                            // Move to the first cell in the next row
                            moveNextRowFirstCell(currentRow);
                            concatenateRow(currentRow, currentCol); // Call the concatenateRow function with the current row
                        }
                    }
                });
            }


        }
    }

    public void updateGridBasedOnEnums(GridPane wordleGrid, int row, int numCols, LetterResult[] result) {
        List<TextField> rowChildren = new ArrayList<>();

        for (int col = 0; col < numCols; col++) {
            rowChildren.add((TextField) wordleGrid.getChildren().get((row * numCols) + col));
        }
        System.out.println(guess);
        System.out.println(wordle.getAnswer());

        //letterResults = wordle.submitGuess(guess);
        for (int i = 0; i < letterResults.length; i++) {
            if (letterResults[i] == LetterResult.GRAY) {
                // rowChildren.get(i).setStyle("-fx-background-color: #DDDDDD");

            }
            if (letterResults[i] == LetterResult.GREEN) {
                rowChildren.get(i).setStyle("-fx-background-color: #019a01; -fx-text-fill: white;");
                //textField.setStyle("-fx-background-color: Gray; -fx-text-fill: white;");

            }
            if (letterResults[i] == LetterResult.YELLOW) {
                rowChildren.get(i).setStyle("-fx-background-color: #DBA800; ; -fx-text-fill: white;");

            }
        }
    }

    private void movePrevCell(int currentRow, int currentCol) {
        TextField prevColTextField = (TextField) wordleGrid.getChildren().get(currentRow * numCols + currentCol - 1);
        prevColTextField.requestFocus();

    }

    private static int getCurrentCol(TextField textField) {
        int currentCol = GridPane.getColumnIndex(textField);
        return currentCol;
    }

    private static int getCurrentRow(TextField textField) {
        int currentRow = GridPane.getRowIndex(textField);
        return currentRow;
    }

    //   the TextField will not display the input and will not refocus in instances where is isn't a letter
    private static void NotALetter(TextField textField, String text) {
        if (!text.isEmpty() && !Character.isLetter(text.charAt(0))) {
            textField.clear();
        }
    }

    private void moveNextRowFirstCell(int currentRow) {

        TextField nextRowTextField = (TextField) wordleGrid.getChildren().get((currentRow + 1) * numCols); // https://stackoverflow.com/questions/39671692/getting-text-from-textarea-in-a-gridpane-in-javafx/39706805#39706805
        nextRowTextField.requestFocus();


    }

    private void moveNextCell(int currentRow, int currentCol) {
        TextField nextColTextField = (TextField) wordleGrid.getChildren().get(currentRow * numCols + currentCol + 1);
        nextColTextField.requestFocus();

        int lastRow = wordleGrid.getRowCount() - 1;
        int lastCol = wordleGrid.getColumnCount() - 1;

//        Node last = wordleGrid.getChildren().get(lastRow * (lastCol + 1) + lastCol);
//        if (nextColTextField == last)
//        {
//            ((TextField) last).setEditable(true);
//        }

    }

    private void movePreviousCell(int currentRow, int currentCol) {
        if (currentCol == 0) {
            return; // Do nothing if already at the first cell of the row
        }
        TextField prevColTextField = (TextField) wordleGrid.getChildren().get(currentRow * numCols + currentCol - 1);
        prevColTextField.requestFocus();
    }

    private void concatenateRow(int row, int curCol) {
        guess = "";

        StringBuilder rowText = new StringBuilder();
        for (int col = 0; col < numCols; col++) {
            TextField textField = (TextField) wordleGrid.getChildren().get(row * numCols + col);
            rowText.append(textField.getText());
        }
        String guess = rowText.toString();

        submit(guess, row, curCol);

    }

    private void submit(String guess, int row, int curCol) {
        //  System.out.println(Arrays.toString(wordle.submitGuess(guess)));

        System.out.println(guess);
       // System.out.println(wordle.getRemainingGuesses());


        try {
            letterResults = wordle.submitGuess(guess);
            error.setText(" ");
            error.setStyle(" -fx-text-fill: black;");
        } catch (IllegalWordException e) {
                error.setText("INVALID INPUT: TRY AGAIN");
                error.setStyle(" -fx-text-fill: red;");
            for (int i = 0; i < numCols; i++) {
                //System.out.println("check: " + row * numCols + i);
                TextField textField = (TextField) wordleGrid.getChildren().get(row * numCols + i);
                textField.clear();
            }
 /// make it clear the entire row and then focus on the first cell in that row
            int firstCellIndex = row * numCols;
            TextField firstTextField = (TextField) wordleGrid.getChildren().get(firstCellIndex);
            firstTextField.requestFocus();
            if (row == 5 && curCol == 4)
            {
                TextField test = (TextField) wordleGrid.getChildren().get( row * numCols + 4);
                test.setEditable(true);
                System.out.println("got here");
            }

           throw new IllegalArgumentException("Invalid argument provided.");
        } catch (GameAlreadyOverException e) {
            throw new GameAlreadyOverException(" Game is over");
        }
        System.out.println(wordle.getRemainingGuesses());
        if (wordle.isWin()) {
            game0ver(wordleGrid);
            System.out.println("you WON nice");
        }
        if (wordle.isLoss()) {
            game0ver(wordleGrid);
            System.out.println("you ran out of lives");
        }

        updateGridBasedOnEnums(wordleGrid, row, numCols, letterResults);

    }

    public void game0ver(GridPane wordleGrid) {


        for (Node node : wordleGrid.getChildren()) {
            if (node instanceof TextField) {
                //((TextField) node).clear(); //MOVE THIS TO THE RESTART METHOD AND MAKE SET EDITABLE (TRUE)
                ((TextField) node).setEditable(false); // optional: make text fields uneditable after game over
            }
        }
        restartGame();

    }

    protected void restartGame() {
//
        Stage gameOverStage = new Stage();
        gameOverStage.setTitle("WORDLE");
        gameOverStage.setMinWidth(300);
        gameOverStage.setMinHeight(200);
        Label gameOverLabel = new Label("Game Is Over \n Play again?");
        Button yesButton = new Button("Yes");
        Button noButton = new Button("No");
//
        noButton.setOnAction( event -> {
            Platform.exit();
        });
        yesButton.setOnAction( event -> {
            //resetGUI();

            Wordle wordle = new WordleImplementation();
            System.out.println( "Restarting app" );
            gameOverStage.close();
            Stage stage = (Stage) yesButton.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("wordle-view.fxml"));
            Parent root = null;
            try {
                root = loader.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            stage.setScene(new Scene(root));
            root.setStyle("-fx-background-color: black;");
            stage.show();
        });
        VBox vbox = new VBox(gameOverLabel, yesButton, noButton);

        vbox.setSpacing(20);
        vbox.setAlignment(Pos.CENTER);

        Scene gameOverScene = new Scene(vbox);
        gameOverStage.setScene(gameOverScene);
        gameOverStage.show();
//
    }

    }

