package Logic;

import javafx.scene.image.Image;

public class Player {
    private static int width = 100;
    private static int height = 100;
    private String name;
    private int kills;
    private int xCord;
    private int yCord;
    Color color;
    int speed = 10;
    double shotCooldownTimer = 0;
    Direction direction;
    Hitbox hitbox;

    public Player(String playerName, int playerKills, Color color) {
        hitbox = new Hitbox(0, 0, width, height, playerName);
        name = playerName;
        kills = playerKills;
        this.color = color;
        direction = Direction.NORTH;
    }

    public Player(String playerName, Color color) {
        hitbox = new Hitbox(0, 0, width, height, playerName);
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

    void setKills(int kills) {
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

    void setxCord(int xCord) {
        this.xCord = xCord;
        hitbox.setX(xCord);
    }

    public int getyCord() {
        return yCord;
    }

    void setyCord(int yCord) {
        this.yCord = yCord;
        hitbox.setY(yCord);
    }

    public Image getPlayerImage() {
        String playerImage = "images/" + color.toString();

        if (direction == Direction.NORTH) {
            playerImage += "North";
        } else if (direction == Direction.EAST) {
            playerImage += "East";
        } else if (direction == Direction.SOUTH) {
            playerImage += "South";
        } else if (direction == Direction.WEST) {
            playerImage += "West";
        }
        playerImage += ".png";
        return new Image(playerImage);
    }
}
