package com.graphhopper;

import com.graphhopper.routing.Path;
import com.graphhopper.routing.util.EncodingManager;
import com.graphhopper.routing.util.Weighting;
import com.graphhopper.storage.*;
import com.graphhopper.util.Helper;
import com.graphhopper.util.Unzipper;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
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
