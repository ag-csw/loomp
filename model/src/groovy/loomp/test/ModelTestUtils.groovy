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
package loomp.test

import loomp.model.DomainEntity
import loomp.model.Resource
import loomp.utils.JenaUtils
import static junit.framework.Assert.*

/**
 * Testing the model classes
 */

class ModelTestUtils {
	/**
	 * Assert that two instances of a domain class are equal.
	 *
	 * @param orig
	 * 		a instance (reference instances)
	 * @param copy
	 * 		another instance
	 */
	public static assertEquiv(DomainEntity orig, DomainEntity copy) {
		assertNotNull "parameter orig of the entity is null", orig
		assertNotNull "paremeter copy of the entity is null", copy
		assertTrue "parameter orig of the entity is not an instance of DomainEntity", orig instanceof DomainEntity
		assertTrue "paremeter copy of the entity is not an instance of DomainEntity", copy instanceof DomainEntity
		def collNames = JenaUtils.getCollectionFieldNames(orig)
		def llmNames = JenaUtils.getLocaleLiteralMapFieldNames(orig)
		orig.mapping.each { k, pred ->
			if (!k.startsWith("__")) {
				if (llmNames.contains(k)) {
					assertEquals "Map sizes do not match", orig."$k".size(), copy."$k".size()
					orig."$k".each { key, val ->
						assertEquals "Values of $key do not match", val, copy."$k".get(key)
					}
				} else if (!collNames.contains(k) || orig.mapping?.__ordered?.contains(k)) {
					assertEquals "$k not equal.", orig."$k", copy."$k"
				} else {
					// if the value is a Collection or an Array and the member is not ordered we have to compare sets
					assertEquals "$k not equal.", orig."$k" as Set, copy."$k" as Set
				}
			}
		}
	}

	/**
	 * Assert that two list of instances of a domain class contain the same instances in the same order.
	 *
	 * @param orig
	 * 		a list of instances (reference instances)
	 * @param copy
	 * 		another list of instances
	 */
	public static assertEquiv(List<? extends DomainEntity> orig, List<? extends DomainEntity> copy) {
		assertEquals "Sizes of lists do not match", orig.size(), copy.size()
		def oIter = orig.iterator()
		def cIter = copy.iterator()
		while (oIter.hasNext()) {
			assertTrue "URI of orig does not match URI of copy", oIter.next().uri.equals(cIter.next().uri)
		}
	}

	/**
	 * Assert that two list of instances of a domain class contain the same instances in the same order.
	 *
	 * @param orig
	 * 		a list of instances (reference instances)
	 * @param copy
	 * 		another list of instances
	 */
	public static assertEquiv(Set<? extends DomainEntity> orig, Set<? extends DomainEntity> copy) {
		assertEquals "Sizes of lists do not match", orig.size(), copy.size()
		orig.each { origEntity ->
			assertNotNull "Could not find $origEntity.uri in copy set", copy.find { it.uri == origEntity.uri }
		}
	}

	/**
	 * Assert that two instances of Resource are equal.
	 *
	 * @param orig
	 * 		a instance (reference instance)
	 * @param copy
	 * 		another instance
	 */
	public static assertEquiv(Resource orig, Resource copy) {
		assertNotNull "parameter orig of the entity is null", orig
		assertNotNull "paremeter copy of the entity is null", copy
		assertEquals "URIs not euqal", orig.uri, copy.uri
		assertEquals "Property set not equal", orig.properties, copy.properties
	}
}
