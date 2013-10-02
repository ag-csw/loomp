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
package loomp.vocabulary

class Loomp {
	/** Loomp Namespace */
	static final String NS = "http://vocab.loomp.org/model/"

	/** Loomp Classes */
	static final URI DomainEntity = resource("DomainEntity")
	static final URI Document = resource("Document")
	static final URI Element = resource("Element")
	static final URI ElementText = resource("ElementText")
	static final URI ElementSparql = resource("ElementSparql")
	// TODO in RDF loomp:User should be a subclass of foaf:Person or something similar
	static final URI User = resource("User")
	static final URI AnnotationSet = resource("AnnotationSet")
	static final URI Annotation = resource("Annotation")
	static final URI Dictionary = resource("Dictionary")
	static final URI LocaleReference = resource("LocaleReference")

	/** Loomp Properties */
	static final URI describes = property("describes")
	static final URI containsResource = property("containsResource")
	static final URI hasElements = property("hasElements")
	static final URI hasRdfaContent = property("hasRDFaContent")
	static final URI hasSparqlQuery = property("hasSparqlContent")
	static final URI endpoint = property("endpoint")
	static final URI template = property("template")
	static final URI refersTo = property("refersTo")
	static final URI hasAnnotations = property("hasAnnotations")
	static final URI annotationProperty = property("annotationProperty")
	static final URI annotationDomain = property("annotationDomain")
	static final URI annotationRange = property("annotationRange")
	static final URI dbVersion = property("dbVersion")


	/** @return a resource having a given URI */
	protected static final URI resource(local) { URI.create(NS + local) }
	/** @return a property having a given URI */
	protected static final URI property(local) { URI.create(NS + local) }
}


