package com.rockbb.robatis.common;

import com.rockbb.robatis.dao.dto.TableColumnDTO;
import com.rockbb.robatis.dao.mapper.TableColumnMapper;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Milton on 2015/5/24 at 22:07.
 */
public class Main {

    public static void main(String[] args)     {
        ApplicationContext context = new ClassPathXmlApplicationContext("./spring/spring-context.xml");
        TableColumnMapper tableColumnMapper = (TableColumnMapper)context.getBean("tableColumnMapper");

        FreemarkerHelper helper = new FreemarkerHelper("/template", "UTF-8");

        List<String> tableNames = null;
        if (AppConfig.DB_TYPE == 0 || AppConfig.DB_TYPE == 2) {
            tableNames = tableColumnMapper.getMySQLTables();
        } else if (AppConfig.DB_TYPE == 1) {
            tableNames = tableColumnMapper.getOracleTables();
        } else if (AppConfig.DB_TYPE == 3) {
            tableNames = tableColumnMapper.getPGTables();
        }
        List<String> tablesInclude = null;
        if (AppConfig.TABLES_INCLUDE != null && AppConfig.TABLES_INCLUDE.length() > 0) {
            String[] array = AppConfig.TABLES_INCLUDE.split(",");
            tablesInclude = Arrays.asList(array);
        }
        String mappingPath = DTOUtil.getFieldMappingPath();
        File mappingFolder = new File(mappingPath);
        if (!mappingFolder.isDirectory()) {
            mappingFolder.mkdirs();
        }

        for (String tableName : tableNames) {
            if (tablesInclude != null) {
                if (!tablesInclude.contains(tableName.toUpperCase())) {
                    continue;
                }
            }

            Map<String, Object> params = new HashMap<>();
            params.put("tableName", tableName);
            List<TableColumnDTO> columns = null;
            if (AppConfig.DB_TYPE == 0 || AppConfig.DB_TYPE == 2) {
                columns = tableColumnMapper.getMySQLSchema(params);
            } else if (AppConfig.DB_TYPE == 1) {
                columns = tableColumnMapper.getOracleSchema(params);
            } else if (AppConfig.DB_TYPE == 3) {
                columns = tableColumnMapper.getPGSchema(params);
            }

            String entityName = DTOUtil.secondParse(columns, tableName);
            Map<String, Object> data = new HashMap<>();
            data.put("name", entityName);
            data.put("columns", columns);
            helper.write(mappingPath, tableName + ".txt", "field_map.ftl", data);

            String dtoName = DTOUtil.getDTOName(entityName);
            System.out.println(dtoName + "(" + tableName + ")");

            data = DTOUtil.genDTOClass(dtoName, columns, true);
            String destPath = DTOUtil.getClassFilePath();
            helper.write(destPath, dtoName + ".java", "dto.ftl", data);

            data = MapperUtil.genMapperInterface(tableName, entityName, columns);
            destPath = MapperUtil.getClassFilePath();
            helper.write(destPath, entityName + AppConfig.MAPPER_SUFFIX + ".java", "mapper.ftl", data);

            data = MapperUtil.genMapperXML(tableName, entityName, columns);
            helper.write(destPath, entityName + AppConfig.MAPPER_SUFFIX + ".xml", "mapper_xml.ftl", data);

            data = ServiceUtil.genServiceInterface(entityName, columns);
            destPath = ServiceUtil.getInterfaceFilePath();
            helper.write(destPath, entityName + AppConfig.DTO_SUFFIX + AppConfig.SERVICE_SUFFIX + ".java", "service.ftl", data);

            data = ServiceUtil.genServiceImplementation(entityName, columns);
            destPath = ServiceUtil.getImplFilePath();
            helper.write(destPath, entityName + AppConfig.DTO_SUFFIX + AppConfig.SERVICE_IMPL_SUFFIX + ".java", "service_impl.ftl", data);
        }

    }
}
