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
class RDFS {
	public static final java.lang.String NAMESPACE = "http://www.w3.org/2000/01/rdf-schema#"

	public static final URI RESOURCE = "${NAMESPACE}Resource".toURI()
	public static final URI LITERAL = "${NAMESPACE}Literal".toURI()
	public static final URI CLASS = "${NAMESPACE}Class".toURI()
	public static final URI SUBCLASSOF = "${NAMESPACE}subClassOf".toURI()
	public static final URI SUBPROPERTYOF = "${NAMESPACE}subPropertyOf".toURI()
	public static final URI DOMAIN = "${NAMESPACE}domain".toURI()
	public static final URI RANGE = "${NAMESPACE}range".toURI()
	public static final URI COMMENT = "${NAMESPACE}comment".toURI()
	public static final URI LABEL = "${NAMESPACE}label".toURI()
	public static final URI DATATYPE = "${NAMESPACE}Datatype".toURI()
	public static final URI CONTAINER = "${NAMESPACE}Container".toURI()
	public static final URI MEMBER = "${NAMESPACE}member".toURI()
	public static final URI ISDEFINEDBY = "${NAMESPACE}isDefinedBy".toURI()
	public static final URI SEEALSO = "${NAMESPACE}seeAlso".toURI()
	public static final URI CONTAINERMEMBERSHIPPROPERTY = "${NAMESPACE}ContainerMembershipProperty".toURI()
}
