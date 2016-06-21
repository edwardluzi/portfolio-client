package org.goldenroute.portfolioclient.dagger2;

import org.goldenroute.portfolioclient.services.RemoteService;
import org.goldenroute.portfolioclient.services.RemoteServiceImpl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ApplicationModule {
    private DaggerApplication mApplication;

    public ApplicationModule(DaggerApplication application) {
        mApplication = application;
    }

    @Provides
    @Singleton
    RemoteService providesRemoteService() {
        return new RemoteServiceImpl();
    }
}
