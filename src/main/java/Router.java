import com.graphhopper.GHRequest;
import com.graphhopper.GraphHopperPathAPI;
import com.graphhopper.routing.Path;
import com.graphhopper.routing.util.AllEdgesIterator;
import com.graphhopper.util.CmdArgs;
import com.graphhopper.util.EdgeIterator;
import com.graphhopper.util.PointList;
import model.Coord;
import model.GeoJsonFeature;
import model.GeoJsonLineGeometry;
import model.TaxiTrip;
import org.joda.time.DateTime;
import org.joda.time.Seconds;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by thomas on 05/03/16.
 */
public class Router implements Serializable {

    private GraphHopperPathAPI api = null;

    public Router() {
        load();
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        load();
    }

    private void load() {
        if (api == null) {
            api = new GraphHopperPathAPI();
            if (!api.forServer().setInMemory(true, true).load("graph.zip/new-york_new-york.osm-gh")) {
                throw new IllegalStateException("Graph not loaded");
            }
        }
    }

    public List<EdgeVisit> route(TaxiTrip trip) throws Exception {
        List<EdgeVisit> visits = route(trip.getPickupCoord(), trip.getDropoffCoord(), trip.getTripDistance(), trip.getPickupDatetime(), trip.getDropoffDateTime());
        if (visits == null || visits.size() == 0) {
            //System.out.println("FAILED " + trip);
        }
        return visits;
    }

    public List<EdgeVisit> route(Coord fromCoord, Coord toCoord, final int distance, DateTime startTime, DateTime endTime) throws Exception {
        if (startTime.isAfter(endTime)) {
            throw new IllegalArgumentException("EndTime before StartTime");
        }
        GHRequest req = new GHRequest(fromCoord.getLatitude(), fromCoord.getLongitude(), toCoord.getLatitude(), toCoord.getLongitude());
        req.setVehicle("CAR");
        Path path = api.calcPaths(req);
        if (path == null) {
            return Collections.emptyList();
        }

        int seconds = (int) Seconds.secondsBetween(startTime, endTime).getSeconds();
        final ArrayList<EdgeInternal> edges = new ArrayList<>();
        path.forEveryEdge(new Path.EdgeVisitor() {
            @Override
            public void next(EdgeIterator edgeIterator, int i) {
                EdgeInternal e = new EdgeInternal();
                e.distance = edgeIterator.getDistance();
                e.edgeId = edgeIterator.getEdge();
                edges.add(e);
            }
        });
        double totaldistance = path.getDistance();
        double drivenDistance = 0;
        ArrayList<EdgeVisit> visits = new ArrayList<>();
        for (EdgeInternal e : edges) {
            double progress = drivenDistance / totaldistance;
            int hourOfDay = startTime.plusSeconds((int) (progress * seconds)).getHourOfDay();
            visits.add(new EdgeVisit(e.edgeId, hourOfDay));
            drivenDistance += e.distance;
        }
        return visits;
    }

    public GeoJsonFeature geometryEdge(int edgeId) {
        AllEdgesIterator e = api.getGraph().getAllEdges();
        while (e.getEdge() != edgeId && e.next()){

        }
        if (e.getEdge() != edgeId){
            System.out.println(edgeId);
            return null;
        }
        PointList p = e.getWayGeometry();
        GeoJsonLineGeometry lineString = new GeoJsonLineGeometry();
        lineString.addPoint(api.getGraph().getLatitude(e.getBaseNode()), api.getGraph().getLongitude(e.getBaseNode()));
        e.getBaseNode();
        for (int i = 0; i < p.getSize(); i++){
            lineString.addPoint(p.getLatitude(i), p.getLongitude(i));
        }
        lineString.addPoint(api.getGraph().getLatitude(e.getAdjNode()), api.getGraph().getLongitude(e.getAdjNode()));
        return new GeoJsonFeature(lineString);
    }

    private static class EdgeInternal{
        private double distance;
        private int edgeId;
    }

    public static class EdgeVisit implements Serializable {
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
