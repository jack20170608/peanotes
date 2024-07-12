package top.ilovemyhome.peanotes.security.filter;

import top.ilovemyhome.peanotes.security.domain.ServerWebExchange;

public interface WebFilter {

	void filter(ServerWebExchange exchange, WebFilterChain chain);

}
