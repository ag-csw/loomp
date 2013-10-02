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
package loomp.oca.client.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Class representing an RDF resource. */
public class Resource extends BaseEntity {
	/** Label used to represent the resource in a UI */
	String label;

	/**
	 * maps including the basic properties of a resource.
	 * The identifier(key of the map) is the link of a declared property,
	 * the value(value of the map) is a list of values for specified property:
	 * Bsp: Resource Merkel: could have following properties:
	 * Map:    key: http://xmlns.com/foaf/0.1/firstName
	 * value: [Merkel; Angela Merkel; A.Merkel; Merkel, Angela... ]
	 *
	 * NOTE: Strings are used instead of URIs for links, because GWT doesn't understand URIs
	 */
	Map<String, List<String>> literalProps;

	/**
	 * basically same functionality like literalProps, except value are links to to external
	 * definitions (i.e. opencalais, wikipedia.. etc.)
	 */
	Map<String, List<String>> uriProps;


	public Resource() {
		literalProps = new HashMap<String, List<String>>();
		uriProps = new HashMap<String, List<String>>();
	}

	public String getLabel() {
		final String labelProp = "http://www.w3.org/2000/01/rdf-schema#label";
		if (label == null && literalProps.containsKey(labelProp)) {
			label = literalProps.get(labelProp).get(0);
		}
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Map<String, List<String>> getLiteralProps() {
		return literalProps;
	}

	public void setLiteralProps(Map<String, List<String>> literalProps) {
		this.literalProps = literalProps;
	}

	public Map<String, List<String>> getUriProps() {
		return uriProps;
	}

	public void setUriProps(Map<String, List<String>> uriProps) {
		this.uriProps = uriProps;
	}

	public List<String> getSpecProperty(String pName, Boolean literal) {
		if (literal) return literalProps.get(pName);
		else return uriProps.get(pName);
	}


	@Override
	public String toString() {
		//TODO: some more meaningful output
		return "Resource{URI=" + getUri() + "}";
	}
}
