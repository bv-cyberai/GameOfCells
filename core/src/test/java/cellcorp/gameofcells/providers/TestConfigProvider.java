package cellcorp.gameofcells.providers;

import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public class TestConfigProvider {

    ConfigProvider configProvider = new ConfigProvider();
    HashMap<String,String> testConfigData = configProvider.getConfigData();

    public TestConfigProvider() {
        //While I would like to test this with the config file,
        //the config file can change, so I've hardcode the values,
        //and just tested the getters.

        testConfigData.put("cellHealth","50");
        testConfigData.put("glucosePopupMessage","glucoseFound");
        testConfigData.put("cellMovementSpeed","200");
        testConfigData.put("badint",null);
        testConfigData.put("badPopupMessage",null);
        testConfigData.put("badFloat",null);


    }


    @Test
    public void testValueGetters() {
        assertEquals(50,configProvider.getIntValue("cellHealth"));
        assertEquals("glucoseFound", configProvider.getStringValue("glucosePopupMessage"));
        assertEquals(200f, configProvider.getFloatValue("cellMovementSpeed"));
    }

    @Test
    public void testValueExceptions() {
        assertThrowsExactly(NumberFormatException.class, () -> configProvider.getIntValue("badint"));
        assertThrowsExactly(NullPointerException.class, () -> configProvider.getStringValue("badPopupMessage"));
        assertThrowsExactly(NumberFormatException.class, () -> configProvider.getFloatValue("badFloat"));
    }
}
