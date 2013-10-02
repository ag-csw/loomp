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
package loomp.utils

import org.codehaus.groovy.grails.commons.ApplicationHolder as AH

import com.hp.hpl.jena.rdf.model.Model
import com.hp.hpl.jena.rdf.model.ModelFactory
import loomp.model.db.SystemParam
import loomp.vocabulary.Loomp
import loomp.model.*
import org.codehaus.groovy.grails.commons.ConfigurationHolder

/**
 * Utilities on instances of the domain model
 */
class ModelUtils {
	/**
	 * Add methods for loading, saving, etc. to the classes of the domain model.
	 */
	public static def addMethodsToModelClasses = {
		def clazzes = TypeMapper.instance.getDomainClasses() + [BaseEntity.class, Resource.class]
		clazzes.each {
			it.metaClass {
				'static' {
					/**
					 * Load an instance of a domain model class.
					 *
					 * @param uri
					 * 		uri of the resource to be loaded
					 * @return an instance of the appropriate domain model class
					 *
					 * @throws NullPointerException
					 * @throws IllegalArgumentException
					 */
					load = { uri ->
						if (!uri)
							throw new NullPointerException("Uri is null")

						// because we are in static context, loompService is not injected
						Model m = AH.application.mainContext.loompService.getData(uri)
						return delegate.fromModel.call(m)
					}
					/**
					 * Load all instances of a domain model class.
					 *
					 * @return a list of instances of the appropriate domain model class
					 *
					 * @throws IllegalArgumentException
					 */
					loadAll = {
						return delegate.loadAll([:])
					}

					/**
					 * Load all (up to limit) instances of a domain model class starting at offset.
					 *
					 * @param offset (in params)
					 *   where to start (null = starting at the beginning)
					 * @param limit (in params)
					 *   how many (null = all)
					 * @param sort (in params)
					 * 		URI of the property for sorting the result list (null = uri)
					 * @return a list of instances of the appropriate domain model class
					 *
					 * @throws NullPointerException
					 * @throws IllegalArgumentException
					 */
					loadAll = { params ->
						def constraints = delegate?.type ? [type: delegate.type] : [:]
						return delegate.loadAll(constraints, params)
					}

					/**
					 * Load all (up to limit) instances of a domain model class which satisfy
					 * the given constraints starting at offset. The parameter constraints is
					 * a map <member, values> with the meaning that the member of the entity
					 * should have one of the given values.
					 *
					 * @param constraints
					 * 		constraints on the entities to be loaded
					 * @param offset (in params)
					 *   where to start (null = starting at the beginning)
					 * @param limit (in params)
					 *   how many (null = all)
					 * @param sort (in params)
					 * 		URI of the property for sorting the result list (null = uri)
					 * @return a list of instances of the appropriate domain model class
					 *
					 * @throws NullPointerException
					 * @throws IllegalArgumentException
					 */
					loadAll = { constraints, params ->
						// because we are in static context, loompService is not injected
						def models = AH.application.mainContext.loompService.getAllData(
								delegate.translateConstraints(constraints), params)
						return delegate.fromModels.call(models)
					}

					/**
					 * Load all children of a given property (property has to be an RDF list)
					 * which satisfy the given constraints. The parameter constraints is
					 * a map <member, values> with the meaning that the member of the entity
					 * should have one of the given values.
					 *
					 * @param uri
					 * 		uri of the resource to be loaded
					 * @param property
					 * 		name of the property (not its mapped URI) to be loaded
					 * @param clazz
					 * 		java class of the values of the property
					 * @param constraints
					 * 		constraints on the entities to be loaded
					 * @param offset (in params)
					 *   where to start (null = starting at the beginning)
					 * @param limit (in params)
					 *   how many (null = all)
					 * @param sort (in params)
					 * 		URI of the property for sorting the result list (null = uri)
					 * @return a list of instances of the appropriate domain model class
					 *
					 * @throws NullPointerException
					 * @throws IllegalArgumentException
					 */
					loadAllChilds = { uri, property, clazz, constraints, params ->
						if (!uri)
							throw new NullPointerException("Parameter uri is null")
						if (!property)
							throw new NullPointerException("Parameter property is null")
						if (!clazz && constraints)
							throw new NullPointerException("Parameter clazz has to be not null if constraints are given")

						def propertyUri = delegate.mapping["$property"]
						if (!propertyUri)
							throw new IllegalArgumentException("${delegate.class.getSimpleName()} does not have a property $property")
						// because we are in static context, loompService is not injected
						def models = AH.application.mainContext.loompService.getListEntriesByParent(
								uri, propertyUri, clazz?.translateConstraints(constraints), params)
						return delegate.fromModels.call(models)
					}

					loadAllChilds = { uri, property, params ->
						return delegate.loadAllChilds(uri, property, null, null, params)
					}

					/**
					 * Get count of documents at the current endpoint.
					 *
					 * @return result of document count
					 */
					count = {
						return AH.application.mainContext.loompService.countData(delegate.type)
					}

					/**
					 * Count the children of a given property of a given domain entity.
					 *
					 * @param uri
					 * 		uri of the resource to be loaded
					 * @param property
					 * 		name of the property (not its mapped URI) to be loaded
					 * @param constraints
					 * 		constraints on the entities to be loaded
					 * @return number of children of property
					 *
					 * @throws NullPointerException
					 * @throws IllegalArgumentException
					 */
					countChilds = { uri, property, clazz, constraints ->
						if (!uri)
							throw new NullPointerException("Parameter uri is null")
						if (!property)
							throw new NullPointerException("Parameter property is null")
						if (!clazz && constraints)
							throw new NullPointerException("Parameter clazz has to be not null if constraints are given")

						def propertyUri = delegate.mapping["$property"]
						if (!propertyUri)
							throw new IllegalArgumentException("${delegate.class.getSimpleName()} does not have a property $property")
						return AH.application.mainContext.loompService.countEntriesByParent(
								uri, propertyUri, clazz?.translateConstraints(constraints))
					}

					countChilds = { uri, property ->
						return delegate.countChilds(uri, property, null, null)
					}
				}

				//
				// NON-STATIC METHODS
				//

				/**
				 * see static version of loadAllChilds
				 */
				loadAllChilds = { property, clazz, constraints, params ->
					if (!delegate.uri)
						throw new ModelException("Loading children of a non-persistent entity does not work")
					if (!property)
						throw new NullPointerException("Parameter property is null")
					if (!clazz && constraints)
						throw new NullPointerException("Parameter clazz has to be not null if constraints are given")

					def propertyUri = delegate.mapping["$property"]
					if (!propertyUri)
						throw new IllegalArgumentException("${delegate.class.getSimpleName()} does not have a property $property")
					def models = AH.application.mainContext.loompService.getListEntriesByParent(
							delegate.uri, propertyUri, clazz?.translateConstraints(constraints), params)
					return delegate.fromModels.call(models)
				}

				loadAllChilds = { uri, property, params ->
					return delegate.loadAllChilds(uri, property, null, null, params)
				}

				/**
				 * Save an instance of a domain model class. If the URI of the instance is not
				 * set, e.g., null, then a new URI is assigned. The values dateCreated, lastModified,
				 * and version are set automatically. It is ensured that an entity with a newer version
				 * will not be overwritten. The version of entity to be saved has to be the old version
				 * increased by one.
				 *
				 * @param graphUri
				 * 		URI of the graph to store the instance (null => default graph)
				 *
				 * @return the saved instance if successful, otherwise null
				 * @throws ConcurrentModificationException if endpoint contains a newer version of the entity
				 */
				save = { URI graphUri ->
					if (!delegate.uri) {
						// TODO later if there exist user-defined base URI then they should be included here
						delegate.uri = AH.application.mainContext.uriService.generateUri()
						delegate.dateCreated = new Date()
					} else {
						def versioningEnabled = SystemParam.findByName(SystemParam.VERSIONING) as Boolean
						// do versioning
						if (versioningEnabled && delegate instanceof DomainEntity) {
							def storeVersion = AH.application.mainContext.loompService.getVersion(uri)
							if (delegate.dbVersion == null) {
								delegate.dbVersion = 0
							}
							if (storeVersion && delegate.dbVersion <= storeVersion) {
								throw new ConcurrentModificationException("Store contains a newer version of $delegate.uri")
							}
						}
					}

					if (delegate instanceof BaseEntity) {
						delegate.lastModified = new Date()
					}

					// Convert entity
					Model model = ModelFactory.createDefaultModel()
					delegate.toModel.call(model, delegate)
					delegate.uri = AH.application.mainContext.loompService.saveData(delegate.uri, model, graphUri)

					// Process RDFa to store it in a named graph. Thus, we know which statements has been generated by which elements
					if (delegate instanceof ElementText && delegate.content) {
						Model rdfaModel = ModelFactory.createDefaultModel()
						if (delegate.content instanceof Collection) {
							delegate.content.each { rdfaModel.add(RdfaUtils.processRdfa(it, delegate)) }
						} else {
							rdfaModel.add(RdfaUtils.processRdfa(delegate.content, delegate))
						}
						// all old RDFa statements are deleted and the new one are inserted
						AH.application.mainContext.loompService.saveData(null, rdfaModel, delegate.uri)
						AH.application.mainContext.loompService.clear(ConfigurationHolder.config.loomp.temporaryGraph)
					}

					return delegate.uri ? delegate : null
				}

				/**
				 * Save the entity in the default graph.
				 */
				save = {
					return delegate.save((URI) null)
				}

				/**
				 * Deletes an instance of a domain model class.
				 *
				 * @return true if successful
				 */
				delete = {
					if (!delegate.uri) {
						throw new ModelException("Deleting a non-persistent entity does not work")
					}
					return AH.application.mainContext.loompService.deleteData(delegate.uri)
				}
			}
		}
	}
}
