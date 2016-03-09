package com.graphhopper;

import com.graphhopper.routing.Path;
import com.graphhopper.routing.RoutingAlgorithm;
import com.graphhopper.routing.util.AlgorithmPreparation;
import com.graphhopper.routing.util.DefaultEdgeFilter;
import com.graphhopper.routing.util.EncodingManager;
import com.graphhopper.routing.util.NoOpAlgorithmPreparation;

/**
 * Created by thomas on 05/03/16.
 */
public class GraphHopperPathAPI extends GraphHopper {

    public Path calcPaths(GHRequest request){
        route(request);
        AlgorithmPreparation prepare = NoOpAlgorithmPreparation.createAlgoPrepare(getGraph(), request.getAlgorithm(), getEncodingManager().getEncoder(request.getVehicle()), request.getType());
        RoutingAlgorithm algo = prepare.createAlgo();

        DefaultEdgeFilter edgeFilter = new DefaultEdgeFilter(getEncodingManager().getEncoder(request.getVehicle()));

        int from = getIndex().findClosest(request.getFrom().lat, request.getFrom().lon, edgeFilter).getClosestNode();
        int to = getIndex().findClosest(request.getTo().lat, request.getTo().lon, edgeFilter).getClosestNode();
        return algo.calcPath(from, to);
    }
}
