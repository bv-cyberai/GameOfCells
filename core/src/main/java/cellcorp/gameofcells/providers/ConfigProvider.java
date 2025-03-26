package cellcorp.gameofcells.providers;

import cellcorp.gameofcells.AssetFileNames;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.files.FileHandle;


import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;

public class ConfigProvider {

    private static ConfigProvider instance;
    private static HashMap<String, String> configData;
    private static String fileString;
    private static String descriptionString;
    private static final String CONFIG_URL = "http://localhost:1600/assets/config.txt";
    private String cacheBuster = CONFIG_URL + "?nocahce=" +System.currentTimeMillis();

    public ConfigProvider() {
        configData = new HashMap<>();
    }

    public void loadConfig() {
        if (Gdx.app.getType() == com.badlogic.gdx.Application.ApplicationType.WebGL) {
            loadConfigServer();
        } else {
            loadConfigLocal();
        }
//        parseObjectAttributes();
//        parseDescriptions();
    }

    private void loadConfigServer() {
        System.out.println("Attempting to load config from server...");

        Net.HttpRequest request = new Net.HttpRequest(com.badlogic.gdx.Net.HttpMethods.GET);
        request.setUrl(cacheBuster);

        Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                fileString = httpResponse.getResultAsString();
                Gdx.app.log("Config Debug","RAWDATA \n" + fileString);
                fileString = new String(httpResponse.getResultAsString().getBytes(), StandardCharsets.UTF_8);
                fileString = fileString.replace("\r\n", "\n");
                descriptionString = fileString;
                System.out.println("Config loaded from server.");
                parseObjectAttributes();
                parseDescriptions();
            }

            @Override
            public void failed(Throwable t) {
                System.err.println("Failed to load config from server: " + t.getMessage());
            }

            @Override
            public void cancelled() {
                System.err.println("Config request cancelled.");
            }
        });
    }

    public void loadConfigLocal() {
        System.out.println("attempting to load gdxfiles");
        FileHandle file = Gdx.files.internal(AssetFileNames.USER_CONFIG);
        System.out.println("gdx files loaded");
        fileString = file.readString();
        descriptionString = new String(fileString);
//        parseObjectAttributes();
//        parseDescriptions();


    }

    private void parseObjectAttributes() {
        for (String line : fileString.split("\n", -1)) {
//            System.out.println(line);
            if (line.contains("[descriptions]")) {
                //Descriptions require different parsing, break loop.
                break;
            }
//            System.out.println(descriptionString);
            descriptionString = descriptionString.substring(line.length()+1);
//            System.out.println(descriptionString);


            //These are lines for the user - skip parsing.
            if (line.startsWith("[") || line.startsWith("#") || line.isEmpty()) {
                continue;
            }

            if (line.contains("#")) {
                int trimIndex = line.indexOf("#");
                String trimmedLine = line.substring(0, trimIndex);
                System.out.println(trimmedLine);
                trimmedLine = trimmedLine.trim();
                System.out.println(trimmedLine);
                String[] lineArray = trimmedLine.split(":");
                System.out.println(Arrays.toString(lineArray));
                configData.put(lineArray[0], lineArray[1]);
            }
        }
    }

    private void parseDescriptions() {
        System.out.println("DS: " + descriptionString);
        for (String line : descriptionString.split("/", -1)) {
            if (line.startsWith("[") || line.startsWith("#") || line.isEmpty()) {
                continue;
            }
            System.out.println(line);
            String[] lineArray = line.split(":");
            System.out.println(Arrays.toString(lineArray));
            if (lineArray.length == 2) {
                configData.put(lineArray[0], lineArray[1]);
            }
        }
    }

    public int getIntValue(String key) throws NumberFormatException {
        String value = configData.get(key);
        return Integer.parseInt(value);
    }

    public float getFloatValue(String key) throws NumberFormatException {
        String value = configData.get(key);
        int temp = Integer.parseInt(value);
        return (float) temp;
    }

    public String getStringValue(String key) throws NullPointerException {
        String value = configData.get(key);
        if (value == null) {
            throw new NullPointerException("Key: " + key + " null!");
        }
        return value;
    }


}
