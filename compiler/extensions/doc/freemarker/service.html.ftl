<#include "doc_comment.html.ftl">
<#include "linkedtype.html.ftl">
<#include "collaboration_diagram.html.ftl">
<html>
    <head>
        <title>service ${packageName}.${name}</title>
        <link rel="stylesheet" type="text/css" href="webStyles.css">
        <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    </head>

    <body>
        <h2>${packageName}</h2>
        <div class="msgdetail">
<#if docComment.isDeprecated>
            <span class="deprecated">(deprecated) </span>
            <del>
</#if>
                <i>service</i> ${name}
<#if docComment.isDeprecated>
            </del>
</#if>
        </div>
        <p/>
        <@doc_comment docComment/>

        <table>
        <tr><td class="docuCode">
            <table>
                <tr><td>service ${name}</td></tr>
                <tr><td>{</td></tr>
<#list rpcList as rpc>
                <tr><td id="tabIndent">
                    rpc <#if rpc.hasResponseStreaming>stream </#if><@linkedtype rpc.responseType/> <#rt>
                    <a onclick="loadContent('#${rpc.name}') class="fieldLink">${rpc.name}</a> <#t>
                    <#lt><#if rpc.hasRequestStreaming>stream </#if><@linkedtype rpc.requestType/>;
                </td></tr>
</#list>
                <tr><td>};</td></tr>
            </table>
        </td></tr>
        </table>

        <h2>RPC methods</h2>

        <dl>
<#list rpcList as rpc>
            <dt class="memberItem"><a name="${rpc.name}">${rpc.name}:</a></dt>
            <dd class="memberDetail">
            <@doc_comment rpc.docComment/>
            </dd>
</#list>
        </dl>
<#if collaborationDiagramSvgFileName??>

    <@collaboration_diagram collaborationDiagramSvgFileName/>
</#if>
    </body>
</html>
