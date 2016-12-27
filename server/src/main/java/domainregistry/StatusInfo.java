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


public class StatusInfo {

    private String userGuid;
    private String userURL;
    private String storageType;
    private String numHyperties;
    private String numUsers;

    public void UserInfo () {

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

}
