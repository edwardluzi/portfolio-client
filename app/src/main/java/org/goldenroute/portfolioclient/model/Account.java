package org.goldenroute.portfolioclient.model;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@SuppressWarnings("InstanceVariableNamingConvention")
public class Account {
    private Long id;
    private String username;
    private Profile profile;
    private List<Portfolio> portfolios;

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public List<Portfolio> getPortfolios() {
        return portfolios;
    }

    public void setPortfolios(List<Portfolio> portfolios) {
        this.portfolios = portfolios;
    }

    public Portfolio find(final Long pid) {
        for (Portfolio portfolio : this.portfolios) {
            if (portfolio.getId().equals(pid)) {
                return portfolio;
            }
        }
        return null;
    }

    public void addOrUpdate(Portfolio portfolio) {

        if (this.portfolios == null) {
            this.portfolios = new ArrayList<>();
        }

        int index = Collections.binarySearch(this.portfolios, portfolio, new Comparator<Portfolio>() {
            @Override
            public int compare(Portfolio lhs, Portfolio rhs) {
                return (int) (lhs.getId() - rhs.getId());
            }
        });

        if (index >= 0) {
            this.portfolios.set(index, portfolio);
        } else {
            this.portfolios.add(portfolio);
        }
    }

    public void remove(Collection<Long> pids) {
        for (Long pid : pids) {
            Portfolio portfolio = find(pid);
            if (portfolio != null) {
                this.portfolios.remove(portfolio);
            }
        }
    }
}
