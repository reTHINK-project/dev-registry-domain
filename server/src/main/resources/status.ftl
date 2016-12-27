<html>
<head>
    <style>
        * { font-weight: normal; font-family: arial; box-sizing: border-box;}
        body { margin: 20px; }
        .hyperties { padding: 15px; border: 1px solid #ccc; border-radius: 5px; margin: 15px 0; background: #FCFCFC; }
        .hyperties h2 { margin-top: 0; padding-bottom: 15px; border-bottom: 1px solid #ddd; }

        /*ul { margin-top: 0; padding: 0; }
        .post h3 { font-size: 16px; margin-bottom: 5px; }
        .categories li { display: inline-block; }
        .categories li:after { content: ","; }
        .categories li:last-child:after { content: ""; }*/
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
    <h2>GUID: ${user.userGuid}, UserURL: ${user.userURL}</h2>
    <!-- Falta ver quantas hyperties o user tem, se tiver tera uma lista com HypertyInstance?! e depois é só percorrer aqui e tirar as infos uteis -->

</div>
</#list>
</#if>

</body>
</html>
