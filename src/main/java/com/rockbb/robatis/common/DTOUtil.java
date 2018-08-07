package com.rockbb.robatis.common;

import com.rockbb.robatis.dao.dto.TableColumnDTO;

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

    public static String getEntityName(String tableName) {
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

    public static String genInitializer(String varName, String varType, boolean isPrimary) {
        StringBuilder sb = new StringBuilder();
        String value;
        if (varType.equals("int")) value = "0";
        else if (varType.equals("long")) value = "0L";
        else if (varType.equals("Date")) value = "new Date()";
        else if (varType.equals("Timestamp")) value = "new Timestamp(System.currentTimeMillis())";
        else if (varType.equals("String")) {
            if (isPrimary && AppConfig.DTO_PRIMARY_UUID == 1)
                value = "java.util.UUID.randomUUID().toString().replace(\"-\", \"\")";
            else value = "\"\"";
        }
        else if (varType.equals("BigDecimal")) value = "BigDecimal.valueOf(0, 3)";
        else if (varType.equals("char")) value = "' '";
        else value = "null";
        sb.append(varName).append(CommonUtil.genIndentSpace(varName, 30)).append("= ").append(value).append(";");
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

    public static Map<String, Object> genDTOClass(String className, List<TableColumnDTO> columns, boolean withInitializer) {
        Map<String, Object> root = new HashMap<>();
        root.put("package", getClassPackage());
        root.put("className", className);
        root.put("withInitializer", withInitializer);

        Set<String> imports = new TreeSet<>();
        List<String> fields = new ArrayList<>();
        List<String> methods = new ArrayList<>();
        List<String> inits = new ArrayList<>();

        imports.add("java.io.Serializable");
        for (TableColumnDTO column : columns) {
            String varName = column.getVariableName();
            String varType = getVariableType(column.getType());
            String importer = getImporter(varType);
            if (importer != null) {
                imports.add(importer);
            }
            fields.add("private "+varType+" "+varName);
            methods.add(genGetter(varName, varType));
            methods.add(genSetter(varName, varType));
            if (withInitializer) {
                inits.add(genInitializer(varName, varType, column.isPrimary()));
            }
        }

        root.put("imports", imports);
        root.put("fields", fields);
        root.put("methods", methods);
        root.put("inits", inits);
        return root;
    }

    private static String getImporter(String varType) {
        if (varType.equals("Date")) return "java.util.Date";
        else if (varType.equals("Timestamp")) return "java.sql.Timestamp";
        else if (varType.equals("String")) return "java.lang.String";
        else if (varType.equals("BigDecimal")) return "java.math.BigDecimal";
        else return null;
    }

    private static String getVariableType(String columnType) {
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
        else if (columnType.indexOf("timestamp") == 0)
            return "Timestamp";
        else if (columnType.indexOf("date") == 0)
            return "Date";
        else
            return "Unknown";
    }

}
