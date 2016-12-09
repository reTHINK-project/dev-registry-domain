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

public class HypertyInstance {
    private List<String> resources;
    private List<String> dataSchemes;
    private String descriptor;
    private String startingTime;
    private String hypertyID;
    private String userID;
    private String lastModified;
    private String status;
    private Integer expires;
    private String guid;

    // P2P
    private String runtime;
    private String p2pRequester;
    private String p2pHandler;

    public HypertyInstance(){
    }

    public HypertyInstance(String descriptor,
                           List<String> resources,
                           List<String> dataSchemes,
                           String startingTime,
                           String lastModified,
                           int expires,
                           String status,
                           String p2pRequester,
                           String p2pHandler,
                           String runtime,
                           String guid){


        this.descriptor = descriptor;
        this.dataSchemes = dataSchemes;
        this.resources = resources;
        this.startingTime = startingTime;
        this.lastModified = lastModified;
        this.expires = expires;
        this.guid = guid;
        this.status = status;
        this.runtime = runtime;
        this.p2pRequester = p2pRequester;
        this.p2pHandler = p2pHandler;
    }

    public HypertyInstance(String descriptor,
                           String startingTime,
                           String userID,
                           List<String> resources,
                           List<String> dataSchemes,
                           String runtime,
                           String p2pRequester,
                           String p2pHandler,
                           String lastModified,
                           int expires,
                           String status,
                           String guid){

        this.descriptor = descriptor;
        this.guid = guid;
        this.resources = resources;
        this.dataSchemes = dataSchemes;
        this.startingTime = startingTime;
        this.lastModified = lastModified;
        this.expires = expires;
        this.userID = userID;
        this.status = status;
        this.runtime = runtime;
        this.p2pRequester = p2pRequester;
        this.p2pHandler = p2pHandler;
    }

    public HypertyInstance(String descriptor){
        this.descriptor = descriptor;
    }

    public String getDescriptor(){
        return this.descriptor;
    }

    public void setDescriptor(String descriptor){
        this.descriptor = descriptor;
    }

    public String getHypertyID(){
        return this.hypertyID;
    }

    public void setHypertyID(String hypertyID){
        this.hypertyID = hypertyID;
    }

    public String getUserID(){
        return this.userID;
    }

    public void setUserID(String userID){
        this.userID = userID;
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

    public List<String> getResources(){
        return this.resources;
    }

    public void setResources(List<String> resources){
        this.resources = resources;
    }

    public List<String> getDataSchemes(){
        return this.dataSchemes;
    }

    public void setDataSchemes(List<String> dataSchemes){
        this.dataSchemes = dataSchemes;
    }

    public Integer getExpires(){
        return this.expires;
    }

    public void setExpires(int expires){
        this.expires = expires;
    }

    public String getStatus(){
        return this.status;
    }

    public void setStatus(String status){
        this.status = status;
    }

    // P2P specs

    public void setRuntime(String runtime){
        this.runtime = runtime;
    }

    public String getRuntime(){
        return this.runtime;
    }

    public void setHandler(String p2pHandler){
        this.p2pHandler = p2pHandler;
    }

    public String getHandler(){
        return this.p2pHandler;
    }

    public void setRequester(String p2pRequester){
        this.p2pRequester = p2pRequester;
    }

    public String getRequester(){
        return this.p2pRequester;
    }

    public void setGuid(String guid){
        this.guid = guid;
    }

    public String getGuid(){
        return this.guid;
    }
}
