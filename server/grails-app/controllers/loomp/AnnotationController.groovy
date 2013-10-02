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

import loomp.model.Annotation
import loomp.model.AnnotationSet
import loomp.model.DomainEntity
import loomp.model.TypeMapper
import loomp.utils.JenaUtils
import loomp.vocabulary.Loomp

/**
 * Controller to access AnnotationSet and Annotation. Although a client
 * may also access other domain entities, it is not guaranteed that it
 * will return a correct result. Accessing arbitrary resources will
 * definitely be erroneous.
 */
class AnnotationController extends GenericController {
	def loompService

	def index = {
		redirect controller: "testAnnotation"
	}

	/**
	 * Retrieve an annotation set or an annotation identified by a given URI. If
	 * 'full' is set to true and 'uri' refers to an annotation set then the
	 * annotations of this set are returned, too.
	 *
	 * Hint: ApiFilter ensures that we get a valid URI and format.
	 *
	 * @param uri
	 * 		URI of the requested entity
	 * @param full
	 * 		true = also retrieve annotations of an annotation set
	 * @param fmt
	 * 		format to encode the result
	 * @return serialized entity
	 */
	def get = {
		if (params?.full == 'true') {
			def entities = JenaUtils.modelsToEntities.call(loompService.getDataFull(request.uri, Loomp.hasAnnotations, Loomp.AnnotationSet))
			renderObject(entities)
		} else {
			def entity = DomainEntity.load(request.uri)
			renderObject(entity)
		}
	}

	/**
	 * If parameter set refers to an annotation set then the get method returns only annotations
	 * of that annotation set. Otherwise the get method covers all annotations.
	 *
	 * If parameter domain and/or range is present then the result is a set of
	 * annotations which property has the given domain and/or range.
	 *
	 * @param type
	 * 		URI of AnnotationSet or Annotation
	 * @param set
	 * 		URI referring to an annotation set (optional, type has to be the type URI of Annotation)
	 * @param domain
	 * 		domain of an annotation (type has to be the type URI of Annotation)
	 * @param range
	 * 		range of an annotation (type has to be the type URI of Annotation)
	 * @param property
	 * 		property of an annotation (type has to be the type URI of Annotation)
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
		def clazz = domainClass(request.typeUri)
		def entities
		if (AnnotationSet.class.equals(clazz)) {
			entities = AnnotationSet.loadAll(params)
		} else {
			// domain, range, property has to be not empty
			def constraints = [type: Loomp.Annotation]
			params.subMap(['domain', 'range', 'property']).each { k, v ->
				if (v) {
					try {
						constraints.put(k, URI.create(v)) 
					} catch (MalformedURLException e) {
						renderBadRequest(e.getMessage())
					}
				}
			}
			// TODO w.r.t the semantics of loadAllChilds we should use AnnotationSet here. But we have to modify the method to retrieve the type of the property first, so that the translation of the contraints will work  
			entities = params?.set ?
				AnnotationSet.loadAllChilds(params.set, "annotations", Annotation.class, constraints, params) :
				Annotation.loadAll(constraints, params)
		}
		renderObject(entities)
	}

	/**
	 * Get the latest annotation sets or annotations.
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
		def clazz = domainClass(request.typeUri)
		renderObject(clazz.loadAll(params))
	}

	/**
	 * If parameter set identifies an annotation set then all its annotations are
	 * counted. Otherwise, the number of all annotation sets is returned.
	 *
	 * @param fmt
	 * 		format to encode the result
	 * @return number of annotation sets / annotations of annotation set 
	 */
	def count = {
		def cnt = request?.set ?
			AnnotationSet.countEntriesByParent(request.set, Loomp.hasAnnotations, Loomp.Annotation) :
			AnnotationSet.count()
		renderObject(cnt)
	}

	/**
	 * Parse a document from the received data and save the values to the RDF store. If the entity
	 * has not been assigned a URI then a new one is generated.
	 *
	 * Hint: ApiFilter ensures that we get a valid URI and format.
	 *
	 * @param data
	 * 		URI of the entity to be saved
	 * @param fmt
	 * 		format to be used for parsing the data and for returning the result
	 * @return URI of the saved entity
	 */
	def save = {
		if (!params?.data) {
			log.error "Action called without mandatory parameter data"
			renderBadRequest("Parameter 'data' is mandatory")
		}

		def entity
		try {
			entity = parseString(params.data)
		} catch (IllegalArgumentException e) {
			renderBadRequest(text: e.getMessage())
			return
		}
		if (entity.save()) {
			log.info "Saved entity $entity.uri"
			renderObject(entity.uri)
		} else {
			log.error "Error while saving the given entity ${e.getMessage()}"
			renderBadRequest("Error while saving the entity")
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
