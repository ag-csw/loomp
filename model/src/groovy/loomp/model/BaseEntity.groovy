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

import com.hp.hpl.jena.vocabulary.DC
import com.hp.hpl.jena.vocabulary.DCTerms
import com.google.gson.annotations.Expose

/**
 * Base of all entities to ensure that they have a member URI.
 *
 * Note 1: Members that should be serialized as JSON have be marked with @Expose.
 * Note 2: If the super class exposes the same static member then the value of the member of the super will be serialized.  
 */
class BaseEntity {
	/** URI identifying the entity (not mapped) */
	@Expose URI uri

	/** uri of the creator */
	@Expose URI creator

	/** creation date */
	@Expose Date dateCreated

	/** date of last modification */
	@Expose Date lastModified

	/** Provide a mapping between member and RDF. Keys must not start with '__', because these have special meaning.  */
	public static mapping = [
			creator: DC.creator,
			dateCreated: DCTerms.created,
			lastModified: DCTerms.modified
	]
}
