package Main;

import Logic.GameField;
import Logic.Hitbox;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

public class Mainframe extends Application {
    //TODO UI: MAIN: LEADERBOARD
    //TODO UI: PLAYWIN:
    private Stage stage;
    private Scene mainMenue;
    private Scene inGame;
    private static Mainframe instance;
    private Manager manager;
    //TODO: Bitte in Manager oder Shot Klasse
    //moving is Shot classer, timer? wemgehört die Timer name mehr spezifieren
    private ArrayList<Logic.Shot> shots = new ArrayList<>();
    private boolean aShot;
    private boolean moving;
    private double timer = 0;
    private Label lblConnectionInfo;

    public Mainframe() {
        instance = this;
        manager = new Manager();
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
        lblConnectionInfo = new Label("Disconnected");

        Button btnNewGame = new Button("Start Game");
        btnNewGame.setOnAction(e -> handleStartButton());

        Button btnHostGame = new Button("Host");
        btnHostGame.setOnAction(e -> handleHost());

        Button btnConnect = new Button("Connect");
        btnConnect.setOnAction(e -> handleConnection());

        Button btnLogin = new Button("Login");
        btnLogin.setOnAction(e -> handleLogin(lblLoginLog));

        Button btnLeaderboard = new Button("Leaderboard");
        //TODO: Add event on Leaderboard button

        VBox verticalBox = new VBox(20);
        verticalBox.setAlignment(Pos.CENTER);
        verticalBox.getChildren().addAll(lblMainMenue, lblConnectionInfo, lblLoginLog, btnHostGame, btnConnect, btnLogin, btnNewGame, btnLeaderboard);
        mainMenue = new Scene(verticalBox, 300, 600);
        mainMenue.getStylesheets().add(Mainframe.class.getResource("UIStyle.css").toString());

        stage.setScene(mainMenue);
        stage.setTitle("2D Shooter Main Menue");
    }

    private void handleHost() {
        manager.initHost();
        Dialog dialog = new Dialog();
        dialog.setTitle("Hosting");
        dialog.setHeaderText(null);

        Label lblIP = new Label("current IP: " + manager.host.getMyIP());
        Label lblPort = new Label("Port: " + manager.host.getMyPort());
        Label lblClient = new Label(((manager.host.client != null) ? manager.host.client.getLocalAddress() + " connected" : "Nobody connected."));

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField txfPort = new TextField();
        txfPort.setPromptText(manager.host.getMyPort());

        Button btnChangePort = new Button("change port");
        btnChangePort.setOnAction(e -> {
            try {
                manager.host.setMyPort(new Integer(txfPort.getText()));
                lblPort.setText("Port: " + manager.host.getMyPort());
            } catch (Exception exc) {
                Alerter.Alert(Alert.AlertType.ERROR, "Error", "Please use numbers for the Port");
            }
        });

        Button btnRefresh = new Button("Refresh");
        btnRefresh.setOnAction(e -> {
            if (manager.host.client != null) {
                lblClient.setText(manager.host.client.getLocalAddress() + " connected");
                manager.isConnected = true;
            }
        });

        grid.add(lblIP, 0, 0);
        grid.add(lblPort, 0, 1);
        grid.add(new Label("Port: "), 0, 2);
        grid.add(txfPort, 1, 2);
        grid.add(btnRefresh, 0, 3);
        grid.add(btnChangePort, 1, 3);
        grid.add(lblClient, 0, 4);

        dialog.getDialogPane().setContent(grid);

        manager.host.createHost();

        dialog.showAndWait();

        if (manager.host.client != null) {
            lblConnectionInfo.setText("Hosting" + manager.host.client.getLocalAddress());
            manager.isConnected = true;
            if (manager.currentPlayer != null) {
                manager.SendNewLoginInfo();
            }
            manager.handleIncommingMessages();
            if (manager.otherPlayer != null)
                lblConnectionInfo.setText("Hosting " + manager.otherPlayer.getName());
        } else
            lblConnectionInfo.setText("Disconnected");
    }

    private void handleConnection() {
        manager.initClient();
        Dialog dialog = new Dialog();
        dialog.setTitle("Connecting");
        dialog.setHeaderText(null);

        Label lblIP = new Label("current IP: " + manager.client.getMyIP());
        Label lblPort = new Label("Port: " + manager.client.getMyPort());
        Label lblServerIP = new Label("Server IP:");

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField txfServerIP = new TextField();
        txfServerIP.setText("192.168.8.103");

        TextField txfPort = new TextField();
        txfPort.setText(manager.client.getMyPort());

        Button btnConnect = new Button("Connect");
        btnConnect.setOnAction(e -> {
            try {
                manager.client.setMyPort(new Integer(txfPort.getText()));
                lblPort.setText("Port: " + manager.client.getMyPort());
                manager.client.connectToHost(txfServerIP.getText(), new Integer(manager.client.getMyPort()));
            } catch (IOException ioExc) {
                Alerter.Alert(Alert.AlertType.ERROR, "IO Error", "Something went wrong" + ioExc.getMessage());
            } catch (Exception exc) {
                Alerter.Alert(Alert.AlertType.ERROR, "Error", "Please use numbers for the port");
            }
        });

        grid.add(lblIP, 0, 0);
        grid.add(lblPort, 0, 1);
        grid.add(new Label("Host Address: "), 0, 2);
        grid.add(txfServerIP, 1, 2);
        grid.add(new Label("Port: "), 0, 3);
        grid.add(txfPort, 1, 3);
        grid.add(btnConnect, 0, 4);

        dialog.getDialogPane().setContent(grid);

        dialog.showAndWait();

        if (manager.client.host != null) {
            lblConnectionInfo.setText("Connected to " + manager.client.host.getLocalAddress());
            if (manager.currentPlayer != null) {
                manager.SendNewLoginInfo();
            }
            manager.handleIncommingMessages();
            if (manager.otherPlayer != null)
                lblConnectionInfo.setText("Connected to " + manager.otherPlayer.getName());
        } else
            lblConnectionInfo.setText("Disconnected");
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

        playerName.ifPresent(s -> lblLoginLog.setText(manager.login(s)));
    }

    private void handleStartButton() {
        manager.handleIncommingMessages();
        if (manager.currentPlayer != null && manager.otherPlayer != null) {
            if (manager.client != null)
                lblConnectionInfo.setText("Connected to " + manager.otherPlayer.getName());
            if (manager.host != null)
                lblConnectionInfo.setText("Hosting " + manager.otherPlayer.getName());
            if (manager.otherPlayer != null) {
                setUpInGameScene();
                handleInGame();
            }
        } else {
            Alerter.Alert(Alert.AlertType.ERROR, "Login Error", "You and Other Player must login first");
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

    private void handleInput(ArrayList<String> input) {
        timer -= 0.016;
        if (input.contains("W")) {
            if (manager.currentPlayer.getyCord() > 1) {
                redPlayer = new Image("images/RedNorth.png");
                manager.moveUp();
                moving = true;
            }
        } else if (input.contains("A")) {
            if (manager.currentPlayer.getxCord() > 1) {
                redPlayer = new Image("images/RedWest.png");
                manager.moveLeft();
                moving = true;
            }
        } else if (input.contains("S")) {
            if (manager.currentPlayer.getyCord() < (manager.gameField.y - 101)) {
                redPlayer = new Image("images/RedSouth.png");
                manager.moveDown();
                moving = true;
            }
        } else if (input.contains("D")) {
            if (manager.currentPlayer.getxCord() < (manager.gameField.x - 101)) {
                redPlayer = new Image("images/RedEast.png");
                manager.moveRight();
                moving = true;
            }
        } else {
            moving = false;
        }
        if (input.contains("UP")) {
            if (timer <= 0) {
                if (!aShot) {
                    if (!moving) {
                        redPlayer = new Image("images/RedNorth.png");
                    }
                    shots.add(manager.shoot(0));
                    aShot = true;
                    timer = 0.5;
                }
            }
        } else if (input.contains("DOWN")) {
            if (timer <= 0) {
                if (!aShot) {
                    if (!moving) {
                        redPlayer = new Image("images/RedSouth.png");
                    }
                    shots.add(manager.shoot(2));
                    aShot = true;
                    timer = 0.5;
                }
            }
        } else if (input.contains("LEFT")) {
            if (timer <= 0) {
                if (!aShot) {
                    if (!moving) {
                        redPlayer = new Image("images/RedWest.png");
                    }
                    shots.add(manager.shoot(3));
                    aShot = true;
                    timer = 0.5;
                }
            }
        } else if (input.contains("RIGHT")) {
            if (timer <= 0) {
                if (!aShot) {
                    if (!moving) {
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
        Canvas canvas = new Canvas(manager.gameField.x, manager.gameField.y);

        HBox horizontalBox = new HBox(50);
        horizontalBox.setAlignment(Pos.CENTER);
        horizontalBox.getChildren().addAll(lblPlayer, lblKills);

        VBox verticalBox = new VBox(30);
        verticalBox.setAlignment(Pos.TOP_CENTER);
        verticalBox.getChildren().addAll(horizontalBox, canvas);

        inGame = new Scene(verticalBox, 1400, 1000);
        inGame.getStylesheets().add(Mainframe.class.getResource("UIStyle.css").toString());
        ArrayList<String> input = new ArrayList<>();
        inGame.setOnKeyPressed(
                e -> {
                    String code = e.getCode().toString();

                    // only add once... prevent duplicates
                    if (!input.contains(code))
                        input.add(code);
                });
        inGame.setOnKeyReleased(
                e -> {
                    String code = e.getCode().toString();
                    input.remove(code);
                    aShot = false;
                });

        GraphicsContext gc = canvas.getGraphicsContext2D();

        redPlayer = new Image("images/RedNorth.png");
        shotImage = new Image("images/shot.png");

        new AnimationTimer() {
            public void handle(long currentNanoTime) {
                manager.inGame();
                gc.clearRect(0, 0, manager.gameField.x, manager.gameField.y);
                gc.setFill(Color.DARKGRAY);
                gc.fillRect(0, 0, manager.gameField.x, manager.gameField.y);

                handleInput(input);

                // background image clears canvas
                gc.drawImage(redPlayer, manager.currentPlayer.getxCord(), manager.currentPlayer.getyCord());

                shots.forEach(shot -> {
                    if (shot.dead == true) {
                        shots.remove(shot);
                    }
                    switch (shot.direction) {
                        case 0:
                            shot.moveUp();
                            gc.drawImage(shotImage, shot.getTranslateX(), shot.getTranslateY());

                            if (shot.getTranslateY() < -10) {
                                shot.dead = true;
                            }
                            break;
                        case 1:
                            shot.moveRight();
                            gc.drawImage(shotImage, shot.getTranslateX(), shot.getTranslateY());
                            if (shot.getTranslateX() > manager.gameField.x) {
                                shot.dead = true;
                            }
                            break;
                        case 2:
                            shot.moveDown();
                            gc.drawImage(shotImage, shot.getTranslateX(), shot.getTranslateY());
                            if (shot.getTranslateY() > manager.gameField.y) {
                                shot.dead = true;
                            }
                            break;
                        case 3:
                            shot.moveLeft();
                            gc.drawImage(shotImage, shot.getTranslateX(), shot.getTranslateY());
                            if (shot.getTranslateX() < -10) {
                                shot.dead = true;
                            }
                            break;
                    }

                    if ((shot.getBoundsInParent().intersects(manager.currentPlayer.getBoundsInParent()))) {
                        //he ded
                    }


                });
            }
        }.start();

        stage.setScene(inGame);
        stage.setTitle("2D Shooter EXTREME");
        stage.setFullScreen(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
