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

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.event.*;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.RootPanel;
import loomp.oca.client.model.BaseEntity;
import loomp.oca.client.model.ElementText;
import loomp.oca.client.model.Resource;
import loomp.oca.client.ui.OcaEditor;
import loomp.oca.client.ui.Sidebar;
import loomp.oca.client.ui.TitleEditor;
import com.extjs.gxt.ui.client.widget.layout.RowData;

import java.util.List;

/** Entry point classes define <code>onModuleLoad()</code>. */
public class OneClickAnnotator implements EntryPoint {
	private final String ANNOTATION_SET_GROUP_ID = "annotationSetGroup";
	/** registry key of the loomp service */
	public static final String LOOMP_SERVICE = "gwtLoompService";

	/** content panel containing the OCA */
	ContentPanel content;

	/** the text area to edit an Element (see domain model of loomp) */
	OcaEditor editor;

	/** dialog to edit the text */
	TitleEditor titleEditor;

    /** content panel containing buttons*/
    Sidebar sidebar;

	/** This is the entry point method. */
	public void onModuleLoad() {

		registerServices();
		content = new ContentPanel();
		content.setSize(960,500);

		GWT.log("OCA: Header " + content.getHeader().getText());
		content.getHeader().addListener(Events.OnDoubleClick, new Listener<BaseEvent>() {
			public void handleEvent(BaseEvent be) {
				GWT.log("OCA: Showing title editor");
				titleEditor.setElementText(editor.getTextElement());
				titleEditor.show();
			}
		});

		initEditor();
        initSidebar();
		initTitleEditor();

        content.setLayout(new RowLayout(Style.Orientation.HORIZONTAL));
        content.add(editor, new RowData(1, 1, new Margins()));
        content.add(sidebar, new RowData(160, 500, new Margins(80,-1,0,0)));

		RootPanel.get("wrapper").add(content);
		// the iframe is created after the content has been added to the panel
		// TODO remove static application context oca
		editor.addCssFile("/oca/css/loomp_gwt.css");
        editor.setSidebar(sidebar);
	}

	/** Register services needed by this module */
	private void registerServices() {
		String moduleRelativeURL = GWT.getModuleBaseURL() + "rpc";
		GWT.log("OCA: moduleRelativeURL " + moduleRelativeURL);

		GwtLoompServiceAsync loompService = (GwtLoompServiceAsync) GWT.create(GwtLoompService.class);
		GWT.log("OCA: registering loomp service " + (loompService == null ? "FAILED" : "succeeded"));
		if (loompService != null) {
			ServiceDefTarget endpoint = (ServiceDefTarget) loompService;
			endpoint.setServiceEntryPoint(moduleRelativeURL);
			Registry.register(LOOMP_SERVICE, loompService);
		}
	}

	// INIT UI

	/** Initialize the editor for editing the title */
	private void initTitleEditor() {
		titleEditor = new TitleEditor(content.getHeader());
	}

	/** Initialize the text editor for editing text. The member content has to be set in advance. */
	private void initEditor() {
		GWT.log("OCA: Initializing editor");
		editor = new OcaEditor(new ElementText(), content.getHeader());
		editor.setWidth("100%");
		editor.setStyleName("oca-editor");

        // uri of the elementText
		String uri = Window.Location.getParameter("uri");

        if (uri != null) {
			GWT.log("OCA: Loading entity with URI " + uri);
			final GwtLoompServiceAsync loompService = (GwtLoompServiceAsync) Registry.get(OneClickAnnotator.LOOMP_SERVICE);

			loompService.load(uri, new AsyncCallback<BaseEntity>() {
				public void onSuccess(BaseEntity result) {
					if (result instanceof ElementText) {
						GWT.log("OCA: ** entity loaded");
						editor.setTextElement((ElementText) result);
					} else {
						GWT.log("OCA: ** entity loaded, but it has not a type supported by the OCA. Did not change text element");
					}
				}

				public void onFailure(Throwable caught) {
					GWT.log("OCA: Loading entity failed", caught);
				}
			});
		}
	}

    private void initSidebar() {

        sidebar = new Sidebar(editor);

        final GwtLoompServiceAsync loompService = (GwtLoompServiceAsync) Registry.get(OneClickAnnotator.LOOMP_SERVICE);

        // uri of the elementText
		String uri = Window.Location.getParameter("uri");

        // get all entities within the elementText and add them to the sidebar
        loompService.containedResources(uri, new AsyncCallback<List<Resource>>() {
            public void onSuccess(List<Resource> resources) {
                sidebar.init(resources);
            }

            public void onFailure(Throwable throwable) {
                GWT.log("OCA: ** failed to load containedResources in elementText");
            }
        });
    }
}
