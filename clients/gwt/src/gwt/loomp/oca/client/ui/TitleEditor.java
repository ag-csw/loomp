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


import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.Header;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.TextBox;
import loomp.oca.client.model.ElementText;

/**
 * Change the title of the text element. The change is NOT saved.
 *
 * @author rheese
 */
public class TitleEditor extends Dialog {
	/** text element shown in the editor */
	ElementText element;

	/** text field */
	TextBox titleField;

	/** Header component of the editor */
	Header header;

	public TitleEditor(Header header) {
		this.header = header;
		setId("title-editor-dlg");
		setModal(true);
		setLayout(new FitLayout());
		setButtons(Dialog.OKCANCEL);
		setHideOnButtonClick(true);
	}

	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		titleField = new TextBox();
		add(titleField);
	}

	@Override
	protected void afterRender() {
		super.afterRender();
		titleField.setFocus(true);
	}

	@Override
	protected void onButtonPressed(Button button) {
		if (button.getItemId().equals(Dialog.OK)) {
			String title = titleField.getText();
			GWT.log("TE: Setting title of text element to " + title);
			element.setTitle(title);
			// TODO maybe fire an event notifying that the title has changed instead of setting them explicitly
			header.setText(title);
		}
		super.onButtonPressed(button);
	}

	public ElementText getElementText() {
		return element;
	}

	public void setElementText(ElementText element) {
		this.element = element;
		if (element.getTitle() != null) {
			titleField.setValue(element.getTitle());
		}
	}
}
