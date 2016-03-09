package model;

import java.util.ArrayList;
import java.util.List;
public class GeoJsonLineGeometry {
    private final static String type = "LineString";
    private List<double[]> coordinates = new ArrayList<>();

    public void addPoint(double latitude, double longitude){
        if (coordinates == null){
            coordinates = new ArrayList<>();
        }
        coordinates.add(new double[]{longitude,latitude});
    }

    public List<double[]> getCoordinates() {
        return coordinates;
    }

    public String getType() {
        return type;
    }

    public int numberOfPoints() {
        return coordinates == null ? -1 : coordinates.size();
    }
}
