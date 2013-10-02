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

/**
 * User: rheese
 */
class RDF {
	public static final java.lang.String NAMESPACE = "http://www.w3.org/1999/02/22-rdf-syntax-ns#"
	
	public static final URI TYPE = "${NAMESPACE}type".toURI()
	public static final URI PROPERTY = "${NAMESPACE}Property".toURI()
	public static final URI XMLLITERAL = "${NAMESPACE}XMLLiteral".toURI()
	public static final URI SUBJECT = "${NAMESPACE}Subject".toURI()
	public static final URI PREDICATE = "${NAMESPACE}Predicate".toURI()
	public static final URI OBJECT = "${NAMESPACE}Object".toURI()
	public static final URI STATEMENT = "${NAMESPACE}Statement".toURI()
	public static final URI BAG = "${NAMESPACE}Bag".toURI()
	public static final URI ALT = "${NAMESPACE}Alt".toURI()
	public static final URI SEQ = "${NAMESPACE}Seq".toURI()
	public static final URI VALUE = "${NAMESPACE}value".toURI()
	public static final URI LI = "${NAMESPACE}li".toURI()
	public static final URI LIST = "${NAMESPACE}List".toURI()
	public static final URI FIRST = "${NAMESPACE}first".toURI()
	public static final URI REST = "${NAMESPACE}rest".toURI()
	public static final URI NIL = "${NAMESPACE}nil".toURI()
}
