package journey.data;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StationDAOTest {
    StationDAO stationDAO;

    @BeforeEach
    void setUp() {
        stationDAO = new StationDAO();
    }

    @Test
    void queryStation() {
        Station station = stationDAO.queryStation(1);
        assertNotNull(station);
    }
}