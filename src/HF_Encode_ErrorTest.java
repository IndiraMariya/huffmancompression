/*
 *  JUnit test for the GenWeights Class.
 */
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.aden.hf_lib.HuffCompTestLib;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(OrderAnnotation.class)
class HF_Encode_ErrorTest {
	private final int ordCR = 13;
	HuffCompTestLib hflib = new HuffCompTestLib();
	GenWeights gw;
	HuffCompAlerts hca;
	EncodeDecode enc;
    String dir = "data/";
    
    /**
     * Gets the operating system from the System.
     *
     * @return the operating system from the 
     */
    private static String getOperatingSystem() {
    	String os = System.getProperty("os.name");
    	return os;
    }
	
	/**
	 * Determines which OS the test is running on, as it makes a difference
	 *
	 * @throws Exception the exception
	 */
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		System.out.println("Running on: "+getOperatingSystem());
	}

	/**
	 * Tear down after class.
	 *
	 * @throws Exception the exception
	 */
	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	/**
	 * Sets the up.
	 *
	 * @throws Exception the exception
	 */
	@BeforeEach
	void setUp() throws Exception {
	}

	/**
	 * Tear down.
	 *
	 * @throws Exception the exception
	 */
	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	@Order(1)
	void test_errorCheck_textFile() {
		hca = new HuffCompAlerts(null);
		gw = new GenWeights(hca);
		enc = new EncodeDecode(gw,hca);
		File encFile = new File("encode/test.bin");
		ArrayList<String> alerts;
		// create valid weights file
		gw.generateWeights("data/simple.txt");
		gw.saveWeightsToFile("weights/simple.csv");
		hca.resetLastAlertType();

		if (encFile.exists())
			assertTrue(encFile.delete());
		
		System.out.println("Testing input text file Error Check Functionality:");
		// test empty source file name
		enc.encode("", "encode/test.bin", "weights/simple.csv", false);
		alerts = hca.getLastAlertType();
		assertTrue(alerts.size() == 1);
		assertTrue("INPUT".equals(alerts.get(0)));
		assertTrue(!encFile.exists());
		hca.resetLastAlertType();
		
		// test that source file is not a file
		enc.encode(dir, "encode/test.bin", "weights/simple.csv", false);
		alerts = hca.getLastAlertType();
		assertTrue(alerts.size() == 1);
		assertTrue("INPUT".equals(alerts.get(0)));
		assertTrue(!encFile.exists());
		hca.resetLastAlertType();

		File testFile = new File(dir+"simple.tst");
        if (testFile.exists()) 
        	assertTrue(testFile.delete());
        
		// test that source file does not exist
		enc.encode(dir+"simple.tst", "encode/test.bin", "weights/simple.csv", false);
		alerts = hca.getLastAlertType();
		assertTrue(alerts.size() == 1);
		assertTrue("INPUT".equals(alerts.get(0)));
		assertTrue(!encFile.exists());
		hca.resetLastAlertType();
		
		// test source file exists but is empty
		try {
			assertTrue(testFile.createNewFile());
			enc.encode(dir+"simple.tst", "encode/test.bin", "weights/simple.csv", false);
			alerts = hca.getLastAlertType();
			assertTrue(alerts.size() == 1);
			assertTrue("INPUT".equals(alerts.get(0)));
			assertTrue(!encFile.exists());
			assertTrue(testFile.delete());
			hca.resetLastAlertType();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}

	@Test
	@Order(2)
	void test_errorCheck_weightsFile() {
		hca = new HuffCompAlerts(null);
		gw = new GenWeights(hca);
		enc = new EncodeDecode(gw,hca);
		File encFile = new File("encode/test.bin");
		ArrayList<String> alerts;
		gw.generateWeights("data/simple.txt");
		gw.saveWeightsToFile("weights/simple.csv");
		hca.resetLastAlertType();

		System.out.println("Testing weights file errors Error Check Functionality:");

		if (encFile.exists())
			assertTrue(encFile.delete());

		// test empty weights file name
		enc.encode(dir+"simple.txt", "encode/test.bin", "", false);
		alerts = hca.getLastAlertType();
		assertTrue(alerts.size() >= 1);
		assertTrue("INPUT".equals(alerts.get(0)));
		assertTrue(encFile.exists());
		assertTrue(encFile.delete());
		hca.resetLastAlertType();

		// test weights file name is not a file (ie, a directory)
		enc.encode(dir+"simple.txt", "encode/test.bin", "weights/", false);
		alerts = hca.getLastAlertType();
		assertTrue(alerts.size() >= 1);
		assertTrue("INPUT".equals(alerts.get(0)));
		assertTrue(encFile.exists());
		assertTrue(encFile.delete());
		hca.resetLastAlertType();

		// test empty weights file does not exist - should raise an alert but not fail -
		// code should generate the weights file from the source
		enc.encode(dir+"simple.txt", "encode/test.bin", "weights/simple.cvs", false);
		alerts = hca.getLastAlertType();
		assertTrue(alerts.size() >= 1);
		assertTrue("INPUT".equals(alerts.get(0)));
		assertTrue(encFile.exists());
		assertTrue(encFile.delete());
		hca.resetLastAlertType();
	}

	@Test
	@Order(3)
	void test_errorCheck_encodeFile() {
		hca = new HuffCompAlerts(null);
		gw = new GenWeights(hca);
		enc = new EncodeDecode(gw,hca);
		File encFile = new File("encode/test.bin");
		ArrayList<String> alerts;
		gw.generateWeights("data/simple.txt");
		gw.saveWeightsToFile("weights/simple.csv");
		hca.resetLastAlertType();

		System.out.println("Testing encoded file errors Error Check Functionality:");

		if (encFile.exists())
			assertTrue(encFile.delete());
		
		// test empty encode file name
		enc.encode(dir+"simple.txt", "", "weights/simple.csv", false);
		alerts = hca.getLastAlertType();
		assertTrue(alerts.size() >= 1);
		assertTrue("OUTPUT".equals(alerts.get(0)));
		assertTrue(!encFile.exists());
		hca.resetLastAlertType();

		// test file name is not a file   (ie, a directory)
		enc.encode(dir+"simple.txt", "encode/", "weights/simple.csv", false);
		alerts = hca.getLastAlertType();
		assertTrue(alerts.size() >= 1);
		assertTrue("OUTPUT".equals(alerts.get(0)));
		assertTrue(!encFile.exists());
		hca.resetLastAlertType();

		// successfully generate encoded file, the do it again to test CONFIRM
		enc.encode(dir+"simple.txt", "encode/test.bin", "weights/simple.csv", false);
		alerts = hca.getLastAlertType();
		assertTrue(alerts.size() >= 0);
		if (alerts.size() > 0)
			assertTrue("DONE".equals(alerts.get(0)));
		hca.resetLastAlertType();
		assertTrue(encFile.exists());
		enc.encode(dir+"simple.txt", "encode/test.bin", "weights/simple.csv", false);
		alerts = hca.getLastAlertType();
		assertTrue(alerts.size() >= 1);
		assertTrue("CONFIRM".equals(alerts.get(0)));
		assertTrue(encFile.exists());
		assertTrue(encFile.delete());
		hca.resetLastAlertType();
	}

}
