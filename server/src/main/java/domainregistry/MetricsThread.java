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

import org.apache.log4j.Logger;

class MetricsThread extends Thread{
    static Logger log = Logger.getLogger(MetricsThread.class.getName());

    private static final int TWO_SECONDS = 2000;
    HypertyController controller;
    CassandraClient cassandraClient;
    RiemannCommunicator riemann;

    public MetricsThread(HypertyController controller, CassandraClient cassandraClient, RiemannCommunicator riemann){
        this.controller = controller;
        this.cassandraClient = cassandraClient;
        this.riemann = riemann;
    }

    @Override
    public void run(){
        try{
            while(true){
                Thread.sleep(TWO_SECONDS);
                double writes = (double) controller.getNumWrites();
                double reads =  (double) controller.getNumReads();
                this.riemann.send("http get", "http", reads);
                this.riemann.send("http put", "http", writes);
                double liveNodes = (double) cassandraClient.getNumLiveNodes();
                double clusterSize = (double) cassandraClient.getClusterSize();
                this.riemann.send("cassandra live nodes", "cassandra", liveNodes);
                this.riemann.send("cassandra cluster size", "cassandra", clusterSize);
            }
        }catch(InterruptedException e){
            e.printStackTrace();
        }
    }
}
