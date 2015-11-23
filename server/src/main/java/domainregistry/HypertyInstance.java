package domainregistry;

import java.util.ArrayList;
import java.util.List;

public class HypertyInstance {
    private String catalogAddress;
    private String guid;
    private String lastUpdate;

    public HypertyInstance(String catalogAddress, String guid, String lastUpdate){
        this.catalogAddress = catalogAddress;
        this.guid = guid;
        this.lastUpdate = lastUpdate;
    }

    public HypertyInstance(){
    }

    public String getCatalogAddress(){
        return this.catalogAddress;
    }

    public String getGuid(){
        return this.guid;
    }

    public String getLastUpdate(){
        return this.lastUpdate;
    }

    public void setCatalogAddress(String catalogAddress){
        this.catalogAddress = catalogAddress;
    }

    public void setGuid(String guid){
        this.guid = guid;
    }

    public void setLastUpdate(String lastUpdate){
        this.lastUpdate = lastUpdate;
    }
}
