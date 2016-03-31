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
import static java.lang.System.out;
import java.util.*;
import java.lang.System;
import org.apache.log4j.Logger;
import java.net.*;
import java.io.IOException;

public class Addresses{
    static Logger log = Logger.getLogger(Addresses.class.getName());

    private static final String CONTACT_POINTS = "CONTACT_POINTS_IPS";
    private static final String APP_SERVERS = "APP_SERVERS_IPS";
    private static final int TIMEOUT = 100;

    protected static Collection<InetAddress> getClusterContactPointsAddresses(){
        String adds = System.getenv(CONTACT_POINTS);
        return getAddresses(adds);
    }

    protected static Collection<InetAddress> getAppServersAddresses(){
        String adds = System.getenv(APP_SERVERS);
        return getAddresses(adds);
    }

    protected static boolean isHostReachable(InetAddress host){
        try{
            return host.isReachable(TIMEOUT);
        } catch (IOException e){
            log.error("Exception catched while reaching the host. A network error occured");
            return false;
        }
    }

    private static Collection<InetAddress> getAddresses(String addresses){
        Collection<InetAddress> allAddresses = new ArrayList<InetAddress>();
        if(addresses != null){
            String[] ips = addresses.split(",");
            for(String ip : ips){
                try{
                    allAddresses.add(InetAddress.getByName(ip));
                } catch(UnknownHostException e){
                    log.error("Exception catched. Unknown or malformed host ip");
                }
            }
            return allAddresses;
        }
        else return Collections.emptyList();
    }
}
