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

import grails.test.*
import loomp.vocabulary.Loomp

/**
 * Before running these tests a loomp server has to be started and filled with entities.
 */
class LoompServiceTests extends GrailsUnitTestCase {
	void testLoadAll() {
		def loompService = getLoompServiceInstance()
		def entities = loompService.loadAll(Loomp.ElementText.toString())
		assertNotNull "Result is null", entities
		assertTrue "List is empty", entities.size() > 0
	}

	void testLoad() {
		def loompService = getLoompServiceInstance()
		def entities = loompService.loadAll(Loomp.ElementText.toString())
		assertNotNull "Result is null", entities
		assertTrue "List is empty", entities.size() > 0
		def uri = entities.get(0).uri 
		def entity = loompService.load(uri)
		assertNotNull "Result is null", entity
	}

	void testLoadResourcesByLiteral() {
		def loompService = getLoompServiceInstance()
		def entities = loompService.searchResources("e", null)
		assertNotNull "Result is null", entities
		assertTrue "List is empty", entities.size() > 0
	}

	void testListAnnotationSets() {
		def loompService = getLoompServiceInstance()
		def entities = loompService.loadAnnotationSets(null)
		assertNotNull "Result is null", entities
		assertTrue "List is empty", entities.size() > 0
	}


	void testListAnnotations() {
		def loompService = getLoompServiceInstance()
		def entities = loompService.loadAnnotationSets(null)
		assertNotNull "Result is null", entities
		assertTrue "List is empty", entities.size() > 0
		def subEntities = loompService.loadAnnotations(entities[0].uri, null)
		assertNotNull "Result is null", subEntities
		assertTrue "List is empty", subEntities.size() > 0
	}

	LoompService getLoompServiceInstance() {
		mockLogging HttpService
		mockLogging LoompService
		def loompService = new LoompService()
		loompService.httpService = new HttpService()
		return loompService
	}
}
