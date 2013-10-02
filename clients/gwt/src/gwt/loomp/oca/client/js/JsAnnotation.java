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
package loomp.oca.client.js;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Class encapsulating an annotation for native JavaScript code.
 *
 * User: heese
 * Date: 28.07.2010
 */
public class JsAnnotation extends JavaScriptObject {
	// GWT requires a protected constructor
	protected JsAnnotation() { }

	/**
	 * @return an instance of JsAnnotation
	 */
	public static native JsAnnotation createObject() /*-{
	  return {};
	}-*/;

	/**
	 * @param uri - the URI of the annotated resource (the subject)
	 */
	public final native void setResourceUri(String uri) /*-{ this.resourceUri = uri; }-*/;

	/**
	 * @return the URI of the annotated resource (the subject)
	 */
	public final native String getResourceUri() /*-{ return this.resourceUri; }-*/;

	/**
	 * @param uri - the URI of the annotated property (the predicate)
	 */
	public final native void setPropertyUri(String uri) /*-{ this.propertyUri = uri; }-*/;

	/**
	 * @return the URI of the annotated property (the predicate)
	 */
	public final native String getPropertyUri() /*-{ return this.propertyUri; }-*/;

    	/**
	 * @param uri - the URI of the annotated property (the predicate)
	 */
	public final native void setDomainUri(String uri) /*-{ this.domainUri = uri; }-*/;

	/**
	 * @return the URI of the annotated property (the predicate)
	 */
	public final native String getDomainUri() /*-{ return this.domainUri; }-*/;
	/**
	 * @return the annotated text (the object)
	 */
	public final native String getAnnotatedText() /*-{ return this.annotatedText; }-*/;
}
