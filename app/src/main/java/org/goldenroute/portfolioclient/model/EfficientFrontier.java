package org.goldenroute.portfolioclient.model;


import java.util.List;

@SuppressWarnings("InstanceVariableNamingConvention")
public class EfficientFrontier {
    private List<MarkowitzPortfolio> frontiers;
    private MarkowitzPortfolio globalMinimumVariance;

    public EfficientFrontier(List<MarkowitzPortfolio> frontiers,
                             MarkowitzPortfolio globalMinimumVariance) {
        this.frontiers = frontiers;
        this.globalMinimumVariance = globalMinimumVariance;
    }

    public List<MarkowitzPortfolio> getFrontiers() {
        return frontiers;
    }

    public MarkowitzPortfolio getGlobalMinimumVariance() {
        return globalMinimumVariance;
    }
}
