package ${package};

<#if imports??><#list imports as import>import ${import};
</#list></#if>


<#if springCloud == 1>@FeignClient(value = "commons")
@RequestMapping(value = "/${beanName}")
</#if>public interface ${serviceName} {
    <#if springCloud == 1>@RequestMapping(value = "/add", method = RequestMethod.GET)
    </#if>int add(<#if springCloud == 1>@RequestParam(value = "dto") </#if>${dtoName} dto);

    <#if springCloud == 1>@RequestMapping(value = "/delete", method = RequestMethod.GET)
    </#if>int delete(<#if primaryVars??><#list primaryVars as primaryVar>${primaryVar}<#if primaryVar_has_next>, </#if></#list></#if>);

    <#if springCloud == 1>@RequestMapping(value = "/update", method = RequestMethod.GET)
    </#if>int update(<#if springCloud == 1>@RequestParam(value = "dto") </#if>${dtoName} dto);

    <#if springCloud == 1>@RequestMapping(value = "/alter", method = RequestMethod.GET)
    </#if>int alter(<#if primaryVars??><#list primaryVars as primaryVar>${primaryVar}<#if primaryVar_has_next>, </#if></#list></#if>);

    <#if springCloud == 1>@RequestMapping(value = "/get", method = RequestMethod.GET)
    </#if>${dtoName} get(<#if primaryVars??><#list primaryVars as primaryVar>${primaryVar}<#if primaryVar_has_next>, </#if></#list></#if>);

    <#if springCloud == 1>@RequestMapping(value = "/list", method = RequestMethod.GET)
    </#if>List<${dtoName}> list(<#if springCloud == 1>@RequestParam(value = "pager") </#if>Pager pager);

    <#if primaryKeys??><#if primaryKeys?size == 1><#assign primaryKey = primaryKeys[0]>
    <#if springCloud == 1>@RequestMapping(value = "/listIds", method = RequestMethod.GET)
    </#if>List<${primaryKey[4]}> listIds(<#if springCloud == 1>@RequestParam(value = "pager") </#if>Pager pager);

</#if></#if>
    <#if springCloud == 1>@RequestMapping(value = "/count", method = RequestMethod.GET)
    </#if>long count();
}