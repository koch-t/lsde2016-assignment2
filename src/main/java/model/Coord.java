package model;

/**
 * Created by thomas on 02/03/16.
 */
public class Coord {

    private final float latitude;
    private final float longitude;

    public Coord(float latitude, float longitude){
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public float getLatitude() {
        return latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Coord coord = (Coord) o;

        if (Float.compare(coord.latitude, latitude) != 0) return false;
        return Float.compare(coord.longitude, longitude) == 0;

    }

    @Override
    public int hashCode() {
        int result = (latitude != +0.0f ? Float.floatToIntBits(latitude) : 0);
        result = 31 * result + (longitude != +0.0f ? Float.floatToIntBits(longitude) : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Coord{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
