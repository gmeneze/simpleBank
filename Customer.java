package simpleBank;

import java.io.Console;
import java.util.Scanner;

public class Customer {

	private int accountNumber;
	private String firstName;
	private String lastName;
	private double amount;
	private char[] pin;
	private AccountStatus accountStatus;

	// To take user input.
	private static Scanner input = new Scanner(System.in);

	// to receive user's password silently.
	private static Console console = System.console();

	// Variable to be used to generate unique customer account numbers, account
	// numbers will be in sequence.
	private static int ACC_NUM_GENERATOR = 10000;

	// Constructor overloaded to create customer profile, the customer details
	// will be uploaded to HashMap.
	public Customer() {

		System.out.println("Creating new account... ");

		System.out.println(" Enter First name: ");
		System.out.print(">");
		firstName = input.next();

		System.out.println(" Enter Last name: ");
		System.out.print(">");
		lastName = input.next();

		System.out.println(" Enter amount: ");
		System.out.print(">");
		amount = input.nextDouble();

		System.out.println(" Generating account number ..");
		accountNumber = numberGenerator();
		System.out.println(" Your Account number is " + accountNumber
				+ ". \n Please make a note of it for future reference");

		pin = console.readPassword("Enter your password:");

		accountStatus = AccountStatus.OPEN;
	}

	// Parameterized constructor used to create Customer objects from master
	// file. These objects are then uploaded to the HashMap.
	public Customer(int accountNumber, String firstName, String lastName,
			double amount, char[] pin, AccountStatus accountStatus) {

		this.accountNumber = accountNumber;
		this.firstName = firstName;
		this.lastName = lastName;
		this.amount = amount;
		this.pin = pin;
		this.accountStatus = accountStatus;

	}

	// toString method is overloaded to generate a string with customer
	// information to be written to master file when application quits.
	@Override
	public String toString() {
		return accountNumber + ":" + firstName + ":" + lastName + ":" + amount
				+ ":" + String.valueOf(pin) + ":" + accountStatus + "\n";
	}

	public static int getACC_NUM_GENERATOR() {

		return ACC_NUM_GENERATOR;
	}

	// Generates unique AccountNumber for each new Customer.
	public static int numberGenerator() {

		return ACC_NUM_GENERATOR++;
	}

	// No setter has been provided for accountNumber, it should not be modified
	// once assigned.
	public int getAccountNumber() {

		return accountNumber;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public double getAmount() {

		return amount;
	}

	public void setAmount(double amount) {

		this.amount = amount;
	}

	public char[] getPin() {

		return pin;
	}

	public void setPin(char[] pin) {

		this.pin = pin;
	}

	public AccountStatus getAccountStatus() {
		return accountStatus;
	}

	public void setAccountStatus(AccountStatus accountStatus) {
		this.accountStatus = accountStatus;
	}

	public void displayDetails() {

		System.out.println(" Your account details are  ");
		System.out.println("\n First Name: " + this.firstName);
		System.out.println(" Last Name: " + this.lastName);
		System.out.println(" Account Balance: " + this.amount + "\n");

	}

}