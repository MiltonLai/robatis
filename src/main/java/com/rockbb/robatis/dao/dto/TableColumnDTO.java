package com.rockbb.robatis.dao.dto;

import com.rockbb.robatis.common.CommonUtil;

public class TableColumnDTO {
	private String field = "";
	private String type = "";
	private String key = "";
	private String extra = "";

	public String toString() {
		return field + " " + type + " " + key + " " + extra;
	}
	public String getField() {
		return field;
	}
	public void setField(String field) {
		this.field = field;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getExtra() {
		return extra;
	}
	public void setExtra(String extra) {
		this.extra = extra;
	}

	// Utilities
	public boolean isPrimary() {
		return (key.equals("PRI") || key.equals("P"));
	}

	public String getJdbcType() {
		if (type.indexOf("NVARCHAR2") == 0) return "NVARCHAR";
		else if (type.indexOf("CHAR") == 0) return "VARCHAR";
		else if (type.indexOf("NCHAR") == 0) return "NVARCHAR";
		else if (type.indexOf("VARCHAR2") == 0) return "VARCHAR";
		else if (type.indexOf("DATE") == 0) return "TIMESTAMP";
		else if (type.indexOf("NUMBER") == 0) return "NUMERIC";
		else if (type.indexOf("BLOB") == 0) return "VARCHAR";
		else if (type.indexOf("CLOB") == 0) return "VARCHAR";
		else if (type.indexOf("NCLOB") == 0) return "NVARCHAR";
		else return type;
	}

	public String getVariableName() {
		return CommonUtil.camelCaseName(field.trim().toLowerCase(), false);
	}
}