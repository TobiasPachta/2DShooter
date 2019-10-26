package Logic;

import Data.IOData;
import Main.Alerter;
import javafx.scene.control.Alert;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Manager {
    private IOData IOData;
    private Tools tools;
    private List<Player> listOfPlayer;
    public Player otherPlayer;
    public Player currentPlayer;
    public GameField gameField;
    public Data.Host host;
    public Data.Client client;
    public boolean isConnected = false;

    public Manager() {
        IOData = new IOData();
        tools = new Tools();
        listOfPlayer = new ArrayList<>();
        gameField = new GameField();
        load();
    }

    public void initHost() {
        try {
            if (host != null)
                host.host.close();
        } catch (IOException ioExc) {
            Alerter.Alert(Alert.AlertType.ERROR, "IO Error", "Something went wrong" + ioExc.getMessage());
        }
        host = null;
        otherPlayer = null;
        host = new Data.Host();
        host.createHost();
    }

    public void initClient() {
        try {
            if (client != null)
                client.host.close();
        } catch (IOException ioExc) {
            Alerter.Alert(Alert.AlertType.ERROR, "IO Error", "Something went wrong" + ioExc.getMessage());
        }
        client = null;
        otherPlayer = null;
        client = new Data.Client();
    }

    public void handleIncommingMessages() {
        String message = "";
        try {
            if (client != null)
                message = client.readMessage();
            else if (host != null)
                message = host.readMessage();
        } catch (IOException ioExc) {
            Alerter.Alert(Alert.AlertType.ERROR, "IO Error", "Something went wrong" + ioExc.getMessage());
        }

        String[] commands = message.split("/");
        for (String command : commands) {
            //new login
            if (command.startsWith("nl")) {
                String[] splitLine = command.split(":");

                if (tools.checkIfPlayerExists(splitLine[1], listOfPlayer)) {
                    for (Player player : listOfPlayer) {
                        if (player.toString().contains(splitLine[1])) {
                            otherPlayer = player;
                            break;
                        }
                    }
                }
            }
            //TODO: Handle other messages

        }
    }

    public void newGame() {
        SpawnPlayer();
        //TODO: Log new game started for when both player have to start at the same time
    }

    private void SpawnPlayer() {
        currentPlayer.setxCord(ThreadLocalRandom.current().nextInt(0, gameField.x));
        currentPlayer.setyCord(ThreadLocalRandom.current().nextInt(0, gameField.y));
    }

    public void inGame() {
        //TODO: send currentPlayer Infos, send currentPLayer Shots
        handleIncommingMessages();
    }

    //TODO: PlayerGotShot
    //Player dies respawn, kill for other player up Save new kill in fiel
    void CheckIfPLayerGotHit() {
        //TODO: If Shot coord == player coord
        //shoot owner gets raiseKill()
        //then respawn shot player
        //TODO: Save new Stats
    }

    public String login(String playerName) {
        load();
        if (tools.checkIfPlayerExists(playerName, listOfPlayer)) {
            for (Player player : listOfPlayer) {
                if (player.toString().contains(playerName)) {
                    currentPlayer = player;
                    break;
                }
            }
        } else {
            currentPlayer = new Player(playerName);
            listOfPlayer.add(currentPlayer);
            saveNewPlayer(currentPlayer);
        }
        SendNewLoginInfo();
        return "Welcome: " + currentPlayer.getName();

    }


    //TODO: more send functions
    public void SendNewLoginInfo() {
        try {
            if (client != null && currentPlayer != null)
                client.writeMessage("/nl:" + currentPlayer.getName());
            else if (host != null && currentPlayer != null)
                host.writeMessage("/nl:" + currentPlayer.getName());
        } catch (IOException ioExc) {
            Alerter.Alert(Alert.AlertType.ERROR, "IO Error", "Something went wrong" + ioExc.getMessage());
        }
    }

    private void load() {
        String content = IOData.readWholeFile();
        listOfPlayer = tools.handlePlayerContent(content);
    }

    private void saveNewPlayer(Player playerToSave) {
        try {
            IOData.writeNewPlayer(playerToSave.toString());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    void updatePlayers() {
        try {
            IOData.writeNewFile(tools.createContentFromList(listOfPlayer));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void moveUp() {
        currentPlayer.setyCord(currentPlayer.getyCord() - currentPlayer.speed); // % gameField.y + gameField.y) % gameField.y);
    }

    public void moveDown() {
        currentPlayer.setyCord(currentPlayer.getyCord() + currentPlayer.speed); // % gameField.y + gameField.y) % gameField.y);
    }

    public void moveLeft() {
        currentPlayer.setxCord(currentPlayer.getxCord() - currentPlayer.speed); // % gameField.x + gameField.x) % gameField.x);
    }

    public void moveRight() {
        currentPlayer.setxCord(currentPlayer.getxCord() + currentPlayer.speed); //% gameField.x + gameField.x) % gameField.x);
    }

    public Shot shoot(int heading) {
        Player who = currentPlayer;
        return new Shot((who.getxCord()) + 50, (who.getyCord()) + 50, 10, 10, heading, Color.YELLOW);
    }
}
