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
package loomp.utils.test

import org.junit.Test
import loomp.test.TestDataGenerator
import loomp.utils.JsonUtils
import static junit.framework.Assert.assertNotNull
import static junit.framework.Assert.assertTrue
import static junit.framework.Assert.assertFalse
import static loomp.test.ModelTestUtils.assertEquiv
import loomp.model.ElementText
import loomp.model.Resource
import loomp.model.Resource
import loomp.utils.JsonUtils
import loomp.utils.JsonUtils
import loomp.model.LocaleLiteralMap
import loomp.model.TypedPropertyValue

/**
 * Test if converting JSON utils work.
 */
class JsonUtilsTests {
	def generator = new TestDataGenerator()

	@Test
	void testFromJsonSingle() {
		def entity = generator.getResource()
		assertNotNull "Entity is null", entity
		def json = JsonUtils.toJson(entity)
		assertNotNull "JSON is null", json
		assertFalse "JSON is empty", json.isEmpty()
		assertEquiv entity, JsonUtils.fromJson(json, Resource.class)
	}

	@Test
	void testFromJsonToResource() {
		def tpv = new TypedPropertyValue(
				property: URI.create("http://example/foo"),
				value: new LocaleLiteralMap(["de":"foo"]), isLiteral: true)
		def entity = generator.getResource(1, 1)
		entity.props << tpv
		assertNotNull "Entity is null", entity
		def json = JsonUtils.toJson(entity)
		assertNotNull "JSON is null", json
		assertFalse "JSON is empty", json.isEmpty()
		assertEquiv entity, JsonUtils.fromJson(json, Resource.class)
	}

	@Test
	void testToJsonSingle() {
		def entity = generator.getElementText()
		assertNotNull "Entity is null", entity
		def json = JsonUtils.toJson(entity)
		assertNotNull "JSON is null", json
		assertFalse "JSON is empty", json.isEmpty()
		assertTrue "JSON does not contain type", json.contains("type")
		assertEquiv entity, JsonUtils.fromJson(json, ElementText.class)
	}

	@Test
	void testToJsonList() {
		def entities = [generator.getElementText()]
		assertNotNull "Entities is null", entities
		assertFalse "Entities is empty", entities.isEmpty()
		def json = JsonUtils.toJson(entities)
		assertNotNull "JSON is null", json
		assertFalse "JSON is empty", json.isEmpty()
		assertTrue "JSON does not contain type", json.contains("type")
		assertEquiv entities, JsonUtils.fromJson(json)
	}
}
