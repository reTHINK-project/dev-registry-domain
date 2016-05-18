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
import org.apache.commons.lang3.ArrayUtils;

public class HypertyService{
    static Logger log = Logger.getLogger(HypertyService.class.getName());
    private static final String EXPIRES = "EXPIRES";
    private static final String SCHEMES = "dataSchemes";
    private static final String RESOURCES = "resources";

    private static final String SCHEMES_PREFIX = "s.";
    private static final String RESOURCES_PREFIX = "r.";

    public Map<String, HypertyInstance> getAllHyperties(Connection connectionClient, String userID) {
        Map<String, HypertyInstance> allUserHyperties = connectionClient.getUserHyperties(userID);

        if(connectionClient.userExists(userID)){
            deleteExpiredHyperties(connectionClient, userID);
        }

        if(connectionClient.userExists(userID) && !allUserHyperties.isEmpty()){
            return allUserHyperties;
        }

        else throw new UserNotFoundException();
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
        Map<String, HypertyInstance> foundHyperties = new HashMap();
        Map<String, HypertyInstance> allUserHyperties = connectionClient.getUserHyperties(userID);

        if(allUserHyperties.isEmpty()) throw new DataNotFoundException();

        String res = parameters.get(RESOURCES);
        String schemes = parameters.get(SCHEMES);

        String[] resourceTypes = (res != null) ? res.split(","): new String[0];
        String[] dataSchemes = (schemes != null) ? schemes.split(",") : new String[0];

        List<String> prefixResourceType = javaMapImplementation(Arrays.asList(resourceTypes), RESOURCES_PREFIX);
        List<String> prefixSchemeType = javaMapImplementation(Arrays.asList(dataSchemes), SCHEMES_PREFIX);

        List<String> hypertyPrefixParams = new ArrayList<String>(prefixResourceType);
        hypertyPrefixParams.addAll(prefixSchemeType);

        for (Map.Entry<String, HypertyInstance> entry : allUserHyperties.entrySet()){
            HypertyInstance hyperty = entry.getValue();

            List<String> dataSchemesTypes = javaMapImplementation(hyperty.getDataSchemes(), SCHEMES_PREFIX);
            List<String> resourcesTypes = javaMapImplementation(hyperty.getResources(), RESOURCES_PREFIX);

            List<String> hypertyParams = new ArrayList<String>(dataSchemesTypes);
            hypertyParams.addAll(resourcesTypes);
            Set hypertyParamsSet = new HashSet(hypertyParams);

            if(hypertyParamsSet.containsAll(new HashSet<String>(hypertyPrefixParams))){
                foundHyperties.put(entry.getKey(), hyperty);
            }
        }

        if(!foundHyperties.isEmpty())
            return foundHyperties;

        else throw new HypertiesNotFoundException();
    }


    protected void deleteExpiredHyperties(Connection connectionClient, String userID){
        String actualDate = Dates.getActualDate();

        Map<String, HypertyInstance> userHyperties = connectionClient.getUserHyperties(userID);
        Map<String, HypertyInstance> hyperties = new ConcurrentHashMap<String, HypertyInstance>(userHyperties);
<<<<<<< HEAD
=======

        log.info(hyperties.getClass());
>>>>>>> master

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

    private List<String> javaMapImplementation(List<String> originalResourceStruct, String prefix){
        List<String> finalList = new ArrayList<String>();

        for(String paramType : originalResourceStruct){
            finalList.add(prefix + paramType);
        }

        return finalList;
    }
}

