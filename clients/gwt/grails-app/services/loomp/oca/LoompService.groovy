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

import grails.util.GrailsNameUtils
import loomp.vocabulary.Loomp
import loomp.utils.JsonUtils

/**
 * Service for retrieving data from a loomp server. All method return JSON.
 */
class LoompService {
	static transactional = false

	def httpService
	def resourceService

	/**
	 * Retrieve the Version of the loomp API.
	 *
	 * @return version as JSON
	 */
	def String loompApiVersion() {
		def json = httpService.doGet("meta", "version", null);
		return json ? json.getString("version") : "0.0"
	}

	/**
	 * Count the entities of a given type.
	 *
	 * @param typeUri
	 * 		type URI of the entities to be counted
	 * @return list of instances as JSON
	 */
	def int count(typeUri) {
		if (!typeUri)
			throw new NullPointerException("Type URI is null")

		def params = [type:typeUri]
		log.debug "Counting entities of type $typeUri"
		def json = httpService.doGet("content", "count", params)
		return json ? json.getInt("count") : 0
	}

	/**
	 * Load all entities of a given type.
	 *
	 * @param typeUri
	 * 		type URI of the entities to be loaded
	 * @return list of instances as JSON
	 */
	def String loadAll(typeUri, params = null) {
		if (!typeUri)
			throw new NullPointerException("Type URI is null")

		if (!params) params = [:]
		params.type = typeUri

		log.debug "Retrieving all entities of type $typeUri"
		def json = httpService.doGet("content", "getAll", params)
		return json ?: "[]"
	}

	/**
	 * Load latest modified entities of a given type.
	 *
	 * @param uri
	 * 		URI of the entity to be loaded
	 * @param max
	 * 		number of returned entities
	 * @return list of instances as JSON
	 */
	def String latest(typeUri, max) {
		if (!typeUri)
			throw new NullPointerException("Type URI is null")
		def params = [type: typeUri, sort: "lastModified"]
		if (max) params.max = max

		log.debug "Retrieving latest entities of type $typeUri"
		def json = httpService.doGet("content", "latest", params)
		return json ?: "[]"
	}

	/**
	 * Load the resource contained in a given element.
	 *
	 * @param uri
	 * 		URI of the entity to be loaded
	 * @param max
	 * 		number of returned entities
	 * @return list of instances as JSON
	 */
	def String containedResources(uri, params) {
		if (!uri)
			throw new NullPointerException("URI is null")
		if (!params) params = [:]
		params.uri = uri
		log.debug "Retrieving contained resources of entity  $uri"
		def json = httpService.doGet("content", "containedResources", params)
		return json ?: "[]"
	}

	/**
	 * Search resources matching (partly) a given query string. The search is restricted
	 * to the property propUri. If propUri is null then all literals are searched.
	 *
	 * @param query
	 * 		a query string
	 * @param auris
	 * 		URIs of annotations
	 * @param inclExtern
	 * 		include external data sources
	 * @param offset (in params)
	 *      where to start
	 * @param max (in params)
	 *      how many
	 * @param sort (in params)
	 * 		URI of the property for sorting the result list (null = uri)
	 * @param sortLocale (in params)
	 * 		locale used for sorting
	 * @return list of resource instances as JSON
	 */
	def String searchResources(String query, auris, boolean inclExtern, params = null) {
		if (GrailsNameUtils.isBlank(query))
			return "[]"
		if (!params) params = [:]
		params.putAll([query: query, external: inclExtern])
		if (auris) params.auris = auris

		log.debug "Executing search for query: $query"
		def json = httpService.doGet("content", "searchResources", params)
		return json ?: "[]"
	}

	def int searchResourcesCount(String query, auris, boolean inclExtern) {
		if (GrailsNameUtils.isBlank(query))
			return 0
		def params = [query: query, external: inclExtern]
		if (auris) params.auris = auris

		log.debug "Getting size of result set for query: $query"
		def json = httpService.doGet("content", "searchResourcesCount", params)
		return json ? json.getInt("count") : 0
	}

	/**
	 * Get the annotations of the resources that would be found by #searchResources(query).
	 *
	 * @param query
	 * 		a query string
	 * @param auris
	 * 		URIs of annotations
	 * @return list of annotation instances as JSON
	 */
	def String annotationsOfSearchResources(String query, auris, params = null) {
		if (GrailsNameUtils.isBlank(query))
			return "[]"
		if (!params) params = [:]
		params.query = query
		params.auris = auris

		log.debug "Getting annotations for query: $query"
		def json = httpService.doGet("content", "annotationsOfSearch", params)
		return json ?: "[]"
	}

	/**
	 * Search entities containing the given resources and annotations.
	 *
	 * @param uris
	 * 		URIs of resources
	 * @param auris
	 *		URIs of annotations
	 * @return list of entities as JSON
	 */
	def String loadEntitiesByResources(uris, auris, params = null) {
		if (!uris && !auris) {
			log.warn "Both parameter uris and auris are null"
			return "[]"
		}

		if (!params) params = [:]
		if (uris) params.uris = uris
		if (auris) params.auris = auris

		log.debug "Loading entities containing the resources $uris and resources annotated by $auris"
		def json = httpService.doPost("content", "containingResources", params)
		return json ?: "[]"
	}

	def int loadEntitiesByResourcesCount(uris, auris) {
		if (!uris && !auris) {
			log.warn "Both parameter uris and auris are null"
			return 0
		}

		log.debug "Counting entities containing the resources $uris and resources annotated by $auris"
		def params = [uris: uris, auris: auris]
		def json = httpService.doPost("content", "containingResourcesCount", params)
		return json ? json.getInt("count") : 0
	}

	/**
	 * Load an entity.
	 *
	 * @param uri
	 * 		URI of the entity to be loaded
	 * @return an instance as JSON
	 */
	def String load(uri) {
		if (!uri)
			throw new NullPointerException("URI is null")
		def json = httpService.doGet("content", "get", [uri: uri])
		return json ?: ""
	}

	/**
	 * List all available annotation sets not including their annotations. Never returns null
	 * (in case of an error an empty list is returned).
	 *
	 * @param ling
	 * 		load only strings of the given language
	 * @return list of instances as JSON
	 */
	def String loadAnnotationSets(String ling, params = null) {
		if (!params) params = [:]
		params.type = Loomp.AnnotationSet
		params.ling = ling
		log.info "Loading all annotation sets"
		def json = httpService.doGet("annotation", "getAll", params)
		return json ?: "[]"
	}

	/**
	 * Load an annotation set including its annotations
	 *
	 * @param uri
	 * 		URI of an annotation set
	 * @param ling
	 * 		load only strings of the given language
	 * @return list of instances as JSON
	 */
	def String loadAnnotations(uri, String ling, params = null) {
		if (!uri)
			throw new NullPointerException("URI is null")
		if (!params) params = [:]
		params.set = uri
		params.type = Loomp.Annotation
		params.ling = ling

		log.info "Loading annotations of annotation set $uri (locale: $ling)"
		def json = httpService.doGet("annotation", "getAll", params)
		return json ?: "[]"
	}

	/**
	 * TODO: Testfuntcion
	 * Call auto annotation service from loomps
	 * for test reasons only OpenCalais
	 *
	 * @param text
	 * 		text to be annotated
	 * @return annotated text
	 */
	def String autoAnnotate(String text) {
		log.info "Calling loomp for autoannotaions with $text"
        def respond=httpService.doGet("ars", "recommend", ["annotators": "openCalais",
				"openCalais.licenseID": "3bqn96b5rjfgj4mk9w3bpwzf", "text": text, "openCalais.contentType": "text/raw",
				"openCalais.allowSearch": "true", "openCalais.allowDistribution": "true"])
        if (respond!=null)
        {
            def convert= JsonUtils.fromJson(respond.toString(), new GwtEntityMapper())
            return convert.content;
        }
        return respond
	}

	/**
	 * Save an entity given as JSON.
	 *
	 * @param json
	 * 		a JSON string
	 * @return saved instance as JSON
	 */
	def String save(String json) {
		log.info "Saving an entity"
		return httpService.doGet("content", "save", [data: json])
	}

	/**
	 * Save an entity given as JSON.
	 *
	 * @param json
	 * 		a JSON string
	 * @return saved instance as JSON
	 */
	def String delete(uri) {
		if (!uri)
			throw new NullPointerException("URI is null")
		log.info "Deleting entity $uri"
		return httpService.doGet("content", "delete", [uri: uri])
	}

	/**
	 * Save an entity given as JSON.
	 *
	 * @param json
	 * 		a JSON string
	 * @return saved instance as JSON
	 */
	def String saveResource(String json) {
		return httpService.doGet("content", "saveResource", [data: json, temp: true])
	}
}
