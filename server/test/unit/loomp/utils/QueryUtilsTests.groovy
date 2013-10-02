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
package loomp.utils

import grails.test.GrailsUnitTestCase

/**
 * Test if converting QueryUtils work.
 */
class QueryUtilsTests extends GrailsUnitTestCase {
	void testCreateOrFilterEqual() {
		final uri = URI.create("http://ex.com")
		assertEquals 'Null list failed', '', QueryUtils.createOrFilterEqual('?v', null)
		assertEquals 'Empty list failed', '', QueryUtils.createOrFilterEqual('?v', [])
		assertEquals 'String entry failed', 'FILTER (?v = "a") .', QueryUtils.createOrFilterEqual('?v', 'a')
		assertEquals 'URI entry failed', "FILTER (?v = <$uri>) .", QueryUtils.createOrFilterEqual('?v', [uri])
		assertEquals 'Single entry failed', 'FILTER (?v = "a") .', QueryUtils.createOrFilterEqual('?v', ['a'])
		assertEquals 'Two entries failed', 'FILTER (?v = "a" || ?v = "b") .', QueryUtils.createOrFilterEqual('?v', ['a', 'b'])
		assertEquals 'Three entries failed', 'FILTER (?v = "a" || ?v = "b" || ?v = "c") .', QueryUtils.createOrFilterEqual('?v', ['a', 'b', 'c'])
	}

	void testSplitSearchString() {
		assertTrue 'Single token failed', QueryUtils.splitSearchString('word').containsAll(['word'])
		assertTrue 'Two tokens failed', QueryUtils.splitSearchString('word1 word2').containsAll(['word1', 'word2'])
		assertTrue 'Three tokens failed', QueryUtils.splitSearchString('word1 word2 word3').containsAll(['word1', 'word2', 'word3'])
		assertTrue 'Single phrase failed', QueryUtils.splitSearchString('"word1 word2"').containsAll(['word1 word2'])
		assertTrue 'Single phrase + token failed', QueryUtils.splitSearchString('"word1 word2" word3').containsAll(['word1 word2', 'word3'])
		assertTrue 'Single phrase + two tokens failed', QueryUtils.splitSearchString('word0 "word1 word2" word3').containsAll(['word0', 'word1 word2', 'word3'])
		assertTrue 'Two phrases failed', QueryUtils.splitSearchString('"word1 word2" "word3 word4"').containsAll(['word1 word2', 'word3 word4'])
	}

	void testCreateGraphPattern() {
		final uri = URI.create("http://ex.com")
		def m = [:]
		assertEquals 'Null list failed', '', QueryUtils.createGraphPattern('?v', null)
		assertEquals 'Empty list failed', '', QueryUtils.createGraphPattern('?v', [:])

		m.put(null, 'a')
		assertEquals 'String entry failed', '?v ?__pred__ ?__var__0 . FILTER (?__var__0 = "a") .\n', QueryUtils.createGraphPattern('?v', m)
		m.clear()
		m.put(URI.create('http://ex.com/p1'), null)
		assertEquals 'String entry failed', '?v <http://ex.com/p1> ?__var__0 . \n', QueryUtils.createGraphPattern('?v', m)

		m.put(URI.create('http://ex.com/p1'), 'a')
		assertEquals 'String entry failed', '?v <http://ex.com/p1> ?__var__0 . FILTER (?__var__0 = "a") .\n',
				QueryUtils.createGraphPattern('?v', m)
		m.put(URI.create('http://ex.com/p1'), ['a'])
		assertEquals 'Single string entry as list failed', '?v <http://ex.com/p1> ?__var__0 . FILTER (?__var__0 = "a") .\n',
				QueryUtils.createGraphPattern('?v', m)
		m.put(URI.create('http://ex.com/p1'), uri)
		assertEquals 'Single uri entry failed', '?v <http://ex.com/p1> ?__var__0 . FILTER (?__var__0 = <http://ex.com>) .\n',
				QueryUtils.createGraphPattern('?v', m)

		m.put(URI.create('http://ex.com/p1'), ['a', 'b'])
		assertEquals 'Two string entries failed', '?v <http://ex.com/p1> ?__var__0 . FILTER (?__var__0 = "a" || ?__var__0 = "b") .\n',
				QueryUtils.createGraphPattern('?v', m)
		m.put(URI.create('http://ex.com/p1'), ['a'])
		m.put(URI.create('http://ex.com/p2'), ['a', 'b'])
		assertEquals 'Two properties failed', '?v <http://ex.com/p1> ?__var__0 . FILTER (?__var__0 = "a") .\n?v <http://ex.com/p2> ?__var__1 . FILTER (?__var__1 = "a" || ?__var__1 = "b") .\n',
				QueryUtils.createGraphPattern('?v', m)
	}
}
