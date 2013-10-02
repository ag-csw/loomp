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

import com.hp.hpl.jena.vocabulary.DC
import grails.test.GrailsUnitTestCase
import loomp.test.TestDataGenerator
import static loomp.test.ModelTestUtils.assertEquiv

/**
 * Testing the class Document
 */
class DocumentTests extends GrailsUnitTestCase {
	def generator = new TestDataGenerator()
	def loompService

	void setUp() {
	}

	void testSaveNextVersion() {
		loompService.clear()
		def doc = generator.getDocument()
		assertNotNull "Save not successful", doc.save()
		[0..15].each {
			doc.incVersion()
			assertNotNull "Save of Version $it not successful", doc.save()
		}
	}

	void testSaveLowerVersion() {
		loompService.clear()
		def doc = generator.getDocument()
		doc.dbVersion = 15
		assertNotNull "Save not successful", doc.save()
		doc.dbVersion = 5
		try {
			assertNotNull "Save not successful", doc.save()
			assert false
		} catch (ConcurrentModificationException e) {
			assert true
		}
	}

	void testSaveSameVersion() {
		loompService.clear()
		def doc = generator.getDocument()
		doc.dbVersion = 15
		assertNotNull "Save not successful", doc.save()
		try {
			assertNotNull "Save not successful", doc.save()
			assert false
		} catch (ConcurrentModificationException e) {
			assert true
		}
	}

	void testSave() {
		loompService.clear()
		def doc = generator.getDocument()
		assertNotNull "Save not successful", doc.save()
	}

	void testSaveNoUri() {
		loompService.clear()
		def doc = generator.getDocument()
		doc.uri = null
		assertNotNull "Save not successful", doc.save()
	}

	void testLoadNoElements() {
		loompService.clear()
		def doc = generator.getDocument(0, 0)
		assertNotNull "Save not successful", doc.save()
		def loaded = Document.load(doc.uri)
		assertEquiv doc, loaded
	}

	void testLoadWithGreater2Elements() {
		loompService.clear()
		def doc = generator.getDocument(2, null)
		assertNotNull "Save not successful", doc.save()
		def loaded = Document.load(doc.uri)
		assertEquiv doc, loaded
	}

	void testLoadAll() {
		final NUM_DOCS = 30

		loompService.clear()
		def docs = generator.getDocuments(NUM_DOCS).sort { it.uri }
		assertEquals docs.size(), NUM_DOCS
		docs.each { assertNotNull "Save not successful", it.save() }

		def loaded = Document.loadAll()
		assertFalse "no documents retrieved", loaded.isEmpty()
		assertEquals "number of loaded documents is different", docs.size(), loaded.size()
		assertEquiv(docs, loaded)
	}

	void testLoadAllLimit() {
		final NUM_DOCS = 10
		final LIMIT = 5

		loompService.clear()
		def docs = generator.getDocuments(NUM_DOCS).sort { it.uri }
		assertEquals docs.size(), NUM_DOCS
		docs.each { assertNotNull "Save not successful", it.save() }

		def params = [limit: LIMIT]
		def loaded = Document.loadAll(params)
		assertFalse "no documents retrieved", loaded.isEmpty()
		assertEquals "number of loaded documents is different", loaded.size(), LIMIT
		assertEquiv(docs.subList(0, LIMIT), loaded)
	}

	void testLoadAllOffset() {
		final NUM_DOCS = 30
		final OFFSET = 5

		loompService.clear()
		def docs = generator.getDocuments(NUM_DOCS).sort { it.uri }
		assertEquals docs.size(), NUM_DOCS
		docs.each { assertNotNull "Save not successful", it.save() }

		def params = [offset: OFFSET]
		def loaded = Document.loadAll(params)
		assertFalse "no documents retrieved", loaded.isEmpty()
		assertEquals "number of loaded documents is different", loaded.size(), (docs.size() - OFFSET)
		assertEquiv(docs.subList(OFFSET, docs.size()), loaded)
	}

	void testLoadAllPaging() {
		final NUM_DOCS = 30
		final PAGE_SIZE = 4

		loompService.clear()
		def docs = generator.getDocuments(NUM_DOCS).sort { it.uri }
		assertEquals docs.size(), NUM_DOCS
		docs.each { assertNotNull "Save not successful", it.save() }

		def params = [offset: 0, limit: PAGE_SIZE]
		def loaded = Document.loadAll(params)
		while (loaded.size() > 0) {
			assertEquiv(docs.subList(params.offset, Math.min(params.offset + PAGE_SIZE, NUM_DOCS)), loaded)
			params.offset += PAGE_SIZE
			loaded = Document.loadAll(params)
		}
	}

	void testLoadAllSorted() {
		final NUM_DOCS = 10

		loompService.clear()
		def docs = generator.getDocuments(NUM_DOCS).unique { it.title }.sort { it.title }
		docs.each { assertNotNull "Save not successful", it.save() }

		def params = [sort: DC.title]
		def loaded = Document.loadAll(params)
		assertFalse "no documents retrieved", loaded.isEmpty()
		assertEquals "number of loaded documents is different", loaded.size(), docs.size()
		assertEquiv(docs, loaded)
	}

	void testCount() {
		final NUM_DOCS = 7

		loompService.clear()
		def docs = generator.getDocuments(NUM_DOCS).sort { it.uri }
		assertEquals docs.size(), NUM_DOCS
		docs.each { assertNotNull "Save not successful", it.save() }

		def count = Document.count()
		assertEquals NUM_DOCS, count
	}
}
