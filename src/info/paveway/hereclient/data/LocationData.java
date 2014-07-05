package info.paveway.hereclient.data;

public class LocationData extends AbstractBaseData {

    private double mLatitude;

    private double mLongitude;

    public void setLatitude(double latitude) {
        mLatitude = latitude;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public void setLongitude(double longitude) {
        mLongitude = longitude;
    }

    public double getLongitude() {
        return mLongitude;
    }
}
