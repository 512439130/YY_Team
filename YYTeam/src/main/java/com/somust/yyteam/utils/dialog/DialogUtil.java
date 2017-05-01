package com.somust.yyteam.utils.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.somust.yyteam.R;

/**
 * Created by 13160677911 on 2017-4-22.
 */

public class DialogUtil {
    public static String inputData = null;

    public static String buttonFlag = "";
    /**
     * 弹出输入提示框
     */
    public static String showEditDialog(final Context context,String dialogName) {
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.dialog_edit, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(promptsView);
        final TextView dialog_name_tv = (TextView) promptsView.findViewById(R.id.dialog_name_tv);
        dialog_name_tv.setText(dialogName);
        final EditText userInput = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput);

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                inputData = userInput.getText().toString();
                                buttonFlag = "确定";
                            }
                        })
                .setNegativeButton("取消",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                                buttonFlag = "取消";
                            }
                        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        return inputData;
    }



    /**
     * 弹出无输入确认框
     */
    public static void showNullDialog(Context context,String dialogName) {
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.dialog_null_edit, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(promptsView);

        TextView dialog_name = (TextView) promptsView.findViewById(R.id.dialog_name_tv);
        dialog_name.setText(dialogName);

        alertDialogBuilder
                .setCancelable(false)
                .setNegativeButton("确定",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                buttonFlag = "确定";
                            }
                        })
                .setPositiveButton("取消",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                buttonFlag = "取消";
                                dialog.cancel();
                            }
                        });


        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

}
