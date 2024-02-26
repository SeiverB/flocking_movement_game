import movement.util.Vector2;
import java.awt.*;

public class Pen {

    public Vector2 minCoords;
    public Vector2 maxCoords;
    public Color color;
    public Boolean enabled = true;

    public Pen(Vector2 minCoords, Vector2 maxCoords, Color color){
        this.minCoords = minCoords;
        this.maxCoords = maxCoords;
        this.color = color;
    }

    public void drawPen(Graphics g){
        g.setColor(this.color);
        g.fillRect(Math.round(minCoords.x), Math.round(minCoords.y), Math.round(maxCoords.x - minCoords.x), Math.round(maxCoords.y - minCoords.y));
    }

    public boolean isInPen(Vector2 position){
        if(this.enabled && position.x >= minCoords.x && position.x <= maxCoords.x && position.y >= minCoords.y && position.y <= maxCoords.y){
            return true;
        }
        return false;
    }

}
