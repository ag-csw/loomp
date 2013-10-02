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
package loomp.oca.client;

import com.google.gwt.i18n.client.Constants;
import com.google.gwt.resources.client.ClientBundle;

/**
 * Interface to represent the style of some UI elements. This is a workaround, because
 * some UI elements set the style in its "style" attribute and cannot be overwritten
 * by CSS.
 */
public interface OneClickAnnotatorStyle extends Constants {

	/**
	 * @return height of a button group of the toolbar
	 */
	@Constants.DefaultIntValue(71)
	int oca_toolbar_btnGrp_height();

	/**
	 * @return width of a button group of the toolbar
	 */
	@Constants.DefaultIntValue(330)
	int oca_toolbar_btnGrp_width();

	/**
	 * @return width of a (annotation) button of the toolbar
	 */
	@Constants.DefaultIntValue(55)
	int oca_toolbar_button_width();

	/**
	 * @return width of a (annotation) button of the toolbar
	 */
	@Constants.DefaultIntValue(44)
	int oca_toolbar_button_height();
}
