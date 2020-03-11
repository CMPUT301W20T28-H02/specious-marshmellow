public class Wallet {
    private double balance;

    public Wallet() {
        this.balance = 0;
    }

    public Wallet(double balance) {
        this.balance = balance;
    }

    // Getters
    public double getBalance() {
        return this.balance; // Return the amount in the user's wallet
    }

    // Add money to the wallet
    public void deposit(double amount){
      this.balance += amount;
    }

    // Take money out of the wallet
    public void withdraw(double amount){
      this.balance -= amount;
    }
}
