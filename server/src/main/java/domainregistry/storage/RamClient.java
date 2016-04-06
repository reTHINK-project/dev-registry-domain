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

import java.util.*;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class RamClient implements Connection{
    static Logger log = Logger.getLogger(RamClient.class.getName());

    private Map<String, Map<String, HypertyInstance>> userServices = new HashMap<>();

    public Map<String, HypertyInstance> getUserHyperties(String userID) {
        log.info("Received request for " + userID + " hyperties");
        return userServices.get(userID);
    }

    public boolean userExists(String userID){
        return userServices.containsKey(userID);
    }

    public void deleteUserHyperty(String hypertyID){
        for (String userID : userServices.keySet()) {
            if(userServices.get(userID).keySet().contains(hypertyID)){
                userServices.get(userID).keySet().remove(hypertyID);
                log.info("Removed hyperty " + hypertyID + " from " + userID);
            }
            if(userServices.get(userID).keySet().isEmpty()){
                userServices.remove(userID);
            }
        }
    }

    public boolean hypertyExists(String hypertyID){
        for (String userID : userServices.keySet()) {
            if(userServices.get(userID).keySet().contains(hypertyID)){
                return true;
            }
        }
        return false;
    }

    public void insertHyperty(HypertyInstance hyperty){
        String user = hyperty.getUserID();
        if(userExists(user)){
            userServices.get(user).put(hyperty.getHypertyID(), hyperty);
            log.info("Inserted hyperty with ID " + hyperty.getHypertyID());
            return;
        }
        Map<String, HypertyInstance> services = new HashMap<>();
        services.put(hyperty.getHypertyID(), hyperty);
        userServices.put(user, services);
        log.info("Created user " + user + " and hyperty " + hyperty.getHypertyID());
    }

    public void updateHyperty(HypertyInstance newHyperty){
        userServices.get(newHyperty.getUserID()).put(newHyperty.getHypertyID(), newHyperty);
        log.info("Updated hyperty " + newHyperty.getHypertyID() + " from user " + newHyperty.getUserID());
    }

    public ArrayList<String> getAllUsers(){
        return new ArrayList<String>(userServices.keySet());
    }

    public HypertyInstance getHyperty(String hypertyID){
        for (String userID : userServices.keySet()) {
            if(userServices.get(userID).keySet().contains(hypertyID)){
                return userServices.get(userID).get(hypertyID);
            }
        }
        return null;
    }

    public int getNumberOfHyperties(){
        int numHyperties = 0;

        for (String userID : userServices.keySet()) {
            numHyperties += userServices.get(userID).keySet().size();
        }
        return numHyperties;
    }
}
