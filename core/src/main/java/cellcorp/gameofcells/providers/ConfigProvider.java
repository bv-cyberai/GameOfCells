package cellcorp.gameofcells.providers;

import cellcorp.gameofcells.AssetFileNames;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.files.FileHandle;

import java.util.Arrays;
import java.util.HashMap;

/**
 * Config Provider
 * <p>
 * Provides the ability to read a user editable config file. On desktop, this file
 * is parsed locally. On the web/gwt it will reach out to a webserver to
 * retrieve the config file.
 * <p>
 * Config data can be accessed via the data below using getters for the specific
 * key:value pair from the config file. This enables us to retrieve any value we
 * desire without a specific function for each variable.
 * <p>
 * Instructions have been provided in the readme to spin up a local instance of a
 * webserver during development.
 * <p>
 * This config provider parses Tim's implementable Minimal Language (TIML). A brief
 * rundown is given below.
 * <p>
 * ## - Comments skipped on parsing
 * [Object] - Logical grouping is skipped on parsing
 * variable:Value ##Description if you so choose.
 * <p>
 * [descriptions] - This is the only logical grouping that is important.
 * All message descriptions should appear below this line. All object
 * descriptions should appear above this line.
 * <p>
 * Logical groupings after this line require '/' line ender.
 * <p>
 * variable:message/   <--- Don't forget the line ender on messages.
 * / - line ender allows for \n in our messages.
 *
 * @author Brendon Vineyard / vineyabn207
 * @author Andrew Sennoga-Kimuli / sennogat106
 * @author Mark Murphy / murphyml207
 * @author Tim Davey / daveytj206
 * @date 03/26/2025
 * @course CIS 405
 * @assignment GameOfCells
 */
public class ConfigProvider {

    //Used for CSDEV
//    private static final String CONFIG_URL = "http://cs.potsdam.edu/Classes/405/CellCorp/assets/config.txt";
    //Used for dev/testing locally
    private static final String CONFIG_URL = "http://localhost:1600/assets/config.txt";
    // This forces the browser to grab a new copy of config.txt
    private static final String CACHE_BUSTER = CONFIG_URL + "?nocache=" + System.currentTimeMillis();
    private static String fileString;


    //I tried turning these Config_url commands into a gradle flag,
    // but GWT hates you and everyone else.
    // By default, this will be left pointing to csdev.
    // BE CAREFUL DO NOT BUILD WITH LOCAL HOST AND PUSH TO CSDEV!!
    private static String descriptionString;
    private HashMap<String, String> configData;

    /**
     * Config Provider Constructor
     * <p>
     * Initializes the hashMap used for lookup.
     * <p>
     * NOTE: I have made a -devServer flag, This is set in the following manner.
     * <p>
     * gradle build -devServer
     * <p>
     * I have it default to the Production sever in hopes that the dev version
     * never gets pushed to csdev accidentally.
     */
    public ConfigProvider() {
        configData = new HashMap<>();
    }

    /**
     * configLoader
     * <p>
     * Used to determine which type of load needs to occur based on web/desktop
     * instance.
     */
    public void loadConfig() {
        if (Gdx.app.getType() == com.badlogic.gdx.Application.ApplicationType.WebGL) {
            loadConfigServer();
        } else {
            loadConfigLocal();
        }
    }

    /**
     * Server Config Loader
     * <p>
     * Sends a GET request to webserver to retrieve config.txt for use in GWT.
     */
    private void loadConfigServer() {
        Net.HttpRequest request = new Net.HttpRequest(com.badlogic.gdx.Net.HttpMethods.GET);
        request.setUrl(CACHE_BUSTER);

        Gdx.net.sendHttpRequest(
                request, new Net.HttpResponseListener() {
                    @Override
                    public void handleHttpResponse(Net.HttpResponse httpResponse) {
                        fileString = httpResponse.getResultAsString();

                        if (fileString == null || fileString.isEmpty()) {
                            Gdx.app.error("ConfigProvider", "Empty config file received!");
                            return;
                        }

                        //This should print information to the javascript console
                        // accessed via your browsers developer tools.
                        Gdx.app.log("Config Debug", "RAWDATA \n" + fileString);

                        //Ensures filesStrings can be parsed correctly.
                        fileString = fileString.replace("\r\n", "\n");

                        descriptionString = fileString;
                        //For some reason this only works here, which means its also called separately in
                        //load config local
                        parseObjectAttributes();
                        parseDescriptions();
                    }

                    @Override
                    public void failed(Throwable t) {
                        Gdx.app.error("ConfigProvider", "Failed to load config from server: " + t.getMessage());
                    }

                    @Override
                    public void cancelled() {
                        Gdx.app.error("ConfigProvider", "Config request cancelled.");
                    }
                }
        );
    }

    /**
     * Local Config Loader
     * <p>
     * Loads user Config for parsing
     */
    private void loadConfigLocal() {
        FileHandle file = Gdx.files.internal(AssetFileNames.USER_CONFIG);
        fileString = file.readString();
        descriptionString = fileString;
        parseObjectAttributes();
        parseDescriptions();
    }

    /**
     * Object Parser
     * <p>
     * Parses objects above the [descriptions] logical grouping.
     * <p>
     * See above for more details.
     */
    private void parseObjectAttributes() {
        for (String line : fileString.split("\n", -1)) {
            if (line.contains("[descriptions]/")) {
                //Descriptions require different parsing, break loop.
                break;
            }
            //This String mirrors the file string and current line is removed after each read.
            // This avoids re-reading the line for description parsing.
            descriptionString = descriptionString.substring(line.length() + 1);


            //These are lines for the user - skip parsing.
            if (line.startsWith("[") || line.startsWith("#") || line.isEmpty()) {
                continue;
            }

            // Remove inline comments.
            if (line.contains("#")) {
                int trimIndex = line.indexOf("#");
                line = line.substring(0, trimIndex);
            }

            line = line.trim();
            String[] lineArray = line.split(":");
            Gdx.app.log("parse1", Arrays.toString(lineArray));
            if (lineArray.length >= 2) {
                configData.put(lineArray[0], lineArray[1]);
            }

        }
    }

    /**
     * Description Parser
     * <p>
     * Parses message descriptions after the [descriptions] logical grouper.
     * Removing / at the end of the message descriptions.
     */
    private void parseDescriptions() {
        for (String line : descriptionString.split("/", -1)) {
            //Handle windows CRLF
            line = line.replace("\r\n", "\n");

            //This should remove the \n after / was read, while leaving the others intact.
            if (line.length() >= 2 && line.charAt(0) == '\n') {
                line = line.substring(1);
            }

            //These are lines for the user - skip parsing.
            if (line.startsWith("[") || line.startsWith("#") || line.startsWith(" ") || line.isEmpty()) {
                continue;
            }
            String[] lineArray = line.split(":");
            if (lineArray.length >= 2) {
                lineArray[1] = lineArray[1].replace("/", "");
                configData.put(lineArray[0], lineArray[1]);
            }
        }
    }

    /**
     * Int value Getter
     * <p>
     * Returns value of key:value pair and attempts integer conversion.
     *
     * @param key - The Key to read
     * @return - The value for the key
     * @throws NumberFormatException - When the key can't be converted
     *                               to an Integer.
     */
    public int getIntValue(String key) throws NumberFormatException {
        String value = configData.get(key);
        if (value == null) {
            throw new NumberFormatException("Value for key: " + key + "' is null!");
        }
        return Integer.parseInt(value);
    }

    /**
     * Float Value Getter
     * <p>
     * Returns value of key:value pair and attempts float conversion.
     *
     * @param key - The key to read
     * @return - The value for the key
     * @throws NumberFormatException When the key can't be converted
     *                               to a float.
     */
    public float getFloatValue(String key) throws NumberFormatException {
        String value = configData.get(key);

        if (value == null) {
            throw new NumberFormatException("Value for key: " + key + "' is null!");
        }
        int temp = Integer.parseInt(value);
        return (float) temp;
    }

    /**
     * String Value Getter
     * <p>
     * Returns value of key:value pair as a String
     *
     * @param key - The key to read
     * @return - The value for the key
     * @throws NullPointerException - When the value is null.
     */
    public String getStringValue(String key) throws NullPointerException {
        String value = configData.get(key);
        if (value == null) {
            throw new NullPointerException("Key: " + key + " null!");
        }
        return value.replace("\\n", "\n");
    }

    /**
     * Gets the string corresponding to the given key, or returns the given default.
     */
    public String getStringOrDefault(String key, String defaultValue) {
        try {
            return this.getStringValue(key);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public HashMap<String, String> getConfigData() {
        return configData;
    }

    /**
     * DO NOT USE OUTSIDE OF TESTING
     * <p>
     * Reads testing data.
     */
    public void loadDataForParsingTestDoNotUse() {
        configData = new HashMap<>();
        FileHandle file = Gdx.files.internal(AssetFileNames.TEST_CONFIG);
        fileString = file.readString();
        descriptionString = fileString;
        parseObjectAttributes();
        parseDescriptions();
    }
}
