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

import com.hp.hpl.jena.vocabulary.DCTerms
import loomp.model.DomainEntity
import loomp.model.TypeMapper
import loomp.utils.JenaUtils
import loomp.vocabulary.Loomp
import loomp.model.BaseEntity
import loomp.model.Resource
import loomp.utils.JsonUtils

import org.codehaus.groovy.grails.commons.ConfigurationHolder

/**
 * Controller accepting REST request for accessing content
 */
class ContentController extends GenericController {
	def loompService

	def index = {
		redirect controller: "testContent"
	}

	/**
	 * Retrieve the entity identified by a given URI.
	 *
	 * Hint: ApiFilter ensures that we get a valid URI and format.
	 *
	 * @param uri
	 * 		URI of the requested entity
	 * @param fmt
	 * 		format to encode the result
	 * @return serialized entity
	 */
	def get = {
		final entityType = loompService.getEntityType(request.uri)
		final resource = entityType ? DomainEntity.load(request.uri) : Resource.load(request.uri)
		renderObject(resource)
	}

	/**
	 * Retrieve all entities of a given type from the SPARQL endpoint.
	 *
	 * @param type
	 * 		URI representing the type of an entity
	 * @param offset
	 * 		offset in the list of entities (optional); defaults to 5
	 * @param limit
	 *      amount of entities to be returned (optional)
	 * @param sort
	 * 		property to sort the result (optional)
	 * @param fmt
	 * 		format to encode the result
	 * @return list of serialized entities
	 */
	def getAll = {
		final clazz = domainClass(request.typeUri)
		final resources = clazz ? clazz.loadAll(params) : BaseEntity.loadAll(params)
		renderObject(resources)
	}

	/**
	 * Get the types of a given URI.
	 *
	 * @param uri
	 * 		URI of the requested entity
	 * @param entity
	 * 		if set to true then only the entity type are returned
	 * @param offset
	 * 		offset in the list of entities (optional); defaults to 5
	 * @param limit
	 *      amount of entities to be returned (optional)
	 * @param sort
	 * 		property to sort the result (optional)
	 * @param fmt
	 * 		format to encode the result
	 */
	def type = {
		final types = params?.entity ?
			loompService.getEntityType(request.uri) :
			loompService.getTypes(request.uri, params)
		renderObject(types)
	}

	/**
	 * Get all entities that contain a specified resource.
	 *
	 * @param uri
	 * 		URI of a resource
	 * @param offset
	 * 		offset in the list of entities (optional); defaults to 5
	 * @param limit
	 *      amount of entities to be returned (optional)
	 * @param sort
	 * 		property to sort the result (optional)
	 * @param fmt
	 * 		format to encode the result
	 * @return list of serialized entities
	 */
	def containingResources = {
		final entities = JenaUtils.modelsToEntities.call(
				loompService.getEntitiesByResources(request.uris, request.auris, params))
		renderObject(entities)
	}

	def containingResourcesCount = {
		final count = loompService.getEntitiesByResourcesCount(request.uris, request.auris)
		renderObject([count: count])
	}

	/**
	 * Return all resources that are contained in a given ElementText or Document
	 *
	 * @param uri
	 * 		URI of a text element
	 * @param only
	 * 		only retrieve statements that are contained in text element
	 */
	def containedResources = {
		final resource = JenaUtils.modelsToResources.call(
				loompService.getContainedResources(request.uri, null, params, (params.only == "true")))
		renderObject(resource)
	}

	/**
	 * Search for a given string in the instances of ElementText.
	 *
	 * @param query
	 * 		a query string
	 * @param prop
	 * 		properties to be searched in
	 * @param offset
	 * 		offset in the list of entities (optional); defaults to 0
	 * @param limit
	 *      amount of entities to be returned (optional)
	 * @param sort
	 * 		property to sort the result (optional)
	 * @return a list of matching ElementText
	 */
	def searchText = {
		final resources = JenaUtils.modelsToEntities.call(loompService.searchElementText(request.query, params))
		renderObject(resources)
	}

	def searchTextCount = {
		final count = loompService.searchElementTextCount(request.query)
		renderObject([count: count])
	}

	/**
	 * Search for a given string in the literals of annotated Resources.
	 *
	 * @param query
	 * 		a query string
	 * @param prop
	 * 		properties to be searched in
	 * @param external
	 * 		include external data sources
	 * @param offset
	 * 		offset in the list of entities (optional); defaults to 0
	 * @param limit
	 *      amount of entities to be returned (optional)
	 * @param sort
	 * 		property to sort the result (optional)
	 * @return a list of matching ElementText
	 */
	def search = {
		final resources = JenaUtils.modelsToResources.call(
				loompService.searchIt(request.query, request.propUris, request.typeUri, params.external == "true", params))
		renderObject(resources)
	}

	def searchCount = {
		final count = loompService.searchItCount(request.query, request.propUris, request.typeUri, params.external == "true")
		renderObject([count: count])
	}

	/**
	 * Search for a given string in the literals of annotated Resources.
	 *
	 * @param query
	 * 		a query string
	 * @param prop
	 * 		properties to be searched in
	 * @param external
	 * 		include external data sources
	 * @param offset
	 * 		offset in the list of entities (optional); defaults to 0
	 * @param limit
	 *      amount of entities to be returned (optional)
	 * @param sort
	 * 		property to sort the result (optional)
	 * @return a list of matching ElementText
	 */
	def searchResources = {
		final resources = JenaUtils.modelsToResources.call(
				loompService.searchResources(request.query, request.auris, params.external == "true", params))
		renderObject(resources)
	}

	def searchResourcesCount = {
		final count = loompService.searchResourcesCount(request.query, request.auris, params.external == "true")
		renderObject([count: count])
	}

	/**
	 * Get the annotations that are used with the resources obtained by calling #search.
	 */
	def annotationsOfSearch = {
		final resources = JenaUtils.modelsToEntities.call(loompService.annotationsOfSearch(request.query, request.propUris, request.typeUri, params))
		renderObject(resources)
	}

	/**
	 * Get the latest entities of a given type in the endpoint.
	 *
	 * @param type
	 * 		URI representing the type of an entity
	 * @param limit
	 *      amount of entities to be returned (optional)
	 * @param fmt
	 * 		format to encode the result
	 * @return list of the latest entities (serialized)
	 */
	def latest = {
		if (!params?.max) params.max = 5
		params.sort = DCTerms.modified
		params.order = "desc"
		final entities = domainClass(request.typeUri).loadAll(params)
		renderObject(entities)
	}

	/**
	 * Get the number of entities of a given type.
	 *
	 * @param type
	 * 		URI representing the type of an entity
	 * @param fmt
	 * 		format to encode the result
	 * @return number of entities
	 */
	def count = {
		final clazz = domainClass(request.typeUri)
		final cnt = clazz ? clazz.count() : loompService.countData(request.typeUri)
		renderObject([count: cnt])
	}

	/**
	 * Parse an entity from the received data and save the values to the RDF store. If the entity
	 * has not been assigned a URI then a new one is generated.
	 *
	 * Hint: ApiFilter ensures that we get a valid URI and format.
	 *
	 * @param data
	 * 		URI of the entity to be saved
	 * @param fmt
	 * 		format to be used for parsing the data and for returning the result
	 * @return the saved entity
	 */
	def save = {
		if (!params?.data) {
			log.error "Action called without mandatory parameter data"
			renderBadRequest("Parameter 'data' is mandatory")
			return
		}

		def entity
		try {
			entity = parseString(params.data, request.fmt)
		} catch (IllegalArgumentException e) {
			renderBadRequest(e.getMessage())
			return
		}

		if (!entity) {
			log.error "Unable to parse entity from data: $params.data"
			renderBadRequest("Unable to parse entity from data")
			return
		}

		try {
			if (entity.save()) {
				log.info "Saved entity $entity.uri"
				renderObject(entity)
			} else {
				log.error "Error while saving the entity $entity"
				renderInternalError("Entity could not be saved")
			}
		} catch (ConcurrentModificationException e) {
			log.error "Error while saving the entity $entity: ${e.getMessage()}"
			renderInternalError("Error while saving the entity: ${e.getMessage()}")
		}
	}

	/**
	 * Save a resource.
	 *
	 * @param data
	 * 		data describing the resource
	 * @param temp
	 * 		if set to true then the data is stored in a temporary graph
	 */
	def saveResource = {
		if (!params?.data) {
			log.error "Action called without mandatory parameter data"
			renderBadRequest("Parameter 'data' is mandatory")
		}

		def graphUri = params?.temp == "true" ? ConfigurationHolder.config.loomp.temporaryGraph : null
		Resource resource = JsonUtils.fromJson(params.data, Resource.class)
		if (resource.save(URI.create(graphUri))) {
			log.info "Saved resource $resource.uri"
			renderObject(["value": "sucess"])
		} else {
			log.error "Error while saving the resource $resource"
			renderInternalError("Resource could not be saved")
		}
	}

	/**
	 * Delete all triples having a given subject URI.
	 *
	 * @param uri
	 * 		URI of the entity to be deleted
	 * @param fmt
	 * 		format to encode the result
	 * @return URI of the deleted entity
	 */
	def delete = {
		def entity = new DomainEntity(uri: request.uri)
		if (entity.delete()) {
			log.info "Deleted entity with URI $entity.uri"
			renderObject(entity.uri)
		} else {
			log.error "Error while deleting the given entity ${e.getMessage()}"
			renderInternalError("Error while deleting entity with URI $entity.uri")
		}
	}

	// shorthand for accessing {@link TypeMapper}
	private def domainClass(typeUri) { TypeMapper.instance.getDomainClass(typeUri) }
}
