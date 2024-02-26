package movement.behaviours;

import java.util.ArrayList; // For arbitrary size lists 

import movement.util.*;

public class Separation implements Behaviour{
    public Kinematic character;
    public float maxAcceleration;

    public ArrayList<? extends Kinematic> targets;

    public float threshold;

    public float decayCoefficient;
    

    public Separation(Kinematic character, ArrayList<? extends Kinematic> targets, float maxAcceleration, float threshold, float decayCoefficient){
        this.character = character;
        this.targets = targets;
        this.maxAcceleration = maxAcceleration;
        this.threshold = threshold;
        this.decayCoefficient = decayCoefficient;        
    }

    @Override
    public SteeringOutput getSteering(){
        
        Vector2 linear = new Vector2(0.0f, 0.0f);

        for(int i = 0; i < this.targets.size(); i++){
            
            Kinematic currentKin = this.targets.get(i);

            // Ensure that kinematic being tested isn't the local character's kinematic (that which the force is being applied to)
            if(currentKin.hashCode() == this.character.hashCode()){
                continue;
            }

            Vector2 direction = (this.character.position).subtract(currentKin.position);
            float distance = direction.magnitude();

            if(distance < threshold){
                
                // inverse square separation
                //float strength = Math.min((this.decayCoefficient / (distance * distance)), this.maxAcceleration);
                
                // linear separation
                float strength = this.maxAcceleration * ((this.threshold - distance) / this.threshold);

                direction = direction.normalize();
                linear = linear.add(direction.normalize().scalar(strength));
            
            }
        }
        return new SteeringOutput(linear, 0.0f);
    }
}