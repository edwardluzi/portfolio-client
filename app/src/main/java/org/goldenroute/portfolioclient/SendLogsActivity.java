package org.goldenroute.portfolioclient;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SendLogsActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String NEW_LINE = System.getProperty("line.separator");

    @Bind(R.id.button_send_logs)
    protected Button mButtonSend;

    @Bind(R.id.button_skip_send_logs)
    protected Button mButtonSkip;

    @Bind(R.id.text_view_send_log_description)
    protected TextView mTextViewDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_logs);
        ButterKnife.bind(this);

        mButtonSend.setOnClickListener(this);
        mButtonSkip.setOnClickListener(this);

        mTextViewDescription.setText(Html.fromHtml(getString(R.string.label_send_log_description)));
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.button_send_logs:
                sendLogs();
                break;
            case R.id.button_skip_send_logs:
                gotoMain();
                break;
        }
    }

    private void sendLogs() {
        StringBuilder report = new StringBuilder();

        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(this.openFileInput("stack.trace")));
            String line;
            while ((line = reader.readLine()) != null) {
                report.append(line);
                report.append(IntentConstants.NEW_LINE);
            }
            reader.close();
            deleteFile("stack.trace");
        } catch (IOException e) {
            e.printStackTrace();
        }

        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("plain/text");
        sendIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"edward.yh.lu@gmail.com"});
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Logs");
        sendIntent.putExtra(Intent.EXTRA_TEXT, report.toString());

        try {
            startActivityForResult(sendIntent, IntentConstants.RC_EMAIL);
        } catch (ActivityNotFoundException ae) {
            ae.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IntentConstants.RC_EMAIL) {
            gotoMain();
        }
    }

    private void gotoMain() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
