package com.example.common;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * 跨域配置
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        // 1. CORS 配置容器
        CorsConfiguration config = new CorsConfiguration();
        // 2. 允许前端所在的域名（不能用 "*"，否则带凭证会被浏览器拒绝）
        config.addAllowedOrigin("http://localhost:5173");
        // 如果你的前端域名不固定，也可以：
        // config.addAllowedOriginPattern("*");
        // 3. 允许携带 Cookie
        config.setAllowCredentials(true);
        // 4. 允许所有请求头
        config.addAllowedHeader(CorsConfiguration.ALL);
        // 5. 允许所有请求方法
        config.addAllowedMethod(CorsConfiguration.ALL);
        // （可选）预检请求的缓存时间，单位秒
        config.setMaxAge(3600L);

        // 把上面配置映射到所有接口
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        // 返回新的 CorsFilter，Spring 会自动注册
        return new CorsFilter(source);
    }
}