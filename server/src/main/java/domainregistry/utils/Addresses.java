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

public class Addresses{
    static Logger log = Logger.getLogger(Addresses.class.getName());

    protected static Collection<InetAddress> getClusterContactPoints(){
        Collection<InetAddress> contactPoints = new ArrayList<InetAddress>();
        String addresses = System.getenv("CONTACT_POINTS_IPS");
        if(addresses != null){
            String[] ips = addresses.split(",");
            for(String ip : ips){
                System.out.println(ip);
                try{
                    contactPoints.add(InetAddress.getByName(ip));
                } catch(UnknownHostException e){
                    log.error("Unknown or malformed host ip");
                }
            }
            return contactPoints;
        }
        else return Collections.emptyList();
    }
}
