package ${package};

<#if imports??><#list imports as import>import ${import};
</#list></#if>


<#if springCloud == 1>@RequestMapping("/${beanName}")
@RestController
</#if>@Repository("${beanName}")
public class ${className} implements ${serviceName} {

    <#if springCloud == 1>${r'@Value("${spring.cloud.client.hostname}")
    String ipAddress;
    @Value("${server.port}")
    String port;
    @Value("${spring.application.name}")
    String applicationName;'}

</#if>
    @Resource(name="${mapperBeanName}")
    private ${mapperClassName} ${mapperBeanName};

    @Override
    <#if springCloud == 1>@RequestMapping(value = "/add", method = RequestMethod.GET)
    </#if>public int add(<#if springCloud == 1>@RequestParam </#if>${dtoName} dto) {
        return ${mapperBeanName}.insert(dto);
    }

    @Override
    <#if springCloud == 1>@RequestMapping(value = "/delete", method = RequestMethod.GET)
    </#if>public int delete(<#if primaryVars??><#list primaryVars as primaryVar>${primaryVar[0]} ${primaryVar[1]}<#if primaryVar_has_next>, </#if></#list></#if>) {
        return ${mapperBeanName}.delete(<#if primaryVars??><#list primaryVars as primaryVar>${primaryVar[1]}<#if primaryVar_has_next>, </#if></#list></#if>);
    }

    @Override
    <#if springCloud == 1>@RequestMapping(value = "/update", method = RequestMethod.GET)
    </#if>public int update(<#if springCloud == 1>@RequestParam </#if>${dtoName} dto) {
        return ${mapperBeanName}.update(dto);
    }

    @Override
    <#if springCloud == 1>@RequestMapping(value = "/alter", method = RequestMethod.GET)
    </#if>public int alter(<#if primaryVars??><#list primaryVars as primaryVar><#if springCloud == 1>@RequestParam </#if>${primaryVar[0]} ${primaryVar[1]}<#if primaryVar_has_next>, </#if></#list></#if>) {
        ArgGen args = new ArgGen();
        // Add more parameters here
        return ${mapperBeanName}.alter(<#if primaryVars??><#list primaryVars as primaryVar>${primaryVar[1]}, </#list></#if>args.getArgs());
    }

    @Override
    <#if springCloud == 1>@RequestMapping(value = "/get", method = RequestMethod.GET)
    </#if>public ${dtoName} get(<#if primaryVars??><#list primaryVars as primaryVar><#if springCloud == 1>@RequestParam </#if>${primaryVar[0]} ${primaryVar[1]}<#if primaryVar_has_next>, </#if></#list></#if>) {
        return ${mapperBeanName}.select(<#if primaryVars??><#list primaryVars as primaryVar>${primaryVar[1]}<#if primaryVar_has_next>, </#if></#list></#if>);
    }

    @Override
    <#if springCloud == 1>@RequestMapping(value = "/list", method = RequestMethod.GET)
    </#if>public List<${dtoName}> list(<#if springCloud == 1>@RequestParam </#if>Pager pager) {
        pager.setSorts(${mapperClassName}.ORDERBY);
        ArgGen args = new ArgGen();
        // Add more parameters here
        return ${mapperBeanName}.list(pager, args.getArgs());
    }

<#if primaryKeys??><#if primaryKeys?size == 1><#assign primaryKey = primaryKeys[0]>
    @Override
    <#if springCloud == 1>@RequestMapping(value = "/listIds", method = RequestMethod.GET)
    </#if>public List<${primaryKey[4]}> listIds(<#if springCloud == 1>@RequestParam </#if>Pager pager) {
        pager.setSorts(${mapperClassName}.ORDERBY);
        ArgGen args = new ArgGen();
        // Add more parameters here
        return ${mapperBeanName}.listIds(pager, args.getArgs());
    }

</#if></#if>
    @Override
    <#if springCloud == 1>@RequestMapping(value = "/count", method = RequestMethod.GET)
    </#if>public long count() {
        ArgGen args = new ArgGen();
        // Add more parameters here
        return ${mapperBeanName}.count(args.getArgs());
    }

}