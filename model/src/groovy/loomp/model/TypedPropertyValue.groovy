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
 * Represents a single resource.
 */
class TypedPropertyValue {
	/** URI of the property*/
	@Expose URI property

	/** value of the property, e.g., a literal or resource URI */
	@Expose Object value

	/** Indicator whether value is a literal (true) or a resource (false) */
	@Expose boolean isLiteral

	boolean equals(o) {
		if (this.is(o)) return true;
		if (getClass() != o.class) return false;

		TypedPropertyValue that = (TypedPropertyValue) o;

		if (isLiteral != that.isLiteral) return false;
		if (property != that.property) return false;
		if (value != that.value) return false;

		return true;
	}

	int hashCode() {
		int result;
		result = property.hashCode();
		result = 31 * result + value.hashCode();
		result = 31 * result + (isLiteral ? 1 : 0);
		return result;
	}
}
