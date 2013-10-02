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

	<a name="${message(code: 'view.ars.info.anchor1')}"><h2><g:message code="view.ars.info.openCalais.headline"/></h2></a>
	<p>
		<g:message code="view.ars.info.openCalais.p.part1"/>
		<a href="http://opencalais.com/">
			<img src="${message(code: 'img.openCalais.logo.small')}" alt="${message(code: 'img.openCalais.logo.alt')}" title="${message(code: 'img.openCalais.logo.alt')}"/>
			<g:message code="view.ars.info.openCalais.p.link.text"/>
		</a>
		<g:message code="view.ars.info.openCalais.p.part2"/>
	</p>
	<div style="clear:both;"/>

	<a name="${message(code: 'view.ars.info.anchor2')}"><h2><g:message code="view.ars.info.zemanta.headline"/></h2></a>
	<p>
		<g:message code="view.ars.info.zemanta.p.part1"/>
		<a href="http://www.zemanta.com/" target="_blank">
			<img src="${message(code: 'img.zemanta.logo.small')}" alt="${message(code: 'img.zemanta.logo.alt')}" title="${message(code: 'img.zemanta.logo.alt')}"/>
		</a>
		<g:message code="view.ars.info.zemanta.p.part2"/>
	</p>

	<h2><g:message code="view.ars.info.parameter.headline"/></h2>
	<p><g:message code="view._general.webService.ars.url" args="[new ApplicationTagLib().makeServerURL()]"/></p>
	<table rules="all">
		<colgroup>
			<col width="200"/>
			<col width="200"/>
			<col width="*"/>
		</colgroup>
		<tr>
			<th><g:message code="view.ars.info.parameter.th.paramName"/></th>
			<th><g:message code="view.ars.info.parameter.th.values"/></th>
			<th><g:message code="view.ars.info.parameter.th.description"/></th>
		</tr>
		<tr>
			<td>annotators</td>
			<td>
				<ul>
					<li>openCalais</li>
					<li>zemanta</li>
				</ul>
			</td>
			<td>
				<p><g:message code="view.ars.info.parameter.annotators.description.p1"/></p>
				<p><g:message code="view.ars.info.parameter.annotators.description.p2"/></p>
				<p><g:message code="view.ars.info.parameter.annotators.description.p3"/></p>
			</td>
		</tr>
		<tr>
			<td>text</td>
			<td/>
			<td><g:message code="view.ars.info.parameter.text.description"/></td>
		</tr>
		<tr>
			<td colspan="3" class="partHeadline"><g:message code="view.ars.info.parameter.partHeadline.openCalais"/></td>
		</tr>
		<tr>
			<td>openCalais.allowDistribution</td>
			<td>
				<ul>
					<li>true</li>
					<li>false</li>
				</ul>
				<strong><g:message code="view.ars.info.parameter.values.default"/></strong> false
			</td>
			<td><g:message code="view.ars.info.parameter.openCalais.allowDistribution.description"/></td>
		</tr>
		<tr>
			<td>openCalais.allowSearch</td>
			<td>
				<ul>
					<li>true</li>
					<li>false</li>
				</ul>
				<strong><g:message code="view.ars.info.parameter.values.default"/></strong> false
			</td>
			<td><g:message code="view.ars.info.parameter.openCalais.allowSearch.description"/></td>
		</tr>
		<tr>
			<td>openCalais.contentType</td>
			<td>
				<ul>
					<li>text/raw</li>
					<li>text/txt</li>
					<li>text/xml</li>
					<li>text/html</li>
				</ul>
				<strong><g:message code="view.ars.info.parameter.values.default"/></strong> text/raw
			</td>
			<td><g:message code="view.ars.info.parameter.openCalais.contentType.description"/></td>
		</tr>
		<tr>
			<td>openCalais.licenseID</td>
			<td/>
			<td><g:message code="view.ars.info.parameter.openCalais.licenseID.description"/></td>
		</tr>
		<tr>
			<td colspan="3" class="partHeadline"><g:message code="view.ars.info.parameter.partHeadline.zemanta"/></td>
		</tr>
		<tr>
			<td>zemanta.apiKey</td>
			<td/>
			<td><g:message code="view.ars.info.parameter.zemanta.apiKey.description"/></td>
		</tr>
	</table>



</div>
</body>
</html>