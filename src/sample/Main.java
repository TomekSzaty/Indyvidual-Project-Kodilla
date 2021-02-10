package sample;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class Main extends Application {

    private String playerName;
    private Instant start;
    private Instant end;
    private int gameTime;
    private static Scene scene1, scene2, scene3;
    private GridPane gridPane = new GridPane();
    private BorderPane borderPane = new BorderPane();
    private Label label = new Label("KÓŁKO  -- KRZYŻYK");
    private Button restartButton = new Button("Restart Game");
    private Font font = Font.font("Tahoma", FontWeight.BOLD, 30);
    private Button[] buttons = new Button[9];
    private int countDraw;
    private boolean gameOver = false;
    private int activePlayer = 1;
    private int[] gameState = {3, 3, 3, 3, 3, 3, 3, 3, 3};
    private int[][] winningPosition = {
            {0, 1, 2},
            {3, 4, 5},
            {6, 7, 8},
            {0, 3, 6},
            {1, 4, 7},
            {2, 5, 8},
            {0, 4, 8},
            {2, 4, 6}
    };

    @Override
    public void start(Stage stage) throws Exception {
        Label welcomeLabel = new Label("Witaj w grze  \"Kółko-Krzyżyk\"!");
        welcomeLabel.setFont(Font.font("Tahoma", FontWeight.BOLD, 32));
        Text writeName = new Text("Wpisz swoje imie");
        writeName.setFont(Font.font("Tahoma", FontWeight.BOLD, 30));
        TextField field = new TextField();
        field.setFont(Font.font(18));
        field.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                playerName = field.getText();
            }
        });
        Button button1 = new Button("START GRY");
        button1.setFont(Font.font("Tahoma", FontWeight.NORMAL, 25));
        button1.setAlignment(Pos.BOTTOM_CENTER);
        button1.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                playerName = field.getText();
                field.getText();
                start = Instant.now();
                stage.setScene(scene2);
            }
        });
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(55);
        grid.setVgap(60);
        grid.setPadding(new Insets(30));
        grid.add(welcomeLabel, 0, 0, 2, 1);
        grid.add(writeName, 0, 1);
        grid.add(field, 0, 2, 1, 1);
        grid.add(button1, 0, 3, 2, 2);
        scene1 = new Scene(grid, 600, 600);
        this.createGUI();
        this.handleEvent();
        scene2 = new Scene(borderPane, 600, 600);
        scene2.getStylesheets().add
                (Main.class.getResource("sample.css").toExternalForm());
        stage.setScene(scene1);
        scene1.getStylesheets().add
                (Main.class.getResource("sample.css").toExternalForm());
        stage.show();
    }

    private void createGUI() {
        label.setFont(font);
        restartButton.setFont(font);
        borderPane.setTop(label);
        borderPane.setBottom(restartButton);
        BorderPane.setAlignment(label, Pos.CENTER);
        BorderPane.setAlignment(restartButton, Pos.CENTER);
        borderPane.setPadding(new Insets(20));
        int btNr = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Button button = new Button();
                button.setId(btNr + "");
                button.setFont(font);
                button.setPrefWidth(150);
                button.setPrefHeight(150);
                gridPane.add(button, j, i);
                gridPane.setAlignment(Pos.CENTER);
                buttons[btNr] = button;
                btNr++;
            }
        }
        borderPane.setCenter(gridPane);
    }

    private void handleEvent() {
        restartButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                for (int i = 0; i < 9; i++) {
                    gameState[i] = 3;
                    buttons[i].setGraphic(null);
                    buttons[i].setBackground(null);
                    buttons[i].setBorder(new Border(new BorderStroke(Color.DARKBLUE, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1))));
                    gameOver = false;
                    countDraw = 0;
                    start = Instant.now();
                }
            }
        });
        for (Button btn : buttons) {
            btn.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    Button currentBtn = (Button) actionEvent.getSource();
                    String idS = currentBtn.getId();
                    int idI = Integer.parseInt(idS);

                    if (!gameOver) {
                        if (gameState[idI] == 3) {
                            if (!playerTurn(idI)){
                                cpuHardTurn();
                            }
                        } else {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Niedozwolony ruch!");
                            alert.setContentText("Zmien pole");
                            alert.show();
                        }
                    }
                }
            });
        }
    }

    private boolean playerTurn(int fieldIndex) {
        boolean gameEnded;
        buttons[fieldIndex].setGraphic(new ImageView(new Image("file:src/sample/Krzyzyk1.png")));
        gameState[fieldIndex] = 1;
        gameEnded = checkForWinner(true);
        if (!gameEnded) {
            gameEnded = checkForDraw();
        }
        return gameEnded;
    }

    private void cpuEasyTurn() {
        for (int i = 0; i < gameState.length; i++) {
            if (gameState[i] == 3) {
                int cpuIndex = i;
                buttons[cpuIndex].setGraphic(new ImageView(new Image("file:src/sample/Kolko1.png")));
                gameState[cpuIndex] = 0;
                checkForWinner(true);
                checkForDraw();
                break;
            }
        }
    }

    private void cpuHardTurn() {
        List<Integer> availableFields = new ArrayList<>();

        for (int i = 0; i < gameState.length; i++) {
            if (gameState[i] == 3) {
                availableFields.add(i);
            }
        }
        Random random = new Random();
        int index = random.nextInt(availableFields.size());
        int cpuIndex = availableFields.get(index);
        buttons[cpuIndex].setGraphic(new ImageView(new Image("file:src/sample/Kolko1.png")));
        gameState[cpuIndex] = 0;
        checkForWinner(false);
        checkForDraw();
    }

    private boolean checkForWinner(boolean isPlayer) {
        if (!gameOver) {
            for (int[] wp : winningPosition) {
                if (gameState[wp[0]] == gameState[wp[1]] && gameState[wp[1]] == gameState[wp[2]] && gameState[wp[1]] != 3) {
                    end = Instant.now();
                    gameTime = (int) Duration.between(start, end).getSeconds();
                    AlertBox.display("Game over", "Nick:  " + playerName +" - " + " Czas gry " + ": " + gameTime + " sec");
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Gra zakończona!");
                    alert.setHeaderText(isPlayer ? "Wygrana!" : "Przegrana!");
                    buttons[wp[0]].setBackground(new Background(new BackgroundFill(Color.GREEN, CornerRadii.EMPTY, Insets.EMPTY)));
                    buttons[wp[1]].setBackground(new Background(new BackgroundFill(Color.GREEN, CornerRadii.EMPTY, Insets.EMPTY)));
                    buttons[wp[2]].setBackground(new Background(new BackgroundFill(Color.GREEN, CornerRadii.EMPTY, Insets.EMPTY)));
                    alert.show();
                    gameOver = true;
                    return true;
                }
            }
        }
        countDraw++;
        return false;
    }

    private boolean checkForDraw() {
        if (countDraw == 9 && !gameOver) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Remis!");
            alert.setHeaderText("Zagraj ponownie :) !!!");
            alert.show();
            gameOver = true;
            return true;
        }
        return false;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
