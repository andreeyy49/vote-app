package com.example.firebaselearning;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

public class DialogLoading {
    Context context;
    Dialog dialog;

    public DialogLoading(Context context){
        this.context = context;
    }

    public void showDialog(){
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.activity_dialog_loading);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        //init

        dialog.create();
        dialog.show();
    }

    public void closeDialog(){
        dialog.dismiss();
    }
}
