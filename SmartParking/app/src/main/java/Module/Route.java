package Module;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by Amod Gandhe on 4/23/2018.
 */

public class Route {
    public Distance distance;
    public Duration duration;
    public String endAddress;
    public LatLng endLocation;
    public String startAddress;
    public LatLng startLocation;

    public List<LatLng> points;
}
