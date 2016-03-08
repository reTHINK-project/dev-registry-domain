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

public class Main {
    public static void main(String[] args) {
        final CassandraClient client = new CassandraClient();
        Collection<InetAddress> clusterContactPoinsts = Addresses.getClusterContactPoints();
        client.connect(clusterContactPoinsts);

        HypertyService service = new HypertyService();
        new HypertyController(service);
        new HeartBeatThread(service).start();
    }
}

