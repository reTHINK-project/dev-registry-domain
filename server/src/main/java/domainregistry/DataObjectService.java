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
import java.util.concurrent.ConcurrentHashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.json.JSONArray;
import org.json.JSONObject;

public class DataObjectService{
    static Logger log = Logger.getLogger(DataObjectService.class.getName());
    private static final String EXPIRES = "EXPIRES";

    private Map<String, DataObjectInstance> dataObjects = new HashMap<>();

    public void createDataObject(Connection client, DataObjectInstance dataObject){
        String dataObjectUrl = dataObject.getUrl();
        long expiresLimit = Long.valueOf(System.getenv(EXPIRES)).longValue();

        if(validateExpiresField(dataObject.getExpires(), expiresLimit)){
            dataObject.setExpires((int) expiresLimit);
            log.info("Expires was set to the max value allowed by the Domain Registry: " + expiresLimit);
        }

        if(client.dataObjectExists(dataObjectUrl))
            updateDataObject(client, dataObject);

        else newDataObject(client, dataObject);
    }

    private boolean validateExpiresField(long expires, long limit){
        return expires > limit;
    }

    public void updateDataObjectFields(Connection connectionClient, DataObjectInstance updatedDataObject){
        Gson gson = new Gson();
        String dataObjectUrl = updatedDataObject.getUrl();

        if(!connectionClient.dataObjectExists(dataObjectUrl))
            throw new DataObjectNotFoundException();

        DataObjectInstance oldDataObject = connectionClient.getDataObjectByUrl(dataObjectUrl);

        String oldDataObjectJson = gson.toJson(oldDataObject);
        String updatedDataObjectJson = gson.toJson(updatedDataObject);

        log.info("FIELDS DATA OBJECT TO PERFORM THE UPDATE " + updatedDataObjectJson);

        String resultJson = JsonHelper.mergeJsons(updatedDataObjectJson, oldDataObjectJson);

        log.info("RESULT DATA OBJECT JSON: " + resultJson);

        updateDataObject(connectionClient, gson.fromJson(resultJson, DataObjectInstance.class));
    }

    public void keepAlive(Connection client, String dataObjectUrl){
        if(client.dataObjectExists(dataObjectUrl)){
            DataObjectInstance dataObject = client.getDataObjectByUrl(dataObjectUrl);
            dataObject.setLastModified(Dates.getActualDate());
            client.insertDataObject(dataObject);
        }

        else throw new DataNotFoundException();
    }

    public DataObjectInstance getDataObject(Connection client, String dataObjectUrl){
        if(client.dataObjectExists(dataObjectUrl))
            return client.getDataObjectByUrl(dataObjectUrl);

        else throw new DataNotFoundException();
    }

    public Map<String, DataObjectInstance> getDataObjectsByHyperty(Connection client, String hypertyReporter){
        Map<String, DataObjectInstance> dObjects = client.getDataObjectsByHyperty(hypertyReporter);

        if(dObjects.isEmpty())
            throw new DataNotFoundException();

        else return dObjects;
    }

    public Map<String, DataObjectInstance> getDataObjectsByName(Connection client, String dataObjectName){
        Map<String, DataObjectInstance> dObjects = client.getDataObjectsByName(dataObjectName);

        if(dObjects.isEmpty())
            throw new DataNotFoundException();

        else return dObjects;
    }

    public Map<String, DataObjectInstance> getSpecificDataObjectsByUrl(Connection client, String dataObjectUrl, Map<String, String> parameters){
        DataObjectInstance dataObject = client.getDataObjectByUrl(dataObjectUrl);

        Map<String, DataObjectInstance> dataObjects = new HashMap<>();
        dataObjects.put(dataObjectUrl, dataObject);

        Map<String, DataObjectInstance> foundDataObjects = AdvancedSearch.getDataObjects(parameters, dataObjects);

        if(!foundDataObjects.isEmpty())
            return foundDataObjects;

        else throw new DataObjectNotFoundException();
    }

    public Map<String, DataObjectInstance> getSpecificDataObjectsByReporter(Connection client, String dataObjectReporter, Map<String, String> parameters){
        Map<String, DataObjectInstance> dObjects = client.getDataObjectsByHyperty(dataObjectReporter);

        if(dObjects.isEmpty()) throw new DataNotFoundException();

        Map<String, DataObjectInstance> foundDataObjects = AdvancedSearch.getDataObjects(parameters, dObjects);

        if(!foundDataObjects.isEmpty())
            return foundDataObjects;

        else throw new DataObjectNotFoundException();
    }

    public Map<String, DataObjectInstance> getSpecificDataObjectsByName(Connection client, String dataObjectName, Map<String, String> parameters){
        Map<String, DataObjectInstance> dObjects = client.getDataObjectsByName(dataObjectName);

        if(dObjects.isEmpty()) throw new DataNotFoundException();

        Map<String, DataObjectInstance> foundDataObjects = AdvancedSearch.getDataObjects(parameters, dObjects);

        if(!foundDataObjects.isEmpty())
            return foundDataObjects;

        else throw new DataObjectNotFoundException();
    }

    public void deleteExpiredDataObjects(Connection client){
        String actualDate = Dates.getActualDate();

        Map<String, DataObjectInstance> allDataObjects = client.getDataObjects();
        Map<String, DataObjectInstance> dataObjects = new ConcurrentHashMap<String, DataObjectInstance>(allDataObjects);

        for (Map.Entry<String, DataObjectInstance> entry : dataObjects.entrySet()){
            String lastModified = entry.getValue().getLastModified();
            int expires = entry.getValue().getExpires();
            if(Dates.dateCompare(actualDate, lastModified) > expires){
                client.deleteDataObject(entry.getKey());
            }
        }
    }

    private void newDataObject(Connection client, DataObjectInstance dataObject){
        dataObject.setStartingTime(Dates.getActualDate());
        dataObject.setLastModified(Dates.getActualDate());
        client.insertDataObject(dataObject);
    }

    private void updateDataObject(Connection client, DataObjectInstance newDataObject){
        String dataObjectUrl = newDataObject.getUrl();
        DataObjectInstance oldDataObject = client.getDataObjectByUrl(dataObjectUrl);
        newDataObject.setLastModified(Dates.getActualDate());
        newDataObject.setStartingTime(oldDataObject.getStartingTime());
        client.insertDataObject(newDataObject);
    }
}
