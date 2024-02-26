import movement.util.Kinematic;
import movement.behaviours.Behaviour;
import movement.behaviours.SteeringOutput;
import movement.util.Vector2;
import java.awt.*;
import java.awt.image.BufferedImage;


public class Character extends Kinematic{
    
    private Behaviour behaviour;
    private BufferedImage image;
    public int facing = 1;

    public Character(Vector2 position, float maxSpeed, BufferedImage image){
        super(position, maxSpeed);
        this.image = image;
    }

    public void setBehaviour(Behaviour behaviour){
        this.behaviour = behaviour;
    }

    public Behaviour getBehaviour(){
        return this.behaviour;
    }

    public void updateCharacter(float delay){
        SteeringOutput newsteer = this.behaviour.getSteering();
        this.update(newsteer, delay);
    }

    private void updateCharacterOrientation(){
        if(this.velocity.x > 0){
            this.facing = -1;
        }
        else if(this.velocity.x < 0){
            this.facing = 1;
        }
    }

    public void drawCharacter(Graphics g){
        updateCharacterOrientation();
        Vector2 dim = new Vector2(this.image.getWidth() * this.facing, this.image.getHeight());
        Vector2 pos = this.position.subtract(dim.scalar(1.0f/2.0f));

        g.drawImage(this.image, Math.round(pos.x), Math.round(pos.y), (int)dim.x, (int)dim.y, null);
    }

    
}
