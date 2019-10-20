package Logic;

import Data.Data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Manager {
    private Data data;
    private Tools tools;
    private List<Player> listOfPlayer;
    public Player currentPlayer;
    public GameField gameField;

    public Manager() {
        data = new Data();
        tools = new Tools();
        listOfPlayer = new ArrayList<>();
        gameField = new GameField();
        load();
    }

    public void newGame() {
        SpawnPlayer();
        //TODO: Log new game started for when both player have to start at the same time
    }

    public void SpawnPlayer()
    {
        currentPlayer.setxCord(ThreadLocalRandom.current().nextInt(0, gameField.x));
        currentPlayer.setyCord(ThreadLocalRandom.current().nextInt(0, gameField.y));
    }

    //TODO: PlayerGotShot
    //Player dies respawn, kill for other player up Save new kill in fiel
    void CheckIfPLayerGotHit() {
        //TODO: If Shot coord == player coord
        //shoot owner gets raiseKill()
        //then respawn shot player
        //TODO: Update UI
        //TODO: Save new Stats
    }

    public String login(String playerName) {
        if (tools.checkIfPlayerExists(playerName, listOfPlayer)) {
            for (Player player : listOfPlayer) {
                if (player.toString().contains(playerName)) {
                    currentPlayer = player;
                    break;
                }
            }

            return "Welcome back " + currentPlayer.getName();
        } else {
            currentPlayer = new Player(playerName);
            listOfPlayer.add(currentPlayer);
            saveNewPlayer(currentPlayer);

            return "Welcome: " + currentPlayer.getName();
        }
    }

    public void load() {
        String content = data.readWholeFile();
        listOfPlayer = tools.handlePlayerContent(content);
    }

    public void saveNewPlayer(Player playerToSave) {
        try {
            data.writeNewPlayer(playerToSave.toString());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    void updatePlayers() {
        try {
            data.writeNewFile(tools.createContentFromList(listOfPlayer));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    //TODO: Player Input,
    //input key, wasd then movement, arrowkeys then shoot
    public void moveUp()
    {
        currentPlayer.setyCord(((currentPlayer.getyCord() - currentPlayer.speed) % gameField.y + gameField.y) % gameField.y);
    }
    public void moveDown()
    {
        currentPlayer.setyCord(((currentPlayer.getyCord() + currentPlayer.speed) % gameField.y + gameField.y) % gameField.y);
    }
    public void moveLeft()
    {
        currentPlayer.setxCord(((currentPlayer.getxCord() - currentPlayer.speed) % gameField.x+gameField.x) % gameField.x);
    }
    public void moveRight()
    {
        currentPlayer.setxCord(((currentPlayer.getxCord() + currentPlayer.speed) % gameField.x + gameField.x) % gameField.x);
    }
}
