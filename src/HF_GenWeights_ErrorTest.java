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
class HF_GenWeights_ErrorTest {
	private final int ordCR = 13;
	HuffCompTestLib hflib = new HuffCompTestLib();
	GenWeights gw;
	HuffCompAlerts hca;
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
		int[] tstWeights;
		ArrayList<String> alerts;
		System.out.println("Testing input text file Error Check Functionality:");
		// empty file name
		tstWeights = gw.readInputFileAndReturnWeights("");
		alerts = hca.getLastAlertType();
		assertTrue(alerts.size() == 1);
		assertTrue("INPUT".equals(alerts.get(0)));
		assertTrue(tstWeightsIsEmpty(tstWeights));
		hca.resetLastAlertType();

		//delete sacrificial file if it exists already - due to debugging
		File testFile = new File(dir+"simple.tst");
		if (testFile.exists()) 
			assertTrue(testFile.delete());

		// file does not exist
		tstWeights = gw.readInputFileAndReturnWeights(dir+"simple.tst");

		alerts = hca.getLastAlertType();
		assertTrue(alerts.size() == 1);
		assertTrue("INPUT".equals(alerts.get(0)));
		assertTrue(tstWeightsIsEmpty(tstWeights));
		hca.resetLastAlertType();
		try {
			// create sacrificial file to test exists but is empty
			assertTrue(testFile.createNewFile());
			tstWeights = gw.readInputFileAndReturnWeights(dir+"simple.tst");
			alerts = hca.getLastAlertType();
			assertTrue(alerts.size() == 1);
			assertTrue("INPUT".equals(alerts.get(0)));
			assertTrue(tstWeightsIsEmpty(tstWeights));
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
		int[] tstWeights;
		ArrayList<String> alerts;
		System.out.println("Testing saveWeights Error Check Functionality:");

		// test error if file name is empty
		gw.saveWeightsToFile("");
		alerts = hca.getLastAlertType();
		assertTrue(alerts.size() == 1);
		assertTrue("OUTPUT".equals(alerts.get(0)));
		hca.resetLastAlertType();

		// test error if file name actually is not a file 
		gw.saveWeightsToFile(dir);
		alerts = hca.getLastAlertType();
		assertTrue(alerts.size() == 1);
		assertTrue("OUTPUT".equals(alerts.get(0)));
		hca.resetLastAlertType();

		// test successful DONE indication
		File tstSave = new File("weights/tstSimple.csv");
		if (tstSave.exists()) 
			assertTrue(tstSave.delete());
		gw.saveWeightsToFile(tstSave.getPath());
		alerts = hca.getLastAlertType();
		assertTrue(alerts.size() == 1);
		assertTrue("DONE".equals(alerts.get(0)));
		hca.resetLastAlertType();
		
		// test successful CONFIRM and DONE alert
		gw.saveWeightsToFile(tstSave.getPath());
		alerts = hca.getLastAlertType();
		assertTrue(alerts.size() == 2);
		assertTrue("CONFIRM".equals(alerts.get(0)));
		assertTrue("DONE".equals(alerts.get(1)));
		assertTrue(tstSave.delete());
		hca.resetLastAlertType();		
	}

	boolean tstWeightsIsEmpty(int[] tst) {
		for (int i = 0; i < tst.length; i++) {
			if (tst[i] != 0) return false;
		}
		return true;
	}
}
