package Logic;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Shot extends Rectangle {
    public boolean dead = false;
    int speed = 20;
    public int direction;


    Shot(int x, int y, int w, int h, int direction, Color color) {
        super(w, h, color);

        this.direction = direction;

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
