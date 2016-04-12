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
    static Logger log = Logger.getLogger(HypertyService.class.getName());

    private Map<String, DataObjectInstance> dataObjects = new HashMap<>();

    public void createDataObject(DataObjectInstance dataObject, String dataObjectID){
        dataObjects.put(dataObjectID, dataObject);
    }

    public DataObjectInstance getDataObject(String dataObjectID){
        if(dataObjects.containsKey(dataObjectID))
            return dataObjects.get(dataObjectID);

        else throw new DataNotFoundException();
    }
}
