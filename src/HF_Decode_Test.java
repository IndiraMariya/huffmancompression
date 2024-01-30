import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import myfileio.MyFileIO;

/**
 * JUnit test for testing the decode function
 * 
 * @author scott
 *
 */
@TestMethodOrder(OrderAnnotation.class)
class HF_Decode_Test {

	EncodeDecode enc;
	EncodeDecode dec;
	HuffmanCompressionUtilities huffUtil;
	GenWeights gw;
	HuffCompAlerts hca;
	MyFileIO fio = new MyFileIO();

	private final int NUM_ASCII = 128;
    int[] weights = new int[NUM_ASCII];
    private static String os;
    private String textFile;
    private String weightsFile;
    private String encodeFile;
    private String decodeFile;
    private boolean optimize = true;
    
	private static long startTime;
	private static long stopTime;
    
	private File binOutf;
	
    /**
     * Gets the operating system.
     *
     * @return the operating system
     */
    private static String getOperatingSystem() {
    	os = System.getProperty("os.name");
    	return os;
    }

    /**
     * Create the filenames from the supplied base, and prepend the
     * appropriate directory locations
     *
     * @param base the base
     */
    private void updateFileNames(String base) {
    	textFile = "data/"+base+".txt";
    	weightsFile = "weights/"+base+".csv";
    	encodeFile = "encode/"+base+".bin";
    	decodeFile = "decode/"+base+".txt";
    	if (!optimize) {
    		encodeFile = encodeFile.replaceAll(".bin", "_full.bin");
    		decodeFile = decodeFile.replaceAll(".txt", "_full.txt");
    	}
    }
 
    /**
     * Gets the operating system before each test, and resets the
     * start time for benchmarking the time taken by the entire test suite
     *
     * @throws Exception the exception
     */
    @BeforeAll
	static void setUpBeforeClass() throws Exception {
    	getOperatingSystem();
    	startTime = System.nanoTime();
	}

	/**
	 * Sets the stop time after completion of all tests and
	 * write this information to the JU_ExecTime file
	 *
	 * @throws Exception the exception
	 */
	@AfterAll
	static void tearDownAfterClass() throws Exception {
		stopTime = System.nanoTime();
		File exec = new File("JU_ExecTime");
		BufferedWriter bw = new BufferedWriter(new FileWriter(exec));
		try {
			bw.write(String.format("Test Execution Time: %5.3f\n",(stopTime - startTime)/1e9));
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
	
	/**
	 * These tests all follow the same methodology:
	 * 1) set optimize flag to true (optimized Huffman Tree) or false (full Huffman Tree)
	 * 2) Initialize the instances of the required classes and the filenames
	 *    for the source text, frequency weights, encoded binary and decoded text files.
	 * 3) Recreate the weights file, and initialize the weights array in huffUtil
	 * 4) Remove the encoded binary and decoded text files, if they exist
	 * 5) encode and then decode the file
	 * 6) verify that the decoded file exists and that it is the same as the original
	 *    source file.
	 * 7) remove the output files.
	 */
	
	/**
	 * Test decode of simple.txt with an optimized Huffman Tree
	 */
	@Test
	@Order(1)
	void test_decode_simple_optimize() {
		optimize = true;
		hca = new HuffCompAlerts(null);
		gw = new GenWeights(hca);
		enc = new EncodeDecode(gw,hca);
		dec = new EncodeDecode(gw,hca);
		huffUtil = new HuffmanCompressionUtilities();

		String base = "simple";
		updateFileNames(base);
		
		checkWeightsFile();
		huffUtil.setWeights(new int[NUM_ASCII]);
		assertTrue(removeOutputFiles());

		enc.encode(textFile, encodeFile,weightsFile,optimize);
		dec.decode(encodeFile, decodeFile,weightsFile,optimize);
		// recreate file handle to test existence of binary file
		File outFh = new File(decodeFile);
		assertTrue(outFh.exists());
		File origFh = new File(textFile);
		assertTrue(compareFiles(origFh, outFh));
		assertTrue(removeOutputFiles());
	}

	/**
	 * Test decode of simple.txt with a full Huffman Tree
	 */
	@Test
	@Order(2)
	void test_decode_simple_full() {
		optimize = false;
		hca = new HuffCompAlerts(null);
		gw = new GenWeights(hca);
		enc = new EncodeDecode(gw,hca);
		dec = new EncodeDecode(gw,hca);
		huffUtil = new HuffmanCompressionUtilities();
		String base = "simple";
		updateFileNames(base);
		
		checkWeightsFile();
		huffUtil.setWeights(new int[NUM_ASCII]);
		assertTrue(removeOutputFiles());
		enc.encode(textFile, encodeFile,weightsFile,optimize);
		dec.decode(encodeFile, decodeFile,weightsFile,optimize);
		// recreate file handle to test existence of binary file
		File outFh = new File(decodeFile);
		assertTrue(outFh.exists());
		File origFh = new File(textFile);
		assertTrue(compareFiles(origFh, outFh));	
		assertTrue(removeOutputFiles());
	}
	
	/**
	 * Test decode of Green Eggs and Ham.txt with an optimized Huffman Tree
	 */
	@Test
	@Order(3)
	void test_decode_GEAH_optimize() {
		optimize=true;
		hca = new HuffCompAlerts(null);
		gw = new GenWeights(hca);
		enc = new EncodeDecode(gw,hca);
		dec = new EncodeDecode(gw,hca);
		huffUtil = new HuffmanCompressionUtilities();
		String base = "Green Eggs and Ham";
		updateFileNames(base);
		
		checkWeightsFile();
		huffUtil.setWeights(new int[NUM_ASCII]);
		assertTrue(removeOutputFiles());
		enc.encode(textFile, encodeFile,weightsFile,optimize);
		dec.decode(encodeFile, decodeFile,weightsFile,optimize);
		// recreate file handle to test existence of binary file
		File outFh = new File(decodeFile);
		assertTrue(outFh.exists());
		File origFh = new File(textFile);
		assertTrue(compareFiles(origFh, outFh));	
		assertTrue(removeOutputFiles());
	}
	
	/**
	 * Test decode of The Cat in the Hat.txt with an optimized Huffman Tree
	 */
	@Test
	@Order(4)
	void test_decode_TCITH_optimize() {
		optimize=true;
		hca = new HuffCompAlerts(null);
		gw = new GenWeights(hca);
		enc = new EncodeDecode(gw,hca);
		dec = new EncodeDecode(gw,hca);
		huffUtil = new HuffmanCompressionUtilities();
		String base = "The Cat in the Hat";
		updateFileNames(base);
		
		checkWeightsFile();
		huffUtil.setWeights(new int[NUM_ASCII]);
		assertTrue(removeOutputFiles());
		enc.encode(textFile, encodeFile,weightsFile,optimize);
		dec.decode(encodeFile, decodeFile,weightsFile,optimize);
		// recreate file handle to test existence of binary file
		File outFh = new File(decodeFile);
		assertTrue(outFh.exists());
		File origFh = new File(textFile);
		assertTrue(compareFiles(origFh, outFh));	
		assertTrue(removeOutputFiles());
	}
	
	/**
	 * Test decode of Harry Potter and the Sorcerer.txt with an optimized Huffman Tree
	 */
	@Test
	@Order(5)
	void test_decode_HPATS_optimize() {
		optimize=true;
		hca = new HuffCompAlerts(null);
		gw = new GenWeights(hca);
		enc = new EncodeDecode(gw,hca);
		dec = new EncodeDecode(gw,hca);
		huffUtil = new HuffmanCompressionUtilities();
		String base = "Harry Potter and the Sorcerer";
		updateFileNames(base);
		
		checkWeightsFile();
		huffUtil.setWeights(new int[NUM_ASCII]);
		assertTrue(removeOutputFiles());
		enc.encode(textFile, encodeFile,weightsFile,optimize);
		dec.decode(encodeFile, decodeFile,weightsFile,optimize);
		// recreate file handle to test existence of binary file
		File outFh = new File(decodeFile);
		assertTrue(outFh.exists());
		File origFh = new File(textFile);
		assertTrue(compareFiles(origFh, outFh));	
		assertTrue(removeOutputFiles());
	}
	
	/**
	 * Test decode War and Peace.txt with an optimized Huffman Tree
	 */
	@Test
	@Order(6)
	void test_decode_WAP_optimize() {
		optimize=true;
		hca = new HuffCompAlerts(null);
		gw = new GenWeights(hca);
		enc = new EncodeDecode(gw,hca);
		dec = new EncodeDecode(gw,hca);
		huffUtil = new HuffmanCompressionUtilities();
		String base = "warAndPeace";
		updateFileNames(base);
		
		checkWeightsFile();
		huffUtil.setWeights(new int[NUM_ASCII]);
		assertTrue(removeOutputFiles());
		enc.encode(textFile, encodeFile,weightsFile,optimize);
		dec.decode(encodeFile, decodeFile,weightsFile,optimize);
		// recreate file handle to test existence of binary file
		File outFh = new File(decodeFile);
		assertTrue(outFh.exists());
		File origFh = new File(textFile);
		assertTrue(compareFiles(origFh, outFh));	
		assertTrue(removeOutputFiles());
	}
	
	/**
	 * Test decode War and Peace with a full Huffman Tree
	 */
	@Test
	@Order(7)
	void test_decode_WAP_full() {
		optimize=false;
		hca = new HuffCompAlerts(null);
		gw = new GenWeights(hca);
		enc = new EncodeDecode(gw,hca);
		dec = new EncodeDecode(gw,hca);
		huffUtil = new HuffmanCompressionUtilities();
		String base = "warAndPeace";
		updateFileNames(base);		
		checkWeightsFile();
		huffUtil.setWeights(new int[NUM_ASCII]);
		assertTrue(removeOutputFiles());

		enc.encode(textFile, encodeFile,weightsFile,optimize);
		dec.decode(encodeFile, decodeFile,weightsFile,optimize);
		// recreate file handle to test existence of binary file
		File outFh = new File(decodeFile);
		assertTrue(outFh.exists());
		File origFh = new File(textFile);
		assertTrue(compareFiles(origFh, outFh));	
		assertTrue(removeOutputFiles());
	}

	/**
	 * This test checks for proper EOF detection during decoding. Errors here
	 * could either be in the executeDecode method itself or possibly in the 
	 * traverseTree method.
	 * 
	 * Methodology: Using the simpler.txt file, generate the weights. Leverage the
	 * encodings of the characters + EOF character, write, encode and decode 11 files
	 * such that the encoded files will be between 1 and 3 bytes long, and the terminating
	 * bit of the EOF character (the one that traverses to the leaf node) is in every possible
	 * bit position in a byte. Make sure that the decoded file matches the original source text file,
	 * then clean everything up.
	 */
	@Test
	@Order(9)
	void test_EOFdetection() {
		System.out.println("Test 8: Checking Write EOF and padding");
		optimize = false;
		hca = new HuffCompAlerts(null);
		gw = new GenWeights(hca);
		enc = new EncodeDecode(gw,hca);
		dec = new EncodeDecode(gw,hca);
		huffUtil = new HuffmanCompressionUtilities();
		String base = "simpler";
		updateFileNames(base);
		checkWeightsFile();
		huffUtil.setWeights(new int[NUM_ASCII]);
		removeBinaryFile();
		enc.encode(textFile,encodeFile,weightsFile,optimize);
		removeBinaryFile();
		String[] testStrings = new String[] {"t","h"," t","th","he","it","an","tnt","in","hen","ta ta"};
		
		for (int i = 0; i < testStrings.length; i++ ) {
			File binFile = new File("encode/test_writeEOFandPadding_"+i+".bin");
			File testFile = new File("data/test_writeEOFandPadding_"+i+".txt");
			File decodeFile = new File("decode/test_writeEOFandPadding_"+i+".txt");
			writeTestTextFile(testFile,testStrings[i]);
			enc.encode(testFile.getPath(), binFile.getPath(), weightsFile, optimize);
			enc.decode(binFile.getPath(),decodeFile.getPath(),weightsFile,optimize);

			assertTrue(compareFiles(testFile, decodeFile));
			assertTrue(testFile.delete());
			assertTrue(binFile.delete());
			assertTrue(decodeFile.delete());
		}
		assertTrue(new File(weightsFile).delete());
	}

	
	/**
	 * Test decode GEA hw WAP.
	 */
	@Test
	@Order(10)
	void test_decode_GEAHwWAP() {
		optimize=false;
		hca = new HuffCompAlerts(null);
		gw = new GenWeights(hca);
		enc = new EncodeDecode(gw,hca);
		dec = new EncodeDecode(gw,hca);
		huffUtil = new HuffmanCompressionUtilities();
		String decodeWeights = "warAndPeace";
		String base = "Green Eggs and Ham";
		updateFileNames(decodeWeights);
		decodeWeights = weightsFile;
		String expectData = "";
		String actData = "";
		
		checkWeightsFile();
		huffUtil.setWeights(new int[NUM_ASCII]);
		updateFileNames(base);

		assertTrue(removeOutputFiles());
		
		enc.encode(textFile, encodeFile,weightsFile,optimize);
		enc.decode(encodeFile, decodeFile,decodeWeights,optimize);
		// recreate file handle to test existence of binary file
		File outFh = new File(decodeFile);
		assertTrue(outFh.exists());
		if (os.contains("Win")) {
			assertTrue(outFh.length() == 3724);
			expectData = "noleret ";
		} else {
			assertTrue(outFh.length() ==3238);
			expectData = "neoaaboa";
		} 
		try {
			BufferedReader br = new BufferedReader(new FileReader(outFh));
			for (int i = 0;  i < 8; i++) {
				char c = (char) br.read();
				actData += c;
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		assertEquals(expectData,actData);
		assertTrue(removeOutputFiles());
	} 
	/**
	 * Test decode GEA hw WAP.
	 */
	@Test
	@Order(8)
	void test_decode_GEAHwsimple() {
		optimize=false;
		hca = new HuffCompAlerts(null);
		gw = new GenWeights(hca);
		enc = new EncodeDecode(gw,hca);
		dec = new EncodeDecode(gw,hca);
		huffUtil = new HuffmanCompressionUtilities();
		String decodeWeights = "simple";
		String base = "Green Eggs and Ham";
		updateFileNames(decodeWeights);
		decodeWeights = weightsFile;
		
		checkWeightsFile();
		huffUtil.setWeights(new int[NUM_ASCII]);
		updateFileNames(base);

		assertTrue(removeOutputFiles());
		
		enc.encode(textFile, encodeFile,decodeWeights,optimize);
		binOutf = new File(encodeFile);
		int[] expectData;
		if (os.contains("Win")) {
			assertTrue(checkBinaryOutput(binOutf, 5080));
			expectData = new int[]{64,0,0,0,0,1,49,0,0};
		} else {
			assertTrue(checkBinaryOutput(binOutf, 5112));
			expectData = new int[] {216,0,0,0,0,0,185,176,0};
		} 
		BufferedInputStream bis = fio.openBufferedInputStream(binOutf);
		assertTrue(read8Bytes(bis,expectData));
		
		enc.decode(encodeFile, decodeFile,decodeWeights,optimize);
		// recreate file handle to test existence of binary file
		File outFh = new File(decodeFile);
		assertTrue(outFh.exists());
		File origFh = new File(textFile);
		assertTrue(compareFiles(origFh, outFh));	
		assertTrue(removeOutputFiles());
	} 

	/**
	 * Write the temporary test text file for checking EOF padding
	 *
	 * @param outf the outf
	 * @param str the str
	 */
	private void writeTestTextFile(File outf,String str) {
		BufferedWriter bw = fio.openBufferedWriter(outf);
		try {
			for (int i = 0; i < str.length(); i++) {
				bw.write(str.charAt(i));
			}
			fio.closeFile(bw);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Compare two files to ensure that they are identical (character by character)
	 *
	 * @param fh1 the fh 1
	 * @param fh2 the fh 2
	 * @return true, if successful
	 */
	private boolean compareFiles(File fh1, File fh2) {
		BufferedInputStream inFh1 = null;
		BufferedInputStream inFh2 = null;
		int byte_cnt = 0;
		System.out.println("Comparing files "+fh1.getPath()+" and "+fh2.getPath());
	
		try {
			inFh1 = new BufferedInputStream(new FileInputStream(fh1));
			inFh2 = new BufferedInputStream(new FileInputStream(fh2));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		boolean EOF = false;
		boolean match = true;
		
		while (!EOF) {
			int byte_fh1 = readByteFromInStream(inFh1);
			int byte_fh2 = readByteFromInStream(inFh2);
			byte_cnt++;
			EOF = byte_fh1 == -1;		
			match = byte_fh1 == byte_fh2;
			if (EOF && !match) {
				if (byte_fh2 == 13)   // is there a CR? if so, must be followed by a \n
				   byte_fh2 = readByteFromInStream(inFh2);
				if (byte_fh2 == 10)   // is there a \n?
					byte_fh2 = readByteFromInStream(inFh2); // should be EOF now
				match = byte_fh1 == byte_fh2;
			}
			if (!match) { 
				System.out.println("Mismatch detected between file "+fh1.getPath()+" and file "+fh2.getPath()+" at byte "+byte_cnt);
			    System.out.println("   Expected byte value = "+byte_fh1+"("+((char) byte_fh1)+")");
			    System.out.println("   Actual byte value   = "+byte_fh2+"("+((char) byte_fh2)+")");
			    closeInStream(inFh1);
			    closeInStream(inFh2);
				return false;
			}

		}	
	    closeInStream(inFh1);
	    closeInStream(inFh2);
		
		return true;
	}

	/**
	 * Close BufferedInputStream
	 *
	 * @param inS the BufferedInputStream
	 */
	private void closeInStream(BufferedInputStream inS) {
		try {
			inS.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
		
	/**
	 * If the weights file exists, delete it and then force regeneration
	 *
	 * @return true, if successful
	 */
	private boolean checkWeightsFile() {
		File wtsFh = new File(weightsFile);
		System.out.println("Generating weights file: "+ weightsFile);

		System.out.println("Checking if file exists: "+wtsFh.getPath());
		if (wtsFh.exists()) 
			wtsFh.delete();
		
		weights = gw.readInputFileAndReturnWeights(textFile);
		writeWeightsFile(wtsFh);
		return  (wtsFh.exists()); 
	}	
	
	/**
	 * Write weights file.
	 *
	 * @param outf the outf
	 */
	private void writeWeightsFile(File outf) {
		String line;
		try {
			DataOutputStream output = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(outf)));
			for (int i = 0; i < weights.length; i++ ) {
					line = i+","+weights[i]+",\n";
				output.writeBytes(line);
			}
			output.flush();
			output.close();
		} catch (IOException e) {
			System.err.println("Error in writing file: "+outf.getName());
			e.printStackTrace();
		}
	}

	/**
	 * Removes the encoded binary, it it exists
	 *
	 * @return true, if successful
	 */
	private boolean removeBinaryFile() {
		binOutf = new File(encodeFile);

		System.out.println("Checking if file exists: "+binOutf.getPath());
		if (binOutf.exists()) {
			if (binOutf.delete()) {
				System.out.println("Deleted file: "+binOutf.getPath());
				return true;
			} else {
				return false;
			}
		}
		return true;
	}	
	
	/**
	 * Removes the output files in encode/ and decode/ directories
	 *
	 * @return true, if successful
	 */
	private boolean removeOutputFiles() {
		boolean status = true;
		binOutf = new File(decodeFile);	
		System.out.println("Checking if file exists: "+binOutf.getPath());
		if (binOutf.exists()) {
			if (binOutf.delete()) {
				System.out.println("Deleted file: "+binOutf.getPath());
			} else {
				status = false;
			}
		}
		if (status) {
			binOutf = new File(encodeFile);	
			System.out.println("Checking if file exists: "+binOutf.getPath());
			if (binOutf.exists()) {
				if (binOutf.delete()) {
					System.out.println("Deleted file: "+binOutf.getPath());
				} else {
					status = false;
				}
			}
		}
		return status;
	}	
	
	/**
	 * Read byte from the BufferedInputStream
	 *
	 * @param bInS the buffered input stream
	 * @return the byte
	 */
	private byte readByteFromInStream(BufferedInputStream bInS) {
		byte readByte=0;
		try {
			 readByte = (byte) bInS.read();
		} catch (IOException e) {
			System.out.println("Caught Exception while trying to read a byte");
			e.printStackTrace();
		}
		return readByte;
	}
		
	/**
	 * Check binary output.
	 *
	 * @param binOutf the bin outf
	 * @param expectedSize the expected size
	 * @return true, if successful
	 */
	private boolean checkBinaryOutput(File binOutf, int expectedSize) {
		if (!binOutf.exists()) {
			System.out.println("   Encode did not create file "+binOutf.getPath());
			return false;
		}
		long bfLen = binOutf.length();
		if (bfLen == 0) {
			System.out.println("   Encode created empty file "+binOutf.getPath());
			return false;			
		} else if (bfLen != expectedSize) {
			System.out.println("   Encode created file "+binOutf.getPath()+", but file size does not match expectations");
			System.out.println("   Expected Length: "+expectedSize);
			System.out.println("   Actual Length:   "+bfLen);
			return false;			
		}
		return true;
	}
	
	/**
	 * Read 8 bytes.
	 *
	 * @param bis the bufferedInputStream
	 * @param expect - the expected values of the first 8 bytes
	 * @return true, if successful
	 */
	private boolean read8Bytes(BufferedInputStream bis, int[] expect) {
		int [] actual = new int[8];
		boolean status = true;
		try {
			for (int i = 0; i < 8; i++) {
				actual[i] = (bis.read() & 0xff);
				status = status && (actual[i] == expect[i]);
//				System.out.printf("%d,",actual[i]);
			}
			bis.close();
		} catch (IOException e) {
			System.out.println("IO Exception occurred while reading binary file\n");
			e.printStackTrace();
		}
		return status;
	}

}
