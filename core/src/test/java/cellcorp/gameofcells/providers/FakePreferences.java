package cellcorp.gameofcells.providers;

import com.badlogic.gdx.Preferences;

import java.util.HashMap;
import java.util.Map;

/**
 * Fake Preferences Class
 *
 * This class simulates using Gdx.preferences.
 * This class is used to test the saving and loading logic
 * of GameSaverLoader.
 */
public class FakePreferences implements Preferences {
    private Map<String, Object> data = new HashMap<>();

    /**
     * Add Boolean
     * @param key
     * @param val
     * @return The preferences object
     */
    @Override
    public Preferences putBoolean(String key, boolean val) {
        data.put(key, val);
        return this;
    }

    /**
     * Add Int
     * @param key
     * @param val
     * @return The preferences object
     */
    @Override
    public Preferences putInteger(String key, int val) {
        data.put(key, val);
        return this;
    }

    //Not Needed
    @Override
    public Preferences putLong(String key, long val) {
        return null;
    }

    /**
     * Add Float
     * @param key
     * @param val
     * @return The preferences object
     */
    @Override
    public Preferences putFloat(String key, float val) {
        data.put(key, val);
        return this;
    }

    //Not Needed
    @Override
    public Preferences putString(String key, String val) {
        return null;
    }

    //NotNeeded
    @Override
    public Preferences put(Map<String, ?> vals) {
        return null;
    }

    /**
     * Boolean Getter
     * @param key
     * @return the value
     */
    @Override
    public boolean getBoolean(String key) {
        return (boolean) data.getOrDefault(key, false);
    }

    /**
     * Integer Getter
     * @param key
     * @return the value
     */
    @Override
    public int getInteger(String key) {
        return (int) data.getOrDefault(key, -999);
    }

    //Not Needed
    @Override
    public long getLong(String key) {
        return 0;
    }

    /**
     * Float Getter
     * @param key
     * @return the value
     */
    @Override
    public float getFloat(String key) {
        return (float) data.getOrDefault(key, -999f);
    }

    //Not Needed
    @Override
    public String getString(String key) {
        return "";
    }

    /**
     * Boolean getter
     * @param key
     * @param defValue
     * @return the value
     */
    @Override
    public boolean getBoolean(String key, boolean defValue) {
        return (boolean) data.getOrDefault(key, false);
    }

    /**
     * Integer getter
     * @param key
     * @param defValue
     * @return the value
     */
    @Override
    public int getInteger(String key, int defValue) {
        return (int) data.getOrDefault(key, -999);
    }

    //not needed
    @Override
    public long getLong(String key, long defValue) {
        return -999;
    }

    /**
     * Float Getter
     * @param key
     * @param defValue
     * @return The value
     */
    @Override
    public float getFloat(String key, float defValue) {
        return (float) data.getOrDefault(key, -999);
    }

    //Not needed
    @Override
    public String getString(String key, String defValue) {
        return "";
    }

    /**
     * Returns a read only Map<String, Object> with all the key, objects of the preferences.
     */
    @Override
    public Map<String, ?> get() {
        return data;
    }

    //Not needed
    @Override
    public boolean contains(String key) {
        return false;
    }

    /**
     * Clears the data map.
     */
    @Override
    public void clear() {
        data.clear();
    }

    //Not needed
    @Override
    public void remove(String key) {
    }

    //Not needed

    /**
     * Makes sure the preferences are persisted.
     */
    @Override
    public void flush() {
    }

}
