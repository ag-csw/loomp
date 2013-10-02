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
package loomp.model.db

/**
 * Represents a system parameter for configuring the application.
 */
class SystemParam {
	/** name of the parameter defining the URL to query a SPARQL endpoint */
	final static ENDPOINT_QUERY_URL = "endpoint_query_url"

	/** name of the parameter defining the URL to send updates to a SPARQL endpoint */
	final static ENDPOINT_UPDATE_URL = "endpoint_update_url"

	/**
	 * name of the parameter defining the syntax to query a SPARQL endpoint, e.g.,
	 * to use functions (for example, count(*)) that are not part of SPARQL
	 */
	final static ENDPOINT_SPARQL_SYNTAX = "endpoint_sparql_syntax"

	/** name of the parameter defining the limit of a query result if no limit has been provided */
	final static ENDPOINT_LIMIT = "endpoint_limit"

	/** name of the parameter defining the system wide base URL for generating URIs of resources */
	final static BASE_NS = "base_ns"

	/** name of the parameter defining the system wide base URL for generating versioned URIs of resources */
	final static BASE_NS_VERSION = "base_ns.version"

	/** name of the parameter which indicates if versioning of entities is enabled */
	final static VERSIONING = "versioning"

	/** name of the parameter  */
	String name
	/** value of the parameter  */
	String value

	static constraints = {
		name(nullable: false, blank: false, maxSize: 256)
		value(nullable: true, maxSize: 512)
	}
}
