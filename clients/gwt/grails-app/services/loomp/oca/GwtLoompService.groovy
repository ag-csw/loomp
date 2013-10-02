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

import com.google.gson.Gson
import loomp.model.TypedPropertyValue
import loomp.oca.client.model.Annotation
import loomp.oca.client.model.AnnotationSet
import loomp.oca.client.model.BaseEntity
import loomp.oca.client.model.Resource
import loomp.utils.JsonUtils
import loomp.vocabulary.RDF
import loomp.vocabulary.RDFS

/**
 * Service for retrieving data from a loomp server.
 *
 * If methods of this class should be called by GWT then their names must not start
 * with 'get'!
 */
class GwtLoompService {
	static transactional = false

	def resourceService

	// next line needed for GWT; otherwise GWT cannot access service
	static expose = ['gwt:loomp.oca.client']

	static final Gson gson = new Gson()
	def loompService

	/**
	 * Retrieve the Version of the loomp API.
	 *
	 * @return version as a string
	 */
	def String loompApiVersion() {
		return loompService.loompApiVersion();
	}

	/**
	 * Load all entities of a given type.
	 *
	 * @param uri
	 * 		URI of the entity to be loaded
	 * @return a instance of the GWT domain model
	 */
	def List<BaseEntity> loadAll(String typeUri) {
		return (List<BaseEntity>) JsonUtils.fromJson(loompService.loadAll(typeUri, null), new GwtEntityMapper())
	}

	/**
	 * Load the latest modified entities of a given type.
	 *
	 * @param uri
	 * 		URI of the entity to be loaded
	 * @param max
	 * 		number of returned entities
	 * @return a instance of the GWT domain model
	 */
	def List<BaseEntity> latest(typeUri, max) {
		return (List<BaseEntity>) JsonUtils.fromJson(loompService.latest(typeUri, max), new GwtEntityMapper())
	}

	/**
	 * @param ling (in params)
	 * 		requested language of the resource labels
	 * @see LoompService#searchResources
	 */
	def List<Resource> searchResources(String query, String auri, boolean inclExtern, params = null) {
		def resources = (List<Resource>) JsonUtils.fromJson(
				loompService.searchResources(query, auri, inclExtern, params), new GwtEntityMapper())
		resources.each { it.label = resourceService.getLabel(it, (params?.ling ?: "de")) }
		return resources
	}

	/**
	 * @see LoompService#searchResourcesCount
	 */
	def int searchResourcesCount(String query, String auri, boolean inclExtern) {
		return loompService.searchResourcesCount(query, auri, inclExtern)
	}

	/**
	 * Load an entity.
	 *
	 * @param uri
	 * 		URI of the entity to be loaded
	 * @return a instance of the domain model
	 */
	def BaseEntity load(String uri) {
		return (BaseEntity) JsonUtils.fromJson(loompService.load(uri), new GwtEntityMapper())
	}

	/**
	 * List all available annotation sets not including their annotations. Never returns null
	 * (in case of an error an empty list is returned).
	 *
	 * @return a list of annotation sets
	 */
	def List<AnnotationSet> loadAnnotationSets(language) {
		return (List<AnnotationSet>) JsonUtils.fromJson(loompService.loadAnnotationSets(language), new GwtEntityMapper(language))
	}

	/**
	 * Load an annotation set including its annotations
	 *
	 * @param uri
	 * 		URI of an annotation set
	 * @return an annotation set
	 */
	def List<Annotation> loadAnnotations(uri, language) {
		return (List<Annotation>) JsonUtils.fromJson(loompService.loadAnnotations(uri, language), new GwtEntityMapper(language))
	}

	/**
	 * Call auto annotation service from loomp
	 * for test reasons only OpenCalais
	 *
	 * @param uri
	 * 		URI of an annotation set
	 * @return an annotation set
	 */
	def String autoAnnotate(String text) {
        log.info "Respond by autoannotate: $text"
		//return JsonUtils.fromJson(loompService.autoAnnotate(text))
        return loompService.autoAnnotate(text)
	}

	/**
	 * Save a given entity.
	 *
	 * @param entity
	 * 		an entity
	 * @return the saved entity if successful, otherwise null
	 */
	def BaseEntity save(BaseEntity entity) {
		def mapper = new GwtEntityMapper()
		def result = loompService.save(JsonUtils.toJson(entity, mapper))
		return (BaseEntity) JsonUtils.fromJson(result, mapper)
	}

	/**
	 * Method for saving resources in the database.
	 *
	 * @param uri
	 * 		URI identifying resource to save
	 * @param value
	 *       primary annotation text assigned to this resource
	 * @param a
	 *       Annotation to which given resource belongs
	 * @param comment
	 *       primary annotation comment, which describes this resource
	 * @return an annotation set
	 */
	def String saveResource(String uri, String value, Annotation a, String comment) {
		// creating a map, which creates the connection between annotation, resource and annotated text
		/*
			creating the specific resource to save in loomp:
			despite oca model and loomp model entities are similar
			still, that are different objects, from different libraries.
			Which means -> a loomp Resource has to be created and
			all needed data passed to it, before being sent to the
			loomp-server.
		 */
		def resource = new loomp.model.Resource(uri: URI.create(uri))
		resource.props = []
		// add annotated text
		resource.props << new TypedPropertyValue(property: URI.create(a.propertyUri), value: value, isLiteral: true)
		// add comment
		resource.props << new TypedPropertyValue(property: RDFS.COMMENT, value: comment, isLiteral: true)
		// add type
		if (a.propertyUri)
			resource.props << new TypedPropertyValue(property: RDF.TYPE, value: URI.create(a.domainUri), isLiteral: false)

		// converting the resource into Json (plain text format),
		// for sending it to the loomp server
		def json = loompService.saveResource(JsonUtils.toJson(resource))
		return json ?: ""
	}

    /**
     *
     * @param uri
     * @return
     */
    def List<Resource> containedResources(String uri, params = null){
        def resources = (List<Resource>) JsonUtils.fromJson(
				loompService.containedResources(uri, null), new GwtEntityMapper())
		resources.each { it.label = resourceService.getLabel(it, (params?.ling ?: "de"))}
		return resources;
    }
}
