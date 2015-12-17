package domainregistry;

import static spark.Spark.*;
import java.util.Map;
import com.google.gson.Gson;

public class HypertyController {

    public HypertyController(final HypertyService hypertyService) {

        Gson gson = new Gson();

        get("/", (req, res) -> gson.toJson(new Messages("rethink registry api")));

        get("/hyperty/user/:user_id", (req,res) -> {
            String userID = req.params(":user_id");
            res.type("application/json");
            return gson.toJson(hypertyService.getAllHyperties(userID));
        });

        get("/hyperty/user/:user_id/:hyperty_instance_id", (req,res) -> {
            res.type("application/json");
            String userID = req.params(":user_id");
            String hypertyID = req.params(":hyperty_instance_id");
            return gson.toJson(hypertyService.getUserHyperty(userID, hypertyID));
        });

        put("/hyperty/user/:user_id/:hyperty_instance_id", (req,res) -> {
            res.type("application/json");
            String userID = req.params(":user_id");
            String hypertyID = req.params(":hyperty_instance_id");
            String body = req.body();
            HypertyInstance hi = gson.fromJson(body, HypertyInstance.class);
            res.status(200);
            gson.toJson(hypertyService.createUserHyperty(userID, hypertyID, hi));
            return gson.toJson(new Messages("hyperty created"));
        });

        delete("/hyperty/user/:user_id/:hyperty_instance_id", (req,res) -> {
            res.type("application/json");
            String hypertyID = req.params(":hyperty_instance_id");
            String userID = req.params(":user_id");
            gson.toJson(hypertyService.deleteUserHyperty(userID, hypertyID));
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
