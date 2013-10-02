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
package loomp.oca

import grails.test.GrailsUnitTestCase
import loomp.test.TestDataGenerator
import loomp.model.Resource
import loomp.model.TypedPropertyValue

/**
 * @author rheese
 */
class GwtEntityMapperTest extends GrailsUnitTestCase {
	def generator = new TestDataGenerator()
	def mapper = new GwtEntityMapper()

	public void testConvertAnnotation() {
		def entity = generator.getAnnotationSet()
		entity.labels."de" = "foobar"
		entity.comments."de" = "bar foo"
		def converted = mapper.convert(entity)
		assertEquals "label is wrong", entity.labels.de, converted.title
		assertEquals "comment is wrong", entity.comments.de, converted.comment
	}

	public void testConvertResource() {
		def entity = new Resource(uri: "http://example.com".toURI())
		entity.props = [
				new TypedPropertyValue(property: "http://example.com/p".toURI(), value: "foobar", isLiteral: true),
				new TypedPropertyValue(property: "http://example.com/q".toURI(), value: "http://example.com/foo".toURI(), isLiteral: false),
		]
		def converted = mapper.convert(entity)
		assertEquals "literal is missing", converted.literalProps."http://example.com/p", ["foobar"]
		assertEquals "uri is missing", converted.uriProps."http://example.com/q", ["http://example.com/foo"]
	}
}
