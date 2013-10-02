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

import loomp.vocabulary.Loomp
//import loomp.vocabulary.RDF
import loomp.model.Resource
import loomp.model.Annotation
import org.springframework.web.servlet.support.RequestContextUtils

class DocumentController {
	final LATEST_NUM = 5
	def serverLoompService
	def resourceService

	def index = {
		return [uri: params.uri]
	}

	def show = {
		return [entity: serverLoompService.load(request.entity.uri)]
	}

	def latestAjax = {
		return [latest: serverLoompService.latest(Loomp.elementText, LATEST_NUM)]
	}

	def latestDeleteAjax = {
		def entity = request.entity
		if (serverLoompService.delete(entity.uri)) {
			flash.ajax_msg = message(code: 'default.deleted.message', args: [message(code: 'elementText.label'), entity.title])
		} else {
			flash.ajax_err = message(code: 'default.not.deleted.message', args: [message(code: 'elementText.label'), entity.title])
		}
		chain action: 'latestAjax'
	}

	def annotatedTermsAjax = {
		if (!params.only) params.only = true
		def resources = serverLoompService.containedResources(request.entity.uri, params)
		def views = resourceService.getResourceViews(resources, RequestContextUtils.getLocale(request).language)
		return [views: views, entity: request.entity]
	}
}
