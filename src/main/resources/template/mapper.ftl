package ${package};

<#if imports??><#list imports as import>import ${import};
</#list></#if>

@Mapper
public interface ${className} {
<#if dbType == 0 || dbType == 2>
    @Insert("""
        INSERT INTO `${tableName}` (
<#if insertFields??><#list insertFields as item>            `${item}`<#if item_has_next>,</#if>
</#list></#if>
<#elseif dbType == 3>
    @Insert("""
        INSERT INTO "${tableName}" (
<#if insertFields??><#list insertFields as item>            "${item}"<#if item_has_next>,</#if>
</#list></#if>
</#if>
        ) VALUES (
<#list inserts as insert>            ${insert}<#if insert_has_next>,</#if>
</#list>        )
    """)
    int insert(${dtoName} dto);

<#if dbType == 0 || dbType == 2>
    @Delete("""
        DELETE FROM `${tableName}` WHERE <#if accurateWheres??><#list accurateWheres as accurateWhere>${accurateWhere}<#if accurateWhere_has_next> AND </#if></#list></#if>
    """)
<#elseif dbType == 3>
    @Delete("""
        DELETE FROM "${tableName}" WHERE <#if accurateWheres??><#list accurateWheres as accurateWhere>${accurateWhere}<#if accurateWhere_has_next> AND </#if></#list></#if>
    """)
</#if>
    int delete(<#if primaryVars??><#list primaryVars as primaryVar>${primaryVar}<#if primaryVar_has_next>, </#if></#list></#if>);

    @Update("""
<#if dbType == 0 || dbType == 2>
        UPDATE `${tableName}` SET
<#elseif dbType == 3>
        UPDATE "${tableName}" SET
</#if>
<#list updateStrs as updateStr>            ${updateStr}<#if updateStr_has_next>,</#if>
</#list>
        WHERE <#if accurateWheres??><#list accurateWheres as accurateWhere>${accurateWhere}<#if accurateWhere_has_next> AND </#if></#list></#if>
    """)
    int update(${dtoName} dto);

    @Results(id="${resultMapId}", value={
<#if resultMapList??><#list resultMapList as resultMap>        ${resultMap},
    </#list></#if>
    })
    @Select("""
        SELECT * FROM `${tableName}` WHERE <#if accurateWheres??><#list accurateWheres as accurateWhere>${accurateWhere}<#if accurateWhere_has_next> AND </#if></#list></#if>
    """)
    ${dtoName} select(<#if primaryVars??><#list primaryVars as primaryVar>${primaryVar}<#if primaryVar_has_next>, </#if></#list></#if>);

    @ResultMap("${resultMapId}")
    @Select("""
    <script>
<#if dbType == 0 || dbType == 2>
        SELECT * FROM `${tableName}`
<#elseif dbType == 3>
        SELECT * FROM "${tableName}"
</#if>
        <where>
        </where>
        <if test='pager != null'>
            <if test='pager.sorts.size &gt; 0'>
                ORDER BY
                <foreach collection="pager.sorts" item="item" separator=",">${r"${item.field} ${item.order}"}</foreach>
            </if>
<#if dbType == 0 || dbType == 2>
            LIMIT ${r"#{pager.offset}, #{pager.limit}"}
<#elseif dbType == 3>
            LIMIT ${r"#{pager.limit} OFFSET #{pager.offset}"}
</#if>
        </if>
    </script>
    """)
    List<${dtoName}> list(
        @Param("pager") Pager pager);

    @Select("""
    <script>
<#if dbType == 0 || dbType == 2>
        SELECT COUNT(1) FROM `${tableName}`
<#elseif dbType == 3>
        SELECT COUNT(1) FROM "${tableName}"
</#if>
        <where>
        </where>
    </script>
    """)
    long count();

}