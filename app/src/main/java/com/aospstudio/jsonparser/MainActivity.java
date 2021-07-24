package com.aospstudio.jsonparser;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;

import org.json.JSONException;

public class MainActivity extends AppCompatActivity {

    private Context context = this;
    private MaterialToolbar toolbar;
    private ProgressBar progressBar;
    private BottomSheetDialog sheet;
    private TextView text, msg;
    private MaterialButton button_ok, button_close;
    private JsonObjectRequest jsonObjectRequest;
    private final String webserver_url = "https://aospstudio.com/api/apps/github.json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        progressBar = findViewById(R.id.progressBar);

        AppController.getInstance().getRequestQueue().getCache().remove(webserver_url);
        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, webserver_url, null, response -> {
            try {
                boolean status = response.getBoolean("status");
                String title = response.getString("app_name");
                progressBar.setVisibility(View.GONE);
                toolbar.setTitle(title);
                if (!status) {
                    sheet = new BottomSheetDialog(context);
                    sheet.setContentView(R.layout.sheet_main);
                    sheet.setCancelable(false);
                    sheet.setDismissWithAnimation(true);
                    text = sheet.findViewById(R.id.text);
                    text.setText("Application not allowed");
                    msg = sheet.findViewById(R.id.message);
                    msg.setText("Access to this app is forbidden by server side");
                    button_close = sheet.findViewById(R.id.button_close);
                    button_close.setText("close app");
                    button_close.setOnClickListener(view -> {
                        finish();
                        sheet.dismiss();
                    });
                    button_ok = sheet.findViewById(R.id.button_ok);
                    button_ok.setVisibility(View.GONE);
                    sheet.show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                progressBar.setVisibility(View.GONE);
                sheet = new BottomSheetDialog(context);
                sheet.setContentView(R.layout.sheet_main);
                sheet.setCancelable(false);
                sheet.setDismissWithAnimation(true);
                text = sheet.findViewById(R.id.text);
                text.setText("There is a problem");
                msg = sheet.findViewById(R.id.message);
                msg.setText("There was a problem retrieving information from the server, please try again");
                button_close = sheet.findViewById(R.id.button_close);
                button_close.setText("close app");
                button_close.setOnClickListener(view -> {
                    finish();
                    sheet.dismiss();
                });
                button_ok = sheet.findViewById(R.id.button_ok);
                button_ok.setText("Try again");
                button_ok.setOnClickListener(view -> {
                    AppController.getInstance().cancelPendingRequests(response);
                    reConnectVolley();
                    progressBar.setVisibility(View.VISIBLE);
                    sheet.dismiss();
                });
                sheet.show();
            }
        }, error -> {
            progressBar.setVisibility(View.GONE);
            sheet = new BottomSheetDialog(context);
            sheet.setContentView(R.layout.sheet_main);
            sheet.setCancelable(false);
            sheet.setDismissWithAnimation(true);
            text = sheet.findViewById(R.id.text);
            text.setText("Could not connect to server");
            msg = sheet.findViewById(R.id.message);
            msg.setText("There was a problem connecting to the server and the application cannot start");
            button_close = sheet.findViewById(R.id.button_close);
            button_close.setText("close app");
            button_close.setOnClickListener(view -> {
                finish();
                sheet.dismiss();
            });
            button_ok = sheet.findViewById(R.id.button_ok);
            button_ok.setText("Try again");
            button_ok.setOnClickListener(view -> {
                reConnectVolley();
                sheet.dismiss();
            });
            sheet.show();
        });

        AppController.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    private void reConnectVolley() {
        AppController.getInstance().getRequestQueue().getCache().remove(webserver_url);
        AppController.getInstance().addToRequestQueue(jsonObjectRequest);
        progressBar.setVisibility(View.VISIBLE);
    }
}
