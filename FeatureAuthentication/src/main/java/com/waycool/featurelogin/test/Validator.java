package com.waycool.featurelogin.test;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class Validator implements TextWatcher {
    private boolean mIsValid = false;
    int i;
    public Validator(int i){
        this.i = i;
    }
    public boolean isValid() {
        return mIsValid;
    }
    public boolean checkMobileNumber(CharSequence number){
        if(number.toString().trim().length()==10)
            return true;
        return false;
    }
    public boolean checkOTPNumber(CharSequence otp){
        if (otp.toString().length()!= 4) {
            return false;
        }else if(otp.toString().trim().isEmpty()){
            return false;
        }
        return true;
    }

    public boolean checkNmaeField(EditText name){
        if(name.getText().toString().trim().isEmpty())
            return false;
        return true;
    }
    public boolean checkLocationField(EditText location){
        location.setText("testname");
        if(location.getText().toString().trim().isEmpty())
            return false;
        return true;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        if(i==1) {
            mIsValid = checkMobileNumber(editable);
        }else if(i==2){
            mIsValid = checkOTPNumber(editable);
        }

    }
}

