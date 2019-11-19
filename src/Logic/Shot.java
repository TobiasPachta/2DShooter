package Logic;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Shot extends Rectangle {
    private static int width = 10;
    private static int height = 10;
    private static Color color = Color.YELLOW;
    public boolean dead = false;
    private int speed = 20;
    public Direction direction;
    Hitbox hitbox;
    //type 0 current
    //type 1 other
    int type;

    Shot(int x, int y, Direction direction, int type) {
        super(width, height, color);
        this.type = type;
        this.direction = direction;

        hitbox = new Hitbox(x, y, width, height, "bullet");

        setTranslateX(x);
        setTranslateY(y);
    }

    public void moveUp() {
        setTranslateY(getTranslateY() - speed);
        hitbox.setY(getTranslateY() - speed);
    }

    public void moveRight() {
        setTranslateX(getTranslateX() + speed);
        hitbox.setX(getTranslateX() + speed);
    }

    public void moveDown() {
        setTranslateY(getTranslateY() + speed);
        hitbox.setY(getTranslateY() + speed);
    }

    public void moveLeft() {
        setTranslateX(getTranslateX() - speed);
        hitbox.setX(getTranslateX() - speed);
    }
}
