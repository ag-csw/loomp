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
import loomp.utils.RdfaUtils

/**
 * Service for extracting RDFa from content
 */
class RdfaService {
	boolean transactional = false

	/**
	 * Parse a given string to extract RDF statements. The String has to contain
	 * valid xhtml+rdfa.
	 *
	 * @param base
	 * 		base URI of the xhtml document
	 * @param xhtml
	 * 		a string
	 * @return model containing the extracted statements
	 */
	def Model extract(String base, String xhtml) {
		return RdfaUtils.extract(base, xhtml)
	}

	/**
	 * This method realizes the same functionality as #extract(URI, String) but accepts a
	 * fragment of XHTML, e.g., without enclosing tags html and body. The optionally given
	 * prefixes are added to the generated xhtml.
	 *
	 * @param base
	 * 		base URI of the xhtml document
	 * @param xhtml
	 * 		a string
	 * @param prefixes
	 * 		a map defining prefixes (optional)
	 * @return a main memory model containing the extracted statements
	 */
	def Model extractFromXhtmlFragment(String base, String fragment, Map prefixes) {
		return RdfaUtils.extractFromXhtmlFragment(base, fragment, prefixes)
	}
}
