<%@ page import="org.codehaus.groovy.grails.plugins.web.taglib.ApplicationTagLib" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta name="layout" content="main"/>
	<title><g:message code="view.ars.info.title"/></title>
</head>

<body>
<div class="body">
  
  <div><g:render template="/layouts/messages"/></div>


	<h1><g:message code="view.ars.info.headline"/></h1>
	<p><g:message code="view._general.description.p1"/></p>
	<p><g:message code="view._general.description.p2"/></p>   
	<p><g:message code="view.ars.info.p3" args="[
		message(code: 'view.ars.info.anchor1'), message(code: 'view.ars.info.anchor2')
	]"/></p>
	<p><g:message code="view._general.webService.ars.url" args="[new ApplicationTagLib().makeServerURL()]"/></p>
    <br>
   <g:form controller="ars" action="testInput" class="withText">
	<table rules="all">
		<colgroup>
			<col width="200"/>
			<col width="*"/>
		</colgroup>
		<tr>

			<th><g:message code="view.ars.info.parameter.th.paramName"/></th>
			<th><g:message code="view.ars.info.parameter.th.values"/></th>
		</tr>
		<tr>
			<td>annotators</td>
			<td>
              <g:select name="annotators" from="${['all','openCalais', 'zemanta']}"
                noSelection="['':'-Choose anotator-']"/>
			</td>
		</tr>
		<tr>
			<td>text</td>
			<td><g:textArea name="textToAnnotate" value="" rows="5" cols="40"/></td>
		</tr>
		<tr>
			<th colspan="2" ><g:message code="view.ars.info.parameter.partHeadline.openCalais"/></th>
		</tr>
		<tr>
			<td>openCalais.allowDistribution</td>
			<td>
                <g:select name="openCalais.allowDistribution" from="${['true', 'false']}" value="false"/>
				<strong><g:message code="view.ars.info.parameter.values.default"/></strong> false
			</td>
		</tr>
		<tr>
			<td>openCalais.allowSearch</td>
			<td>
                <g:select name="openCalais.allowSearch" from="${['true', 'false']}" value="false"/>
				<strong><g:message code="view.ars.info.parameter.values.default"/></strong> false
			</td>
		</tr>
		<tr>
			<td>openCalais.contentType</td>
			<td>
               <g:select name="openCalais.contentType" from="${['text/raw', 'text/txt','text/xml','text/html']}" value="text/raw"/>

				<strong><g:message code="view.ars.info.parameter.values.default"/></strong> text/raw
			</td>
		</tr>
		<tr>
			<td>openCalais.licenseID</td>
			<td><g:textField name="openCalais.licenseID" value="" /></td>
		</tr>
		<tr>
			<th colspan="2" ><g:message code="view.ars.info.parameter.partHeadline.zemanta"/></th>
		</tr>
		<tr>
			<td>zemanta.apiKey</td>
			<td><g:textField name="zemanta.apiKey" value="" /></td>
		</tr>
	</table>


	<div class="buttons">
		<g:submitButton class="list" name="list" value="${message(code: 'default.button.create.label')}"/>
	</div>
   </g:form>
  </div>
</body>
</html>