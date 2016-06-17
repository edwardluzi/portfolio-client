package org.goldenroute.portfolioclient.model;

public class PortfolioReportParameters {

    private double riskfree;
    private double riskAversion;
    private boolean shortingAllowed;
    private String intervals;

    public PortfolioReportParameters()
    {
    }

    public PortfolioReportParameters(double riskfree, double riskAversion, boolean shortingAllowed, String intervals)
    {
        this.riskfree = riskfree;
        this.riskAversion = riskAversion;
        this.shortingAllowed = shortingAllowed;
        this.intervals = intervals;
    }

    public double getRiskfree()
    {
        return riskfree;
    }

    public void setRiskfree(double riskfree)
    {
        this.riskfree = riskfree;
    }

    public double getRiskAversion()
    {
        return riskAversion;
    }

    public void setRiskAversion(double riskAversion)
    {
        this.riskAversion = riskAversion;
    }

    public boolean isShortingAllowed()
    {
        return shortingAllowed;
    }

    public void setShortingAllowed(boolean shortingAllowed)
    {
        this.shortingAllowed = shortingAllowed;
    }

    public String getIntervals()
    {
        return intervals;
    }

    public void setIntervals(String intervals)
    {
        this.intervals = intervals;
    }
}
