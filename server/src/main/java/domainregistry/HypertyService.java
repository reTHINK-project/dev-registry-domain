package domainregistry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Date;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class HypertyService{

    private Map<String, Map<String, HypertyInstance>> userServices = new HashMap<>();

    public Map<String, HypertyInstance> getAllHyperties(String userID) {
      Map<String, HypertyInstance> services = userServices.get(userID);
      if(checkObjectExistance(userID) && !services.isEmpty())
        return services;

      else if(!checkObjectExistance(userID))
        throw new UserNotFoundException();

      else throw new DataNotFoundException();
    }

    public String createUserHyperty(String userID, String hypertyID, HypertyInstance instance){
        if(checkObjectExistance(userID) && checkObjectExistance(userID, hypertyID)){
            HypertyInstance hyperty = userServices.get(userID).get(hypertyID);
            hyperty.setLastModified(getActualDate());
            hyperty.setDescriptor(instance.getDescriptor());
            userServices.get(userID).put(hypertyID, hyperty);
        }
        else if(checkObjectExistance(userID) && !checkObjectExistance(userID, hypertyID)){
            instance.setStartingTime(getActualDate());
            instance.setLastModified(getActualDate());
            userServices.get(userID).put(hypertyID, instance);
        }
        else{
          Map<String, HypertyInstance> services = new HashMap<>();
          instance.setStartingTime(getActualDate());
          instance.setLastModified(getActualDate());
          services.put(hypertyID, instance);
          userServices.put(userID, services);
        }

        return hypertyID;
    }

    public HypertyInstance getUserHyperty(String userID, String hypertyID){
        if(checkObjectExistance(userID, hypertyID)){
            return userServices.get(userID).get(hypertyID);
        }

        else if(!checkObjectExistance(userID))
            throw new UserNotFoundException();

        else throw new DataNotFoundException();
    }

    public String deleteUserHyperty(String userID, String hypertyID){
        if(checkObjectExistance(userID, hypertyID)){
            userServices.get(userID).remove(hypertyID);
            return hypertyID;
        }

        else if(!checkObjectExistance(userID))
            throw new UserNotFoundException();

        else throw new DataNotFoundException();
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

    private String getActualDate() {
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        return sdf.format(date);
    }

    public Map<String, Map<String, HypertyInstance>> getServices(){
        return userServices;
    }
}
