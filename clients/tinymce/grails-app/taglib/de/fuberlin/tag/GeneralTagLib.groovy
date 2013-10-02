/*******************************************************************************
 * This file is part of the Coporate Semantic Web Project.
 * 
 * This work has been partially supported by the ``InnoProfile-Corporate Semantic Web" project funded by the German Federal 
 * Ministry of Education and Research (BMBF) and the BMBF Innovation Initiative for the New German Laender - Entrepreneurial Regions.
 * 
 * http://www.corporate-semantic-web.de/
 * 
 * 
 * Freie Universitaet Berlin
 * Copyright (c) 2007-2013
 * 
 * 
 * Institut fuer Informatik
 * Working Group Coporate Semantic Web
 * Koenigin-Luise-Strasse 24-26
 * 14195 Berlin
 * 
 * http://www.mi.fu-berlin.de/en/inf/groups/ag-csw/
 * 
 *  
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published 
 * by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, 
 * Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA or see <http://www.gnu.org/licenses/>
 ******************************************************************************/
package de.fuberlin.tag

import grails.util.GrailsUtil
import org.apache.commons.lang.StringUtils

/**
 * Tags to ease the pain of output.
 */
class GeneralTagLib {
	static namespace = 'gen'

	def groovyPagesTemplateEngine

	/**
	 * Render the title using either a message code or a string value.
	 * @param code
	 * 		code taken from the message bundle
	 * @param args
	 * 		arguments for the message
	 * @param value
	 * 		a string value to be rendered as it is
	 */
	def title = {attrs ->
		if (!attrs?.code && !attrs?.value)
			throwTagError("Tag [title] is missing one of its required attributes [code] or  [value]")
		def title
		if (attrs?.value) {
			title = attrs.value
		} else {
			title = attrs?.args ? g.message(code: attrs.code, args: attrs.args) : g.message(code: attrs.code)
		}
		out << "${g.message(code: 'project.title')} :: $title"
	}

	/**
	 * Only render a template if the resource exists.
	 */
	def renderIfExists = {attrs, body ->
		def engine = groovyPagesTemplateEngine
		def uri = grailsAttributes.getTemplateUri(attrs.template, request)
		def contextPath = attrs.contextPath ? attrs.contextPath : ""
		def res_1 = engine.getResourceForUri("${contextPath}${uri}")
		def res_2 = engine.getResourceForUri("${contextPath}/grails-app/views/${uri}")

		if (res_1.exists() || res_2.exists()) {
			out << g.render(
					template: attrs.template,
					bean: attrs?.bean,
					model: attrs?.model,
					collection: attrs?.collection,
					var: attrs?.var,
					plugin: attrs?.plugin,
					body: body)
		}
	}

	/**
	 * Renders a given comment. If the comment is empty or null then default.no_comment.label
	 * is rendered instead.
	 */
	def noComment = {attrs ->
		out << ((GrailsUtil.isBlank(attrs?.comment)) ? message(code: "default.no_comment.label") : attrs.comment)
	}

	/**
	 * Outputs "active" if the current controller name equals the given name.
	 */
	def active = {attrs ->
		if (!attrs?.name)
			throwTagError("Tag [active] is missing required attribute [name]")
		out << (controllerName == attrs.name ? "active" : "")
	}

	/**
	 * Render a list of concepts grouped by language. The concepts are grouped by the first label
	 * @param from
	 * 		a set of concepts
	 */
	// TODO consider the case when a concept does not contain any label
	def groupedConcepts = { attrs ->
		if (!attrs?.from)
			throwTagError("Tag [groupedConcepts] is missing required attribute [from]")

		if (attrs.from.isEmpty())
			return

		def withLink = attrs?.action 
		def controller = attrs?.controller ? attrs?.controller : controllerName
		def id = attrs?.id ? attrs.id : ''
		def params = attrs?.params ? attrs?.params : [:]

		// sort so that we get a result sorted by language message
		from = from.sort{ g.message(code: 'language.${it.labels[0].lang}.label') }
		def groupedConcepts = attrs.from.groupBy{ it.labels[0].lang }
		out << "<ul>"
		// TODO output the browser language first
		groupedConcepts.each { lang, concepts ->
			if (concepts) {
				out << '<li class="group">' + message(code: "language.${lang}.label") + '</li>'
				concepts.each {
					out << "<li>"
					if (withLink) {
						params['url'] = it.resource.stringValue()
						out << "<a href=\"${g.createLink(controller: controller, action: attrs.action, params: params, id: id)}\">"
					}
					// since we expect
					out << it.labels[0]
					if (withLink) {
						out << "</a>"
					}
					out << "</li>"
				}
			}
		}
		out << "</ul>"
	}

	/**
	 * Outputs the code for an in-place-editor.
	 * @param controller
	 * @param action
	 * @param id
	 * 		ID of the database object
	 * @param cssId
	 * 		ID of the replaced HTML object
	 * @param params
	 * 		params passed to the called action
	 * @param args
	 * 		addtional arguments for the Ajax script
	 */
	def inPlaceEditor = {attrs ->
		if (!attrs?.cssId)
			throwTagError("Tag [formatAjaxSelect] is missing required attribute [cssId]")
		if (!attrs?.id)
			throwTagError("Tag [inPlaceEditor] is missing required attribute [id]")
		final def DFLT_EDITOR_OPTIONS = [
				cancelControl: "'button'",
				cancelText: "'${g.message(code: 'default.button.cancel.label')}'",
				clickToEditText: "'${g.message(code: 'default.button.click_to_edit.label')}'",
				savingText: "'${g.message(code: 'default.saving.message')}'"
		]

		def controller = attrs?.controller ? attrs.controller : controllerName
		def action = attrs?.action ? attrs.action : actionName
		def params = attrs?.params ? attrs.params : []
		def argElem = []
		(DFLT_EDITOR_OPTIONS + (attrs?.args ? attrs.args : [])).each {k, v ->
			argElem << "$k:$v"
		}
		def args = StringUtils.join(argElem, ',')

		out << '<script type="text/javascript">\n'
		out << '// <![CDATA[\n'
		out << "new Ajax.InPlaceEditor('$attrs.cssId'," +
				"'${createLink(controller: controller, action: action, id: attrs.id, params: params)}'," +
				"{$args});\n"
		out << '// ]]>\n'
		out << '</script>\n'
	}

	/**
	 * Outputs the code for an in-place-editor.
	 * @param controller
	 * @param action
	 * @param id
	 * 		ID of the database object
	 * @param from
	 * @param optionKey
	 * @param optionValue
	 * @param cssId
	 * 		ID of the replaced HTML object
	 * @param args
	 * 		addtional arguments for the Ajax script
	 */
	def inPlaceSelector = {attrs ->
		// TODO check the presence of mandatory attributes
		if (!attrs?.cssId)
			throwTagError("Tag [formatAjaxSelect] is missing required attribute [cssId]")
		if (!attrs?.from)
			throwTagError("Tag [formatAjaxSelect] is missing required attribute [from]")
		if (!attrs?.optionKey)
			throwTagError("Tag [formatAjaxSelect] is missing required attribute [key]")

		final def DFLT_EDITOR_OPTIONS = [
				cancelControl: "'button'",
				cancelText: "'${g.message(code: 'default.button.cancel.label')}'",
				clickToEditText: "'${g.message(code: 'default.button.click_to_edit.label')}'",
				savingText: "'${g.message(code: 'default.saving.message')}'"
		]

		def controller = attrs?.controller ? attrs.controller : controllerName
		def action = attrs?.action ? attrs.action : actionName
		def collection = formatAjaxSelect(from: attrs.from, optionKey: attrs.optionKey, optionValue: attrs.optionValue)
		def elements = []
		(DFLT_EDITOR_OPTIONS + (attrs?.args ? attrs.args : [])).each {k, v ->
			elements << "$k:$v"
		}
		def args = StringUtils.join(elements, ',')

		out << '<script type="text/javascript">\n'
		out << '// <![CDATA[\n'
		out << "new Ajax.InPlaceCollectionEditor('$attrs.cssId'," +
				"'${createLink(controller: attrs.controller, action: attrs.action)}'," +
				"{collection:$collection, $args});\n"
		out << '// ]]>\n'
		out << '</script>\n'
	}


	/**
	 * Format a collection of objects so that it can be used for the argument collection in a inPlaceSelector.
	 * @param from
	 * 		a collection of objects
	 * @param optionKey
	 * 		field of the objects to be used as key
	 * @param optionValue
	 * 		field of the objects to be used as value; if value is not set, the key is used as value
	 */
	def formatAjaxSelect = {attrs ->
		if (!attrs.from)
			throwTagError("Tag [formatAjaxSelect] is missing required attribute [collection]")
		if (!attrs.optionKey)
			throwTagError("Tag [formatAjaxSelect] is missing required attribute [key]")

		def optionKey = attrs.optionKey
		def optionValue = attrs?.optionValue ? attrs.optionValue : attrs.optionKey

		def elements = []
		attrs.from.each {
			elements << "['${it[optionKey]}', '${it[optionValue]}']"
		}

		out << "[${StringUtils.join(elements, ',')}]"
	}
}
