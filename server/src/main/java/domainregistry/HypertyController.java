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
            String[] pathSplit = req.pathInfo().split("/hyperty/user/")[1].split("/(?=hyperty)");
            String userID = pathSplit[0];
            log.info("Received request for " + userID + " hyperties");
            return gson.toJson(hypertyService.getAllHyperties(userID));
        });

        put("/hyperty/user/*", (req,res) -> {
            res.type("application/json");
            String body = req.body();
            String[] pathSplit = req.pathInfo().split("/hyperty/user/")[1].split("/(?=hyperty)");
            String userID = pathSplit[0];
            String hypertyID = pathSplit[1];
            HypertyInstance hi = gson.fromJson(body, HypertyInstance.class);
            log.info("Received hyperty with ID: " + hypertyID + " and descriptor: " + hi.getDescriptor());
            res.status(200);
            gson.toJson(hypertyService.createUserHyperty(userID, hypertyID, hi));
            log.info("Created hyperty with ID: " + hypertyID);
            return gson.toJson(new Messages("hyperty created"));
        });

        delete("/hyperty/user/*", (req,res) -> {
            res.type("application/json");
            String[] pathSplit = req.pathInfo().split("/hyperty/user/")[1].split("/(?=hyperty)");
            String userID = pathSplit[0];
            String hypertyID = pathSplit[1];
            gson.toJson(hypertyService.deleteUserHyperty(userID, hypertyID));
            log.info("Deleted hyperty with ID: " + hypertyID);
            return gson.toJson(new Messages("hyperty deleted"));
        });

        get("/throwexception", (request, response) -> {
            throw new DataNotFoundException();
        });

        exception(DataNotFoundException.class, (e, req, res) -> {
            res.status(400);
            res.body(gson.toJson(new Messages("data not found")));
        });

        get("/throwexception", (request, response) -> {
            throw new UserNotFoundException();
        });

        exception(UserNotFoundException.class, (e, req, res) -> {
            res.status(400);
            res.body(gson.toJson(new Messages("user not found")));
        });
    }
}
