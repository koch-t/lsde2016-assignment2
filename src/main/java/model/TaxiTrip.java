package model;

import java.time.ZonedDateTime;

public class TaxiTrip {
    private final ZonedDateTime pickupDatetime;
    private final ZonedDateTime dropoffDateTime;
    private final int tripDistance;
    private final Coord pickupCoord;
    private final Coord dropoffCoord;

    public TaxiTrip(ZonedDateTime pickupDatetime, ZonedDateTime dropoffDateTime, int tripDistance, Coord pickupCoord, Coord dropoffCoord) {
        this.pickupDatetime = pickupDatetime;
        this.dropoffDateTime = dropoffDateTime;
        this.tripDistance = tripDistance;
        this.pickupCoord = pickupCoord;
        this.dropoffCoord = dropoffCoord;
    }

    public Coord getDropoffCoord() {
        return dropoffCoord;
    }

    public ZonedDateTime getDropoffDateTime() {
        return dropoffDateTime;
    }

    public Coord getPickupCoord() {
        return pickupCoord;
    }

    public ZonedDateTime getPickupDatetime() {
        return pickupDatetime;
    }

    public int getTripDistance() {
        return tripDistance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TaxiTrip taxiTrip = (TaxiTrip) o;

        if (tripDistance != taxiTrip.tripDistance) return false;
        if (pickupDatetime != null ? !pickupDatetime.equals(taxiTrip.pickupDatetime) : taxiTrip.pickupDatetime != null)
            return false;
        if (dropoffDateTime != null ? !dropoffDateTime.equals(taxiTrip.dropoffDateTime) : taxiTrip.dropoffDateTime != null)
            return false;
        if (pickupCoord != null ? !pickupCoord.equals(taxiTrip.pickupCoord) : taxiTrip.pickupCoord != null)
            return false;
        return dropoffCoord != null ? dropoffCoord.equals(taxiTrip.dropoffCoord) : taxiTrip.dropoffCoord == null;

    }

    @Override
    public int hashCode() {
        int result = pickupDatetime != null ? pickupDatetime.hashCode() : 0;
        result = 31 * result + (dropoffDateTime != null ? dropoffDateTime.hashCode() : 0);
        result = 31 * result + tripDistance;
        result = 31 * result + (pickupCoord != null ? pickupCoord.hashCode() : 0);
        result = 31 * result + (dropoffCoord != null ? dropoffCoord.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "TaxiTrip{" +
                "pickupDatetime=" + pickupDatetime +
                ", dropoffDateTime=" + dropoffDateTime +
                ", tripDistance=" + tripDistance +
                ", pickupCoord=" + pickupCoord +
                ", dropoffCoord=" + dropoffCoord +
                '}';
    }
}
