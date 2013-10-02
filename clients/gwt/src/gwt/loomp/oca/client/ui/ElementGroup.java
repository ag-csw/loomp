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
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ButtonGroup;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import loomp.oca.client.*;
import loomp.oca.client.model.BaseEntity;
import loomp.oca.client.model.ElementText;

/** Button group providing operations on the loaded element. */
public class ElementGroup extends ButtonGroup {
	/* i18n */
	OneClickAnnotatorConstants constants = GWT.create(OneClickAnnotatorConstants.class);
	OneClickAnnotatorMessages messages = GWT.create(OneClickAnnotatorMessages.class);
	OneClickAnnotatorStyle style = GWT.create(OneClickAnnotatorStyle.class);

	OcaEditor editor;

	public ElementGroup(OcaEditor editor) {
		super(3);
		this.editor = editor;
		setHeight(style.oca_toolbar_btnGrp_height());
		setHeading(constants.oca_elementGroup_heading());
	}

	@Override
	protected void onRender(Element parent, int pos) {
		super.onRender(parent, pos);
		Button btn = new Button(constants.oca_elementGroup_btn_save());
		btn.setId("saveText");
		btn.setScale(Style.ButtonScale.LARGE);
		btn.setWidth(style.oca_toolbar_button_width());
		btn.setHeight(style.oca_toolbar_button_height());
		SelectionListener<ButtonEvent> saveBtnListener = new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent be) {
				if (be.getButton().getId().compareTo("saveText") == 0) {
					saveContent();
				}
			}
		};
		btn.addSelectionListener(saveBtnListener);
		add(btn);
	}

	// Call service to save current content
	private void saveContent() {
		GWT.log("EG: Saving content");
		final GwtLoompServiceAsync loompService = (GwtLoompServiceAsync) Registry.get(OneClickAnnotator.LOOMP_SERVICE);

		editor.getTextElement().setContent(editor.getContent());
		loompService.save(editor.getTextElement(), new AsyncCallback<BaseEntity>() {
			public void onSuccess(BaseEntity entity) {
				MessageBox.info("", constants.oca_dialog_save_success(), null);
				GWT.log("EG: Respond of save is " + entity);
				if (entity != null && entity instanceof ElementText) {
					ElementText element = (ElementText) entity;
					editor.setTextElement(element);
					editor.setContent(element.getContent());
				} else {
					GWT.log("EG: returned entity is null or is not instance of ElementText");
				}
			}

			public void onFailure(Throwable caught) {
				GWT.log("EG: ** The client was unable to save the text ", caught);
				MessageBox.info(constants.oca_dialog_version_heading(), constants.oca_dialog_save_failure(), null);
			}
		});
	}
}
