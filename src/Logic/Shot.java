package Logic;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Shot extends Rectangle {
    public boolean dead = false;
    public int speed = 20;
    public int direction;
    //type 0 current
    //type 1 other
    public int type;

    Shot(int x, int y, int w, int h, int direction, Color color, int type) {
        super(w, h, color);
        this.type = type;
        this.direction = direction;

        Hitbox box = new Hitbox(x, y, w, h, "bullet");

        setTranslateX(x);
        setTranslateY(y);

    }


    public void moveUp() {
        setTranslateY(getTranslateY() - speed);

    }
    public void moveRight() {
        setTranslateX(getTranslateX() + speed);
    }
    public void moveDown() {
        setTranslateY(getTranslateY() + speed);
    }
    public void moveLeft() {
        setTranslateX(getTranslateX() - speed);
    }
}
