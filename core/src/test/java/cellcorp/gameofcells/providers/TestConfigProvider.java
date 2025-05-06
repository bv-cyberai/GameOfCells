package cellcorp.gameofcells.providers;

import cellcorp.gameofcells.AssetFileNames;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
        testConfigData.put("mitoHeal-Cost","20,5");
        testConfigData.put("badVector", null);
        testConfigData.put("badint",null);
        testConfigData.put("badPopupMessage",null);
        testConfigData.put("badFloat",null);



    }

    @Test
    public void testParsing() {
        // Mock Gdx.app
        Gdx.app = mock(Application.class);
        doNothing().when(Gdx.app).log(anyString(), anyString());

        //Mock Files
        Gdx.files = mock(Files.class);
        FileHandle mockFileHandle = mock(FileHandle.class);
        when(Gdx.files.internal(AssetFileNames.TEST_CONFIG)).thenReturn(mockFileHandle);
        when(mockFileHandle.readString()).thenReturn("## line comment\n\n[cell]\ntestInt:100       ##inline comment is ignored.\n"+
            "testFloat:100\ntestVector:20,5\n[descriptions]/\ntestMessage:Read me/\n");


        configProvider.loadDataForParsingTestDoNotUse();
        HashMap<String, String> expectedConfigData = new HashMap<>();
        expectedConfigData.put("testInt", "100");
        expectedConfigData.put("testFloat", "100");
        expectedConfigData.put("testMessage", "Read me");
        expectedConfigData.put("testVector", "20,5");
        HashMap<String, String> testParsingData = configProvider.getConfigData();
        assertEquals(expectedConfigData, testParsingData);
    }


    @Test
    public void testValueGetters() {
        assertEquals(50,configProvider.getIntValue("cellHealth"));
        assertEquals("glucoseFound", configProvider.getStringValue("glucosePopupMessage"));
        assertEquals(200f, configProvider.getFloatValue("cellMovementSpeed"));
        assertEquals(new Vector2(20,5),configProvider.getVector2("mitoHeal-Cost"));
    }

    @Test
    public void testValueExceptions() {
        assertThrowsExactly(NumberFormatException.class, () -> configProvider.getIntValue("badint"));
        assertThrowsExactly(NullPointerException.class, () -> configProvider.getStringValue("badPopupMessage"));
        assertThrowsExactly(NumberFormatException.class, () -> configProvider.getFloatValue("badFloat"));
        assertThrows(Exception.class, () -> configProvider.getVector2("badVector"));
    }
}
