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

import static java.lang.System.out;

public class CassandraClient{
    private Cluster cluster;
    private Session session;

    public void connect(Collection<InetAddress> addresses){
        this.cluster = Cluster.builder()
            .addContactPoints(addresses)
            .withRetryPolicy(DefaultRetryPolicy.INSTANCE)
            .withLoadBalancingPolicy(new TokenAwarePolicy(new RoundRobinPolicy()))
            .build();

        try{
            session = cluster.connect("rethink");
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

    public Session getSession(){
        return this.session;
    }

    public void close(){
        cluster.close();
    }
}
