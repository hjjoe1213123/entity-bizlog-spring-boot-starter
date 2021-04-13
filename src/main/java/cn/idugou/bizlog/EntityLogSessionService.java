package cn.idugou.bizlog;

import cn.idugou.bizlog.dto.SessionPair;
import org.apache.commons.lang3.tuple.MutablePair;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @Author : hejun
 * @Create : 2020/10/14 16:03
 * @Version : 1.0.1
 * @Copyright : Copyright (c) 2020
 * @Description :
 */
public class EntityLogSessionService {

    private static ThreadLocal<MutablePair<String, SessionPair>> sessionIdThreadLocal = new ThreadLocal<>();
    private static ThreadLocal<Map<String,Object>> oldValueThreadLocal = new ThreadLocal<>();

    public boolean checkSessionId() {
        return (sessionIdThreadLocal.get() != null);
    }

    public <T> void setOldValue(String key, Object oldValue) {
        if (null == oldValueThreadLocal.get()) {
            oldValueThreadLocal.set(new HashMap<>(1));
        }
        oldValueThreadLocal.get().put(key, oldValue);
    }

    public Object getOldValue(String key) {
        return oldValueThreadLocal.get().get(key);
    }

    public boolean checkAndCreateSessionId(String value,String ip) {
        boolean reslut = checkSessionId();
        if (!reslut) {
            String sessionId = UUID.randomUUID().toString();
            SessionPair sessionPair = new SessionPair();
            sessionPair.setIp(ip);
            sessionPair.setValue(value);
            sessionPair.setVersion(Integer.valueOf(0));

            MutablePair<String, SessionPair> pair = new MutablePair(sessionId, sessionPair);
            sessionIdThreadLocal.set(pair);
            return true;
        }
        return false;
    }

    public void clearSessionId() {
        oldValueThreadLocal.remove();
        sessionIdThreadLocal.remove();
    }

    public MutablePair<String, SessionPair> getSessionIdAndVersion() {
        if (checkSessionId()) {
            MutablePair<String, SessionPair> sessionPair = sessionIdThreadLocal.get();
            MutablePair<String, SessionPair> result = new MutablePair(sessionPair.left, sessionPair.right);
            sessionPair.right.setVersion(sessionPair.right.getVersion() + 1);
            return result;
        }
        return null;
    }
}
