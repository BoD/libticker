<p xmlns="http://www.w3.org/1999/html">
    <head>
        <link rel="stylesheet" href="https://fonts.googleapis.com/icon?family=Material+Icons">
        <link rel="stylesheet" href="https://code.getmdl.io/1.3.0/material.indigo-pink.min.css">
        <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
        <script defer src="https://code.getmdl.io/1.3.0/material.min.js"></script>

        <title>${configuration.appName} ${configuration.appVersion} configuration</title>

        <style>

body {
    padding: 16px;
    font-family: 'Roboto', 'Helvetica', 'Arial', sans-serif !important;
    background-color: #f1f1f1;
    width: auto;
}

table {
    margin: 4px 0 0 0;
    border-collapse: collapse;
    color: rgba(0,0,0,.54);
    width: 100%;
    font-size: 14px;
}

table, td {
    border: 1px solid rgba(0,0,0,.54);
}

td:not(:last-child) {
    white-space: nowrap;
}

td:last-child {
    width: 100%;
}

td {
    padding: 8px
}

.no-border {
    border: 0px none;
    background: #fffde7
}

td:not(:last-child).no-border {
    padding-right: 0;
}

form {
    margin: 0 0 0 0;
}

.card.mdl-card {
    width: 640px;
    min-height: 0;
}

.card > .mdl-card__title > h5 {
  margin: 0;
}

.card > .mdl-card__title > h5 > .mdl-card__subtitle-text {
    margin-top: 4px;
    color: rgba(0,0,0,.33)
}


h3 {
    margin: 0;
    margin-left: 16px;
}

h4 {
    margin-left: 16px;
}

subtitle1 {
    color:
}

        </style>
    </head>
    <body>
    <h3>${configuration.appName} ${configuration.appVersion} configuration</h3>
    <h4>Running plugins</h4>
    <#if managedPlugins?size != 0>
    <#list managedPlugins as plugin>
    <div class="card mdl-card mdl-shadow--2dp">
        <div class="mdl-card__title mdl-card--border">
            <h5>
                <div class="mdl-card__title-text">${plugin.descriptor.displayName}</div>
                <div class="mdl-card__subtitle-text">${plugin.descriptor.className}</div>
            </h5>
        </div>
        <div class="mdl-card__supporting-text">
            <#if plugin.descriptor.configurationDescriptor??>
    Configuration:
            <div style="overflow-x:auto;">
<table>
    <#list plugin.descriptor.configurationDescriptor.itemDescriptors as confItem>
    <tr>
        <td>${confItem.displayName}</td>
        <td>${plugin.configuration.get(confItem.key)}</td>
    </tr>
</#list>
</table>
            </div>
            <#else>
(Not configurable)
        </#if>
    </div>
    <form action="/action" method="post" enctype="application/x-www-form-urlencoded">
    <input type="hidden" name="action" value="unmanage"/>
    <input type="hidden" name="idx" value="${plugin?index}"/>
        <div class="mdl-card__actions mdl-card--border">
            <input type="submit" value="Remove"
                   class="mdl-button mdl-js-button mdl-js-ripple-effect mdl-button--accent"/>
        </div>
</form>
</div>
    <#sep><br/></#sep>
</#list>
<#else>
(No running plugins)
</#if>
<br/>
<hr/>

<h4>Available plugins</h4>
<#list availablePlugins as descriptor>
<div class="card mdl-card mdl-shadow--2dp">
    <div class="mdl-card__title mdl-card--border">
        <h5>
            <div class="mdl-card__title-text">${descriptor.displayName}</div>
            <div class="mdl-card__subtitle-text">${descriptor.className}</div>
        </h5>
    </div>
    <form action="/action" method="post" enctype="application/x-www-form-urlencoded">
        <div class="mdl-card__supporting-text">
            <input type="hidden" name="action" value="manage"/>
            <input type="hidden" name="className" value="${descriptor.className}"/>

            <#if descriptor.configurationDescriptor??>
            Configuration:<br/>
            <#if descriptor.configurationDescriptor.moreInfo??>
            <table class="no-border">
                <tr>
                    <td class="no-border"><i class="material-icons md-dark md-24">info</i></td>
                    <td class="no-border">${descriptor.configurationDescriptor.moreInfo}</td>
                </tr>
            </table>
            </#if>

                <#list descriptor.configurationDescriptor.itemDescriptors as confItem>

                <div class="mdl-textfield mdl-js-textfield mdl-textfield--floating-label">
                    <input class="mdl-textfield__input"
                           type="text"
                    <#if confItem.type == "NUMBER">pattern="-?[0-9]*(\.[0-9]+)?"</#if>
                           name="conf_${confItem.key}"
                           id="conf_${confItem.key}"
                           value="${confItem.defaultValue!}">
                    <label class="mdl-textfield__label" for="conf_${confItem.key}">${confItem.displayName}${confItem.required?string("", " (optional)")}</label>
                    <#if confItem.type == "NUMBER"><span class="mdl-textfield__error">Must be a number</span></#if>
                </div>
                ${confItem.moreInfo!""}
            </#list>

            <#else>
            (Not configurable)
        </#if>
</div>
<div class="mdl-card__actions mdl-card--border">
    <input type="submit" value="Add" class="mdl-button mdl-button--colored mdl-js-button mdl-js-ripple-effect"/>
</div>
    </form>
</div>
<#sep><br/></#sep>
</#list>


</body>
</html>