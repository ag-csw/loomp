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

import com.hp.hpl.jena.rdf.model.Resource

/**
 * Class for creating identifiers of blank nodes that can be used for serializing
 * an RDF model so that the serialization can be used in an SPARQL update query.
 */
class BNodeGenerator {
	def nodeToIdent = [:]

	/**
	 * Returns a completely new identifier of a blank node. @see # getIdentifier(Resource).
	 *
	 * @return an identifier
	 */
	public String getIdentifier() {
		def ident = genIdentifier()
		while (nodeToIdent.containsValue(ident)) {
			// actually this could result in an endless loop but it won't happen :-)
			ident = genIdentifier()
		}
		return ident
	}

	/**
	 * Generate a new blank node identifier of the form _:xxx based on a given node
	 * of the model. The generator assures that for the same given node the same
	 * identifier is returned. The given node has represent a blank node. 
	 *
	 * @param node
	 * 		a blank node (null = a completely new identifier is created)
	 * @return an identifier
	 * @throws IllegalArgumentException if node does not represent a blank node
	 */
	public String getIdentifier(Resource node) {
		if (!node)
			return getIdentifier()
		if (!node.isAnon())
			throw new IllegalArgumentException("parameter node has to represent a blank node")

		if (nodeToIdent.containsKey(node.getId())) {
			return nodeToIdent.get(node.getId())
		} else {
			def ident = getIdentifier()
			nodeToIdent.put(node.getId(), ident)
			return ident
		}
	}

	/**
	 * @return an identifier of a blank node
	 */
	protected def genIdentifier = { return "_:${UUID.randomUUID().toString()}" }
}
