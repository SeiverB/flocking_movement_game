package movement.behaviours;
import java.util.ArrayList; // For arbitrary size lists 
import movement.util.*;

public class BlendedSteering implements Behaviour{


    private class BehaviourAndWeight {
        public Behaviour steeringBehaviour;
        public float weight;

        public BehaviourAndWeight(Behaviour steeringBehaviour, float weight){
            this.steeringBehaviour = steeringBehaviour;
            this.weight = weight;
        }

    }

    private ArrayList<BehaviourAndWeight> behaviours = new ArrayList<BehaviourAndWeight>();
    public float maxAcceleration;
    public float maxRotation;

    public BlendedSteering(float maxAcceleration, float maxRotation){
        this.maxAcceleration = maxAcceleration;
        this.maxRotation = maxRotation;
    }

    public SteeringOutput getSteering(){

        if(behaviours.size() == 0){
            return new SteeringOutput();
        }

        Vector2 resultLinear = new Vector2(0.0f, 0.0f);
        float resultAngular = 0.0f;

        for(int i = 0; i < this.behaviours.size(); i++){
            BehaviourAndWeight currentBNW = this.behaviours.get(i); 
            SteeringOutput currentSO = currentBNW.steeringBehaviour.getSteering();
            float currentWeight = currentBNW.weight;

            resultLinear = resultLinear.add(currentSO.linear.scalar(currentWeight));
            resultAngular += (currentSO.angular * currentWeight);

        }

        // Clamp results according to maxAccel/maxRotat
        resultLinear = resultLinear.scalarMax(this.maxAcceleration);
        resultAngular = Math.max(resultAngular, this.maxRotation);

        return new SteeringOutput(resultLinear, resultAngular);

    }

    public void addBehaviour(Behaviour behaviour, float weight){
        this.behaviours.add(new BehaviourAndWeight(behaviour, weight));
    }

    public float getBehaviourWeight(int index){
        return this.behaviours.get(index).weight;
    }

    public void setBehaviourWeight(int index, float weight){
        this.behaviours.get(index).weight = weight;
    }

    public Behaviour getBehaviour(int index){
        return this.behaviours.get(index).steeringBehaviour;
    }

}