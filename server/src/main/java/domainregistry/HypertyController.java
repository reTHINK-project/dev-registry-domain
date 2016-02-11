package domainregistry;

import static spark.Spark.*;
import org.apache.log4j.Logger;
import java.util.Map;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class HypertyController {

    static Logger log = Logger.getLogger(HypertyController.class.getName());

    public HypertyController(final HypertyService hypertyService) {

        Gson gson = new Gson();

        get("/", (req, res) -> gson.toJson(new Messages("rethink registry api")));

        get("/hyperty/user/*", (req,res) -> {
            res.type("application/json");
            JsonObject data = new JsonObject();
            String[] pathSplit = req.pathInfo().split("/hyperty/user/")[1].split("/(?=hyperty)");
            String userID = pathSplit[0];
            if(pathSplit.length == 1){
                log.info("Received request for " + userID + " hyperties");
                data.addProperty("code", 200);
                data.add("value", gson.toJsonTree(hypertyService.getAllHyperties(userID)));
                res.status(200);
                return data;
            }
            String hypertyID = pathSplit[1];
            log.info("Received request for " + hypertyID + " from user " + userID);
            data.addProperty("code", 200);
            data.add("value", gson.toJsonTree(hypertyService.getUserHyperty(userID, hypertyID)));
            res.status(200);
            return data;
        });

        put("/hyperty/user/*", (req,res) -> {
            res.type("application/json");
            JsonObject data = new JsonObject();
            String body = req.body();
            String[] pathSplit = req.pathInfo().split("/hyperty/user/")[1].split("/(?=hyperty)");
            String userID = pathSplit[0];
            String hypertyID = pathSplit[1];
            HypertyInstance hi = gson.fromJson(body, HypertyInstance.class);
            log.info("Received hyperty with ID: " + hypertyID + " and descriptor: " + hi.getDescriptor());
            hypertyService.createUserHyperty(userID, hypertyID, hi);
            log.info("Created hyperty with ID: " + hypertyID);
            data.addProperty("code", 200);
            res.status(200);
            return data;
        });

        delete("/hyperty/user/*", (req,res) -> {
            res.type("application/json");
            JsonObject data = new JsonObject();
            String[] pathSplit = req.pathInfo().split("/hyperty/user/")[1].split("/(?=hyperty)");
            String userID = pathSplit[0];
            String hypertyID = pathSplit[1];
            hypertyService.deleteUserHyperty(userID, hypertyID);
            log.info("Deleted hyperty with ID: " + hypertyID);
            data.addProperty("code", 200);
            res.status(200);
            return data;
        });

        get("/throwexception", (request, response) -> {
            throw new DataNotFoundException();
        });

        exception(DataNotFoundException.class, (e, req, res) -> {
            JsonObject data = new JsonObject();
            res.status(404);
            data.addProperty("code", 404);
            data.addProperty("description", "Data not found");
            res.body(gson.toJson(data));
        });

        get("/throwexception", (request, response) -> {
            throw new UserNotFoundException();
        });

        exception(UserNotFoundException.class, (e, req, res) -> {
            JsonObject data = new JsonObject();
            res.status(404);
            data.addProperty("code", 404);
            data.addProperty("description", "User not found");
            res.body(gson.toJson(data));
        });
    }
}
