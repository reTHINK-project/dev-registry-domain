package domainregistry;

import java.util.ArrayList;
import java.util.List;

public class HypertyInstance {
    private String descriptor;
    private String startingTime;
    private String lastModified;

    public HypertyInstance(){
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
}
