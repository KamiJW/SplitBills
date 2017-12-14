package com.google.cloud.solutions.flexenv.common;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dasheng on 12/14/17.
 */

public class Account {
    // fields for account info
    private String name;
    private double balance;
    private List<Account> friends;
    private List<Transaction> transactionList; // transactions a user have
    private List<Debt> debtList; // debts a user have

    public Account(String name) {
        this.name = name;
        this.balance = 0;
        this.friends = new ArrayList<Account>();
        this.transactionList = new ArrayList<Transaction>();
        this.debtList = new ArrayList<Debt>();
    }
    // Before adding an acc to the friendlist, need to check if the added account
    // is already in the friendlist and if it is the account itself
    public void addFriend(Account acc) {
        if((acc != this) && (!friends.contains(acc))) {
            friends.add(acc);
            // once the acc is added to this.friendlist
            // let acc.friendlist have this
            acc.addFriend(this);
        }
    }
    public void deleteFriend(Account acc){
        if(friends.contains(acc)){
            friends.remove(acc);
        }
    }
    public double addBalance(double val) {
        balance += val;
        return balance;
    }
    // once the transaction is added to the transactionlist,
    // let initiator be the original account
    public void addTrans(Transaction tran){
        transactionList.add(tran);
        tran.setInitiator(this);
    }
    // when a new debt is added to the debtlist,
    // set the account as lender of the debt
    public void addDebtAsLender(Debt d) {
        if((d != null) && (d.getDebtor() != this)) {
            debtList.add(d);
            d.setLender(this);
        }
    }
    // when a new debt is added to the debtlist,
    // set the account as debtor of the debt
    public void addDebtAsDebtor(Debt d) {
        if((d != null) && (d.getLender() != this)) {
            debtList.add(d);
            d.setDebtor(this);
        }
    }
    public void removeDebt(Debt d){
        debtList.remove(d);
    }

    public List<Debt> getDebt(){
        return this.debtList;
    }

    public boolean hasAcc(Account acc){
        for(Account a: friends){
            if(a == acc) {
                return true;
            }
        }
        return false;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public List<Account> getFriends() {
        return friends;
    }

    public void setFriends(List<Account> friends) {
        this.friends = friends;
    }

    public List<Transaction> getTransactionList() {
        return transactionList;
    }

    public void setTransactionList(List<Transaction> transactionList) {
        this.transactionList = transactionList;
    }
}
