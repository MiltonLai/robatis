package com.rockbb.robatis.common;

import com.rockbb.robatis.dao.dto.TableColumnDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * 
 * Created by Milton on 2015/5/24 at 23:04.
 */
public class MapperUtil {
    public static final String DT = AppConfig.DT;

    /**
     * 生成 mapper interface 内容
     *
     * @param entityName 不带前后缀的纯entity bean名称, 如用tableName生成可输入null或空字符串
     * @param columns 数据表字段
     * @return xml format text
     */
    public static Map<String, Object> genMapperInterface(
            String tableName, String entityName, List<TableColumnDTO> columns) {
        String dtoName = DTOUtil.getDTOName(entityName);
        List<String[]> primaryKeys = getPrimaryKeys(columns, false);

        Map<String, Object> root = new HashMap<>();
        root.put("dbType", AppConfig.DB_TYPE);
        root.put("package", getClassPackage());
        root.put("className", entityName + AppConfig.MAPPER_SUFFIX);
        root.put("beanName", DTOUtil.getBeanName(entityName) + AppConfig.MAPPER_SUFFIX);
        root.put("entityName", entityName);
        root.put("dtoName", dtoName);
        root.put("primaryKeys", primaryKeys);
        root.put("tableName", tableName);
        root.put("resultMapId", entityName.toLowerCase() + "_1");

        Set<String> imports = new TreeSet<>();
        imports.add("java.util.List");
        imports.add("java.util.Map");
        imports.add(DTOUtil.getClassPackage() + "." + entityName + AppConfig.DTO_SUFFIX);
        imports.add("org.apache.ibatis.annotations.Delete");
        imports.add("org.apache.ibatis.annotations.Insert");
        imports.add("org.apache.ibatis.annotations.Mapper");
        imports.add("org.apache.ibatis.annotations.Param");
        imports.add("org.apache.ibatis.annotations.Result");
        imports.add("org.apache.ibatis.annotations.ResultMap");
        imports.add("org.apache.ibatis.annotations.Results");
        imports.add("org.apache.ibatis.annotations.Select");
        imports.add("org.apache.ibatis.annotations.Update");
        for (String[] key : primaryKeys) {
            if (key[2].equals("String")) {
                imports.add("java.lang.String");
            } else if (key[2].contains("java") && !key[2].equals("java.lang.String")) {
                imports.add(key[2]);
            } else if (key[4].equals("Long")) {
                imports.add("java.lang.Long");
            } else if (key[4].equals("Integer")) {
                imports.add("java.lang.Integer");
            }
        }
        root.put("imports", imports);
        // Insert
        List<String> insertFields = new ArrayList<>();
        for (int i=0; i < columns.size(); i++) {
            insertFields.add(columns.get(i).getField());
        }
        root.put("insertFields", insertFields);

        List<String> inserts = new ArrayList<>();
        for (TableColumnDTO column : columns) {
            if (AppConfig.DB_TYPE == 0 || AppConfig.DB_TYPE == 2 || AppConfig.DB_TYPE == 3) {
                inserts.add(getMySQLValueAssigner(column.getJavaName()));
            } else {
                inserts.add(getOracleValueAssigner(column.getJavaName(), column.getJdbcType()));
            }
        }
        root.put("inserts", inserts);
        // Update
        List<TableColumnDTO> normalColumns = getNormalColumns(columns);
        List<String> updateStrs = new ArrayList<>();
        for (TableColumnDTO column : normalColumns) {
            StringBuilder sb = new StringBuilder();
            if (AppConfig.DB_TYPE == 0 || AppConfig.DB_TYPE == 2) {
                sb.append('`').append(column.getField()).append('`');
            } else {
                sb.append('"').append(column.getField()).append('"');
            }
            sb.append(CommonUtil.genIndentSpace(column.getField(), 30));
            sb.append("= ");
            if (column.getJavaName().equalsIgnoreCase("version")) {
                sb.append("version + 1");
            } else {
                if (AppConfig.DB_TYPE == 0 || AppConfig.DB_TYPE == 2 || AppConfig.DB_TYPE == 3) {
                    sb.append(getMySQLValueAssigner(column.getJavaName()));
                } else {
                    sb.append(getOracleValueAssigner(column.getJavaName(), column.getJdbcType()));
                }
            }
            updateStrs.add(sb.toString());
        }
        root.put("updateStrs", updateStrs);

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
        root.put("accurateWheres", getAccurateWheres(columns));
        List<String> resultMapList = columns.stream().map(column->{
            StringBuilder sb = new StringBuilder();
            sb.append("@Result(column=\"");
            sb.append(column.getField()).append("\",");
            sb.append(CommonUtil.genIndentSpace(column.getField(), 30));
            sb.append("property=\"").append(column.getJavaName()).append("\")");
            return sb.toString();
        }).collect(Collectors.toList());
        root.put("resultMapList", resultMapList);



        return root;
    }

    /**
     * 生成mapper xml内容
     *
     * @param tableName 数据表名
     * @param entityName 不带前后缀的纯entity bean名称, 如用默认可输入null或空字符串
     * @param columns 数据表字段
     * @return xml format text
     */
    public static Map<String, Object> genMapperXML(
            String tableName, String entityName, List<TableColumnDTO> columns) {

        Map<String, Object> root = new HashMap<>();
        root.put("dbType", AppConfig.DB_TYPE);
        root.put("tableName", tableName);
        root.put("className", DTOUtil.getClassPackage() + "." + entityName + AppConfig.DTO_SUFFIX);
        root.put("resultMapId", entityName.toLowerCase() + "_1");

        List<String[]> primaryKeys = getPrimaryKeys(columns, true);
        List<TableColumnDTO> keyColumns = getKeyColumns(columns);
        List<TableColumnDTO> normalColumns = getNormalColumns(columns);
        root.put("primaryKeys", primaryKeys);
        root.put("key_columns", keyColumns);
        root.put("nor_columns", normalColumns);
        root.put("namespace", getClassPackage() + "." + entityName + AppConfig.MAPPER_SUFFIX);
        root.put("accurateWheres", getAccurateWheres(columns));

        List<String> resultMapList = new ArrayList<>();
        boolean hasVersion = false;
        String versionField = "";
        for (TableColumnDTO keyColumn : keyColumns) {
            StringBuilder sb = new StringBuilder();
            sb.append("<id column=\"");
            sb.append(keyColumn.getField()).append("\"").append(DT);
            sb.append(CommonUtil.genIndentSpace(keyColumn.getField(), 25));
            sb.append("property=\"").append(keyColumn.getJavaName()).append("\" />");
            resultMapList.add(sb.toString());
        }
        for (TableColumnDTO normalColumn : normalColumns) {
            if (normalColumn.getField().equalsIgnoreCase("version")) {
                hasVersion = true;
                versionField = normalColumn.getField();
            }
            StringBuilder sb = new StringBuilder();
            sb.append("<result column=\"");
            sb.append(normalColumn.getField()).append("\"");
            sb.append(CommonUtil.genIndentSpace(normalColumn.getField(), 25));
            sb.append("property=\"").append(normalColumn.getJavaName()).append("\" />");
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
        for (TableColumnDTO column : keyColumns) {
            if (autoIncrement && !skip) {
                skip = true;
                continue;
            }
            if (AppConfig.DB_TYPE == 0 || AppConfig.DB_TYPE == 2 || AppConfig.DB_TYPE == 3) {
                inserts.add(getMySQLValueAssigner(column.getJavaName()));
            } else {
                inserts.add(getOracleValueAssigner(column.getJavaName(), column.getJdbcType()));
            }
        }
        for (TableColumnDTO column : normalColumns) {
            if (AppConfig.DB_TYPE == 0 || AppConfig.DB_TYPE == 2 || AppConfig.DB_TYPE == 3) {
                inserts.add(getMySQLValueAssigner(column.getJavaName()));
            } else {
                inserts.add(getOracleValueAssigner(column.getJavaName(), column.getJdbcType()));
            }
        }
        root.put("inserts", inserts);

        // Update
        List<String> updateStrs = new ArrayList<>();
        for (TableColumnDTO column : normalColumns) {
            StringBuilder sb = new StringBuilder().append(column.getField());
            sb.append(CommonUtil.genIndentSpace(column.getField(), 20));
            sb.append("= ");
            if (column.getJavaName().equalsIgnoreCase("version")) {
                sb.append("version + 1");
            } else {
                if (AppConfig.DB_TYPE == 0 || AppConfig.DB_TYPE == 2 || AppConfig.DB_TYPE == 3) {
                    sb.append(getMySQLValueAssigner(column.getJavaName()));
                } else {
                    sb.append(getOracleValueAssigner(column.getJavaName(), column.getJdbcType()));
                }
            }
            updateStrs.add(sb.toString());
        }
        root.put("updateStrs", updateStrs);

        // Alter
        List<String> alters = new ArrayList<>();
        for (TableColumnDTO column : normalColumns) {
            StringBuilder sb = new StringBuilder();
            if (column.getJavaName().equalsIgnoreCase("version")) {
                sb.append(column.getField());
                sb.append(CommonUtil.genIndentSpace(column.getField(), 20));
                sb.append("= ");
                sb.append(column.getField() + " + 1,");
            } else {
                sb.append("<if test=\"param." + column.getJavaName() + " != null\">").append(column.getField());
                sb.append(CommonUtil.genIndentSpace(column.getJavaName(), 15))
                        .append(CommonUtil.genIndentSpace(column.getField(),15));
                sb.append("= ");
                if (AppConfig.DB_TYPE == 0 || AppConfig.DB_TYPE == 2 || AppConfig.DB_TYPE == 3) {
                    sb.append(getMySQLValueAssigner("param." + column.getJavaName()));
                } else {
                    sb.append(getOracleValueAssigner("param." + column.getJavaName(), column.getJdbcType()));
                }
                sb.append(",</if>");
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

    private static String getOracleValueAssigner(String field, String jdbcType) {
        return "#{" + field + ", jdbcType=" + jdbcType + "}";
    }

    public static List<String[]> getPrimaryKeys(List<TableColumnDTO> columns, boolean forXml) {
        List<String[]> primaryKeys = new ArrayList<>();
        for (int i = 0; i < columns.size(); i++) {
            if (columns.get(i).isPrimary()) {
                String[] key = new String[5];
                key[0] = columns.get(i).getField();
                key[1] = columns.get(i).getJavaName();
                key[2] = columns.get(i).getJavaType();
                key[3] = columns.get(i).getExtra();
                if (forXml) {
                    if (columns.get(i).getJavaType().equals("String")) {
                        key[2] = "java.lang.String";
                        key[4] = "java.lang.String";
                    } else if (columns.get(i).getJavaType().equalsIgnoreCase("long")) {
                        key[4] = "java.lang.Long";
                    } else if (columns.get(i).getJavaType().equals("int")) {
                        key[4] = "java.lang.Integer";
                    } else if (columns.get(i).getJavaType().equals("Integer")) {
                        key[2] = "java.lang.Integer";
                        key[4] = "java.lang.Integer";
                    }
                } else {
                    key[4] = key[2];
                    if (columns.get(i).getJavaType().equalsIgnoreCase("long")) {
                        key[4] = "Long";
                    } else if (columns.get(i).getJavaType().equals("int")) {
                        key[4] = "Integer";
                    }
                }
                primaryKeys.add(key);
            }
        }
        return primaryKeys;
    }

    public static List<TableColumnDTO> getKeyColumns(List<TableColumnDTO> columns) {
        List<TableColumnDTO> keyColumns = new ArrayList<>();
        for (TableColumnDTO column : columns) {
            if (column.isPrimary()) {
                keyColumns.add(column);
            }
        }
        return keyColumns;
    }

    public static List<TableColumnDTO> getNormalColumns(List<TableColumnDTO> columns) {
        List<TableColumnDTO> normalColumns = new ArrayList<>();
        for (TableColumnDTO column : columns) {
            if (!column.isPrimary()) {
                normalColumns.add(column);
            }
        }
        return normalColumns;
    }

    private static List<String> getAccurateWheres(List<TableColumnDTO> columns) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < columns.size(); i++) {
            if (columns.get(i).isPrimary()) {
                String[] key = new String[2];
                key[0] = columns.get(i).getField();
                key[1] = columns.get(i).getJavaName();
                list.add(key[0] + " = #{" + key[1] + "}");
            }
        }
        return list;
    }
}
