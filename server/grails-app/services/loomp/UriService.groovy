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

import loomp.model.db.SystemParam
import org.apache.commons.lang.StringUtils
import loomp.model.VersionedUri
import loomp.model.ResourceType

/**
 * Service for managing URIs.
 */
class UriService {
	/** maximum number of attempts to find an unused URI   */
	final MAX_URI_TEST = 3

	def loompService

	/**
	 * Generate a new URI for content (ResourceType.DATA) using the system wide base
	 * namespace.
	 *
	 * @return
	 * generated URI
	 */
	def generateUri() {
		return generateUri(baseNs(), ResourceType.DATA)
	}

	/**
	 * Generate a new URI for content (ResourceType.DATA).
	 *
	 * @param base
	 * 		base of the URI
	 * @return
	 * generated URI
	 */
	def generateUri(String base) {
		return generateUri(base, ResourceType.DATA)
	}

	/**
	 * Generate a new URI for a given type of resource using the system wide base
	 * namespace.
	 *
	 * @param type
	 * 		type of the resource which will be assigned the generated URI
	 * @return
	 * generated URI
	 */
	def generateUri(ResourceType type) {
		return generateUri(baseNs(), ResourceType.DATA)
	}

	/**
	 * Generate a new URI for a given type of resource.
	 *
	 * @param base
	 * 		base namespace of the URI
	 * @param type
	 * 		type of the resource which will be assigned the generated URI
	 * @return
	 * generated URI
	 */
	def generateUri(String base, ResourceType type) {
		def exists = true
		def attempts = 0
		def uri = null

		base = StringUtils.removeEnd(base, "/")

		while (exists && attempts < MAX_URI_TEST) {
			uri = URI.create("$base/${type.asUriPart()}/${UUID.randomUUID().toString()}")
			// TODO Avoid possible conflicts: Remember the generated URIs for some time and then forget them.
			exists = loompService.existsUri(uri)
			++attempts
		}

		if (exists) {
			throw new LoompException("Unable to generate an unused URI")
		}
		return uri
	}

	/**
	 * Create a versioned URI for a given URI. Use the namespace for versioned URIs. The
	 * ResourceType of uri is maintained.
	 *
	 * @param uri
	 * 		a URI
	 * @return versioned Uri
	 */
	// TODO something is here wrong
	def VersionedUri getVersionedUri(uri) {
		return getVersionedUri(versionBaseNs(), uri)
	}

	/**
	 * @return system wide base namespace
	 * @throws LoompException if the parameter has not been configured
	 */
	def String baseNs() {
		def baseParam = SystemParam.findByName(SystemParam.BASE_NS)
		if (!baseParam)
			throw new LoompException("System parameter $SystemParam.BASE_NS has not been configured")
		return baseParam.value
	}

	/**
	 * @return system wide base namespace for versioned URIs
	 * @throws LoompException if the parameter has not been configured
	 */
	def String versionBaseNs() {
		def baseParam = SystemParam.findByName(SystemParam.BASE_NS_VERSION)
		if (!baseParam)
			throw new LoompException("System parameter $SystemParam.BASE_NS_VERSION has not been configured")
		return baseParam.value
	}
}
