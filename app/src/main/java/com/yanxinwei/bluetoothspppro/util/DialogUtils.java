package com.yanxinwei.bluetoothspppro.util;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.yanxinwei.bluetoothspppro.R;

/**
 * DialogUtils
 * Created by yanxinwei on 16/10/9.
 */

public class DialogUtils {

    public interface DialogInputListener {
        void onInput(String content);
    }

    public static void showDialogInput(String title, Context context, final DialogInputListener listener) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_input, null);
        dialogBuilder.setView(view);
        dialogBuilder.setTitle(title);

        final EditText editText = (EditText) view.findViewById(R.id.edt_content);
        Button btnConfirm = (Button) view.findViewById(R.id.btn_confirm);
        final AlertDialog alertDialog = dialogBuilder.show();
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = editText.getText().toString().trim();
                listener.onInput(content);
                alertDialog.dismiss();
            }
        });
        editText.requestFocus();
    }

}
