package org.goldenroute.portfolioclient.dagger2;

import org.goldenroute.portfolioclient.AnalysisPortfolioListActivity;
import org.goldenroute.portfolioclient.MainActivity;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {ApplicationModule.class})
public interface ApplicationComponent {
    void inject(AnalysisPortfolioListActivity activity);

    void inject(MainActivity activity);
}