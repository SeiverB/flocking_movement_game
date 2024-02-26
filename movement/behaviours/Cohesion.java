package movement.behaviours;

import java.util.ArrayList; // For arbitrary size lists 

import movement.util.*;


public class Cohesion implements Behaviour {

    public Kinematic centerOfMass;
    public ArrayList<? extends Kinematic> targets;
    public float threshold;
    private Arrive arrive;

    public Cohesion(Kinematic character, ArrayList<? extends Kinematic> targets, float maxAcceleration, float threshold){
        // Arrive(Kinematic character, Kinematic target, float maxAcceleration, float maxSpeed){
        this.centerOfMass = new Kinematic(new Vector2(0.0f, 0.0f), 0);
        this.targets = targets;
        this.threshold = threshold;
        this.updateCenterOfMass();
        
        this.arrive = new Arrive(character, this.centerOfMass, maxAcceleration);

    }

    // Updates the position variable in the kinematic that this.arrive uses as its target parameter. 
    private void updateCenterOfMass(){
        Vector2 result = new Vector2(0.0f, 0.0f);
        int flockSize = this.targets.size();
        int closeMembers = 0;

        if(flockSize == 0){
            this.centerOfMass.position = result;
            return;
        }

        // Add positions of flock together if within certain distance
        for(int i = 0; i < flockSize; i++){
            Vector2 currentPos = this.targets.get(i).position;
            if(currentPos.distance(this.arrive.character.position) < this.threshold){
                result = result.add(currentPos);
                closeMembers += 1;
            }
        }

        if(closeMembers == 0){
            this.centerOfMass.position = result;
            return;
        }

        // Divide by number of entities close to character to get center of mass
        result = result.scalar(1.0f / closeMembers);

        this.centerOfMass.position = result;

    }

    // Simply call arrive behaviour using calculated center of mass as target.
    @Override
    public SteeringOutput getSteering(){
        this.updateCenterOfMass();
        return this.arrive.getSteering();
    }    

}
