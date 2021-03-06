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
import java.util.*;
import org.apache.log4j.Logger;

public class RiemannCommunicator {
    static Logger log = Logger.getLogger(RiemannCommunicator.class.getName());

    private static final String RUNNING = "running";
    private static final int PORT = 5555;

    private RiemannClient riemannClient;

    public RiemannCommunicator(){
        setRiemannClient();
    }

    public void send(String service, String tag, double metric){
        try {
            this.riemannClient.event().
                service(service).
                state(RUNNING).
                metric(metric).
                tags(tag).
                send();

        } catch (Exception e){
            this.riemannClient.close();
            log.error("Could not send event to riemann. Reconnecting...");
            setRiemannClient();
        }
    }


    private void setRiemannClient(){
        String address = Addresses.getRiemannServerName();
        try{
            this.riemannClient = RiemannClient.tcp(address, PORT);
            this.riemannClient.connect();
            log.info("Connection to a riemann client has succeeded");
        } catch(Exception e){
            log.error("Could not connect to a riemann server. Is " + address + " running?");
        }
    }
}
