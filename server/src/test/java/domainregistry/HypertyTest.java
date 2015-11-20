package domainregistry;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

public class HypertyTest {

  private HypertyService services = null;
  private HypertyInstance ins = null;
  private String user;
  private String hypertyID;

  @Before
  public void setData(){
    user = "john@skype.com";
    hypertyID = "12312jjjj12x";
    services = new HypertyService();
    services.createUser(user);
    ins = new HypertyInstance();
    ins.setCatalogAddress("asdasdasd");
    ins.setGuid("12312KKKasd");
    ins.setLastUpdate("12-12-12");
    services.createUserHyperty(user, hypertyID, ins);
  }

  @Test
  public void createUserTest(){
    assertTrue(services.getServices().containsKey(user));
  }

  @Test
  public void createUserHyperty(){
    Map<String, HypertyInstance> servs = services.getServices().get(user);
    assertTrue(servs.containsKey(hypertyID));
    HypertyInstance i = servs.get(hypertyID);
    assertEquals(ins.getCatalogAddress(), i.getCatalogAddress());
    assertEquals(ins.getGuid(), i.getGuid());
    assertEquals(ins.getLastUpdate(), i.getLastUpdate());
  }

  @Test
  public void removeUserHyperty(){
    services.deleteUserHyperty(user, hypertyID);
    assertFalse(services.getServices().get(user).containsKey(hypertyID));
  }
}

