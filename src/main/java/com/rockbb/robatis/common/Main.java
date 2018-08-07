package com.rockbb.robatis.common;

import com.rockbb.robatis.dao.dto.TableColumnDTO;
import com.rockbb.robatis.dao.mapper.TableColumnMapper;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

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

        List<String> tableNames = (AppConfig.DB_TYPE == 0)?
                tableColumnMapper.getMySQLTables() : tableColumnMapper.getOracleTables();

        for (String tableName : tableNames) {
            String entityName = DTOUtil.getEntityName(tableName);
            String dtoName = DTOUtil.getDTOName(entityName);

            System.out.println(dtoName + "(" + tableName + ")");

            Map<String, Object> params = new HashMap<String, Object>();
            params.put("tableName", tableName);
            List<TableColumnDTO> columns = (AppConfig.DB_TYPE == 0)?
                    tableColumnMapper.getMySQLSchema(params) : tableColumnMapper.getOracleSchema(params);
            Map<String, Object> data = DTOUtil.genDTOClass(dtoName, columns, true);
            String destPath = DTOUtil.getClassFilePath();
            helper.write(destPath, dtoName + ".java", "dto.ftl", data);

            data = MapperUtil.genMapperInterface(tableName, null, columns, null);
            destPath = MapperUtil.getClassFilePath();
            helper.write(destPath, entityName + AppConfig.MAPPER_SUFFIX + ".java", "mapper.ftl", data);

            data = MapperUtil.genMapperXML(tableName, null, columns, null);
            helper.write(destPath, entityName + AppConfig.MAPPER_SUFFIX + ".xml", "mapper_xml.ftl", data);

            data = ServiceUtil.genServiceInterface(tableName, null, columns, null);
            destPath = ServiceUtil.getInterfaceFilePath();
            helper.write(destPath, entityName + AppConfig.DTO_SUFFIX + AppConfig.SERVICE_SUFFIX + ".java", "service.ftl", data);

            data = ServiceUtil.genServiceImplementation(tableName, null, columns, null);
            destPath = ServiceUtil.getImplFilePath();
            helper.write(destPath, entityName + AppConfig.DTO_SUFFIX + AppConfig.SERVICE_IMPL_SUFFIX + ".java", "service_impl.ftl", data);
        }

    }
}
