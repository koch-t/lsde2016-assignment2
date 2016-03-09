package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thomas on 09/03/16.
 */
public class GeoJsonCollection {
    private final static String type = "FeatureCollection";
    private List<GeoJsonFeature> features;

    public void addGeoJsonFeature(GeoJsonFeature feature){
        if (features == null){
            features = new ArrayList<>();
        }
        features.add(feature);
    }

    public List<GeoJsonFeature> getFeatures() {
        return features;
    }

    public String getType() {
        return type;
    }
}
