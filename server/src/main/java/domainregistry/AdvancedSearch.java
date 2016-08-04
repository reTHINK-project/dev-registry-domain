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
import org.apache.commons.lang3.ArrayUtils;

public class AdvancedSearch{
    private static final String SCHEMES = "dataSchemes";
    private static final String RESOURCES = "resources";
    private static final String SCHEMES_PREFIX = "s.";
    private static final String RESOURCES_PREFIX = "r.";

    public static Map<String, DataObjectInstance> getDataObjects(Map<String, String> params, Map<String, DataObjectInstance> objects){
        Map<String, DataObjectInstance> foundDataObjects = new HashMap();

        for (Map.Entry<String, DataObjectInstance> entry : objects.entrySet()){
            DataObjectInstance dataObject = entry.getValue();

            List<String> resourcesTypes = map(dataObject.getResources(), RESOURCES_PREFIX);
            List<String> dataSchemesTypes = map(dataObject.getDataSchemes(), SCHEMES_PREFIX);

            List<String> dataObjectParams = new ArrayList<String>(dataSchemesTypes);
            dataObjectParams.addAll(resourcesTypes);
            Set dataObjectsParamsSet = new HashSet(dataObjectParams);

            if(dataObjectsParamsSet.containsAll(new HashSet<String>(getQueryParams(params)))){
                foundDataObjects.put(entry.getKey(), dataObject);
            }
        }

        return foundDataObjects;
    }

    public static Map<String, HypertyInstance> getHyperties(Map<String, String> parameters, Map<String, HypertyInstance> data){
        Map<String, HypertyInstance> foundHyperties = new HashMap();

        for (Map.Entry<String, HypertyInstance> entry : data.entrySet()){
            HypertyInstance hyperty = entry.getValue();

            List<String> dataSchemesTypes = map(hyperty.getDataSchemes(), SCHEMES_PREFIX);
            List<String> resourcesTypes = map(hyperty.getResources(), RESOURCES_PREFIX);

            List<String> hypertyParams = new ArrayList<String>(dataSchemesTypes);
            hypertyParams.addAll(resourcesTypes);
            Set hypertyParamsSet = new HashSet(hypertyParams);

            if(hypertyParamsSet.containsAll(new HashSet<String>(getQueryParams(parameters)))){
                foundHyperties.put(entry.getKey(), hyperty);
            }
        }

        return foundHyperties;
    }

    private static List<String> getQueryParams(Map<String, String> parameters){
        String res = parameters.get(RESOURCES);
        String schemes = parameters.get(SCHEMES);

        String[] resourceTypes = (res != null) ? res.split(","): new String[0];
        String[] dataSchemes = (schemes != null) ? schemes.split(",") : new String[0];

        List<String> prefixResourceType = map(Arrays.asList(resourceTypes), RESOURCES_PREFIX);
        List<String> prefixSchemeType = map(Arrays.asList(dataSchemes), SCHEMES_PREFIX);

        List<String> hypertyPrefixParams = new ArrayList<String>(prefixResourceType);
        hypertyPrefixParams.addAll(prefixSchemeType);

        return hypertyPrefixParams;
    }

    private static List<String> map(List<String> originalStruct, String prefix){
        List<String> finalList = new ArrayList<String>();

        for(String paramType : originalStruct){
            finalList.add(prefix + paramType);
        }

        return finalList;
    }
}

