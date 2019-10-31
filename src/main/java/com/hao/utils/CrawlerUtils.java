package com.hao.utils;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;

@SuppressWarnings("all")
public class CrawlerUtils {

    /**
     * 释放请求资源
     *
     * @param httpRequestBase
     * @param closeableHttpResponse
     */
    public static void releaseConnection(HttpRequestBase httpRequestBase, CloseableHttpResponse closeableHttpResponse) {
        releaseConnection(httpRequestBase);
        releaseConnection(closeableHttpResponse);
    }

    /**
     * 释放HttpRequestBase
     *
     * @param httpRequestBase
     */
    public static void releaseConnection(HttpRequestBase httpRequestBase) {
        try {
            httpRequestBase.releaseConnection();
        } catch (Exception e) {
        }
    }

    /**
     * 释放CloseableHttpResponse
     *
     * @param closeableHttpResponse
     */
    public static void releaseConnection(CloseableHttpResponse closeableHttpResponse) {
        try {
            closeableHttpResponse.close();
        } catch (Exception e) {
        }
    }

}