package com.rockbb.robatis.dao.mapper;

import com.rockbb.robatis.dao.dto.TableColumnDTO;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository("tableSchemaMapper")
public interface TableColumnMapper
{
	List<String> getMySQLTables();
	List<String> getOracleTables();
	List<String> getPGTables();
	List<TableColumnDTO> getMySQLSchema(Map<String, Object> params);
	List<TableColumnDTO> getOracleSchema(Map<String, Object> params);
	List<TableColumnDTO> getPGSchema(Map<String, Object> params);
}