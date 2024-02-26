package movement.util;

import movement.behaviours.SteeringOutput;

public class Kinematic {

    public Vector2 position;
    public Vector2 velocity;
    public float orientation;
    public float rotation;
    public float maxSpeed;

    public Kinematic(Vector2 position, float maxSpeed){
        this.position = new Vector2(position.x, position.y);
        this.velocity = new Vector2(0, 0);
        this.orientation = 0;
        this.rotation = 0;
        this.maxSpeed = maxSpeed;
    }

    // Newton-Euler-1 integration update
    public void update(SteeringOutput steering, float time){
        
        // Update position & orientation 
        this.position = this.position.add(velocity.scalar(time));
        this.orientation += this.rotation * time;

        // Update velocity and rotation
        this.velocity = this.velocity.add(steering.linear.scalar(time));
        this.rotation += steering.angular * time;
        
        // Check if maximum speed is extended
        if(this.velocity.magnitude() > this.maxSpeed){
            this.velocity = this.velocity.normalize().scalar(this.maxSpeed);
        }

    }

}
