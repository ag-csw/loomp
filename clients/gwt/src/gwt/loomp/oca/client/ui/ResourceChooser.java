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
package loomp.oca.client.ui;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.data.*;
import com.extjs.gxt.ui.client.event.*;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Format;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.ListView;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ToggleButton;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import loomp.oca.client.GwtLoompServiceAsync;
import loomp.oca.client.OneClickAnnotator;
import loomp.oca.client.OneClickAnnotatorConstants;
import loomp.oca.client.model.Annotation;
import loomp.oca.client.model.Resource;
import loomp.oca.client.utils.UriUtils;

import java.util.ArrayList;
import java.util.List;

/** Container for choosing a resource from a list of resources. */
public class ResourceChooser extends Dialog {
	// Loomp cannot be accessed from here
	public static String RDFS_COMMENT = "http://www.w3.org/2000/01/rdf-schema#comment";

	OneClickAnnotatorConstants constants = GWT.create(OneClickAnnotatorConstants.class);

	private ResourceChooser rsc = this;
	private ContentPanel main = new ContentPanel();

	/** Annotation that has been selected in the editor */
	private Annotation selAnnotation;

	/** Text that has been selected in the editor */
	private String selectedText = "";

	/** Panel containing the details of a resource */
	private ContentPanel details;
	private TextArea description;

	/** View showing the found resources */
	private ListView<BeanModel> resourceList;

	/** Field to search for a resource manually */
	private TextField<String> searchField;

	/** Field to create a new resource to annotate a text */
	private ToggleButton newResourceField;

	public ResourceChooser() {
		setId("resource-chooser-dlg");
		setHeading(constants.oca_resourceChooser_heading());
		setMinWidth(600);
		setMinHeight(400);
		setModal(true);
		setLayout(new BorderLayout());
		setBodyStyle("border: none; background: none");
		setBodyBorder(false);
		setButtons(Dialog.OKCANCEL);
		setHideOnButtonClick(true);
	}

	@Override
	protected void beforeRender() {

	}

	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		GWT.log("RC: Getting loomp service from registry");

		// main panel
		main = new ContentPanel();
		main.setBorders(true);
		main.setBodyBorder(false);
		main.setLayout(new FitLayout());
		main.setHeaderVisible(false);

		initResourceList();
		initDetails();
		initNewResourceField();
		initSearchField();

		BorderLayoutData eastData = new BorderLayoutData(Style.LayoutRegion.EAST, 300);
		eastData.setSplit(true);

		BorderLayoutData centerData = new BorderLayoutData(Style.LayoutRegion.CENTER);
		centerData.setMargins(new Margins(0, 5, 0, 0));

		// user has to select something first
		getButtonById("ok").disable();
		add(details, eastData);
		add(main, centerData);
	}

	// Test
	public class Foo extends BaseModel {
		String u;
		String v;

		public Foo(String u, String v) {
			set("u", u);
			set("v", v);
		}
	}

	// Test
	public void initGrid(Resource resource) {
		List<Foo> foos = new ArrayList<Foo>();

		for (String key : resource.getLiteralProps().keySet()) {
			foos.add(new Foo(key, key));
		}

		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
		ColumnConfig column = new ColumnConfig();
		column.setId("u");
		column.setHeader("U");
		column.setAlignment(Style.HorizontalAlignment.LEFT);
		column.setWidth(120);
		configs.add(column);

		column = new ColumnConfig();
		column.setId("v");
		column.setHeader("V");
		column.setAlignment(Style.HorizontalAlignment.LEFT);
		column.setWidth(180);
		configs.add(column);

		ListStore<Foo> fooStore = new ListStore<Foo>();
		fooStore.add(foos);

		ColumnModel cm = new ColumnModel(configs);
		Grid<Foo> grid = new Grid<Foo>(fooStore, cm);
		grid.setHeight(200);
		details.add(new Text(constants.oca_resourceChooser_information()));
		details.add(grid);
		details.repaint();
	}

	/** Initialize the view showing the resources */
	private void initResourceList() {
		resourceList = new ListView<BeanModel>() {
			@Override
			protected BeanModel prepareData(BeanModel model) {
				Resource resource = model.getBean();
				GWT.log("RC.initView: Processing resource: " + resource.getLabel() + "(" + resource.getUri() + ")");
				final String label = resource.getLabel();
				model.set("label", label != null ? Format.ellipse(resource.getLabel(), 35) : constants.oca_editor_no_label());
				final List<String> comments = resource.getLiteralProps().get(Format.ellipse(RDFS_COMMENT, 50));
				if (comments != null && comments.size() > 0) {
					model.set("comment", comments.get(0));
				}
				return model;
			}
		};

		resourceList.setId("oca-rc-resource-list");
		resourceList.setTemplate(getTemplate());
		resourceList.setBorders(false);
		resourceList.setItemSelector("div.resource");
		resourceList.getSelectionModel().setSelectionMode(Style.SelectionMode.SINGLE);
		resourceList.getSelectionModel().addListener(Events.SelectionChange,
				new Listener<SelectionChangedEvent<BeanModel>>() {
					public void handleEvent(SelectionChangedEvent<BeanModel> be) {
						onSelectionChange(be);
					}
				});
		resourceList.addListener(Events.OnDoubleClick, new Listener<ListViewEvent>() {
			public void handleEvent(ListViewEvent be) {
				fireEvent(Events.Hide, new WindowEvent(rsc, getButtonById("ok")));
				rsc.hide();
			}
		});
		resourceList.setStore(searchResources(selectedText, selAnnotation.getUri(), false));
		main.add(resourceList);
	}

	/** Initialize the button for view the details of a resource */
	private void initDetails() {
		details = new ContentPanel();
		details.setBorders(true);
		details.setBodyBorder(false);
		details.setLayout(new FitLayout());
		details.setHeaderVisible(false);
		details.setTopComponent(new Text(constants.oca_resourceChooser_description()));

		// description
		description = new TextArea();
		description.setEnabled(false);
		description.setStyleAttribute("backgroundColor", "white");
		details.add(description);
	}

	/** Initialize the button for creating a new resource */
	private void initNewResourceField() {
		newResourceField = new ToggleButton(constants.oca_resourceChooser_newResource());
		newResourceField.setHeight(20);
		newResourceField.addListener(Events.OnDoubleClick, new Listener<BoxComponentEvent>() {
			public void handleEvent(BoxComponentEvent be) {
				onSelectionChange(null);
				fireEvent(Events.Hide, new WindowEvent(rsc, getButtonById("ok")));
				rsc.hide();
			}
		});
		newResourceField.addListener(Events.OnClick, new Listener<BoxComponentEvent>() {
			public void handleEvent(BoxComponentEvent be) {
				GWT.log("RC: New resource clicked");
				onSelectionChange(null);
			}
		});
		main.setTopComponent(newResourceField);
	}

	/** Initialize the text field for searching manually */
	public void initSearchField() {
		searchField = new TextField<String>();
		searchField.setEmptyText(constants.oca_resourceChooser_search_caption());
		searchField.setAllowBlank(true);
		searchField.setWidth(200);
		searchField.addListener(Events.OnKeyPress, new Listener<ComponentEvent>() {
			public void handleEvent(ComponentEvent be) {
				if (be.getKeyCode() == (char) KeyCodes.KEY_ENTER) {
					doManualSearch(searchField.getValue());
				}
			}
		});

		Button searchFieldSubmitButton = new Button(constants.oca_resourceChooser_search_button());
		searchFieldSubmitButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent be) {
				doManualSearch(searchField.getValue());
			}
		});

		ToolBar combine = new ToolBar();
		combine.add(searchField);
		combine.add(searchFieldSubmitButton);
		main.setBottomComponent(combine);
	}

	/**
	 * If the user entered a search string then perform the search. If query is empty
	 * then the selected text is searched again.
	 *
	 * @param query
	 * 		string to be searched
	 */
	private void doManualSearch(String query) {
		if (query != null && query.trim().length() > 0) {
			resourceList.setStore(searchResources(query, null, true));
		} else {
			resourceList.setStore(searchResources(selectedText, selAnnotation.getUri(), false));
		}
	}

	/**
	 * Search for the given query in the database.
	 *
	 * @param query
	 * 		a query string
	 * @param auri
	 * 		URI of an annotation in which query is searched for (may be null)
	 * @param inclExternal
	 * 		if set to true external data sources are included in search
	 * @return a list store
	 */
	private ListStore<BeanModel> searchResources(final String query, final String auri, final boolean inclExternal) {
		final GwtLoompServiceAsync loompService = (GwtLoompServiceAsync) Registry.get(OneClickAnnotator.LOOMP_SERVICE);
		RpcProxy<List<Resource>> proxy = new RpcProxy<List<Resource>>() {
			@Override
			protected void load(Object loadConfig, AsyncCallback<List<Resource>> callback) {
				GWT.log("RC: Searching for: " + query);
				// TODO add parameter params.ling to include language of browser language (thus the labels of resources are in the right language)
				loompService.searchResources(query, auri, inclExternal, callback);
			}
		};
		ListLoader<ListLoadResult<BeanModel>> loader = new BaseListLoader<ListLoadResult<BeanModel>>(proxy, new BeanModelReader());
		ListStore<BeanModel> store = new ListStore<BeanModel>(loader);
		loader.load();
		return store;
	}

	/**
	 * Handle a click on the list of resources.
	 *
	 * @param se
	 * 		selection event
	 */
	private void onSelectionChange(SelectionChangedEvent<BeanModel> se) {
		if (se != null && se.getSelection().size() > 0) {
			GWT.log("RC: Selection changed to: " + se.getSelection().toString());
			Resource resource = (Resource) se.getSelectedItem().<ModelData>getBean();
			newResourceField.toggle(false);
			List<String> comments = resource.getLiteralProps().get(RDFS_COMMENT);
			if (comments != null && comments.size() > 0) {
				description.setRawValue(comments.get(0));
			} else {
				StringBuilder comment = new StringBuilder();
				for (String prop : resource.getLiteralProps().keySet()) {
					if (!prop.contains("loomp")) {
						List<String> values = resource.getLiteralProps().get(prop);
						comment.append(UriUtils.localName(prop));
						comment.append(": ");
						for (String value : values) {
							comment.append(value);
							comment.append(", ");
						}
						comment.setLength(comment.length() - 2);
						comment.append("\n");
					}
				}
				description.setRawValue(comment.toString());
			}
			description.setEnabled(false);
			getButtonById("ok").enable();
			initGrid(resource);
		} else {
			GWT.log("RC: Selection changed to new resource");
			newResourceField.toggle(true);
			resourceList.getSelectionModel().deselectAll();
			description.setRawValue("");
			description.setEnabled(true);
			description.focus();
			getButtonById("ok").enable();
		}
	}

	/**
	 * Get the resource that has been selected by the user.
	 *
	 * @return a resource
	 */
	public Resource getSelectedResource() {
		BeanModel model = resourceList.getSelectionModel().getSelectedItem();
		return model != null ? (Resource) model.getBean() : null;
	}

	// TODO change id of div
	private native String getTemplate() /*-{
		return '<tpl for="."><div class="resource" id="{label}"><div class="label">{label}</div><div class="description">{comment}</div></div></tpl>';
	}-*/;


	public native String getDetailTemplate() /*-{
		return '<div class="details"><tpl for="."><div class="resource">{comment}</div></tpl></div>';
	}-*/;

	public Annotation getSelAnnotation() {
		return selAnnotation;
	}

	public void setSelAnnotation(Annotation selAnnotation) {
		this.selAnnotation = selAnnotation;
	}

	public TextArea getDetails() {
		return description;
	}

	public TextField<String> getSearchField() {
		return searchField;
	}

	public String getSelectedText() {
		return selectedText;
	}

	public void setSelectedText(String selectedText) {
		this.selectedText = selectedText;
	}
}
