package org.goldenroute.portfolioclient.model;

@SuppressWarnings("InstanceVariableNamingConvention")
public class MarkowitzPortfolio {
    private double[] weights;
    private double expectedReturn;
    private double variance;
    private double standardDeviation;
    private double riskfree;
    private double sharpe;

    public double[] getWeights() {
        return weights;
    }

    public double getExpectedReturn() {
        return expectedReturn;
    }

    public double getExpectedReturnPercentage() {
        return expectedReturn * 100;
    }

    public double getVariance() {
        return variance;
    }

    public double getStandardDeviation() {
        return standardDeviation;
    }

    public double getStandardDeviationPercentage() {
        return standardDeviation * 100;
    }

    public double getRiskfree() {
        return riskfree;
    }

    public double getSharpe() {
        return sharpe;
    }

    public MarkowitzPortfolio(double[] weights, double expectedReturn, double variance, double riskfree, double sharpe) {
        this.weights = weights;
        this.expectedReturn = expectedReturn;
        this.variance = variance;
        this.riskfree = riskfree;
        this.sharpe = sharpe;
    }
}
