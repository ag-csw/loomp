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
package loomp

import org.springframework.web.servlet.support.RequestContextUtils
import grails.converters.JSON
import sun.org.mozilla.javascript.internal.continuations.Continuation

class StdFilters {
	final static g = new org.codehaus.groovy.grails.plugins.web.taglib.ApplicationTagLib()

	def serverLoompService

	def filters = {
		/**
		 * Put ctxtLang (active language of the browser) into request.
		 */
		languages(controller: '*', action: '*') {
			before = {
				request.ctxtLang = RequestContextUtils.getLocale(request).language

				if (params.uri) {
					try {
						request.uri = URI.create(URLDecoder.decode(params.uri, "utf-8"))
					} catch (IllegalArgumentException e) {
						// NullPointerException cannot occur
						log.error "Invalid URI $params.uri"
						// TODO render error
						return false
					}
					log.info "Requested URI: $request.uri"
				}

				// creating URI lists
				["uris", "auris"].each { key ->
					// if we submit a query using jQuery then jQuery automatically adds [] at the end of the parameter name.
					if (params[key] || params["$key[]"]) {
						if (!params[key]) {
							params[key] = params["$key[]"]
							params.remove("$key[]")
						}

						if (params[key] instanceof String) {
							if (params[key] == 'null' || params[key] == '[]') {
								params[key] = []
							} else if (params[key].charAt(0) == '[') {
								// the plugin providing remote pagination submits an array as json which is not recognized by grails
								String json = params[key].replace("[", "[\"").replaceAll(",", "\",\"").replace("]", "\"]")
								params[key] = JSON.parse(json)
							}
						}
						def uris = (params[key] instanceof String ? [params[key]] : params[key])
						request[key] = []
						uris.each { uri ->
							try {
								request[key] << URI.create(URLDecoder.decode(uri, "utf-8"))
							} catch (IllegalArgumentException e) {
								// NullPointerException cannot occur
								log.error "Invalid URI $uri"
								// TODO render error
								return false
							}
						}
						log.info "Requested URIs: ${request[key]}"
					}
				}

				if (params.prop) {
					def propUris = (params.prop instanceof String ? [params.prop] : params.prop)
					request.propUris = []
					propUris.each { uri ->
						try {
							request.propUris << URI.create(URLDecoder.decode(uri, "utf-8"))
						} catch (IllegalArgumentException e) {
							// NullPointerException cannot occur
							log.error "Invalid URI $uri"
							// TODO render error
							return false
						}
					}
					log.info "Requested URIs: $request.propUris"
				}
				return true
			}
		}

		/** Check that a URI is given and load the entity.      */
		load(controller: 'document', action: '(latestDeleteAjax|annotatedTermsAjax|show)') {
			before = {
				if (!params.uri) {
					flash.message = g.message('default.missing.parameter.message', args: [g.message(code: 'elementText.uri.label')])
					redirect controller: (actionName == 'latestDeleteAjax' ? 'home' : 'document')
					return false
				}

				def entity = serverLoompService.load(params.uri)
				if (!entity) {
					flash.message = g.message('default.not.found.message', args: [params.uri])
					redirect controller: (actionName == 'latestDeleteAjax' ? 'home' : 'document')
					return false
				}
				request.entity = entity

				return true
			}
		}
	}
}
