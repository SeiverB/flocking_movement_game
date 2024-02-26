package movement.behaviours;

import movement.util.*;

public class Flee implements Behaviour{
    public Kinematic character;
    public Kinematic target;
    public float maxAcceleration;

    public Flee(Kinematic character, Kinematic target, float maxAcceleration){
        this.character = character;
        this.target = target;
        this.maxAcceleration = maxAcceleration;
    }

    @Override
    public SteeringOutput getSteering(){
        Vector2 linear = character.position.subtract(target.position);

        //Fully accelerate along direction
        linear = linear.normalize().scalar(this.maxAcceleration);

        SteeringOutput result = new SteeringOutput(linear, 0.0f);

        return result;
    }

}
