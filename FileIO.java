package simpleBank;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Map;

public class FileIO {

	// set the master file to which Customer information will be written to and
	// loaded from.
	private static final String BANK_ACCOUNT_FILE = "/Users/glenmenezes/Desktop/simpleBank/master.txt";

	// To improve reading and writing performance BufferedReader and
	// BufferedWriter classes are used.
	private BufferedReader buffReader = null;
	private BufferedWriter buffWriter = null;

	// To create the master file if it does not exist, if it exists do nothing.
	private void createFile() {
		File masterFile = new File(BANK_ACCOUNT_FILE);
		try {
			masterFile.createNewFile();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * To read data from the master file. Each line will contain Customer
	 * information, this information is used to create a customer object using
	 * parameterized Constructor of Customer class.This object is then used to
	 * upload customer information into HashMap using accountUpload method.
	 */
	public void readFile() {
		createFile();
		Customer account;
		try {
			buffReader = new BufferedReader(new FileReader(BANK_ACCOUNT_FILE));
			String accountData = null;
			while ((accountData = buffReader.readLine()) != null) {
				String[] split = accountData.split(":");

				account = new Customer(Integer.parseInt(split[0]), split[1],
						split[2], Double.parseDouble(split[3]),
						split[4].toCharArray(), AccountStatus.valueOf(split[5]));

				Application.accountUpload(account);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (buffReader != null)
				try {
					buffReader.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
		}

	}

	/*
	 * To write data to the master file. The toString method of Customer class
	 * is used to create a string with customer information. This string is then
	 * written to the master file.The master file is over-written each time the
	 * application ends so that it holds the latest customer info.
	 */
	public void writeFile(Map<Integer, Customer> newMap) {
		createFile();
		try {
			buffWriter = new BufferedWriter(new FileWriter(BANK_ACCOUNT_FILE));

			for (Customer account : newMap.values()) {
				buffWriter.write(account + "");
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (buffWriter != null)
				try {
					buffWriter.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
		}

	}
}
