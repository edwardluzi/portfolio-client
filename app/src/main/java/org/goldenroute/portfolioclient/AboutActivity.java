package org.goldenroute.portfolioclient;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.util.Linkify;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        TextView textView = (TextView) findViewById(R.id.text_view_about_info);
        textView.setText(Html.fromHtml(readRawTextFile(R.raw.about)));
        Linkify.addLinks(textView, Linkify.ALL);
    }

    public String readRawTextFile(int id) {

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(getResources().openRawResource(id)));
        StringBuilder text = new StringBuilder();
        String line;

        try {
            while ((line = bufferedReader.readLine()) != null) text.append(line);
        } catch (IOException e) {
            return "";
        }

        return text.toString();
    }
}
