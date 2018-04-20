package Modules;

import java.util.List;

/**
 * Created by Amod Gandhe on 4/7/2018.
 */

public interface DirectionFinderListener {
    void onDirectionFinderStart();
    void onDirectionFinderSuccess(List<Route> route);
}
