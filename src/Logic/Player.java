package Logic;

import javafx.scene.image.Image;
import javafx.scene.shape.Rectangle;

public class Player extends Rectangle {
    private String name;
    private int kills;
    private int xCord;
    private int yCord;
    public Color color;
    public int speed = 10;
    public double shotCooldownTimer = 0;
    public Direction direction;

    public Player(String playerName, int playerKills, Color color) {
        name = playerName;
        kills = playerKills;
        this.color = color;
        direction = Direction.NORTH;
    }

    public Player(String playerName, Color color) {
        name = playerName;
        kills = 0;
        this.color = color;
        direction = Direction.NORTH;
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

    public Image getPlayerImage() {
        String playerImage = "images/"+color.toString();

        if (direction == Direction.NORTH) {
            playerImage+="North";
        }else if (direction == Direction.EAST) {
            playerImage+="East";
        }else if (direction == Direction.SOUTH) {
            playerImage+="South";
        }else if (direction == Direction.WEST) {
            playerImage+="West";
        }
        playerImage+=".png";
        return new Image(playerImage);
    }
}
