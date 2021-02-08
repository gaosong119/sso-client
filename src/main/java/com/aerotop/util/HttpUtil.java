package com.aerotop.util;

import com.aerotop.constant.HttpConstant;
import org.apache.http.Consts;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: HttpUtil
 * @Description: HTTP 工具类
 * @Author: gaosong
 * @Date 2021/1/29 10:58
 */
public class HttpUtil {
    /**
     * @Description: 向目标地址发送post方式请求注销会话
     * @Author: gaosong
     * @Date: 2021/1/29 11:23
     * @param url: 待注销的URL
     * @param params: 请求参数,key:请求注销常量,value: 局部会话中的token
     * @return: boolean
     **/
    public static boolean postDestroy(String url, Map<String, String> params){
        //请求返回标志
        boolean postResult = false;
        //注销对象
        CloseableHttpClient httpclient = HttpClients.createDefault();
        //post方式请求对象
        HttpPost httpPost = new HttpPost(url);
        // 参数处理
        if (params != null && !params.isEmpty()) {
            //建立一个NameValuePair集合，用于存储欲传送的参数
            List<NameValuePair> list = new ArrayList<>();
            //循环将参数放入集合中
            Iterator<Map.Entry<String, String>> it = params.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, String> entry = it.next();
                list.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
            //设置编码
            httpPost.setEntity(new UrlEncodedFormEntity(list, Consts.UTF_8));
        }
        CloseableHttpResponse response = null;
        try {
            response = httpclient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            if(statusCode== HttpConstant.SUCCESS){
                postResult = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                release(response,httpclient);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return postResult;
    }
    /**
     * @Description: 释放资源
     * @Author: gaosong
     * @Date: 2021/1/29 15:51
     * @param httpResponse:
     * @param httpClient:
     * @return: void
     **/
    public static void release(CloseableHttpResponse httpResponse, CloseableHttpClient httpClient) throws IOException {
        if (httpResponse != null) {
            httpResponse.close();
        }
        if (httpClient != null) {
            httpClient.close();
        }
    }
}
