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

import com.hp.hpl.jena.rdf.model.Model
import com.hp.hpl.jena.rdf.model.ModelFactory
import loomp.test.TestDataGenerator
import loomp.utils.JenaUtils
import loomp.utils.JsonUtils
import org.junit.Test
import static junit.framework.Assert.assertNotNull
import static junit.framework.Assert.assertNull
import static junit.framework.Assert.assertEquals
import static loomp.test.ModelTestUtils.assertEquiv
import com.hp.hpl.jena.rdf.model.impl.LiteralImpl
import com.hp.hpl.jena.graph.Node
import com.hp.hpl.jena.rdf.model.ResourceFactory

/**
 * Test if converting models and objects work.
 */
class JenaUtilsTests {
	def generator = new TestDataGenerator()


	@Test
	// Document has random number of Elements
	void testConvertDocument() {
		def doc = generator.getDocument()
		Model model = ModelFactory.createDefaultModel()
		JenaUtils.entityToModel(model, doc)

		def newDoc = JenaUtils.modelToEntity(model)
		assertEquiv(doc, newDoc)
	}

	@Test
	// Document has at least two Elements
	void testConvertDocumentWithGreater1Elements() {
		def doc = generator.getDocument(2, null)
		Model model = ModelFactory.createDefaultModel()
		JenaUtils.entityToModel(model, doc)

		def newDoc = JenaUtils.modelToEntity(model)
		assertEquiv(doc, newDoc)
	}

	@Test
	// Document has no Elements
	void testConvertDocumentNoElements() {
		def doc = generator.getDocument(0, 0)
		Model model = ModelFactory.createDefaultModel()
		JenaUtils.entityToModel(model, doc)

		def newDoc = JenaUtils.modelToEntity(model)
		assertEquiv(doc, newDoc)
	}

	@Test
	void testConvertElement() {
		def elem = generator.getElementText()
		Model model = ModelFactory.createDefaultModel()
		JenaUtils.entityToModel(model, elem)

		def newElem = JenaUtils.modelToEntity(model)
		assertEquiv(elem, newElem)
	}

	@Test
	void testConvertElementWithRdfa() {
		def elem = generator.getElementText()
		elem.content = """<span xmlns:geo="http://www.loomp.org/ontology/geography#">Die <span property="geo:locationName" about="http://www.loomp.org/dic/pi/0.1/HD3WT1DSE4VPIF2DZRBQH9V8V07BDKKK">Grünen</span> stehen in Umfragen glänzend da, Parteivordere denken öffentlich über ein Bündnis mit der Union nach. Aber die <span property="geo:cityName" about="http://www.loomp.org/dic/pi/0.1/HD3WT1DSE4VPIF2DZRBQH9V8V07BDKKK">CDU</span> gibt sich zurückhaltend: Die Schnittmenge mit der Ökopartei sei zu klein, die Debatte über eine <span property="geo:locationName" about="http://www.loomp.org/dic/pi/0.1/HD3WT1DSE4VPIF2DZRBQH9V8V07BDYYY">Koalition</span> "unnütz wie ein Kropf".</span>"""
		Model model = ModelFactory.createDefaultModel()
		JenaUtils.entityToModel(model, elem)
		assertEquals("Number of statements is wrong", 12, model.size())
	}

	@Test
	void testConvertAnnotation() {
		def anno = generator.getAnnotation()
		Model model = ModelFactory.createDefaultModel()
		JenaUtils.entityToModel(model, anno)

		def newAnno = JenaUtils.modelToEntity(model)
		assertEquiv(anno, newAnno)
	}

	@Test
	void testConvertAnnotationSet() {
		def annoSet = generator.getAnnotationSet()
		Model model = ModelFactory.createDefaultModel()
		JenaUtils.entityToModel(model, annoSet)

		def newAnnoSet = JenaUtils.modelToEntity(model)
		assertEquiv(annoSet, newAnnoSet)
	}

	@Test
	void testGetDomainClassInstance() {
		def doc = generator.getDocument()
		Model model = ModelFactory.createDefaultModel()
		JenaUtils.entityToModel(model, doc)
		groovy.util.GroovyTestCase.assertEquals doc.class, JenaUtils.getDomainClassInstance(model).class
	}

	@Test
	void testConvertDocumentJson() {
		def doc = generator.getDocument()
		def json = JsonUtils.toJson(doc)
		assertNotNull json
		def newDoc = JsonUtils.fromJson(json)
		assertEquiv(doc, newDoc)
	}

	@Test
	void testConvertElementTextJson() {
		def elem = generator.getElementText()
		def json = JsonUtils.toJson(elem)
		assertNotNull json
		def newElem = JsonUtils.fromJson(json)
		assertEquiv(elem, newElem)
	}

	@Test
	void testConvertPersonJson() {
		def pers = generator.getPerson()
		def json = JsonUtils.toJson(pers)
		assertNotNull json
		def newPers = JsonUtils.fromJson(json)
		assertEquiv(pers, newPers)
	}

	@Test
	void testConvertResourceWithGreater0Properties() {
		def res = generator.getResource(1, null)
		Model model = ModelFactory.createDefaultModel()
		JenaUtils.resourceToModel(model, res)
		def newRes = JenaUtils.modelToResource(model)
		assertEquiv(res, newRes)
	}

	@Test
	void testConvertResourceNoProperties() {
		def res = generator.getResource(0, 0)
		Model model = ModelFactory.createDefaultModel()
		JenaUtils.resourceToModel(model, res)
		def newRes = JenaUtils.modelToResource(model)
		assertNull "copy is not null", newRes
	}

	@Test
	void testConvertResourceProperties() {
		def res = generator.getResource(0, 0)
		Model model = ModelFactory.createDefaultModel()
		JenaUtils.resourceToModel(model, res)
		def newRes = JenaUtils.modelToResource(model)
		assertNull "copy is not null", newRes
	}

	@Test
	void testConvertIntegerJson() {
		def i = 9
		assertNotNull i.toString(), JsonUtils.toJson(i)
	}

	@Test
	void n2sString() {
		def node = new LiteralImpl(Node.createLiteral("foobar", "de", false), null)
		assertEquals "Strings do not match", "\"foobar\"@de", JenaUtils.n2s(node)
	}

	@Test
	void n2sInt() {
		def node = ResourceFactory.createTypedLiteral(2)
		assertEquals "Strings do not match", "\"2^^http://www.w3.org/2001/XMLSchema#int\"", JenaUtils.n2s(node)
	}
}
