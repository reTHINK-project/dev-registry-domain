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
import java.util.*;
import org.apache.log4j.Logger;

import com.datastax.driver.core.exceptions.WriteTimeoutException;

class HeartBeatThread extends Thread {
    static Logger log = Logger.getLogger(HeartBeatThread.class.getName());

    HypertyService hypertyService;
    DataObjectService dataObjectService;

    Connection storageClient;
    long time;

    public HeartBeatThread(HypertyService hypertyService, DataObjectService dataObjectService, Connection storageClient, long time){
        this.hypertyService = hypertyService;
        this.dataObjectService = dataObjectService;
        this.time = time;
        this.storageClient = storageClient;
    }

    @Override
    public void run(){
        try{
            while(true){
                TimeUnit.SECONDS.sleep(this.time);
                changeHypertyStatus(this.storageClient);
                changeDataObjectStatus(this.storageClient);
            }
        }catch(InterruptedException | WriteTimeoutException e){
            e.printStackTrace();
        }
    }

    private void changeHypertyStatus(Connection storageClient){
        log.info("Hyperty status verification has started...");
        ArrayList<String> users = storageClient.getAllUsers();

        if(!users.isEmpty()){
            for(String user : users){
                hypertyService.deleteExpiredHyperties(storageClient, user);
            }
        }
    }

    private void changeDataObjectStatus(Connection storageClient){
        log.info("DataObject status verification has started...");
        ArrayList<String> allDataObjects = storageClient.getAllDataObjects();

        if(!allDataObjects.isEmpty()){
            dataObjectService.deleteExpiredDataObjects(storageClient);
        }
    }
}
