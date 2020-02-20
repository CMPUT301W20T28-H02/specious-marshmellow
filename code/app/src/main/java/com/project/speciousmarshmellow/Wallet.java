//package com.project.speciousmarshmellow;
//
//public class Wallet {
//    private double balance;
//
//    public Wallet() {
//        this.balance = 0;
//    }
//
//    public Wallet(double balance) {
//        this.balance = balance;
//    }
//
//    public double checkBalance() {
//        // return the amount in the user wallet
//        return this.balance;
//
//    }
//
//    public void depositQRBucks(QRCode qrBucks) {
//        // add the total generated from the qrBucks to the user balance
//        this.balance += qrBucks.getAmount();
//    }
//
//    public QRCode withdrawQRBucks(double amount) {
//        // decrease the user balance by the amount to withdraw
//        // generate qr code for the amount
//        this.balance -= amount;
//
//        QRCode qrCode = new QRCode();
//        return qrCode;
//    }
//}
