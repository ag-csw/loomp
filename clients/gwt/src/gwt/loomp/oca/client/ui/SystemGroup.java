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
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ButtonGroup;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import loomp.oca.client.*;

/**
 * Button group for providing operations on the system.
 */
public class SystemGroup extends ButtonGroup implements Listener<ButtonEvent> {
	final String VERSION_ID = "version";

	/* i18n */
	OneClickAnnotatorConstants constants = GWT.create(OneClickAnnotatorConstants.class);
	OneClickAnnotatorMessages messages = GWT.create(OneClickAnnotatorMessages.class);
	OneClickAnnotatorStyle style = GWT.create(OneClickAnnotatorStyle.class);

	OcaEditor editor;

	public SystemGroup(OcaEditor editor) {
		super(3);
		this.editor = editor;
		setHeight(style.oca_toolbar_btnGrp_height());
		setHeading(constants.oca_systemGroup_heading());
	}

	@Override
	protected void onRender(Element parent, int pos) {
		super.onRender(parent, pos);
		Button btn = new Button(constants.oca_systemGroup_btn_version());
		btn.setId(VERSION_ID);
		btn.setScale(Style.ButtonScale.LARGE);
		btn.setWidth(style.oca_toolbar_button_width());
		btn.setHeight(style.oca_toolbar_button_height());
		btn.addListener(Events.OnClick, this);
		add(btn);
	}


	public void handleEvent(ButtonEvent be) {
		Button btn = be.getButton();
		if (VERSION_ID.equals(btn.getId())) {
			final GwtLoompServiceAsync loompService = (GwtLoompServiceAsync) Registry.get(OneClickAnnotator.LOOMP_SERVICE);
			loompService.loompApiVersion(new AsyncCallback<String>() {
				public void onSuccess(String result) {
					MessageBox.info(constants.oca_dialog_version_heading(), messages.oca_dialog_version_heading(result), null);
				}

				public void onFailure(Throwable caught) {
				}
			});
		}
	}
}
