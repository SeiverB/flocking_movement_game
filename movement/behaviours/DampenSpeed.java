package movement.behaviours;

import movement.util.*;

public class DampenSpeed implements Behaviour{
    public Kinematic character;
    public float maxSpeed, maxAcceleration;

    // When velocity exceeds maxSpeed, dampen
    public DampenSpeed(Kinematic character, float maxAcceleration, float maxSpeed){
        this.character = character;
        this.maxSpeed = maxSpeed;
        this.maxAcceleration = maxAcceleration;
    }

    @Override
    public SteeringOutput getSteering(){
        float speed = this.character.velocity.magnitude();
        Vector2 linear = new Vector2(0.0f, 0.0f);

        if(speed > maxSpeed){
            linear = this.character.velocity.normalize().scalar(-maxAcceleration);
        }

        SteeringOutput result = new SteeringOutput(linear, 0.0f);

        return result;
    }

}
