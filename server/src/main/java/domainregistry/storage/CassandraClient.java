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

import com.datastax.driver.core.*;
import com.datastax.driver.core.policies.RoundRobinPolicy;
import com.datastax.driver.core.policies.TokenAwarePolicy;
import com.datastax.driver.core.policies.DefaultRetryPolicy;
import com.datastax.driver.core.exceptions.NoHostAvailableException;
import java.net.InetAddress;
import java.util.*;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import com.datastax.driver.core.querybuilder.Insert;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Update;

import static java.lang.System.out;

public class CassandraClient implements Connection{
    private static final Logger log = LogManager.getLogger(CassandraClient.class.getName());
    private static final String DEAD = "disconnected";

    public static final String KEYSPACE  = "rethinkeyspace";
    public static final String IDHYPERTIES = "hyperties_by_id";
    public static final String USERHYPERTIES = "hyperties_by_user";
    public static final String URLDATAOBJECTS = "data_objects_by_url";
    public static final String REPORTERDATAOBJECTS = "data_objects_by_reporter";
    public static final String NAMEDATAOBJECTS = "data_objects_by_name";
    public static final String GUIDBYUSER = "guid_by_user_id";
    public static final String EMAILBYUSER = "hyperties_by_email";
    public static final String UPDATEDHYPERTIES = "updated_hyperties";
    public static final String DOWN = "DOWN";

    private Cluster cluster;
    private Session session;

    public void connect(Collection<InetAddress> addresses){
        this.cluster = Cluster.builder()
            .addContactPoints(addresses)
            .withRetryPolicy(DefaultRetryPolicy.INSTANCE)
            .withLoadBalancingPolicy(new TokenAwarePolicy(new RoundRobinPolicy()))
            .build();

        try{
            session = cluster.connect(KEYSPACE);
            final Metadata metadata = this.cluster.getMetadata();
            out.printf("Connected to cluster: %s\n", metadata.getClusterName());
            for (final Host host : metadata.getAllHosts()){
                out.printf("Datacenter: %s; Host: %s; Rack: %s\n",
                        host.getDatacenter(), host.getAddress(), host.getRack());
            }
        } catch (NoHostAvailableException e){
            System.out.println("No Cassandra server available");
        }
    }

    public Map<String, HypertyInstance> getUpdatedHypertiesMap(){
        Map<String, HypertyInstance> hyperties = new HashMap();
        Statement select = QueryBuilder.select().all().from(KEYSPACE, UPDATEDHYPERTIES);
        ResultSet results = session.execute(select);
        if(results == null) return Collections.emptyMap();

        for(Row row : results){
             HypertyInstance hyperty = new HypertyInstance(row.getString("descriptor"),
                                                         row.getList("resources", String.class),
                                                         row.getList("dataSchemes", String.class),
                                                         row.getString("startingTime"),
                                                         row.getString("lastModified"),
                                                         row.getInt("expires"),
                                                         row.getString("status"),
                                                         row.getString("p2pRequester"),
                                                         row.getString("p2pHandler"),
                                                         row.getString("runtime"),
                                                         row.getString("guid"));
             hyperty.setHypertyID(row.getString("hypertyID"));
             hyperties.put(row.getString("hypertyID"), hyperty);
         }

         return hyperties;
    }

    public void clearUpdatedHypertiesMap() {
        Statement select = QueryBuilder.truncate(KEYSPACE, UPDATEDHYPERTIES);
        ResultSet results = session.execute(select);
    }

    public void updateTableUpdatedHyperties(HypertyInstance hyperty, String table){
        if(updatedHypertyExists(hyperty, table)){
            HypertyInstance updatedhyperty = getUpdatedHyperty(hyperty, table);
            //if(hyperty.getStatus().equals("live")){
            Statement deleteFromNames = QueryBuilder.delete().from(KEYSPACE, table)
                                             .where(QueryBuilder.eq("hypertyID", hyperty.getHypertyID()));
            session.execute(deleteFromNames);
            // } else {
            //     Statement update = QueryBuilder.update(KEYSPACE, table)
            //                            .with(QueryBuilder.set("descriptor", hyperty.getDescriptor()))
            //                            .and(QueryBuilder.set("lastModified", hyperty.getLastModified()))
            //                            .and(QueryBuilder.set("resources", hyperty.getResources()))
            //                            .and(QueryBuilder.set("dataSchemes", hyperty.getDataSchemes()))
            //                            .and(QueryBuilder.set("expires", hyperty.getExpires()))
            //                            .and(QueryBuilder.set("runtime", hyperty.getRuntime()))
            //                            .and(QueryBuilder.set("p2pRequester", hyperty.getRequester()))
            //                            .and(QueryBuilder.set("p2pHandler", hyperty.getHandler()))
            //                            .and(QueryBuilder.set("status", hyperty.getStatus()))
            //                            .and(QueryBuilder.set("guid", hyperty.getGuid()))
            //                            .where(QueryBuilder.eq("hypertyID", hyperty.getHypertyID()));
            //     session.execute(update);
            // }
        } else {
            if(hyperty.getStatus().equals("disconnected")){
                Statement statement = QueryBuilder.insertInto(KEYSPACE, table)
                                        .value("hypertyID", hyperty.getHypertyID())
                                        .value("user", hyperty.getUserID())
                                        .value("guid", hyperty.getGuid())
                                        .value("descriptor", hyperty.getDescriptor())
                                        .value("resources", hyperty.getResources())
                                        .value("dataSchemes", hyperty.getDataSchemes())
                                        .value("startingTime", hyperty.getStartingTime())
                                        .value("lastModified", hyperty.getLastModified())
                                        .value("runtime", hyperty.getRuntime())
                                        .value("p2pRequester", hyperty.getRequester())
                                        .value("p2pHandler", hyperty.getHandler())
                                        .value("expires", hyperty.getExpires())
                                        .value("status", hyperty.getStatus());
                session.execute(statement);
            }
        }
    }

    public HypertyInstance getUpdatedHyperty(HypertyInstance hyperty, String table){
        Statement select = QueryBuilder.select().all().from(KEYSPACE, table)
                                                      .where(QueryBuilder.eq("hypertyID", hyperty.getHypertyID()));

        ResultSet results = session.execute(select);
        Row row = results.one();
        HypertyInstance newHyperty =  new HypertyInstance(row.getString("descriptor"), row.getString("startingTime"),
                  row.getString("user"), row.getList("resources", String.class), row.getList("dataSchemes", String.class),
                  row.getString("runtime"), row.getString("p2pRequester"), row.getString("p2pHandler"),
                  row.getString("lastModified"), row.getInt("expires"), row.getString("status"), row.getString("guid"));

        newHyperty.setHypertyID(hyperty.getHypertyID());
        return newHyperty;
    }

    private boolean updatedHypertyExists(HypertyInstance hyperty, String table){
        Statement select = QueryBuilder.select().all().from(KEYSPACE, table)
            .where(QueryBuilder.eq("hypertyID", hyperty.getHypertyID()));

        ResultSet results = session.execute(select);
        Row row = results.one();
        return row != null;
    }

    public void insertHyperty(HypertyInstance hyperty){
        insertStatement(hyperty, USERHYPERTIES);
        insertStatement(hyperty, IDHYPERTIES);
        insertGuid(hyperty, GUIDBYUSER);
        insertEmail(getUserEmail(hyperty.getUserID()), hyperty.getHypertyID(), EMAILBYUSER);
    }

    private String getUserEmail(String userID){
        String[] userIdSplitted = userID.split("/");
        return userIdSplitted[userIdSplitted.length - 1];
    }

    private void insertEmail(String email, String hypertyId, String table){
        Statement statement;

        if(emailExists(email)){
            statement = QueryBuilder.update(KEYSPACE, table)
                .with(QueryBuilder.add("hyperties_ids", hypertyId))
                .where(QueryBuilder.eq("email", email));
        }
        else{
            Set<String> hyperties_ids = new HashSet<String>();
            hyperties_ids.add(hypertyId);

            statement = QueryBuilder.insertInto(KEYSPACE, table)
                .value("email", email)
                .value("hyperties_ids", hyperties_ids);
        }

        if(getSession() != null){
            getSession().execute(statement);
        }

        else log.error("Invalid cassandra session.");
    }

    private boolean emailExists(String email){
        Statement select = QueryBuilder.select().all().from(KEYSPACE, EMAILBYUSER)
            .where(QueryBuilder.eq("email", email));

        ResultSet results = session.execute(select);
        Row row = results.one();
        return row != null;
    }

    private void insertGuid(HypertyInstance hyperty, String table){
        Statement statement = QueryBuilder.insertInto(KEYSPACE, table)
            .value("guid", hyperty.getGuid())
            .value("user", hyperty.getUserID());

        if(getSession() != null){
            getSession().execute(statement);
        }
        else log.error("Invalid cassandra session.");
    }

    private void insertStatement(HypertyInstance hyperty, String table){
        Statement statement = QueryBuilder.insertInto(KEYSPACE, table)
            .value("hypertyID", hyperty.getHypertyID())
            .value("user", hyperty.getUserID())
            .value("guid", hyperty.getGuid())
            .value("descriptor", hyperty.getDescriptor())
            .value("resources", hyperty.getResources())
            .value("dataSchemes", hyperty.getDataSchemes())
            .value("startingTime", hyperty.getStartingTime())
            .value("lastModified", hyperty.getLastModified())
            .value("runtime", hyperty.getRuntime())
            .value("p2pRequester", hyperty.getRequester())
            .value("p2pHandler", hyperty.getHandler())
            .value("expires", hyperty.getExpires())
            .value("status", hyperty.getStatus());

        if(getSession() != null){
            getSession().execute(statement);
        }
        else log.error("Invalid cassandra session.");
    }

    public void insertDataObject(DataObjectInstance dataObject){
        insertStatementDataObjects(dataObject, NAMEDATAOBJECTS);
        insertStatementDataObjects(dataObject, REPORTERDATAOBJECTS);
        insertStatementDataObjects(dataObject, URLDATAOBJECTS);
    }

    public void insertStatementDataObjects(DataObjectInstance dataObject, String table){
        String dataObjectName = dataObject.getName();
        Statement statement = QueryBuilder.insertInto(KEYSPACE, table)
            .value("name", dataObjectName)
            .value("schem", dataObject.getSchema())
            .value("resources", dataObject.getResources())
            .value("dataSchemes", dataObject.getDataSchemes())
            .value("startingTime", dataObject.getStartingTime())
            .value("lastModified", dataObject.getLastModified())
            .value("runtime", dataObject.getRuntime())
            .value("p2pRequester", dataObject.getRequester())
            .value("reporter", dataObject.getReporter())
            .value("url", dataObject.getUrl())
            .value("status", dataObject.getStatus())
            .value("expires", dataObject.getExpires());

        if(getSession() != null){
            getSession().execute(statement);
        }
        else log.error("Invalid cassandra session.");
    }

    public ArrayList<HypertyInstance> getHypertiesByEmail(String email){
        Statement select = QueryBuilder.select().column("hyperties_ids").from(KEYSPACE, EMAILBYUSER)
            .where(QueryBuilder.eq("email", email));

        ResultSet results = session.execute(select);

        Set<String> hypertiesUrls = new HashSet<String>();

        for(Row row : results){
            hypertiesUrls = row.getSet("hyperties_ids", String.class);
        }

        ArrayList<HypertyInstance> hyperties = new ArrayList<HypertyInstance>();

        for(String hypertyId : hypertiesUrls){
            HypertyInstance hyperty = getHyperty(hypertyId);
            hyperty.setHypertyID(hypertyId);
            hyperties.add(hyperty);
        }

        return hyperties;
    }

    public int getNumberOfHyperties(){
        ArrayList<String> data = new ArrayList<String>();

        Statement select = QueryBuilder.select().column("hypertyID").from(KEYSPACE, IDHYPERTIES);
        ResultSet results = session.execute(select);

        if(results == null) return 0;

        for (Row row : results){
            String hyperty = row.getString("hypertyID");
            if(!data.contains(hyperty)){
                data.add(hyperty);
            }
        }
        return data.size();
    }

    public ArrayList<String> getAllUsers(){
        ArrayList<String> data = new ArrayList<String>();

        Statement select = QueryBuilder.select().column("user").from(KEYSPACE, USERHYPERTIES);
        ResultSet results = session.execute(select);

        if(results == null) return new ArrayList();

        for (Row row : results){
            String user = row.getString("user");
            if(!data.contains(user)){
                data.add(user);
            }
        }
        return data;
    }


    public ArrayList<String> getAllDataObjects(){
        ArrayList<String> data = new ArrayList<String>();

        Statement select = QueryBuilder.select().column("url").from(KEYSPACE, URLDATAOBJECTS);
        ResultSet results = session.execute(select);

        if(results == null) return new ArrayList();

        for (Row row : results){
            String dataObjectUrl = row.getString("url");
            if(!data.contains(dataObjectUrl)){
                data.add(dataObjectUrl);
            }
        }
        return data;
    }

    public HypertyInstance getHyperty(String hypertyID){
        Statement select = QueryBuilder.select().all().from(KEYSPACE, IDHYPERTIES)
                                                      .where(QueryBuilder.eq("hypertyID", hypertyID));
        ResultSet results = session.execute(select);
        Row row = results.one();
        HypertyInstance newHyperty =  new HypertyInstance(row.getString("descriptor"), row.getString("startingTime"),
                row.getString("user"), row.getList("resources", String.class), row.getList("dataSchemes", String.class),
                row.getString("runtime"), row.getString("p2pRequester"), row.getString("p2pHandler"),
                row.getString("lastModified"), row.getInt("expires"), row.getString("status"), row.getString("guid"));

        newHyperty.setHypertyID(hypertyID);
        return newHyperty;
    }

    public Map<String, HypertyInstance> getHypertiesByGuid(String guid){
        String userId = getUserByGuid(guid);
        return getUserHyperties(userId);
    }

    public String getUserByGuid(String guid){
        Statement select = QueryBuilder.select().all().from(KEYSPACE, GUIDBYUSER)
                                                      .where(QueryBuilder.eq("guid", guid));
        ResultSet results = session.execute(select);
        Row row = results.one();
        return row.getString("user");
    }

    public boolean guidExists(String guid){
        Statement select = QueryBuilder.select().all().from(KEYSPACE, GUIDBYUSER)
                                                      .where(QueryBuilder.eq("guid", guid));

        ResultSet results = session.execute(select);
        Row row = results.one();
        return row != null;
    }

    public int getNumUsersWithHyperties(){
        ArrayList<String> data = new ArrayList<String>();

        Statement select = QueryBuilder.select().column("guid").from(KEYSPACE, GUIDBYUSER);
        ResultSet results = session.execute(select);

        if(results == null) return 0;

        for (Row row : results){
            String hyperty = row.getString("guid");
            if(!data.contains(hyperty)){
                data.add(hyperty);
            }
        }
        return data.size();
    }

    public Map<String, String> getMapUsersByGuid(){
        Map<String, String> usersByGuid = new HashMap();

        Statement select = QueryBuilder.select().all().from(KEYSPACE, GUIDBYUSER);
        ResultSet results = session.execute(select);

        if(results == null) return Collections.emptyMap();

        for(Row row : results){
            usersByGuid.put(row.getString("guid"), row.getString("user"));
        }

        return usersByGuid;
    }

    public DataObjectInstance getDataObjectByUrl(String dataObjectUrl){
        Statement select = QueryBuilder.select().all().from(KEYSPACE, URLDATAOBJECTS)
                                                      .where(QueryBuilder.eq("url", dataObjectUrl));
        ResultSet results = session.execute(select);
        Row row = results.one();
        return new DataObjectInstance(row.getString("name"), row.getString("schem"), row.getList("dataSchemes", String.class),
                row.getList("resources", String.class), row.getString("reporter"), row.getString("url"), row.getString("startingTime"),
                row.getString("lastModified"), row.getString("status"), row.getInt("expires"), row.getString("runtime"), row.getString("p2pRequester"));
    }

    public boolean hypertyExists(String hypertyID){
        Statement select = QueryBuilder.select().all().from(KEYSPACE, IDHYPERTIES)
                                                      .where(QueryBuilder.eq("hypertyID", hypertyID));

        ResultSet results = session.execute(select);
        Row row = results.one();
        return row != null;
    }

    public boolean dataObjectExists(String dataObjectUrl){
        Statement select = QueryBuilder.select().all().from(KEYSPACE, URLDATAOBJECTS)
                                                      .where(QueryBuilder.eq("url", dataObjectUrl));

        ResultSet results = session.execute(select);
        Row row = results.one();
        return row != null;
    }

    public boolean userExists(String userID){
        Statement select = QueryBuilder.select().all().from(KEYSPACE, USERHYPERTIES)
                                                      .where(QueryBuilder.eq("user", userID));
        ResultSet results = session.execute(select);
        Row row = results.one();
        return row != null;
    }

    public void updateHyperty(HypertyInstance hyperty){
        updateTableIDs(hyperty, IDHYPERTIES);
        updateTableUsers(hyperty, USERHYPERTIES);
        updateTableUpdatedHyperties(hyperty, UPDATEDHYPERTIES);
    }

    private void updateTableIDs(HypertyInstance hyperty, String table){
        Statement update = QueryBuilder.update(KEYSPACE, table)
                                       .with(QueryBuilder.set("descriptor", hyperty.getDescriptor()))
                                       .and(QueryBuilder.set("lastModified", hyperty.getLastModified()))
                                       .and(QueryBuilder.set("resources", hyperty.getResources()))
                                       .and(QueryBuilder.set("dataSchemes", hyperty.getDataSchemes()))
                                       .and(QueryBuilder.set("expires", hyperty.getExpires()))
                                       .and(QueryBuilder.set("runtime", hyperty.getRuntime()))
                                       .and(QueryBuilder.set("p2pRequester", hyperty.getRequester()))
                                       .and(QueryBuilder.set("p2pHandler", hyperty.getHandler()))
                                       .and(QueryBuilder.set("status", hyperty.getStatus()))
                                       .and(QueryBuilder.set("guid", hyperty.getGuid()))
                                       .where(QueryBuilder.eq("hypertyID", hyperty.getHypertyID()));
        if(getSession() != null){
            getSession().execute(update);
        }
        else log.error("Invalid cassandra session.");
    }

    private void updateTableUsers(HypertyInstance hyperty, String table){
        Statement update = QueryBuilder.update(KEYSPACE, table)
                                       .with(QueryBuilder.set("descriptor", hyperty.getDescriptor()))
                                       .and(QueryBuilder.set("lastModified", hyperty.getLastModified()))
                                       .and(QueryBuilder.set("resources", hyperty.getResources()))
                                       .and(QueryBuilder.set("dataSchemes", hyperty.getDataSchemes()))
                                       .and(QueryBuilder.set("expires", hyperty.getExpires()))
                                       .and(QueryBuilder.set("runtime", hyperty.getRuntime()))
                                       .and(QueryBuilder.set("p2pRequester", hyperty.getRequester()))
                                       .and(QueryBuilder.set("p2pHandler", hyperty.getHandler()))
                                       .and(QueryBuilder.set("status", hyperty.getStatus()))
                                       .and(QueryBuilder.set("guid", hyperty.getGuid()))
                                       .where(QueryBuilder.eq("hypertyID", hyperty.getHypertyID()))
                                       .and(QueryBuilder.eq("user", hyperty.getUserID()));
        if(getSession() != null){
            getSession().execute(update);
        }
        else log.error("Invalid cassandra session.");
    }

    public Map<String, DataObjectInstance> getDataObjects(){
        Map<String, DataObjectInstance> allDataObjects = new HashMap();

        Statement select = QueryBuilder.select().all().from(KEYSPACE, URLDATAOBJECTS);

        ResultSet results = session.execute(select);

        if(results == null) return Collections.emptyMap();

        for(Row row : results){
            allDataObjects.put(row.getString("url"), new DataObjectInstance(row.getString("name"), row.getString("schem"), row.getList("dataSchemes", String.class),
                        row.getList("resources", String.class), row.getString("reporter"), row.getString("url"),
                        row.getString("startingTime"), row.getString("lastModified"), row.getString("status"), row.getInt("expires"),
                        row.getString("runtime"), row.getString("p2pRequester")));
        }
        return allDataObjects;
    }

    public Map<String, HypertyInstance> getUserHyperties(String userID){
        Map<String, HypertyInstance> allUserHyperties = new HashMap();

        Statement select = QueryBuilder.select().all().from(KEYSPACE, USERHYPERTIES)
                                                      .where(QueryBuilder.eq("user", userID));
        ResultSet results = session.execute(select);

        if(results == null) return Collections.emptyMap();

        for(Row row : results){
             HypertyInstance hyperty = new HypertyInstance(row.getString("descriptor"),
                                                         row.getList("resources", String.class),
                                                         row.getList("dataSchemes", String.class),
                                                         row.getString("startingTime"),
                                                         row.getString("lastModified"),
                                                         row.getInt("expires"),
                                                         row.getString("status"),
                                                         row.getString("p2pRequester"),
                                                         row.getString("p2pHandler"),
                                                         row.getString("runtime"),
                                                         row.getString("guid"));
             hyperty.setHypertyID(row.getString("hypertyID"));
             allUserHyperties.put(row.getString("hypertyID"), hyperty);
        }

        return allUserHyperties;
    }

    public Map<String, DataObjectInstance> getDataObjectsByHyperty(String hypertyReporter){
        Map<String, DataObjectInstance> allHypertyDataObjects = new HashMap();

        Statement select = QueryBuilder.select().all().from(KEYSPACE, REPORTERDATAOBJECTS)
                                                      .where(QueryBuilder.eq("reporter", hypertyReporter));
        ResultSet results = session.execute(select);

        if(results == null) return Collections.emptyMap();

        for(Row row : results){
            allHypertyDataObjects.put(row.getString("url"), new DataObjectInstance(row.getString("name"), row.getString("schem"), row.getList("dataSchemes", String.class),
                                                                              row.getList("resources", String.class), row.getString("reporter"), row.getString("url"),
                                                                              row.getString("startingTime"), row.getString("lastModified"), row.getString("status"), row.getInt("expires"),
                                                                              row.getString("runtime"), row.getString("p2pRequester")));
        }
        return allHypertyDataObjects;
    }

    public Map<String, DataObjectInstance> getDataObjectsByName(String dataObjectName){
        Map<String, DataObjectInstance> foundDataObjects = new HashMap();

        Statement select = QueryBuilder.select().all().from(KEYSPACE, NAMEDATAOBJECTS)
                                                      .where(QueryBuilder.eq("name", dataObjectName));
        ResultSet results = session.execute(select);

        if(results == null) return Collections.emptyMap();

        for(Row row : results){
            foundDataObjects.put(row.getString("url"), new DataObjectInstance(row.getString("name"), row.getString("schem"), row.getList("dataSchemes", String.class),
                                                                              row.getList("resources", String.class), row.getString("reporter"), row.getString("url"),
                                                                              row.getString("startingTime"), row.getString("lastModified"), row.getString("status"), row.getInt("expires"),
                                                                              row.getString("runtime"), row.getString("p2pRequester")));
        }
        return foundDataObjects;
    }

    public void deleteUserHyperty(String hypertyID){
        HypertyInstance hyperty = getHyperty(hypertyID);

        String oldStatus = hyperty.getStatus();
        hyperty.setStatus(DEAD);
        hyperty.setHypertyID(hypertyID);
        String newStatus = hyperty.getStatus();

        updateHyperty(hyperty);

        // log.info("Deleted from database hyperty with ID: " + hypertyID);

        // Statement deleteFromID = QueryBuilder.delete().from(KEYSPACE, IDHYPERTIES)
        //                                               .where(QueryBuilder.eq("hypertyID", hypertyID));
        //
        // Statement deleteFromUsers = QueryBuilder.delete().from(KEYSPACE, USERHYPERTIES)
        //                                               .where(QueryBuilder.eq("user", hyperty.getUserID()))
        //                                               .and(QueryBuilder.eq("hypertyid", hypertyID));
        //
        // getSession().execute(deleteFromID);
        // getSession().execute(deleteFromUsers);
    }

    public void deleteDataObject(String dataObjectUrl){
        DataObjectInstance dataObject = getDataObjectByUrl(dataObjectUrl);
        String oldStatus = dataObject.getStatus();

        dataObject.setStatus(DEAD);
        dataObject.setUrl(dataObjectUrl);

        String newStatus = dataObject.getStatus();

        insertDataObject(dataObject);

        // Statement deleteFromUrls = QueryBuilder.delete().from(KEYSPACE, URLDATAOBJECTS)
        //                                         .where(QueryBuilder.eq("url", dataObjectUrl));
        //
        //
        // Statement deleteFromNames = QueryBuilder.delete().from(KEYSPACE, NAMEDATAOBJECTS)
        //                                         .where(QueryBuilder.eq("name", dataObject.getName()))
        //                                         .and(QueryBuilder.eq("url", dataObjectUrl));
        //
        // Statement deleteFromReporters = QueryBuilder.delete().from(KEYSPACE, REPORTERDATAOBJECTS)
        //                                         .where(QueryBuilder.eq("reporter", dataObject.getReporter()))
        //                                         .and(QueryBuilder.eq("url", dataObjectUrl));
        //
        // getSession().execute(deleteFromReporters);
        // getSession().execute(deleteFromUrls);
        // getSession().execute(deleteFromNames);
        // log.info("Deleted from database dataobject with name: " + dataObjectUrl);
    }

    public Session getSession(){
        return this.session;
    }

    public int getClusterSize(){
        Metadata metadata = this.cluster.getMetadata();
        return metadata.getAllHosts().size();
    }

    public int getNumLiveNodes(){
        int numLiveNodes = getClusterSize();
        for(Host h : this.cluster.getMetadata().getAllHosts()){
            if(h.getState().equals(DOWN)){
                numLiveNodes--;
            }
        }
        return numLiveNodes;
    }

    public long getNumRequestsPerformed(){
        return this.cluster.getMetrics().getRequestsTimer().getCount();
    }

    public void close(){
        cluster.close();
    }
}
