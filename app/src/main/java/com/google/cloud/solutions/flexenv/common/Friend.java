package com.google.cloud.solutions.flexenv.common;

import android.net.Uri;
import android.widget.EditText;

/**
 * Created by Jingwei Shen on 2017/12/14.
 */

public class Friend {
    private String Email;
    private String Debt;
    private String Photo;

    public Friend(String email, String debt, String photo){
        Email = email;
        Debt = debt;
        Photo = photo;
    }

    public String getEmail(){
        return Email;
    }

    public String getDebt(){
        return Debt;
    }

    public String getPhoto(){
        return Photo;
    }

}
