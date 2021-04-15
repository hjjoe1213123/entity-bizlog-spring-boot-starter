package cn.idugou.bizlog;

import cn.idugou.bizlog.annotation.EntityLog;
import cn.idugou.bizlog.util.HttpRemoteIPUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * @Author: hejun
 * @Create: 2021/4/12 14:23
 * @Version: 1.0.1
 * @Copyright: Copyright (c) 2021
 * @Description:
 */
@Aspect
public class EntityLogAspectj implements Ordered {

    @Autowired
    private EntityLogSessionService entityLogSessionService;

    private int order;

    @Around("@annotation(cn.idugou.bizlog.annotation.EntityLog)")
    public Object distributedLockAround(ProceedingJoinPoint point) throws Throwable {
        boolean isCreate = false;
        try {
            Signature sig = point.getSignature();
            MethodSignature msig = null;
            if (!(sig instanceof MethodSignature)) {
                throw new IllegalArgumentException("该注解只能用于方法");
            }
            msig = (MethodSignature) sig;
            Object target = point.getTarget();
            Method currentMethod = target.getClass().getMethod(msig.getName(), msig.getParameterTypes());
            EntityLog annotation = currentMethod.getAnnotation(EntityLog.class);

            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = null;
            if (null != requestAttributes) {
                request = ((ServletRequestAttributes) requestAttributes).getRequest();
            }

            isCreate = entityLogSessionService.checkAndCreateSessionId(annotation.value(), HttpRemoteIPUtil.getRemoteIP(request));
            Object result = point.proceed();
            return result;
        } finally {
            if (isCreate) {
                entityLogSessionService.clearSessionId();
            }
        }
    }

    @Override
    public int getOrder() {
        return this.order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
