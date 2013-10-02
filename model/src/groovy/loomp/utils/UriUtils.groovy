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

import loomp.model.VersionedUri

/**
 * Utilities for operations on URIs.
 */
class UriUtils {
	/**
	 * Create a versioned URI for a given URI. Use the given namespace 'base' for the versioned
	 * URI. If no 'base' is given then 'uri' is used instead. The ResourceType of uri is kept. If
	 * the give URI has not the expected format then null is returned.
	 *
	 * @param uri
	 * 		a URI
	 * @param base
	 * 		base namespace of the versioned URI
	 * @return versioned Uri
	 */
	static def VersionedUri getVersionedUri(base, uri) {
		if (base) {
			// extract the last two parts of a URI
			// e.g., given 'http://ex.com/data/something' the matcher.group() contains 'data/something'
			def matcher = uri.toString() =~ "/[^/]*/[^/]*\$"
			return matcher.find() ? new VersionedUri(uri: URI.create(base.toString() + matcher.group()), version: 0) : null
		} else {
			return new VersionedUri(uri: uri, version: 0)
		}
	}

	/**
	 * @see #getVersionedUri(Object, Object) 
	 *
	 * @param uri
	 * 		a URI
	 * @param base
	 * 		base namespace of the versioned URI
	 * @return versioned Uri
	 */
	static def VersionedUri getVersionedUri(uri) {
		return getVersionedUri(null, uri)
	}

	/**
	 * Get the local name of the project URI.
	 *
	 * @return local name
	 */
	static def String localName(uri) {
		uri = uri?.toString()
		if (uri == null || uri.trim().length() == 0) {
			return ""
		}

		if (uri.endsWith('/') || uri.endsWith('#')) {
			return uri
		}

		def label = uri.lastIndexOf('#') != -1 ? uri.substring(uri.lastIndexOf('#') + 1) : uri.substring(uri.lastIndexOf('/') + 1)

		// put a space before every capital letter
		return label.replaceAll("(a-z_)([A-ZÜÖÄ])", "\$1 \$2")
	}
}
