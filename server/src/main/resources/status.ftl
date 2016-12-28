<html>
<head>
    <style>
        * { font-weight: normal; font-family: arial; box-sizing: border-box;}
        body { margin: 20px; }
        .hyperties { padding: 15px; border: 3px solid #ccc; border-radius: 5px; margin: 15px 0; background: #FCFCFC; border-color: #8B0000; }
        .hyperties h2 { margin-top: 0; padding-bottom: 15px; border-bottom: 1px solid #ddd; }
        .listHyperties { padding: 15px; border: 1px solid #ccc; border-radius: 5px; margin: 15px 0; background: #FCFCFC; }
        .listHyperties h3 { margin-top: 0; padding-bottom: 15px; border-bottom: 1px solid #ddd; }
        .hypertiesInfo {list-style-type: disc;}
        .hypertiesInfo li {display: inline-block;}
        .hypertiesStats:before{content: "\25CF";}
    </style>
</head>
<body>
<h1>Domain Registry Status Page</h1>

<#if Init??>
<#list Init as info>
<h3>Storage type: ${info.storageType}, Users with Hyperties: ${info.numUsers}, Hyperties stored: ${info.numHyperties} </h3>
</#list>
</#if>

<#if Users??>
<#list Users as user>
<div class="hyperties">
    <h2>GUID: ${user.userGuid} UserURL: ${user.userURL} Total Hyperties: ${user.totalHyperties} Active Hyperties: ${user.liveHyperties} Disconnected Hyperties: ${user.deadHyperties} </h2>
    <#list user.listHyperties as hyperty>
    <div class="listHyperties">
      <h3> HypertyID: ${hyperty.hypertyID} </h2>
      <ul class="hypertiesInfo">
        <li class="hypertiesStats"> Descriptor: ${hyperty.descriptor} </li>
        <li class="hypertiesStats"> DataSchemes:  </li>
        <#list hyperty.dataSchemes as dataSchemes>
             <li> ${dataSchemes}<#if dataSchemes_has_next>, </#if> </li>
        </#list>
        <li class="hypertiesStats"> Starting Time: ${hyperty.startingTime} </li>
        <li class="hypertiesStats"> Last Modified: ${hyperty.lastModified} </li>
        <li class="hypertiesStats"> Status: ${hyperty.status} </li>
        <li class="hypertiesStats"> Expires: ${hyperty.expires} </li>
        <li class="hypertiesStats"> Resources:  </li>
        <#list hyperty.resources as resource>
             <li> ${resource}<#if resource_has_next>, </#if> </li>
        </#list>
        <li class="hypertiesStats"> Peer-To-Peer Requester: ${hyperty.requester} </li>
        <li class="hypertiesStats"> Peer-To-Peer Handler: ${hyperty.handler} </li>
        <li class="hypertiesStats"> Runtime: ${hyperty.runtime} </li>
    </ul>
    </div>
    </#list>
</div>
</#list>
</#if>

</body>
</html>
