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

import com.hp.hpl.jena.rdf.model.Model
import com.hp.hpl.jena.rdf.model.ModelFactory
import loomp.model.DomainEntity
import net.rootdev.javardfa.JenaStatementSink
import net.rootdev.javardfa.ParserFactory
import org.xml.sax.InputSource
import org.xml.sax.XMLReader

/**
 * Utils for processing XHTML+RDFa
 */
class RdfaUtils {
	/**
	 * Process the given RDFa fragment and add the extracted statements to the given
	 * model. Additionally, for each subject a statement is generated that indicates
	 * that it is contained in the given entity.
	 *
	 * @param fragment
	 * 		an XHTML+RDFa fragment
	 * @param entity
	 * 		entity containing the RDFa
	 * @return a model
	 */
	static Model processRdfa(String fragment, DomainEntity entity) {
		// Ee have to create a separate model because the iterator will also return the subjects
		// of newly added statements. Thus, a statement like <uri> <containsResource> <uri> will
		// be generated
		return RdfaUtils.extractFromXhtmlFragment(entity.uri, fragment, null)
	}

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
	static Model extract(base, String xhtml) {
		if (!base)
			throw new NullPointerException("parameter docUri is null")
		if (!xhtml)
			throw new NullPointerException("parameter xhtml is null or empty")

		Model model = ModelFactory.createDefaultModel()
		InputSource is = new InputSource(new StringReader(xhtml))
		// Parsing a string the system ID has to be set explicitly. Otherwise a NullPointerException is thrown by the parser.
		is.setSystemId(base.toString())
		XMLReader reader = ParserFactory.createReaderForFormat(new JenaStatementSink(model), ParserFactory.Format.XHTML)
		reader.parse(is)
		return model
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
	static Model extractFromXhtmlFragment(base, String fragment, Map prefixes) {
		if (!base)
			throw new NullPointerException("parameter base uri is null")
		if (fragment == null)
			throw new NullPointerException("parameter fragment is null")

		def sb = new StringBuilder()
		sb.append '<html xmlns="http://www.w3.org/1999/xhtml/"'
		prefixes.each { k, v -> sb.append " xmlns:$k=\"$v\""}
		sb.append "><head></head>"
		sb.append "<body>$fragment</body>"
		sb.append "</html>"
		return extract(base.toString(), sb.toString())
	}
}
