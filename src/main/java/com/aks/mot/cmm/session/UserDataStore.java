package com.aks.mot.cmm.session;

import java.util.HashMap;
import java.util.Map;

public class UserDataStore {

    public static ThreadLocal<Map<Object, Object>> store = new ThreadLocal<Map<Object, Object>>();

    public static void put( Object key, Object value ) {
        Map<Object, Object> map = getStorage();
        map.put( key, value );
    }

    public static Object get( Object key ) {
        Map<Object, Object> map = store.get();
        if (map == null) {
            return null;
        }

        return map.get( key );
    }

    public static void clear() {
        store.remove();
    }

    private static Map<Object, Object> getStorage() {
        Map<Object, Object> map = store.get();

        if (map == null) {
            map = new HashMap<Object, Object>();
            store.set(map);
        }
        
        return map;
    }
}
