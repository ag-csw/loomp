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
import com.hp.hpl.jena.rdf.model.Resource
import loomp.utils.BNodeGenerator
import org.junit.Test
import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertTrue

/**
 * Test the functionality of the BNodeGenerator
 */
class BNodeGeneratorTests {
	BNodeGenerator generator = new BNodeGenerator()

	/**
	 *  The identifier has to have the expected syntax
	 */
	@Test
	void testGenerateNull() {
		def ident = generator.getIdentifier()
		def parts = ident.split(":")
		assertTrue parts.length == 2
		assertEquals parts[0], "_"
		junit.framework.Assert.assertTrue UUID.fromString(parts[1]) instanceof UUID
	}

	/**
	 *  For the same node the same identifier has to be returned
	 */
	@Test
	void testGenerateSame() {
		Model m = ModelFactory.createDefaultModel()
		Resource node = m.createResource()
		assertTrue node.isAnon()
		def ident = generator.getIdentifier(node)
		assertEquals generator.getIdentifier(node), ident
	}
}
