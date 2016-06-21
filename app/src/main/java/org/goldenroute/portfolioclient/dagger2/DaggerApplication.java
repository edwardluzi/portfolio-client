package org.goldenroute.portfolioclient.dagger2;


import android.app.Application;

public class DaggerApplication extends Application {

    private ApplicationComponent mComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        mComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .build();
    }

    public ApplicationComponent getComponent() {
        return mComponent;
    }
}
