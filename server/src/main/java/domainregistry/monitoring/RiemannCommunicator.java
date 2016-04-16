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

import com.aphyr.riemann.client.RiemannClient;
import org.apache.log4j.Logger;

public class RiemannCommunicator {
    static Logger log = Logger.getLogger(RiemannCommunicator.class.getName());

    private static final String RUNNING = "running";
    private static final int PORT = 5555;
    private static final int FIVE_SECONDS = 5000;

    public static void send(String service, String tag, double metric){
        try {
            RiemannClient client = getRiemannClient();
            client.connect();
            client.event().
                service(service).
                state(RUNNING).
                metric(metric).
                tags(tag).
                send();

            client.close();
        } catch (Exception e){
            log.error("Could not send event to riemann");
        }
    }

    private static RiemannClient getRiemannClient() throws Exception {
        String address = Addresses.getRiemannServerName();
        return RiemannClient.tcp(address, PORT);
    }

}
