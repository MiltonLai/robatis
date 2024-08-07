package ${package};

<#if imports??><#list imports as import>import ${import};
</#list></#if>

@Mapper
public interface ${className} {
    @Insert("""
        INSERT INTO `${tableName}` (
<#if insertFields??><#list insertFields as item>            `${item}`<#if item_has_next>,</#if>
</#list></#if>
        ) VALUES (
<#list inserts as insert>            ${insert}<#if insert_has_next>,</#if>
</#list>        )
    """)
    int insert(${dtoName} dto);

    @Delete("DELETE FROM `${tableName}` WHERE <#if accurateWheres??><#list accurateWheres as accurateWhere>${accurateWhere}<#if accurateWhere_has_next> AND </#if></#list></#if>")
    int delete(<#if primaryVars??><#list primaryVars as primaryVar>${primaryVar}<#if primaryVar_has_next>, </#if></#list></#if>);

    @Update("""
        UPDATE `${tableName}` SET
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
        SELECT * FROM `${tableName}`
        <where>
        </where>
        <if test='pager != null'>
            <if test='pager.sorts.size &gt; 0'>
                ORDER BY
                <foreach collection="pager.sorts" item="item" separator=",">${r"${item.field} ${item.order}"}</foreach>
            </if>
            LIMIT ${r"#{pager.offset}, #{pager.limit}"}
        </if>
    </script>
    """)
    List<${dtoName}> list(
        @Param("pager") Pager pager);

    @Select("""
    <script>
        SELECT COUNT(1) FROM `${tableName}`
        <where>
        </where>
    </script>
    """)
    long count();

}