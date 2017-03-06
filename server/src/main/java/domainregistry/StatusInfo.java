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



public class StatusInfo {

    static Logger log = Logger.getLogger(HypertyController.class.getName());
    private String domainURL;
    private String userGuid;
    private String userURL;
    private String storageType;
    private String numHyperties;
    private String numUsers;
    private String clusterDBSize;
    private String clusterLiveNodes;
    private String totalHyperties;
    private String liveHyperties;
    private String deadHyperties;
    private List<HypertyInstance> listHyperties;

    public void StatusInfo () {

    }

    public void setDomainURL(String domainURL){
        this.domainURL = "http://" + domainURL;
    }

    public String getDomainURL(){
        return this.domainURL;
    }

    public void setUserGuid(String guid){
        this.userGuid=guid;
    }

    public String getUserGuid(){
        return this.userGuid;
    }

    public void setUserURL(String userURL){
        this.userURL=userURL;
    }

    public String getUserURL(){
        return this.userURL;
    }

    public void setStorageType(String storageType){
        this.storageType=storageType;
    }

    public String getStorageType(){
        return this.storageType;
    }

    public void setNumHyperties(String numHyperties){
        this.numHyperties=numHyperties;
    }

    public String getNumHyperties(){
        return this.numHyperties;
    }

    public void setNumUsers(String numUsers){
        this.numUsers=numUsers;
    }

    public String getNumUsers(){
        return this.numUsers;
    }

    public void setClusterDBSize(String clusterDBSize){
        this.clusterDBSize=clusterDBSize;
    }

    public String getClusterDBSize(){
        return this.clusterDBSize;
    }

    public void setClusterLiveNodes(String clusterLiveNodes){
        this.clusterLiveNodes=clusterLiveNodes;
    }

    public String getClusterLiveNodes(){
        return this.clusterLiveNodes;
    }

    public void setTotalHyperties(String totalHyperties){
        this.totalHyperties=totalHyperties;
    }

    public String getTotalHyperties(){
        return this.totalHyperties;
    }

    public void setLiveHyperties(String liveHyperties){
        this.liveHyperties=liveHyperties;
    }
    public String getLiveHyperties(){
        return this.liveHyperties;
    }

    public void setDeadHyperties(String deadHyperties){
        this.deadHyperties=deadHyperties;
    }

    public String getDeadHyperties(){
        return this.deadHyperties;
    }

    public void setListHyperties(List<HypertyInstance> listHyperties){
        this.listHyperties=listHyperties;
    }

    public List<HypertyInstance> getListHyperties(){
        return this.listHyperties;
    }

}
