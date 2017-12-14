package com.google.cloud.solutions.flexenv.common;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Dasheng on 12/14/17.
 */

public abstract class Transaction {
    private Account initiator;
    private List<Account> involved;
    private double dividedAmount;
    private String title;
    private String use;
    private Date dateCreated;
    private boolean isSolved;
    private List<Debt> debtInvolved;

    public Transaction(Account initiator, List<Account> involved, double dividedAmount, String title, String use) {
        this.initiator = initiator;
        this.involved = involved;
        this.dividedAmount = dividedAmount;
        this.title = title;
        this.use = use;
        this.isSolved = false;
        for(Account a: involved){
            a.addTrans(this);
        }
        this.debtInvolved = new ArrayList<Debt>();
//        for(Account a: involved){
//            debtInvolved.add(new Debt(this.initiator, a, dividedAmount));
//        }
    }


    public Account getInitiator() {
        return initiator;
    }

    public void setInitiator(Account newInitiator) {
        if((initiator != newInitiator) && (newInitiator != null)) {
            this.initiator = newInitiator;
            this.initiator.addTrans(this);
        }
    }
    public void initDebt(){}

    public List<Account> getInvolved() {
        return involved;
    }

    public void setInvolved(List<Account> involved) {
        this.involved = involved;
    }

    public double getDividedAmount() {
        return dividedAmount;
    }

    public void setDividedAmount(double dividedAmount) {
        this.dividedAmount = dividedAmount;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUse() {
        return use;
    }

    public void setUse(String use) {
        this.use = use;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public boolean isSolved() {
        return isSolved;
    }

    public void setSolved(boolean solved) {
        isSolved = solved;
    }
}
