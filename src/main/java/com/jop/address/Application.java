package com.jop.address;

import static com.google.common.collect.Lists.newArrayList;
import static springfox.documentation.schema.AlternateTypeRules.newRule;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.filter.HttpPutFormContentFilter;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.schema.WildcardType;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import com.fasterxml.classmate.TypeResolver;
import com.jop.address.exception.AddressExceptionResolver;

@SpringBootApplication
@EnableSwagger2
@ComponentScan(basePackages = { "com.jop.address.controller", "com.jop.address.service" })
@EnableJpaRepositories(basePackages = "com.jop.address.repository")
@EntityScan(basePackages = "com.jop.address.domain")
public class Application extends WebMvcConfigurerAdapter {

	@Autowired
	private TypeResolver typeResolver;

	public static void main(String[] args) throws Exception {
		new SpringApplicationBuilder(Application.class).run(args);
	}

	@Bean
	public HttpPutFormContentFilter httpPutFormContentFilter() {
		return new HttpPutFormContentFilter();
	}

	@Bean
	public DispatcherServlet dispatcherServlet() {
		DispatcherServlet ds = new DispatcherServlet();
		ds.setThrowExceptionIfNoHandlerFound(true);
		return ds;
	}

	@Override
	public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
		exceptionResolvers.add(new AddressExceptionResolver());
	}

	@Bean
	public MethodValidationPostProcessor methodValidationPostProcessor() {
		return new MethodValidationPostProcessor();
	}

	@Bean
	public Docket addressApi() {
		return new Docket(DocumentationType.SWAGGER_2)
		    .groupName("address-api")
		    .select()
		    .apis(RequestHandlerSelectors.any())
		    .paths(PathSelectors.ant("/address/**"))
		    .build()
		    .pathMapping("/")
		    .directModelSubstitute(LocalDate.class, String.class)
		    .genericModelSubstitutes(ResponseEntity.class)
		    .alternateTypeRules(
		        newRule(
		            typeResolver.resolve(DeferredResult.class,
		                typeResolver.resolve(ResponseEntity.class, WildcardType.class)),
		            typeResolver.resolve(WildcardType.class)))
		    .useDefaultResponseMessages(false)
		    .globalResponseMessage(
		        RequestMethod.GET,
		        newArrayList(new ResponseMessageBuilder().code(500).message("500 message")
		            .responseModel(new ModelRef("Error")).build())).securitySchemes(newArrayList(apiKey()))
		    .securityContexts(newArrayList(securityContext())).enableUrlTemplating(false);
	}

	@Bean
	public Docket managerApi() {
		return new Docket(DocumentationType.SWAGGER_2)
		    .groupName("mamanger-api")
		    .select()
		    .apis(RequestHandlerSelectors.any())
		    .paths(PathSelectors.ant("/manager/**"))
		    .build()
		    .pathMapping("/")
		    .directModelSubstitute(LocalDate.class, String.class)
		    .genericModelSubstitutes(ResponseEntity.class)
		    .alternateTypeRules(
		        newRule(
		            typeResolver.resolve(DeferredResult.class,
		                typeResolver.resolve(ResponseEntity.class, WildcardType.class)),
		            typeResolver.resolve(WildcardType.class)))
		    .useDefaultResponseMessages(false)
		    .globalResponseMessage(
		        RequestMethod.GET,
		        newArrayList(new ResponseMessageBuilder().code(500).message("500 message")
		            .responseModel(new ModelRef("Error")).build())).securitySchemes(newArrayList(apiKey()))
		    .securityContexts(newArrayList(securityContext())).enableUrlTemplating(false);
	}

	private ApiKey apiKey() {
		return new ApiKey("mykey", "api_key", "header");
	}

	private SecurityContext securityContext() {
		return SecurityContext.builder()
		    .securityReferences(defaultAuth())
		    .forPaths(PathSelectors.regex("/anyPath.*"))
		    .build();
	}

	List<SecurityReference> defaultAuth() {
		AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
		AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
		authorizationScopes[0] = authorizationScope;
		return newArrayList(new SecurityReference("mykey", authorizationScopes));
	}

	@Bean
	SecurityConfiguration security() {
		return new SecurityConfiguration(
		    "test-app-client-id",
		    "test-app-realm",
		    "test-app",
		    "apiKey");
	}

	@Bean
	UiConfiguration uiConfig() {
		return new UiConfiguration("validatorUrl");
	}
}