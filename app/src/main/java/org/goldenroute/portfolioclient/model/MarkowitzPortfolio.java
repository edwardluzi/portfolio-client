package org.goldenroute.portfolioclient.model;

@SuppressWarnings("InstanceVariableNamingConvention")
public class MarkowitzPortfolio {
    private double[] weights;
    private double expectedReturn;
    private double variance;
    private double standardDeviation;
    private double riskFree;
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

    public double getRiskFree() {
        return riskFree;
    }

    public double getSharpe() {
        return sharpe;
    }

    public MarkowitzPortfolio(double[] weights, double expectedReturn, double variance, double riskFree, double sharpe) {
        this.weights = weights;
        this.expectedReturn = expectedReturn;
        this.variance = variance;
        this.riskFree = riskFree;
        this.sharpe = sharpe;
    }
}
