package domainregistry;

import com.jayway.restassured.RestAssured;
import org.junit.Test;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import static com.jayway.restassured.RestAssured.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class ServerRestIT{
    @BeforeClass
    public static void configureRestAssured() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 4567;
    }

    @AfterClass
    public static void unconfigureRestAssured() {
        RestAssured.reset();
    }

    @Test
    public void checkThatWeCanCreateANewUser(){
        put("/user_id/rui@skype.com").then()
            .assertThat()
            .statusCode(200)
            .body("message", equalTo("user created"));
    }

    @Test
    public void getServicesFromNonexistantUser(){
        get("/user_id/j@fb.com").then()
            .assertThat()
            .statusCode(404)
            .body("message", equalTo("No services found"));
    }
}
