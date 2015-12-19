package domainregistry;

import com.jayway.restassured.RestAssured;
import org.junit.Test;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Before;

import static com.jayway.restassured.RestAssured.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class ITserverRest{
    @BeforeClass
    public static void configureRestAssured() {
        RestAssured.baseURI = "http://localhost/hyperty/user";
        RestAssured.port = 4567;
    }

    @AfterClass
    public static void unconfigureRestAssured() {
        RestAssured.reset();
    }

    @Test
    public void checkThatWeCanCreateAUserAndAHyperty(){
        String jsonString = "{\"descriptor\":\"sadasd11111222222\"}";

        given().contentType("application/json")
            .body(jsonString)
            .expect().statusCode(200)
            .when().put("/user%3A%2F%2Fua.pt%2F123/hyperty%3A%2F%2Fua.pt%2Fb7b3rs4-3245-42gn-4327-238jhdq83d8");

        get("/user%3A%2F%2Fua.pt%2F123/hyperty%3A%2F%2Fua.pt%2Fb7b3rs4-3245-42gn-4327-238jhdq83d8")
            .then()
            .assertThat()
            .body("descriptor", equalTo("sadasd11111222222"));
    }
}
