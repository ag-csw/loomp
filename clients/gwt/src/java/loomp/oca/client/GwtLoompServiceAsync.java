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

import com.google.gwt.user.client.rpc.AsyncCallback;
import loomp.oca.client.model.Annotation;
import loomp.oca.client.model.AnnotationSet;
import loomp.oca.client.model.BaseEntity;
import loomp.oca.client.model.Resource;

import java.util.List;

public interface GwtLoompServiceAsync {
	void loompApiVersion(AsyncCallback<String> callback);

	void loadAll(String typeUri, AsyncCallback<List<Resource>> callback);

	void load(String uri, AsyncCallback<BaseEntity> callback);

	void searchResources(String query, String typeUri, boolean inclExtern, AsyncCallback<List<Resource>> callback);

	void loadAnnotationSets(String language, AsyncCallback<List<AnnotationSet>> callback);

	void loadAnnotations(String uri, String language, AsyncCallback<List<Annotation>> callback);

	void autoAnnotate(String atext, AsyncCallback<String> callback);

	void save(BaseEntity entity, AsyncCallback<BaseEntity> asyncCallback);

	void saveResource(String uri, String value, Annotation a, String comment, AsyncCallback<String> asyncCallback);

    void containedResources(String uri, AsyncCallback<List<Resource>> callback);


}
