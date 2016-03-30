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
import java.util.Map;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class HypertyController {

    public HypertyController(final HypertyService hypertyService, final Connection connectionClient) {

        Gson gson = new Gson();

        // get("/live", (req, res) -> {
        //     res.type("application/json");
        //     Map<String, String> databaseStats = status.getCassandraStats(connectionClient);
        //     res.status(200);
        //     return gson.toJson(databaseStats);
        // });

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
            hyperty.setUserID(userID);
            hyperty.setHypertyID(hypertyID);
            hypertyService.createUserHyperty(connectionClient, hyperty);
            res.status(200);
            return gson.toJson(new Messages("Hyperty created"));
        });

        delete("/hyperty/user/*", (req,res) -> {
            res.type("application/json");
            String[] encodedURL = req.url().split("/");
            String userID = decodeUrl(encodedURL[encodedURL.length - 2]);
            String hypertyID = decodeUrl(encodedURL[encodedURL.length - 1]);
            hypertyService.deleteUserHyperty(connectionClient, userID, hypertyID);
            res.status(200);
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
            throw new CouldNotRemoveHypertyException();
        });

        exception(CouldNotRemoveHypertyException.class, (e, req, res) -> {
            res.status(404);
            res.body(gson.toJson(new Messages("Could not remove hyperty")));
        });

        get("/throwexception", (request, response) -> {
            throw new UserNotFoundException();
        });

        exception(UserNotFoundException.class, (e, req, res) -> {
            res.status(404);
            res.body(gson.toJson(new Messages("User not found")));
        });

        get("/throwexception", (request, response) -> {
            throw new CouldNotCreateOrUpdateHypertyException();
        });

        exception(CouldNotCreateOrUpdateHypertyException.class, (e, req, res) -> {
            res.status(404);
            res.body(gson.toJson(new Messages("Could not create or update hyperty")));
        });
    }

    private static String decodeUrl(String url) throws java.io.UnsupportedEncodingException {
        return java.net.URLDecoder.decode(url, "UTF-8");
    }
}
