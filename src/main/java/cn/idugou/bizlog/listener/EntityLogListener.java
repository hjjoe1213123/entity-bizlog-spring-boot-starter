package cn.idugou.bizlog.listener;

import cn.idugou.bizlog.EntityLogAutoConfiguration;
import cn.idugou.bizlog.dto.EntityLogDTO;

import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;

/**
 * @Author: hejun
 * @Create: 2021/4/12 14:23
 * @Version: 1.0.1
 * @Copyright: Copyright (c) 2021
 * @Description:
 */
public class EntityLogListener {

    @PostUpdate
    public <T> void postUpdate(T entity) {
        EntityLogService entityLogService = EntityLogAutoConfiguration.getApplicationContext().getBean(EntityLogService.class);
        EntityLogDTO<T> entityLogDTO = entityLogService.getUpdateLog(entity);
        if (entityLogDTO != null) {
            entityLogService.saveEntityLog(entity.getClass(), entityLogDTO);
        }
    }

    @PostPersist
    public <T> void postPersist(T entity) {
        EntityLogService entityLogService = EntityLogAutoConfiguration.getApplicationContext().getBean(EntityLogService.class);
        EntityLogDTO<T> entityLogDTO = entityLogService.getSaveLog(entity);
        if (entityLogDTO != null) {
            entityLogService.saveEntityLog(entity.getClass(), entityLogDTO);
        }
    }

    @PostRemove
    public <T> void postRemove(T entity) {
        EntityLogService entityLogService = EntityLogAutoConfiguration.getApplicationContext().getBean(EntityLogService.class);
        EntityLogDTO<T> entityLogDTO = entityLogService.getDeleteLog(entity);
        if (entityLogDTO != null) {
            entityLogService.saveEntityLog(entity.getClass(), entityLogDTO);
        }
    }

    @PostLoad
    public <T> void postLoad(T entity) {
        EntityLogService entityLogService = EntityLogAutoConfiguration.getApplicationContext().getBean(EntityLogService.class);
        entityLogService.setPostLoadLog(entity);
    }

}
