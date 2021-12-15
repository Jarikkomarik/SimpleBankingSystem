package banking;

import java.sql.*;

public class BankDB {  //covering communication with Sqlite3 DB
    private static Connection conn;

    public static void closeConnection() {
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void InitializeTable(String fileName) {
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:" + fileName);
            if (conn != null) {
                createTable();
            }

        } catch (SQLException e) {
            try {
                conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            System.out.println(e.getMessage());
        }
    }

    private static void createTable() {
        try (Statement statement = conn.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS card (\n"
                    + "	id INTEGER PRIMARY KEY,\n"
                    + "	number TEXT NOT NULL,\n"
                    + "	pin TEXT NOT NULL,\n"
                    + "	balance INTEGER DEFAULT 0\n"
                    + ");");
        } catch (SQLException throwables) {
            System.out.println("Failed to create table!");
            throwables.printStackTrace();
        }
    }

    public static void putCardIntoCardDB(Card card) {
        try (Statement statement = conn.createStatement()) {
            statement.executeUpdate("INSERT INTO card (number, pin) VALUES (\n"
                    + card.getCardNumber() + ",\n"
                    + card.getPinCode() + "\n"
                    + ");");
        } catch (SQLException throwables) {
            System.out.println("Failed to put card!");
            throwables.printStackTrace();
        }
    }

    public static boolean checkIfNumberIsUsed(long num) {
        try (Statement statement = conn.createStatement()) {
            try (ResultSet resSet = statement.executeQuery("SELECT number FROM card WHERE number = " + num + ";")) {
                if (resSet.next()) {
                    return true;
                } else {
                    return false;
                }
            }
        } catch (SQLException throwables) {
            System.out.println("Failed to check if number is used!");
            throwables.printStackTrace();
        }
        throw new RuntimeException();
    }

    public static Card returnCardFromDatabase(long number) {
        try (Statement statement = conn.createStatement()) {
            try (ResultSet resSet = statement.executeQuery("SELECT number, pin, balance FROM card WHERE number = " + number + ";")) {
                if (resSet.next()) {
                    return new Card(resSet.getLong("number"), resSet.getInt("pin"), resSet.getLong("balance"));
                } else {
                    return null;
                }
            }
        } catch (SQLException throwables) {
            System.out.println("Failed to check if card is used!");
            throwables.printStackTrace();
        }
        throw new RuntimeException();
    }

    public static String changeBalance(int income, long accountNumber) {
        try (Statement statement = conn.createStatement()) {
            statement.executeUpdate("UPDATE card " +
                    "SET balance = balance + " + income +
                    " WHERE number = " + accountNumber + ";");
            return "Income was added!";
        } catch (SQLException throwables) {
            return "Failed to addIncome()";
        }
    }

    public static int getBalance(long accountNumber) {
        try (Statement statement = conn.createStatement()) {
            try (ResultSet resultSet = statement.executeQuery("SELECT balance FROM card WHERE number = " + accountNumber + ";")) {
                return resultSet.getInt(1);
            }
        } catch (SQLException throwables) {
            System.out.println("Failed to get balance.");
            throwables.printStackTrace();
            return 0;
        }
    }

    public static String closeAccount(long accountNumber) {
        try (Statement statement = conn.createStatement()) {
            statement.executeUpdate("DELETE FROM card " +
                    " WHERE number = " + accountNumber + ";");
            return "The account has been closed!";
        } catch (SQLException throwables) {
            return "Failed to close account";
        }
    }

    public static String doTransfer(long fromAccountNumber, long toAccountNumber, int amount) {
        String debitQuery = "UPDATE card SET balance = balance + " + amount + " WHERE number = " + toAccountNumber + " ;";
        String creditQuery = "UPDATE card SET balance = balance - " + amount + " WHERE number = " + fromAccountNumber + " ;";
        Savepoint s1 = null;
        try (PreparedStatement p1 = conn.prepareStatement(debitQuery);
             PreparedStatement p2 = conn.prepareStatement(creditQuery)) {
            conn.setAutoCommit(false);
            s1 = conn.setSavepoint();
            p1.executeUpdate();
            p2.executeUpdate();
            conn.commit();
        } catch (SQLException throwables) {
            try {
                conn.rollback(s1);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return "Failed to do transfer";
        }

        try {
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Success!";
    }

}
