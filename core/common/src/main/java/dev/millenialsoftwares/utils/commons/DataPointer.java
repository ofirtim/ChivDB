package dev.millenialsoftwares.utils.commons;

import dev.millenialsoftwares.utils.structs.ChivCondition;

import java.util.List;

/**
 * A general like interface for inheritance functionality only.
 */
public interface DataPointer<K, V> {

    public SearchResult getResults(int maxPolling);

    public SearchResult getResults();

    public List<ChivCondition> getFilters();

    public void addFilter(ChivCondition... filters);

    public <T> T project(T projection);
}