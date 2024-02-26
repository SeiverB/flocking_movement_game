package movement.behaviours;

import movement.util.*;

public class Seek implements Behaviour{
    public Kinematic character;
    public Kinematic target;
    public float maxAcceleration;

    public Seek(Kinematic character, Kinematic target, float maxAcceleration){
        this.character = character;
        this.target = target;
        this.maxAcceleration = maxAcceleration;
    }

    @Override
    public SteeringOutput getSteering(){
        Vector2 linear = target.position.subtract(character.position);

        //Fully accelerate along direction
        linear = linear.normalize().scalar(this.maxAcceleration);

        SteeringOutput result = new SteeringOutput(linear, 0.0f);

        return result;
    }

}
