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

import java.util.ArrayList;
import java.util.List;

public class HypertyInstance {
    private String descriptor;
    private String startingTime;
    private String hypertyID;
    private String userID;
    private String lastModified;
    private int expires;

    public HypertyInstance(){
    }

    public HypertyInstance(String descriptor,
                           String startingTime,
                           String lastModified,
                           int expires){

        this.descriptor = descriptor;
        this.startingTime = startingTime;
        this.lastModified = lastModified;
        this.expires = expires;
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

    public int getExpires(){
        return this.expires;
    }

    public void setExpires(int expires){
        this.expires = expires;
    }
}
