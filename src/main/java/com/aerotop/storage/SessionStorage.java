package com.aerotop.storage;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @ClassName: SessionStorage
 * @Description: Session存储集合
 * @Author: gaosong
 * @Date 2021/1/29 14:04
 */
public enum SessionStorage {
    /**用来调用方法*/
    INSTANCE;
    /**局部会话存储集合,key代表token,value代表HttpSession对象*/
    private Map<String, HttpSession> map = new HashMap<>();

    public void set(String token, HttpSession session) {
        map.put(token, session);
    }
    /**
     * @Description: 根据token删除局部会话中的HttpSession对象
     * @Author: gaosong
     * @Date: 2021/1/29 14:58
     * @param token:
     * @return: javax.servlet.http.HttpSession
     **/
    public HttpSession destroyToken(String token) {
        if (map.containsKey(token) && token!=null) {
            Iterator<Map.Entry<String, HttpSession>> it = map.entrySet().iterator();
            while(it.hasNext()){
                Map.Entry<String, HttpSession> entry=it.next();
                String key=entry.getKey();
                if(token.equals(key)){
                    it.remove();
                    return entry.getValue();
                }
            }
        }
        return null;
    }
}
