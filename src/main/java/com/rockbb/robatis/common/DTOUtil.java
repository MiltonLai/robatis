package com.rockbb.robatis.common;

import com.rockbb.robatis.dao.dto.TableColumnDTO;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Milton on 2015/5/24 at 23:04.
 */
public class DTOUtil {

    private static String getEntityName(String tableName) {
        // Try to remove the table-prefix
        for (String prefix : AppConfig.DB_PREFIX) {
            if (tableName.indexOf(prefix) == 0) {
                tableName = tableName.substring(prefix.length());
                break;
            }
        }
        return CommonUtil.camelCaseName(tableName, true);
    }

    public static String getDTOName(String entityName) {
        return entityName + AppConfig.DTO_SUFFIX;
    }

    public static String getBeanName(String entityName) {
        return entityName.substring(0, 1).toLowerCase() + entityName.substring(1);
    }

    public static String genGetter(String varName, String varType) {
        StringBuilder sb = new StringBuilder();
        sb.append("public ").append(varType).append(" get").append(varName.substring(0, 1).toUpperCase()
                + varName.substring(1)).append("() {").append("return ").append(varName).append(";}");
        return sb.toString();
    }

    public static String genSetter(String varName, String varType) {
        StringBuilder sb = new StringBuilder();
        sb.append("public void set").append(varName.substring(0, 1).toUpperCase()
                + varName.substring(1)).append("(").append(varType).append(" ").append(varName).append(") {")
                .append("this.").append(varName).append(" = ").append(varName).append(";}");
        return sb.toString();
    }

    public static String genInitializer(String varName, String varType, boolean isPrimary, String nullable) {
        StringBuilder sb = new StringBuilder();
        String value;
        if (varType.equals("int")) value = "0";
        else if (varType.equals("long")) value = "0L";
        else if (varType.equals("Date")) value = "new Date()";
        else if (varType.equals("Timestamp")) value = "new Timestamp(System.currentTimeMillis())";
        else if (varType.equals("String")) {
            if (isPrimary && AppConfig.DTO_PRIMARY_UUID == 1)
                value = "java.util.UUID.randomUUID().toString().replace(\"-\", \"\")";
            else if (nullable.equals("Y")) {
                value = "null";
            } else {
                value = "\"\"";
            }
        }
        else if (varType.equals("BigDecimal")) value = "BigDecimal.valueOf(0, 3)";
        else if (varType.equals("char")) value = "' '";
        else value = "null";
        sb.append(varName).append(CommonUtil.genIndentSpace(varName, 30)).append("= ").append(value);
        return sb.toString();
    }

    public static String getClassPackage() {
        return AppConfig.DTO_PACKAGE;
    }

    public static String getClassFilePath() {
        String delimiter = "/";
        if (AppConfig.FILE_OUT_FOLDER.lastIndexOf('/') == AppConfig.FILE_OUT_FOLDER.length() - 1)
            delimiter = "";

        return AppConfig.FILE_OUT_FOLDER + delimiter + getClassPackage().replace(".", "/") + "/";
    }

    public static String secondParse(List<TableColumnDTO> columns, String tableName) {
        String mappingFilePath = getFieldMappingPath() + tableName + ".txt";
        File mappingFile = new File(mappingFilePath);
        if (mappingFile.exists()) {
            String entityName = null;
            List<String[]> tmpList = new ArrayList<>();
            List<String> lines = CommonUtil.readFileToLines(mappingFilePath);
            for (String line : lines) {
                if (CommonUtil.regexMatch(line, "^\\/\\*\\*\\s+.*")) {
                    continue;
                }
                if (CommonUtil.regexMatch(line, "^\\/\\/.*")) {
                    entityName = line.substring(2).trim();
                    continue;
                }
                String[] s = line.split("\\s+");
                tmpList.add(s);
            }

            for (int i = 0; i < columns.size(); i++) {
                columns.get(i).setJavaType(tmpList.get(i)[0]);
                columns.get(i).setJavaName(tmpList.get(i)[1]);
            }
            if (entityName == null) {
                return getEntityName(tableName);
            } else {
                return entityName;
            }
        } else {
            for (int i = 0; i < columns.size(); i++) {
                columns.get(i).setJavaType(getVariableType(columns.get(i).getType(), columns.get(i).getExtra().equals("Y")));
                columns.get(i).setJavaName(CommonUtil.camelCaseName(columns.get(i).getField().trim().toLowerCase(), false));
            }
            return getEntityName(tableName);
        }
    }

    public static Map<String, Object> genDTOClass(String className, List<TableColumnDTO> columns, boolean withInitializer) {
        Map<String, Object> root = new HashMap<>();
        root.put("package", getClassPackage());
        root.put("className", className);
        root.put("withInitializer", withInitializer);

        Set<String> imports = new TreeSet<>();
        List<String> comments = new ArrayList<>();
        List<String> fields = new ArrayList<>();
        List<String> methods = new ArrayList<>();
        List<String> inits = new ArrayList<>();

        imports.add("java.io.Serializable");
        for (TableColumnDTO column : columns) {
            String varName = column.getJavaName();
            String varType = column.getJavaType();
            String importer = getImporter(varType);
            if (importer != null) {
                imports.add(importer);
            }
            comments.add(column.toString());
            fields.add("private " + varType + " " + varName);
            methods.add(genGetter(varName, varType));
            methods.add(genSetter(varName, varType));
            if (withInitializer) {
                inits.add(genInitializer(varName, varType, column.isPrimary(), column.getExtra()));
            }
        }

        root.put("imports", imports);
        root.put("comments", comments);
        root.put("fields", fields);
        root.put("methods", methods);
        root.put("inits", inits);
        return root;
    }

    private static String getImporter(String varType) {
        if (varType.equals("Date")) return "java.util.Date";
        else if (varType.equals("Timestamp")) return "java.sql.Timestamp";
        else if (varType.equals("String")) return "java.lang.String";
        else if (varType.equals("Long")) return "java.lang.Long";
        else if (varType.equals("Integer")) return "java.lang.Integer";
        else if (varType.equals("BigDecimal")) return "java.math.BigDecimal";
        else return null;
    }

    private static String getVariableType(String columnType, boolean nullable) {
        String type = _getVariableType(columnType);
        if (nullable && type.equals("int")) {
            return "Integer";
        }
        if (nullable && type.equals("long")) {
            return "Long";
        }
        return type;
    }

    private static String _getVariableType(String columnType) {
        columnType = columnType.trim().toLowerCase().replace("numeric", "decimal");
        if (columnType.indexOf("number") == 0) {
            Pattern p = Pattern.compile("(?i)^number\\((\\d+),(\\d+)\\)$");
            Matcher m = p.matcher(columnType);
            if (m.find()) {
                int length = Integer.parseInt(m.group(1));
                int scale = Integer.parseInt(m.group(2));
                if (scale > 0)
                    return "BigDecimal";
                else {
                    if (length < 10) return "int";
                    else return "long";
                }
            }

            p = Pattern.compile("(?i)^number\\((\\d+)\\)$");
            m = p.matcher(columnType);
            if (m.find()) {
                int length = Integer.parseInt(m.group(1));
                if (length < 10) return "int";
                else return "long";
            } else
                return "long";
        }
        if (columnType.indexOf("bigint") == 0)
            return "long";
        else if (columnType.indexOf("int") == 0 && columnType.indexOf("unsigned") > 0)
            return "long";
        else if (columnType.indexOf("int") == 0
                || columnType.indexOf("mediumint") == 0
                || columnType.indexOf("smallint") == 0
                || columnType.indexOf("tinyint") == 0)
            return "int";
        else if (columnType.indexOf("char(1)") == 0)
            return "char";
        else if (columnType.indexOf("char") == 0
                || columnType.indexOf("nvarchar") == 0
                || columnType.indexOf("nchar") == 0
                || columnType.indexOf("varchar") == 0
                || columnType.indexOf("text") == 0
                || columnType.indexOf("mediumtext") == 0
                || columnType.indexOf("blob") == 0
                || columnType.indexOf("clob") == 0
                || columnType.indexOf("nclob") == 0)
            return "String";
        else if (columnType.indexOf("decimal") == 0)
            return "BigDecimal";
        else if (columnType.indexOf("timestamp") == 0) {
            if (AppConfig.DB_TYPE == 0) {
                return "Timestamp";
            } else {
                return "Date";
            }
        } else if (columnType.indexOf("date") == 0)
            return "Date";
        else
            return "Unknown";
    }

    public static String getFieldMappingPath() {
        String delimiter = "/";
        if (AppConfig.FILE_OUT_FOLDER.lastIndexOf('/') == AppConfig.FILE_OUT_FOLDER.length() - 1)
            delimiter = "";

        return AppConfig.FILE_OUT_FOLDER + delimiter + "mapping/";
    }
}
