package Logic;

import java.awt.*;
import javafx.scene.shape.Rectangle;

public class Player extends Rectangle {
    private String name;
    private int kills;
    private int xCord;
    private int yCord;
    public int speed = 10;
    public boolean hasShot;
    public boolean isMoving;
    public double shotCooldownTimer = 0;

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

    public int getxCord() {
        return xCord;
    }

    public void setxCord(int xCord) {
        this.xCord = xCord;
    }

    public int getyCord() {
        return yCord;
    }

    public void setyCord(int yCord) {
        this.yCord = yCord;
    }

}
