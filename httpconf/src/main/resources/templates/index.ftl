<p xmlns="http://www.w3.org/1999/html">
    <head>
        <style>
table {
  border-collapse: collapse;
}

table, td {
    border: 1px solid;
}

td {
    padding: 5pt;
}

.card {
    background: #EEE;
}

        </style>
    </head>
    <body>
    <h1>${configuration.appName} ${configuration.appVersion} configuration page</h1>
    <h2>Currently running plugins</h2>
    <#list managedPlugins as plugin>
    <div class="card">
        <h3>${plugin.descriptor.displayName}</h3>
<p>Id: <b>${plugin.descriptor.className}</b></p>
<p>
    <#if plugin.descriptor.configurationDescriptor??>
    Configuration:
<table>
    <#list plugin.descriptor.configurationDescriptor.configurationItemDescriptors as confItem>
    <tr>
        <td>${confItem.displayName}</td>
        <td>${plugin.configuration.get(confItem.key)}</td>
    </tr>
</#list>
</table>
<#else>
(Not configurable)
</#if>
</p>
<form action="/action" method="post" enctype="application/x-www-form-urlencoded">
    <input type="hidden" name="action" value="unmanage"/>
    <input type="hidden" name="idx" value="${plugin?index}"/>
    <input type="submit" value="Remove"/>
</form>
</div>
<br/>
</#list>

<hr/>

<h2>Available plugins</h2>
<#list availablePlugins as descriptor>
<div class="card">
    <h3>${descriptor.displayName}</h3>
    <p>Id: <b>${descriptor.className}</b></p>
    <p>
        <#if descriptor.configurationDescriptor??>
        Configuration:
    <table>
        <#list descriptor.configurationDescriptor.configurationItemDescriptors as confItem>
        <tr>
            <td>${confItem.displayName}</td>
            <td>${confItem.type}</td>
            <td>${confItem.required?string("Required", "Optional")}</td>
            <td>${confItem.defaultValue!""}</td>
            <td>${confItem.moreInfo!""}</td>
        </tr>
    </
    #list>
    </table>
    <#else>
    (Not configurable)
</#if>
</p>
</div>
<br/>
</#list>


</body>
</html>