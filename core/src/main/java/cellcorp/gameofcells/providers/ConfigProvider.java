package cellcorp.gameofcells.providers;

//import org.tomlj.Toml;
//import org.tomlj.TomlParseResult;

import cellcorp.gameofcells.AssetFileNames;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Interpolation;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

//Uses singleton design pattern to ensure the existence of only 1 config
//provide.r
public class ConfigProvider {

    private static ConfigProvider instance;
//    private String configData;
    private static HashMap<String,String> configData;
    private static String fileString;
    private static String descriptionString;


//    private ConfigProvider() {
//        System.out.println("Attemtping to load config");
//        loadConfig();
//
//    }

//    public static ConfigProvider getInstance() {
//        if (instance == null) {
//            System.out.println("ConfigProvider is Null");
//            instance = new ConfigProvider();
//            System.out.println("ConfigProvider is initialized");
//        }
//        return instance;
//    }

    public ConfigProvider() {
        configData = new HashMap<>();
    }

    public void loadConfig() {
        System.out.println("attempting to load gdxfiles");
        FileHandle file = Gdx.files.internal(AssetFileNames.USER_CONFIG);
        System.out.println("gdx files loaded");
        fileString = file.readString();
        descriptionString = new String(fileString);
        parseObjectAttributes();
        parseDescriptions();



    }

    private void parseObjectAttributes() {
        for (String line : fileString.split("\n",-1)) {
            if(line.contains("[descriptions]")) {
                //Descriptions require different parsing, break loop.
                break;
            }
            descriptionString = descriptionString.substring(line.length()+1);


            //These are lines for the user - skip parsing.
            if(line.startsWith("[")||line.startsWith("#")|| line.isEmpty()){
                continue;
            }

            if(line.contains("#")) {
                int trimIndex = line.indexOf("#");
                String trimmedLine = line.substring(0,trimIndex);
                System.out.println(trimmedLine);
                trimmedLine = trimmedLine.trim();
                System.out.println(trimmedLine);
                String[] lineArray = trimmedLine.split(":");
                System.out.println(Arrays.toString(lineArray));
                configData.put(lineArray[0],lineArray[1]);
            }





        }
    }

    private void parseDescriptions() {
        System.out.println("DS: " + descriptionString);
        for (String line : descriptionString.split("/",-1)) {
            if(line.startsWith("[")||line.startsWith("#")|| line.isEmpty()){
                continue;
            }
            System.out.println(line);
            String[] lineArray = line.split(":");
            System.out.println(Arrays.toString(lineArray));
            if(lineArray.length ==2) {
                configData.put(lineArray[0], lineArray[1]);
            }
        }
    }

//    public int getCellHealth(String key) {
//        String value = configData.get(key);
//
//        //Use default value if exception occurs.
//        int retValue = 100;
//        try{
//            return Integer.parseInt(value);
//
//        } catch (NumberFormatException e) {
//            return retValue;
//        }
//
//    }

//    public int getCellHealth() {
//        String value = configData.get("cellHealth");
//
//        //Use default value if exception occurs.
//        int retValue = 100;
//        try{
//            return Integer.parseInt(value);
//
//        } catch (NumberFormatException e) {
//            return retValue;
//        }
//
//    }


    public int getIntValue(String key)  throws NumberFormatException{
        String value = configData.get(key);
        return Integer.parseInt(value);

        //Use default value if exception occurs.
//        try{
//            return Integer.parseInt(value);
//
//        } catch (NumberFormatException e) {
//            throw new NumberFormatException(e.getMessage());
//        }
    }

    public float getFloatValue(String key) throws NumberFormatException {
        String value = configData.get(key);
        int temp = Integer.parseInt(value);
        return (float) temp;

        //Use default value if exception occurs.
//        try{
//            int temp = Integer.parseInt(value);
//            return (float) temp;
//
//        } catch (NumberFormatException e) {
//            throw new NumberFormatException(e.getMessage());
//        }

    }

    public String getStringValue(String key) throws NullPointerException {
        String value = configData.get(key);
        if (value == null) {
            throw new NullPointerException("Key: "+ key + " null!");
        }
        return value;
    }


}
