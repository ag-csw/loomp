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

import org.apache.commons.logging.LogFactory

/**
 * Map representing a locale with its value.
 */
class LocaleLiteralMap extends HashMap<String, String> {

	private static def log = LogFactory.getLog(LocaleLiteralMap.class)

	/**
	 * Get a literal in the preferred locale. If there is no such a literal exists or
	 * prefLocale is empty or null then the first literal is returned. If the map is
	 * empty then null is returned.
	 *
	 * @param prefLocale
	 * 		preferred local of the literal
	 * @return a literal
	 */
	public String getAnyLiteral(String prefLocale = null) {
		if (isEmpty())
			return null

		def literal = super.get(prefLocale)
		if (!literal) {
			return values().iterator().next()
		}
	    return literal
	}

	/**
	 * Get the value for a given key. An empty string is returned if the map does not contain the key.
	 * @param key
	 * 		a key
	 * @return the value
	 */
	public String get(String key) {
		if (!this."$key") {
			log.warn "no value entry in LocalLiteral Map for the key $key"
			return ""
		}
		return this."$key"
	}
}
