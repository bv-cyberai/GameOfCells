package cellcorp.gameofcells.providers;

//import org.tomlj.Toml;
//import org.tomlj.TomlParseResult;

import cellcorp.gameofcells.AssetFileNames;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Interpolation;

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


    public ConfigProvider() {


    }

//    public static ConfigProvider getInstance() {
//        if (instance == null) {
//            instance = new ConfigProvider();
//        }
//        return instance;
//    }


    public void loadConfig() {
        FileHandle file = Gdx.files.internal(AssetFileNames.USER_CONFIG);
        fileString = file.readString();
        descriptionString = new String(fileString);
        parseObjectAttributes();
        parseDescriptions();



    }

    private void parseObjectAttributes() {
        for (String line : fileString.split("\n",-1)) {
//            System.out.println(line);
            descriptionString = descriptionString.substring(line.length());


//            if (descriptionString.startsWith(line)) {
//                descriptionString = descriptionString.substring(line.length()).trim();
//            }
            System.out.println("=====================");
            System.out.println(descriptionString);
            System.out.println("=================");

            //These are lines for the user - skip parsing.
            if(line.startsWith("[")||line.startsWith("#")|| line.isEmpty()){
                continue;
            }

            //Descriptions require different parsing, break loop.
            if(line.equals("[descriptions]")) {
                break;
            }
        }
    }

    private void parseDescriptions() {
        for (String line : fileString.split("\n")) {
            System.out.println(line);
        }
    }

//    public float getCellHealth(String key) {
//        return (float) configData.getLong(key);
//    }


}
