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

public class CassandraClient{
    private static final Logger log = LogManager.getLogger(CassandraClient.class.getName());

    public static final String KEYSPACE  = "rethink";
    public static final String HYPERTIES = "hyperties";
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

    public void insertHyperty(HypertyInstance hyperty, String hypertyID, String userID){
        Statement statement = QueryBuilder.insertInto(KEYSPACE, HYPERTIES)
            .value("hypertyID", hypertyID)
            .value("user", userID)
            .value("descriptor", hyperty.getDescriptor())
            .value("startingTime", hyperty.getStartingTime())
            .value("lastModified", hyperty.getLastModified())
            .value("expires", hyperty.getExpires());

        if(getSession() != null){
            getSession().execute(statement);
            log.info("Inserted in database hyperty with ID: " + hypertyID);
        }
        else log.error("Invalid cassandra session.");
    }

    public HypertyInstance getHyperty(String hypertyID){
        Statement select = QueryBuilder.select().all().from(KEYSPACE, HYPERTIES)
                                                      .where(QueryBuilder.eq("hypertyID", hypertyID));
        ResultSet results = session.execute(select);
        Row row = results.one();
        return new HypertyInstance(row.getString("descriptor"), row.getString("startingTime"),
                row.getString("lastModified"), row.getInt("expires"));
    }

    public boolean hypertyExists(String hypertyID){
        Statement select = QueryBuilder.select().all().from(KEYSPACE, HYPERTIES)
                                                      .where(QueryBuilder.eq("hypertyID", hypertyID));

        ResultSet results = session.execute(select);
        Row row = results.one();
        return row != null;
    }

    public void updateHyperty(String hypertyID, HypertyInstance hyperty){
        Statement update = QueryBuilder.update(KEYSPACE, HYPERTIES)
                                       .with(QueryBuilder.set("descriptor", hyperty.getDescriptor()))
                                       .and(QueryBuilder.set("lastModified", hyperty.getLastModified()))
                                       .and(QueryBuilder.set("expires", hyperty.getExpires()))
                                       .where(QueryBuilder.eq("hypertyID", hypertyID));
        if(getSession() != null){
            getSession().execute(update);
            log.info("Updated in database hyperty with ID: " + hypertyID);
        }
        else log.error("Invalid cassandra session.");
    }

    public Session getSession(){
        return this.session;
    }

    public void close(){
        cluster.close();
    }
}
