/**
  * Copyright 2015-2016 INESC-ID
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  * http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
**/

package domainregistry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

public class HypertyService{
    static Logger log = Logger.getLogger(HypertyService.class.getName());

    private Map<String, Map<String, HypertyInstance>> userServices = new HashMap<>();

    public Map<String, HypertyInstance> getAllHyperties(String userID) {
        Map<String, HypertyInstance> services = getAllUserHyperties(userID);

        if(checkObjectExistance(userID) && !services.isEmpty())
            deleteExpiredHyperties(userID);

        if(checkObjectExistance(userID) && !services.isEmpty())
            return services;

        else if(!checkObjectExistance(userID))
            throw new UserNotFoundException();

        else throw new DataNotFoundException();
    }

    public void createUserHyperty(String userID, String hypertyID, HypertyInstance instance){
        if(checkObjectExistance(userID) && checkObjectExistance(userID, hypertyID)){
            HypertyInstance hyperty = userServices.get(userID).get(hypertyID);
            hyperty.setLastModified(Dates.getActualDate());
            hyperty.setDescriptor(instance.getDescriptor());
            userServices.get(userID).put(hypertyID, hyperty);
        }
        else if(checkObjectExistance(userID) && !checkObjectExistance(userID, hypertyID)){
            instance.setStartingTime(Dates.getActualDate());
            instance.setLastModified(Dates.getActualDate());
            userServices.get(userID).put(hypertyID, instance);
        }
        else{
            Map<String, HypertyInstance> services = new HashMap<>();
            instance.setStartingTime(Dates.getActualDate());
            instance.setLastModified(Dates.getActualDate());
            services.put(hypertyID, instance);
            userServices.put(userID, services);
        }
    }

    public void deleteUserHyperty(String userID, String hypertyID){
        if(checkObjectExistance(userID, hypertyID)){
            userServices.get(userID).remove(hypertyID);
            log.info("Deleted hyperty" + hypertyID + " from user " + userID);
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

    private Map<String, HypertyInstance> getAllUserHyperties(String userID){
        return userServices.get(userID);
    }

    protected void deleteExpiredHyperties(String userID){
        String actualDate = Dates.getActualDate();
        Map<String, HypertyInstance> userHyperties = getAllUserHyperties(userID);
        for (Map.Entry<String, HypertyInstance> entry : userHyperties.entrySet()){
            String lastModified = entry.getValue().getLastModified();
            int expires = entry.getValue().getExpires();
            if(Dates.dateCompare(actualDate, lastModified) > expires){
                deleteUserHyperty(userID, entry.getKey());
            }
        }
    }

    public Map<String, Map<String, HypertyInstance>> getServices(){
        return userServices;
    }
}
