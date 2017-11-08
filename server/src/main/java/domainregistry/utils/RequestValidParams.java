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

public class RequestValidParams {
    // PUT requests are supposed to receive:
    private static final List<String> hypertyCreationExpectedParams =
          Collections.unmodifiableList(Arrays.asList(
                           "expires",
                           "dataSchemes",
                           "resources",
                           "runtime",
                           "status",
                           "descriptor"));

    private static final List<String> dataObjectCreationExpectedParams =
          Collections.unmodifiableList(Arrays.asList(
                           "schema",
                           "name",
                           "reporter",
                           "resources",
                           "dataSchemes",
                           "status",
                           "expires",
                           "runtime"));

    protected static List<String> getHypertyvalidParamskeys(){
        return hypertyCreationExpectedParams;
    }

    protected static List<String> getDataObjectsvalidParamskeys(){
        return dataObjectCreationExpectedParams;
    }
}
