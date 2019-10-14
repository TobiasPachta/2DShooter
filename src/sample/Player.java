package sample;

public class Player {
    private String name;
    private int kills;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if(name.length()>12)
            return;
        this.name = name;
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        if(kills < 0)
            return;
        this.kills = kills;
    }

    @Override
    public String toString() {
        return String.format("Player: %s \n Kills: %d \n\n",name,kills);
    }
}
