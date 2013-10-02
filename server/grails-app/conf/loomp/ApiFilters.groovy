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

import loomp.RenderFormat

class ApiFilters {
	def loompService

	final static SUPPORTED_FORMATS = RenderFormat.values() as Set

	static filters = {
		/**
		 * Parameters
		 * 		fmt: format of the result, Default: RenderFormat.JSON
		 */
		processFormat(controller: "*", action: "*") {
			before = {
				// validate a supported format and put it into request
				request.fmt = RenderFormat.JSON
				if (params?.fmt) {
					try {
						request.fmt = RenderFormat.valueOf(params.fmt.toUpperCase())
						if (!SUPPORTED_FORMATS.contains(request.fmt)) {
							log.error "Unsupported format $request.fmt"
							render(status: HttpURLConnection.HTTP_BAD_REQUEST, text: "Unsupported format $params.fmt requested. Supported values are $SUPPORTED_FORMATS")
							return false
						}
					} catch (IllegalArgumentException e) {
						// NullPointerException cannot occur
						log.error "Unsupported format $params.fmt"
						render(status: HttpURLConnection.HTTP_BAD_REQUEST, text: e.getMessage())
						return false
					}
				}
				return true
			}
		}

		processQuery(controller: '(content|annotation)', action: '(search*|*Search)') {
			before = {
				// validate that a given URI is really a URI and put into request
				if (params?.query) {
					request.query = params.query
				} else {
					log.error "$controllerName#$actionName called without mandatory parameter query"
					render(status: HttpURLConnection.HTTP_BAD_REQUEST, text: "Parameter 'query' is mandatory")
					return false
				}
				return true
			}
		}

		/**
		 * Parameters
		 * 		uri: a URI of a domain entity, e.g., a document, an element, ...
		 * 		type: URI indicating the type of a domain class
		 * 		data: data in JSON format representing an entity to be accessed
		 * 		fmt: format of the result
		 */
		processParams(controller: "(content|annotation)", action: '*') {
			final ACTIONS_WITH_URI = ['get', 'delete', 'type', 'containing', 'containedResources']
			final ACTIONS_WITH_TYPE = ['getAll', 'latest', 'count']

			before = {
				// validate that a given URI is really a URI and put into request
				if (params?.uri) {
					try {
						request.uri = URI.create(URLDecoder.decode(params.uri, "utf-8"))
						log.info "Requested URI: $request.uri"
					} catch (IllegalArgumentException e) {
						// NullPointerException cannot occur
						log.error "Invalid URI $param.uri"
						render(status: HttpURLConnection.HTTP_BAD_REQUEST, text: e.getMessage())
						return false
					}
				} else {
					if (ACTIONS_WITH_URI.contains(actionName)) {
						log.error "$controllerName#$actionName called without mandatory URI"
						render(status: HttpURLConnection.HTTP_BAD_REQUEST, text: "Parameter 'uri' is mandatory")
						return false
					}
				}

				["uris", "auris"].each { key ->
					if (params[key]) {
						def uris = (params[key] instanceof String) ? [params[key]] : params[key]
						request[key] = []
						uris.each { String uri ->
							try {
								request[key] << URI.create(URLDecoder.decode(uri, "utf-8"))
							} catch (IllegalArgumentException e) {
								// NullPointerException cannot occur
								log.error "Invalid URI $uri"
								render(status: HttpURLConnection.HTTP_BAD_REQUEST, text: e.getMessage())
								return false
							}
						}
						log.info "Requested URIs: $request[key]"
					}
				}

				// validate that a given type URI is really a URI, check that we know the type, and put into request
				if (params?.type) {
					try {
						request.typeUri = URI.create(URLDecoder.decode(params.type, "utf-8"))
						log.info "Requested type URI: $request.typeUri"
					} catch (IllegalArgumentException e) {
						// NullPointerException cannot occur
						log.error "Invalid type URI $param.type"
						render(status: HttpURLConnection.HTTP_BAD_REQUEST, text: e.getMessage())
						return false
					}
				} else {
					if (ACTIONS_WITH_TYPE.contains(actionName)) {
						log.error "$controllerName#$actionName called without mandatory type URI"
						render(status: HttpURLConnection.HTTP_BAD_REQUEST, text: "Parameter 'type' is mandatory")
						return false
					}
				}

				if (params?.prop) {
					request.propUris = params.prop instanceof String ? [params.prop.toURI()] : params.prop.collect { it.toURI() }
				}

				return true
			}
		}

		/**
		 * Process parameters that are special for accessing annotation sets and annotations
		 * @param set
		 * 		URI of an annotation set
		 */
		processAnnotationParams(controller: "annotation", action: '*') {
			final ACTIONS_WITH_SET = []
			before = {
				if (params?.set) {
					request.set = params.set
					// TODO Maybe we should ensure that it is a URI of an annotation. But this is another query :-|
				} else {
					if (ACTIONS_WITH_SET.contains(actionName)) {
						log.error "$controllerName#$actionName called without mandatory set"
						render(status: HttpURLConnection.HTTP_BAD_REQUEST, text: "Parameter 'set' is mandatory")
						return false
					}
				}
				return true
			}
		}
	}
}
