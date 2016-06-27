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
    private Map<String, String> parameters;
    private Map<String, HypertyInstance> data;

    private static final String SCHEMES = "dataSchemes";
    private static final String RESOURCES = "resources";
    private static final String SCHEMES_PREFIX = "s.";
    private static final String RESOURCES_PREFIX = "r.";

    public AdvancedSearch(Map<String, String> parameters, Map<String, HypertyInstance> data){
        this.parameters = parameters;
        this.data = data;
    }

    public Map<String, HypertyInstance> getHyperties(){
        Map<String, HypertyInstance> foundData = new HashMap();

        String res = this.parameters.get(RESOURCES);
        String schemes = this.parameters.get(SCHEMES);

        String[] resourceTypes = (res != null) ? res.split(","): new String[0];
        String[] dataSchemes = (schemes != null) ? schemes.split(",") : new String[0];

        List<String> prefixResourceType = map(Arrays.asList(resourceTypes), RESOURCES_PREFIX);
        List<String> prefixSchemeType = map(Arrays.asList(dataSchemes), SCHEMES_PREFIX);

        List<String> hypertyPrefixParams = new ArrayList<String>(prefixResourceType);
        hypertyPrefixParams.addAll(prefixSchemeType);

        for (Map.Entry<String, HypertyInstance> entry : this.data.entrySet()){
            HypertyInstance hyperty = entry.getValue();

            List<String> dataSchemesTypes = map(hyperty.getDataSchemes(), SCHEMES_PREFIX);
            List<String> resourcesTypes = map(hyperty.getResources(), RESOURCES_PREFIX);

            List<String> hypertyParams = new ArrayList<String>(dataSchemesTypes);
            hypertyParams.addAll(resourcesTypes);
            Set hypertyParamsSet = new HashSet(hypertyParams);

            if(hypertyParamsSet.containsAll(new HashSet<String>(hypertyPrefixParams))){
                foundData.put(entry.getKey(), hyperty);
            }
        }

        return foundData;
    }

    private List<String> map(List<String> originalStruct, String prefix){
        List<String> finalList = new ArrayList<String>();

        for(String paramType : originalStruct){
            finalList.add(prefix + paramType);
        }

        return finalList;
    }
}

