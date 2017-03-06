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

import java.net.*;
import java.util.*;
import org.apache.log4j.Logger;

public class Main {
    static Logger log = Logger.getLogger(Main.class.getName());

    private static final String CASSANDRA = "Cassandra";
    private static final String RAM = "Ram";
    private static final String STORAGE = "STORAGE_TYPE";
    private static final String EXPIRES = "EXPIRES";
    private static final String RIEMANN = "RIEMANN_SERVER";

    public static void main(String[] args) {
        String storageType = System.getenv(STORAGE);
        String expires = System.getenv(EXPIRES);
        long time = Long.valueOf(expires).longValue();

        if(storageType.equals("CASSANDRA")){
            log.info("Cassandra choosen. Requests will be saved in a Cassandra db cluster");

            Collection<InetAddress> clusterContactPoinsts = Addresses.getClusterContactPointsAddresses();
            final CassandraClient cassandraClient = new CassandraClient();

            if (clusterContactPoinsts.isEmpty()){
                log.error("No contact points provided. Program will exit.");
                return;
            }
            else ((CassandraClient) cassandraClient).connect(clusterContactPoinsts);

            HypertyService hypertyService = new HypertyService();
            DataObjectService dataObjectService = new DataObjectService();
            StatusService status = new StatusService(CASSANDRA, cassandraClient, hypertyService, dataObjectService);
            HypertyController controller = new HypertyController(status, hypertyService, cassandraClient, dataObjectService);
            new HeartBeatThread(hypertyService, dataObjectService, cassandraClient, time).start();

            if(System.getenv(RIEMANN) != null){
                log.info("Riemann env variable was set. Events will begin to be sent to " + System.getenv(RIEMANN));
                RiemannCommunicator riemann = new RiemannCommunicator();
                new MetricsThread(controller, cassandraClient, riemann).start();
            }
        }

        if(storageType.equals("RAM")){
            log.info("RAM choosen. Requests will be saved in-memory");
            final Connection ramClient = new RamClient();
            HypertyService hypertyService = new HypertyService();
            DataObjectService dataObjectService = new DataObjectService();
            StatusService status = new StatusService(RAM, ramClient, hypertyService, dataObjectService);
            HypertyController controller = new HypertyController(status, hypertyService, ramClient, dataObjectService);
            new HeartBeatThread(hypertyService, dataObjectService, ramClient, time).start();
        }
    }
}

