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

    public static final String KEYSPACE  = "rethinkeyspace";
    public static final String IDHYPERTIES = "hyperties_by_id";
    public static final String USERHYPERTIES = "hyperties_by_user";
    public static final String DATAOBJECTS = "data_objects";
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

    public void insertHyperty(HypertyInstance hyperty){
        insertStatement(hyperty, USERHYPERTIES);
        insertStatement(hyperty, IDHYPERTIES);
    }

    private void insertStatement(HypertyInstance hyperty, String table){
        Statement statement = QueryBuilder.insertInto(KEYSPACE, table)
            .value("hypertyID", hyperty.getHypertyID())
            .value("user", hyperty.getUserID())
            .value("descriptor", hyperty.getDescriptor())
            .value("resources", hyperty.getResources())
            .value("dataSchemes", hyperty.getDataSchemes())
            .value("startingTime", hyperty.getStartingTime())
            .value("lastModified", hyperty.getLastModified())
            .value("expires", hyperty.getExpires());

        if(getSession() != null){
            getSession().execute(statement);
            log.info("Inserted in database hyperty with ID: " + hyperty.getHypertyID() + " from user " + hyperty.getUserID());
        }
        else log.error("Invalid cassandra session.");
    }

    public void insertDataObject(DataObjectInstance dataObject){
        String dataObjectName = dataObject.getName();
        Statement statement = QueryBuilder.insertInto(KEYSPACE, DATAOBJECTS)
            .value("name", dataObjectName)
            .value("schem", dataObject.getSchema())
            .value("startingTime", dataObject.getStartingTime())
            .value("lastModified", dataObject.getLastModified())
            .value("reporter", dataObject.getReporter())
            .value("url", dataObject.getUrl());

        if(getSession() != null){
            getSession().execute(statement);
            log.info("Inserted in database data object with name: " + dataObjectName);
        }
        else log.error("Invalid cassandra session.");

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

    public HypertyInstance getHyperty(String hypertyID){
        Statement select = QueryBuilder.select().all().from(KEYSPACE, IDHYPERTIES)
                                                      .where(QueryBuilder.eq("hypertyID", hypertyID));
        ResultSet results = session.execute(select);
        Row row = results.one();
        return new HypertyInstance(row.getString("descriptor"), row.getString("startingTime"),
                row.getString("user"), row.getList("resources", String.class), row.getList("dataSchemes", String.class),
                row.getString("lastModified"), row.getInt("expires"));

    }

    public DataObjectInstance getDataObject(String dataObjectName){
        Statement select = QueryBuilder.select().all().from(KEYSPACE, DATAOBJECTS)
                                                      .where(QueryBuilder.eq("name", dataObjectName));
        ResultSet results = session.execute(select);
        Row row = results.one();
        return new DataObjectInstance(row.getString("name"), row.getString("schem"),
                row.getString("reporter"), row.getString("url"), row.getString("startingTime"), row.getString("lastModified"));
    }

    public boolean hypertyExists(String hypertyID){
        Statement select = QueryBuilder.select().all().from(KEYSPACE, IDHYPERTIES)
                                                      .where(QueryBuilder.eq("hypertyID", hypertyID));

        ResultSet results = session.execute(select);
        Row row = results.one();
        return row != null;
    }

    public boolean dataObjectExists(String dataObjectName){
        Statement select = QueryBuilder.select().all().from(KEYSPACE, DATAOBJECTS)
                                                      .where(QueryBuilder.eq("name", dataObjectName));

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
    }

    private void updateTableIDs(HypertyInstance hyperty, String table){
        Statement update = QueryBuilder.update(KEYSPACE, table)
                                       .with(QueryBuilder.set("descriptor", hyperty.getDescriptor()))
                                       .and(QueryBuilder.set("lastModified", hyperty.getLastModified()))
                                       .and(QueryBuilder.set("resources", hyperty.getResources()))
                                       .and(QueryBuilder.set("dataSchemes", hyperty.getDataSchemes()))
                                       .and(QueryBuilder.set("expires", hyperty.getExpires()))
                                       .where(QueryBuilder.eq("hypertyID", hyperty.getHypertyID()));
        if(getSession() != null){
            getSession().execute(update);
            log.info("Updated in database hyperty with ID: " + hyperty.getHypertyID() + " from user " + hyperty.getUserID());
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
                                       .where(QueryBuilder.eq("hypertyID", hyperty.getHypertyID()))
                                       .and(QueryBuilder.eq("user", hyperty.getUserID()));
        if(getSession() != null){
            getSession().execute(update);
            log.info("Updated in database hyperty with ID: " + hyperty.getHypertyID() + " from user " + hyperty.getUserID());
        }
        else log.error("Invalid cassandra session.");
    }

    public Map<String, HypertyInstance> getUserHyperties(String userID){
        log.info("Requested hyperties from user: " + userID);
        Map<String, HypertyInstance> allUserHyperties = new HashMap();

        Statement select = QueryBuilder.select().all().from(KEYSPACE, USERHYPERTIES)
                                                      .where(QueryBuilder.eq("user", userID));
        ResultSet results = session.execute(select);

        if(results == null) return Collections.emptyMap();

        for(Row row : results){
            allUserHyperties.put(row.getString("hypertyID"), new HypertyInstance(row.getString("descriptor"),
                                                                                 row.getList("resources", String.class),
                                                                                 row.getList("dataSchemes", String.class),
                                                                                 row.getString("startingTime"),
                                                                                 row.getString("lastModified"),
                                                                                 row.getInt("expires")));
        }
        return allUserHyperties;
    }

    public void deleteUserHyperty(String hypertyID){
        HypertyInstance hyperty = getHyperty(hypertyID);

        Statement deleteFromID = QueryBuilder.delete().from(KEYSPACE, IDHYPERTIES)
                                                      .where(QueryBuilder.eq("hypertyID", hypertyID));

        Statement deleteFromUsers = QueryBuilder.delete().from(KEYSPACE, USERHYPERTIES)
                                                      .where(QueryBuilder.eq("user", hyperty.getUserID()))
                                                      .and(QueryBuilder.eq("hypertyid", hypertyID));

        getSession().execute(deleteFromID);
        getSession().execute(deleteFromUsers);
        log.info("Deleted from database hyperty with ID: " + hypertyID);
    }

    public void deleteDataObject(String dataObjectName){
        Statement delete = QueryBuilder.delete().from(KEYSPACE, DATAOBJECTS)
                                                .where(QueryBuilder.eq("name", dataObjectName));
        getSession().execute(delete);
        log.info("Deleted from database dataobject with name: " + dataObjectName);
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
