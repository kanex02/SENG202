package seng202.team4;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit test for simple App, default from Maven
 */
public class AppTest
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void testApp()
    {
        assertTrue( true );
    }

    @Test
    public void tomTest()
    {
        assertEquals(1+1,2);
    }

    @Test
    public void danielTest()
    {
        assertEquals(9+10,19);
    }
}
