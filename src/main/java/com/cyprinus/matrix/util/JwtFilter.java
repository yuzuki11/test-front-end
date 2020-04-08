package com.cyprinus.matrix.util;

import com.cyprinus.matrix.type.MatrixHttpServletRequestWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;

@Component
@WebFilter(filterName = "JwtFilter", urlPatterns = "/*")
public class JwtFilter implements Filter {

    private final
    JwtUtil jwtUtil;

    private final
    RedisUtil redisUtil;

    @Autowired
    public JwtFilter(JwtUtil jwtUtil, RedisUtil redisUtil) {
        this.jwtUtil = jwtUtil;
        this.redisUtil = redisUtil;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        MatrixHttpServletRequestWrapper matrixHttpServletRequestWrapper = new MatrixHttpServletRequestWrapper((HttpServletRequest) servletRequest);
        String token = matrixHttpServletRequestWrapper.getHeader("token");
        matrixHttpServletRequestWrapper.setTokenInfo(jwtUtil.decode(token));
        if (!redisUtil.hasKey("TOKEN" + token)) matrixHttpServletRequestWrapper.setTokenInfo(null);
        filterChain.doFilter(matrixHttpServletRequestWrapper, servletResponse);
    }

    @Override
    public void destroy() {

    }
}
