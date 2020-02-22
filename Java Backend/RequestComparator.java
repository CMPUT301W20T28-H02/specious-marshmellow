import java.lang.Math;
import java.util.Comparator;

public class RequestComparator implements Comparator<Request>
{
    Location driverLocation;

    public RequestComparator(Location driverLocation) {
      this.driverLocation = driverLocation;
    }

    // Used for sorting in ascending order of distance between driver location and request
    public int compare(Request a, Request b)
    {
        if (Math.abs(a.getDistance(a.getStartLocation(), driverLocation)) == Math.abs(b.getDistance(b.getStartLocation(), driverLocation))){
          return 0;   // If the Requests are equally far away from the driver
        } else if (Math.abs(a.getDistance(a.getStartLocation(), driverLocation)) < Math.abs(b.getDistance(b.getStartLocation(), driverLocation))){
          return -1;  // If Request a is closer to the driver's current location than Request b, return -1 because a < b
        } else {
          return 1;
        }
    }
}
