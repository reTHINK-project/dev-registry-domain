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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import org.apache.log4j.Logger;

class HeartBeatThread extends Thread {
    static Logger log = Logger.getLogger(HeartBeatThread.class.getName());

    final int TEN_SECONDS = 10000;
    HypertyService service;

    public HeartBeatThread(HypertyService service){
        this.service = service;
    }

    @Override
    public void run(){
        try{
            while(true){
                Thread.sleep(TEN_SECONDS);
                if(!service.getServices().isEmpty()){
                    inactiveHypertiesVerification();
                }
            }
        }catch(InterruptedException e){
            e.printStackTrace();
        }
    }

    private void inactiveHypertiesVerification(){
        Map<String, Map<String, HypertyInstance>> userServices = service.getServices();
        for(Map.Entry<String, Map<String, HypertyInstance>> entry : userServices.entrySet()){
            for(Map.Entry<String, HypertyInstance> hyperties : entry.getValue().entrySet()){
                String lastModified = hyperties.getValue().getLastModified();
                if(hypertyAgeVerification(Dates.getActualDate(), lastModified)){
                    deleteHyperty(entry.getKey(), hyperties.getKey());
                }
            }
        }
    }

    private boolean hypertyAgeVerification(String actualDate, String lastModifiedDate){
        final int ONE_MINUTE = 60; //CHANGE later. small value to facilitate local testing
        return Dates.dateCompare(actualDate, lastModifiedDate) > ONE_MINUTE;
    }

    private void deleteHyperty(String user, String hyperty){
        service.deleteUserHyperty(user, hyperty);
        log.info("deleted hyperty" + hyperty + " from user " + user);
    }
}
