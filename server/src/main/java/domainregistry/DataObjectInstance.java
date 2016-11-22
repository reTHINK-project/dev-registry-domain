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

import java.util.List;

public class DataObjectInstance {
    private String schema;
    private String url;
    private String reporter;
    private String name;
    private String status;
    private String startingTime;
    private List<String> resources;
    private List<String> dataSchemes;
    private String lastModified;

    public DataObjectInstance(){
    }

    public DataObjectInstance(String name, String schema, String reporter, String url){
        this.schema = schema;
        this.url = url;
        this.reporter = reporter;
        this.name = name;
    }

    public DataObjectInstance(String name, String schema, List<String> dataSchemes, List<String> resources, String reporter, String url, String startingTime, String lastModified, String status){
        this.schema = schema;
        this.url = url;
        this.resources = resources;
        this.reporter = reporter;
        this.name = name;
        this.startingTime = startingTime;
        this.dataSchemes = dataSchemes;
        this.lastModified = lastModified;
        this.status = status;
    }

    public List<String> getResources(){
        return this.resources;
    }

    public void setResources(List<String> resources){
        this.resources = resources;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getUrl(){
        return this.url;
    }

    public void setUrl(String url){
        this.url = url;
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

    public String getStartingTime(){
        return this.startingTime;
    }

    public void setStartingTime(String time){
        this.startingTime = time;
    }

    public String getLastModified(){
        return this.lastModified;
    }

    public void setLastModified(String time){
        this.lastModified = time;
    }

    public List<String> getDataSchemes(){
        return this.dataSchemes;
    }

    public void setDataSchemes(List<String> dataSchemes){
        this.dataSchemes = dataSchemes;
    }

    public String getStatus(){
        return this.status;
    }

    public void setStatus(String status){
        this.status = status;
    }
}
