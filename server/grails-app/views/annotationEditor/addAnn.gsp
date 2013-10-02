<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
	<title><g:message code="admin.index.heading"/></title>
	<meta name="layout" content="main"/>
</head>
<body>
  <g:form controller="annotationEditor" action="addAnn" class="withText">
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
</body>
</html>