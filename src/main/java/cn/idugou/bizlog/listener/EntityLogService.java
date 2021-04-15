package cn.idugou.bizlog.listener;

import cn.idugou.bizlog.EntityLogProperties;
import cn.idugou.bizlog.EntityLogScanService;
import cn.idugou.bizlog.EntityLogSessionService;
import cn.idugou.bizlog.dto.ColumnLogDTO;
import cn.idugou.bizlog.dto.EntityLogDTO;
import cn.idugou.bizlog.dto.LogType;
import cn.idugou.bizlog.dto.SessionPair;
import cn.idugou.bizlog.scan.ColumnInfo;
import cn.idugou.bizlog.scan.TableInfo;
import org.apache.commons.lang3.tuple.MutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nullable;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author: hejun
 * @Create: 2021/4/12 14:23
 * @Version: 1.0.1
 * @Copyright: Copyright (c) 2021
 * @Description:
 */
public class EntityLogService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private EntityLogScanService entityLogScanService;

    @Autowired
    private EntityLogSessionService entityLogSessionService;


    @Autowired
    private EntityLogProperties entityLogProperties;


    public <T> void saveEntityLog(Class<T> clz, EntityLogDTO entityLogDTO) {
//        rabbitTemplate.convertAndSend("", entityLogProperties.getQueueName(), entityLogDTO, message -> {
//            message.getMessageProperties().setHeader("rowId", entityLogDTO.getRowId());
//            message.getMessageProperties().setHeader("sessionId", entityLogDTO.getSessionId());
//            message.getMessageProperties().setHeader("version", entityLogDTO.getVersion());
//            return message;
//        });
    }

    public <T> EntityLogDTO<T> getSaveLog(T entity) {
        MutablePair<String, SessionPair> sessionIdAndVersion = entityLogSessionService.getSessionIdAndVersion();
        if (null == sessionIdAndVersion) {
            return null;
        }

        TableInfo tableInfo = entityLogScanService.getTableInfo(entity.getClass());
        Object rowId = null;
        try {
            rowId = tableInfo.getPrimaryKeyField().get(entity);
        } catch (IllegalAccessException e) {
            log.error("{}", entity.getClass().getSimpleName());
            return null;
        }
        EntityLogDTO<T> entityLogDTO = new EntityLogDTO();
        entityLogDTO.setSessionId(sessionIdAndVersion.getKey());
        entityLogDTO.setVersion(sessionIdAndVersion.getValue().getVersion());
        entityLogDTO.setValue(sessionIdAndVersion.getValue().getValue());
        entityLogDTO.setIp(sessionIdAndVersion.getValue().getIp());
//        if (null != getUser()) {
//            entityLogDTO.setUserId(String.valueOf(getUser().getUserId()));
//            entityLogDTO.setUsername(getUser().getUsername());
//            entityLogDTO.setNickname(getUser().getNickname());
//        }
        entityLogDTO.setLogType(LogType.INSERT);
        entityLogDTO.setOperateTime(new Date());
        entityLogDTO.setTableName(tableInfo.getName());
        entityLogDTO.setTableDesc(tableInfo.getDesc());
        entityLogDTO.setRowId(rowId.toString());
        entityLogDTO.setNewValue(entity);
        return entityLogDTO;
    }

    public <T> EntityLogDTO<T> getDeleteLog(T entity) {
        MutablePair<String, SessionPair> sessionIdAndVersion = entityLogSessionService.getSessionIdAndVersion();
        if (null == sessionIdAndVersion) {
            return null;
        }

        TableInfo tableInfo = entityLogScanService.getTableInfo(entity.getClass());
        Object rowId = null;
        try {
            rowId = tableInfo.getPrimaryKeyField().get(entity);
        } catch (IllegalAccessException e) {
            log.error("{}", entity.getClass().getSimpleName());
            return null;
        }
        EntityLogDTO<T> entityLogDTO = new EntityLogDTO();
        entityLogDTO.setSessionId(sessionIdAndVersion.getKey());
        entityLogDTO.setVersion(sessionIdAndVersion.getValue().getVersion());
        entityLogDTO.setValue(sessionIdAndVersion.getValue().getValue());
        entityLogDTO.setIp(sessionIdAndVersion.getValue().getIp());
//        if (null != getUser()) {
//            entityLogDTO.setUserId(String.valueOf(getUser().getUserId()));
//            entityLogDTO.setUsername(getUser().getUsername());
//            entityLogDTO.setNickname(getUser().getNickname());
//        }
        entityLogDTO.setLogType(LogType.DELETE);
        entityLogDTO.setOperateTime(new Date());
        entityLogDTO.setTableName(tableInfo.getName());
        entityLogDTO.setTableDesc(tableInfo.getDesc());
        entityLogDTO.setRowId(rowId.toString());
        entityLogDTO.setOldValue(entity);
        return entityLogDTO;
    }

    public <T> void setPostLoadLog(T entity) {
        Class<?> clz = entity.getClass();
        Table table = AnnotationUtils.findAnnotation(clz, Table.class);
        TableInfo tableInfo = entityLogScanService.getTableInfo(clz);
        if (tableInfo == null || tableInfo.getPrimaryKeyField() == null) {
            log.error("{}", clz.getSimpleName());
            return;
        }

        Object rowId = null;
        Map<String, Object> objectMap = new HashMap<>();
        try {
            rowId = tableInfo.getPrimaryKeyField().get(entity);
            Object instance = clz.newInstance();
            BeanUtils.copyProperties(entity, instance);
            objectMap.put(table.name() + rowId, instance);
            entityLogSessionService.setOldValue(table.name() + rowId, instance);
        } catch (IllegalAccessException | InstantiationException e) {
            log.error("{}", clz.getSimpleName());
            return;
        }
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @Nullable
    public <T> EntityLogDTO<T> getUpdateLog(T entity) {
        MutablePair<String, SessionPair> sessionIdAndVersion = entityLogSessionService.getSessionIdAndVersion();
        if (null == sessionIdAndVersion) {
            return null;
        }

        Class<?> clz = entity.getClass();
        TableInfo tableInfo = entityLogScanService.getTableInfo(clz);
        if (tableInfo == null || tableInfo.getPrimaryKeyField() == null) {
            log.error("{}", clz.getSimpleName());
            return null;
        }
        T oldValue = null;
        Object rowId = null;
        try {
            rowId = tableInfo.getPrimaryKeyField().get(entity);
            Table table = AnnotationUtils.findAnnotation(clz, Table.class);
            oldValue = (T) entityLogSessionService.getOldValue(table.name() + rowId);
        } catch (IllegalAccessException e) {
            log.error("{}", clz.getSimpleName());
            return null;
        }

        if (oldValue == null) {
            return getSaveLog(entity);
        }
        EntityLogDTO<T> entityLogDTO = new EntityLogDTO();
        entityLogDTO.setSessionId(sessionIdAndVersion.getKey());
        entityLogDTO.setVersion(sessionIdAndVersion.getValue().getVersion());
        entityLogDTO.setValue(sessionIdAndVersion.getValue().getValue());
        entityLogDTO.setIp(sessionIdAndVersion.getValue().getIp());
//        if (null != getUser()) {
//            entityLogDTO.setUserId(String.valueOf(getUser().getUserId()));
//            entityLogDTO.setUsername(getUser().getUsername());
//            entityLogDTO.setNickname(getUser().getNickname());
//        }
        entityLogDTO.setLogType(LogType.UPDATE);
        entityLogDTO.setOperateTime(new Date());
        entityLogDTO.setTableName(tableInfo.getName());
        entityLogDTO.setTableDesc(tableInfo.getDesc());
        entityLogDTO.setRowId(rowId.toString());
        entityLogDTO.setOldValue(oldValue);
        entityLogDTO.setNewValue(entity);
        List<ColumnLogDTO> columnLogList = new ArrayList<>();
        try {
            for (ColumnInfo columnInfo : tableInfo.getColumns()) {
                Object columnOldValue = columnInfo.getField().get(oldValue);
                Object columnNewValue = columnInfo.getField().get(entity);
                if (columnOldValue == null && columnNewValue == null) {
                    continue;
                }
                if (columnOldValue == null || !columnOldValue.equals(columnNewValue)) {
                    ColumnLogDTO columnLogDTO = new ColumnLogDTO();
                    columnLogDTO.setColumnName(columnInfo.getName());
                    columnLogDTO.setColumnText(columnInfo.getText());
                    columnLogDTO.setOldValue(getValueByObject(columnOldValue));
                    columnLogDTO.setNewValue(getValueByObject(columnNewValue));
                    columnLogList.add(columnLogDTO);
                }
            }
        } catch (IllegalAccessException e) {
            log.error("", e);
        }
        if (columnLogList.size() > 0) {
            Map<String, ColumnLogDTO> columnLogMap = columnLogList.stream().collect(Collectors.toMap(ColumnLogDTO::getColumnName, p -> p));
            entityLogDTO.setColumnLogMap(columnLogMap);
        }
        return entityLogDTO;
    }

    private String getValueByObject(Object value) {
        if (value == null) {
            return null;
        }
        return value.toString();
    }
}
