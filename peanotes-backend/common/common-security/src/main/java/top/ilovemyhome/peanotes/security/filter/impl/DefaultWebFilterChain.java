package top.ilovemyhome.peanotes.security.filter.impl;

import top.ilovemyhome.peanotes.security.domain.ServerWebExchange;
import top.ilovemyhome.peanotes.security.filter.WebFilter;
import top.ilovemyhome.peanotes.security.filter.WebFilterChain;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;


public class DefaultWebFilterChain implements WebFilterChain {

    private final List<WebFilter> allFilters;

    private final WebHandler handler;

    private final WebFilter currentFilter;

    private final DefaultWebFilterChain chain;


    public DefaultWebFilterChain(WebHandler handler, List<WebFilter> filters) {
        this.allFilters = Collections.unmodifiableList(filters);
        this.handler = handler;
        DefaultWebFilterChain chain = initChain(filters, handler);
        this.currentFilter = chain.currentFilter;
        this.chain = chain.chain;
    }

    private static DefaultWebFilterChain initChain(List<WebFilter> filters, WebHandler handler) {
        DefaultWebFilterChain chain = new DefaultWebFilterChain(filters, handler, null, null);
        ListIterator<? extends WebFilter> iterator = filters.listIterator(filters.size());
        while (iterator.hasPrevious()) {
            chain = new DefaultWebFilterChain(filters, handler, iterator.previous(), chain);
        }
        return chain;
    }

    /**
     * Private constructor to represent one link in the chain.
     */
    private DefaultWebFilterChain(List<WebFilter> allFilters, WebHandler handler,
                                  WebFilter currentFilter, DefaultWebFilterChain chain) {

        this.allFilters = allFilters;
        this.currentFilter = currentFilter;
        this.handler = handler;
        this.chain = chain;
    }

    @Deprecated
    public DefaultWebFilterChain(WebHandler handler, WebFilter... filters) {
        this(handler, Arrays.asList(filters));
    }


    public List<WebFilter> getFilters() {
        return this.allFilters;
    }

    public WebHandler getHandler() {
        return this.handler;
    }


    @Override
    public void filter(ServerWebExchange exchange) {
        this.currentFilter != null && this.chain != null ?
            invokeFilter(this.currentFilter, this.chain, exchange) :
            this.handler.handle(exchange);
    }

    private void invokeFilter(WebFilter current, DefaultWebFilterChain chain, ServerWebExchange exchange) {
        current.filter(exchange, chain);
    }

}
