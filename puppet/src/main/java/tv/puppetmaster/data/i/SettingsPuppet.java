package tv.puppetmaster.data.i;

import java.util.Set;

public interface SettingsPuppet extends Puppet {

    String          getDefinition();

    Storage         getStorage();
    void            setStorage(Storage storage);

    boolean         getBoolean(String key, boolean defValue);
    float           getFloat(String key, float defValue);
    int             getInt(String key, int defValue);
    long            getLong(String key, long defValue);
    String          getString(String key, String defValue);
    Set<String>     getStringSet(String key, Set<String> defValue);

    interface Storage {
        boolean     getBoolean(String key, boolean defValue);
        void        setBoolean(boolean value);
        float       getFloat(String key, float defValue);
        void        setFloat(float value);
        int         getInt(String key, int defValue);
        void        setInt(int value);
        long        getLong(String key, long defValue);
        void        setLong(long value);
        String      getString(String key, String defValue);
        void        setString(String value);
        Set<String> getStringSet(String key, Set<String> defValue);
        void        setStringSet(Set<String> value);
    }
}