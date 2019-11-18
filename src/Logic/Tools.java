package Logic;

import java.util.ArrayList;
import java.util.List;

public class Tools {

    List<Player> handlePlayerContent(String content) {
        List<Player> players = new ArrayList<>();
        if (content.isEmpty())
            return players;

        String[] lines = content.split("\n");
        String playerName = "";
        int kills = 0;
        for (String line : lines) {
            if (line.isEmpty())
                continue;

            if (line.contains("Player:")) {
                String[] playerLine = line.split(":");
                playerName = playerLine[1].trim();
            } else if (line.contains("Kills:")) {
                String[] killsLine = line.split(":");
                kills = new Integer(killsLine[1].trim());
                players.add(new Player(playerName, kills, Color.Blue));
            }
        }

        return players;
    }

    String createContentFromList(List<Player> listOfPlayers) {
        StringBuilder builder = new StringBuilder();
        for (Player player : listOfPlayers) {
            builder.append(player.toString());
        }

        return builder.toString();
    }

    boolean checkIfPlayerExists(String playerName, List<Player> listOfPlayer) {
        if (listOfPlayer.isEmpty())
            return false;

        for (Player player : listOfPlayer) {
            if (player.toString().contains(playerName))
                return true;
        }

        return false;
    }
}
