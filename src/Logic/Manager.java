package Logic;

import Data.IOData;
import Main.Alerter;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
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
                            otherPlayer.color = Logic.Color.Red;
                            break;
                        }
                    }
                }
            }
            //Type Coordinates
            else if(command.startsWith("tc")) {
                //Type X Y Direction
                String[] data = command.split(" ");
                if(data[1].contains("host"))
                {
                    otherPlayer.setxCord(new Integer(data[2]).intValue());
                    otherPlayer.setyCord(new Integer(data[3]).intValue());
                    otherPlayer.direction = evaluateDirection(data[4]);
                }
                else if(data[1].contains("client"))
                {
                    currentPlayer.setxCord(new Integer(data[2]).intValue());
                    currentPlayer.setyCord(new Integer(data[3]).intValue());
                    currentPlayer.direction = evaluateDirection(data[4]);
                }
                else if(data[1].contains("shots"))
                {
                    playerShots = new ArrayList<>();
                    for( int i = 2; i < data.length; i++)
                    {
                        String[] shotInfo = data[i].split(";");
                        playerShots.add(new Shot(new Integer(shotInfo[0]).intValue(), new Integer(shotInfo[1]).intValue(), Shot.width, Shot.height, evaluateDirection(shotInfo[2]), Shot.color, new Integer(shotInfo[3]).intValue() ));
                    }
                }
            }
            //Client Input
            else if(command.startsWith("ci")){
                //Type Direction (1,2,3,4 up, down, left right)
                String[] data = command.split(" ");
                if(data.length > 1)
                    handleInput(otherPlayer, data[1]);
            }
            //Kill
            else if(command.startsWith("k")){
                //Player
            }
            //GameOver
            else if(command.startsWith("go")){
                // Stop.
            }
        }
    }

    public Direction evaluateDirection(String direction)
    {
        switch (direction)
        {
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
        SpawnPlayer();
        //TODO: Log new game started for when both player have to start at the same time
    }

    private void SpawnPlayer() {
        int x  = ThreadLocalRandom.current().nextInt(0, gameField.x);
        int y = ThreadLocalRandom.current().nextInt(0, gameField.y);
        currentPlayer.setxCord(x);
        currentPlayer.setyCord(y);

        int newX = ThreadLocalRandom.current().nextInt(0, gameField.x);
        int newY = ThreadLocalRandom.current().nextInt(0, gameField.y);
        otherPlayer.setxCord(newX != x ? newX : ThreadLocalRandom.current().nextInt(0, gameField.x));
        otherPlayer.setyCord(newY != y ? newY : ThreadLocalRandom.current().nextInt(0, gameField.y));
    }

    public void doInGame() {
        //TODO: send currentPlayer Infos, send currentPLayer Shots
        if(host != null)
        {
            try {
                host.writeMessage("/tc " + "host" + " " + currentPlayer.getxCord() + " " + currentPlayer.getyCord() + " " + currentPlayer.direction);
                host.writeMessage("/tc " + "client" + " " + otherPlayer.getxCord() + " " + otherPlayer.getyCord() + " " + otherPlayer.direction);

                String message = "/tc shots ";
                for (Shot shot : playerShots) {
                    message+= (int)shot.getTranslateX() +";"+(int)shot.getTranslateY()+";"+ shot.direction+";"+shot.type +" ";
                }
                host.writeMessage(message);
            }
            catch (IOException exc)
            {
                Alerter.Alert(Alert.AlertType.ERROR, "ERROR", "Sending player infos failed "+exc.getMessage());
            }
        }

        handleIncommingMessages();
    }

    //TODO: PlayerGotShot
    //Player dies respawn, kill for other player up Save new kill in fiel
    public void CheckIfPLayerGotHit(Shot shot) {
        //TODO: check for both shot.type who gets shot?
        if ((shot.getBoundsInParent().intersects(currentPlayer.getBoundsInParent()))) {
            //he ded
        }
        //TODO: shoot owner gets raiseKill()
        //TODO: then respawn shot player
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
            currentPlayer = new Player(playerName, Logic.Color.Blue);
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
        } catch (IOException ioExc) {
            Alerter.Alert(Alert.AlertType.ERROR, "IO Error", "Something went wrong" + ioExc.getMessage());
        }
    }

    public void handleInput(Player player, String input)
    {
        player.shotCooldownTimer -= 0.016;

        if (player.shotCooldownTimer <= 0) {
            if (input.contains("UP")) {
                addShot(Direction.NORTH,player);
            } else if (input.contains("DOWN")) {
                addShot(Direction.SOUTH,player);
            } else if (input.contains("LEFT")) {
                addShot(Direction.WEST,player);
            } else if (input.contains("RIGHT")) {
                addShot(Direction.EAST,player);
            }
        }

        if (input.equals("W") ) {
            if (player.getyCord() > 1)
                moveUp(player);
        } else if (input.equals("A")) {
            if (player.getxCord() > 1)
                moveLeft(player);
        } else if (input.equals("S")) {
            if (player.getyCord() < (gameField.y - 101))
                moveDown(player);
        } else if (input.equals("D")) {
            if (player.getxCord() < (gameField.x - 101))
                moveRight(player);
        }
    }

    public void handleInput(ArrayList<String> inputs) {
        try {
            for (String input: inputs) {
                if (client != null)
                    client.writeMessage("/ci " + input);
                if(host != null)
                    handleInput(currentPlayer, input);
            }
        } catch (IOException ioExc) {
            Alerter.Alert(Alert.AlertType.ERROR, "IO Error", "Something went wrong" + ioExc.getMessage());
        }
    }

    void updatePlayers() {
        try {
            IOData.writeNewFile(tools.createContentFromList(listOfPlayer));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void moveUp(Player player) {
        player.setyCord(player.getyCord() - player.speed); // % gameField.y + gameField.y) % gameField.y);
        player.direction = Direction.NORTH;
    }

    public void moveDown(Player player) {
        player.setyCord(player.getyCord() + player.speed); // % gameField.y + gameField.y) % gameField.y);
        player.direction = Direction.SOUTH;
    }

    public void moveLeft(Player player) {
        player.setxCord(player.getxCord() - player.speed); // % gameField.x + gameField.x) % gameField.x);
        player.direction = Direction.WEST;
    }

    public void moveRight(Player player) {
        player.setxCord(player.getxCord() + player.speed); //% gameField.x + gameField.x) % gameField.x);
        player.direction = Direction.EAST;
    }

    private Shot shoot(Direction heading, Player player, int playerNum) {
        return new Shot((player.getxCord()) + 50, (player.getyCord()) + 50, Shot.width, Shot.height, heading, Shot.color, playerNum);
    }

    public void addShot(Direction heading, Player player) {
        if(host != null)
            playerShots.add(shoot(heading, player,0));
        playerShots.add(shoot(heading, player,1));

        player.shotCooldownTimer = 0.5;
    }
}
