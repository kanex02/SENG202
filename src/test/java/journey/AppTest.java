package journey;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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

    /**
     * KX test
     */
    @Test
    public void testKX(){
        assertFalse(false);
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

    @Test
    public void ellaTest()
    {
        assertEquals(1+4,5);
    }

    @Test
    public void katieTest() { assertEquals(1+3,4); }

}
