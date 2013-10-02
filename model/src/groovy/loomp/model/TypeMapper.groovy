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

/**
 * Class for mapping URIs to a Groovy/Java class.
 */
class TypeMapper {
	Map<URI, Class> typeMap

	/**
	 * The single global instance of the TypeMapper. Done this way rather
	 * than simply making the static field directly accessible to allow us to
	 * dynamically replace the entire mapper table if needed.
	 */
	static instance

	/**
	 * Static initializer. Creates an instance and puts all model classes of this
	 * package into the map. 
	 */
	static {
		instance = new TypeMapper()
		instance.typeMap = [:]
		[DomainEntity.class, AnnotationSet.class, Annotation.class, Document.class,
				Element.class, ElementText.class, ElementSparql.class, loomp.model.Person.class].each {
			instance.register(it.type, it)
		}
	}

	/**
	 * Get the domain class corresponding to a given URI. It returns null if the URI
	 * is unknown.
	 *
	 * @param typeUri
	 * 		a URI referring to the type of a domain class
	 * @return a class or null
	 */
	def getDomainClass(typeUri) {
		if (typeUri instanceof String) {
			typeUri = URI.create(typeUri)
		}
		return typeMap.get(typeUri)
	}

	/**
	 * Get all domain classes. Never returns null.
	 *
	 * @return domain classes
	 */
	def getDomainClasses() {
		return typeMap.values()
	}

	/**
	 * Get all known type URIs. Never returns null.
	 *
	 * @return type URIs
	 */
	def getKnownTypes() {
		return typeMap.keySet()
	}

	/**
	 * @param typeUri
	 * 		a type URI
	 *
	 * @return true iff the type is known by the mapper
	 */
	def isKnown(typeUri) {
		return typeMap.containsKey(typeUri)
	}

	/**
	 * Register a new datatype
	 *
	 * @param typeUri
	 * 		URI of the type
	 * @param clazz
	 * 		a class
	 */
	def register(typeUri, clazz) {
		if (typeUri instanceof String) {
			typeUri = URI.create(typeUri)
		}
		typeMap.put(typeUri, clazz)
	}

	/**
	 * Remove all mapping entries.
	 */
	def clear() {
		typeMap.clear()
	}
}
