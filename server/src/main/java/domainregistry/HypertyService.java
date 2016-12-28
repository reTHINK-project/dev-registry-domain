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
import java.util.concurrent.ConcurrentHashMap;
import org.apache.log4j.Logger;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.json.JSONArray;
import org.json.JSONObject;
import org.apache.commons.lang3.ArrayUtils;

public class HypertyService{
    static Logger log = Logger.getLogger(HypertyService.class.getName());
    private static final String EXPIRES = "EXPIRES";
    private static final String DEAD = "disconnected";
    private static final String LIVE = "live";

    public Map<String, HypertyInstance> getAllHyperties(Connection connectionClient, String userID) {
        Map<String, HypertyInstance> allUserHyperties = connectionClient.getUserHyperties(userID);

        if(connectionClient.userExists(userID)){
            deleteExpiredHyperties(connectionClient, userID);
        }

        Map<String, HypertyInstance> hypertiesWithStatusUpdated = connectionClient.getUserHyperties(userID);

        if(connectionClient.userExists(userID) && allHypertiesAreUnavailable(hypertiesWithStatusUpdated)){
            return hypertiesWithStatusUpdated;
        }

        if(connectionClient.userExists(userID) && !allUserHyperties.isEmpty()){
            return liveHyperties(hypertiesWithStatusUpdated);
        }

        else throw new UserNotFoundException();
    }

    public Map<String, HypertyInstance> getHypertiesByGuid(Connection connectionClient, String guid){
        if(!connectionClient.guidExists(guid)){
            throw new UserNotFoundException();
        }

        Map<String, HypertyInstance> returnedHyperties = connectionClient.getHypertiesByGuid(guid);

        if(returnedHyperties.isEmpty())
            throw new DataNotFoundException();

        String userID = connectionClient.getUserByGuid(guid);
        deleteExpiredHyperties(connectionClient, userID);
        Map<String, HypertyInstance> hypertiesWithStatusUpdated = connectionClient.getUserHyperties(userID);

        if(allHypertiesAreUnavailable(hypertiesWithStatusUpdated)){
            return hypertiesWithStatusUpdated;
        }

        return liveHyperties(hypertiesWithStatusUpdated);
    }

    // Status page shows all hyperties independent of the their status
    public Map<String, HypertyInstance> getHypertiesForStatusPage(Connection connectionClient, String guid){
        String userID = connectionClient.getUserByGuid(guid);
        deleteExpiredHyperties(connectionClient, userID);
        return connectionClient.getUserHyperties(userID);
    }

    public HypertyInstance getHypertyByUrl(Connection connectionClient, String hypertyUrl){
        if(!connectionClient.hypertyExists(hypertyUrl)){
            throw new DataNotFoundException();
        }

        HypertyInstance hypertyFound = connectionClient.getHyperty(hypertyUrl);

        String actualDate = Dates.getActualDate();
        String lastModified = hypertyFound.getLastModified();

        int expires = hypertyFound.getExpires();

        if(Dates.dateCompare(actualDate, lastModified) > expires){
            connectionClient.deleteUserHyperty(hypertyUrl);
        }

        return connectionClient.getHyperty(hypertyUrl);
    }

    private Map<String, HypertyInstance> liveHyperties(Map<String, HypertyInstance> hyperties){
        Map<String, HypertyInstance> hypertiesToBeReturned = new HashMap();

        for (Map.Entry<String, HypertyInstance> entry : hyperties.entrySet()){
            String status = entry.getValue().getStatus();
            if(!status.equals(DEAD)){
                hypertiesToBeReturned.put(entry.getKey(), entry.getValue());
            }
        }

        return hypertiesToBeReturned;
    }

    public void updateHypertyFields(Connection connectionClient, HypertyInstance updatedHyperty){
        Gson gson = new Gson();
        String hypertyID = updatedHyperty.getHypertyID();

        if(!connectionClient.hypertyExists(hypertyID))
            throw new CouldNotCreateOrUpdateHypertyException();

        HypertyInstance oldHyperty = connectionClient.getHyperty(hypertyID);

        String oldHypertyJson = gson.toJson(oldHyperty);
        String updatedHypertyJson = gson.toJson(updatedHyperty);

        log.info("FIELDS TO PERFORM THE UPDATE " + updatedHypertyJson);

        String resultJson = JsonHelper.mergeJsons(updatedHypertyJson, oldHypertyJson);

        log.info("RESULT JSON: " + resultJson);

        updateHyperty(connectionClient, gson.fromJson(resultJson, HypertyInstance.class));
    }

    public void createUserHyperty(Connection connectionClient, HypertyInstance newHyperty){
        long expiresLimit = Long.valueOf(System.getenv(EXPIRES)).longValue();
        String userID = newHyperty.getUserID();
        String hypertyID = newHyperty.getHypertyID();

        if(validateExpiresField(newHyperty.getExpires(), expiresLimit)){
            newHyperty.setExpires((int) expiresLimit);
            log.info("Expires was set to the max value allowed by the Domain Registry: " + expiresLimit);
        }

        if(connectionClient.userExists(userID)){
            checkHypertyExistence(connectionClient, newHyperty);
            return;
        }

        if(connectionClient.hypertyExists(hypertyID))
            throw new CouldNotCreateOrUpdateHypertyException();

        else newHyperty(connectionClient, newHyperty);
    }

    public void keepAlive(Connection connectionClient, String hypertyID){
        if(!connectionClient.hypertyExists(hypertyID))
            throw new DataNotFoundException();

        log.info("Keep alive hyperty: " + hypertyID);

        HypertyInstance hyperty = connectionClient.getHyperty(hypertyID);
        hyperty.setLastModified(Dates.getActualDate());
        hyperty.setHypertyID(hypertyID);
        hyperty.setStatus(LIVE);
        connectionClient.updateHyperty(hyperty);
    }

    public void deleteUserHyperty(Connection connectionClient, String userID, String hypertyID){
        if(!connectionClient.userExists(userID))
            throw new UserNotFoundException();

        if(!connectionClient.hypertyExists(hypertyID))
            throw new DataNotFoundException();

        Map<String, HypertyInstance> userHyperties = connectionClient.getUserHyperties(userID);
        if(userHyperties.keySet().contains(hypertyID)){
            connectionClient.deleteUserHyperty(hypertyID);
        }

        else throw new CouldNotRemoveHypertyException();
    }

    public Map<String, HypertyInstance> getSpecificHyperties(Connection connectionClient, String userID, Map<String, String> parameters){
        Map<String, HypertyInstance> allUserHyperties = connectionClient.getUserHyperties(userID);

        if(allUserHyperties.isEmpty()) throw new DataNotFoundException();

        Map<String, HypertyInstance> foundHyperties = AdvancedSearch.getHyperties(parameters, allUserHyperties);

        if(!foundHyperties.isEmpty())
            return foundHyperties;

        else throw new HypertiesNotFoundException();
    }


    protected void deleteExpiredHyperties(Connection connectionClient, String userID){
        String actualDate = Dates.getActualDate();

        Map<String, HypertyInstance> userHyperties = connectionClient.getUserHyperties(userID);
        Map<String, HypertyInstance> hyperties = new ConcurrentHashMap<String, HypertyInstance>(userHyperties);

        for (Map.Entry<String, HypertyInstance> entry : hyperties.entrySet()){
            String lastModified = entry.getValue().getLastModified();
            int expires = entry.getValue().getExpires();
            if(Dates.dateCompare(actualDate, lastModified) > expires){
                connectionClient.deleteUserHyperty(entry.getKey());
            }
        }
    }

    private void checkHypertyExistence(Connection connectionClient, HypertyInstance hyperty){
        if(connectionClient.hypertyExists(hyperty.getHypertyID()))
            checkHypertyOwnership(connectionClient, hyperty);

        else newHyperty(connectionClient, hyperty);
    }

    private void checkHypertyOwnership(Connection connectionClient, HypertyInstance hyperty){
        String userID = hyperty.getUserID();
        String hypertyID = hyperty.getHypertyID();
        Map<String, HypertyInstance> userHyperties = connectionClient.getUserHyperties(userID);

        if(userHyperties.keySet().contains(hypertyID))
            updateHyperty(connectionClient, hyperty);

        else throw new CouldNotCreateOrUpdateHypertyException();
    }

    private void newHyperty(Connection connectionClient, HypertyInstance newHyperty){
        String userID = newHyperty.getUserID();
        String hypertyID = newHyperty.getHypertyID();
        newHyperty.setStartingTime(Dates.getActualDate());
        newHyperty.setLastModified(Dates.getActualDate());
        connectionClient.insertHyperty(newHyperty);
    }

    private void updateHyperty(Connection connectionClient, HypertyInstance newHyperty){
        String userID = newHyperty.getUserID();
        String hypertyID = newHyperty.getHypertyID();
        HypertyInstance oldHyperty = connectionClient.getHyperty(hypertyID);
        newHyperty.setLastModified(Dates.getActualDate());
        newHyperty.setStartingTime(oldHyperty.getStartingTime());
        connectionClient.updateHyperty(newHyperty);
    }

    private boolean validateExpiresField(long expires, long limit){
        return expires > limit;
    }

    public boolean allHypertiesAreUnavailable(Map<String, HypertyInstance> hyperties){
        for (Map.Entry<String, HypertyInstance> entry : hyperties.entrySet()){
            String status = entry.getValue().getStatus();
            if(!status.equals(DEAD))
                return false;
        }
        return true;
    }
}
