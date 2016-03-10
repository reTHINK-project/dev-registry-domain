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

        if(!cassandra.userExists(userID))
            throw new UserNotFoundException();

        if(allUserHyperties.isEmpty())
            throw new DataNotFoundException();

        else return allUserHyperties;
    }

    public void createUserHyperty(CassandraClient cassandra, String userID, String hypertyID, HypertyInstance newHyperty){
        if(cassandra.hypertyExists(hypertyID)){
            HypertyInstance oldHyperty = cassandra.getHyperty(hypertyID);
            newHyperty.setLastModified(Dates.getActualDate());
            newHyperty.setStartingTime(oldHyperty.getStartingTime());
            cassandra.updateHyperty(hypertyID, newHyperty);
        }

        else{
            newHyperty.setStartingTime(Dates.getActualDate());
            newHyperty.setLastModified(Dates.getActualDate());
            cassandra.insertHyperty(newHyperty, hypertyID, userID);
        }
    }

    public void deleteUserHyperty(CassandraClient cassandra, String userID, String hypertyID){
        if(!cassandra.userExists(userID))
            throw new UserNotFoundException();

        if(cassandra.hypertyExists(hypertyID))
            cassandra.deleteUserHyperty(hypertyID);

        else throw new DataNotFoundException();
    }
}
