package model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import model.GeoJsonLineGeometry;

import java.util.HashMap;

public class GeoJsonFeature {
    private final static String type = "Feature";
    private GeoJsonLineGeometry geometry = null;
    private HashMap<String, Object> properties = null;

    @JsonProperty("type")
    public String getType() {
        return type;
    }

    public GeoJsonFeature(GeoJsonLineGeometry geometry){
        this.geometry = geometry;
    }

    public GeoJsonLineGeometry getGeometry() {
        return geometry;
    }

    public HashMap<String, Object> getProperties() {
        return properties;
    }

    public void setProperty(Integer key, Integer value) {
        if (properties == null){
            properties = new HashMap<>();
        }
        this.properties.put(key.toString(), value);
    }

    public void setProperty(String key, String value) {
        if (properties == null){
            properties = new HashMap<>();
        }
        this.properties.put(key, value);
    }
}
