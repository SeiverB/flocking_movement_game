package movement.behaviours;

import java.util.ArrayList;

import movement.util.*;

public class VelocityMatch implements Behaviour{
    private Kinematic character;
    private Kinematic target;
    private float maxAcceleration;
    private ArrayList<? extends Kinematic> targets;
    private boolean hasFlock = false;

    // the time over which target speed is achieved
    public static final float TIMETOTARGET = 0.6f;

    // Initializer for velocity matching to a single character
    public VelocityMatch(Kinematic character, Kinematic target, float maxAcceleration){
        this.character = character;
        this.target = target;
        this.maxAcceleration = maxAcceleration;
    }

    // Initializer for velocity matching to a flock of characters
    public VelocityMatch(Kinematic character, ArrayList<? extends Kinematic> targets, float maxAcceleration){
        this.character = character;
        this.target = new Kinematic(new Vector2(0.0f, 0.0f), 0);
        this.targets = targets;
        this.maxAcceleration = maxAcceleration;
        this.hasFlock = true;
    }

    @Override
    public SteeringOutput getSteering(){
        int flockSize = this.targets.size();
        if(flockSize == 0){
            return new SteeringOutput(new Vector2(0, 0), 0);
        }

        if(this.hasFlock){
            Vector2 resVelocity = new Vector2(0.0f, 0.0f);
            for(int i = 0; i < targets.size(); i++){
                Kinematic curTarget = this.targets.get(i);
                resVelocity = resVelocity.add(curTarget.velocity);
            }
            resVelocity = resVelocity.scalar(1.0f / this.targets.size());
            this.target.velocity = resVelocity;
        }


        // Acceleration attempts to get to target velocity
        Vector2 linear = (this.target.velocity.subtract(character.velocity)).scalar(1.0f / VelocityMatch.TIMETOTARGET);

        // Check if we are accelerating too fast
        if(linear.magnitude() > this.maxAcceleration){
            linear = (linear.normalize()).scalar(this.maxAcceleration);
        }
        float angular = 0;

        return new SteeringOutput(linear, angular);

    }
}
