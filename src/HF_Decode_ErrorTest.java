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
class HF_Decode_ErrorTest {
	private final int ordCR = 13;
	HuffCompTestLib hflib = new HuffCompTestLib();
	GenWeights gw;
	HuffCompAlerts hca;
	EncodeDecode enc;
	EncodeDecode dec;
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
	void test_errorCheck_encodeFile() {
		hca = new HuffCompAlerts(null);
		gw = new GenWeights(hca);
		enc = new EncodeDecode(gw,hca);
		dec = new EncodeDecode(gw,hca);
		File encFile = new File("encode/test.bin");
		File decFile = new File("decode/test.txt");
		ArrayList<String> alerts;
		// create valid weights file
		gw.generateWeights("data/simple.txt");
		gw.saveWeightsToFile("weights/simple.csv");
		enc.encode("data/simple.txt","encode/test.bin","weights/simple.csv",false);
		if (decFile.exists())
			assertTrue(decFile.delete());
		assertTrue(encFile.exists());
		hca.resetLastAlertType();

		
		System.out.println("Testing input text file Error Check Functionality:");
		// empty file name
		dec.decode("","decode/test.txt", "weights/simple.csv", false);
		alerts = hca.getLastAlertType();
		assertTrue(alerts.size() == 1);
		assertTrue("INPUT".equals(alerts.get(0)));
		assertTrue(!decFile.exists());
		hca.resetLastAlertType();
		
		// file does not exist
		dec.decode("encode/test.bix", "decode/test.txt","weights/simple.csv", false);
		alerts = hca.getLastAlertType();
		assertTrue(alerts.size() == 1);
		assertTrue("INPUT".equals(alerts.get(0)));
		assertTrue(!decFile.exists());
		hca.resetLastAlertType();
		// file exists but is empty
		File testFile = new File("encode/test_empty.bin");
		try {
			assertTrue(testFile.createNewFile());
			dec.decode("encode/test_empty.bin","decode/test.txt", "weights/simple.csv", false);
			alerts = hca.getLastAlertType();
			assertTrue(alerts.size() == 1);
			assertTrue("INPUT".equals(alerts.get(0)));
			assertTrue(!decFile.exists());
			assertTrue(testFile.delete());
			hca.resetLastAlertType();
		} catch (IOException e) {
			e.printStackTrace();
		}	
		assertTrue(encFile.delete());
	}

	@Test
	@Order(2)
	void test_errorCheck_weightsFile() {
		hca = new HuffCompAlerts(null);
		gw = new GenWeights(hca);
		enc = new EncodeDecode(gw,hca);
		dec = new EncodeDecode(gw,hca);
		File encFile = new File("encode/test.bin");
		File decFile = new File("decode/test.txt");
		ArrayList<String> alerts;
		gw.generateWeights("data/simple.txt");
		gw.saveWeightsToFile("weights/simple.csv");
		enc.encode("data/simple.txt","encode/test.bin","weights/simple.csv",false);
		assertTrue(encFile.exists());
		if (decFile.exists())
			assertTrue(decFile.delete());
		hca.resetLastAlertType();

		System.out.println("Testing weights file errors Error Check Functionality:");


		dec.decode("encode/test.bin","decode/test.txt", "", false);
		alerts = hca.getLastAlertType();
		assertTrue(alerts.size() >= 1);
		assertTrue("INPUT".equals(alerts.get(0)));
		assertTrue(!decFile.exists());
		hca.resetLastAlertType();

		dec.decode("encode/test.bin","decode/test.txt", "weights/", false);
		alerts = hca.getLastAlertType();
		assertTrue(alerts.size() >= 1);
		assertTrue("INPUT".equals(alerts.get(0)));
		assertTrue(!decFile.exists());
		hca.resetLastAlertType();

		dec.decode("encode/test.bin","decode/test.txt", "weights/simple.cvs", false);
		alerts = hca.getLastAlertType();
		assertTrue(alerts.size() >= 1);
		assertTrue("INPUT".equals(alerts.get(0)));
		assertTrue(!decFile.exists());
		hca.resetLastAlertType();
		assertTrue(encFile.delete());
	}

	@Test
	@Order(3)
	void test_errorCheck_decodeFile() {
		hca = new HuffCompAlerts(null);
		gw = new GenWeights(hca);
		enc = new EncodeDecode(gw,hca);
		dec = new EncodeDecode(gw,hca);
		File encFile = new File("encode/test.bin");
		File decFile = new File("decode/test.txt");
		ArrayList<String> alerts;
		gw.generateWeights("data/simple.txt");
		gw.saveWeightsToFile("weights/simple.csv");
		enc.encode("data/simple.txt","encode/test.bin","weights/simple.csv",false);
		assertTrue(encFile.exists());
		hca.resetLastAlertType();
		assertTrue(encFile.exists());
		if (decFile.exists())
			assertTrue(decFile.delete());
		
		System.out.println("Testing weights file errors Error Check Functionality:");

		dec.decode("encode/test.bin","", "weights/simple.csv", false);
		alerts = hca.getLastAlertType();
		assertTrue(alerts.size() >= 1);
		assertTrue("OUTPUT".equals(alerts.get(0)));
		assertTrue(!decFile.exists());
		hca.resetLastAlertType();

		dec.decode("encode/test.bin","decode/", "weights/simple.csv", false);
		alerts = hca.getLastAlertType();
		assertTrue(alerts.size() >= 1);
		assertTrue("OUTPUT".equals(alerts.get(0)));
		assertTrue(!decFile.exists());
		hca.resetLastAlertType();

		dec.decode("encode/test.bin","decode/test.txt", "weights/simple.csv", false);
		alerts = hca.getLastAlertType();
		assertTrue(alerts.size() == 0);
		hca.resetLastAlertType();
		assertTrue(decFile.exists());
		dec.decode("encode/test.bin","decode/test.txt", "weights/simple.csv", false);
		alerts = hca.getLastAlertType();
		assertTrue(alerts.size() >= 1);
		assertTrue("CONFIRM".equals(alerts.get(0)));
		assertTrue(decFile.exists());
		assertTrue(decFile.delete());
		hca.resetLastAlertType();
		assertTrue(encFile.delete());
	}

}
