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

import java.util.Map;
import java.util.concurrent.TimeUnit;

class HeartBeatThread extends Thread {
    HypertyService service;

    public HeartBeatThread(HypertyService service){
        this.service = service;
    }

    @Override
    public void run(){
        try{
            while(true){
                TimeUnit.DAYS.sleep(1); //cleanup is executed once a day
                if(!service.getServices().isEmpty()){
                    hypertiesCleanup();
                }
            }
        }catch(InterruptedException e){
            e.printStackTrace();
        }
    }

    private void hypertiesCleanup(){
        Map<String, Map<String, HypertyInstance>> users = service.getServices();
        for(String userID : users.keySet()){
            service.deleteExpiredHyperties(userID);
        }
    }
}
