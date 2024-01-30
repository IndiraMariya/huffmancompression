package myfileio;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * The Class MyFileIO.
 */
public class MyFileIO {
	
	/** The Constant FILE_OK. Indicates that the file can be opened for read or write */
	public static final int FILE_OK=0;
	
	/** The Constant EMPTY_NAME. */
	public static final int EMPTY_NAME=1;
	
	/** The Constant NOT_A_FILE. */
	public static final int NOT_A_FILE = 2;
	
	/** The Constant READ_EXIST_NOT. */
	public static final int FILE_DOES_NOT_EXIST=3;
	
	/** The Constant READ_ZERO_LENGTH. */
	public static final int READ_ZERO_LENGTH=4;
	
	/** The Constant NO_READ_ACCESS. */
	public static final int NO_READ_ACCESS=5;
	
	/** The Constant NO_WRITE_ACCESS. */
	public static final int NO_WRITE_ACCESS=6;
	
	/** The Constant WRITE_EXISTS. */
	public static final int WRITE_EXISTS=7;
	
	/**
	 * Returns the File descriptor (Handle) for the given path.
	 *
	 * @param filename  the filename, including relative or absolute path
	 * @return  File object which contains information about the file
	 */
	public File getFileHandle (String filename) {
		if (filename == null) return null;
		return (new File(filename));
	}
	
	/**
	 * Creates an empty file, if it is safe to do so
	 * Pre-checks to be performed
	 *  1) check for an empty filename
	 *  2) check if the file exists 
	 *  If these checks occur, the file will not be created.
	 *  Otherwise, returns the result of the File createNewFile() method
	 *  This of course means that you need to get the File handle!
	 *  
	 *  Errors are not expected (given 1 and 2), but it is possible that
	 *  this method will generate an IOException or SecurityException.
	 *  Both should be caught here: the catch should print out a message
	 *  to the console indicating that the error occurred, and then 
	 *  call e.printStackTrace();
	 *
	 * @param filename the filename
	 * @return true, if successful
	 */
	public boolean createEmptyFile(String filename) {
	    File fh = getFileHandle(filename);
	    boolean status = false;
	    if (fh == null) return status;
	    if ("".equals(filename) || fh.exists())
	    	return status; 
	    try {
	    	status = fh.createNewFile();
	    }
	    catch (IOException e) {
	    	System.out.println("An IOException occurred.");
	    	e.printStackTrace();
	    }
	    catch (SecurityException e) {
	    	System.out.println("A Security Exception occurred.");
	    	e.printStackTrace();
	    }
	    return status;
	}
	
	/**
	 * Delete file.  Uses the File delete() method to delete a file
	 * Pre-checks:
	 * 1) the filename must not be empty
	 * 2) file must exist
	 * 3) file must actually be a file
	 * If these conditions are not met, the delete() should not be attempted
	 * 
	 *  Errors are not expected, but it is possible that
	 *  this method will generate a SecurityException.
	 *  This should be caught here: the catch should print out a message
	 *  to the console indicating that the error occurred, and then 
	 *  call e.printStackTrace();
	 *
	 * @param filename the filename
	 * @return true, if successful
	 */
	public boolean deleteFile(String filename) {
	    File fh = getFileHandle(filename);
	    boolean status = false;
	    if (fh == null) return status;
	    if ("".equals(filename) || !fh.exists() || !fh.isFile())
	    	return status; 
	    try {
	    	status = fh.delete();
	    }
	    catch (SecurityException e) {
	    	System.out.println("A Security Exception occurred.");
	    	e.printStackTrace();
	    }
	    return status;	
	}	

	/**
	 * This method checks information about the file handle 
	 * to determine if it is safe to read or write the file,
	 * based upon the requested access type.
	 * 
	 * If the name of the file is empty, return the appropriate
	 * error code - this does NOT depend upon the value of read
	 * 
	 * Conditions to read the file:
	 *   a) file exists
	 *   b) file is indeed a file
	 *   c) file has data
	 *   d) file has read access
	 *   
	 * Conditions to write the file:
	 *   a) file does not exist 
	 *   b) file exists, is writable, and is a file. Note that in this
	 *      case, you should should return WRITE_EXISTS to indicate
	 *      that the file can be written but does already exist.
	 *      
	 * If the conditions are not met, you should return the correct failure
	 * status. 
	 *
	 * @param file is the File descriptor. 
	 * @param read if true, perform the read checks; if false,
	 *        perform the write checks
	 * @return the results of the status checks. These are the
	 *        constants defined at the top of the file.
	 */
	public int checkFileStatus(File file, boolean read) {
		if ("".equals(file.getName())) 
			return EMPTY_NAME;
		if (read) {
			if (!file.exists())
				return FILE_DOES_NOT_EXIST;
			if (!file.isFile())
				return NOT_A_FILE;
			if (file.length() == 0)
				return READ_ZERO_LENGTH;
			if (!file.canRead()) 
				return NO_READ_ACCESS;
		} else {
			if (file.exists()) {
				if (!file.canWrite()) 
					return NO_WRITE_ACCESS;
				return (file.isFile() ? WRITE_EXISTS : NOT_A_FILE);
			}
		}
		return FILE_OK;
	}
	
	/**
	 * Open a FileReader object for this File handle. 
	 * 
	 * Any exceptions should be caught here, but should NOT
	 * terminate - just return a null pointer.
	 *
	 * @param file the file handle
	 * @return FileReader object if successful, otherwise null
	 */
	public FileReader openFileReader(File file) {
		FileReader fr = null;
		try {
			fr = new FileReader(file);
		}
		catch (FileNotFoundException e) {
			System.out.println("ERROR - Did not find file for reading!!");
		}
		return fr;
	}
	
	/**
	 * Open file input stream. Catch exceptions and warn user.
	 *
	 * @param file the file
	 * @return the file input stream - returns null if an error occured 
	 */
	public FileInputStream openFileInputStream(File file ) {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			System.out.println("ERROR - Unable to read file or open as FileInputStream");
		}
		return fis;
	}
	
	/**
	 * Open FileWriter object for this File handle. 
	 * 
	 * Any exceptions should be caught here, but should NOT
	 * terminate - just return a null pointer.
	 *
	 * @param file the file handle
	 * @return FileWriter object if successful, otherwise null
	 */
	public FileWriter openFileWriter(File file) {
		FileWriter fw = null;
		try {
			fw = new FileWriter(file);
		}
		catch (IOException e) {
			System.out.println("IO Exception error - could not open file for writing!!");
		}
		return fw;
	}
	
	/**
	 * Open file output stream. Catch exceptions and warn user
	 *
	 * @param file the file
	 * @return the file output stream - returns null if an error occured
	 */
	public FileOutputStream openFileOutputStream(File file ) {
		FileOutputStream fos = null;

		try {
			fos = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			System.out.println("ERROR - Unable to write file or open as FileOutputStream");
		}
		return fos;
	}
	
	
	/**
	 * Open BufferedReader object for this File handle. 
	 *
	 * @param file the file handle
	 * @return BufferedReader object if successful, otherwise null
	 */
	public BufferedReader openBufferedReader(File file) {
		BufferedReader br = null;
		FileReader fr = openFileReader(file);
		if (fr != null)
			br = new BufferedReader(fr);
		return br;
	}
	
	/**
	 * Open buffered input stream.
	 *
	 * @param file the file
	 * @return the buffered input stream - returns null if an error occured
	 */
	public BufferedInputStream openBufferedInputStream(File file) {
		BufferedInputStream bis = null;
		FileInputStream fis = openFileInputStream(file);
		if (fis != null)
			bis = new BufferedInputStream(fis);
		return bis;
	}
	
	/**
	 * Open BufferedWriter object for this File handle. 
	 *
	 * @param file the file handle
	 * @return BufferedWriter object if successful, otherwise null
	 */
	public BufferedWriter openBufferedWriter(File file) {
		BufferedWriter bw = null;
		FileWriter fw = openFileWriter(file);
		if (fw != null)
			bw = new BufferedWriter(fw);
		return bw;
	}
	
	/**
	 * Open buffered output stream.
	 *
	 * @param file the file
	 * @return the buffered output stream
	 */
	public BufferedOutputStream openBufferedOutputStream(File file) {
		BufferedOutputStream bos = null;
		FileOutputStream fos = openFileOutputStream(file);
		if (fos != null)
			bos = new BufferedOutputStream(fos);
		return bos;
	}
	
	/**
	 * Close file. 
	 *  Catches IOException if it occurs - output an error message, then 
	 *  e.printStackTrace
	 *
	 * @param fr the FileReader object
	 */
	public void closeFile(FileReader fr) {
		try {
			fr.close();
		}
		catch (IOException e) {
			System.out.println("Attempt to close FileReader failed");
			e.printStackTrace();
		}
	}
	
	/**
	 * Flush and Close file. 
	 *  Catches IOException if it occurs - output an error message, then 
	 *  e.printStackTrace
	 *
	 * @param fw the FileWriter object
	 */
	public void closeFile(FileWriter fw) {
		try {
			fw.flush();
			fw.close();
		}
		catch (IOException e) {
			System.out.println("Attempt to flush and close FileWriter failed");
			e.printStackTrace();
		}
	}

	/**
	 * Close file. 
	 *  Catches IOException if it occurs - output an error message, then 
	 *  e.printStackTrace
	 *
	 * @param br the BufferedReader object
	 */
	public void closeFile(BufferedReader br) {
		try {
			br.close();
		}
		catch (IOException e) {
			System.out.println("Attempt to close BufferedReader failed");
			e.printStackTrace();
		}
	}

	/**
	 * Flush and Close file. 
	 *  Catches IOException if it occurs - output an error message, then 
	 *  e.printStackTrace
	 *
	 * @param bw the BufferedWriter object
	 */
	public void closeFile(BufferedWriter bw) {
		try {
			bw.flush();
			bw.close();
		}
		catch (IOException e) {
			System.out.println("Attempt to flush and close BufferedWriter failed");
			e.printStackTrace();
		}
	}
	
	/**
	 * Close stream. Catch exceptions, inform user and print stack trace
	 *
	 * @param bis the bis
	 */
	public void closeStream(BufferedInputStream bis) {
		try {
			bis.close();
		}
		catch (IOException e) {
			System.out.println("Attempt to close BufferedInputStream failed");
			e.printStackTrace();
		}
	}

	/**
	 * Close stream. Catch exceptions, inform user and print stack trace
	 *
	 * @param bos the bos
	 */
	public void closeStream(BufferedOutputStream bos) {
		try {
			bos.flush();
			bos.close();
		}
		catch (IOException e) {
			System.out.println("Attempt to flush and close BufferedOutputStream failed");
			e.printStackTrace();
		}
	}

}
