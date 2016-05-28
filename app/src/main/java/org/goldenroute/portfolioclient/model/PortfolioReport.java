package org.goldenroute.portfolioclient.model;

import java.util.List;
import java.util.Map;

@SuppressWarnings("InstanceVariableNamingConvention")
public class PortfolioReport {
    private Long portfolioId;
    private String portfolioName;
    private List<String> symbols;
    private MarkowitzPortfolio overall;
    private Map<String, MarkowitzPortfolio> individuals;
    private EfficientFrontier efficientFrontier;

    public PortfolioReport(Long portfolioId, String portfolioName, List<String> symbols, MarkowitzPortfolio overall,
                           Map<String, MarkowitzPortfolio> individuals, EfficientFrontier efficientFrontier) {
        this.portfolioId = portfolioId;
        this.portfolioName = portfolioName;
        this.symbols = symbols;
        this.overall = overall;
        this.individuals = individuals;
        this.efficientFrontier = efficientFrontier;
    }

    public Long getPortfolioId() {
        return portfolioId;
    }

    public String getPortfolioName() {
        return portfolioName;
    }

    public List<String> getSymbols() {
        return symbols;
    }

    public MarkowitzPortfolio getOverall() {
        return overall;
    }

    public Map<String, MarkowitzPortfolio> getIndividuals() {
        return individuals;
    }

    public EfficientFrontier getEfficientFrontier() {
        return efficientFrontier;
    }
}
