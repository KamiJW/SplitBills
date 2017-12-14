package com.google.cloud.solutions.flexenv.common;

/**
 * Created by Dasheng on 12/14/17.
 */

class Debt {
    private Account lender;
    private Account debtor;
    private double netBalance;


    public Debt(Account lender, Account debtor, double netBalance) {
        this.lender = lender;
        this.debtor = debtor;
        this.netBalance = netBalance;
    }

    public Account getLender() {
        return lender;
    }

    public void setLender(Account newLender) {
        if((newLender != lender) && (newLender != null)){
            this.lender = newLender;
            lender.addDebtAsLender(this);
        }
    }

    public Account getDebtor() {
        return debtor;
    }

    public void setDebtor(Account newDebtor) {
        if((newDebtor != debtor) && (newDebtor != null)){
            this.debtor = newDebtor;
            debtor.addDebtAsDebtor(this);
        }
    }

    public double getNetBalance() {
        return netBalance;
    }

    public void setNetBalance(double netBalance) {
        this.netBalance = netBalance;
    }
}
