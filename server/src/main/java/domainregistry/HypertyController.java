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
import org.apache.log4j.Logger;
// import java.util.*;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class HypertyController {

    static Logger log = Logger.getLogger(HypertyController.class.getName());

    public HypertyController(final HypertyService hypertyService, final CassandraClient cassandra) {

        Gson gson = new Gson();

        get("/live", (req, res) -> {
            log.info("Live page requested. Statistics on the way...");
            res.type("application/json");
            Map<String, String> databaseStats = status.getDomainRegistryStats();
            res.status(200);
            return gson.toJson(databaseStats);
        });

        get("/hyperty/user/*", (req,res) -> {
            res.type("application/json");
            String[] encodedURL = req.url().split("/");
            String userID = decodeUrl(encodedURL[encodedURL.length - 1]);
            Map<String, HypertyInstance> userHyperties = hypertyService.getAllHyperties(connectionClient, userID);
            res.status(200);
            return gson.toJson(userHyperties);
        });

        put("/hyperty/user/*", (req,res) -> {
            res.type("application/json");
            String body = req.body();
            String[] encodedURL = req.url().split("/");
            String userID = decodeUrl(encodedURL[encodedURL.length - 2]);
            String hypertyID = decodeUrl(encodedURL[encodedURL.length - 1]);
            HypertyInstance hyperty = gson.fromJson(body, HypertyInstance.class);
            log.info("Received request for hyperty with ID: " +
                hypertyID + " descriptor: " + hyperty.getDescriptor() + " expires " + hyperty.getExpires());
            hypertyService.createUserHyperty(cassandra, userID, hypertyID, hyperty);
            res.status(200);
            log.info("Created and stored hyperty with ID: " + hypertyID);
            return gson.toJson(new Messages("Hyperty created"));
        });

        // delete("/hyperty/user#<{(|", (req,res) -> {
        //     res.type("application/json");
        //     String[] encodedURL = req.url().split("/");
        //     String userID    = decodeUrl(encodedURL[encodedURL.length - 2]);
        //     String hypertyID = decodeUrl(encodedURL[encodedURL.length - 1]);
        //     hypertyService.deleteUserHyperty(userID, hypertyID);
        //     res.status(200);
        //     log.info("Deleted hyperty with ID: " + hypertyID);
        //     return gson.toJson(new Messages("Hyperty deleted"));
        // });

        get("/throwexception", (request, response) -> {
            throw new DataNotFoundException();
        });

        exception(DataNotFoundException.class, (e, req, res) -> {
            res.status(404);
            res.body(gson.toJson(new Messages("Data not found")));
        });

        get("/throwexception", (request, response) -> {
            throw new UserNotFoundException();
        });

        exception(UserNotFoundException.class, (e, req, res) -> {
            res.status(404);
            res.body(gson.toJson(new Messages("User not found")));
        });

    }

    private static String decodeUrl(String url) throws java.io.UnsupportedEncodingException {
        return java.net.URLDecoder.decode(url, "UTF-8");
    }
}
