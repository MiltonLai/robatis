package com.rockbb.robatis.common;

import com.rockbb.robatis.dao.dto.TableColumnDTO;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Milton on 2015/5/24 at 23:04.
 */
public class ServiceUtil {
    public static final String LE = AppConfig.LE;
    public static final String DT = AppConfig.DT;

    /**
     * 生成 service interface 代码
     *
     * @param tableName 数据表名
     * @param entityName 不带前后缀的纯entity bean名称, 如用tableName生成可输入null或空字符串
     * @param columns 数据表字段
     * @param variables entity bean的成员变量列表, 与数据表字段数量和顺序要一致, 使用column所生成可以输入null
     */
    public static Map<String, Object> genServiceInterface(
            String tableName, String entityName, List<TableColumnDTO> columns, List<String> variables) {
        if (entityName == null || entityName.length() == 0) {
            entityName = DTOUtil.getEntityName(tableName);
        }
        if (variables == null || variables.size() == 0) {
            variables = new ArrayList<String>();
            for (TableColumnDTO column : columns) {
                variables.add(column.getVariableName());
            }
        }
        Map<String, Object> root = new HashMap<>();
        List<String[]> primaryKeys = MapperUtil.getPrimaryKeys(columns, variables, false);
        root.put("primaryKeys", primaryKeys);

        Set<String> imports = new TreeSet<>();
        imports.add("java.util.List");
        imports.add("com.rockbb.commons.lib.web.Pager");
        imports.add(DTOUtil.getClassPackage() + "." + DTOUtil.getDTOName(entityName));
        for (String[] key : primaryKeys) {
            if (key[2].contains("java"))
                imports.add(key[2]);
        }
        root.put("imports", imports);

        root.put("package", AppConfig.SERVICE_PACKAGE);
        root.put("entityName", entityName);
        root.put("serviceName", getServiceName(entityName));

        List<String> primaryVars = new ArrayList<>();
        if (primaryKeys.size() == 0) {
            primaryVars.add("Serializable id");
        } else {
            for (int i = 0; i < primaryKeys.size(); i++) {
                String[] key = primaryKeys.get(i);
                primaryVars.add(key[2]+" "+key[1]);
            }
        }
        root.put("primaryVars", primaryVars);

        return root;
    }

    /**
     * 生成 service implementation 代码
     *
     * @param tableName 数据表名
     * @param entityName 不带前后缀的纯entity bean名称, 如用tableName生成可输入null或空字符串
     * @param columns 数据表字段
     * @param variables entity bean的成员变量列表, 与数据表字段数量和顺序要一致, 使用column所生成可以输入null
     */
    public static Map<String, Object> genServiceImplementation(
            String tableName, String entityName, List<TableColumnDTO> columns, List<String> variables) {
        if (entityName == null || entityName.length() == 0) {
            entityName = DTOUtil.getEntityName(tableName);
        }
        if (variables == null || variables.size() == 0) {
            variables = new ArrayList<String>();
            for (TableColumnDTO column : columns) {
                variables.add(column.getVariableName());
            }
        }
        Map<String, Object> root = new HashMap<>();
        root.put("package", AppConfig.SERVICE_IMPL_PACKAGE);
        root.put("entityName", entityName);
        root.put("beanName", DTOUtil.getBeanName(getServiceName(entityName)));
        root.put("className", getServiceImplName(entityName));
        root.put("serviceName", getServiceName(entityName));

        root.put("mapperBeanName", getBeanMapper(entityName));
        root.put("mapperClassName", MapperUtil.getMapperName(entityName));

        List<String[]> primaryKeys = MapperUtil.getPrimaryKeys(columns, variables, false);
        root.put("primaryKeys", primaryKeys);

        Set<String> imports = new TreeSet<>();
        imports.add("java.util.List");
        imports.add("java.util.Map");
        imports.add("java.util.HashMap");
        imports.add("com.rockbb.commons.lib.web.ArgGen");
        imports.add("com.rockbb.commons.lib.web.Pager");
        imports.add("org.springframework.stereotype.Repository");
        imports.add("javax.annotation.Resource");

        imports.add(DTOUtil.getClassPackage() + "." + DTOUtil.getDTOName(entityName));
        imports.add(MapperUtil.getClassPackage() + "." + MapperUtil.getMapperName(entityName));
        imports.add(AppConfig.SERVICE_PACKAGE + "." + getServiceName(entityName));
        for (String[] key : primaryKeys) {
            if (key[2].contains("java"))
                imports.add(key[2]);
        }
        root.put("imports", imports);

        List<String[]> primaryVars = new ArrayList<>();
        if (primaryKeys.size() == 0) {
            primaryVars.add(new String[]{"Serializable","id"});
        } else {
            for (int i = 0; i < primaryKeys.size(); i++) {
                String[] key = primaryKeys.get(i);
                primaryVars.add(new String[]{key[2], key[1]});
            }
        }
        root.put("primaryVars", primaryVars);
        return root;
    }

    public static String getInterfaceFilePath() {
        String delimiter = "/";
        if (AppConfig.FILE_OUT_FOLDER.lastIndexOf('/') == AppConfig.FILE_OUT_FOLDER.length() - 1)
            delimiter = "";

        return AppConfig.FILE_OUT_FOLDER + delimiter + AppConfig.SERVICE_PACKAGE.replace(".", "/") + "/";
    }

    public static String getImplFilePath() {
        String delimiter = "/";
        if (AppConfig.FILE_OUT_FOLDER.lastIndexOf('/') == AppConfig.FILE_OUT_FOLDER.length() - 1)
            delimiter = "";

        return AppConfig.FILE_OUT_FOLDER + delimiter + AppConfig.SERVICE_IMPL_PACKAGE.replace(".", "/") + "/";
    }

    public static String getServiceName(String entityName) {
        return entityName + AppConfig.DTO_SUFFIX + AppConfig.SERVICE_SUFFIX;
    }

    public static String getServiceImplName(String entityName) {
        return entityName + AppConfig.DTO_SUFFIX + AppConfig.SERVICE_IMPL_SUFFIX;
    }

    public static String getBeanMapper(String entityName) {
        return DTOUtil.getBeanName(MapperUtil.getMapperName(entityName));
    }
}
