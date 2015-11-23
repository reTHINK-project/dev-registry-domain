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
    public void getAllExistingUserHypertiesTest(){
        String newHypertyID = "123dasxxxxlkiII";
        HypertyInstance hyperty = new HypertyInstance();
        hyperty.setLastUpdate("13-13-13");
        hyperty.setGuid("12312jjjjAA");
        hyperty.setCatalogAddress("addd12AAQQ");
        services.createUserHyperty(user, newHypertyID, hyperty);
        Map<String, HypertyInstance> allHyperties = services.getAllHyperties(user);
        assertEquals(2, allHyperties.keySet().size());
    }

    @Test
    public void getAllHypertiesNonexistingUserTest(){
        assertNull(services.getAllHyperties("jj@twitter.com"));
    }

    @Test
    public void getUserHypertyTest(){
        assertEquals(ins, services.getUserHyperty(user, hypertyID));
    }

    @Test
    public void getNonexistentHypertyTest(){
        assertNull(services.getUserHyperty(user, ""));
    }

    @Test
    public void createUserTest(){
        assertTrue(services.getServices().containsKey(user));
    }

    @Test
    public void createAExistentUserTest(){
        assertNull(services.createUser(user));
    }

    @Test
    public void createHypertyNonexistentUserTest(){
        String nonexistentUser = "kkk@facebook.com";
        String res = services.createUserHyperty(nonexistentUser, hypertyID, ins);
        assertNull(res);
    }

    @Test
    public void createUserHypertyTest(){
        Map<String, HypertyInstance> servs = services.getServices().get(user);
        assertTrue(servs.containsKey(hypertyID));
        HypertyInstance i = servs.get(hypertyID);
        assertEquals(ins, i);
    }

    @Test
    public void updateHypertyInfoTest(){
        Map<String, HypertyInstance> servs = services.getServices().get(user);
        HypertyInstance hyperty = new HypertyInstance();
        hyperty.setLastUpdate("13-13-13");
        hyperty.setGuid("12312jjjjAA");
        hyperty.setCatalogAddress("addd12AAQQ");
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

    @Test
    public void removeANonexistentHypertyTest(){
        String nonexistentHypertyID = "sdasdsa111112";
        String res = services.deleteUserHyperty(user, nonexistentHypertyID);
        assertNull(res);
    }
}

