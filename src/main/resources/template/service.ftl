package ${package};

<#if imports??><#list imports as import>import ${import};
</#list></#if>

public interface ${serviceName} {
    int add(${entityName} dto);

    int delete(<#if primaryVars??><#list primaryVars as primaryVar>${primaryVar}<#if primaryVar_has_next>, </#if></#list></#if>);

    int update(${entityName} dto);

    int alter(<#if primaryVars??><#list primaryVars as primaryVar>${primaryVar}<#if primaryVar_has_next>, </#if></#list></#if>);

    ArticleContentDTO get(<#if primaryVars??><#list primaryVars as primaryVar>${primaryVar}<#if primaryVar_has_next>, </#if></#list></#if>);

    List<${entityName}> list(Pager pager);

<#if primaryKeys??><#if primaryKeys?size == 1><#assign primaryKey = primaryKeys[0]>
    List<${primaryKey[4]}> listIds(Pager pager);

</#if></#if>
    long count();
}