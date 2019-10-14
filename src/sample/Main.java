package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setFullScreen(true);
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
        //TODO UI: Login Playername, Prüfen ob player exestiert im file, oder neue player erstellen
        //TODO UI: MAIN: HOST, CONNECT, LEADERBOARD
        //TODO UI: PLAYWIN: Playername, Kills, board with Coords,
        //TODO UI: Player Spawn move with awsd shoot with arrowkeys
    }


    public static void main(String[] args) {
        launch(args);
    }
}
