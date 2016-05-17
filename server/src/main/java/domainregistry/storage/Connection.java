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
    //dataobjects
    public void insertDataObject(DataObjectInstance dataObject);
    public boolean dataObjectExists(String dataObjectName);
    public DataObjectInstance getDataObjectByUrl(String dataObjectUrl);
    // public DataObjectInstance getDataObjectByName(String dataObjectName);
    public void deleteDataObject(String dataObjectName);
    public Map<String, DataObjectInstance> getDataObjectsByHyperty(String hypertyReporter);
}
