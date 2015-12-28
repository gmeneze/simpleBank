package simpleBank;

import java.io.Console;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Application {

	// Customer information is stored in a HashMap for constant time upload and
	// retrieval, the account number is the key.
	private static Map<Integer, Customer> newMap = new HashMap<Integer, Customer>();

	/*
	 * To upload data to and load data from the master file, data is uploaded to
	 * the file whenever application quits and is loaded from the file whenever
	 * the application is booted. This is done in order to maintain existing
	 * customer information and history.
	 */
	private static FileIO fileOperation = new FileIO();

	// To take user input.
	private static Scanner input = new Scanner(System.in);

	// To take user's password as input silently. Password will be received as character array.
	private static Console console = System.console();

	/*
	 * To maintain a counter of wrong attempts in entering the account number
	 * and passcode, the user gets 3 attempts for each after which the
	 * application quits.
	 */
	private static int WRONG_ATTEMPT_COUNTER = 0;

	// To check status of various method calls.
	private static boolean status;

	public static void main(String[] args) {

		printWelcomeMsg();

		// Load data from the master file. If file is empty, no data is loaded
		// and the flow continues.
		fileOperation.readFile();
		do {

			printHelp();

			System.out.print(">");

			String option = input.next();

			// User input is not case sensitive.
			if (option.trim().equalsIgnoreCase("quit")) {
				fileOperation.writeFile(newMap);
				System.out.println("Thank you for banking with us.");
				break;
			}

			else if (option.trim().equalsIgnoreCase("open")) {
				openNewAccount();
			}

			else if (option.trim().equalsIgnoreCase("balance")) {
				status = balanceEnquiry();

				if (!status)
					break;
			}

			else if (option.trim().equalsIgnoreCase("deposit")) {
				status = depositMoney();

				if (!status)
					break;
			}

			else if (option.trim().equalsIgnoreCase("withdraw")) {
				status = withdrawMoney();

				if (!status)
					break;
			}

			else if (option.trim().equalsIgnoreCase("transfer")) {
				status = transferMoney();

				if (!status)
					break;
			} else if (option.trim().equalsIgnoreCase("close")) {
				status = closeAccount();

				if (!status)
					break;
			}

			else {
				System.out.println(" Invalid option, Please try again. ");
			}

		} while (true);

	}

	public static void printWelcomeMsg() {

		System.out.println("\n Welcome to Simple Bank... ");

	}

	public static void printHelp() {

		System.out.println("\nYou can perform the following operations .. ");
		System.out.println(" Open - to open a new account");
		System.out.println(" Balance - For balance Enquiry");
		System.out.println(" Deposit - to deposit money");
		System.out.println(" Withdraw - to withdraw money");
		System.out.println(" Close - to close an existing account");
		System.out.println(" Transfer - to transfer funds within SimpleBank");
		System.out.println(" Quit - to exit application");

	}

	public static void openNewAccount() {

		// Taking customer details and assignment of account number is done
		// within the Constructor.
		Customer newCustomer = new Customer();

		newMap.put(newCustomer.getAccountNumber(), newCustomer);

	}

	public static boolean balanceEnquiry() {

		// Check if the account number belongs to an existing customer.
		Customer custDetails = verifyAccount();

		// if the account number entered is invalid after 3 attempts exit the
		// application.
		if (custDetails == null) {
			return false;
		} else {

			// verify the customer's passcode.
			status = verifyPasscode(custDetails);

			// if the passcode entered is invalid after 3 attempts exit the
			// application.
			if (!status) {
				return false;
			}

			/*
			 * check if the account is Open or not, if not, no balance
			 * information will be displayed, customer will be notified that the
			 * account is closed.
			 */
			status = checkAccountStatus(custDetails);

			if (!status) {
				return true;
			} else {
				custDetails.displayDetails();
				return true;
			}

		}
	}

	public static boolean depositMoney() {

		Customer custDetails = verifyAccount();
		if (custDetails == null) {
			return false;
		} else {

			status = verifyPasscode(custDetails);
			if (!status) {
				return false;
			}

			status = checkAccountStatus(custDetails);
			if (!status) {
				return true;
			}

			System.out.println("Please enter the amount you wish to deposit: ");
			System.out.print(">");

			custDetails.setAmount(custDetails.getAmount() + input.nextDouble());

			// show confirmation that the deposit was made.
			System.out.println("You now have $" + custDetails.getAmount()
					+ " in your account");

			newMap.put(custDetails.getAccountNumber(), custDetails);
			return true;
		}
	}

	public static boolean withdrawMoney() {

		Customer custDetails = verifyAccount();
		double amount;

		if (custDetails == null) {
			return false;

		} else {

			status = verifyPasscode(custDetails);
			if (!status) {
				return false;
			}

			status = checkAccountStatus(custDetails);
			if (!status) {
				return true;
			}

			// Check if the customer has non zero account balance, else
			// withdrawal cannot be made.
			amount = checkAmount(custDetails);

			if (amount == 0) {
				return true;
			}

			custDetails.setAmount(custDetails.getAmount() - amount);

			// display the remaining balance in customer's account.
			System.out.println("You now have $" + custDetails.getAmount()
					+ " in your account");

			newMap.put(custDetails.getAccountNumber(), custDetails);
			return true;

		}
	}

	public static boolean closeAccount() {

		Customer custDetails = verifyAccount();

		if (custDetails == null) {

			return false;

		} else {

			status = verifyPasscode(custDetails);
			if (!status) {
				return false;
			}

			status = checkAccountStatus(custDetails);
			if (!status) {
				return true;
			}

			System.out
					.println("Do you wish to continue? \n Yes - to continue \n No - to Quit");
			System.out.print(">");

			/*
			 * re-confirmation to ensure that the customer wishes to close the
			 * account, potential enhancement would be to display offers at this
			 * stage, to retain the customer.
			 */
			if (input.next().trim().equalsIgnoreCase("yes")) {

				custDetails.setAccountStatus(AccountStatus.CLOSED);

				System.out.println("Account " + custDetails.getAccountNumber()
						+ " has been closed, $ " + custDetails.getAmount()
						+ " will be refunded");
			}

			return true;
		}
	}

	/*
	 * Money can be transferred only from a source account which is - 
	 * 1. Valid
	 * 2. Open 
	 * 3. has enough account balance.
	 * 
	 * Money can be transferred to a target account which is - 
	 * 1. Valid 
	 * 2. Open.
	 */

	public static boolean transferMoney() {
		Customer sourceAccount = verifyAccount();
		Customer targetAccount;
		double amount;
		// verify source customer's account number.
		if (sourceAccount == null) {
			return false;

		} else {
			// verify source customer's passcode.
			status = verifyPasscode(sourceAccount);
			if (!status) {
				return false;
			}

			// check if the source account is Open.
			status = checkAccountStatus(sourceAccount);
			if (!status) {
				return true;
			}

			// Check if source account has enough balance.
			amount = checkAmount(sourceAccount);
			if (amount == 0) {
				return true;
			}

			System.out.println(" Please provide target customer details - ");
			// verify target customer.
			targetAccount = verifyAccount();

			if (targetAccount == null) {
				return false;

			} else {
				// check if targetAccount is open.
				status = checkAccountStatus(targetAccount);
				if (!status) {
					return true;
				}

				sourceAccount.setAmount(sourceAccount.getAmount() - amount);
				targetAccount.setAmount(targetAccount.getAmount() + amount);

				newMap.put(sourceAccount.getAccountNumber(), sourceAccount);
				newMap.put(targetAccount.getAccountNumber(), targetAccount);

				// Display confirmation of fund transfer.
				System.out.println("Funds transfer was successful!");

				// display the remaining balance in source customer's account.
				System.out.println("You now have $" + sourceAccount.getAmount()
						+ " in your account");

				return true;
			}

		}
	}

	// Transactions can be performed only for accounts in OPEN status, check
	// status of the account, display appropriate message if closed.
	public static boolean checkAccountStatus(Customer inputCust) {

		if (inputCust.getAccountStatus() == AccountStatus.CLOSED) {
			System.out.println(" Sorry, The account "
					+ inputCust.getAccountNumber() + " is closed.");
			return false;
		}

		return true;
	}

	// Verify that the account number exists in the HashMap, 3 attempts are
	// granted after which the application exits.
	public static Customer verifyAccount() {

		WRONG_ATTEMPT_COUNTER = 0;

		Customer custDetails;

		do {
			System.out.println(" Please enter the bank account number");

			System.out.print(">");

			int accountNumber = input.nextInt();

			custDetails = newMap.get(accountNumber);

			if (custDetails == null) {

				WRONG_ATTEMPT_COUNTER++;

				System.out.println("Account not found, you have exhausted "
						+ WRONG_ATTEMPT_COUNTER + " out of 3 attempts..");
			}

		} while (custDetails == null && WRONG_ATTEMPT_COUNTER < 3);

		if (WRONG_ATTEMPT_COUNTER == 3) {

			System.out
					.println("You have exceeded maximum number of attempts..Quitting");

			return null;
		} else {

			return custDetails;
		}
	}

	// Verify that the entered passcode is valid, 3 attempts are granted after
	// which the application exits.User's passcode is received silently.
	public static boolean verifyPasscode(Customer custDetails) {

		WRONG_ATTEMPT_COUNTER = 0;

		do {

			if (Arrays.equals(custDetails.getPin(),
					console.readPassword("Enter your pin:"))) {

				return true;

			} else {

				WRONG_ATTEMPT_COUNTER++;

				System.out.println("Incorrect Pin entered, you have exhausted "
						+ WRONG_ATTEMPT_COUNTER + " out of 3 attempts..");
			}
		} while (WRONG_ATTEMPT_COUNTER < 3);

		System.out
				.println("You have exceeded maximum number of attempts..Quitting");

		return false;
	}

	public static double checkAmount(Customer custDetails) {
		double amount;

		// Withdrawal or money transfer can be done only if the customer has
		// non-zero account balance.
		if (custDetails.getAmount() == 0) {
			System.out
					.println("Sorry, you do not have any funds to withdraw/transfer ");
			return 0;
		}

		// Customer cannot withdraw/transfer more money than she holds in her
		// account.
		do {

			System.out
					.println("Please enter the amount you wish to withdraw/transfer: ");

			System.out.print(">");

			amount = input.nextDouble();

			if (custDetails.getAmount() - amount < 0) {
				System.out.println("You have only $" + custDetails.getAmount()
						+ " in your account.Please try again.");
			} else {
				break;
			}

		} while (true);

		return amount;
	}

	// Upload existing customer data from master file each time the application
	// boots.
	public static void accountUpload(Customer c) {

		// Upload customer details into the Map
		newMap.put(c.getAccountNumber(), c);

		// ACC_NUM_GENERATOR should be set to a value equal to the max
		// accountNumber in the file.
		Customer.numberGenerator();

	}
}
