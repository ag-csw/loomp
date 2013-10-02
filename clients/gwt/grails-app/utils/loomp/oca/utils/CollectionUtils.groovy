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
package loomp.oca.utils

/**
 * @author rheese
 */
class CollectionUtils {
	/**
	 * Create a new set that does not contain the given object. If the collection does not
	 * contain the object the collection itself is returned.
	 *
	 * @param c
	 * 		a collection
	 * @param o
	 * 		an object
	 * @return a collection
	 */
	public static Set without(Collection c, Object o) {
		if (!c)
			return Collections.emptySet()
		if (!c.contains(o))
			return c

		if (c.size() == 1) {
			return Collections.emptySet()
		} else {
			def newC = new HashSet(c)
			newC.remove(o)
			return newC
		}
	}

	/**
	 * If o is in c then remove it. If o is not in c then put it into c.
	 *
	 * @param c
	 * 		a collection
	 * @param o
	 * 		an object
	 * @return a collection
	 */
	public static Set toggle(Collection c, Object o) {
		def newC = new HashSet()
		if (!c || !c.contains(o)) {
			newC.add(o);
		} else {
			newC.addAll(c)
			newC.remove(o)
		}
		return newC
	}
}
