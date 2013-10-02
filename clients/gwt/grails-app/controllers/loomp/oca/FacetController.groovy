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
package loomp.oca

import org.springframework.web.servlet.support.RequestContextUtils
import loomp.vocabulary.Loomp
import grails.converters.JSON


class FacetController {
	final int PAGE_SIZE = 5
	def serverLoompService
	def resourceService

	def index = {
		return [query: params.query]
	}

	def listAjax = {
		if (!params.max) params.max = PAGE_SIZE
		def entities = serverLoompService.loadAll(Loomp.ElementText, params)
		def total = serverLoompService.count(Loomp.ElementText)
		return [entities: entities, total: total, query: params.query]
	}

	def searchResourcesAjax = {
		if (!params.max) params.max = PAGE_SIZE

		def views = []
		def total = 0
		def annotations = []
		if (params.query) {
			log.debug "Searching for $params.query"
			def resources = serverLoompService.searchResources(params.query, request.auris, false, params)
			total = serverLoompService.searchResourcesCount(params.query, request.auris, false)
			views = resourceService.getResourceViews(resources, RequestContextUtils.getLocale(request).language)
			annotations = serverLoompService.annotationsOfSearchResources(params.query, request.auris)
		}
		return [views: views, total: total, annotations: annotations, query: params.query, auris: request.auris]
	}

	def searchEntitiesAjax = {
		if (!params.max) params.max = PAGE_SIZE

		if (!request.uris && !request.auris) {
			chain action: 'listAjax'
			return
		}

		// selected resources
		def views = []
		// TODO create a method taking a list of URIs to retrieve resources
		request.uris.each { uri ->
			views << resourceService.getResourceView(serverLoompService.load(uri), RequestContextUtils.getLocale(request).language)
		}
		// selected annotations
		def annotations = []
		request.auris.each { annotations << serverLoompService.load(it) }

		def entities = serverLoompService.loadEntitiesByResources(request.uris, request.auris, params)
		def total = serverLoompService.loadEntitiesByResourcesCount(request.uris, request.auris)

		return [entities: entities, total: total, views: views, annotations: annotations, uris: request.uris, auris: request.auris]
	}

	def completeAnnotation = {
		def ling = RequestContextUtils.getLocale(request).language
		def annotations = resourceService.completeAnnotation(params.term?.toLowerCase(), ling)
		def result = []
		annotations.each {
			result << [value: it.labels.getAnyLiteral(ling), uri: it.uri.toString()]
		}
		render text: result as JSON
	}
}
