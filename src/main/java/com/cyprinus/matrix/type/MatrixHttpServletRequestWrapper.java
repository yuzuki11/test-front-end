package com.cyprinus.matrix.type;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.HashMap;
import java.util.Map;

public class MatrixHttpServletRequestWrapper extends HttpServletRequestWrapper {

    private MatrixTokenInfo tokenInfo;

    public MatrixHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    public void setTokenInfo(MatrixTokenInfo tokenInfo) {
        this.tokenInfo = tokenInfo;
    }

    public MatrixTokenInfo getTokenInfo() {
        return tokenInfo;
    }

}
