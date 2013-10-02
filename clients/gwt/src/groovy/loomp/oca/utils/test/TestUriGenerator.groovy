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
package loomp.oca.utils.test


/**
 * Class that generates unique URIs for testing purposes.
 */
class TestUriGenerator {
	static final TYPE_DATA = "data"
	static final TYPE_ANNOTATION = "annotation"

	/** contains all URIs returned by the generator */
	def uris = []

	/** maximum number of attempts to find an unused URI */
	final MAX_URI_TEST = 3

	/**
	 * Generate a new URI for content (ResourceType.DATA).
	 *
	 * @param base
	 *		base of the URI
	 * @return
	 * 		generated URI
	 */
	def String generateUri(String base) {
		return generateUri(base, TYPE_DATA)
	}

	/**
	 * Generate a new URI for a given type of resource.
	 *
	 * @param base
	 *		base of the URI
	 * @param type
	 * 		type of the resource which will be assigned the generated URI
	 * @return
	 * 		generated URI
	 */
	def String generateUri(String base, String type) {
		def exists = true
		def attempts = 0
		def uri = null

		while (exists && attempts < MAX_URI_TEST) {
			uri = "$base/${type.toString().toLowerCase()}/${UUID.randomUUID().toString()}"
			exists = existsUri(uri)
			++attempts
		}

		if (exists) {
			throw new Exception("Unable to generate an unused URI")
		}
		uris << uri
		return uri
	}

	/**
	 * Check if a URI already exists.
	 *
	 * @param uri
	 * 		a uri
	 * @return true iff a URI exists
	 */
	def existsUri(uri) {
		return uris.contains(uri)
	}

	/**
	 * Clear the list of existing URIs
	 */
	def reset() {
		uris.clear()
	}
}
