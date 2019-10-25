package Logic;

import java.awt.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Hitbox extends Rectangle {

    String type;

    Hitbox(int x, int y, int w, int h, String type) {
        super(x, y, w, h);

        setTranslateX(x);
        setTranslateY(y);
    }

}
