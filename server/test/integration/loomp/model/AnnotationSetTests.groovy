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

import com.hp.hpl.jena.vocabulary.RDFS
import grails.test.GrailsUnitTestCase
import loomp.test.TestDataGenerator
import loomp.vocabulary.Loomp
import static loomp.test.ModelTestUtils.assertEquiv

/**
 * Testing the class AnnotationSet
 */

class AnnotationSetTests extends GrailsUnitTestCase {
	def generator = new TestDataGenerator()
	def loompService

	void testSave() {
		loompService.clear()
		def entity = generator.getAnnotationSet()
		assertNotNull entity
		assertNotNull "Save not successful", entity.save()
	}

	void testSaveNoUri() {
		loompService.clear()
		def entity = generator.getAnnotationSet()
		assertNotNull entity
		entity.uri = null
		assertNotNull "Save not successful", entity.save()
	}

	void testLoadNoElements() {
		loompService.clear()
		def entity = generator.getAnnotationSet(0, 0)
		assertNotNull entity
		assertNotNull "Save not successful", entity.save()
		def loaded = AnnotationSet.load(entity.uri)
		assertEquiv entity, loaded
	}

	void testLoadWithGreater2Elements() {
		loompService.clear()
		def entity = generator.getAnnotationSet(2, null)
		assertNotNull entity
		assertNotNull "Save not successful", entity.save()
		def loaded = AnnotationSet.load(entity.uri)
		assertEquiv entity, loaded
	}

	void testLoadAll() {
		final NUM = 30

		loompService.clear()
		def entities = generator.getAnnotationSets(NUM).sort { it.uri }
		assertEquals entities.size(), NUM
		entities.each { assertNotNull "Save not successful", it.save() }

		def loaded = AnnotationSet.loadAll()
		assertFalse "no annotation sets retrieved", loaded.isEmpty()
		assertEquals "number of loaded annotation sets is different", entities.size(), loaded.size()
		assertEquiv(entities, loaded)
	}

	void testLoadAllLimit() {
		final NUM = 10
		final LIMIT = 5

		loompService.clear()
		def entities = generator.getAnnotationSets(NUM).sort { it.uri }
		assertEquals entities.size(), NUM
		entities.each { assertNotNull "Save not successful", it.save() }

		def params = [limit: LIMIT]
		def loaded = AnnotationSet.loadAll(params)
		assertFalse "no annotation sets retrieved", loaded.isEmpty()
		assertEquals "number of loaded annotation sets is different", loaded.size(), LIMIT
		assertEquiv(entities.subList(0, LIMIT), loaded)
	}

	void testLoadAllOffset() {
		final NUM = 30
		final OFFSET = 5

		loompService.clear()
		def entities = generator.getAnnotationSets(NUM).sort { it.uri }
		assertEquals entities.size(), NUM
		entities.each { assertNotNull "Save not successful", it.save() }

		def params = [offset: OFFSET]
		def loaded = AnnotationSet.loadAll(params)
		assertFalse "no annotation sets retrieved", loaded.isEmpty()
		assertEquals "number of loaded annotation sets is different", loaded.size(), (entities.size() - OFFSET)
		assertEquiv(entities.subList(OFFSET, entities.size()), loaded)
	}

	void testLoadAllPaging() {
		final NUM = 30
		final PAGE_SIZE = 4

		loompService.clear()
		def entities = generator.getAnnotationSets(NUM).sort { it.uri }
		assertEquals entities.size(), NUM
		entities.each { assertNotNull "Save not successful", it.save() }

		def params = [offset: 0, limit: PAGE_SIZE]
		def loaded = AnnotationSet.loadAll(params)
		while (loaded.size() > 0) {
			assertEquiv(entities.subList(params.offset, Math.min(params.offset + PAGE_SIZE, NUM)), loaded)
			params.offset += PAGE_SIZE
			loaded = AnnotationSet.loadAll(params)
		}
	}

	void testLoadAllSorted() {
		final NUM = 10

		loompService.clear()
		def entities = generator.getAnnotationSets(NUM).unique { it.labels."en" }.sort { it.labels."en" }
		entities.each { assertNotNull "Save not successful", it.save() }

		def params = [sort: RDFS.label, sortLocale: "en"]
		def loaded = AnnotationSet.loadAll(params)
		assertFalse "no annotation sets retrieved", loaded.isEmpty()
		assertEquals "number of loaded annotation sets is different", loaded.size(), entities.size()
		assertEquiv(entities, loaded)
	}

	void testCount() {
		final NUM = 7

		loompService.clear()
		def entities = generator.getAnnotationSets(NUM).sort { it.uri }
		assertEquals entities.size(), NUM
		entities.each { assertNotNull "Save not successful", it.save() }

		def count = AnnotationSet.count()
		assertEquals NUM, count
	}

	void testLoadAllChilds() {
		loompService.clear()
		def entity = generator.getAnnotationSet(2, null)
		assertNotNull "Save not successful", entity.save()
		entity.annotations.each {
			assertNotNull "Save not successful", generator.getAnnotation(it).save()
		}
		def childs = AnnotationSet.loadAllChilds(entity.uri, "annotations", Annotation.class, [type: Loomp.Annotation], [:])
		assertEquals "Number of children wrong", entity.annotations.size(), childs.size()
		assertTrue "URI of annotations do not match", childs.uri.containsAll(entity.annotations)
	}

	void testLoadAllChildsNoConstraints() {
		loompService.clear()
		def entity = generator.getAnnotationSet(2, null)
		assertNotNull "Save not successful", entity.save()
		entity.annotations.each {
			assertNotNull "Save not successful", generator.getAnnotation(it).save()
		}
		def childs = AnnotationSet.loadAllChilds(entity.uri, "annotations", [:])
		assertEquals "Number of children wrong", entity.annotations.size(), childs.size()
		assertTrue "URI of annotations do not match", childs.uri.containsAll(entity.annotations)
	}

	void testCountChilds() {
		loompService.clear()
		def entity = generator.getAnnotationSet(2, null)
		assertNotNull "Save not successful", entity.save()
		entity.annotations.each {
			assertNotNull "Save not successful", generator.getAnnotation(it).save()
		}
		def count = AnnotationSet.countChilds(entity.uri, "annotations", Annotation.class, [type: Loomp.Annotation])
		assertEquals "Number of children wrong", entity.annotations.size(), count
	}

	void testCountChildsNoConstraints() {
		loompService.clear()
		def entity = generator.getAnnotationSet(2, null)
		assertNotNull "Save not successful", entity.save()
		entity.annotations.each {
			assertNotNull "Save not successful", generator.getAnnotation(it).save()
		}
		def count = AnnotationSet.countChilds(entity.uri, "annotations")
		assertEquals "Number of children wrong", entity.annotations.size(), count
	}
}
