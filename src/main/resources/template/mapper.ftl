package ${package};

<#if imports??><#list imports as import>import ${import};
</#list></#if>

@Repository("${beanName}")
public interface ${className} {
    String[] ORDERBY = {<#if orderbys??><#list orderbys as orderby>"${orderby}"<#if orderby_has_next>, </#if></#list></#if>};

    int insert(${dtoName} dto);

    int delete(<#if primaryVars??><#list primaryVars as primaryVar>${primaryVar}<#if primaryVar_has_next>, </#if></#list></#if>);

    int update(${dtoName} dto);

    int alter(<#if primaryVars??><#list primaryVars as primaryVar>${primaryVar}, </#list></#if>@Param("param") Map<String, Object> args);

    ${dtoName} select(<#if primaryVars??><#list primaryVars as primaryVar>${primaryVar}<#if primaryVar_has_next>, </#if></#list></#if>);

    List<${dtoName}> list(
        @Param("pager") Pager pager,
        @Param("param") Map<String, Object> args);
<#if primaryKeys??><#if primaryKeys?size == 1><#assign primaryKey = primaryKeys[0]>
    List<${primaryKey[4]}> listIds(
        @Param("pager") Pager pager,
        @Param("param") Map<String, Object> args);
</#if></#if>
    long count(@Param("param") Map<String, Object> args);

}