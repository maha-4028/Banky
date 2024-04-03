package BankingSystem;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class BankY {
    private static final String DATABASE_FILE = "accounts.db";
    private Map<String, Account> accounts;
    private Scanner scanner;

    public BankY() {
        accounts = new HashMap<>();
        scanner = new Scanner(System.in);
        loadAccounts();
    }

    private void loadAccounts() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(DATABASE_FILE))) {
            accounts = (HashMap<String, Account>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            // If file not found or error reading, continue with an empty database
        }
    }

    private void saveAccounts() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATABASE_FILE))) {
            oos.writeObject(accounts);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createAccount(String accountNumber, String accountHolderName) {
        synchronized (accounts) {
            if (!accounts.containsKey(accountNumber)) {
                Account account = new Account(accountNumber, accountHolderName);
                accounts.put(accountNumber, account);
                saveAccounts();
                System.out.println("Account created successfully");
            } else {
                System.out.println("Account already exists");
            }
        }
    }

    public void deposit(String accountNumber, double amount) {
        synchronized (accounts) {
            Account account = accounts.get(accountNumber);
            if (account != null) {
                account.deposit(amount);
                saveAccounts();
                System.out.println("Amount deposited successfully");
            } else {
                System.out.println("Account not found");
            }
        }
    }

    public void withdraw(String accountNumber, double amount) {
        synchronized (accounts) {
            Account account = accounts.get(accountNumber);
            if (account != null) {
                if (account.withdraw(amount)) {
                    saveAccounts();
                    System.out.println("Amount withdrawn successfully");
                } else {
                    System.out.println("Insufficient balance");
                }
            } else {
                System.out.println("Account not found");
            }
        }
    }

    public void displayBalance(String accountNumber) {
        synchronized (accounts) {
            Account account = accounts.get(accountNumber);
            if (account != null) {
                System.out.println("Balance: " + account.getBalance());
            } else {
                System.out.println("Account not found");
            }
        }
    }

    public static void main(String[] args) {
        BankY bank = new BankY();
        Scanner scanner = new Scanner(System.in);
        int choice;

        do {
            System.out.println("\n1. Create Account");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. Display Balance");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    System.out.print("Enter account number: ");
                    String accNum = scanner.next();
                    System.out.print("Enter account holder name: ");
                    String accHolder = scanner.next();
                    bank.createAccount(accNum, accHolder);
                    break;
                case 2:
                    System.out.print("Enter account number: ");
                    String depositAccNum = scanner.next();
                    System.out.print("Enter amount to deposit: ");
                    double depositAmount = scanner.nextDouble();
                    bank.deposit(depositAccNum, depositAmount);
                    break;
                case 3:
                    System.out.print("Enter account number: ");
                    String withdrawAccNum = scanner.next();
                    System.out.print("Enter amount to withdraw: ");
                    double withdrawAmount = scanner.nextDouble();
                    bank.withdraw(withdrawAccNum, withdrawAmount);
                    break;
                case 4:
                    System.out.print("Enter account number: ");
                    String balanceAccNum = scanner.next();
                    bank.displayBalance(balanceAccNum);
                    break;
                case 5:
                    System.out.println("Exiting...");
                    break;
                default:
                    System.out.println("Invalid choice");
            }
        } while (choice != 5);

        scanner.close();
    }
}

