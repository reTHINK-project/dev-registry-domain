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
    private Map<String, ArrayList<HypertyInstance>> hypertiesByEmail = new HashMap<>();

    private Map<String, String> userByGuid = new HashMap<>();
    private Map<String, HypertyInstance> updatedHyperties = new HashMap<>();
    private Map<String, DataObjectInstance> updatedDataObjects = new HashMap<>();

    public Map<String, HypertyInstance> getUpdatedHypertiesMap(){
        if(updatedHyperties == null) return Collections.emptyMap();

        return updatedHyperties;
    }

    public Map<String, DataObjectInstance> getUpdatedDataObjectsMap(){
        return updatedDataObjects;
    }

    public void clearUpdatedHypertiesMap(){
        updatedHyperties = new HashMap<>();
    }

    public void clearUpdatedDataObjectsMap(){
        updatedDataObjects = new HashMap<>();
    }

    public ArrayList<HypertyInstance> getHypertiesByEmail(String email){
        if(emailExists(email)){
            return hypertiesByEmail.get(email);
        }

        else return new ArrayList();
    }

    private boolean emailExists(String email){
        return hypertiesByEmail.containsKey(email);
    }

    private void associateHypertyWithEmail(String email, HypertyInstance hyperty){
        if(emailExists(email)){
            ArrayList<HypertyInstance> hyperties = hypertiesByEmail.get(email);
            for(HypertyInstance data : hyperties){
                if(data.getHypertyID().equals(hyperty.getHypertyID())){
                    hyperties.remove(data);
                    break;
                }
            }
            hyperties.add(hyperty);
        }

        else {
            ArrayList<HypertyInstance> hyperties = new ArrayList<HypertyInstance>();
            hyperties.add(hyperty);
            hypertiesByEmail.put(email, hyperties);
        }
    }

    private boolean emailHasHyperty(String email, HypertyInstance hyperty){
        return hypertiesByEmail.get(email).contains(hyperty);
    }

    public ArrayList<HypertyInstance> getHypertiesByEmail(String email){
        if(emailExists(email)){
            return hypertiesByEmail.get(email);
        }

        else return new ArrayList();
    }

    private boolean emailExists(String email){
        return hypertiesByEmail.containsKey(email);
    }

    private void associateHypertyWithEmail(String email, HypertyInstance hyperty){
        if(emailExists(email)){
            ArrayList<HypertyInstance> hyperties = hypertiesByEmail.get(email);
            for(HypertyInstance data : hyperties){
                if(data.getHypertyID().equals(hyperty.getHypertyID())){
                    hyperties.remove(data);
                    break;
                }
            }
            hyperties.add(hyperty);
        }

        else {
            ArrayList<HypertyInstance> hyperties = new ArrayList<HypertyInstance>();
            hyperties.add(hyperty);
            hypertiesByEmail.put(email, hyperties);
        }
    }

    private boolean emailHasHyperty(String email, HypertyInstance hyperty){
        return hypertiesByEmail.get(email).contains(hyperty);
    }

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
                if(!oldStatus.equals(DEAD))
                    updatedHyperties.put(hyperty.getHypertyID(), hyperty);

                hyperty.setStatus(DEAD);
                String newStatus = hyperty.getStatus();

                userServices.get(userID).keySet().remove(hypertyID);
                userServices.get(userID).put(hypertyID, hyperty);
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

    private String getUserEmail(String userID){
        String[] userIdSplitted = userID.split("/");
        return userIdSplitted[userIdSplitted.length - 1];
    }

    public void insertHyperty(HypertyInstance hyperty){
        String user = hyperty.getUserID();
        if(userExists(user)){
            userServices.get(user).put(hyperty.getHypertyID(), hyperty);
            userByGuid.put(hyperty.getGuid(), hyperty.getUserID());
            associateHypertyWithEmail(getUserEmail(user), hyperty);
            return;
        }

        Map<String, HypertyInstance> services = new HashMap<>();
        services.put(hyperty.getHypertyID(), hyperty);
        userServices.put(user, services);
        userByGuid.put(hyperty.getGuid(), hyperty.getUserID());
        associateHypertyWithEmail(getUserEmail(user), hyperty);
    }

    public void updateHyperty(HypertyInstance newHyperty){
        userServices.get(newHyperty.getUserID()).put(newHyperty.getHypertyID(), newHyperty);
        associateHypertyWithEmail(getUserEmail(newHyperty.getUserID()), newHyperty);
        checkUpdatedHyperties(newHyperty);
    }

    public void checkUpdatedHyperties(HypertyInstance hyperty){
        if(updatedHyperties.containsKey(hyperty.getHypertyID()))
            updatedHyperties.remove(hyperty.getHypertyID());
    }

    public void checkUpdatedDataObjects(DataObjectInstance dataObject){
        if(updatedDataObjects.containsKey(dataObject.getUrl()))
            updatedDataObjects.remove(dataObject.getUrl());
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
        checkUpdatedDataObjects(dataObject);
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

        String oldStatus = dataObject.getStatus();

        if(!oldStatus.equals(DEAD))
          updatedDataObjects.put(dataObject.getUrl(), dataObject);

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
