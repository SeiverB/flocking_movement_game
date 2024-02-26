package movement.behaviours;

import movement.util.*;

public class WallAvoidance implements Behaviour{
    
    public WallCollisionDetector detector;

    public Kinematic character;
    public Kinematic target;
    public float maxAcceleration;
    public float avoidDistance;
    public float lookAhead;

    private Seek seek;

    public WallAvoidance(Kinematic character, WallCollisionDetector detector, float maxAcceleration, float lookAhead){
        this.character = character;
        this.detector = detector;
        this.maxAcceleration = maxAcceleration;
        this.lookAhead = lookAhead;

        this.target = new Kinematic(new Vector2(0.0f, 0.0f), 0);
        this.seek = new Seek(this.character, this.target, this.maxAcceleration);
        
        updateTarget();
    }

    @Override
    public SteeringOutput getSteering(){
        updateTarget();
        return this.seek.getSteering();
    }

    private void updateTarget(){   
        Vector2 newPos = this.detector.getCollision(this.character.position, this.lookAhead);
        this.target.position = newPos;

    }

}