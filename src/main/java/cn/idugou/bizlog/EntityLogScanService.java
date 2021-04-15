package cn.idugou.bizlog;

import cn.idugou.bizlog.annotation.FieldString;
import cn.idugou.bizlog.annotation.TableDesc;
import cn.idugou.bizlog.listener.EntityLogListener;
import cn.idugou.bizlog.scan.ColumnInfo;
import cn.idugou.bizlog.scan.TableInfo;
import org.reflections.ReflectionUtils;
import org.reflections.Reflections;
import org.reflections.scanners.Scanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;

import javax.annotation.PostConstruct;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.Table;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Author: hejun
 * @Create: 2021/4/12 14:23
 * @Version: 1.0.1
 * @Copyright: Copyright (c) 2021
 * @Description:
 */
public class EntityLogScanService {

    private static final Logger log = LoggerFactory.getLogger(EntityLogScanService.class);

    private static Map<Class, TableInfo> classTableMap = null;

    @Autowired
    private EntityLogProperties entityLogProperties;

    private ApplicationContext appCtx;

    public EntityLogScanService(ApplicationContext appCtx) {
        this.appCtx = appCtx;
    }

    @PostConstruct
    public void init() {
        if (classTableMap == null) {
            classTableMap = new HashMap<>();

            String[] annotation = appCtx.getBeanNamesForAnnotation(SpringBootApplication.class);
            Object ctxBean = appCtx.getBean(annotation[0]);
            EntityScan entityScan = AnnotationUtils.findAnnotation(ctxBean.getClass(), EntityScan.class);

            ConfigurationBuilder builder = ConfigurationBuilder.build().addUrls(ClasspathHelper.forClassLoader())
                    .filterInputsBy(new FilterBuilder().includePackage(entityScan.basePackages()))
                    .setScanners(new Scanner[]{new TypeAnnotationsScanner().filterResultsBy(s -> s.equals("javax.persistence.EntityListeners"))});

            Reflections reflections = new Reflections(builder);
            Set<Class<?>> set = reflections.getTypesAnnotatedWith(EntityListeners.class, true);
            Set<Class<?>> result = new HashSet<>();
            for (Class<?> clz : set) {
                if (clz.getAnnotation((Class) Entity.class) != null) {
                    EntityListeners entityListeners = clz.getAnnotation(EntityListeners.class);
                    if (entityListeners != null) {
                        List<Class<?>> classList = Arrays.asList(entityListeners.value());
                        if (classList.contains(EntityLogListener.class)) {
                            result.add(clz);
                        }
                    }
                }
            }
            for (Class<?> clz : result) {
                TableInfo tableInfo = new TableInfo();
                if (clz.getAnnotation((Class) Table.class) != null) {
                    tableInfo.setName((clz.getAnnotation(Table.class)).name());
                } else {
                    tableInfo.setName(clz.getSimpleName());
                }
                if (clz.getAnnotation((Class) TableDesc.class) != null) {
                    tableInfo.setDesc((clz.getAnnotation(TableDesc.class)).tableDesc());
                } else {
                    tableInfo.setDesc(tableInfo.getName());
                }


                Set<Field> fields = ReflectionUtils.getFields(clz);
                List<ColumnInfo> columns = new ArrayList<>();
                fields.forEach(field -> {
                    List<String> excludeColumns = entityLogProperties.getExcludeField();
                    if (excludeColumns != null && excludeColumns.contains(field.getName())) {
                        return;
                    }

                    ColumnInfo columnInfo = new ColumnInfo();
                    columnInfo.setName(field.getName());
                    field.setAccessible(true);
                    columnInfo.setField(field);
                    Id id = field.getAnnotation(Id.class);
                    if (id != null) {
                        tableInfo.setPrimaryKey(columnInfo.getName());
                        tableInfo.setPrimaryKeyField(field);
                    }
                    FieldString entityString = field.getAnnotation(FieldString.class);
                    if (entityString != null) {
                        columnInfo.setText(entityString.fieldName());
                    } else {
                        columnInfo.setText(columnInfo.getName());
                    }
                    columns.add(columnInfo);
                });

                /*Map<String, Field> fieldMap = ClassUtils.findClassBeanFields(clz);
                for (Map.Entry<String, Field> entry : fieldMap.entrySet()) {
                    List<String> excludeColumns = entityLogProperties.getExcludeField();
                    if (excludeColumns != null && excludeColumns.contains(entry.getKey())) {
                        continue;
                    }
                    ColumnInfo columnInfo = new ColumnInfo();
                    columnInfo.setName(entry.getKey());
                    entry.getValue().setAccessible(true);
                    columnInfo.setField(entry.getValue());
                    Id id = entry.getValue().getAnnotation(Id.class);
                    if (id != null) {
                        tableInfo.setPrimaryKey(columnInfo.getName());
                        tableInfo.setPrimaryKeyField(entry.getValue());
                    }
                    FieldString entityString = entry.getValue().getAnnotation(FieldString.class);
                    if (entityString != null) {
                        columnInfo.setText(entityString.fieldName());
                    } else {
                        columnInfo.setText(columnInfo.getName());
                    }
                    columns.add(columnInfo);
                }*/
                tableInfo.setColumns(columns);
                classTableMap.put(clz, tableInfo);
            }
            log.info("");
        }
    }


    public TableInfo getTableInfo(Class<?> clz) {
        return classTableMap.get(clz);
    }
}
