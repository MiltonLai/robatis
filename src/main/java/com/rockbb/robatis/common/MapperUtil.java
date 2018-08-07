package com.rockbb.robatis.common;

import com.rockbb.robatis.dao.dto.TableColumnDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * 
 * Created by Milton on 2015/5/24 at 23:04.
 */
public class MapperUtil {
    public static final String DT = AppConfig.DT;

    /**
     * 生成 mapper interface 内容
     *
     * @param tableName 数据表名
     * @param entityName 不带前后缀的纯entity bean名称, 如用tableName生成可输入null或空字符串
     * @param columns 数据表字段
     * @param variables entity bean的成员变量列表, 与数据表字段数量和顺序要一致, 使用column所生成可以输入null
     * @return xml format text
     */
    public static Map<String, Object> genMapperInterface(
            String tableName, String entityName, List<TableColumnDTO> columns, List<String> variables) {
        if (entityName == null || entityName.length() == 0) {
            entityName = DTOUtil.getEntityName(tableName);
        }
        if (variables == null || variables.size() == 0) {
            variables = new ArrayList<>();
            for (TableColumnDTO column : columns) {
                variables.add(column.getVariableName());
            }
        }
        List<String[]> primaryKeys = getPrimaryKeys(columns, variables, false);

        Map<String, Object> root = new HashMap<>();
        root.put("package", getClassPackage());
        root.put("className", entityName + AppConfig.MAPPER_SUFFIX);
        root.put("beanName", DTOUtil.getBeanName(entityName) + AppConfig.MAPPER_SUFFIX);
        root.put("entityName", entityName);
        root.put("primaryKeys", primaryKeys);

        Set<String> imports = new TreeSet<>();
        imports.add("java.util.List");
        imports.add("java.util.Map");
        imports.add(DTOUtil.getClassPackage() + "." + entityName + AppConfig.DTO_SUFFIX);
        imports.add("org.apache.ibatis.annotations.Param");
        imports.add("org.springframework.stereotype.Repository");
        imports.add("com.rockbb.commons.lib.web.Pager");
        for (String[] key : primaryKeys) {
            if (key[2].contains("java") && !key[2].equals("java.lang.String")) {
                imports.add(key[2]);
            } else if (key[4].equals("Long")) {
                imports.add("java.lang.Long");
            }
        }
        root.put("imports", imports);

        List<String> orderbys = new ArrayList<>();
        for (int i=0; i < columns.size(); i++) {
            if (columns.get(i).isPrimary()) continue;
            orderbys.add(columns.get(i).getField());
        }
        root.put("orderbys", orderbys);

        List<String> primaryVars = new ArrayList<>();
        if (primaryKeys.size() == 0) {
            primaryVars.add("@Param(\"id\") Serializable id");
        } else if (primaryKeys.size() == 1) {
            primaryVars.add("@Param(\"" + primaryKeys.get(0)[1] + "\") " + primaryKeys.get(0)[2] + " " + primaryKeys.get(0)[1]);
        } else {
            for (int i = 0; i < primaryKeys.size(); i++) {
                String[] key = primaryKeys.get(i);
                primaryVars.add("@Param(\""+key[1]+"\") "+key[2]+" "+key[1]);
            }
        }
        root.put("primaryVars", primaryVars);
        return root;
    }

    /**
     * 生成mapper xml内容
     *
     * @param tableName 数据表名
     * @param entityName 不带前后缀的纯entity bean名称, 如用默认可输入null或空字符串
     * @param columns 数据表字段
     * @param variables entity bean的成员变量列表, 与数据表字段数量和顺序要一致, 使用默认可以输入null
     * @return xml format text
     */
    public static Map<String, Object> genMapperXML(
            String tableName, String entityName, List<TableColumnDTO> columns, List<String> variables) {
        if (entityName == null || entityName.length() == 0) {
            entityName = DTOUtil.getEntityName(tableName);
        }
        if (variables == null || variables.size() == 0) {
            variables = new ArrayList<>();
            for (TableColumnDTO column : columns) {
                variables.add(column.getVariableName());
            }
        }
        Map<String, Object> root = new HashMap<>();
        root.put("tableName", tableName);
        root.put("className", DTOUtil.getClassPackage() + "." + entityName + AppConfig.DTO_SUFFIX);
        root.put("resultMapId", entityName.toLowerCase() + "_1");
        // make up the array size difference
        int diff = columns.size() - variables.size();
        if (diff > 0) {
            for (int i = 0; i < diff; i++) variables.add("");
        } else if (diff < 0) {
            for (int i = 0; i < -diff; i++) columns.add(new TableColumnDTO());
        }

        List<String[]> primaryKeys = getPrimaryKeys(columns, variables, true);
        root.put("primaryKeys", primaryKeys);
        root.put("columns", columns);
        root.put("namespace", getClassPackage() + "." + entityName + AppConfig.MAPPER_SUFFIX);
        root.put("accurateWheres", getAccurateWheres(columns, variables));

        List<String> resultMapList = new ArrayList<>();
        boolean hasVersion = false;
        String versionField = "";
        for (int i = 0; i < columns.size(); i++) {
            if (columns.get(i).getField().equalsIgnoreCase("version")) {
                hasVersion = true;
                versionField = columns.get(i).getField();
            }
            StringBuilder sb = new StringBuilder();
            if (columns.get(i).isPrimary())
                sb.append("<id column=\"");
            else
                sb.append("<result column=\"");
            sb.append(columns.get(i).getField()).append("\"");
            if (columns.get(i).isPrimary()) sb.append(DT);
            sb.append(CommonUtil.genIndentSpace(columns.get(i).getField(), 25));
            sb.append("property=\"").append(variables.get(i)).append("\" />");
            resultMapList.add(sb.toString());
        }
        root.put("resultMapList", resultMapList);
        root.put("hasVersion", hasVersion);
        root.put("versionField", versionField);

        // Insert
        boolean autoIncrement = false;
        if (primaryKeys.size() == 1 && primaryKeys.get(0)[3].contains("auto_increment")) {
            autoIncrement = true;
        }
        root.put("autoIncrement", autoIncrement);

        List<String> inserts = new ArrayList<>();
        boolean skip = false;
        for (int i = 0; i < variables.size(); i++) {
            if (columns.get(i).isPrimary() && autoIncrement && !skip) {
                skip = true;
                continue;
            }
            if (AppConfig.DB_TYPE == 0) {
                inserts.add(getMySQLValueAssigner(variables.get(i)));
            } else {
                inserts.add(getOracleValueAssigner(variables.get(i), columns.get(i)));
            }
        }
        root.put("inserts", inserts);

        // Update
        List<String> updateStrs = new ArrayList<>();
        for (int i = 0; i < columns.size(); i++) {
            if (columns.get(i).isPrimary()) continue;
            StringBuilder sb = new StringBuilder().append(columns.get(i).getField());
            sb.append(CommonUtil.genIndentSpace(columns.get(i).getField(), 20));
            sb.append("= ");
            if (variables.get(i).equalsIgnoreCase("version")) {
                sb.append("version + 1");
            } else {
                if (AppConfig.DB_TYPE == 0) {
                    sb.append(getMySQLValueAssigner(variables.get(i)));
                } else {
                    sb.append(getOracleValueAssigner(variables.get(i), columns.get(i)));
                }
            }
            updateStrs.add(sb.toString());
        }
        root.put("updateStrs", updateStrs);

        // Alter
        List<String> alters = new ArrayList<>();
        for (int i = 0; i < columns.size(); i++) {
            if (columns.get(i).isPrimary()) continue;
            StringBuilder sb = new StringBuilder();
            if (variables.get(i).equalsIgnoreCase("version")) {
                sb.append(columns.get(i).getField());
                sb.append(CommonUtil.genIndentSpace(columns.get(i).getField(), 20));
                sb.append("= ");
                sb.append(columns.get(i).getField() + " + 1,");
            } else {
                sb.append("<if test=\"param." + variables.get(i) + " != null\">").append(columns.get(i).getField());
                sb.append(CommonUtil.genIndentSpace(variables.get(i), 15))
                        .append(CommonUtil.genIndentSpace(columns.get(i).getField(),15));
                sb.append("= ");
                if (AppConfig.DB_TYPE == 0) {
                    sb.append(getMySQLValueAssigner("param." + variables.get(i)));
                } else {
                    sb.append(getOracleValueAssigner("param." + variables.get(i), columns.get(i)));
                }
            }
            alters.add(sb.toString());
        }
        root.put("alters", alters);
        return root;
    }

    public static String getClassPackage() {
        return AppConfig.MAPPER_PACKAGE;
    }

    public static String getClassFilePath() {
        String delimiter = "/";
        if (AppConfig.FILE_OUT_FOLDER.lastIndexOf('/') == AppConfig.FILE_OUT_FOLDER.length() - 1)
            delimiter = "";

        return AppConfig.FILE_OUT_FOLDER + delimiter + getClassPackage().replace(".", "/") + "/";
    }

    public static String getMapperName(String entityName) {
        return entityName + AppConfig.MAPPER_SUFFIX;
    }

    private static String getMySQLValueAssigner(String field) {
        return "#{" + field + "}";
    }

    private static String getOracleValueAssigner(String field, TableColumnDTO t) {
        return "#{" + field + ", jdbcType=" + t.getJdbcType() + "}";
    }

    public static List<String[]> getPrimaryKeys(List<TableColumnDTO> columns, List<String> variables, boolean forXml) {
        List<String[]> primaryKeys = new ArrayList<String[]>();
        for (int i = 0; i < columns.size(); i++) {
            if (columns.get(i).isPrimary()) {
                String[] key = new String[5];
                key[0] = columns.get(i).getField();
                key[1] = variables.get(i);
                key[3] = columns.get(i).getExtra();
                if (columns.get(i).getType().contains("CHAR")
                        || columns.get(i).getType().contains("char")) {
                    if (forXml)
                        key[2] = "java.lang.String";
                    else
                        key[2] = "String";
                } else
                    key[2] = "long";

                key[4] = key[2];
                if (key[2].equals("long")) {
                    key[4] = "Long";
                }
                primaryKeys.add(key);
            }
        }
        return primaryKeys;
    }

    private static List<String> getAccurateWheres(List<TableColumnDTO> columns, List<String> variables) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < columns.size(); i++) {
            if (columns.get(i).isPrimary()) {
                String[] key = new String[2];
                key[0] = columns.get(i).getField();
                key[1] = variables.get(i);
                list.add(key[0] + " = #{" + key[1] + "}");
            }
        }
        return list;
    }
}
