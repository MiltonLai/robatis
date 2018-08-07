package com.rockbb.robatis.common;

import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AppConfig {
    private static Logger LOG = Logger.getLogger(AppConfig.class.getName());
    private static final ResourceBundle rb = ResourceBundle.getBundle("application");

    private static String getValue(String key) {
        return rb.getString(key);
    }

    private static int getIntValue(String key) {
        int value = 0;
        try {
            value = Integer.parseInt(getValue(key));
        } catch (Exception e) {
            LOG.log(Level.SEVERE, e.getMessage());
        }
        return value;
    }

    public static final int DTO_PRIMARY_UUID = getIntValue("java.dto.primary.uuid");
    public static final String DTO_PACKAGE = getValue("java.dto.package");
    public static final String MAPPER_PACKAGE = getValue("java.mapper.package");
    public static final String SERVICE_PACKAGE = getValue("java.service.package");
    public static final String SERVICE_IMPL_PACKAGE = getValue("java.service-impl.package");

    public static final String DTO_SUFFIX = getValue("java.dto-suffix");
    public static final String MAPPER_SUFFIX = getValue("java.mapper-suffix");
    public static final String SERVICE_SUFFIX = getValue("java.service-suffix");
    public static final String SERVICE_IMPL_SUFFIX = getValue("java.service-impl-suffix");

    public static final int DB_TYPE = getIntValue("db.type");
    public static final String[] DB_PREFIX = getValue("db.table-prefix").split(",");

    public static final String LE = getValue("file.line.delimiter").equals("0")? "\n" : "\r\n";
    public static final String DT = "    ";
    public static final String FILE_OUT_FOLDER = getValue("file.out.folder");
}
