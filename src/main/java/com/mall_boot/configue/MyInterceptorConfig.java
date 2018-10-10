package com.mall_boot.configue;

import org.springframework.context.annotation.Configuration;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
/**
 * Created by jiangxiao on 2018/10/2.
 */
@Configuration
public class MyInterceptorConfig implements WebMvcConfigurer {
//    /**
//     * 注入拦截器
//     */
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        //将拦截器注入进程序，可同时注入多个拦截器
//        //registry.addInterceptor(new RequestInterceptor()).addPathPatterns("/**");
//        /*
//        使用addPathPatterns增加拦截规则，使用excludePathPatterns排除拦截规则
//        /admin/**：代表http://域名/admin/** 拦截该目录下的所有目录及子目录
//        /admin：代表http://域名/admin 仅拦截此形式访问（无法拦截/admin/ 形式）
//        /admin/*：代表http://域名/admin/* 拦截该目录的所有下级目录不包含子目录（可以拦截/admin/ 形式）
//         */
//        registry.addInterceptor(new RequestInterceptor())
//                .addPathPatterns("/admin/**")
//                .excludePathPatterns("/admin")
//                .excludePathPatterns("/admin/*")
//                .excludePathPatterns("/admin/content/**");
//    }

    /**
     * 注入路径匹配规则
     */
    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        //设置忽略请求URL的大小写
        AntPathMatcher matcher = new AntPathMatcher();
        matcher.setCaseSensitive(false);
        configurer.setPathMatcher(matcher);
        //设置匹配规则
        /*
         setUseSuffixPatternMatch : 设置是否是后缀模式匹配，如“/user”是否匹配/user.*，默认true即匹配
         setUseTrailingSlashMatch : 设置是否自动后缀路径模式匹配，如“/user”是否匹配“/user/”，默认true即匹配
         */
        configurer.setUseSuffixPatternMatch(false).setUseTrailingSlashMatch(false);
    }



}

