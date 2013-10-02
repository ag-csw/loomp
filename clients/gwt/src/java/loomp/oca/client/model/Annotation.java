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

/**
 * Class representing an annotation instantiated for a given language.
 */
public class Annotation extends BaseEntity {
	/** Label of the annotation displayed to users */
	String label;

	/** A description of the annotation displayed to users */
	String comment;

    /**
	 * Domain of the annotation property.
	 *
	 * The domain need not to be the same as defined in the ontology in which
	 * the property is defined. The application domain of annotation set that
	 * this annotation belongs to may allow a more specific domain, e.g., in
	 * the context of person information the domain of foaf:name could be defined
	 * as foaf:Person instead of foaf:Agent.
	 */
	String domainUri;

	/** Uri of the property used to link the selected text to a resource */
    String propertyUri;

	public Annotation() { }

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getPropertyUri() {
		return propertyUri;
	}

	public void setPropertyUri(String uri) {
		this.propertyUri = uri;
	}

    public String getDomainUri() {
        return domainUri;
    }

    public void setDomainUri(String domainUri) {
        this.domainUri = domainUri;
    }
}
