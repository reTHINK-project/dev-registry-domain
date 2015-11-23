package domainregistry;

import static spark.Spark.*;
import java.util.Map;
import com.google.gson.Gson;

public class HypertyController {

    public HypertyController(final HypertyService hypertyService) {

        get("/", (req, res) -> "rethink registry api");

        get("/user_id/:user_id", (req,res) -> {
            Gson gson = new Gson();
            String userID = req.params(":user_id");
            res.type("application/json");
            Map<String, HypertyInstance> services = hypertyService.getAllHyperties(userID);
            if (services != null && !services.isEmpty()){
                res.status(200);
                return gson.toJson(services);
            }
            res.status(404);
            return gson.toJson(new Errors("services or user not found"));
        });

        get("/user_id/:user_id/:hyperty_instance_id", (req,res) -> {
            Gson gson = new Gson();
            res.type("application/json");
            String userID = req.params(":user_id");
            String hypertyID = req.params(":hyperty_instance_id");
            HypertyInstance hi = hypertyService.getUserHyperty(userID, hypertyID);
            if(hi != null){
                res.status(200);
                return gson.toJson(hi);
            }
            res.status(404);
            return gson.toJson(new Errors("user or hyperty not found"));
        });

        put("/user_id/:user_id", (req,res) -> {
            Gson gson = new Gson();
            res.type("application/json");
            String userID = req.params(":user_id");
            if(hypertyService.createUser(userID) != null){
                res.status(200);
                return gson.toJson(new Errors("user created"));
            }
            res.status(404);
            return gson.toJson(new Errors("user already created"));
        });

        put("/user_id/:user_id/:hyperty_instance_id", (req,res) -> {
            Gson gson = new Gson();
            res.type("application/json");
            String userID = req.params(":user_id");
            String hypertyID = req.params(":hyperty_instance_id");
            String body = req.body();
            HypertyInstance hi = gson.fromJson(body, HypertyInstance.class);
            if(hypertyService.createUserHyperty(userID, hypertyID, hi) != null){
                res.status(200);
                return gson.toJson(new Errors("success"));
            }
            res.status(404);
            return gson.toJson(new Errors("user or hyperty not found"));
        });

        delete("/user_id/:user_id/:hyperty_instance_id", (req,res) -> {
            Gson gson = new Gson();
            res.type("application/json");
            String hypertyID = req.params(":hyperty_instance_id");
            String userID = req.params(":user_id");
            if(hypertyService.deleteUserHyperty(userID, hypertyID) != null){
                res.status(200);
                return gson.toJson(new Errors("hyperty deleted"));
            }
            res.status(404);
            return gson.toJson(new Errors("user or hyperty not found"));
        });
    }
}
