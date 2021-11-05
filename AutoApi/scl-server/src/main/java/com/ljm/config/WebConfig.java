package com.ljm.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;


/**
 * 跨域请求支持/token拦截
 * tip:只能写在一个配置类里
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private TokenInterceptor tokenInterceptor;

    @Autowired
    private CurrentUserMethodArgumentResolver currentUserMethodArgumentResolver;

    /**
     * 拦截器配置
     * @param registry
     */
    public void addInterceptors(InterceptorRegistry registry){
        //记录不拦截的路径
        List<String> excludePath = new ArrayList<>();
        excludePath.add("/user/**");  //登录
        //excludePath.add("/static/**");  //静态资源
        //excludePath.add("/assets/**");  //静态资源

        registry.addInterceptor(tokenInterceptor)
                .addPathPatterns("/**")  //对所有请求都拦截
                .excludePathPatterns(excludePath); //排除部分拦截路径
        //WebMvcConfigurer.super.addInterceptors(registry);
    }


    /**
     * 跨域CORS配置
     * @param registry
     */
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowCredentials(true)
                .allowedHeaders("*")
                .allowedMethods("*");
    }

    /**
     * 参数解析器
     *
     * @param argumentResolvers
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(currentUserMethodArgumentResolver);
    }


    /**
     * 用来专门注册一个Handler，来处理静态资源的，例如：图片，js，css等
     * @param registry
     */
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //可以通过http://127.0.0.1:8080/web/home.html访问resources/web/home.html页面
        registry.addResourceHandler("/web/**").addResourceLocations("classpath:/web/");
    }
}
