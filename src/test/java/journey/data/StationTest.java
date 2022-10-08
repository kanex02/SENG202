package journey.data;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StationTest {
    //Only the distanceTo function needs to be tested, as the rest are trivial.
    @Test
    void distanceTo() {
        Station first = new Station();
        Station second = new Station();
        first.setLatitude(53.32055555555556);
        first.setLongitude(-1.7297222222222221);
        second.setLatitude(53.31861111111111);
        second.setLongitude(-1.6997222222222223 );
        double distance = first.distanceTo(second);
        double modelAnswer = 2;
        assertTrue(distance > modelAnswer*0.9 && distance < modelAnswer*1.1);
    }

    @Test
    void distanceToSame() {
        Station first = new Station();
        Station second = new Station();
        first.setLatitude(23423.56);
        first.setLongitude(-8454.3);
        second.setLatitude(23423.56);
        second.setLongitude(-8454.3 );
        double distance = first.distanceTo(second);
        assertEquals(distance, 0);
    }

    @Test
    void getLongDescription() {
        Station station = new Station(0,
                "name",
                "operator",
                "owner",
                "address",
                true,
                4,
                true,
                120,
                true,
                0f,
                0f,
                "connector type",
                "date",
                2,
                new String[]{""},
                true,
                3,
                false
        );

        String modelAnswer = """
                Name: name
                Operator: operator
                Owner: owner
                Address: address
                Number Of Car Parks: 4
                Has free parking
                24 Hour parking available
                Time limit: 120
                Has tourist attractions nearby
                Current Type: connector type
                Number of Connectors: 2
                Not free charging
                Rating: 3
                Not a favourite
                """;
        assertEquals(modelAnswer, station.getLongDescription());

        Station station2 = new Station(0,
                "name",
                "operator",
                "owner",
                "address",
                false,
                4,
                false,
                0,
                false,
                0f,
                0f,
                "connector type",
                "date",
                2,
                new String[]{""},
                false,
                5,
                true
        );

        String modelAnswer2 = """
                Name: name
                Operator: operator
                Owner: owner
                Address: address
                Number Of Car Parks: 4
                Doesn't have free parking
                Unlimited time limit
                Current Type: connector type
                Number of Connectors: 2
                Rating: 5
                Favourite station
                """;
        assertEquals(modelAnswer2, station2.getLongDescription());
    }
}