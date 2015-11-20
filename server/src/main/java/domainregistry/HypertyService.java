package domainregistry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

public class HypertyService{

  private Map<String, Map<String, HypertyInstance>> userServices = new HashMap<>();

  public Map<String, HypertyInstance> getAllHyperties(String userID) {
    return userServices.get(userID);
  }

  public String createUser(String userID){
    Map<String, HypertyInstance> services = new HashMap<>();
    if(!checkObjectExistance(userID)){
      userServices.put(userID, services);
      return userID + " created";
    }
    else return "user already created";
  }

  public String createUserHyperty(String userID, String hypertyID, HypertyInstance instance){
    if(checkObjectExistance(userID)){
      userServices.get(userID).put(hypertyID, instance);
      return hypertyID + " created";
    }
    else return "user not found";
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
    else return "user or data not found";
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
}
