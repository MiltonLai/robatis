//${name}
<#list columns as column>/** ${column.field} ${column.type} ${column.key} ${column.extra} ${column.comments} */
${column.javaType} ${column.javaName}
</#list>