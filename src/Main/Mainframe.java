package Main;

import Logic.Manager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Optional;

public class Mainframe extends Application {
    //TODO UI: MAIN: HOST, CONNECT, LEADERBOARD
    //TODO UI: PLAYWIN: Playername, Kills, board with Coords,
    //TODO UI: Player Spawn move with awsd shoot with arrowkeys
    private Stage stage;
    private Scene mainMenue;
    private Scene inGame;
    private static Mainframe instance;
    private boolean isLoggedIn;
    private Manager manager;

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
        username.textProperty().addListener((observable, oldValue, newValue) -> {
            loginButton.setDisable(newValue.trim().isEmpty());
        });

        dialog.getDialogPane().setContent(grid);

        // Request focus on the username field by default.
        Platform.runLater(() -> username.requestFocus());

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

    private void setUpInGameScene() {
        lblPlayer = new Label("Player: ");
        lblKills = new Label("Kills: ");

        HBox horizontalBox = new HBox(50);
        horizontalBox.setAlignment(Pos.TOP_CENTER);
        horizontalBox.getChildren().addAll(lblPlayer, lblKills);

        inGame = new Scene(horizontalBox, 1200, 628);
        inGame.getStylesheets().add(Mainframe.class.getResource("UIStyle.css").toString());

        stage.setScene(inGame);
        stage.setTitle("2D Shooter EXTREME");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
