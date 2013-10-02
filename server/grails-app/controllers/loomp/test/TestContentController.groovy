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
package loomp.test

import loomp.model.command.PersonCommand
import loomp.model.Person
import loomp.model.command.ElementTextCommand
import loomp.model.ElementText
import loomp.LoompService
import loomp.utils.JenaUtils

class TestContentController {
	def loompService

	def index = { }

	def create = {
		def elements = ElementText.loadAll()
		def pcmd = new PersonCommand()
		def ecmd = new ElementTextCommand()
		if (params.euri) {
			def element = ElementText.load(params.euri)
			if (element) {
				ecmd.uri = element.uri
				ecmd.title = element.title
				ecmd.content = element.content
				ecmd.dateCreated = element.dateCreated
			}
		}
		if (params.puri) {
			def person = Person.load(params.puri)
			if (person) {
				ecmd.uri = person.uri
				ecmd.title = person.title
				ecmd.content = person.content
				ecmd.dateCreated = person.dateCreated
			}
		}
		[elements: elements, person: pcmd, element: ecmd]
	}

	def createUser = {
		PersonCommand cmd ->
		if (cmd.hasErrors()) {
			render view: 'create', model: [person: cmd]
		} else {
			def person = new Person(cmd.properties.subMap(['email', 'firstName', 'lastName', 'lastModified', 'dateCreated']))
			person = person.save()
			log.info "Created person $person.uri"
			flash.message = message(code: 'default.created.message', args:["${message(code: 'person.label')} $person.uri"])
			redirect action: 'create'
		}
	}

	def showElementText = {
		if (!params.uri) {
			flash.error = message(code: 'default.not.found.message', args: [message(code: 'elementText.label'), params.uri])
		}

		def element = ElementText.load(params.uri)
		def resources = JenaUtils.modelsToResources.call(loompService.getContainedResources(params.uri, null, null, true))
		return [element: element, resources: resources]
	}

	def createElementText = {
		ElementTextCommand cmd ->
		if (cmd.hasErrors()) {
			render view: 'create', model: [element: cmd]
		} else {
			def element = null
			if (cmd.uri) {
				element = ElementText.load(cmd.uri)
			}
			if (!element) {
				element = new ElementText()
			}
			element.title = cmd.title
			element.content = cmd.content

			element = element.save()
			log.info "Created text element $element.uri"
			flash.message = message(code: 'default.created.message', args:["${message(code: 'elementText.label')} $element.uri"])
			redirect action: 'create'
		}
	}

	def deleteElementText = {
		if (!params.euri) {
			chain action: 'create'
		}

		def element = ElementText.load(params.euri)
		if (element) {
			element.delete()
		}
		chain action: 'create'
	}
}
