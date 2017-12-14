package com.google.cloud.solutions.flexenv.common;

import java.util.List;

/**
 * Created by Dasheng on 12/14/17.
 */

public class PayBill extends Transaction {
    public PayBill(Account initiator, List<Account> involved, double dividedAmount, String title, String use) {
        super(initiator, involved, dividedAmount, title, use);
    }

    public void initDebt(){
        Debt buff;
        for(Account a: this.getInvolved()){
            buff = new Debt(this.getInitiator(), a, this.getDividedAmount());
            this.getInitiator().addDebtAsDebtor(buff);
            a.addDebtAsLender(buff);
        }
    }
}
