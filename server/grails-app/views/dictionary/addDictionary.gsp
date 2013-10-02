<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
	<title><g:message code="admin.index.heading"/></title>
	<meta name="layout" content="main"/>
	<script
		src="http://ajax.googleapis.com/ajax/libs/dojo/1.5/dojo/dojo.xd.js"
		type="text/javascript"></script>


</head>
<body>

	<div><g:render template="/layouts/messages"/></div>
	<h1><g:message code="annotationEditor.heading"/></h1>



	<h2><g:message code="dictionaryEditor.dictionary.heading"/></h2>

	<g:formRemote name="dictionaryGetForm" url="[controller:'dictionaryEditor',action:'dictionaryGet']" update="[success:'locale']">
			<div class="fields">
				<g:select name="chosenDic" id="chosenDic" from="${dictionaryTitles}"
								noSelection="['New':'New']" onchange="submit()" value="${chosenDic}"/>
			</div>
	</g:formRemote>

	<g:form name="editDictionaryForm" url="[controller:'dictionaryEditor',action:'editDictionary']">
			<div class="fields">
				<label for="titleDic"><g:message code="dictionaryEditor.dictionary.title"/></label>
				<g:textField name="titleDic" value="${titleDic ? titleDic : ''}"/>
				<label for="commentDic"><g:message code="dictionaryEditor.dictionary.comment"/></label>
				<g:textField name="commentDic" value="${commentDic ? commentDic : ''}"/>
				<label for="uriDic"><g:message code="dictionaryEditor.dictionary.uri"/></label>
				<g:textField name="uriDic" value="${uriDic ? uriDic : ''}"/>
			</div>
			<div class="buttons">
				<g:submitButton class="save" name="create" value="${message(code: 'default.button.create.label')}"/>
			</div>
	</g:form>
	<div id="annotationEditArea" >

		<h2><g:message code="annotationEditor.annotation.heading"/></h2>

	</div>
		<table class="insideListRightUp">
			<th class="insideTD">
				<g:message code="annotationEditor.annotation.annotationSetList"/>
				<!--<g:sortableColumn property="titles[0].value" title="Annotations"></g:sortableColumn> -->
			</th>
			<g:each in="${dictionaryTitles}" var="dicInstance">
				<tr>
					<td class="insideTD">
						${dicInstance}
						<!--{fieldValue(bean:anSetInstance,field:titles)[0].value}-->
					</td>
				</tr>
			</g:each>

		</table>
</body>
</html>