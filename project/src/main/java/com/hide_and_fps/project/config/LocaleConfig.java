package com.hide_and_fps.project.config;

import java.util.Locale;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
/*
@Configuration
public class LocaleConfig implements WebMvcConfigurer {

	@Bean
    public LocaleResolver localeResolver() {
        CookieLocaleResolver localeResolver = new CookieLocaleResolver();
        localeResolver.setCookieName("language");
        localeResolver.setCookieMaxAge(-1);
        localeResolver.setCookiePath("/");
        localeResolver.setDefaultLocale(Locale.KOREA);
        return localeResolver;
    }

	@Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
        localeChangeInterceptor.setParamName("language");
        return localeChangeInterceptor;
    }

	@Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }

	@Bean(name="messageSource")
    public ReloadableResourceBundleMessageSource messageSource() {
        ReloadableResourceBundleMessageSource source = new ReloadableResourceBundleMessageSource();
        source.setBasenames(
        		"classpath:/message/common"
        );
        source.setDefaultEncoding("UTF-8");
        return source;
    }

    @Bean(name="messageSourceAccessor")
    public MessageSourceAccessor getMessageSourceAccessor() {
        return new MessageSourceAccessor(this.messageSource());
    }
}
*/

