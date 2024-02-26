package movement.util;

// Simple collision detector for preventing agents from leaving the screen
// XBound has min X value for its x attribute, and max X vlaue for its y attribute
// Likewise, YBound has min Y value for its x attribute, and max Y vlaue for its y attribute
public class WallCollisionDetector {

    public Vector2 XBounds;
    public Vector2 YBounds;

    public WallCollisionDetector(Vector2 XBounds, Vector2 YBounds){
        this.XBounds = XBounds;
        this.YBounds = YBounds;
    }

    public Vector2 getCollision(Vector2 position, float dist){

        float xtarg = position.x;
        float ytarg = position.y;


        if((position.x - dist) < this.XBounds.x){
            xtarg = this.XBounds.x + dist;
        }
        else if((position.x + dist) > this.XBounds.y){
            xtarg = this.XBounds.x - dist;
        }
        if((position.y - dist) < this.YBounds.x){
            ytarg = this.XBounds.y + dist;
        }
        else if((position.y + dist) > this.YBounds.y){
           ytarg = this.XBounds.y - dist;
        }

        Vector2 result = new Vector2(xtarg, ytarg);
        return result;
        
    }
}
