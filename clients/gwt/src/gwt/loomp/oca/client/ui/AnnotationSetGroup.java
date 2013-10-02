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
import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.BeanModelFactory;
import com.extjs.gxt.ui.client.data.BeanModelLookup;
import com.extjs.gxt.ui.client.event.*;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ButtonGroup;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import loomp.oca.client.*;
import loomp.oca.client.js.JsAnnotation;
import loomp.oca.client.js.JsTextSelection;
import loomp.oca.client.model.Annotation;
import loomp.oca.client.model.AnnotationSet;
import loomp.oca.client.model.Resource;
import loomp.oca.client.utils.JsUtils;
import loomp.oca.client.utils.LocaleUtils;

import java.util.ArrayList;
import java.util.List;

/** Toolbar for selecting the active annotation set and for annotating some text. */
public class AnnotationSetGroup extends ButtonGroup implements Listener<ComponentEvent> {
	static final int VISIBLE_ANNOTATIONS = 6;

	static final String FORWARD_ID = "forward";
	static final String BACKWARD_ID = "backward";
	static final String ANNOTATION_SET_MENU_ID = "annotation-set-menu";

	OneClickAnnotatorConstants constants = GWT.create(OneClickAnnotatorConstants.class);
	OneClickAnnotatorMessages messages = GWT.create(OneClickAnnotatorMessages.class);
	OneClickAnnotatorStyle style = GWT.create(OneClickAnnotatorStyle.class);

	/** the text editor */
	OcaEditor editor;

	/** Combo box for selecting an annotation set */
	ComboBox<BeanModel> annotationSetCombo;

	/** panel of the panel containing the annotations */
	HorizontalPanel btnPanel;

	/** listener for handling clicks on buttons of the annotations */
	SelectionListener<ButtonEvent> annotationBtnListener;

	/** list of the available annotation sets */
	List<AnnotationSet> annotationSets;

	/** reference to the annotation set that is currently selected */
	AnnotationSet selectedAnnotationSet;

	/** reference to the button of the annotation set annotationGroup that has been pressed */
	AnnotationButton selectedAnnotation;

	/** index of the first button which is currently visible of an annotation set */
	int startIndexAnnotation = 0;

	/** dialog to select a resource (existing or new) to use for annotating a text phrase */
	ResourceChooser chooser;

	public AnnotationSetGroup(OcaEditor editor) {
		super(4);
		this.editor = editor;
		setHeading(constants.oca_annotationSetGroup_heading());
		setHeight(style.oca_toolbar_btnGrp_height());
	}

	@Override
	protected void beforeRender() {
		super.beforeRender();
		final GwtLoompServiceAsync loompService = (GwtLoompServiceAsync) Registry.get(OneClickAnnotator.LOOMP_SERVICE);
		final String locale = LocaleUtils.getLocale();
		GWT.log("ASG: Listing annotation sets; using locale " + locale);
		loompService.loadAnnotationSets(locale, new AsyncCallback<List<AnnotationSet>>() {
			public void onSuccess(List<AnnotationSet> result) {
				GWT.log("ASG: Listing annotation sets successful");
				setAnnotationSets(result);
			}

			public void onFailure(Throwable caught) {
				GWT.log("ASG: Listing annotation sets failed", caught);
			}
		});
	}

	@Override
	protected void onRender(Element parent, int pos) {
		super.onRender(parent, pos);
		initAnnotations();
		initAnnotationScrolling();
		initAnnotationSet();
		// comment in if it is working
		initAutoAnnotationButton();
		initResourceChooser();
	}

	// INIT UI


	/**
	 * Create the menu for switching between annotation set. On initialization the first
	 * annotation set is selected.
	 */
	protected void initAnnotationSet() {
		final ListStore<BeanModel> store = new ListStore<BeanModel>();
		BeanModelFactory bmf = BeanModelLookup.get().getFactory(AnnotationSet.class);
		store.add(bmf.createModel(getAnnotationSets()));
		annotationSetCombo = new ComboBox<BeanModel>();
		annotationSetCombo.setId(ANNOTATION_SET_MENU_ID);
		annotationSetCombo.setStore(store);
		annotationSetCombo.setTriggerAction(ComboBox.TriggerAction.ALL);
		annotationSetCombo.setDisplayField("title");
		annotationSetCombo.setAllowBlank(false);
		annotationSetCombo.setEditable(false);
		annotationSetCombo.setForceSelection(true);
		annotationSetCombo.setValue(bmf.createModel(getAnnotationSets().get(0)));
		annotationSetCombo.addSelectionChangedListener(new SelectionChangedListener<BeanModel>() {
			@Override
			public void selectionChanged(SelectionChangedEvent<BeanModel> e) {
				AnnotationSet as = e.getSelectedItem().getBean();
				if (as != selectedAnnotationSet) {
					GWT.log("ASG: Selected annotation set is " + as.getUri());
					setSelectedAnnotationSet(as);
				}
			}
		});
		add(annotationSetCombo);
	}


	/** Create a button for each annotation in the selected annotation set. */
	protected void initAnnotations() {
		annotationBtnListener = new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent be) {
				selectedAnnotation = (AnnotationButton) be.getButton();
				if (selectedAnnotation.isPressed()) {
					Element textElem = editor.getTextEditorWindow();
					JsTextSelection textSelection = JsUtils.getSelectedText(textElem);
					String selectedText = textSelection.getSelectedText();
					if (selectedText != null && selectedText.compareTo("") != 0) {
						GWT.log("ASG: Selected text: " + selectedText);
						initResourceChooser();
						chooser.setSelectedText(selectedText);
						chooser.setSelAnnotation(selectedAnnotation.getAnnotation());
						chooser.show();
					} else {
						// no text has been selected
						selectedAnnotation.toggle(false);
					}
				} else {
					Element textElem = editor.getTextEditorWindow();
					JsTextSelection selText = JsUtils.getSelectedText(textElem);
					GWT.log("ASG: remove annotation from " + selText.getSelectedText());
					JsUtils.updateAnnotation(textElem, selText, null);
				}
			}
		};
		btnPanel = new HorizontalPanel();
		btnPanel.setWidth(style.oca_toolbar_btnGrp_width());
		add(btnPanel);
	}


	/** Buttons for scrolling within an annotation set. */
	protected void initAnnotationScrolling() {
		SelectionListener listener = new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent be) {
				Button btn = be.getButton();
				if (btn.getId().equals(FORWARD_ID)) {
					forward();
				} else if (btn.getId().equals(BACKWARD_ID)) {
					backward();
				}
			}
		};

		VerticalPanel panel = new VerticalPanel();
		Button btn = new Button("&and;");
		btn.setScale(Style.ButtonScale.SMALL);
		btn.setId(BACKWARD_ID);
		btn.addSelectionListener(listener);
		panel.add(btn);

		btn = new Button("&or;");
		btn.setScale(Style.ButtonScale.SMALL);
		btn.setId(FORWARD_ID);
		btn.addSelectionListener(listener);
		panel.add(btn);
		add(panel);
	}

	/** Initialize dialog for choosing a resource to link to. */
	private void initResourceChooser() {
		chooser = new ResourceChooser();
		chooser.addListener(Events.Hide, this);
	}


	/** Create the button for calling the auto annotation */
	protected void initAutoAnnotationButton() {
		Button btn = new Button(constants.oca_annotationSetGroup_btn_auto());
		btn.setId("auto");
		btn.setScale(Style.ButtonScale.LARGE);
		btn.setWidth(style.oca_toolbar_button_width());
		btn.setHeight(style.oca_toolbar_button_height());
		SelectionListener<ButtonEvent> autoBtnListener = new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent be) {
				if (be.getButton().getId().compareTo("auto") == 0) {
					GWT.log("testing real Essence of HTMLEditor: " + editor.getRawValue());
                    GWT.log("testing real Essence of HTMLEditor: " + editor.getRawValue());
					//GWT.log("testing real Essence of HTMLEditor: "+editor);
					//Element textElem = editor.getTextEditorWindow();
                    String seltext= editor.getRawValue();
					//selected text
					//String seltext= JsUtils.getSelectedText(textElem).getSelectedText();
					GWT.log("input text: "+ seltext);
					sendQuestion(seltext);
				}
			}
		};
		btn.addSelectionListener(autoBtnListener);

		add(btn);
	}

	//Call service
	private void sendQuestion(String text) {
		GWT.log("Autoannotator: Sending request");

		String uri = "annotators=openCalais&openCalais.licenseID=3bqn96b5rjfgj4mk9w3bpwzf&text=" + text + "&openCalais.contentType=text/raw&openCalais.allowSearch=true&openCalais.allowDistribution=true";
		GWT.log("OCA: Sending question with URI " + uri);
		final String locale = LocaleInfo.getCurrentLocale().getLocaleName();
		final GwtLoompServiceAsync loompService = (GwtLoompServiceAsync) Registry.get(OneClickAnnotator.LOOMP_SERVICE);
		loompService.autoAnnotate(text, new AsyncCallback<String>() {
			public void onSuccess(String result) {

				editor.setRawValue(result);
				GWT.log("respond:" + result);
			}

			public void onFailure(Throwable caught) {
				GWT.log("ASG: ** Loading autoannotations " + selectedAnnotationSet.getUri() + " failed", caught);
			}
		});
	}

	// UPDATE UI

	/**
	 * Update the text of the button for selecting an annotation set. The annotation
	 * buttons are also created.
	 */
	protected void updateAnnotationSets() {
		if (annotationSets == null || annotationSets.isEmpty()) {
			GWT.log("ASG: Cannot update annotation set: list of annotation sets is null or empty");
			return;
		}

		GWT.log("ASG: Updating  " + annotationSets.size() + " annotations sets");
		ListStore<BeanModel> store = annotationSetCombo.getStore();
		store.removeAll();
		BeanModelFactory bmf = BeanModelLookup.get().getFactory(AnnotationSet.class);
		List<BeanModel> list = bmf.createModel(getAnnotationSets());
		store.add(list);
		setSelectedAnnotationSet(getAnnotationSets().get(0));
	}

	/** Hide/show annotation buttons w.r.t. to #startIndexAnnotation */
	protected void updateAnnotations() {
		if (selectedAnnotationSet == null || selectedAnnotationSet.getAnnotations() == null) {
			GWT.log("ASG: Selected annotation set or its annotation list is null");
			return;
		}

		List<Annotation> annotations = selectedAnnotationSet.getAnnotations();
		GWT.log("ASG: Rendering annotations of annotation set '" + selectedAnnotationSet.getTitle() + "' with " + annotations.size() + " annotations");
		btnPanel.removeAll();
		startIndexAnnotation = annotations.isEmpty() ? -1 : 0;

		Button btn;
		for (Annotation a : annotations) {
			GWT.log("ASG: ** Adding button " + a.getLabel());
			btn = new AnnotationButton(a);
			btn.setWidth(style.oca_toolbar_button_width());
			btn.setHeight(style.oca_toolbar_button_height());
			btn.setScale(Style.ButtonScale.LARGE);
			btn.addSelectionListener(annotationBtnListener);
			btnPanel.add(btn);
		}
		updateVisibleAnnotations();
		layout();
	}

	/** Hide/show annotation buttons w.r.t. to #startIndexAnnotation */
	protected void updateVisibleAnnotations() {
		List<Component> btns = btnPanel.getItems();
		for (int i = 0; i < btns.size(); i++) {
			if (i >= startIndexAnnotation && i < startIndexAnnotation + VISIBLE_ANNOTATIONS) {
				btns.get(i).show();
			} else {
				btns.get(i).hide();
			}
		}
	}

	/**
	 * Update annotation buttons according to the cursor position, e.g. toggle the appropriate
	 * annotation button and make this button visible.
	 */
	protected void updateSelectedAnnotation() {
		Element elem = editor.getTextEditorWindow();
		JsTextSelection selText = JsUtils.getSelectedText(elem);
		JsAnnotation annotation = JsUtils.getAnnotation(elem, selText);
		if (annotation != null) {
			String propertyUri = annotation.getPropertyUri();
			String domainUri = annotation.getDomainUri();
			GWT.log("ASG: Try to change active annotation to " + propertyUri + " & " + domainUri);
			setSelectedAnnotation(propertyUri, domainUri);
		} else {
//			GWT.log("ASG: No annotation is currently active - nothing to do");
			setSelectedAnnotation(null, null);
		}
	}

	/**
	 * Set the selected annotation set.
	 *
	 * @param annotationSet
	 * 		new selected annotation set
	 */
	public void setSelectedAnnotationSet(AnnotationSet annotationSet) {
		if (annotationSet == null) {
			GWT.log("ASG: Selected annotation set is null: nothing to do");
			return;
		}
		if (annotationSet.getUri() == null) {
			GWT.log("ASG: Selected annotation set has no URI and cannot be loaded: nothing to do");
			return;
		}

		selectedAnnotationSet = annotationSet;

		// change the current value of the combobox to display the currently selected annotation set
		AnnotationSet chosen = (AnnotationSet) annotationSetCombo.getValue().getBean();
		if (chosen == null || !annotationSet.equals(chosen)) {
			for (BeanModel bm : annotationSetCombo.getStore().getModels()) {
				if (selectedAnnotationSet.equals(bm.getBean())) {
					annotationSetCombo.setValue(bm);
				}
			}
		}

		// update the annotations
		GWT.log("ASG: Current annotation set is now " + selectedAnnotationSet.getUri());
		if (selectedAnnotationSet.getAnnotations() != null && !selectedAnnotationSet.getAnnotations().isEmpty()) {
			GWT.log("ASG: ** Using cached annotations");
			updateAnnotations();
		} else {
			final String locale = LocaleUtils.getLocale();
			GWT.log("ASG: Caching annotations of all annotation set using locale " + locale);

			final GwtLoompServiceAsync loompService = (GwtLoompServiceAsync) Registry.get(OneClickAnnotator.LOOMP_SERVICE);
			// load all annotation sets so that the toolbar can jump to the current annotation after clicking on an annotated phrase
			for (AnnotationSet set : getAnnotationSets()) {
				GWT.log("ASG: ** Loading annotation set " + set.getUri());
				final AnnotationSet currentSet = set;
				loompService.loadAnnotations(set.getUri(), locale, new AsyncCallback<List<Annotation>>() {
					public void onSuccess(List<Annotation> result) {
						currentSet.setAnnotations(result);
						if (currentSet.getUri().equals(selectedAnnotationSet.getUri())) {
							updateAnnotations();
						}
					}

					public void onFailure(Throwable caught) {
						GWT.log("ASG: ** Loading annotations of annotation set " + selectedAnnotationSet.getUri() + " failed", caught);
					}
				});
			}
		}
	}

	/**
	 * Change the selected annotation to a given property URI. If the property URI is not found in the current
	 * annotation set then it is searched within the other available annotation sets. If the property is not
	 * found then nothing happens.
	 *
	 * @param propertyUri
	 * 		URI of a property
	 * @param domainUri
	 * 		URI of the domain of the property
	 */
	protected void setSelectedAnnotation(String propertyUri, String domainUri) {
		if (selectedAnnotation != null &&
				!(selectedAnnotation.getAnnotation().getPropertyUri().equals(propertyUri) &&
						selectedAnnotation.getAnnotation().getDomainUri().equals(domainUri))) {
			// unset old selected annotation
			selectedAnnotation.toggle(false);
			selectedAnnotation = null;
		}

		if (propertyUri != null) {
			AnnotationButton btn = getAnnotationButton(propertyUri, domainUri);
			if (btn == null) {
				GWT.log("ASG: Current annotation set does not contain requested property");
				// the current annotation set does not contain the requested annotation
				for (AnnotationSet set : getAnnotationSets()) {
					for (Annotation a : set.getAnnotations()) {
						if ((domainUri != null && domainUri.equals(a.getDomainUri()) && propertyUri.equals(a.getPropertyUri())) ||
								propertyUri.equals(a.getPropertyUri())) {
							GWT.log("ASG ** Found annotation in " + set.getUri());
							setSelectedAnnotationSet(set);
							btn = getAnnotationButton(propertyUri, domainUri);
							break;
						}
					}

					if (btn != null)
						break;
				}
			}
			if (btn != null) {
				scrollTo(btn);
				selectedAnnotation = btn;
				selectedAnnotation.toggle(true);
				GWT.log("ASG: Annotation button is pressed " + btn.getAnnotation().getLabel());
			} else {
				GWT.log("ASG: Unknown property URI " + propertyUri);
			}
		}
	}

	/**
	 * Search for a given property URI in the currently selected annotation set. If it cannot
	 * be found then null is returned.
	 *
	 * @param propertyUri
	 * 		URI of a property
	 * @param domainUri
	 * 		URI of the domain of the property
	 * @return corresponding annotation button or null
	 */
	protected AnnotationButton getAnnotationButton(String propertyUri, String domainUri) {
		if (propertyUri == null) throw new NullPointerException("parameter propertyUri is null");
		// content contains an empty attribute 'typeof'
		if (domainUri.trim().length() == 0)
			domainUri = null;

		GWT.log("ASG: Search matching button for " + propertyUri + " & " + domainUri);
		for (Component c : btnPanel.getItems()) {
			if (c instanceof AnnotationButton) {
				AnnotationButton btn = (AnnotationButton) c;
				Annotation annotation = btn.getAnnotation();
				if (annotation == null)
					continue;

				String pu = annotation.getPropertyUri();
				String du = annotation.getDomainUri();
				// domain are both null or they are equal and the property URIs match
				if ((du == domainUri || (du != null && du.equals(domainUri))) && pu.equals(propertyUri)) {
					return btn;
				}
			}
		}
		return null;
	}

	/** Scroll backward within an annotation set. */
	protected void backward() {
		GWT.log("ASG: Scrolling backward");
		startIndexAnnotation -= VISIBLE_ANNOTATIONS;
		if (startIndexAnnotation < 0) {
			startIndexAnnotation = 0;
		} else {
			updateVisibleAnnotations();
		}
	}

	/** Scroll forward within an annotation set. */
	protected void forward() {
		GWT.log("ASG: Scrolling forward");
		startIndexAnnotation += VISIBLE_ANNOTATIONS;
		if (startIndexAnnotation >= btnPanel.getItemCount()) {
			startIndexAnnotation -= VISIBLE_ANNOTATIONS;
		} else {
			updateVisibleAnnotations();
		}
	}

	/**
	 * Scroll to a given annotation button within an annotation set.
	 *
	 * @param btn
	 * 		annotation button to scroll to
	 */
	protected void scrollTo(AnnotationButton btn) {
		if (btn != null) {
			GWT.log("ASG: Scrolling to Annotation " + btn.getAnnotation().getLabel());
			int indexBtn = btnPanel.getItems().indexOf(btn);
			startIndexAnnotation = indexBtn - (indexBtn % VISIBLE_ANNOTATIONS);
			updateVisibleAnnotations();
		} else {
			GWT.log("ASG: Should scroll to a button but given button is null");
		}
	}


	// HANDLE EVENTS

	public void handleEvent(ComponentEvent event) {
		if (event instanceof WindowEvent)
			handleWindowEvent((WindowEvent) event);
		else
			GWT.log("ASG: Unhandled event " + event.getClass().getName());
	}

	protected void handleWindowEvent(WindowEvent be) {
		if (be.getButtonClicked() == chooser.getButtonById("ok")) {
			Resource resource = chooser.getSelectedResource();
			Element textElem = editor.getTextEditorWindow();
			JsTextSelection selText = JsUtils.getSelectedText(textElem);
			JsAnnotation annotation = JsAnnotation.createObject();

            // the uri is final to use it within the AsyncCallback function, when saving the resource
			final String uri;
			if (resource != null) {
				GWT.log("ASG: Selected resource: " + resource.getUri());
				uri = resource.getUri();
			} else {
				GWT.log("ASG: New resource");
				uri = genUri(selText.getSelectedText());
				//sending to server
				GWT.log("ASG: ** Creating new resource on server");
				final GwtLoompServiceAsync loompService = (GwtLoompServiceAsync) Registry.get(OneClickAnnotator.LOOMP_SERVICE);
				loompService.saveResource(uri, selText.getSelectedText(), selectedAnnotation.getAnnotation(), chooser.getDetails().getRawValue(), new AsyncCallback<String>() {
					public void onSuccess(String result) {
						GWT.log("respond:" + result);
					}

					public void onFailure(Throwable caught) {
						GWT.log("ASG: ** Loading autoannotations " + selectedAnnotationSet.getUri() + " failed", caught);
					}
				});
			}

			// after adding an annotation update the sidebar
			editor.newAnnotationCreated(uri);

			annotation.setResourceUri(uri);
			annotation.setPropertyUri(selectedAnnotation.getAnnotation().getPropertyUri());
			annotation.setDomainUri(selectedAnnotation.getAnnotation().getDomainUri());
			JsUtils.updateAnnotation(textElem, selText, annotation);

		} else {
			selectedAnnotation.toggle(false);
		}
	}

	private String genUri(String basetext) {
		//def exists = true
		//def attempts = 0
		String uri = "";
		String resultID = "";
		String validChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";

		int maxIndex = validChars.length();
		java.util.Random rnd = new java.util.Random();

		for (int i = 0; i < 32; i++) {
			int rndPos = Math.abs(rnd.nextInt() % maxIndex);
			resultID += validChars.charAt(rndPos);
		}
		uri = "http://www.loomp.org/dic/pi/0.1/" + resultID;

		return uri;
	}

	// GETTER / SETTER
	public List<AnnotationSet> getAnnotationSets() {
		if (annotationSets == null) {
			annotationSets = new ArrayList<AnnotationSet>();
			AnnotationSet as = new AnnotationSet();
			as.setTitle(constants.error_not_available());
			annotationSets.add(as);
		}
		return annotationSets;
	}

	public void setAnnotationSets(List<AnnotationSet> annotationSets) {
		GWT.log("ASG: New list of annotation sets with size " + annotationSets.size());
		this.annotationSets = annotationSets;
		// ensure that every AnnotationSet has a title (otherwise the form in the OCA is invalid)
		int cnt = 1;
		for (AnnotationSet as : getAnnotationSets()) {
			if (as.getTitle() == null || as.getTitle().isEmpty()) {
				as.setTitle(constants.annotationSet_label() + " " + cnt);
				cnt++;
			}
		}
		updateAnnotationSets();
	}
}
