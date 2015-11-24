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
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 4567;
    }

    @AfterClass
    public static void unconfigureRestAssured() {
        RestAssured.reset();
    }

    @Test
    public void checkThatWeCanCreateAUserAndAHyperty(){
        put("/user_id/john@skype.com").then()
            .assertThat()
            .statusCode(200)
            .body("message", equalTo("user created"));

        String jsonString = "{\"catalogAddress\":\"sadasd11111222222\",\"guid\":\"adasdas23234324\",\"lastUpdate\":\"12-12-12\"}";

        given().contentType("application/json")
            .body(jsonString)
            .expect().statusCode(200)
            .when().put("/user_id/john@skype.com/asdas1212321ASASQ");

        get("/user_id/john@skype.com/asdas1212321ASASQ")
            .then()
            .assertThat()
            .body(
                "catalogAddress", equalTo("sadasd11111222222"),
                "guid", equalTo("adasdas23234324"),
                "lastUpdate", equalTo("12-12-12"));
    }

    @Test
    public void checkGetAllUserHyperties(){
        put("/user_id/rui@skype.com").then()
            .assertThat()
            .statusCode(200)
            .body("message", equalTo("user created"));

        String h1 = "{\"catalogAddress\":\"sadasd1111222222\",\"guid\":\"adasas23234324\",\"lastUpdate\":\"11-12-12\"}";
        String h2 = "{\"catalogAddress\":\"sadasd1111222222\",\"guid\":\"adasas23234324\",\"lastUpdate\":\"10-12-12\"}";

        given().contentType("application/json")
            .body(h1)
            .expect().statusCode(200)
            .when().put("/user_id/rui@skype.com/asbbdas1212321ASASQ");

        given().contentType("application/json")
            .body(h2)
            .expect().statusCode(200)
            .when().put("/user_id/rui@skype.com/Csdas1212321ASASQ");

        String res = "sadasd1111222222";
        get("/user_id/rui@skype.com").then().body("asbbdas1212321ASASQ.catalogAddress", equalTo(res));
        get("/user_id/rui@skype.com").then().body("Csdas1212321ASASQ.catalogAddress", equalTo(res));
    }
}
