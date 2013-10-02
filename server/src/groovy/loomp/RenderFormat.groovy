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

/**
 * Available formats to render the result of a request.
 *
 * Note: All values have to be upper case.
 */
public enum RenderFormat {
	JSON (null, "application/json", "json"),
	N3 ("N3", "text/rdf+n3", "n3"),
	RDF_XML ( "RDF/XML", "text/rdf+xml", "rdf"),
	RDF_XML_ABBR ( "RDF/XML-ABBREV", "text/rdf+xml", "rdf")

	RenderFormat(rdfSyntax, contentType, extension) {
		this.rdfSyntax = rdfSyntax
		this.contentType = contentType
		this.extension = extension
	}

	/** The syntax string usable with Jena. If it is not an RDF format then the value is null. */
	String rdfSyntax

	/** content type usable with render */
	String contentType

	/** extension for a file of the type */
	String extension
}
