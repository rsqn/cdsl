package tech.rsqn.cdsl.dsl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class MapModel implements Serializable {
    private Map<String, String> map = new HashMap<>();

    public Map<String, String> getMap() {
        return map;
    }

    public void setMap(Map<String, String> map) {
        this.map = map;
    }

    public void put(String k, String v) {
        map.put(k, v);
    }

    public String put(String k) {
        return map.get(k);
    }
}
