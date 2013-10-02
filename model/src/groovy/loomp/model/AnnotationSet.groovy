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

import loomp.vocabulary.Loomp
import com.hp.hpl.jena.vocabulary.RDFS
import com.google.gson.annotations.Expose

/**
 * An annotation set represents a set of properties that can be used to annotate
 * text or to link resources.
 */
class AnnotationSet extends DomainEntity {
	@Expose static URI type = Loomp.AnnotationSet

	/** labels of the annotation set <locale, label> */
	@Expose LocaleLiteralMap labels

	/** A description of the annotation set displayed to users */
	@Expose LocaleLiteralMap comments

	/** Annotations belonging to the annotation set */
	@Expose List<URI> annotations

	public static mapping = DomainEntity.mapping + [
			labels : RDFS.label,
			comments: RDFS.comment,
			annotations : Loomp.hasAnnotations,
			// special key listing all ordered member of a class; they will be stored as a list in RDF
			__ordered : ["annotations"]
	]
}
