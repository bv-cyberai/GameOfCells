package cellcorp.gameofcells.providers;

import com.badlogic.gdx.Preferences;

import java.util.HashMap;
import java.util.Map;

public class FakePreferences implements Preferences {
    private Map<String, Object> data = new HashMap<>();

    @Override
    public Preferences putBoolean(String key, boolean val) {
        data.put(key, val);
        return this;
    }

    @Override
    public Preferences putInteger(String key, int val) {
        data.put(key, val);
        return this;
    }

    @Override
    //Not Needed
    public Preferences putLong(String key, long val) {
        return null;
    }

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

    @Override
    public boolean getBoolean(String key) {
        return (boolean) data.getOrDefault(key, false);
    }

    @Override
    public int getInteger(String key) {
        return (int) data.getOrDefault(key, -999);
    }

    //Not Needed
    @Override
    public long getLong(String key) {
        return 0;
    }

    @Override
    public float getFloat(String key) {
        return (float) data.getOrDefault(key, -999f);
    }

    //Not Needed
    @Override
    public String getString(String key) {
        return "";
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        return (boolean) data.getOrDefault(key, false);
    }

    @Override
    public int getInteger(String key, int defValue) {
        return (int) data.getOrDefault(key, -999);
    }

    //not needed
    @Override
    public long getLong(String key, long defValue) {
        return -999;
    }

    @Override
    public float getFloat(String key, float defValue) {
        return (float) data.getOrDefault(key, -999);
    }

    //Not needed
    @Override
    public String getString(String key, String defValue) {
        return "";
    }

    //Not needed

    /**
     * Returns a read only Map<String, Object> with all the key, objects of the preferences.
     */
    @Override
    public Map<String, ?> get() {
        return Map.of();
    }

    //Not needed
    @Override
    public boolean contains(String key) {
        return false;
    }

    @Override
    public void clear() {
        data.clear();
    }

    @Override
    public void remove(String key) {
        //Not needed
    }

    /**
     * Makes sure the preferences are persisted.
     */
    @Override
    public void flush() {
        //Not needed
    }
}
