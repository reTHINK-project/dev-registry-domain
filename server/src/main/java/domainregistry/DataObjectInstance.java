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

public class DataObjectInstance {
    private String schema;
    private String url;
    private String reporter;
    private String name;

    public DataObjectInstance(String name, String schema, String reporter, String url){
        this.schema = schema;
        this.url = url;
        this.reporter = reporter;
        this.name = name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getUrl(){
        return this.url;
    }

    public String getSchema(){
        return this.schema;
    }

    public String getReporter(){
        return this.reporter;
    }

    public String getName(){
        return this.name;
    }
}
