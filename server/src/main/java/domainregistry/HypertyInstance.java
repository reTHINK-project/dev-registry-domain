package domainregistry;

import java.util.ArrayList;
import java.util.List;

public class HypertyInstance {
    private String descriptor;

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
}
