package com.graphhopper;

import com.graphhopper.routing.Path;

import java.io.Serializable;
import java.util.List;

/**
 * Created by thomas on 05/03/16.
 */
public class GraphHopperPathAPI extends GraphHopper {

    public List<Path> calcPaths(GHRequest request){
        GHResponse ghResponse = new GHResponse();
        return calcPaths(request, ghResponse);
    }
}
