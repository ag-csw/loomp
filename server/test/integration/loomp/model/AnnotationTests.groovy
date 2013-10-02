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

import grails.test.GrailsUnitTestCase
import loomp.test.TestDataGenerator
import static loomp.test.ModelTestUtils.assertEquiv

/**
 * Testing the class Element
 */
class AnnotationTests extends GrailsUnitTestCase {
	def generator = new TestDataGenerator()
	def loompService

	void setUp() {
	}

	void testSave() {
		loompService.clear()
		def entity = generator.getAnnotation()
		assertNotNull "Save not successful", entity.save()
	}

	void testSaveNoUri() {
		loompService.clear()
		def entity = generator.getDocument()
		assertNotNull entity
		entity.uri = null
		assertNotNull "Save not successful", entity.save()
	}

	void testLoad() {
		loompService.clear()
		def entity = generator.getAnnotation()
		assertNotNull "Save not successful", entity.save()
		def loaded = Annotation.load(entity.uri)
		assertEquiv entity, loaded
	}

	void testLoadAll() {
		final NUM = 30

		loompService.clear()
		def entities = generator.getAnnotations(NUM).sort { it.uri }
		assertEquals entities.size(), NUM
		entities.each { assertNotNull "Save not successful", it.save() }

		def loaded = Annotation.loadAll()
		assertFalse "no annotations retrieved", loaded.isEmpty()
		assertEquals "number of loaded annotations is different", entities.size(), loaded.size()
		assertEquiv(entities, loaded)
	}

	void testLoadAllLimit() {
		final NUM = 10
		final LIMIT = 5

		loompService.clear()
		def entities = generator.getAnnotations(NUM).sort { it.uri }
		assertEquals entities.size(), NUM
		entities.each { assertNotNull "Save not successful", it.save() }

		def params = [limit: LIMIT]
		def loaded = Annotation.loadAll(params)
		assertFalse "no annotations retrieved", loaded.isEmpty()
		assertEquals "number of loaded annotations is different", loaded.size(), LIMIT
		assertEquiv(entities.subList(0, LIMIT), loaded)
	}

	void testLoadAllOffset() {
		final NUM = 30
		final OFFSET = 5

		loompService.clear()
		def entities = generator.getAnnotations(NUM).sort { it.uri }
		assertEquals entities.size(), NUM
		entities.each { assertNotNull "Save not successful", it.save() }

		def params = [offset: OFFSET]
		def loaded = Annotation.loadAll(params)
		assertFalse "no annotations retrieved", loaded.isEmpty()
		assertEquals "number of loaded annotations is different", loaded.size(), (entities.size() - OFFSET)
		assertEquiv(entities.subList(OFFSET, entities.size()), loaded)
	}

	void testLoadAllPaging() {
		final NUM = 30
		final PAGE_SIZE = 4

		loompService.clear()
		def entities = generator.getAnnotations(NUM).sort { it.uri }
		assertEquals entities.size(), NUM
		entities.each { assertNotNull "Save not successful", it.save() }

		def params = [offset: 0, limit: PAGE_SIZE]
		def loaded = Annotation.loadAll(params)
		while (loaded.size() > 0) {
			assertEquiv(entities.subList(params.offset, Math.min(params.offset + PAGE_SIZE, NUM)), loaded)
			params.offset += PAGE_SIZE
			loaded = Annotation.loadAll(params)
		}
	}

	void testCount() {
		final NUM = 7

		loompService.clear()
		def entities = generator.getAnnotations(NUM).sort { it.uri }
		assertEquals entities.size(), NUM
		entities.each { assertNotNull "Save not successful", it.save() }

		def count = Annotation.count()
		assertEquals NUM, count
	}
}
