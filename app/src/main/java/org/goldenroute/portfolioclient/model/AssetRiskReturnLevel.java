package org.goldenroute.portfolioclient.model;

public class AssetRiskReturnLevel {
    private String ticker;
    private double weight;
    private double expectedReturn;
    private double standardDeviation;
    private double sharpe;

    public String getTicker() {
        return ticker;
    }

    public double getWeight() {
        return weight;
    }

    public double getExpectedReturn() {
        return expectedReturn;
    }

    public double getStandardDeviation() {
        return standardDeviation;
    }

    public double getSharpe() {
        return sharpe;
    }

    public AssetRiskReturnLevel(String ticker, double weight, double expectedReturn, double standardDeviation, double sharpe) {
        this.ticker = ticker;
        this.weight = weight;
        this.expectedReturn = expectedReturn;
        this.standardDeviation = standardDeviation;
        this.sharpe = sharpe;
    }
}
