package movement.behaviours;

import movement.util.*;

public class Arrive implements Behaviour{
    public Kinematic character;
    public Kinematic target;
    public float maxAcceleration;

    // radius for arriving at target
    public static final float TARGETRADIUS = 8;

    // radius for beginning to slow down
    public static final float SLOWRADIUS = 60;

    // Time over which we achieve target speed
    public static final float TIMETOTARGET = 0.2f;
    
    public Arrive(Kinematic character, Kinematic target, float maxAcceleration){
        this.character = character;
        this.target = target;
        this.maxAcceleration = maxAcceleration;
    }

    @Override
    public SteeringOutput getSteering(){
        Vector2 direction = target.position.subtract(this.character.position);
        float distance = direction.magnitude();

        // If within target's radius, we have arrived. Return no steering.
        if(distance < Arrive.TARGETRADIUS){
            new SteeringOutput(new Vector2(0, 0), 0);
        }

        float targetSpeed;
        
        // If outside slowradius, then move at max speed.
        if(distance > Arrive.SLOWRADIUS){
            targetSpeed = this.character.maxSpeed;
        }
        else{
            targetSpeed = this.character.maxSpeed * (distance / Arrive.SLOWRADIUS);
        }

        // Now combine speed, and direction to get targetVelocity
        Vector2 targetVelocity = direction.normalize().scalar(targetSpeed);

        // Acceleration now attempts to approach target velocity
        Vector2 linear = (targetVelocity.subtract(character.velocity)).scalar(1 / Arrive.TIMETOTARGET);

        if(linear.magnitude() > this.maxAcceleration){
            linear = linear.normalize().scalar(maxAcceleration);
        }
        float angular = 0; 

        SteeringOutput result = new SteeringOutput(linear, angular);

        return result;
    }

}
