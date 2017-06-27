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

interface Connection{
    //hyperties
    public ArrayList<String> getAllUsers();
    public HypertyInstance getHyperty(String hypertyID);
    public boolean hypertyExists(String hypertyID);
    public boolean userExists(String hypertyID);
    public void updateHyperty(HypertyInstance hyperty);
    public void insertHyperty(HypertyInstance hyperty);
    public Map<String, HypertyInstance> getUserHyperties(String userID);
    public void deleteUserHyperty(String hypertyID);
    public int getNumberOfHyperties();
    public Map<String, HypertyInstance> getHypertiesByGuid(String guid);
    public String getUserByGuid(String guid);
    public boolean guidExists(String guid);
    public ArrayList<HypertyInstance> getHypertiesByEmail(String email);
    public Map<String, HypertyInstance> getUpdatedHypertiesMap();
    public void clearUpdatedHypertiesMap();
    //dataobjects
    public Map<String, DataObjectInstance> getUpdatedDataObjectsMap();
    public void insertDataObject(DataObjectInstance dataObject);
    public boolean dataObjectExists(String dataObjectName);
    public void deleteDataObject(String dataObjectName);
    public DataObjectInstance getDataObjectByUrl(String dataObjectUrl);
    public Map<String, DataObjectInstance> getDataObjectsByHyperty(String hypertyReporter);
    public Map<String, DataObjectInstance> getDataObjectsByName(String dataObjectName);
    public Map<String, DataObjectInstance> getDataObjects();
    public ArrayList<String> getAllDataObjects();
    public void clearUpdatedDataObjectsMap();
}
