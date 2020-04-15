package com.cyprinus.matrix;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "matrix")
public class Config {

    Config(){}

    private String secretKey;

    private String OSSUrl;

    private String OSSSecretKey;

    private String OSSAccessKey;

    private String ImagePathBase;

    private String UrlBase;

    public String getImagePathBase() {
        return ImagePathBase;
    }

    public void setImagePathBase(String imagePathBase) {
        ImagePathBase = imagePathBase;
    }

    public String getOSSAccessKey() {
        return OSSAccessKey;
    }

    public void setOSSAccessKey(String OSSAccessKey) {
        this.OSSAccessKey = OSSAccessKey;
    }

    public void setOSSUrl(String OSSUrl) {
        this.OSSUrl = OSSUrl;
    }

    public void setOSSSecretKey(String OSSSecretKey) {
        this.OSSSecretKey = OSSSecretKey;
    }

    public String getOSSUrl() {
        return OSSUrl;
    }

    public String getOSSSecretKey() {
        return OSSSecretKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getUrlBase() {
        return UrlBase;
    }

    public void setUrlBase(String urlBase) {
        UrlBase = urlBase;
    }
}
