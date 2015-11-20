package domainregistry;

import com.google.gson.Gson;
import spark.Spark;

import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;
import spark.utils.IOUtils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class HypertyIntegrationTest {

    @BeforeClass
    public static void beforeClass(){
      Main.main(null);
    }

    @AfterClass
    public static void afterClass(){
      Spark.stop();
    }

    @Test
    public void aNewUserShouldBeCreated(){
      TestResponse res = request("PUT", "/user_id/john@skype.com");
      String json = res.json();
      assertEquals("john@skype.com created", json);
      TestResponse res2 = request("PUT", "/user_id/john@skype.com");
      String json2 = res2.json();
      assertEquals("user already created", json2);
    }

    private TestResponse request(String method, String path) {
      try {
        URL url = new URL("http://localhost:4567/" + path);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(method);
        connection.setDoOutput(true);
        connection.connect();
        String body = IOUtils.toString(connection.getInputStream());
        return new TestResponse(connection.getResponseCode(), body);
      } catch (IOException e) {
        e.printStackTrace();
        fail("Sending request failed: " + e.getMessage());
        return null;
      }
    }

    private static class TestResponse {

      public final String body;
      public final int status;

      public TestResponse(int status, String body){
        this.status = status;
        this.body = body;
      }

      public String json(){
        return new Gson().fromJson(body, String.class);
      }
    }
}

