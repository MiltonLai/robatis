package ${package};

<#if imports??><#list imports as import>import ${import};
</#list></#if>

@Repository("${beanName}")
public class ${className} implements ${serviceName} {

    @Resource(name="${mapperBeanName}")
    private ${mapperClassName} ${mapperBeanName};

    @Override
    public int add(${entityName} dto) {
        return ${mapperBeanName}.insert(dto);
    }

    @Override
    public int delete(<#if primaryVars??><#list primaryVars as primaryVar>${primaryVar[0]} ${primaryVar[1]}<#if primaryVar_has_next>, </#if></#list></#if>) {
        return ${mapperBeanName}.delete(<#if primaryVars??><#list primaryVars as primaryVar>${primaryVar[1]}<#if primaryVar_has_next>, </#if></#list></#if>);
    }

    @Override
    public int update(${entityName} dto) {
        return ${mapperBeanName}.update(dto);
    }

    @Override
    public int alter(<#if primaryVars??><#list primaryVars as primaryVar>${primaryVar[0]} ${primaryVar[1]}<#if primaryVar_has_next>, </#if></#list></#if>) {
        ArgGen args = new ArgGen();
        // Add more parameters here
        return ${mapperBeanName}.alter(<#if primaryVars??><#list primaryVars as primaryVar>${primaryVar[1]}, </#list></#if>, args.getArgs());
    }

    @Override
    public ${entityName} get(<#if primaryVars??><#list primaryVars as primaryVar>${primaryVar[0]} ${primaryVar[1]}<#if primaryVar_has_next>, </#if></#list></#if>) {
        return ${mapperBeanName}.select(<#if primaryVars??><#list primaryVars as primaryVar>${primaryVar[1]}<#if primaryVar_has_next>, </#if></#list></#if>);
    }

    @Override
    public List<${entityName}> list(Pager pager) {
        pager.setSorts(${mapperClassName}.ORDERBY);
        ArgGen args = new ArgGen();
        // Add more parameters here
        return ${mapperBeanName}.list(pager, args.getArgs());
    }
<#if primaryKeys??><#if primaryKeys?size == 1><#assign primaryKey = primaryKeys[0]>
    @Override
    public List<${primaryKey[4]}> listIds(Pager pager) {
        pager.setSorts(${mapperClassName}.ORDERBY);
        ArgGen args = new ArgGen();
        // Add more parameters here
        return ${mapperBeanName}.listIds(pager, args.getArgs());
    }

</#if></#if>
    @Override
    public long count() {
        ArgGen args = new ArgGen();
        // Add more parameters here
        return ${mapperBeanName}.count(args.getArgs());
    }

}