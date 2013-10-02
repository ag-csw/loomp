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
package loomp

import com.hp.hpl.jena.rdf.model.Model
import com.hp.hpl.jena.rdf.model.ModelFactory
import com.hp.hpl.jena.vocabulary.DC
import com.hp.hpl.jena.vocabulary.RDF
import loomp.model.ElementText
import loomp.utils.JenaUtils
import loomp.vocabulary.Loomp
import grails.test.GrailsUnitTestCase
import loomp.test.TestDataGenerator
import loomp.test.TestUriGenerator
import loomp.test.ModelTestUtils
import org.apache.commons.lang.StringUtils

class LoompServiceTests extends GrailsUnitTestCase {
	TestDataGenerator generator = new TestDataGenerator()
	TestUriGenerator uriGen = new TestUriGenerator()

	LoompService loompService

	final BASE_NS = "http://example.com/test"

	void setUp() {
	}

	void testGetType() {
		loompService.clear()
		def s = uriGen.generateUri(BASE_NS)
		def types = []
		for (i in 1..3) { types << uriGen.generateUri(BASE_NS) }

		Model model = ModelFactory.createDefaultModel()
		types.each {
			model.add(model.createResource(s.toString()), RDF.type, model.createResource(it.toString()))
		}
		loompService.insertData(s, model)
		def loadedTypes = loompService.getTypes(s, [:])
		assertEquals "Type lists are not equal", types as Set, loadedTypes as Set
	}

	void testGetEntityType() {
		loompService.clear()
		final entities = [generator.getPerson(), generator.getDocument(), generator.getElementText()]

		entities.each {
			assertNotNull "Save not successful", it.save()
			assertEquals it.type, loompService.getEntityType(it.uri)
		}
	}

	void testGetParentsByListEntry() {
		loompService.clear()
		def entities = generator.getDocuments(5, 3, null)
		entities.each { assertNotNull "Save not successful", it.save() }

		def models = loompService.getParentsByListEntry(entities[0].elements[1], Loomp.hasElements, Loomp.Document, [:])
		def foundEntity = JenaUtils.modelToEntity(models[0])
		ModelTestUtils.assertEquiv(entities[0], foundEntity)
	}

	void testGetByPropertyAndResource() {
		loompService.clear()
		def entities = generator.getElementTexts(5)
		entities.each { assertNotNull "Save not successful", it.save() }

		// Add a resource that is contained in the element
		Model m = ModelFactory.createDefaultModel()
		def s = entities[0].uri
		def o = uriGen.generateUri(BASE_NS)
		m.add(m.createResource(s.toString()), m.createProperty(Loomp.containsResource.toString()), m.createResource(o.toString()))
		loompService.insertData(s, m)

		def models = loompService.getByPropertyAndResource(o, Loomp.containsResource, Loomp.ElementText, [:])
		def foundEntity = JenaUtils.modelToEntity(models[0])
		ModelTestUtils.assertEquiv(entities[0], foundEntity)
	}

	void testSearchResource() {
		loompService.clear()
		def entity = generator.getResource(1, 6)
		assertNotNull "Save not successful", entity.save()

		def tpv = entity.properties.find{ it.isLiteral }
		def tokens = StringUtils.split(tpv.getValue().toString())
		def searchTerm = tokens.length > 2 ? tokens[tokens.length - 2] : tokens[0]
		assertNotNull searchTerm
	}

	void testSearchElementTextContent() {
		loompService.clear()
		def entity = generator.getElementText()
		assertNotNull "Save not successful", entity.save()

		def models = loompService.searchElementText(getSearchString(entity.content), [:])
		def founds = JenaUtils.modelsToEntities(models)
		ModelTestUtils.assertEquiv([entity] as Set, founds as Set)
	}

	void testSearchElementTextContentTwoWords() {
		loompService.clear()
		def entity = generator.getElementText()
		assertNotNull "Save not successful", entity.save()

		def query = "${getSearchString(entity.title)} ${getSearchString(entity.title)}"
		def models = loompService.searchElementText(query, [:])
		def founds = JenaUtils.modelsToEntities(models)
		ModelTestUtils.assertEquiv([entity] as Set, founds as Set)
	}

	void testSearchElementTextContent2Results() {
		loompService.clear()
		def entities = generator.getElementTexts(2)
		entities[1].content = entities[0].content
		entities.each{ assertNotNull "Save not successful", it.save() }

		def models = loompService.searchElementText(getSearchString(entities[0].content), [:])
		def founds = JenaUtils.modelsToEntities(models)
		ModelTestUtils.assertEquiv(entities as Set, founds as Set)
	}

	void testSearchElementTextTitle() {
		loompService.clear()
		def entity = generator.getElementText()
		assertNotNull "Save not successful", entity.save()

		def models = loompService.searchElementText(getSearchString(entity.title), [:])
		def founds = JenaUtils.modelsToEntities(models)
		ModelTestUtils.assertEquiv([entity] as Set, founds as Set)
	}

	void testSearchElementTextTitle2Results() {
		loompService.clear()
		def entities = generator.getElementTexts(2)
		entities[1].title = entities[0].title
		entities.each { assertNotNull "Save not successful", it.save() }

		def models = loompService.searchElementText(getSearchString(entities[0].title), [:])
		def founds = JenaUtils.modelsToEntities(models)
		ModelTestUtils.assertEquiv(entities as Set, founds as Set)
	}

	void testSearchElementTextTitleAndContent2Results() {
		loompService.clear()
		def entities = generator.getElementTexts(2)
		entities[1].content = entities[0].title
		entities.each { assertNotNull "Save not successful", it.save() }

		def models = loompService.searchElementText(getSearchString(entities[0].title), [:])
		def founds = JenaUtils.modelsToEntities(models)
		ModelTestUtils.assertEquiv(entities as Set, founds as Set)
	}

	void testCount() {
		loompService.clear()
		def entities = generator.getElementTexts(5)
		entities.each { assertNotNull "Save not successful", it.save() }
		assertEquals "Count does not match", entities.size(), loompService.countData(ElementText.type)
	}

	void testCountEntriesByParentN() {
		loompService.clear()
		def entity = generator.getDocument(1, null)
		assertNotNull "Save not successful", entity.save()
		assertEquals "Count does not match", entity.elements.size(), loompService.countEntriesByParent(entity.uri, Loomp.hasElements, null)
	}

	void testCountEntriesByParent0() {
		loompService.clear()
		def entity = generator.getDocument(0, 0)
		assertNotNull "Save not successful", entity.save()
		assertEquals "Count does not match", 0, loompService.countEntriesByParent(entity.uri, Loomp.hasElements, null)
	}

	void testGetDataFullOneProperty() {
		loompService.clear()
		def entity = generator.getDocument(2, null)
		assertNotNull "Save doc not successful", entity.save()
		entity.elements.each { assertNotNull "Save elem $it not successful", generator.getElementText(it).save() }
		assertEquals "Count does not match",
				entity.elements.size()+1,
				loompService.getDataFull(entity.uri, Loomp.hasElements, null).size()
	}

	void testGetDataFullTwoProperties() {
		loompService.clear()
		def entity = generator.getDocument(2, null)
		assertNotNull "Save doc not successful", entity.save()
		entity.elements.each { assertNotNull "Save elem $it not successful", generator.getElementText(it).save() }
		assertNotNull "Save creator not successful", generator.getPerson(entity.creator).save()
		assertEquals "Count does not match",
				entity.elements.size()+1+1,
				loompService.getDataFull(entity.uri, [Loomp.hasElements, DC.creator], null).size()
	}

	def random = new Random()

	private String getSearchString(s) {
		def words = s.split()
		return words[random.nextInt(words.size())]
	}
}
