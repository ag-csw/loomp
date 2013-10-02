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

import grails.test.GrailsUnitTestCase
import loomp.model.ResourceType

class UriServiceTests extends GrailsUnitTestCase {
	final BASE_NS = "http://ex.com"
	final BASE_NS_VERSIONING = "http://ex.com/version"
	def uriService
	def sparqlService
	def loompService

	void setUp() {
		loompService = new LoompService()
		sparqlService = new SparqlService()
		loompService.sparqlService = sparqlService
		uriService = new UriService()
		uriService.loompService = loompService
	}

	void testGenerateUriGivenBase() {
		def uri = uriService.generateUri(BASE_NS)
		assertNotNull "URI is null", uri
		assertTrue "URI does not start with base ns", uri.toString().startsWith(BASE_NS)
	}

	void testGenerateUriGivenBaseAndType() {
		def uri = uriService.generateUri(BASE_NS, ResourceType.DATA)
		assertNotNull "URI is null", uri
		assertTrue "URI does not start with base ns", uri.toString().startsWith(BASE_NS)
	}

	void testExistUri() {
		final s = uriService.generateUri()
		final p = uriService.generateUri()
		final o = uriService.generateUri()

		sparqlService.runUpdateQuery("CLEAR")
		assertFalse "URI not existsUri failed", loompService.existsUri(s)
		sparqlService.runUpdateQuery("INSERT DATA { <$s> <$p> <$o> . }")
		assertTrue "URI existsUri failed", loompService.existsUri(s)
	}
}
