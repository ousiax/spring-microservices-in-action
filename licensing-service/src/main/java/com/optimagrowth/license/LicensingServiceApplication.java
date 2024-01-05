package com.optimagrowth.license;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import com.optimagrowth.license.utils.UserContextInterceptor;

import jakarta.servlet.http.HttpServletRequest;

@SpringBootApplication
@RefreshScope
@EnableDiscoveryClient
@EnableFeignClients
public class LicensingServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(LicensingServiceApplication.class, args);
	}

	// @Bean
	// LocaleResolver localeResolver() {
	// 	SessionLocaleResolver localeResolver = new SessionLocaleResolver();
	// 	localeResolver.setDefaultLocale(Locale.US);
	// 	return localeResolver;
	// }

	@Bean
	public ResourceBundleMessageSource messageSource() {
		ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
		messageSource.setUseCodeAsDefaultMessage(true);
		messageSource.setBasenames("messages");
		return messageSource;
	}

	@LoadBalanced
	@Bean
	RestTemplate getRestTemplate() {
		RestTemplate template = new RestTemplate();
		List<ClientHttpRequestInterceptor> interceptors = List.of(
				new UserContextInterceptor(),
				authorizationHeaderInterceptor());
		template.getInterceptors().addAll(interceptors);
		return template;
	}

	private ClientHttpRequestInterceptor authorizationHeaderInterceptor() {
		return (httpRequest, bytes, clientHttpRequestExecution) -> {
			String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
			if (authorizationHeader != null) {
				httpRequest.getHeaders().set(HttpHeaders.AUTHORIZATION, authorizationHeader);
			}
			return clientHttpRequestExecution.execute(httpRequest, bytes);
		};
	}

	// Actually, directly injecting a request or session-scoped bean into a singleton bean can lead to unintended side effects.
	//
	// A singleton bean is instantiated only once in the Spring container and it lives throughout the application's lifetime. On the other hand, a request or session-scoped bean is newly created for each HTTP request or session. When you try to inject such a scoped bean into a singleton bean:
	//
	// 1. **First problem:** The injection of the scoped bean into the singleton bean happens only once, when the singleton bean is created. After that, the singleton bean will always have a reference to the same instance of the scoped bean, even across different requests or sessions. This can lead to unexpected behavior and issues because you may end up in situations where a singleton bean is holding the state of an outdated scope.
	//
	// 2. **Second problem:** It can lead to concurrency issues in a multi-thread environment (like in a web application where each request is handled in a separate thread) because the singleton bean is shared across multiple threads but the scoped bean is not thread-safe.
	//
	// To tackle this scenario, Spring provides a feature known as scoped-proxy. By creating a proxy of the scoped bean(Spring creates a CGLIB or JDK Dynamic Proxy based proxy), the correct instance of the scoped bean is injected into the singleton bean, even when the scoping boundaries don’t match. The proxy ensures that the correct instance of the scoped bean is retrieved from the correct scope (like an HTTP request) and all method calls on the injected bean are delegated to the correct instance of the scoped bean.
	//
	// Here is an example of using a scoped proxy:
	//
	// ```java
	//     @Component
	//     @Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
	//     public class RequestScopedBean {
	//
	//     }
	// ```
	//
	// Then you can inject this request-scoped bean into a singleton bean:
	//
	// ```java
	// @Service
	// public class SingletonBean {
	//
	//   @Autowired
	//   private RequestScopedBean requestScopedBean;
	//
	// }
	// ```
	//
	// In this case, `requestScopedBean` inside `SingletonBean` is a proxy that fetches the actual request-scoped bean from the current request's context whenever it's accessed.
	//
	// But bear in mind, this adds an extra layer of complexity to your application and should be used carefully. An alternative way could be to pass the scoped bean as a method parameter where required rather than injecting it.
	@Autowired
	private HttpServletRequest request;
}