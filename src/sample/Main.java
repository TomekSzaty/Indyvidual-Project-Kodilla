package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
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
import java.util.stream.Collectors;


public class Main extends Application {

    private String fName;
    private Instant start;
    private Instant end;
    private int gameTime;
    private Stage stage;
    private static Scene scene1, scene2, scene3;
    private GridPane gridPane = new GridPane();
    private BorderPane borderPane = new BorderPane();
    private Label label = new Label("KÓŁKO  -- KRZYŻYK");
    private Button restartButton = new Button("Restart Game");
    Font font = Font.font("Tahoma", FontWeight.BOLD, 30);
    private Button[] btns = new Button[9];
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
        Label label1 = new Label("Witaj w grze  \"Kółko-Krzyżyk\"!");
        label1.setFont(Font.font("Tahoma", FontWeight.BOLD, 32));
        Text writeName = new Text("Wpisz swoje imie");
        writeName.setFont(Font.font("Tahoma", FontWeight.BOLD, 30));
        TextField field = new TextField();
        field.setFont(Font.font(18));
        field.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                fName = field.getText();
            }
        });
        Button button1 = new Button("START GRY");
        button1.setFont(Font.font("Tahoma", FontWeight.NORMAL, 25));
        button1.setAlignment(Pos.BOTTOM_CENTER);
        button1.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                fName = field.getText();
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
        grid.add(label1, 0, 0, 2, 1);//kolumn.0, rząd 0, zakres kol.2,zakres rz.1
        grid.add(writeName, 0, 1); //kolumna 0 ,rzad 1
        grid.add(field, 0, 2, 1, 1); //kol.0, rz.1, zakres kol.2, zakres rz.1
        grid.add(button1, 0, 3, 2, 2);
        scene1 = new Scene(grid, 600, 600);
        Pane pane = new Pane();
        Button button3 = new Button("Zakończ");
        button3.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        button3.setOnAction(event -> Platform.exit());
        pane.getChildren().add(button3);
        scene3 = new Scene(pane, 600, 600);
        this.createGUI();
        this.handleEvent();
        scene2 = new Scene(borderPane, 600, 600);
        stage.setScene(scene1);
        scene1.getStylesheets().add
                (Main.class.getResource("sample.css").toExternalForm());
        stage.show();
    }

    public void createGUI() {
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
                btns[btNr] = button;
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
                    btns[i].setGraphic(null);
                    btns[i].setBackground(null);
                    btns[i].setBorder(new Border(new BorderStroke(Color.DARKBLUE, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1))));
                    gameOver = false;
                    countDraw = 0;
                    start = Instant.now();
                }
            }
        });
        for (Button btn : btns) {
            btn.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    Button currentBtn = (Button) actionEvent.getSource();
                    String idS = currentBtn.getId();
                    int idI = Integer.parseInt(idS);

                    if (!gameOver) {
                        if (gameState[idI] == 3) {
                            //ruch gracza
                            playerTurn(idI);
                            //Ruch komputera
                            cpuHardTurn();
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

    private void playerTurn(int fieldIndex) {
        btns[fieldIndex].setGraphic(new ImageView(new Image("file:src/sample/Krzyzyk1.png")));
        gameState[fieldIndex] = 1;
        checkForWinner();
        checkForDraw();
    }

    public void cpuEasyTurn() {
        for (int i = 0; i < gameState.length; i++) {
            if (gameState[i] == 3) {
                int cpuIndex = i;
                btns[cpuIndex].setGraphic(new ImageView(new Image("file:src/sample/Kolko1.png")));
                gameState[cpuIndex] = 0;
                checkForWinner();
                checkForDraw();
                break;
            }
        }
    }

    public void cpuHardTurn() {
        List<Integer> availableFields = new ArrayList<>();

        for (int i = 0; i < gameState.length; i++) {
            if (gameState[i] == 3) {
                availableFields.add(i);
            }
        }

        Random random = new Random();

        int index = random.nextInt(availableFields.size());
        int cpuIndex = availableFields.get(index);
        btns[cpuIndex].setGraphic(new ImageView(new Image("file:src/sample/Kolko1.png")));
        gameState[cpuIndex] = 0;
        checkForWinner();
        checkForDraw();
    }

    private void checkForWinner() {
        if (!gameOver) {
            for (int[] wp : winningPosition) {
                if (gameState[wp[0]] == gameState[wp[1]] && gameState[wp[1]] == gameState[wp[2]] && gameState[wp[1]] != 3) {
                    //aktualny gracz wygrywa
                    end = Instant.now();
                    gameTime = (int) Duration.between(start, end).getSeconds();
                    AlertBox.display("Game over", "Czas gry " + ": " + gameTime + " sec");
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("XOX-WYGRANA!-XOX");
                    alert.setContentText((activePlayer == 1 ? "X" : "O") + " ZWYCIEZCA :) !!!"); //imie gracza zamiast X
                    btns[wp[0]].setBackground(new Background(new BackgroundFill(Color.GREEN, CornerRadii.EMPTY, Insets.EMPTY)));
                    btns[wp[1]].setBackground(new Background(new BackgroundFill(Color.GREEN, CornerRadii.EMPTY, Insets.EMPTY)));
                    btns[wp[2]].setBackground(new Background(new BackgroundFill(Color.GREEN, CornerRadii.EMPTY, Insets.EMPTY)));
                    alert.show();
                    gameOver = true;
                    break;
                }
            }
        }
        countDraw++;
    }

    private void checkForDraw() {
        if (countDraw == 9 && !gameOver) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("XOX-REMIS!!!-XOX");
            alert.setContentText("ZAGRAJ PONOWNIE :) !!!");
            alert.show();
            gameOver = true;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
