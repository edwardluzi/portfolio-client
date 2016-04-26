package org.goldenroute.portfolioclient.model;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Portfolio {
    private Long id;
    private String name;
    private String benchmark;
    private Currency currency;
    private AssetClass primaryAssetClass;
    private String description;
    private BigDecimal value;
    private BigDecimal cost;
    private BigDecimal dailyChange;
    private BigDecimal dailyChangePercentage;
    private BigDecimal totalChange;
    private BigDecimal totalChangePercentage;

    private List<Transaction> transactions;
    private List<Holding> holdings;
    private BigDecimal weight;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBenchmark() {
        return benchmark;
    }

    public void setBenchmark(String benchmark) {
        this.benchmark = benchmark;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public AssetClass getPrimaryAssetClass() {
        return primaryAssetClass;
    }

    public void setPrimaryAssetClass(AssetClass primaryAssetClass) {
        this.primaryAssetClass = primaryAssetClass;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getValue() {
        return value;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public BigDecimal getDailyChange() {
        return dailyChange;
    }

    public BigDecimal getDailyChangePercentage() {
        return dailyChangePercentage;
    }

    public BigDecimal getTotalChange() {
        return totalChange;
    }

    public BigDecimal getTotalChangePercentage() {
        return totalChangePercentage;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public List<Holding> getHoldings() {
        return holdings;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public Transaction find(final Long tid) {
        for (Transaction transaction : this.transactions) {
            if (transaction.getId() == tid) {
                return transaction;
            }
        }
        return null;
    }
}