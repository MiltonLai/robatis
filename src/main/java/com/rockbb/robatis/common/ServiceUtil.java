package com.rockbb.robatis.common;

import com.rockbb.robatis.dao.dto.TableColumnDTO;

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
     * @param entityName 不带前后缀的纯entity bean名称, 如用tableName生成可输入null或空字符串
     * @param columns 数据表字段
     */
    public static Map<String, Object> genServiceInterface(String entityName, List<TableColumnDTO> columns) {

        String dtoName = DTOUtil.getDTOName(entityName);
        Map<String, Object> root = new HashMap<>();
        List<String[]> primaryKeys = MapperUtil.getPrimaryKeys(columns, false);
        root.put("primaryKeys", primaryKeys);

        Set<String> imports = new TreeSet<>();
        imports.add("java.util.List");
        imports.add("com.rockbb.commons.lib.web.Pager");
        imports.add(DTOUtil.getClassPackage() + "." + DTOUtil.getDTOName(entityName));
        for (String[] key : primaryKeys) {
            if (key[2].contains("java"))
                imports.add(key[2]);
        }
        if (AppConfig.SPRING_CLOUD_ANNOTATION == 1) {
            imports.add("org.springframework.cloud.openfeign.FeignClient");
            imports.add("org.springframework.web.bind.annotation.RequestBody");
            imports.add("org.springframework.web.bind.annotation.RequestMapping");
            imports.add("org.springframework.web.bind.annotation.RequestMethod");
            imports.add("org.springframework.web.bind.annotation.RequestParam");
        }
        root.put("imports", imports);

        root.put("package", AppConfig.SERVICE_PACKAGE);
        root.put("entityName", entityName);
        root.put("dtoName", dtoName);
        root.put("serviceName", getServiceName(entityName));
        root.put("beanName", DTOUtil.getBeanName(getServiceName(entityName)));

        List<String> primaryVars = new ArrayList<>();
        if (primaryKeys.size() == 0) {
            String var = "Serializable id";
            if (AppConfig.SPRING_CLOUD_ANNOTATION == 1) {
                var = "@RequestParam(value = \"id\") " + var;
            }
            primaryVars.add(var);
        } else {
            for (int i = 0; i < primaryKeys.size(); i++) {
                String[] key = primaryKeys.get(i);
                String var = key[2]+" "+key[1];
                if (AppConfig.SPRING_CLOUD_ANNOTATION == 1) {
                    var = "@RequestParam(value = \""+key[1]+"\") " + var;
                }
                primaryVars.add(var);
            }
        }
        root.put("primaryVars", primaryVars);
        root.put("springCloud", AppConfig.SPRING_CLOUD_ANNOTATION);

        return root;
    }

    /**
     * 生成 service implementation 代码
     *
     * @param entityName 不带前后缀的纯entity bean名称, 如用tableName生成可输入null或空字符串
     * @param columns 数据表字段
     */
    public static Map<String, Object> genServiceImplementation(String entityName, List<TableColumnDTO> columns) {
        String dtoName = DTOUtil.getDTOName(entityName);
        Map<String, Object> root = new HashMap<>();
        root.put("package", AppConfig.SERVICE_IMPL_PACKAGE);
        root.put("entityName", entityName);
        root.put("dtoName", dtoName);
        root.put("beanName", DTOUtil.getBeanName(getServiceName(entityName)));
        root.put("className", getServiceImplName(entityName));
        root.put("serviceName", getServiceName(entityName));

        root.put("mapperBeanName", getBeanMapper(entityName));
        root.put("mapperClassName", MapperUtil.getMapperName(entityName));

        List<String[]> primaryKeys = MapperUtil.getPrimaryKeys(columns, false);
        root.put("primaryKeys", primaryKeys);

        Set<String> imports = new TreeSet<>();
        imports.add("java.util.List");
        imports.add("java.util.Map");
        imports.add("java.util.HashMap");
        imports.add("com.rockbb.commons.lib.web.ArgGen");
        imports.add("com.rockbb.commons.lib.web.Pager");
        imports.add("org.springframework.stereotype.Repository");
        imports.add("javax.annotation.Resource");
        if (AppConfig.SPRING_CLOUD_ANNOTATION == 1) {
            imports.add("org.springframework.beans.factory.annotation.Value");
            imports.add("org.springframework.web.bind.annotation.RequestBody");
            imports.add("org.springframework.web.bind.annotation.RequestMapping");
            imports.add("org.springframework.web.bind.annotation.RequestMethod");
            imports.add("org.springframework.web.bind.annotation.RequestParam");
            imports.add("org.springframework.web.bind.annotation.RestController");
        }

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
        root.put("springCloud", AppConfig.SPRING_CLOUD_ANNOTATION);
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
