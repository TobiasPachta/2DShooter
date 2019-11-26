package Logic;

import Data.IOData;
import Main.Alerter;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Manager {
    private IOData IOData;
    private Tools tools;
    public List<Player> listOfPlayer;
    public Player otherPlayer;
    public Player currentPlayer;
    public GameField gameField;
    public Data.Host host;
    public Data.Client client;
    public Image shotImage;
    public boolean isConnected = false;
    public List<Logic.Shot> playerShots = new ArrayList<>();

    public Manager() {
        IOData = new IOData();
        tools = new Tools();
        listOfPlayer = new ArrayList<>();
        gameField = new GameField();
        shotImage = new Image("images/shot.png");
        load();
    }

    public void initHost() {
        if (host != null)
            host.close();
        host = null;
        otherPlayer = null;
        host = new Data.Host();
        host.createHost();
    }

    public void initClient() {
        if (client != null)
            client.close();
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

                if (splitLine.length == 1)
                    continue;
                if (tools.checkIfPlayerExists(splitLine[1], listOfPlayer)) {
                    for (Player player : listOfPlayer) {
                        if (player.toString().contains(splitLine[1])) {
                            otherPlayer = player;
                            otherPlayer.color = Logic.Color.Red;
                            break;
                        }
                    }
                } else {
                    otherPlayer = new Player(splitLine[1], Color.Red);
                    listOfPlayer.add(otherPlayer);
                    saveNewPlayer(otherPlayer);
                }
            }
            //Type Coordinates
            else if (command.startsWith("tc")) {
                String[] data = command.split(" ");
                if(data.length<3)
                    continue;
                if (data[1].contains("shots")) {
                    playerShots = new ArrayList<>();
                    for (int i = 2; i < data.length; i++) {
                        String[] shotInfo = data[i].split(";");
                        if (shotInfo.length < 3)
                            return;
                        playerShots.add(new Shot(Integer.parseInt(shotInfo[0]), Integer.parseInt(shotInfo[1]), evaluateDirection(shotInfo[2]), Integer.parseInt(shotInfo[3])));
                    }
                }
                if(data.length < 4)
                    continue;
                if (data[1].contains("host")) {
                    otherPlayer.setxCord(Integer.parseInt(data[2]));
                    otherPlayer.setyCord(Integer.parseInt(data[3]));
                    otherPlayer.direction = evaluateDirection(data[4]);
                } else if (data[1].contains("client")) {
                    currentPlayer.setxCord(Integer.parseInt(data[2]));
                    currentPlayer.setyCord(Integer.parseInt(data[3]));
                    currentPlayer.direction = evaluateDirection(data[4]);
                }
            }
            //Client Input
            else if (command.startsWith("ci")) {
                //Type Direction (1,2,3,4 up, down, left right)
                String[] data = command.split(" ");
                if (data.length > 1)
                    handleInput(otherPlayer, data[1]);
            }
            //Kill
            else if (command.startsWith("k")) {
                //Player
                if (command.contains(currentPlayer.getName())) {
                    playerGotKill(currentPlayer);
                } else if (command.contains(otherPlayer.getName())) {
                    playerGotKill(otherPlayer);
                }
            }
        }
    }

    private Direction evaluateDirection(String direction) {
        switch (direction) {
            case "EAST":
                return Direction.EAST;
            case "SOUTH":
                return Direction.SOUTH;
            case "WEST":
                return Direction.WEST;
            case "NORTH":
                break;
        }

        return Direction.NORTH;
    }

    public void newGame() {
        //TODO: nur von host
        spawnPlayer(currentPlayer);
        spawnPlayer(otherPlayer);
        //TODO: Log new game started for when both player have to start at the same time
    }

    private void spawnPlayer(Player player) {
        int x = ThreadLocalRandom.current().nextInt(0, gameField.x);
        int y = ThreadLocalRandom.current().nextInt(0, gameField.y);
        player.setxCord(x);
        player.setyCord(y);
    }

    public void doInGame() {
        if (host != null) {
            try {
                host.writeMessage("/tc " + "host" + " " + currentPlayer.getxCord() + " " + currentPlayer.getyCord() + " " + currentPlayer.direction);
                host.writeMessage("/tc " + "client" + " " + otherPlayer.getxCord() + " " + otherPlayer.getyCord() + " " + otherPlayer.direction);

                StringBuilder message = new StringBuilder("/tc shots ");
                for (Shot shot : playerShots) {
                    message.append((int) shot.getTranslateX()).append(";").append((int) shot.getTranslateY()).append(";").append(shot.direction).append(";").append(shot.type).append(" ");
                }
                host.writeMessage(message.toString());
            } catch (IOException exc) {
                Alerter.Alert(Alert.AlertType.ERROR, "ERROR", "Sending player infos failed " + exc.getMessage());
            }
        }

        handleIncommingMessages();
    }

    public void checkIfPLayerGotHit(Shot shot) {
        if (shot.type == 0 && shot.hitbox.intersects(otherPlayer.hitbox.getBoundsInLocal())) {
            playerGotKill(currentPlayer);
            sendKillMessage(currentPlayer);
            spawnPlayer(otherPlayer);
        } else if (shot.type == 1 && shot.hitbox.intersects(currentPlayer.hitbox.getBoundsInLocal())) {
            playerGotKill(otherPlayer);
            sendKillMessage(otherPlayer);
            spawnPlayer(currentPlayer);
        }
    }

    private void playerGotKill(Player player) {
        player.setKills(player.getKills() + 1);
        updatePlayers();
    }

    private void sendKillMessage(Player playerWhoKilled) {
        try {
            host.writeMessage("/k " + playerWhoKilled.getName());
        } catch (IOException ioExc) {
            Alerter.Alert(Alert.AlertType.ERROR, "IO Error", "Something went wrong" + ioExc.getMessage());
        }
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
            currentPlayer = new Player(playerName, Logic.Color.Blue);
            listOfPlayer.add(currentPlayer);
            saveNewPlayer(currentPlayer);
        }

        sendNewLoginInfo();

        return "Welcome: " + currentPlayer.getName();
    }


    public void sendNewLoginInfo() {
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
        } catch (IOException ioExc) {
            Alerter.Alert(Alert.AlertType.ERROR, "IO Error", "Something went wrong" + ioExc.getMessage());
        }
    }

    private void handleInput(Player player, String input) {
        player.shotCooldownTimer -= 0.016;

        if (player.shotCooldownTimer <= 0) {
            if (input.contains("UP")) {
                addShot(Direction.NORTH, player);
            } else if (input.contains("DOWN")) {
                addShot(Direction.SOUTH, player);
            } else if (input.contains("LEFT")) {
                addShot(Direction.WEST, player);
            } else if (input.contains("RIGHT")) {
                addShot(Direction.EAST, player);
            }
        }

        switch (input) {
            case "W":
                if (player.getyCord() > 1)
                    moveUp(player);
                break;
            case "A":
                if (player.getxCord() > 1)
                    moveLeft(player);
                break;
            case "S":
                if (player.getyCord() < (gameField.y - 101))
                    moveDown(player);
                break;
            case "D":
                if (player.getxCord() < (gameField.x - 101))
                    moveRight(player);
                break;
        }
    }

    public void handleInput(ArrayList<String> inputs) {
        try {
            for (String input : inputs) {
                if (client != null)
                    client.writeMessage("/ci " + input);
                if (host != null)
                    handleInput(currentPlayer, input);
            }
        } catch (IOException ioExc) {
            Alerter.Alert(Alert.AlertType.ERROR, "IO Error", "Something went wrong" + ioExc.getMessage());
        }
    }

    private void updatePlayers() {
        try {
            IOData.writeNewFile(tools.createContentFromList(listOfPlayer));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void moveUp(Player player) {
        player.setyCord(player.getyCord() - player.speed);
        player.direction = Direction.NORTH;
    }

    private void moveDown(Player player) {
        player.setyCord(player.getyCord() + player.speed);
        player.direction = Direction.SOUTH;
    }

    private void moveLeft(Player player) {
        player.setxCord(player.getxCord() - player.speed);
        player.direction = Direction.WEST;
    }

    private void moveRight(Player player) {
        player.setxCord(player.getxCord() + player.speed);
        player.direction = Direction.EAST;
    }

    private Shot shoot(Direction heading, Player player, int playerNum) {
        return new Shot((player.getxCord()) + 50, (player.getyCord()) + 50, heading, playerNum);
    }

    private void addShot(Direction heading, Player player) {
        if (player.getName().contains(currentPlayer.getName()))
            playerShots.add(shoot(heading, player, 0));
        if (player.getName().contains(otherPlayer.getName()))
            playerShots.add(shoot(heading, player, 1));

        player.shotCooldownTimer = 0.5;
    }
}
