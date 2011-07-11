/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package datoveStruktury;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import pocitac.SitoveRozhrani;
import static org.junit.Assert.*;

/**
 *
 * @author neiss
 */
public class RoutovaciTabulkaTest {

    RoutovaciTabulka rt;
    SitoveRozhrani eth0;
    SitoveRozhrani wlan0;


    public RoutovaciTabulkaTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        rt = new RoutovaciTabulka();
        eth0=new SitoveRozhrani("eth0", null, null);
        wlan0=new SitoveRozhrani("wlan0", null, null);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void prvniTest(){
        System.out.println("Prvni test ------------------------------------------------------------------------");
        assertTrue(new IpAdresa("1.1.1.1").jeVRozsahu(new IpAdresa("1.1.1.0",24)));
        
        assertEquals( 2 , rt.pridejZaznam(new IpAdresa("0.0.0.0",0),new IpAdresa("1.1.1.1"), null));
        assertEquals( 0 , rt.pridejZaznam(new IpAdresa("1.1.1.0",24), eth0));
        assertEquals( 0 , rt.pridejZaznam(new IpAdresa("0.0.0.0",0),new IpAdresa("1.1.1.1"), null));
        assertEquals( 1 , rt.pridejZaznam(new IpAdresa("0.0.0.0",0),new IpAdresa("1.1.1.1"), null));
        assertEquals( 0 , rt.pridejZaznam(new IpAdresa("1.1.2.128",25), wlan0) );
        assertEquals( 1 , rt.pridejZaznam(new IpAdresa("1.1.2.128",25), wlan0) );
        assertEquals( 0 , rt.pridejZaznam(new IpAdresa("1.1.2.128",25),eth0) );
        assertEquals( 0 , rt.pridejZaznam(new IpAdresa("2.0.0.0",1),wlan0) );

        System.out.println(rt.vypisSeLinuxove());
        
        assertTrue(rt.smazZaznam((new IpAdresa("1.1.1.0",24)), null, null));
        assertTrue(rt.smazZaznam((new IpAdresa("1.1.2.128",25)), null, wlan0));
        assertFalse(rt.smazZaznam((new IpAdresa("1.1.2.128",25)), null, wlan0));
        assertTrue(rt.smazZaznam((new IpAdresa("1.1.2.128",25)), null, eth0));
        assertFalse(rt.smazZaznam((new IpAdresa("0.0.0.0",0)), null, wlan0));
        assertTrue(rt.smazZaznam((new IpAdresa("0.0.0.0",0)), new IpAdresa("1.1.1.1"),null));
        assertFalse(rt.smazZaznam((new IpAdresa("2.0.0.0",1)), new IpAdresa("1.1.1.1"), wlan0));
        assertTrue(rt.smazZaznam((new IpAdresa("2.0.0.0",1)), null, wlan0));
        
        System.out.println(rt.vypisSeLinuxove());
    }

    @Test
    public void druhyTest(){
        System.out.println("Druhy test ------------------------------------------------------------------------");        

        assertEquals( 0 , rt.pridejZaznam(new IpAdresa("1.1.2.128",25), wlan0) );
        assertEquals( 0 , rt.pridejZaznam(new IpAdresa("1.1.2.128",25), eth0) );
        assertEquals(0, rt.pridejZaznam(new IpAdresa("1.1.2.128",25), new IpAdresa("1.1.2.129"), wlan0));
        assertEquals(0, rt.pridejZaznam(new IpAdresa("1.1.2.128",25), new IpAdresa("1.1.2.130"), eth0));

        System.out.println(rt.vypisSeLinuxove());

    }
    
}