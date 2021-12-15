package banking;

public class Card {  //covers all card processes. Generating new card number and pin. Generating pin.

    private long cardNumber;
    private int pinCode;
    private long balance;

    public long getBalance() {
        return balance;
    }

    public long getCardNumber() {
        return cardNumber;
    }

    public int getPinCode() {
        return pinCode;
    }

    Card() {
        cardNumber = generateCardNumber();
        pinCode = generateCardPin();
        balance = 0;
    }

    public Card(long cardNumber, int pinCode, long balance) {
        this.cardNumber = cardNumber;
        this.pinCode = pinCode;
        this.balance = balance;
    }

    private long generateCardNumber() {
        long tempAccountNumber = 4000000000000000l + addCardNumberChecker((long) Math.floor((Math.random() * 9_000_000_00L) + 1_000_000_00L) * 10);
        while (BankDB.checkIfNumberIsUsed(tempAccountNumber)) { //if number is used generate new number while unique number is not generated
            tempAccountNumber = 4000000000000000l + addCardNumberChecker((long) Math.floor((Math.random() * 9_000_000_00L) + 1_000_000_00L) * 10);
        }
        return tempAccountNumber;
    }

    private long addCardNumberChecker(long cardNumber) {
        String[] temp = String.valueOf(cardNumber).split("");
        int[] numbers = new int[9];
        int sum = 0;
        for (int i = 0; i < 9; i++) {
            if (i % 2 == 0) {
                numbers[i] = Integer.valueOf(temp[i]) * 2 > 9 ? Integer.valueOf(temp[i]) * 2 - 9 : Integer.valueOf(temp[i]) * 2;
            } else {
                numbers[i] = Integer.valueOf(temp[i]) > 9 ? Integer.valueOf(temp[i]) - 9 : Integer.valueOf(temp[i]);
            }
            sum += numbers[i];
        }
        return cardNumber += (sum + 8) % 10 == 0 ? 0 : 10 - ((sum + 8) % 10);
    }


    private static int generateCardPin() {
        return (int) Math.floor(Math.random() * 9000) + 1000;
    }

    public static boolean isNotLunh(long accountNumber) {
        String[] temp = String.valueOf(accountNumber).split("");
        int[] numbers = new int[16];
        int sum = 0;
        for (int i = 0; i < 16; i++) {
            if (i % 2 == 0) {
                numbers[i] = Integer.valueOf(temp[i]) * 2 > 9 ? Integer.valueOf(temp[i]) * 2 - 9 : Integer.valueOf(temp[i]) * 2;
            } else {
                numbers[i] = Integer.valueOf(temp[i]) > 9 ? Integer.valueOf(temp[i]) - 9 : Integer.valueOf(temp[i]);
            }
            sum += numbers[i];
        }
        return sum % 10 == 0 ? false : true;
    }


    @Override
    public String toString() {
        return "Card{" +
                "cardNumber=" + cardNumber +
                ", pinCode=" + pinCode +
                ", balance=" + balance +
                '}';
    }

}
