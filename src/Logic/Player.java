package Logic;

public class Player {
    private String name;
    private int kills;

    public Player(String playerName, int playerKills) {
        name = playerName;
        kills = playerKills;
    }

    public Player(String playerName) {
        name = playerName;
        kills = 0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name.length() > 12)
            return;

        this.name = name;
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        if (kills < 0)
            return;

        this.kills = kills;
    }

    @Override
    public String toString() {
        return String.format("Player: %s \nKills: %d \n\n", name, kills);
    }
}
