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
    private static final String DEAD = "disconnected";

    private Map<String, Map<String, HypertyInstance>> userServices = new HashMap<>();
    private Map<String, DataObjectInstance> dataObjects = new HashMap<>();

    private Map<String, String> userByGuid = new HashMap<>();

    public Map<String,String> getMapUsersByGuid(){
      return userByGuid;
    }

    public int getNumUsersWithHyperties(){
      return userByGuid.size();
    }

    public Map<String, HypertyInstance> getHypertiesByGuid(String guid){
        if(guidExists(guid)){
            String userId = getUserByGuid(guid);
            return getUserHyperties(userId);
        }

        else return Collections.emptyMap();
    }

    public String getUserByGuid(String guid){
       return userByGuid.get(guid);
    }

    public boolean guidExists(String guid){
        return userByGuid.containsKey(guid);
    }

    public Map<String, HypertyInstance> getUserHyperties(String userID) {
        log.info("Received request for " + userID + " hyperties");

        Map<String, HypertyInstance> hyperties = userServices.get(userID);

        if(hyperties == null) return Collections.emptyMap();

        return hyperties;
    }

    public boolean userExists(String userID){
        return userServices.containsKey(userID);
    }

    public void deleteUserHyperty(String hypertyID){
        Set<String> allUsersIds = userServices.keySet();
        List<String> users = new ArrayList<String>(allUsersIds);

        for (String userID : users) {
            if(userServices.get(userID).keySet().contains(hypertyID)){
                HypertyInstance hyperty = userServices.get(userID).get(hypertyID);

                String oldStatus = hyperty.getStatus();
                hyperty.setStatus(DEAD);
                String newStatus = hyperty.getStatus();

                userServices.get(userID).keySet().remove(hypertyID);
                userServices.get(userID).put(hypertyID, hyperty);

                log.info("Changed hyperty " + hypertyID + " status from " + oldStatus + " to " + newStatus);
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
            userByGuid.put(hyperty.getGuid(), hyperty.getUserID());
            log.info("Inserted hyperty with ID " + hyperty.getHypertyID());
            return;
        }
        Map<String, HypertyInstance> services = new HashMap<>();
        services.put(hyperty.getHypertyID(), hyperty);
        userServices.put(user, services);
        userByGuid.put(hyperty.getGuid(), hyperty.getUserID());
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

    public void insertDataObject(DataObjectInstance dataObject){
        String dataObjectUrl = dataObject.getUrl();
        dataObjects.put(dataObjectUrl, dataObject);
    }

    public boolean dataObjectExists(String dataObjectUrl){
        return dataObjects.containsKey(dataObjectUrl);
    }

    public DataObjectInstance getDataObjectByUrl(String dataObjectUrl){
        return dataObjects.get(dataObjectUrl);
    }

    public Map<String, DataObjectInstance> getDataObjectsByHyperty(String hypertyReporter){
        Map<String, DataObjectInstance> dataObjectsToBeReturned = new HashMap<>();

        for(DataObjectInstance dataObjectInstance : dataObjects.values()){
            if(dataObjectInstance.getReporter().equals(hypertyReporter)){
                dataObjectsToBeReturned.put(dataObjectInstance.getUrl(), dataObjectInstance);
            }
        }

        if(dataObjectsToBeReturned.isEmpty())
            return Collections.emptyMap();

        else return dataObjectsToBeReturned;
    }

    public Map<String, DataObjectInstance> getDataObjectsByName(String dataObjectName){
        Map<String, DataObjectInstance> dataObjectsToBeReturned = new HashMap<>();

        for(DataObjectInstance dataObjectInstance : dataObjects.values()){
            if(dataObjectInstance.getName().equals(dataObjectName)){
                dataObjectsToBeReturned.put(dataObjectInstance.getUrl(), dataObjectInstance);
            }
        }

        if(dataObjectsToBeReturned.isEmpty())
            return Collections.emptyMap();

        else return dataObjectsToBeReturned;
    }

    public void deleteDataObject(String dataObjectUrl){
        DataObjectInstance dataObject = dataObjects.get(dataObjectUrl);

        dataObjects.remove(dataObjectUrl);
        dataObject.setStatus(DEAD);
        dataObjects.put(dataObjectUrl, dataObject);
    }

    public ArrayList<String> getAllDataObjects(){
        return new ArrayList<String>(dataObjects.keySet());
    }

    public Map<String, DataObjectInstance> getDataObjects(){
        return dataObjects;
    }
}
