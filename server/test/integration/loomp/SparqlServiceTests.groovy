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

import com.hp.hpl.jena.query.QuerySolution
import com.hp.hpl.jena.query.ResultSet
import grails.test.GrailsUnitTestCase
import loomp.utils.QueryUtils
import org.apache.commons.codec.net.URLCodec
import loomp.test.TestUriGenerator
import loomp.utils.JenaUtils

class SparqlServiceTests extends GrailsUnitTestCase {
	final SUBJ = "http://ex.com/subj"
	final PRED = "http://ex.com/pred"
	final OBJ = "http://ex.com/obj"
	final LITERALS = [
			"The quick brown fox jumps over the lazy dog",
			"The quick brown fox\"jumps over the lazy dog",
			"The quick brown fox\njumps over the lazy dog",
			"The quick brown fox\tjumps over the lazy dog",
			"The quick brown fox/jumps over the lazy dog"
	]

	def sparqlService

	void setUp() {
	}

	void testClear() {
		assertTrue "Clear failed", sparqlService.runUpdateQuery(getClear())
	}

	void testInsertTriple() {
		sparqlService.runUpdateQuery(getClear())
		assertTrue "Insert failed", sparqlService.runUpdateQuery(getInsertTriple())
	}

	void testInsertManyTriples() {
		sparqlService.runUpdateQuery(getClear())
		def uriGen = new TestUriGenerator()
		[20, 50, 100].each {
			println "Running $it inserts ..."
			for (int i = 0; i < it; i++) {
				def s = uriGen.generateUri("http://ex.com")
				assertTrue "Insert $i failed", sparqlService.runUpdateQuery("INSERT DATA { <$s> <$PRED> <$OBJ> . }")
			}
		}
	}

	void testInsertLiteralsURLEncoded() {
		URLCodec codec = new URLCodec("UTF-8")
		sparqlService.runUpdateQuery(getClear())
		LITERALS.each {
			assertTrue "Insert \"$it\" failed", sparqlService.runUpdateQuery(getInsertLiteral(codec.encode(it)))
		}
	}

	void testInsertLiteralsEscapeJava() {
		sparqlService.runUpdateQuery(getClear())
		LITERALS.each {
			assertTrue "Insert \"$it\" failed", sparqlService.runUpdateQuery(getInsertLiteral(JenaUtils.escapeSparql(it)))
		}
	}

	void testDelete() {
		sparqlService.runUpdateQuery(getClear())
		assertTrue "Insert failed", sparqlService.runUpdateQuery(getInsertTriple())
		assertTrue "Delete failed", sparqlService.runUpdateQuery(getDelete())
	}

	void testSelectAll() {
		sparqlService.runUpdateQuery(getClear())
		// put data into the store
		assertTrue "Insert failed", sparqlService.runUpdateQuery(getInsertTriple())

		// select this data
		ResultSet rs = sparqlService.runSelectQuery(getSelect(), { ResultSet rs ->
			assertTrue "Select failed: result set empty", rs.hasNext()
			QuerySolution qs = rs.next()
			def node = qs.get("s")
			assertTrue "Select failed: subject $node != $SUBJ", node.toString().equals(SUBJ)
			node = qs.get("p")
			assertTrue "Select failed: predicate $node != $PRED", node.toString().equals(PRED)
			node = qs.get("o")
			assertTrue "Select failed: object $node != $OBJ", node.toString().equals(OBJ)
		})
	}

	void testSelectManyTimes() {
		sparqlService.runUpdateQuery(getClear())
		assertTrue "Insert failed", sparqlService.runUpdateQuery(getInsertTriple())
		[20, 50, 100].each {
			println "Running $it ask queries ..."
			for (int i = 0; i < it; i++) {
				assertTrue "Ask $i failed", sparqlService.runAskQuery("ASK WHERE { <$SUBJ> ?p ?o . }")
			}
		}
	}

	final getInsertTriple() { "INSERT DATA { <$SUBJ> <$PRED> <$OBJ> . }" }

	final getInsertLiteral(lit) { "INSERT DATA { <$SUBJ> <$PRED> \"$lit\" . }" }

	final getDelete() { "DELETE DATA { <$SUBJ> <$PRED> <$OBJ> . }" }

	final getSelect() { "SELECT ?s ?p ?o WHERE { ?s ?p ?o . FILTER ( ?s = <$SUBJ> && ?p = <$PRED> && ?o = <$OBJ>) }" }

	final getClear() { "CLEAR" }
}
