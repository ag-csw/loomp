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
package loomp.utils.entityMapper

import loomp.model.BaseEntity
import loomp.model.Resource

/**
 * Interface for converting between client and server models.
 * B = type corresponding to loomp.model.BaseEntity
 * R = type corresponding to loomp.model.Resource
 */
interface EntityMapper<B, R> {
	/**
	 * Convert a base entity to an instance of B.
	 *
	 * @param entity
	 * 		an entity
	 * @return an instance of B
	 */
	public B convert(BaseEntity entity);

	/**
	 * Convert a resource to an instance of R.
	 *
	 * @param entity
	 * 		an entity
	 * @return an instance of R
	 */
	public R convert(Resource resource);

	/**
	 * Convert an instance of R to a base entity.
	 *
	 * @param entity
	 * 		an instance of R
	 * @return a base entity
	 */
	public BaseEntity convert(B entity);

	/**
	 * Convert an instance of R to a resource.
	 *
	 * @param entity
	 * 		an instance of R
	 * @return a resource
	 */
	public Resource convert(R resource);

	/**
	 * Get the language used to extract labels from an entity.
	 */
	public String getLanguage();

	/**
	 * Set the language used to extract literals from an entity.
	 * @param language
	 * 		a language
	 */
	public void setLaunguage(String language);
}
