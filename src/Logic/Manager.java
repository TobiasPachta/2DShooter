package Logic;

import Data.Data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Manager {
    private Data data;
    private Tools tools;
    private List<Player> listOfPlayer;
    public Player currentPlayer;

    public Manager() {
        data = new Data();
        tools = new Tools();
        listOfPlayer = new ArrayList<>();
        load();
    }

    //TODO: Player Input,
    //input key, wasd then movement, arrowkeys then shoot
    //TODO: Display Playerfield loop
    //check if shoot reaches end of map, check if shoot is on player(hit)
    public void newGame() {
        //TODO: Create Field
        //TODO: Spwan PLayer
        //TODO: Log new game started
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
}
