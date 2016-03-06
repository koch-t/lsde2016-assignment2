import com.graphhopper.GHRequest;
import com.graphhopper.GraphHopperPathAPI;
import com.graphhopper.routing.Path;
import com.graphhopper.util.EdgeIteratorState;
import com.graphhopper.util.shapes.GHPoint;
import model.Coord;
import model.TaxiTrip;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by thomas on 05/03/16.
 */
public class Router implements Serializable {

    private GraphHopperPathAPI api = null;
    public Router(){
        load();
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException{
        load();
    }

    private void load(){
        if (api == null){
            ClassLoader classLoader = getClass().getClassLoader();
            api = new GraphHopperPathAPI();
            api.forDesktop().load(classLoader.getResource("new-york_new-york.osm-gh").getPath());
        }
    }


    public List<EdgeVisit> route(TaxiTrip trip) throws Exception {
        List<EdgeVisit> visits = route(trip.getPickupCoord(), trip.getDropoffCoord(), trip.getTripDistance(), trip.getPickupDatetime(), trip.getDropoffDateTime());
        if (visits == null || visits.size() == 0){
            System.out.println("FAILED "+trip);
        }
        return visits;
    }

    public List<EdgeVisit> route(Coord fromCoord, Coord toCoord, final int distance, ZonedDateTime startTime, ZonedDateTime endTime) throws Exception {
        if (startTime.isAfter(endTime)){
            throw new IllegalArgumentException("EndTime before StartTime");
        }
        GHRequest req = new GHRequest();
        req.setVehicle("CAR");
        req.addPoint(new GHPoint(fromCoord.getLatitude(), fromCoord.getLongitude()));
        req.addPoint(new GHPoint(toCoord.getLatitude(), toCoord.getLongitude()));
        List<Path> resp = api.calcPaths(req);
        if (resp.size() == 0){
            return Collections.emptyList();
        }
        Collections.sort(resp, (o1, o2) -> {
            double distanceDiff1 = distance - o1.getDistance();
            double distanceDiff2 = distance - o2.getDistance();
            if (distanceDiff1 < distanceDiff2){
                return -1;
            }else if (distanceDiff1 > distanceDiff2){
                return 1;
            }else{
                return 0;
            }
        });
        int seconds = (int) startTime.until(endTime, ChronoUnit.SECONDS);
        Path path = resp.get(0);
        double totaldistance = path.getDistance();
        double drivenDistance = 0;
        ArrayList<EdgeVisit> edges = new ArrayList<>();
        for (EdgeIteratorState s : path.calcEdges()){
            double progress = drivenDistance/totaldistance;
            int hourOfDay = startTime.plus((long) (progress*seconds), ChronoUnit.SECONDS).get(ChronoField.HOUR_OF_DAY);
            edges.add(new EdgeVisit(s.getEdge(), hourOfDay));
            drivenDistance += s.getDistance();
        }
        return edges;
    }

    public static class EdgeVisit implements Serializable{
        private final int edgeId;
        private final int hourOfDay;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            EdgeVisit edgeVisit = (EdgeVisit) o;

            if (edgeId != edgeVisit.edgeId) return false;
            return hourOfDay == edgeVisit.hourOfDay;
        }

        @Override
        public int hashCode() {
            int result = edgeId;
            result = 31 * result + hourOfDay;
            return result;
        }

        @Override
        public String toString() {
            return "EdgeVisit{" +
                    "edgeId=" + edgeId +
                    ", hourOfDay=" + hourOfDay +
                    '}';
        }

        public EdgeVisit(int edgeId, int hourOfDay) {
            this.edgeId = edgeId;
            this.hourOfDay = hourOfDay;
        }

        public int getEdgeId() {
            return edgeId;
        }

        public int getHourOfDay() {
            return hourOfDay;
        }
    }
}
