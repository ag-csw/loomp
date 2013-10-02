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

import grails.test.*
import com.hp.hpl.jena.rdf.model.Model
import com.hp.hpl.jena.rdf.model.ModelFactory

class RdfaServiceTests extends GrailsUnitTestCase {
	def service
    protected void setUp() {
        service = new RdfaService()
    }

    void testExtract() {
		final base = "http://ex.com"
		XHTML.each { xhtml, n3 ->
			Model expected = ModelFactory.createDefaultModel()
			expected.read(new StringReader(n3), base, "N3")
			Model result = service.extract(base, xhtml)
			assertTrue expected.isIsomorphicWith(result)
		}
    }

	void testExtractFromXhtmlFragment() {
		final base = "http://ex.com"
		FRAGMENTS.each { fragment, n3 ->
			Model expected = ModelFactory.createDefaultModel()
			expected.read(new StringReader(n3), base, "N3")
			Model result = service.extractFromXhtmlFragment(base, fragment[0], fragment[1])
			assertTrue expected.isIsomorphicWith(result)
		}
	}


	//
	// DATA
	//
// map XHTML+RDFa : contained RDF-Statements in N3 format
final XHTML = [
"""<html
    xmlns="http://www.w3.org/1999/xhtml/"
    xmlns:foaf="http://xmlns.com/foaf/0.1/">
    <head></head>
    <body>
        <p about="http://example.com/#me">
            My name is
            <span property="foaf:name">Damian</span>,
            and you can
            <a rel="foaf:mbox" href="mailto:pldms@mac.com">
            email</a> me.
        </p>
    </body>
</html>"""
: """@prefix :        <http://www.w3.org/1999/xhtml/> .
@prefix foaf:    <http://xmlns.com/foaf/0.1/> .
<http://example.com/#me>
      foaf:mbox <mailto:pldms@mac.com> ;
      foaf:name "Damian" ."""
]

// map [XHTML+RDFa fragment, [prefixes]] : contained RDF-Statements in N3 format
final FRAGMENTS = [
["", null] : "",
["""<p about="http://example.com/#me">
	My name is
	<span property="foaf:name">Damian</span>,
	and you can
	<a rel="foaf:mbox" href="mailto:pldms@mac.com">
	email</a> me.
</p>""", ["foaf" : "http://xmlns.com/foaf/0.1/"]]
: """@prefix :        <http://www.w3.org/1999/xhtml/> .
@prefix foaf:    <http://xmlns.com/foaf/0.1/> .
<http://example.com/#me>
      foaf:mbox <mailto:pldms@mac.com> ;
      foaf:name "Damian" ."""
]
}
