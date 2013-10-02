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

import com.google.gwt.user.client.rpc.RemoteService;
import loomp.oca.client.model.Annotation;
import loomp.oca.client.model.AnnotationSet;
import loomp.oca.client.model.BaseEntity;
import loomp.oca.client.model.Resource;

import java.util.List;

public interface GwtLoompService extends RemoteService {
	String loompApiVersion();

	List<BaseEntity> loadAll(String typeUri);

	BaseEntity load(String uri);

	List<Resource> searchResources(String query, String typeUri, boolean inclExtern);

	List<AnnotationSet> loadAnnotationSets(String language);

	List<Annotation> loadAnnotations(String uri, String language);

	String autoAnnotate(String atext);

	BaseEntity save(BaseEntity entity);

	String saveResource(String uri, String value, Annotation a, String comment);

    List<Resource> containedResources(String uri);
}
