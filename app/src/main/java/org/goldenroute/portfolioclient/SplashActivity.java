package org.goldenroute.portfolioclient;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import java.io.File;

public class SplashActivity extends AppCompatActivity {
    private static final long SPLASH_DELAY_MILLIS = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initialize();
    }

    private void initialize() {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                gotoMain();
            }
        }, SPLASH_DELAY_MILLIS);
    }

    private void gotoMain() {
        if (fileExists("stack.trace")) {
            startActivity(new Intent(this, SendLogsActivity.class));
        } else {
            startActivity(new Intent(this, LoginActivity.class));
        }
        finish();
    }

    public boolean fileExists(String filename) {
        File file = getFileStreamPath(filename);
        return !(file == null || !file.exists());
    }
}
