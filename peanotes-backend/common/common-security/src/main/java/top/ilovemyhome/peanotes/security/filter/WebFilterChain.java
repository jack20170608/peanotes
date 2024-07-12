package top.ilovemyhome.peanotes.security.filter;

import top.ilovemyhome.peanotes.security.domain.ServerWebExchange;

public interface WebFilterChain {

	void filter(ServerWebExchange exchange);

}
