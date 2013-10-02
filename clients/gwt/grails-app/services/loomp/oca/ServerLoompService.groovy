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
import loomp.model.Annotation
import loomp.model.AnnotationSet
import loomp.model.BaseEntity
import loomp.model.Resource
import loomp.utils.JsonUtils

/**
 * Service for retrieving data from a loomp server.
 *
 * If methods of this class should be called by GWT then their names must not start
 * with 'get'!
 */
class ServerLoompService {
	static transactional = false
	static final Gson gson = new Gson()
	def loompService

	/**
	 * @see LoompService#loompApiVersion
	 */
	def String loompApiVersion() {
		return loompService.loompApiVersion()
	}

	/**
	 * @see LoompService#count
	 */
	def int count(typeUri) {
		return loompService.count(typeUri)
	}

	/**
	 * @see LoompService#loadAll
	 */
	def List<BaseEntity> loadAll(typeUri, params = null) {
		return (List<BaseEntity>) JsonUtils.fromJson(loompService.loadAll(typeUri, params))
	}

	/**
	 * @see LoompService#latest
	 */
	def List<BaseEntity> latest(typeUri, max) {
		return (List<BaseEntity>) JsonUtils.fromJson(loompService.latest(typeUri, max))
	}

	/**
	 * @see LoompService#containedResources
	 */
	def List<Resource> containedResources(uri, params = null) {
		return (List<Resource>) JsonUtils.fromJson(loompService.containedResources(uri, params))
	}

	/**
	 * @see LoompService#searchResources
	 */
	def List<Resource> searchResources(String query, auris, boolean inclExtern, params = null) {
		return (List<Resource>) JsonUtils.fromJson(loompService.searchResources(query, auris, inclExtern, params))
	}

	/**
	 * @see LoompService#searchResourcesCount
	 */
	def int searchResourcesCount(String query, auris, boolean inclExtern) {
		return loompService.searchResourcesCount(query, auris, inclExtern)
	}

	/**
	 * @see LoompService#annotationsOfSearchResources
	 */
	def List<Annotation> annotationsOfSearchResources(String query, auris, params = null) {
		return (List<Annotation>) JsonUtils.fromJson(loompService.annotationsOfSearchResources(query, auris, params))
	}

	/**
	 * @see LoompService#loadEntitiesByResources
	 */
	def List<BaseEntity> loadEntitiesByResources(uri, auris, params = null) {
		return (List<BaseEntity>) JsonUtils.fromJson(loompService.loadEntitiesByResources(uri, auris, params))
	}

	/**
	 * @see LoompService#loadEntitiesByResourcesCount
	 */
	def int loadEntitiesByResourcesCount(uri, auris) {
		return loompService.loadEntitiesByResourcesCount(uri, auris)
	}

	/**
	 * @see LoompService#load
	 */
	def load(uri) {
		return JsonUtils.fromJson(loompService.load(uri))
	}

	/**
	 * @see LoompService#loadAnnotationSets
	 */
	def List<AnnotationSet> loadAnnotationSets(language = null) {
		return (List<AnnotationSet>) JsonUtils.fromJson(loompService.loadAnnotationSets(language))
	}

	/**
	 * @see LoompService#loadAnnotations
	 */
	def List<Annotation> loadAnnotations(uri, language = null) {
		return (List<Annotation>) JsonUtils.fromJson(loompService.loadAnnotations(uri, language))
	}

	/**
	 * @see LoompService#autoAnnotate
	 */
	def String autoAnnotate(String text) {
		return (List<Annotation>) JsonUtils.fromJson(loompService.autoAnnotate(text))
	}

	def String delete(uri) {
		return loompService.delete(uri)
	}
}
