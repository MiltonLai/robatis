package com.rockbb.robatis.common;


import com.rockbb.robatis.dao.dto.TableColumnDTO;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CodeGenerator
{
	public static final int TYPE_MYSQL = 0;
	public static final int TYPE_ORACLE = 1;
	private static final String IGNORE_FIELD = "is_deleted";
	private static final String LE = "\r\n";
	private int type;
	private List<TableColumnDTO> dbFields;
	private List<String> classFields;
	private String dbTableName;
	private String className;
	private String resultMapId;
	private List<String[]> primaryKeys;
	private String accurateWhere;

	public CodeGenerator(
			int type,
			List<TableColumnDTO> dbFields,
			List<String> classFields,
			String dbTableName,
			String className)
	{
		this.type = type;
		this.dbFields = dbFields;
		this.classFields = classFields;
		this.dbTableName = dbTableName;
		this.className = className;
		String[] nameParts = className.split("\\.");
		resultMapId = nameParts[nameParts.length - 1].toLowerCase() + "_1";

		// remove the ignore fields
		for (int i = 0; i < this.dbFields.size(); i++)
		{
			if (this.dbFields.get(i).getField().equals(IGNORE_FIELD))
			{
				this.dbFields.remove(i);
				i--;
			}
		}

		// make up the array size difference
		int diff = dbFields.size() - classFields.size();
		if (diff > 0)
		{
			for (int i = 0; i < diff; i ++) classFields.add("");
		}
		else if (diff < 0)
		{
			for (int i = 0; i < - diff; i ++) dbFields.add(new TableColumnDTO());
		}

		primaryKeys = new ArrayList<String[]>();
		accurateWhere = "";
		// Get the type of primary key
		for (int i = 0; i < dbFields.size(); i ++)
		{
			if (dbFields.get(i).isPrimary())
			{
				String[] key = new String[4];
				key[0] = dbFields.get(i).getField();
				key[1] = classFields.get(i);
				key[3] = dbFields.get(i).getExtra();
				if (dbFields.get(i).getType().indexOf("CHAR") >= 0
						|| dbFields.get(i).getType().indexOf("char") >= 0)
					key[2] = "java.lang.String";
				else
					key[2] = "long";

				primaryKeys.add(key);
				if (accurateWhere.length() > 0) accurateWhere += " AND ";
				accurateWhere += key[0] + " = #{" + key[1] + "}";
			}
		}

	}

	public String generate()
	{
		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>").append(LE)
		.append("<!DOCTYPE mapper PUBLIC \"-//ibatis.apache.org//DTD Mapper 3.0//EN\" \"http://ibatis.apache.org/builder/xml/mybatis-3-mapper.dtd\">")
		.append(LE).append(LE).append("<mapper namespace=\"PO_MAPPER_NAME\">").append(LE);
		sb.append("\t<cache eviction=\"LRU\" flushInterval=\"300000\" size=\"128\" readOnly=\"true\" />");
		// Result Map
		sb.append(LE).append("\t<resultMap type=\"" + className + "\" id=\"" + resultMapId + "\">").append(LE);
		for (int i = 0; i < dbFields.size(); i ++)
		{
			sb.append("\t\t");
			if (dbFields.get(i).isPrimary())
				sb.append("<id column=\"");
			else
				sb.append("<result column=\"");
			sb.append(dbFields.get(i).getField()).append("\"")
			.append(genTabSpace(dbFields.get(i).getField(), 2, 9, 1))
			.append("property=\"").append(classFields.get(i)).append("\" />").append(LE);
		}
		sb.append("\t</resultMap>");

		// ref: key
		sb.append(LE);
		sb.append("\t<sql id=\"key\">");
		for (int i = 0; i < primaryKeys.size(); i ++)
		{
			sb.append(primaryKeys.get(i)[0]);
			if (i < primaryKeys.size() - 1) sb.append(", ");
		}
		sb.append("</sql>");
		// ref: columns
		sb.append(LE);
		sb.append("\t<sql id=\"columns\">");
		for (int i = 0; i < dbFields.size(); i ++)
		{
			if (dbFields.get(i).isPrimary()) continue;
			sb.append(dbFields.get(i).getField());
			if (i < dbFields.size() - 1) sb.append(", ");
		}
		sb.append("</sql>");

		// ref: table
		sb.append(LE).append("\t<sql id=\"table\">" + dbTableName + "</sql>");

		// Insert
		boolean autoIncrement = false;
		if (primaryKeys.size() == 1 && primaryKeys.get(0)[3].indexOf("auto_increment") >= 0)
		{
			autoIncrement = true;
		}

		if (autoIncrement)
			sb.append(LE).append(LE).append("\t<insert id=\"insert\" parameterType=\"" + className + "\" useGeneratedKeys=\"true\" keyProperty=\"" + primaryKeys.get(0)[1] + "\">");
		else
			sb.append(LE).append(LE).append("\t<insert id=\"insert\" parameterType=\"" + className + "\">");
		sb.append(LE).append("\t\tINSERT INTO <include refid=\"table\" />");
		if (autoIncrement)
			sb.append(LE).append("\t\t(<include refid=\"columns\" />)");
		else
			sb.append(LE).append("\t\t(<include refid=\"key\" />, <include refid=\"columns\" />)");
		sb.append(LE).append("\t\tVALUES (").append(LE);
		boolean skip = false;
		for (int i = 0; i < classFields.size(); i ++)
		{
			if (dbFields.get(i).isPrimary() && autoIncrement && !skip) {skip = true; continue;}
			sb.append("\t\t");
			if (type == TYPE_MYSQL) {
				sb.append(getMySQLValueAssigner(classFields.get(i)));
			} else {
				sb.append(getOracleValueAssigner(classFields.get(i), dbFields.get(i)));
			}
			if (i < classFields.size() - 1) sb.append(",").append(LE);
		}
		sb.append(")").append(LE).append("\t</insert>");

		// Update
		sb.append(LE).append(LE).append("\t<update id=\"update\" parameterType=\"" + className + "\">");
		sb.append(LE).append("\t\tUPDATE <include refid=\"table\" /> SET").append(LE);
		for (int i = 0; i < dbFields.size(); i ++)
		{
			if (dbFields.get(i).isPrimary()) continue;
			sb.append("\t\t\t").append(dbFields.get(i).getField())
			.append(genTabSpace(dbFields.get(i).getField(), 2, 9, 0)).append("= ");
			if (type == TYPE_MYSQL) {
				sb.append(getMySQLValueAssigner(classFields.get(i)));
			} else {
				sb.append(getOracleValueAssigner(classFields.get(i), dbFields.get(i)));
			}
			if (i < dbFields.size() - 1) sb.append(",");
			sb.append(LE);
		}
		sb.append("\t\tWHERE " + accurateWhere);
		sb.append(LE).append("\t</update>");

		// Delete
		String primaryKeyType = (primaryKeys.size() == 1)? primaryKeys.get(0)[2] : "java.util.Map";
		sb.append(LE).append(LE).append("\t<delete id=\"delete\" parameterType=\"").append(primaryKeyType).append("\">").append(LE)
		.append("\t\tDELETE FROM <include refid=\"table\" />").append(LE)
		.append("\t\tWHERE ").append(accurateWhere).append(LE).append("\t</delete>");

		// Select
		sb.append(LE).append(LE).append("\t<select id=\"select\" parameterType=\"").append(primaryKeyType).append("\" resultMap=\"" + resultMapId + "\">").append(LE)
		.append("\t\tSELECT <include refid=\"key\" />, <include refid=\"columns\" />").append(LE)
		.append("\t\tFROM <include refid=\"table\" />").append(LE)
		.append("\t\tWHERE ").append(accurateWhere).append(LE).append("\t</select>");

		// SelectByPageAndCondition
		sb.append(LE).append(LE).append("\t<select id=\"selectByPageAndCondition\" parameterType=\"java.util.Map\" resultMap=\"" + resultMapId + "\">").append(LE);
		if (type == TYPE_MYSQL)
		{
			sb.append("\t\tSELECT").append(LE).append("\t\t\t<include refid=\"key\" />, <include refid=\"columns\" />").append(LE)
			.append("\t\tFROM").append(LE).append("\t\t\t<include refid=\"table\" />").append(LE)
			.append("\t\t<where>").append(LE).append("\t\t\t<if test=\"category != null\">AND category = #{category}</if>").append(LE)
			.append("\t\t</where>").append(LE).append("\t\tORDER BY").append(LE).append("\t\t\t${orderby} ${order}").append(LE)
			.append("\t\tLIMIT").append(LE).append("\t\t\t#{start}, #{limit}").append(LE);
		}
		else
		{
			sb.append("\t\tSELECT <include refid=\"key\" />, <include refid=\"columns\" /> FROM").append(LE)
			.append("\t\t(").append(LE)
			.append("\t\t\tSELECT A.*, ROWNUM AS RN FROM").append(LE)
			.append("\t\t\t(").append(LE)
			.append("\t\t\t\tSELECT <include refid=\"key\" />, <include refid=\"columns\" /> ").append(LE)
			.append("\t\t\t\tFROM <include refid=\"table\" />").append(LE)
			.append("\t\t\t\t<where>").append(LE).append("\t\t\t\t\t<if test=\"category != null\">AND category = #{category}</if>").append(LE)
			.append("\t\t\t\t</where>").append(LE).append("\t\t\t\tORDER BY").append(LE).append("\t\t\t\t\t${orderby} ${order}").append(LE)
			.append("\t\t\t) A").append(LE)
			.append("\t\t\tWHERE ROWNUM &lt; #{limit}").append(LE)
			.append("\t\t)").append(LE)
			.append("\t\tWHERE RN &gt;= #{start}").append(LE);
		}
		sb.append("\t</select>");

		// CountByCondition
		sb.append(LE).append(LE).append("\t<select id=\"countByCondition\" parameterType=\"java.util.Map\" resultType=\"long\">").append(LE)
		.append("\t\tSELECT").append(LE).append("\t\t\tCOUNT(<include refid=\"key\" />) ").append(LE)
		.append("\t\tFROM").append(LE).append("\t\t\t<include refid=\"table\" />").append(LE)
		.append("\t\t<where>").append(LE).append("\t\t\t<if test=\"category != null\">AND category = #{category}</if>").append(LE)
		.append("\t\t</where>").append(LE).append("\t</select>").append(LE);

		sb.append("</mapper>").append(LE);
		return sb.toString();
	}

	public static List<String> getClassFields(Class clazz)
	{
		List<Field> fields = new ArrayList<Field>();
		getAllFields(fields, clazz);
		List<String> privateFields = new ArrayList<String>();
		StringBuffer sb = new StringBuffer();
		for (Field f : fields)
		{
			if (!Modifier.isPublic(f.getModifiers())
					&& !Modifier.isStatic(f.getModifiers()))
			{
				privateFields.add(f.getName());
				String type = f.getType().toString();
				String value = "?";
				String setter = "";
				if (type.equals("int")) value = "0";
				else if (type.equals("long")) value = "0L";
				else if (type.equals("class java.util.Date")) value = "new Date()";
				else if (type.equals("class java.sql.Timestamp")) value = "new Timestamp(System.currentTimeMillis())";
				else if (type.equals("class java.lang.String")) value = "\"\"";
				else if (type.equals("class java.math.BigDecimal")) value = "BigDecimal.valueOf(0, 3)";
				sb.append(f.getName()).append(genTabSpace(f.getName(), 4, 3, 0)).append("= ")
						.append(value).append(";").append(LE);
			}
		}
		System.out.println(sb.toString());

		StringBuffer sb2 = new StringBuffer();
		List<Method> methods = new ArrayList<Method>();
		getAllMethods(methods, clazz);
		for (Method method : methods)
		{
			String[] setter = parseSetter(method);
			if (setter != null)
			{
				String value = "";
				sb2.append("entity.").append(setter[0]).append('(');
				if (setter[1].equals("int")) value = "bean.getInt(\"" + setter[2] + "\")";
				else if (setter[1].equals("long")) value = "bean.getLong(\""  + setter[2] + "\")";
				else if (setter[1].equals("java.util.Date")) value = "TimeUtil.getDate(bean.get(\"" + setter[2] + "\"),\"yyyy-MM-dd HH:mm\")";
				else if (setter[1].equals("java.lang.String")) value = "bean.get(\"" + setter[2] + "\")";
				else if (setter[1].equals("java.math.BigDecimal")) value = "new BigDecimal(bean.get(\"" + setter[2] + "\"))";
				else value = "bean.get(\"" + setter[2] + "\")";
				sb2.append(value).append(");").append(LE);
			}
		}
		System.out.println(sb2.toString());

		return privateFields;
	}

	private static List<Field> getAllFields(List<Field> fields, Class<?> type) {
	    if (type.getSuperclass() != null) {
	        fields = getAllFields(fields, type.getSuperclass());
	    }
	    fields.addAll(Arrays.asList(type.getDeclaredFields()));

	    return fields;
	}

	private static List<Method> getAllMethods(List<Method> methods, Class<?> type) {
		if (type.getSuperclass() != null) {
	        methods = getAllMethods(methods, type.getSuperclass());
	    }
		methods.addAll(Arrays.asList(type.getDeclaredMethods()));
	    return methods;
	}

	private static StringBuffer genTabSpace(String str, int size, int steps, int adjust)
	{
		int tsize = (str.length() + adjust) / size;
		tsize = (tsize > steps)? 1 : steps + 1 - tsize;
		StringBuffer ts = new StringBuffer();
		for(int j = 0; j < tsize; j ++) ts.append("\t");
		return ts;
	}

	private String getMySQLValueAssigner(String field)
	{
		StringBuffer sb = new StringBuffer();
		sb.append("#{").append(field).append("}");
		return sb.toString();
	}

	private String getOracleValueAssigner(String field, TableColumnDTO t)
	{
		StringBuffer sb = new StringBuffer();
		sb.append("#{").append(field).append(", jdbcType=").append(t.getJdbcType()).append("}");
		return sb.toString();
	}

	private static String[] parseSetter(Method method)
	{
		if (Modifier.isPublic(method.getModifiers()) 
				&& method.getReturnType().equals(void.class) 
				&& method.getParameterTypes().length == 1 
				&& method.getName().matches("^set[A-Z].*"))
		{
			String[] setter = new String[3];
			setter[0] = method.getName();
			setter[1] = method.getParameterTypes()[0].getName();
			setter[2] = method.getName().substring(3, 4).toLowerCase() + method.getName().substring(4);
			return setter;
		}
		return null;
	}
}
