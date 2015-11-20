package domainregistry;

import java.util.ArrayList;
import java.util.List;

public class HypertyInstance {
    private String catalogAddress;
    private String guid;
    private String lastUpdate;

    public String getCatalogAddress(){
      return this.catalogAddress;
    }

    public String getGuid(){
      return this.guid;
    }

    public String getLastUpdate(){
      return this.lastUpdate;
    }
}
