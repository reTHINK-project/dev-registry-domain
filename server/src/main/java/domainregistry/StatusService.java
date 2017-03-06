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

    private static final String TYPE = "Storage_type";
    private static final String DB_CONNECTION_STATUS = "Database connection";
    private static final String DB_SIZE = "Database cluster size";
    private static final String LIVE_NODES = "Database up nodes";
    private static final String UP = "up";
    private static final String STATUS = "status";
    private static final String NUM_OBJECTS = "Hyperties_stored";
    private static final String NUM_REQUESTS = "User requests performed on the cluster";
    private static final String CASSANDRA = "Cassandra";
    private static final String INMEMORY = "Ram";
    private static final String NUM_APP_SERVERS = "Number of app servers";
    private static final String UP_APP_SERVERS = "Number of live app servers";
    private static String DOMAIN_URL;

    private String databaseType;
    private Connection connection;
    private HypertyService hypertyService;
    private DataObjectService dataObjectService;

    private Map<String, String> domainRegistryStats = new HashMap();
    private Map<String, List<Object>> domainRegistryStatsHtml = new HashMap<String, List<Object>>();


    public StatusService(){
    }

    public StatusService(String databaseType, Connection connection, HypertyService hypertyService, DataObjectService dataObjectService){
        this.databaseType = databaseType;
        this.connection = connection;
        this.hypertyService = hypertyService;
        this.dataObjectService = dataObjectService;
    }

    public Map<String, List<Object>> getDomainRegistryStatsGlobal(String domainURL){
        DOMAIN_URL=domainURL;
        if(databaseType.equals(INMEMORY)){
            populateRamStorageStatsHtml();
            infoAboutUsersAndHypertiesRam();
        }

        else if(databaseType.equals(CASSANDRA)){
            populateCassandraStatsHtml();
            infoAboutUsersAndHypertiesCassandra();
        }

        return this.domainRegistryStatsHtml;
    }

    private void populateRamStorageStatsHtml(){
        ArrayList<Object> list = new ArrayList<Object>();
        StatusInfo info = new StatusInfo();
        info.setDomainURL(DOMAIN_URL);
        info.setStorageType(INMEMORY);
        info.setNumHyperties(getNumHyperties());
        info.setNumUsers(String.valueOf(((RamClient) this.connection).getNumUsersWithHyperties()));
        list.add(info);
        domainRegistryStatsHtml.put("Init", list);
    }

    private void populateCassandraStatsHtml(){
        ArrayList<Object> list = new ArrayList<Object>();
        StatusInfo info = new StatusInfo();
        info.setDomainURL(DOMAIN_URL);
        info.setStorageType(CASSANDRA);
        info.setNumHyperties(getNumHyperties());
        info.setNumUsers(String.valueOf(((CassandraClient) this.connection).getNumUsersWithHyperties()));
        info.setClusterDBSize(getClusterDBSize());
        info.setClusterLiveNodes(getClusterLiveNodes());
        list.add(info);
        domainRegistryStatsHtml.put("Init", list);
    }

    private void infoAboutUsersAndHypertiesRam(){
        Map<String, String> usersByGuid = ((RamClient) this.connection).getMapUsersByGuid();
        ArrayList<Object> users = new ArrayList<Object>();
        for(String guid : usersByGuid.keySet()){
            StatusInfo info = new StatusInfo();
            info.setUserGuid(guid);
            info.setUserURL(usersByGuid.get(guid));
            Map<String, HypertyInstance> hyperties = this.hypertyService.getHypertiesForStatusPage(this.connection, guid);
            checkhypertiesState(hyperties, info);
            users.add(info);
        }
        domainRegistryStatsHtml.put("Users", users);
    }

    private void infoAboutUsersAndHypertiesCassandra(){
        Map<String, String> usersByGuid = ((CassandraClient) this.connection).getMapUsersByGuid();
        ArrayList<Object> users = new ArrayList<Object>();
        for(String guid : usersByGuid.keySet()){
            StatusInfo info = new StatusInfo();
            info.setUserGuid(guid);
            info.setUserURL(usersByGuid.get(guid));
            Map<String, HypertyInstance> hyperties = this.hypertyService.getHypertiesForStatusPage(this.connection, guid);
            checkhypertiesState(hyperties, info);
            users.add(info);
        }
        domainRegistryStatsHtml.put("Users", users);
    }

    public void checkhypertiesState(Map<String,HypertyInstance> hyperties, StatusInfo info){
        int totalHyperties = 0;
        int liveHyperties = 0;
        int deadHyperties = 0;
        List<HypertyInstance> listHyperties =  new ArrayList();

        for(HypertyInstance hyperty : hyperties.values()){
            totalHyperties++;

            if(hyperty.getStatus().equals("live"))
                liveHyperties++;

            else deadHyperties++;

            listHyperties.add(hyperty);
        }

        info.setTotalHyperties(String.valueOf(totalHyperties));
        info.setLiveHyperties(String.valueOf(liveHyperties));
        info.setDeadHyperties(String.valueOf(deadHyperties));
        info.setListHyperties(listHyperties);
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
