package journey.data;

public class QueryResult {
    private Station[] stations;
    private int pageSize = 20;

    public Station[] getStations() {
        return stations;
    }

    public void setStations(Station[] stations) {
        this.stations = stations;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
