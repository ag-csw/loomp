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
package loomp.model

import com.hp.hpl.jena.vocabulary.RDFS
import loomp.vocabulary.Loomp
import com.google.gson.annotations.Expose

/**
 * An annotation is part of an annotation set and can be used to annotate texts.
 */
class Annotation extends DomainEntity {
	@Expose static URI type = Loomp.Annotation

	/** Labels of the annotation displayed to users <locale, label> */
	@Expose LocaleLiteralMap labels

	/** A description of the annotation displayed to users <locale, label> */
	@Expose LocaleLiteralMap comments

	/** URI of the property that is used to annotate something */
	@Expose URI property

	/**
	 * domain of the property
	 * The domain need not to be the same as defined in the ontology in which
	 * the property is defined. The application domain of annotation set that
	 * this annotation belongs to may allow a more specific domain, e.g., in
	 * the context of person information the domain of foaf:name could be defined
	 * as foaf:Person instead of foaf:Agent. 
	 */
	@Expose URI domain

	/**
	 * range of the domain
	 * This is only useful for properties linking two resources. It can be used
	 * to allow filtering of linking targets.
	 */
	@Expose URI range

	public static mapping = DomainEntity.mapping + [
			labels: RDFS.label,
			comments: RDFS.comment,
			property: Loomp.annotationProperty,
			domain: Loomp.annotationDomain,
			range: Loomp.annotationRange
	]
}
