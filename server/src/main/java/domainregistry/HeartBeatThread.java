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
                    DeadHypertiesVerification();
                }
            }
        }catch(InterruptedException e){
            e.printStackTrace();
        }
    }

    private void DeadHypertiesVerification(){
        final int ONE_MINUTE = 60; //CHANGE later. very small value only for testing
        Map<String, Map<String, HypertyInstance>> userServices = service.getServices();
        for(Map.Entry<String, Map<String, HypertyInstance>> entry : userServices.entrySet()){
            for(Map.Entry<String, HypertyInstance> hyperties : entry.getValue().entrySet()){
                String lastModified = hyperties.getValue().getLastModified();
                if(Dates.dateCompare(Dates.getActualDate(), lastModified) > ONE_MINUTE){
                    log.info("Hyperty to be deleted" + hyperties.getKey() + " from user " + entry.getKey());
                    service.deleteUserHyperty(entry.getKey(), hyperties.getKey());
                }
            }
        }
    }
}
