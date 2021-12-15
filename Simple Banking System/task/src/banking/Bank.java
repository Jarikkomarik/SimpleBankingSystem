package banking;

import java.util.Scanner;

public class Bank {     // guides client through application.
    private static Scanner sc = new Scanner(System.in);

    public static void runBank(String dbName) {
        BankDB.InitializeTable(dbName);    //initialize Bank DB
        while (true) {
            System.out.print(
                    "1. Create an account\n" +
                            "2. Log into account\n" +
                            "0. Exit\n>");
            int input = sc.nextInt();
            if (input == 1) {
                createNewAccount();
            } else if (input == 2) {
                System.out.print("\nEnter your card number:\n");
                Long accNum = sc.nextLong();
                System.out.print("Enter your PIN:\n");
                int pin = sc.nextInt();
                logIn(accNum, pin);
            } else if (input == 0) {
                System.out.println("\nBye!\n");
                BankDB.closeConnection();
                System.exit(0);
            }
        }
    }

    public static void createNewAccount() {
        Card temp = new Card();
        BankDB.putCardIntoCardDB(temp);
        System.out.println("\nYour card has been created");
        System.out.println("Your card number:");
        System.out.println(temp.getCardNumber());
        System.out.println("Your card PIN:");
        System.out.println(temp.getPinCode() + "\n");
    }

    public static void logIn(long accNum, int pin) {
        Card temp = BankDB.returnCardFromDatabase(accNum);
        if (temp == null) {
            System.out.println("\nWrong card number or PIN!\n");
            return;
        } else if (temp.getPinCode() == pin) {
            System.out.println("\nYou have successfully logged in!\n");
            AccountOperation(temp.getCardNumber());
        } else {
            System.out.println("Wrong card number or PIN!");
        }
    }


    private static void AccountOperation(long accountNumber) {
        boolean iterate = true;
        while (iterate) {

            System.out.print(
                    "1. Balance\n" +
                            "2. Add income\n" +
                            "3. Do doTransfer\n" +
                            "4. Close account\n" +
                            "5. Log Out\n" +
                            "0. Exit\n");

            int input = sc.nextInt();

            switch (input) {
                case 1:
                    getBalance(accountNumber);
                    break;

                case 2:
                    addIncome(accountNumber);
                    break;

                case 3:
                    doTransfer(accountNumber);
                    break;

                case 4:
                    closeAccount(accountNumber);
                    iterate = false;
                    break;

                case 5:
                    iterate = false;
                    System.out.println("\nYou have successfully logged out!\n");
                    break;

                case 0:
                    System.out.print("\nBye!\n");
                    BankDB.closeConnection();
                    System.exit(0);
                    break;

                default:
                    System.out.println("Incorrect input");
            }
        }
    }

    private static void addIncome(long accountNumber) {
        System.out.println("\nEnter income:");
        int income = sc.nextInt();
        System.out.println(BankDB.changeBalance(income, accountNumber) + "\n");
    }

    private static void getBalance(long accountNumber) {
        System.out.println("\nBalance: " + BankDB.getBalance(accountNumber) + "\n");
    }

    private static void closeAccount(long accountNumber) {
        System.out.println("\n" + BankDB.closeAccount(accountNumber) + "\n");
    }

    private static void doTransfer(long accountNumber) {
        int balance = BankDB.getBalance(accountNumber);
        System.out.println("\nTransfer\n" +
                "Enter card number:");
        long inputAccountNumber = sc.nextLong();


        if (inputAccountNumber == accountNumber) {
            System.out.println("You can't doTransfer money to the same account!\n");
            return;
        } else if (Card.isNotLunh(inputAccountNumber)) {
            System.out.println("Probably you made a mistake in the card number. Please try again!\n");
            return;
        } else if (!BankDB.checkIfNumberIsUsed(inputAccountNumber)) {
            System.out.println("Such a card does not exist.\n");
            return;
        }

        System.out.println("Enter how much money you want to doTransfer:");
        int transferAmount = sc.nextInt();

        if (transferAmount > balance) {
            System.out.println("Not enough money!\n");
            return;
        }

        System.out.println(BankDB.doTransfer(accountNumber, inputAccountNumber, transferAmount) + "\n");

    }
}
