<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//ibatis.apache.org//DTD Mapper 3.0//EN" "http://ibatis.apache.org/builder/xml/mybatis-3-mapper.dtd">

<mapper namespace="${namespace}">
    <cache eviction="LRU" flushInterval="300000" size="128" readOnly="false" />
    <resultMap type="${className}" id="${resultMapId}">
<#if resultMapList??><#list resultMapList as resultMap>        ${resultMap}
</#list></#if>
    </resultMap>
    <sql id="key"><#if primaryKeys??><#list primaryKeys as primaryKey>${primaryKey[0]}<#if primaryKey_has_next>, </#if></#list></#if></sql>
    <sql id="columns">
        <#if columns??><#list columns as column><#if !column.primary>${column.field}<#if column_has_next>, </#if></#if></#list></#if>
    </sql>
    <sql id="table">${tableName}</sql>

    <insert id="insert" parameterType="${className}"<#if autoIncrement> useGeneratedKeys="true" keyProperty="${primaryKeys[0][1]}"</#if>>
        INSERT INTO <include refid="table" />
        (<include refid="key" />, <include refid="columns" />)
        VALUES (
<#list inserts as insert>        ${insert}<#if insert_has_next>,
</#if></#list>)
    </insert>

    <update id="update" parameterType="${className}">
        UPDATE <include refid="table" /> SET
<#list updateStrs as updateStr>            ${updateStr}<#if updateStr_has_next>,${'\n'}</#if></#list>
        WHERE <#if accurateWheres??><#list accurateWheres as accurateWhere>${accurateWhere}<#if accurateWhere_has_next> AND </#if></#list></#if><#if hasVersion> AND version = ${'#'}{"${versionField}"}</#if>
    </update>

    <update id="alter">
        UPDATE <include refid="table" />
        <set>
<#list alters as alter>            ${alter}<#if alter_has_next>${'\n'}</#if></#list>
        </set>
        WHERE <#if accurateWheres??><#list accurateWheres as accurateWhere>${accurateWhere}<#if accurateWhere_has_next> AND </#if></#list></#if><#if hasVersion> AND version = ${'#'}{"${versionField}"}</#if>
    </update>

    <delete id="delete"<#if primaryKeys??><#if primaryKeys?size == 1> parameterType="${primaryKeys[0][2]}"</#if></#if>>
        DELETE FROM <include refid="table" />
        WHERE <#if accurateWheres??><#list accurateWheres as accurateWhere>${accurateWhere}<#if accurateWhere_has_next> AND </#if></#list></#if>
    </delete>

    <select id="select"<#if primaryKeys??><#if primaryKeys?size == 1> parameterType="${primaryKeys[0][2]}"</#if></#if> resultMap="${resultMapId}">
        SELECT <include refid="key" />, <include refid="columns" />
        FROM <include refid="table" />
        WHERE <#if accurateWheres??><#list accurateWheres as accurateWhere>${accurateWhere}<#if accurateWhere_has_next> AND </#if></#list></#if>
    </select>

    <sql id="params">

    </sql>

<#if dbType == 0>
    <select id="list" resultMap="${resultMapId}">
        SELECT
            <include refid="key" />, <include refid="columns" />
        FROM
            <include refid="table" />
        <where>
            <include refid="params" />
        </where>
        ORDER BY
            ${'$'}{pager.sort} ${'$'}{pager.order}
        LIMIT
            ${'#'}{pager.offset}, ${'#'}{pager.limit}
    </select>

<#else>
    <select id="list" resultMap="${resultMapId}">
        SELECT <include refid="key" />, <include refid="columns" /> FROM
        (
            SELECT A.*, ROWNUM AS RN FROM
            (
                SELECT B.*
                FROM <include refid="table" /> B
                <where>
                  <include refid="params" />
                </where>
                ORDER BY
                B.${'$'}{pager.sort} ${'$'}{pager.order}
            ) A
            WHERE ROWNUM &lt; ${'#'}{pager.limit}
        )
        WHERE RN &gt;= ${'#'}{pager.offset}
    </select>

</#if>
<#if primaryKeys??><#if primaryKeys?size == 1><#assign primaryKey = primaryKeys[0]><#if dbType == 0>
    <select id="listIds" resultType="${primaryKey[4]}">
        SELECT
            <include refid="key" />
        FROM
            <include refid="table" />
        <where>
            <include refid="params" />
        </where>
        ORDER BY
            ${'$'}{pager.sort} ${'$'}{pager.order}
        LIMIT
            ${'#'}{pager.offset}, ${'#'}{pager.limit}
    </select>

<#else>
    <select id="listIds" resultType="${primaryKey[4]}">
        SELECT <include refid="key" /> FROM
        (
            SELECT A.*, ROWNUM AS RN FROM
            (
                SELECT B.<include refid="key" />
                FROM <include refid="table" /> B
                <where>
                  <include refid="params" />
                </where>
                ORDER BY
                B.${'$'}{pager.sort} ${'$'}{pager.order}
            ) A
            WHERE ROWNUM &lt; ${'#'}{pager.limit}
        )
        WHERE RN &gt;= ${'#'}{pager.offset}
    </select>

</#if></#if></#if>
    <select id="count" resultType="long">
        SELECT
            COUNT(<include refid="key" />)
        FROM
            <include refid="table" />
        <where>
            <include refid="params" />
        </where>
    </select>
</mapper>
