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
import org.json.JSONArray;
import org.json.JSONObject;

import org.apache.log4j.Logger;

import spark.ModelAndView;
import spark.template.freemarker.*;
import freemarker.cache.*;
import freemarker.template.*;

public class HypertyController {
    static Logger log = Logger.getLogger(HypertyController.class.getName());

    private static final String DEAD = "disconnected";

    private int numReads = 0;
    private int numWrites = 0;

    public static final int ALL_HYPERTIES_PATH_SIZE = 6;
    public static final int SPECIFIC_HYPERTIES_PATH_SIZE = 7;

    public static final int ALL_DO_PATH_SIZE = 7;
    public static final int SPECIFIC_DO_PATH_SIZE = 8;


    private static final String KEYSTORE = "KEYSTORE";
    private static final String KEYSTORE_PASSWORD = "KEYSTORE_PASSWORD";

    private static final String DEVELOPMENT = "DEVELOPMENT";
    private static final String DOMAIN_ENV = "DOMAIN_ENV";

    private static final String LOAD_BALANCER_IP = "LOAD_BALANCER_IP";

    private static final String PUT_REQUEST = "PUT";

    public HypertyController(StatusService status, final HypertyService hypertyService, final Connection connectionClient, final DataObjectService dataObjectService) {

        String keystore = System.getenv(KEYSTORE);
        String keystorePassword = System.getenv(KEYSTORE_PASSWORD);

        if(keystore != null && keystorePassword != null){
            secure("cert/" + keystore, keystorePassword, null, null);
            log.info("HTTPS enabled...");
        }

        else log.info("You did not provide either a keystore or a keystore password. HTTP enabled...");

        before((request, response) -> {
            boolean authenticated;

            String loadBalancerIp = System.getenv(LOAD_BALANCER_IP);

            String originIp = request.ip().toString();

            if (loadBalancerIp != null && !loadBalancerIp.equals(originIp)){
                log.info("Unauthorized request...");
                halt(401, "Unauthorized request");
            }
        });

        get("/", (req, res) -> {
            res.redirect("/live");
            return null;
        });

        // Used by the load balancer to redirect unauthorized users
        get("/error", (req, res) -> {
            Gson gson = new Gson();
            res.type("application/json");
            res.status(401);
            return gson.toJson(new Messages("Unauthorized."));
        });

        // GET live page
        get("/live", (req, res) -> {
            Gson gson = new Gson();
            this.numReads++;
            log.info("Live page requested. Status on the way...");
            String accept = req.headers("Accept");

            String domainEnv = System.getenv(DOMAIN_ENV);
            String domainURL = req.host();

            if (accept != null && accept.contains("text/html") && domainEnv != null && domainEnv.equals(DEVELOPMENT)) {
                // produces HTML
                res.type("text/html");
                FreeMarkerEngine freeMarkerEngine = new FreeMarkerEngine();
                Configuration freeMarkerConfiguration = new Configuration();
                freeMarkerConfiguration.setTemplateLoader(new ClassTemplateLoader(HypertyController.class, "/"));
                freeMarkerEngine.setConfiguration(freeMarkerConfiguration);
                Map<String, List<Object>> attributes = status.getDomainRegistryStatsGlobal(req.host());
                res.status(200);
                return freeMarkerEngine.render(new ModelAndView(attributes, "status.ftl"));
            } else {
                // produces JSON
                res.status(200);
                res.type("application/json");
                Map<String, String> databaseStats = status.getDomainRegistryStats();
                return gson.toJson(databaseStats);
            }
        });

        // GET hyperty per URL
        get("/hyperty/url/*", (req,res) -> {
            Gson gson = new Gson();
            this.numReads++;
            res.type("application/json");
            String[] encodedURL = req.url().split("/");
            String hypertyUrl = decodeUrl(encodedURL[encodedURL.length - 1]);
            log.info("Received request for hypertyUrl: " + hypertyUrl);
            HypertyInstance hyperty = hypertyService.getHypertyByUrl(connectionClient, hypertyUrl);

            if(hyperty.getStatus().equals(DEAD)){
                res.status(408);
                return gson.toJson(hyperty);
            }

            res.status(200);
            return gson.toJson(hyperty);
        });

        // GET hyperties per GUID
        get("/hyperty/guid/*", (req,res) -> {
            Gson gson = new Gson();
            this.numReads++;
            res.type("application/json");
            String[] encodedURL = req.url().split("/");
            String guid = decodeUrl(encodedURL[encodedURL.length - 1]);
            log.info("Received request for guid " + guid + " hyperties");
            Map<String, HypertyInstance> hyperties = hypertyService.getHypertiesByGuid(connectionClient, guid);

            if(hypertyService.allHypertiesAreUnavailable(hyperties)){
                res.status(408);
                return gson.toJson(hyperties);
            }

            res.status(200);
            return gson.toJson(hyperties);
        });

        // GET hyperties by email
        get("/hyperty/email/*", (req,res) -> {
            Gson gson = new Gson();
            this.numReads++;
            res.type("application/json");
            String[] encodedURL = req.url().split("/");

            if(encodedURL.length == ALL_HYPERTIES_PATH_SIZE){
                String userEmail = decodeUrl(encodedURL[encodedURL.length - 1]);
                log.info("Received request for email " + userEmail + " hyperties");

                Map<String, HypertyInstance> hyperties = hypertyService.getHypertiesByEmail(connectionClient, userEmail);

                if(hypertyService.allHypertiesAreUnavailable(hyperties)){
                    res.status(408);
                    return gson.toJson(hyperties);
                }

                res.status(200);
                return gson.toJson(hyperties);
            }

            Set<String> queryParams = req.queryParams();

            if(validatePathUrl(queryParams)){
                res.status(404);
                return gson.toJson(new Messages("Not Found"));
            }

            Map<String, String> allParameters = new HashMap();

            for(String type : queryParams){
                allParameters.put(type, req.queryParams(type));
            }

            String userEmail = decodeUrl(encodedURL[encodedURL.length - 2]);

            log.info("Received advanced query per hyperties by email: " + userEmail + " with resources: "
            + allParameters.get("resources") + " and dataSchemes: " + allParameters.get("dataSchemes"));

            Map<String, HypertyInstance> userHyperties = hypertyService.getSpecificHypertiesByEmail(connectionClient, userEmail, allParameters);

            if(hypertyService.allHypertiesAreUnavailable(userHyperties)){
                res.status(408);
                return gson.toJson(userHyperties);
            }

            res.status(200);
            return gson.toJson(userHyperties);
        });

        // GET user hyperties
        get("/hyperty/user/*", (req,res) -> {
            Gson gson = new Gson();
            this.numReads++;
            res.type("application/json");
            String[] encodedURL = req.url().split("/");

            if(encodedURL.length == ALL_HYPERTIES_PATH_SIZE){
                String userID = decodeUrl(encodedURL[encodedURL.length - 1]);
                log.info("Received request for " + userID + " hyperties");
                Map<String, HypertyInstance> userHyperties = hypertyService.getAllHyperties(connectionClient, userID);

                if(hypertyService.allHypertiesAreUnavailable(userHyperties)){
                    res.status(408);
                    return gson.toJson(userHyperties);
                }

                res.status(200);
                return gson.toJson(userHyperties);
            }

            Set<String> queryParams = req.queryParams();

            if(validatePathUrl(queryParams)){
                res.status(404);
                return gson.toJson(new Messages("Not Found"));
            }

            Map<String, String> allParameters = new HashMap();

            for(String type : queryParams){
                allParameters.put(type, req.queryParams(type));
            }

            String userId = decodeUrl(encodedURL[encodedURL.length - 2]);

            log.info("Received advanced query per hyperties by user id: " + userId + " with resources: "
            + allParameters.get("resources") + " and dataSchemes: " + allParameters.get("dataSchemes"));

            Map<String, HypertyInstance> userHyperties = hypertyService.getSpecificHyperties(connectionClient, userId, allParameters);

            if(hypertyService.allHypertiesAreUnavailable(userHyperties)){
                res.status(408);
                return gson.toJson(userHyperties);
            }

            res.status(200);
            return gson.toJson(userHyperties);
        });

        // PUT Hyperty keep alive and field update
        put("/hyperty/url/*", (req,res) -> {
            Gson gson = new Gson();
            res.type("application/json");
            String body = req.body();
            String[] encodedURL = req.url().split("/");
            String hypertyID = decodeUrl(encodedURL[encodedURL.length - 1]);


            if(body.equals("{}")){
                log.info("keep alive with ID " + hypertyID);
                boolean statusChanged = hypertyService.keepAlive(connectionClient, hypertyID);
                Map<String, String> response = new HashMap();
                response.put("statusChanged", String.valueOf(statusChanged));
                Messages message = new Messages("Keep alive");
                response.put("message", message.getMessage());
                res.status(200);
                return gson.toJson(response);
            }

            else{
                log.info("Update hyperty with ID " + hypertyID + " and body " + body);
                HypertyInstance hyperty = gson.fromJson(body, HypertyInstance.class);
                hyperty.setHypertyID(hypertyID);
                boolean statusChanged = hypertyService.updateHypertyFields(connectionClient, hyperty);
                Map<String, String> response = new HashMap();
                response.put("statusChanged", String.valueOf(statusChanged));
                Messages message = new Messages("Hyperty updated");
                response.put("message", message.getMessage());
                res.status(200);
                return gson.toJson(response);
            }
        });

        // User Hyperty creation
        put("/hyperty/user/*", (req,res) -> {
            Gson gson = new Gson();
            this.numWrites++;
            res.type("application/json");
            String body = req.body();

            // validate json fields before create hyperty
            if(!validateHypertyCreationFields(body)){
                List<String> hypertyValidParams = RequestValidParams.getHypertyvalidParamskeys();
                log.info("Invalid request. Hyperty creation valid params are: " + hypertyValidParams);
                halt(400);
            }

            String[] encodedURL = req.url().split("/");
            String userID = decodeUrl(encodedURL[encodedURL.length - 2]);
            String hypertyID = decodeUrl(encodedURL[encodedURL.length - 1]);
            log.info("Inserted hyperty with ID " + hypertyID + ", user url " + userID + " and body " + body + "\n");
            HypertyInstance hyperty = gson.fromJson(body, HypertyInstance.class);
            hyperty.setUserID(userID);
            hyperty.setHypertyID(hypertyID);
            hypertyService.createUserHyperty(connectionClient, hyperty);
            res.status(200);
            return gson.toJson(new Messages("Hyperty created"));
        });


        // DELETE user Hyperty
        delete("/hyperty/user/*", (req,res) -> {
            Gson gson = new Gson();
            res.type("application/json");
            String[] encodedURL = req.url().split("/");
            String userID = decodeUrl(encodedURL[encodedURL.length - 2]);
            String hypertyID = decodeUrl(encodedURL[encodedURL.length - 1]);
            hypertyService.deleteUserHyperty(connectionClient, userID, hypertyID);
            res.status(200);
            return gson.toJson(new Messages("Hyperty deleted"));
        });

        // Create data object
        put("/hyperty/dataobject/*", (req, res) -> {
            Gson gson = new Gson();
            this.numWrites++;
            res.type("application/json");
            String body = req.body();

            // validate json fields before create data object
            if(!validateDataObjectCreationFields(body)){
                List<String> dataObjectValidParams = RequestValidParams.getDataObjectsvalidParamskeys();
                log.info("Invalid request. DataObject creation valid params are: " + dataObjectValidParams);
                halt(400);
            }

            String[] encodedURL = req.url().split("/");
            String dataObjectUrl = decodeUrl(encodedURL[encodedURL.length - 1]);
            log.info("Create dataObject with " + body + " and url " + dataObjectUrl + "\n");
            DataObjectInstance dataObject = gson.fromJson(body, DataObjectInstance.class);
            dataObject.setUrl(dataObjectUrl);
            dataObjectService.createDataObject(connectionClient, dataObject);
            res.status(200);
            return gson.toJson(new Messages("Data object created"));
        });

        // PUT Data object keep alive and field update
        put("/dataobject/url/*", (req,res) -> {
            Gson gson = new Gson();
            res.type("application/json");
            String body = req.body();
            String[] encodedURL = req.url().split("/");
            String dataObjectUrl = decodeUrl(encodedURL[encodedURL.length - 1]);


            if(body.equals("{}")){
                log.info("Keep alive dataobject with url : " + dataObjectUrl);
                boolean statusChanged = dataObjectService.keepAlive(connectionClient, dataObjectUrl);

                res.status(200);
                return gson.toJson(new Messages("Keep alive"));
            }

            else{
                log.info("Update dataobject with : " + body + " and url: " + dataObjectUrl);
                DataObjectInstance dataObject = gson.fromJson(body, DataObjectInstance.class);
                dataObject.setUrl(dataObjectUrl);
                boolean statusChanged = dataObjectService.updateDataObjectFields(connectionClient, dataObject);

                res.status(200);
                return gson.toJson(new Messages("data object updated"));
            }
        });

        // GET data object by its URL
        get("hyperty/dataobject/url/*", (req, res) -> {
            Gson gson = new Gson();
            this.numReads++;
            res.type("application/json");
            String[] encodedURL = req.url().split("/");

            if(encodedURL.length == ALL_DO_PATH_SIZE){
                String dataObjectUrl = decodeUrl(encodedURL[encodedURL.length - 1]);
                log.info("Received query per dataObject by url: " + dataObjectUrl);
                DataObjectInstance dataObject = dataObjectService.getDataObject(connectionClient, dataObjectUrl);
                res.status(200);
                return gson.toJson(dataObject);
            }

            Set<String> queryParams = req.queryParams();

            if(validatePathUrl(queryParams)){
                res.status(404);
                return gson.toJson(new Messages("Not Found"));
            }

            Map<String, String> allParameters = new HashMap();

            for(String type : queryParams){
                allParameters.put(type, req.queryParams(type));
            }

            String dataObjectUrl = decodeUrl(encodedURL[encodedURL.length - 2]);

            log.info("Received advanced query per dataObject by url: " + dataObjectUrl + " with resources: "
            + allParameters.get("resources") + " and dataSchemes: " + allParameters.get("dataSchemes"));

            Map<String, DataObjectInstance> dataObjects = dataObjectService.getSpecificDataObjectsByUrl(connectionClient, dataObjectUrl, allParameters);
            res.status(200);
            return gson.toJson(dataObjects);
        });

        // GET data object by its reporter
        get("hyperty/dataobject/reporter/*", (req, res) -> {
            Gson gson = new Gson();
            this.numReads++;
            res.type("application/json");
            String[] encodedURL = req.url().split("/");

            if(encodedURL.length == ALL_DO_PATH_SIZE){
                String hypertyReporter = decodeUrl(encodedURL[encodedURL.length - 1]);
                log.info("Received query per dataObject by reporter: " + hypertyReporter);
                Map<String, DataObjectInstance> dataObjects = dataObjectService.getDataObjectsByHyperty(connectionClient, hypertyReporter);
                res.status(200);
                return gson.toJson(dataObjects);
            }

            Set<String> queryParams = req.queryParams();

            if(validatePathUrl(queryParams)){
                res.status(404);
                return gson.toJson(new Messages("Not Found"));
            }

            Map<String, String> allParameters = new HashMap();

            for(String type : queryParams){
                allParameters.put(type, req.queryParams(type));
            }

            String dataObjectReporter = decodeUrl(encodedURL[encodedURL.length - 2]);

            log.info("Received advanced query per dataObject by reporter: " + dataObjectReporter + " with resources: "
            + allParameters.get("resources") + " and dataSchemes: " + allParameters.get("dataSchemes"));

            Map<String, DataObjectInstance> dataObjects = dataObjectService.getSpecificDataObjectsByReporter(connectionClient, dataObjectReporter, allParameters);
            res.status(200);
            return gson.toJson(dataObjects);
        });

        // GET data object by its name
        get("hyperty/dataobject/name/*", (req, res) -> {
            Gson gson = new Gson();
            this.numReads++;
            res.type("application/json");
            String[] encodedURL = req.url().split("/");

            if(encodedURL.length == ALL_DO_PATH_SIZE){
                String dataObjectName = decodeUrl(encodedURL[encodedURL.length - 1]);
                log.info("Received query per dataObject by name: " + dataObjectName);
                Map<String, DataObjectInstance> dataObjects = dataObjectService.getDataObjectsByName(connectionClient, dataObjectName);
                res.status(200);
                return gson.toJson(dataObjects);
            }

            Set<String> queryParams = req.queryParams();

            if(validatePathUrl(queryParams)){
                res.status(404);
                return gson.toJson(new Messages("Not Found"));
            }

            Map<String, String> allParameters = new HashMap();

            for(String type : queryParams){
                allParameters.put(type, req.queryParams(type));
            }

            String dataObjectName = decodeUrl(encodedURL[encodedURL.length - 2]);

            log.info("Received advanced query per dataObject by name: " + dataObjectName + " with resources: "
            + allParameters.get("resources") + " and dataSchemes: " + allParameters.get("dataSchemes"));

            Map<String, DataObjectInstance> dataObjects = dataObjectService.getSpecificDataObjectsByName(connectionClient, dataObjectName, allParameters);
            res.status(200);
            return gson.toJson(dataObjects);
        });

        // Subscribe to notifications
        get("registry/updated", (req, res) -> {
            Gson gson = new Gson();
            this.numReads++;
            res.type("application/json");

            log.info("Received request for changed registry objects");

            Map<String, HypertyInstance> userHyperties = hypertyService.getUpdatedHyperties(connectionClient);
            Map<String, DataObjectInstance> userDataObject = dataObjectService.getUpdatedDataObjects(connectionClient);

            Map<String, Object> notificationObjects = new HashMap<>();
            notificationObjects.putAll(userHyperties);
            notificationObjects.putAll(userDataObject);

            res.status(200);
            return gson.toJson(notificationObjects);
        });

        // // DELETE data object by its URL
        // delete("/hyperty/dataobject/url#<{(|", (req, res) -> {
        //     Gson gson = new Gson();
        //     res.type("application/json");
        //     String[] encodedURL = req.url().split("/");
        //     String dataObjectUrl = decodeUrl(encodedURL[encodedURL.length - 1]);
        //     dataObjectService.deleteDataObject(connectionClient, dataObjectUrl);
        //     res.status(200);
        //     return gson.toJson(new Messages("Data object deleted"));
        // });

        get("/throwexception", (request, response) -> {
            throw new TemporaryUnavailableException();
        });

        exception(TemporaryUnavailableException.class, (e, req, res) -> {
            Gson gson = new Gson();
            res.status(408);
            res.body(gson.toJson(new Messages("Temporary Unavailable")));
        });

        get("/throwexception", (request, response) -> {
            throw new DataNotFoundException();
        });

        exception(DataNotFoundException.class, (e, req, res) -> {
            Gson gson = new Gson();
            res.status(404);
            res.body(gson.toJson(new Messages("Not Found")));
        });

        get("/throwexception", (request, response) -> {
            throw new HypertiesNotFoundException();
        });

        exception(HypertiesNotFoundException.class, (e, req, res) -> {
            Gson gson = new Gson();
            res.status(404);
            res.body(gson.toJson(new Messages("Not Found")));
        });

        get("/throwexception", (request, response) -> {
            throw new DataObjectNotFoundException();
        });

        exception(DataObjectNotFoundException.class, (e, req, res) -> {
            Gson gson = new Gson();
            res.status(404);
            res.body(gson.toJson(new Messages("Not Found")));
        });

        get("/throwexception", (request, response) -> {
            throw new CouldNotRemoveHypertyException();
        });

        exception(CouldNotRemoveHypertyException.class, (e, req, res) -> {
            Gson gson = new Gson();
            res.status(404);
            res.body(gson.toJson(new Messages("Not Found")));
        });

        get("/throwexception", (request, response) -> {
            throw new UserNotFoundException();
        });

        exception(UserNotFoundException.class, (e, req, res) -> {
            Gson gson = new Gson();
            res.status(404);
            res.body(gson.toJson(new Messages("Not Found")));
        });

        get("/throwexception", (request, response) -> {
            throw new CouldNotCreateOrUpdateHypertyException();
        });

        exception(CouldNotCreateOrUpdateHypertyException.class, (e, req, res) -> {
            Gson gson = new Gson();
            res.status(404);
            res.body(gson.toJson(new Messages("Not Found")));
        });
    }

    private boolean validatePathUrl(Set<String> params){
        return params.isEmpty() || !validateQueryParams(params);
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

    private static String decodeUrl(String url) throws java.io.UnsupportedEncodingException {
        return java.net.URLDecoder.decode(url, "UTF-8");
    }

    public int getNumReads(){
        return this.numReads;
    }

    public int getNumWrites(){
        return this.numWrites;
    }

    // the following three methods validate inputs

    private boolean validateHypertyCreationFields(String requestBody){
        List<String> jsonRequestKeys = getKeysFromJsonString(requestBody);
        List<String> expectedParams = RequestValidParams.getHypertyvalidParamskeys();

        return expectedParams.containsAll(jsonRequestKeys) && jsonRequestKeys.containsAll(expectedParams);
    }

    private boolean validateDataObjectCreationFields(String requestBody){
        List<String> jsonRequestKeys = getKeysFromJsonString(requestBody);
        List<String> expectedParams = RequestValidParams.getDataObjectsvalidParamskeys();

        return expectedParams.containsAll(jsonRequestKeys) && jsonRequestKeys.containsAll(expectedParams);
    }

    // return json keys as a List<String>
    private List<String> getKeysFromJsonString(String body){
        JSONObject jsonRequest = new JSONObject(body);
        return new ArrayList<String>(jsonRequest.keySet());
    }
}
