package domainregistry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

public class HypertyService{

    private Map<String, Map<String, HypertyInstance>> userServices = new HashMap<>();

    public Map<String, HypertyInstance> getAllHyperties(String userID) {
        if(checkObjectExistance(userID)){
            return userServices.get(userID);
        }
        else return null;
    }

    public String createUser(String userID){
        Map<String, HypertyInstance> services = new HashMap<>();
        if(!checkObjectExistance(userID)){
            userServices.put(userID, services);
            return userID;
        }
        else return null;
    }

    public String createUserHyperty(String userID, String hypertyID, HypertyInstance instance){
        if(checkObjectExistance(userID)){
            userServices.get(userID).put(hypertyID, instance);
            return hypertyID;
        }
        else return null;
    }

    public HypertyInstance getUserHyperty(String userID, String hypertyID){
        if(checkObjectExistance(userID, hypertyID)){
            return userServices.get(userID).get(hypertyID);
        }
        else return null;
    }

    public String deleteUserHyperty(String userID, String hypertyID){
        if(checkObjectExistance(userID, hypertyID)){
            userServices.get(userID).remove(hypertyID);
            return hypertyID + " deleted";
        }
        else return null;
    }

    private boolean checkObjectExistance(String... params){
        int numberOfArguments = params.length;
        if(numberOfArguments == 1){
            return userServices.containsKey(params[0]);
        }
        else{
            return userServices.containsKey(params[0]) &&
                userServices.get(params[0]).containsKey(params[1]);
        }
    }

    public Map<String, Map<String, HypertyInstance>> getServices(){
        return userServices;
    }
}
