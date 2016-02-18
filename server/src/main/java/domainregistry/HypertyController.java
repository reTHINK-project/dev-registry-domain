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
