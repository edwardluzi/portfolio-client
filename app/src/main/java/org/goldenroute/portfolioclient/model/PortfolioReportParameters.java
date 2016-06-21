package org.goldenroute.portfolioclient.model;

public class PortfolioReportParameters {

    private double riskFree;
    private double riskAversion;
    private boolean shortingAllowed;
    private String intervals;

    public PortfolioReportParameters()
    {
    }

    public PortfolioReportParameters(double riskFree, double riskAversion, boolean shortingAllowed, String intervals)
    {
        this.riskFree = riskFree;
        this.riskAversion = riskAversion;
        this.shortingAllowed = shortingAllowed;
        this.intervals = intervals;
    }

    public double getRiskFree()
    {
        return riskFree;
    }

    public void setRiskFree(double riskFree)
    {
        this.riskFree = riskFree;
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
