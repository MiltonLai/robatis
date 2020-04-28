package com.rockbb.robatis.dao.dto;

import com.rockbb.robatis.common.CommonUtil;

public class TableColumnDTO {
	private String field = "";
	private String type = "";
	private String key = "";
	private String extra = "";
	private String comments = "";
	private String javaType = null;
	private String javaName = null;

	public String toString() {
		return field + " " + type + " " + key + " " + extra + " " + comments;
	}
	public String getField() { return field; }
	public void setField(String field) { this.field = field; }
	public String getType() { return type; }
	public void setType(String type) { this.type = type; }
	public String getKey() { return key; }
	public void setKey(String key) { this.key = key; }
	public String getExtra() { return extra; }
	public void setExtra(String extra) { this.extra = extra; }
	public String getComments() { return comments; }
	public void setComments(String comments) { this.comments = comments; }
	public String getJavaType() { return javaType; }
	public void setJavaType(String javaType) { this.javaType = javaType; }
	public String getJavaName() { return javaName; }
	public void setJavaName(String javaName) { this.javaName = javaName; }

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
		else if (type.indexOf("TIMESTAMP") == 0) return "TIMESTAMP";
		else if (type.indexOf("NUMBER") == 0) return "NUMERIC";
		else if (type.indexOf("BLOB") == 0) return "VARCHAR";
		else if (type.indexOf("CLOB") == 0) return "VARCHAR";
		else if (type.indexOf("NCLOB") == 0) return "NVARCHAR";
		else return type;
	}
}