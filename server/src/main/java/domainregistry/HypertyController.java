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

    public HypertyController(final HypertyService hypertyService) {

        Gson gson = new Gson();

        get("/", (req, res) -> gson.toJson(new Messages("rethink registry api")));

        // get("/live", (req, res) -> {
        //     int numberOfHyperties = 0;
        //     res.type("application/json");
        //     Map<String, String> stats = new HashMap();
        //     stats.put("Status", "up");
        //     stats.put("Database type", "RAM");
        //     for(String userID : hypertyService.getServices().keySet())
        //         numberOfHyperties += hypertyService.getServices().get(userID).keySet().size();
        //     stats.put("Number of hyperties", String.valueOf(numberOfHyperties));
        //     res.status(200);
        //     return gson.toJson(stats);
        // });
        get("/hyperty/user/*", (req,res) -> {
            res.type("application/json");
            String[] encodedURL = req.url().split("/");
            String userID = decodeUrl(encodedURL[encodedURL.length - 1]);
            log.info("Received request for " + userID + " hyperties");
            res.status(200);
            return gson.toJson(hypertyService.getAllHyperties(userID));
        });

        put("/hyperty/user/*", (req,res) -> {
            res.type("application/json");
            String body = req.body();
            String[] encodedURL = req.url().split("/");
            String userID    = decodeUrl(encodedURL[encodedURL.length - 2]);
            String hypertyID = decodeUrl(encodedURL[encodedURL.length - 1]);
            HypertyInstance hi = gson.fromJson(body, HypertyInstance.class);
            log.info("Received hyperty with ID: " + hypertyID + " and descriptor: " + hi.getDescriptor());
            gson.toJson(hypertyService.createUserHyperty(userID, hypertyID, hi));
            res.status(200);
            log.info("Created hyperty with ID: " + hypertyID);
            return gson.toJson(new Messages("Hyperty created"));
        });

        delete("/hyperty/user/*", (req,res) -> {
            res.type("application/json");
            String[] encodedURL = req.url().split("/");
            String userID    = decodeUrl(encodedURL[encodedURL.length - 2]);
            String hypertyID = decodeUrl(encodedURL[encodedURL.length - 1]);
            gson.toJson(hypertyService.deleteUserHyperty(userID, hypertyID));
            res.status(200);
            log.info("Deleted hyperty with ID: " + hypertyID);
            return gson.toJson(new Messages("Hyperty deleted"));
        });

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
