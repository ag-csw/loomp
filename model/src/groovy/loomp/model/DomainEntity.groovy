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
package loomp.model

import com.hp.hpl.jena.vocabulary.DCTerms
import com.hp.hpl.jena.vocabulary.RDF
import loomp.utils.JenaUtils
import loomp.utils.UriUtils
import loomp.vocabulary.Loomp
import com.google.gson.annotations.Expose

/**
 * Base of all classes of the domain classes of loomp.
 */
class DomainEntity extends BaseEntity {
	/** type of the entity. Should be overridden by subclasses.     */
	static URI type = Loomp.domainEntity

	/**
	 * URI referring to the previous version of the entity
	 * This property can be used to link to an entity being an earlier version
	 * of this one.
	 */
	@Expose VersionedUri hasVersion

	/** current version of this entity in the database*/
	@Expose Long dbVersion = 0

	public static mapping = BaseEntity.mapping + [
			type: RDF.type,
			hasVersion: DCTerms.hasVersion,
			dbVersion: Loomp.dbVersion
	]

	/**
	 * Increase the version number by one. The version of hasVersion property is also
	 * increased. If the entity has currently no version then a versioned URI is created
	 * using its URI as base of the versioned URI. The last two parts of the entity URI
	 * are transferred to the versioned URI, e.g., 'http://ex.com/data/id' becomes
	 * '<versionBase>/data/id--0'.
	 *
	 * After calling #incVersion(Object) once #incVersion() can be used. The versionBase
	 * is kept. 
	 *
	 * @param versionBase
	 * 		base of the versioned URI
	 */
	public void incVersion(versionBase) {
		if (!hasVersion) {
			dbVersion = 1
			hasVersion = UriUtils.getVersionedUri(versionBase, uri)
		} else {
			dbVersion++
			hasVersion.version++
		}
	}

	/**
	 * Increase the version number by one. The version of hasVersion property is also
	 * increased. If the entity has currently no version then a versioned URI is created
	 * using its URI as base of the versioned URI, e.g., 'http://ex.com/data/id' becomes
	 * 'http://ex.com/data/id--0'.
	 */
	public void incVersion() {
		incVersion(null)
	}

	/**
	 * If clazz defines a mapping substitute the keys of constraints with the mapped
	 * URI. If clazz is null or does not define a mapping the original constraints
	 * are returned.
	 *
	 * @param clazz
	 * 		a class defining a mapping
	 * @param constraints
	 * 		a map of constraints
	 * @return a map of translated constraints
	 */
	static translateConstraints(constraints) {
		if (!mapping || !constraints)
			return constraints
		def newConstraints = [:]
		constraints.each { attr, value ->
			if (mapping["$attr"]) {
				newConstraints.put(mapping["$attr"], value)
			}
		}
		return newConstraints
	}

	/** closure to get a entity from a model   */
	public static fromModel = JenaUtils.modelToEntity

	/** closure to get entities from a set of model   */
	public static fromModels = JenaUtils.modelsToEntities

	/** closure to get a model from a entity   */
	public static toModel = JenaUtils.entityToModel
}
