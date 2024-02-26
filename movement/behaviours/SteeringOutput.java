package movement.behaviours;

import movement.util.Vector2;

public class SteeringOutput {
    public Vector2 linear;
    public float angular;

    public SteeringOutput(Vector2 linear, float angular){
        this.linear = new Vector2(linear.x, linear.y);
        this.angular = angular;
    }

    public SteeringOutput(){
        this.linear = new Vector2(0.0f, 0.0f);
        this.angular = 0.0f;
    }
    
}
