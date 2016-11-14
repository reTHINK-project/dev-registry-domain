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
import org.apache.log4j.Logger;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.json.JSONArray;
import org.json.JSONObject;

public class JsonHelper{
    protected static String mergeJsons(String new_json, String old_json){
        JSONObject newJsonObject = new JSONObject(new_json);
        JSONObject oldJsonObject  = new JSONObject(old_json);

        for(int i = 0; i < newJsonObject.names().length(); i++){
            oldJsonObject.put(newJsonObject.names().getString(i),
                              newJsonObject.get(newJsonObject.names().getString(i)));
        }

        return oldJsonObject.toString();
    }
}
