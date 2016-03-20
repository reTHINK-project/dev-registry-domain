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

import static spark.Spark.*;
import com.datastax.driver.core.*;
import org.apache.log4j.Logger;
import java.util.*;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class StatusService {

    private Map<String, String> cassandraStats = new HashMap();

    public Map<String, String> getCassandraStats(CassandraClient cassandra){
        cassandraSession(cassandra);
        cassandraClusterSize(cassandra);
        return this.cassandraStats;
    }

    private void cassandraSession(CassandraClient cassandra){
        if(cassandra.getSession() != null)
            cassandraStats.put("Database connection", "up");

        else cassandraStats.put("Database connection", "down");
    }

    private void cassandraClusterSize(CassandraClient cassandra){
        cassandraStats.put("Database cluster size", String.valueOf(cassandra.getClusterSize()));
    }
}
