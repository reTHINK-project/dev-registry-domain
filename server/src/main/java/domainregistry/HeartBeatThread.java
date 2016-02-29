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
