package Main;

import Logic.GameField;
import Logic.Manager;
import Logic.Shot;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.Optional;

public class Mainframe extends Application {
    //TODO UI: MAIN: HOST, CONNECT, LEADERBOARD
    //TODO UI: PLAYWIN:
    private Stage stage;
    private Scene mainMenue;
    private Scene inGame;
    private static Mainframe instance;
    private boolean isLoggedIn;
    private Manager manager;
    public ArrayList<Logic.Shot> shots = new ArrayList<Logic.Shot>();
    boolean aShot;
    boolean moving;
    double timer = 0;

    public Mainframe() {
        instance = this;
        manager = new Manager();
        isLoggedIn = false;
    }

    public static Mainframe getInstance() {
        return instance;
    }

    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;

        setUpMainMenue();

        stage.show();
    }

    private void setUpMainMenue() {
        Label lblLoginLog = new Label("Not logged in");
        Label lblMainMenue = new Label("Main Menue");

        Button btnNewGame = new Button("Start Game");
        btnNewGame.setOnAction(e -> handleStartButton());

        Button btnLogin = new Button("Login");
        btnLogin.setOnAction(e -> handleLogin(lblLoginLog));

        Button btnLeaderboard = new Button("Leaderboard");
        //TODO: Add event on Leaderboard button

        VBox verticalBox = new VBox(20);
        verticalBox.setAlignment(Pos.CENTER);
        verticalBox.getChildren().addAll(lblMainMenue, lblLoginLog, btnLogin, btnNewGame, btnLeaderboard);
        mainMenue = new Scene(verticalBox, 300, 300);
        mainMenue.getStylesheets().add(Mainframe.class.getResource("UIStyle.css").toString());

        stage.setScene(mainMenue);
        stage.setTitle("2D Shooter Main Menue");
    }

    private void handleLogin(Label lblLoginLog) {
        // Create the custom dialog.
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Login Dialog");
        dialog.setHeaderText(null);

        ButtonType loginButtonType = new ButtonType("Login", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField username = new TextField();
        username.setPromptText("Username");

        grid.add(new Label("Username:"), 0, 0);
        grid.add(username, 1, 0);

        // Enable/Disable login button depending on whether a username was entered.
        Node loginButton = dialog.getDialogPane().lookupButton(loginButtonType);
        loginButton.setDisable(true);

        // Do some validation (using the Java 8 lambda syntax).
        username.textProperty().addListener((observable, oldValue, newValue) -> loginButton.setDisable(newValue.trim().isEmpty()));

        dialog.getDialogPane().setContent(grid);

        // Request focus on the username field by default.
        Platform.runLater(username::requestFocus);

        // Convert the result to a username-password-pair when the login button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                return username.getText();
            }
            return null;
        });

        Optional<String> playerName = dialog.showAndWait();

        if (playerName.isPresent()) {
            lblLoginLog.setText(manager.login(playerName.get()));
            isLoggedIn = true;
        }
    }

    private void handleStartButton() {
        if (isLoggedIn) {
            setUpInGameScene();
            handleInGame();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Login Error");
            alert.setHeaderText(null);
            alert.setContentText("You must login first");
            alert.showAndWait();
        }
    }

    private Label lblPlayer;
    private Label lblKills;

    private void handleInGame() {
        lblPlayer.setText("Player: " + manager.currentPlayer.getName());
        lblKills.setText("Kills: " + manager.currentPlayer.getKills());
        manager.newGame();
    }

    private Image redPlayer;
    private Image shotImage;
    private void handleInput(ArrayList<String> input)
    {
        timer -= 0.016;
        if(input.contains("W"))
        {
            if(manager.currentPlayer.getyCord() > 1) {
                redPlayer = new Image("images/RedNorth.png");
                manager.moveUp();
                moving = true;
            }
        }
        else if(input.contains("A"))
        {
            if(manager.currentPlayer.getxCord() > 1) {
                redPlayer = new Image("images/RedWest.png");
                manager.moveLeft();
                moving = true;
            }
        }
        else if(input.contains("S"))
        {
            if(manager.currentPlayer.getyCord() < (manager.gameField.y - 101)) {
                redPlayer = new Image("images/RedSouth.png");
                manager.moveDown();
                moving = true;
            }
        }
        else if(input.contains("D"))
        {
            if(manager.currentPlayer.getxCord() < (manager.gameField.x - 101)) {
                redPlayer = new Image("images/RedEast.png");
                manager.moveRight();
                moving = true;
            }
        } else {
            moving = false;
        }
        //TODO UI: Player Spawn, shoot with arrowkeys
        if(input.contains("UP")) {
            if(timer <= 0) {
                if (aShot == false) {
                    if (moving == false) {
                        redPlayer = new Image("images/RedNorth.png");
                    }
                    shots.add(manager.shoot(0));
                    aShot = true;
                    timer = 0.5;
                }
            }
        }
        else if(input.contains("DOWN")) {
            if(timer <= 0) {
                if (aShot == false) {
                    if (moving == false) {
                        redPlayer = new Image("images/RedSouth.png");
                    }
                    shots.add(manager.shoot(2));
                    aShot = true;
                    timer = 0.5;
                }
            }
        }
        else if(input.contains("LEFT")) {
            if(timer <= 0) {
                if (aShot == false) {
                    if(moving == false) {
                        redPlayer = new Image("images/RedWest.png");
                    }
                    shots.add(manager.shoot(3));
                    aShot = true;
                    timer = 0.5;
                }
            }
        }
        else if(input.contains("RIGHT")) {
            if(timer <= 0) {
                 if (aShot == false) {
                     if(moving == false) {
                         redPlayer = new Image("images/RedEast.png");
                     }
                    shots.add(manager.shoot(1));
                    aShot = true;
                    timer = 0.5;
                }
            }
        }

    }

    private void setUpInGameScene() {
        lblPlayer = new Label("Player: ");
        lblKills = new Label("Kills: ");
        Canvas canvas = new Canvas(manager.gameField.x,manager.gameField.y);

        HBox horizontalBox = new HBox(50);
        horizontalBox.setAlignment(Pos.CENTER);
        horizontalBox.getChildren().addAll(lblPlayer, lblKills);

        VBox verticalBox = new VBox(30);
        verticalBox.setAlignment(Pos.TOP_CENTER);
        verticalBox.getChildren().addAll(horizontalBox,canvas);

        inGame = new Scene(verticalBox, 1400, 1000);
        inGame.getStylesheets().add(Mainframe.class.getResource("UIStyle.css").toString());
        ArrayList<String> input = new ArrayList<>();
        inGame.setOnKeyPressed(
                e -> {
                    String code = e.getCode().toString();

                    // only add once... prevent duplicates
                    if ( !input.contains(code) )
                        input.add( code );
                });
        inGame.setOnKeyReleased(
                e -> {
                    String code = e.getCode().toString();
                    input.remove( code );
                    aShot = false;
                });

        GraphicsContext gc = canvas.getGraphicsContext2D();

        redPlayer = new Image("images/RedNorth.png");
        shotImage = new Image("images/shot.png");

        new AnimationTimer()
        {
            public void handle(long currentNanoTime)
            {
                gc.clearRect(0,0,manager.gameField.x,manager.gameField.y);
                gc.setFill(Color.DARKGRAY);
                gc.fillRect(0,0,manager.gameField.x,manager.gameField.y);

                handleInput(input);

                // background image clears canvas
                gc.drawImage( redPlayer, manager.currentPlayer.getxCord(), manager.currentPlayer.getyCord());
                shots.forEach(shot -> {
                    switch (shot.direction) {
                        case 0:
                            shot.moveUp();
                            gc.drawImage(shotImage, shot.getTranslateX(), shot.getTranslateY());
                            if(shot.getTranslateY() < -10) {
                                shot.dead = true;
                            }
                            break;
                        case 1:
                            shot.moveRight();
                            gc.drawImage(shotImage, shot.getTranslateX(), shot.getTranslateY());
                            if(shot.getTranslateX() > manager.gameField.x) {
                                shot.dead = true;
                            }
                            break;
                        case 2:
                            shot.moveDown();
                            gc.drawImage(shotImage, shot.getTranslateX(), shot.getTranslateY());
                            if(shot.getTranslateY() > manager.gameField.y) {
                                shot.dead = true;
                            }
                            break;
                        case 3:
                            shot.moveLeft();
                            gc.drawImage(shotImage, shot.getTranslateX(), shot.getTranslateY());
                            if(shot.getTranslateX() < -10) {
                                shot.dead = true;
                            }
                            break;
                    }

                    //if((shot.getBoundsInParent().intersects(enemyPlayer))

                    if(shot.dead == true) {
                        shots.remove(shot);
                    }

                });
            }
        }.start();

        stage.setScene(inGame);
        stage.setTitle("2D Shooter EXTREME");
        stage.setFullScreen(true);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
