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

import static spark.Spark.*;
import com.datastax.driver.core.*;
import java.net.*;
import org.apache.log4j.Logger;
import java.util.*;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;

public class StatusService {
    static Logger log = Logger.getLogger(StatusService.class.getName());

    private static final String TYPE = "Storage type";
    private static final String DB_CONNECTION_STATUS = "Database connection";
    private static final String DB_SIZE = "Database cluster size";
    private static final String LIVE_NODES = "Database up nodes";
    private static final String UP = "up";
    private static final String STATUS = "status";
    private static final String NUM_OBJECTS = "Hyperties stored";
    private static final String NUM_REQUESTS = "User requests performed on the cluster";
    private static final String CASSANDRA = "Cassandra";
    private static final String INMEMORY = "Ram";
    private static final String NUM_APP_SERVERS = "Number of app servers";
    private static final String UP_APP_SERVERS = "Number of live app servers";

    private String databaseType;
    private Connection connection;

    private Map<String, String> domainRegistryStats = new HashMap();

    public StatusService(){
    }

    public StatusService(String databaseType, Connection connection){
        this.databaseType = databaseType;
        this.connection = connection;
    }

    public Map<String, String> getDomainRegistryStats(){
        domainRegistryStats.put(STATUS, UP);

        if(databaseType.equals(CASSANDRA))
            populateCassandraStats();

        else if(databaseType.equals(INMEMORY))
            populateRamStorageStats();

        else log.error("Invalid storage type");

        return this.domainRegistryStats;
    }

    private void populateCassandraStats(){
        domainRegistryStats.put(TYPE, CASSANDRA);
        domainRegistryStats.put(DB_SIZE, getClusterDBSize());
        domainRegistryStats.put(DB_CONNECTION_STATUS, UP);
        domainRegistryStats.put(NUM_OBJECTS, getNumHyperties());
        domainRegistryStats.put(LIVE_NODES, getClusterLiveNodes());
        //domainRegistryStats.put(NUM_REQUESTS, getNumRequests());
        //domainRegistryStats.put(NUM_APP_SERVERS, getNumAppServers());
        //domainRegistryStats.put(UP_APP_SERVERS, getNumLiveServers());
    }

    private void populateRamStorageStats(){
        domainRegistryStats.put(TYPE, INMEMORY);
        domainRegistryStats.put(NUM_OBJECTS, getNumHyperties());
    }

    private String getClusterDBSize(){
        return String.valueOf(((CassandraClient) this.connection).getClusterSize());
    }

    private String getClusterLiveNodes(){
        return String.valueOf(((CassandraClient) this.connection).getNumLiveNodes());
    }

    private String getNumRequests(){
        return String.valueOf(((CassandraClient) this.connection).getNumRequestsPerformed());
    }

    private String getNumHyperties(){
        return String.valueOf(this.connection.getNumberOfHyperties());
    }

    private String getNumAppServers(){
        int numServers = Addresses.getAppServersAddresses().size();
        return String.valueOf(numServers);
    }

    private String getNumLiveServers(){
        int numAppServers = Integer.parseInt(getNumAppServers());
        for(InetAddress ip : Addresses.getAppServersAddresses()){
            if(!Addresses.isHostReachable(ip)){
                numAppServers--;
            }
        }
        return Integer.toString(numAppServers);
    }
}
