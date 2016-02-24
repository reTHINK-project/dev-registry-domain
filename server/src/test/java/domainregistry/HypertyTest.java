/**
  * Copyright 2015-2016 INESC-ID
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  * http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
**/

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
    private HypertyInstance hypertyInstance = null;
    private String userID;
    private String hypertyID;
    private String descriptor;

    @Before
    public void setData(){
        services = new HypertyService();
        hypertyInstance = new HypertyInstance();
        userID = "user://inesc-id.pt/ruimangas";
        hypertyID = "hyperty://ines-id.pt/123-Ha-123123";
        descriptor = "hyperty-catalogue://inesc-id.pt/1/123-Ha-123123";
        hypertyInstance.setDescriptor(descriptor);
        services.createUserHyperty(userID, hypertyID, hypertyInstance);
    }

    @Test
    public void createUserTest(){
        assertTrue(services.getServices().containsKey(userID));
    }

    @Test
    public void HypertyCreationTest(){
        Map<String, HypertyInstance> userHyperties = services.getServices().get(userID);
        assertTrue(userHyperties.containsKey(hypertyID));
        HypertyInstance userHypertyInstance = userHyperties.get(hypertyID);
        assertEquals(hypertyInstance, userHypertyInstance);
    }

    @Test
    public void getAllExistingUserHypertiesTest(){
      HypertyInstance hypertyInstance = new HypertyInstance();
      String hypertyID = "hyperty://ua.pt/123-ba-123123";
      String descriptor = "hyperty-catalogue://ua.pt/1/123-ba-123123";
      hypertyInstance.setDescriptor(descriptor);
      services.createUserHyperty(userID, hypertyID, hypertyInstance);
      Map<String, HypertyInstance> allUserHyperties = services.getAllHyperties(userID);
      assertEquals(2, allUserHyperties.keySet().size());
    }

    @Test(expected = UserNotFoundException.class) 
    public void getAllHypertiesNonexistingUserTest(){
        services.getAllHyperties("user://inesc-id.pt/ruipereira");
    }

    @Test
    public void updateHypertyInfoTest(){
        HypertyInstance hypertyInstance = new HypertyInstance();
        String descriptor = "hyperty-catalogue://inesc-id.pt/1/222-ba-123123";
        hypertyInstance.setDescriptor(descriptor);
        Map<String, HypertyInstance> allUserHyperties = services.getServices().get(userID);
        services.createUserHyperty(userID, hypertyID, hypertyInstance);
        assertTrue(allUserHyperties.containsKey(hypertyID));
        HypertyInstance newHypertyInstance = allUserHyperties.get(hypertyID);
        String oldHypertyDescriptor = newHypertyInstance.getDescriptor();
        String updatedHypertyDescriptor = hypertyInstance.getDescriptor();
        assertEquals(oldHypertyDescriptor, updatedHypertyDescriptor);
    }

    @Test
    public void removeUserHypertyTest(){
        services.deleteUserHyperty(userID, hypertyID);
        assertFalse(services.getServices().get(userID).containsKey(hypertyID));
    }

    @Test(expected= UserNotFoundException.class) 
    public void removeFromANonExistantUserTest(){
        services.deleteUserHyperty("user://inesc-id.pt//pedro", hypertyID);
    }

    @Test(expected= DataNotFoundException.class)
    public void removeANonexistentHypertyTest(){
        String nonExistentHypertyID = "hyperty://inesc-id.pt//asda-123-ll";
        services.deleteUserHyperty(userID, nonExistentHypertyID);
    }
}

