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


    public Map<String, HypertyInstance> getAllHyperties(CassandraClient cassandra, String userID) {
        Map<String, HypertyInstance> allUserHyperties = cassandra.getUserHyperties(userID);

        if(cassandra.userExists(userID))
            deleteExpiredHyperties(cassandra, userID);

        if(cassandra.userExists(userID)) //if the user still have hyperties
            return allUserHyperties;

        else throw new UserNotFoundException();
    }

    public void createUserHyperty(CassandraClient cassandra, HypertyInstance newHyperty){
        String userID = newHyperty.getUserID();
        String hypertyID = newHyperty.getHypertyID();

        if(cassandra.userExists(userID)){
            checkHypertyExistence(cassandra, newHyperty);
            return;
        }

        if(cassandra.hypertyExists(hypertyID))
            throw new CouldNotCreateOrUpdateHypertyException();

        else newHyperty(cassandra, newHyperty);
    }

    public void deleteUserHyperty(CassandraClient cassandra, String userID, String hypertyID){
        if(!cassandra.userExists(userID))
            throw new UserNotFoundException();

        if(!cassandra.hypertyExists(hypertyID))
            throw new DataNotFoundException();

        Map<String, HypertyInstance> userHyperties = cassandra.getUserHyperties(userID);
        if(userHyperties.keySet().contains(hypertyID)){
            cassandra.deleteUserHyperty(hypertyID);
        }

        else throw new CouldNotRemoveHypertyException();
    }

    protected void deleteExpiredHyperties(CassandraClient cassandra, String userID){
        String actualDate = Dates.getActualDate();
        Map<String, HypertyInstance> userHyperties = cassandra.getUserHyperties(userID);
        for (Map.Entry<String, HypertyInstance> entry : userHyperties.entrySet()){
            String lastModified = entry.getValue().getLastModified();
            int expires = entry.getValue().getExpires();
            if(Dates.dateCompare(actualDate, lastModified) > expires){
                cassandra.deleteUserHyperty(entry.getKey());
            }
        }
    }

    private void checkHypertyExistence(CassandraClient cassandra, HypertyInstance hyperty){
        if(cassandra.hypertyExists(hyperty.getHypertyID()))
            checkHypertyOwnership(cassandra, hyperty);

        else newHyperty(cassandra, hyperty);
    }

    public void checkHypertyOwnership(CassandraClient cassandra, HypertyInstance hyperty){
        String userID = hyperty.getUserID();
        String hypertyID = hyperty.getHypertyID();
        Map<String, HypertyInstance> userHyperties = cassandra.getUserHyperties(userID);

        if(userHyperties.keySet().contains(hypertyID))
            updateHyperty(cassandra, hyperty);

        else throw new CouldNotCreateOrUpdateHypertyException();
    }

    private void newHyperty(CassandraClient cassandra, HypertyInstance newHyperty){
        String userID = newHyperty.getUserID();
        String hypertyID = newHyperty.getHypertyID();
        newHyperty.setStartingTime(Dates.getActualDate());
        newHyperty.setLastModified(Dates.getActualDate());
        cassandra.insertHyperty(newHyperty);
    }

    private void updateHyperty(CassandraClient cassandra, HypertyInstance newHyperty){
        String userID = newHyperty.getUserID();
        String hypertyID = newHyperty.getHypertyID();
        HypertyInstance oldHyperty = cassandra.getHyperty(hypertyID);
        newHyperty.setLastModified(Dates.getActualDate());
        newHyperty.setStartingTime(oldHyperty.getStartingTime());
        cassandra.updateHyperty(newHyperty);
    }
}
