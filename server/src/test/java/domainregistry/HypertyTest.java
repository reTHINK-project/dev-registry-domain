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
        ins = new HypertyInstance();
        ins.setDescriptor("asdasdasd");
        services.createUserHyperty(user, hypertyID, ins);
    }

    @Test
    public void createUserTest(){
        assertTrue(services.getServices().containsKey(user));
    }

    @Test
    public void HypertyCreationTest(){
        Map<String, HypertyInstance> servs = services.getServices().get(user);
        assertTrue(servs.containsKey(hypertyID));
        HypertyInstance i = servs.get(hypertyID);
        assertEquals(ins, i);
    }

    @Test
    public void getAllExistingUserHypertiesTest(){
      String newHypertyID = "123dasxxxxlkiII";
      HypertyInstance hyperty = new HypertyInstance();
      hyperty.setDescriptor("addd12AAQQ");
      services.createUserHyperty(user, newHypertyID, hyperty);
      Map<String, HypertyInstance> allHyperties = services.getAllHyperties(user);
      assertEquals(2, allHyperties.keySet().size());
    }

    @Test(expected = UserNotFoundException.class) 
    public void getAllHypertiesNonexistingUserTest(){
        services.getAllHyperties("jj@twitter.com");
    }

    @Test
    public void updateHypertyInfoTest(){
        Map<String, HypertyInstance> servs = services.getServices().get(user);
        HypertyInstance hyperty = new HypertyInstance();
        hyperty.setDescriptor("asdsa111d");
        services.createUserHyperty(user, hypertyID, hyperty);
        assertTrue(servs.containsKey(hypertyID));
        HypertyInstance newHyperty = servs.get(hypertyID);
        assertEquals(newHyperty, hyperty);
    }

    @Test
    public void removeUserHypertyTest(){
        services.deleteUserHyperty(user, hypertyID);
        assertFalse(services.getServices().get(user).containsKey(hypertyID));
    }

    @Test(expected= UserNotFoundException.class) 
    public void removeFromANonExistantUserTest(){
        services.deleteUserHyperty("jj@twitter.com", hypertyID);
    }

    @Test(expected= DataNotFoundException.class)
    public void removeANonexistentHypertyTest(){
        String nonexistentHypertyID = "sdasdsa111112";
        services.deleteUserHyperty(user, nonexistentHypertyID);
    }
}

