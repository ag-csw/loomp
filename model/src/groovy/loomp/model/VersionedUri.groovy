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

import com.google.gson.annotations.Expose

/**
 * Class representing a URI that carries version information. In its string
 * representation the last numbers of a URI separated by two dashes form the
 * version number of the URI.
 */
class VersionedUri {
	/** versioned URI */
	@Expose URI uri
	/** version number of the URI */ 
	@Expose long version

	def VersionedUri() { }

	/**
	 * Parse a string to obtain a versioned uri
	 * @param s
	 * 		a string
	 */
	def VersionedUri(String s) {
		def parts = s.split("--")
		if (parts.length != 2)
			throw new IllegalArgumentException("Given string $s is not a versioned URI")
		this.uri = URI.create(parts[0])
		this.version = parts[1] as Long
	}

	public String toString() {
		return "$uri--$version"
	}

	boolean equals(o) {
		if (this.is(o)) return true;
		if (!(o instanceof VersionedUri)) return false;

		VersionedUri that = (VersionedUri) o;
		if (version != that.version) return false;
		if (!uri.equals(that.uri)) return false;
		return true;
	}

	int hashCode() {
		int result;

		result = uri.hashCode();
		result = 31 * result + (int) (version ^ (version >>> 32));
		return result;
	}
}
