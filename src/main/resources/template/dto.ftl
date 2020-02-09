package ${package};

<#if imports??><#list imports as import>import ${import};
</#list></#if>

public class ${className} implements Serializable {
<#if fields??><#list fields as field>    /** ${comments[field_index]} */
    ${field};
</#list></#if>

    <#if withInitializer>public ${className} initialize() {
<#if inits??><#list inits as init>        ${init};
</#list></#if>
        return this;
    }</#if>

<#if methods??><#list methods as method>    ${method}
</#list></#if>
}