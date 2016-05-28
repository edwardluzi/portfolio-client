package org.goldenroute.portfolioclient.model;

@SuppressWarnings("InstanceVariableNamingConvention")
public class MarkowitzPortfolio {
    private double[] weights;
    private double expectedReturn;
    private double variance;

    public double[] getWeights() {
        return weights;
    }

    public double getExpectedReturn() {
        return expectedReturn;
    }

    public double getVariance() {
        return variance;
    }

    public MarkowitzPortfolio(double[] weights, double expectedReturn, double variance) {
        this.weights = weights;
        this.expectedReturn = expectedReturn;
        this.variance = variance;
    }
}
