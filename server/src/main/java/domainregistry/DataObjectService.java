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

public class DataObjectService{
    static Logger log = Logger.getLogger(DataObjectService.class.getName());

    private Map<String, DataObjectInstance> dataObjects = new HashMap<>();

    public void createDataObject(Connection client, DataObjectInstance dataObject){
        String dataObjectName = dataObject.getName();

        if(client.dataObjectExists(dataObjectName))
            updateDataObject(client, dataObject);

        else newDataObject(client, dataObject);
    }

    public DataObjectInstance getDataObject(Connection client, String dataObjectName){
        if(client.dataObjectExists(dataObjectName))
            return client.getDataObject(dataObjectName);

        else throw new DataNotFoundException();
    }

    public void deleteDataObject(Connection client, String dataObjectName){
        if(client.dataObjectExists(dataObjectName))
            client.deleteDataObject(dataObjectName);

        else throw new DataNotFoundException();
    }

    private void newDataObject(Connection client, DataObjectInstance dataObject){
        String dataObjectName = dataObject.getName();
        dataObject.setStartingTime(Dates.getActualDate());
        dataObject.setLastModified(Dates.getActualDate());
        client.insertDataObject(dataObject);
    }

    private void updateDataObject(Connection client, DataObjectInstance newDataObject){
        String dataObjectName = newDataObject.getName();
        DataObjectInstance oldDataObject = client.getDataObject(dataObjectName);
        newDataObject.setLastModified(Dates.getActualDate());
        newDataObject.setStartingTime(oldDataObject.getStartingTime());
        client.insertDataObject(newDataObject);
    }
}