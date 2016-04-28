/**
 *  * Copyright 2015-2016 INESC-ID
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 **/

package domainregistry;

import static spark.Spark.*;
import java.util.*;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.log4j.Logger;

public class HypertyController {
    static Logger log = Logger.getLogger(HypertyController.class.getName());

    public static final int ALL_HYPERTIES_PATH_SIZE = 6;
    public static final int SPECIFIC_HYPERTIES_PATH_SIZE = 7;

    public HypertyController(StatusService status, final HypertyService hypertyService, final Connection connectionClient, final DataObjectService dataObjectService) {

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

            if(encodedURL.length == ALL_HYPERTIES_PATH_SIZE){
                String userID = decodeUrl(encodedURL[encodedURL.length - 1]);
                Map<String, HypertyInstance> userHyperties = hypertyService.getAllHyperties(connectionClient, userID);
                res.status(200);
                return gson.toJson(userHyperties);
            }

            Set<String> queryParams = req.queryParams();

            if(queryParams.isEmpty()){
                res.status(404);
                return gson.toJson(new Messages("URL malformed. A query string is needed."));
            }

            if(!validateQueryParams(queryParams)){
                res.status(400);
                return gson.toJson(new Messages("URL malformed."));
            }

            Map<String, String> allParameters = new HashMap();

            for(String type : queryParams){
                allParameters.put(type, req.queryParams(type));
            }

            String userID = decodeUrl(encodedURL[encodedURL.length - 2]);
            Map<String, HypertyInstance> userHyperties = hypertyService.getSpecificHyperties(connectionClient, userID, allParameters);
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

        put("hyperty/dataobject/:name", (req, res) -> {
            res.type("application/json");
            String body = req.body();
            String dataObjectName = req.params(":name");
            DataObjectInstance dataObject = gson.fromJson(body, DataObjectInstance.class);
            dataObject.setName(dataObjectName);
            dataObjectService.createDataObject(connectionClient, dataObject);
            res.status(200);
            return gson.toJson(new Messages("Data object created"));
        });

        get("hyperty/dataobject/:name", (req, res) -> {
            res.type("application/json");
            String dataObjectName = req.params(":name");
            DataObjectInstance dataObject = dataObjectService.getDataObject(connectionClient, dataObjectName);
            res.status(200);
            return gson.toJson(dataObject);
        });

        delete("/hyperty/dataobject/:name", (req, res) -> {
            res.type("application/json");
            String dataObjectName = req.params(":name");
            dataObjectService.deleteDataObject(connectionClient, dataObjectName);
            res.status(200);
            return gson.toJson(new Messages("Data object deleted"));
        });

        get("/throwexception", (request, response) -> {
            throw new DataNotFoundException();
        });

        exception(DataNotFoundException.class, (e, req, res) -> {
            res.status(404);
            res.body(gson.toJson(new Messages("Data not found")));
        });

        get("/throwexception", (request, response) -> {
            throw new HypertiesNotFoundException();
        });

        exception(HypertiesNotFoundException.class, (e, req, res) -> {
            res.status(404);
            res.body(gson.toJson(new Messages("Hyperties not found.")));
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

    private boolean validateQueryParams(Set<String> params){
        if(params.size() == 1){
            return params.contains("dataSchemes") || params.contains("resources");
        }

        else if(params.size() == 2){
            return params.contains("dataSchemes") && params.contains("resources");
        }

        else return false;
    }
}
