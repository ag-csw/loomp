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
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.Header;
import com.extjs.gxt.ui.client.widget.form.HtmlEditor;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.*;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RichTextArea;
import loomp.oca.client.GwtLoompServiceAsync;
import loomp.oca.client.OneClickAnnotator;
import loomp.oca.client.OneClickAnnotatorConstants;
import loomp.oca.client.OneClickAnnotatorMessages;
import loomp.oca.client.model.BaseEntity;
import loomp.oca.client.model.ElementText;
import loomp.oca.client.model.Resource;
import loomp.oca.client.utils.JsUtils;

import java.util.List;

/**
 * Extends HtmlEditor to support adding listener for key and click events to
 * the wrapped text editor. Invoking HtmlEditor#addListener would add the listener
 * to the wrong component.
 *
 * User: heese
 * Date: 29.07.2010
 */
public class OcaEditor extends HtmlEditor {
	/* i18n */
	OneClickAnnotatorConstants constants = GWT.create(OneClickAnnotatorConstants.class);
	OneClickAnnotatorMessages messages = GWT.create(OneClickAnnotatorMessages.class);

	AnnotationSetGroup annotationSetGroup;
	ElementGroup elementGroup;
	SystemGroup systemGroup;

	/** Title bar of the editor */
	Header header;

	/** The URI of the text in the database */
	ElementText textElement;

    /** Reference to the sidebar */
    Sidebar sidebar;

	/**
	 * Create a new editor instance.
	 *
	 * @param textElement
	 * 		element that backing up the content
	 * @param header
	 * 		element that shows the title of the text element
	 */
	public OcaEditor(ElementText textElement, Header header) {
		this.header     = header;
		setTextElement(textElement);
	}

	@Override
	protected void setupToolbar() {
		if (!isShowToolbar())
			return;

		tb                  = new ToolBar();
		annotationSetGroup  = new AnnotationSetGroup(this);
		elementGroup        = new ElementGroup(this);
		systemGroup         = new SystemGroup(this);

		tb.add(elementGroup);
		tb.add(annotationSetGroup);
		tb.add(systemGroup);

		RteListener listener = new RteListener();

		rte.addListener(Events.OnKeyPress, listener);
		rte.addListener(Events.OnClick, listener);
		rte.addListener(Events.OnDoubleClick, listener);
		GWT.log("!");

		Element e = DOM.createDiv();
		e.setClassName("x-html-editor-tb");
		el().insertFirst(e);
		tb.render(e);
	}

	public void addCssFile(String css) {
		/* http://www.sencha.com/forum/showthread.php?69946-Using-styles-in-HTMLEditor */
		Timer t = new CssTimer(css);
		t.schedule(500);
	}

	/** @return the window element containing the editor content */
	public Element getTextEditorWindow() {
		NodeList<Node> nodes = getElement().getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			Node n = nodes.getItem(i);
			if (n.getNodeName().equalsIgnoreCase("iframe"))
				return (Element) n;
		}
		GWT.log("OE: Unable to locate iframe");
		return null;
	}

	@Override
	public RichTextArea.BasicFormatter getBasicFormatter() {
		return null;
	}

	@Override
	public RichTextArea.ExtendedFormatter getExtendedFormatter() {
		return null;
	}

	/** Handle key and mouse events to update the annotation toolbar. */
	public class RteListener implements Listener<ComponentEvent> {

		@Override
		public void handleEvent(ComponentEvent e) {
			EventType type = e.getType();
			int keyCode = -1;
			if (type == Events.OnKeyPress) {
				keyCode = e.getKeyCode();
			}
			if (type == Events.OnDoubleClick) {
				GWT.log("OE.RteListener: Doubleclick");
			}

			//GWT.log("count. "+JsUtils.getSelectedText(getTextEditorWindow()).getSelectedText());
			//GWT.log("compare: "+ (JsUtils.getSelectedText(getTextEditorWindow()).getSelectedText().compareTo("")!=0));
			//GWT.log("event: "+ type);
			//GWT.log("keyCode: "+ keyCode);
			// either mouse button clicked or a cursor key pressed
			if (keyCode == -1 || (keyCode > 37 && keyCode <= 40) || keyCode == 192 || keyCode == 37 || keyCode == 40 || keyCode == 38) {
				GWT.log("OE.RteListener: -1");
				annotationSetGroup.updateSelectedAnnotation();
			}
			if (type == Events.OnKeyPress && JsUtils.getSelectedText(getTextEditorWindow()).getSelectedText().compareTo("") != 0) {
				GWT.log("OE.:RteListener: OnKeyPress");
				annotationSetGroup.updateSelectedAnnotation();
			}
		}
	}

	public ElementText getTextElement() {
		return textElement;
	}

	/**
	 * Set the text element and update the content of the editor pane with the content of the
	 * text element.
	 *
	 * @param textElement
	 * 		a text element
	 */
	public void setTextElement(ElementText textElement) {
		GWT.log("OE: Changing text element to " + textElement.getUri());
		this.textElement = textElement;
		header.setText(textElement.getTitle() != null ? textElement.getTitle() : constants.oca_editor_no_title());
		setContent(textElement.getContent());
	}

	/**
	 * Get the content of the editor pane. The content is surrounded by span containing namespace declarations.
	 *
	 * @return the wrapped content
	 */
	public String getContent() {
		//TODO: because of some reason the text taken from the editor contains an additional <br> at the end
		String text = getRawValue().replaceAll("<br>$", "");
		return NameSpace.wrapText(text.replaceAll("<br[^>]*>", "<br />"));
	}

	/**
	 * Set the content of the editor pane. A surrounding span will be removed.
	 *
	 * @param content
	 * 		the content
	 */
	public void setContent(String content) {
		setRawValue(NameSpace.unwrapText(content));
	}

    public void setSidebar(Sidebar sidebar) {
        this.sidebar = sidebar;
    }

	/** Helper class to include a CSS file into the iframe of the editor */
	class CssTimer extends Timer {
		String css;

		CssTimer(String css) {
			this.css = css;
		}

		@Override
		public void run() {
			Document d = IFrameElement.as(getElement().getElementsByTagName("iframe").getItem(0))
					.getContentDocument();
			LinkElement style = d.createLinkElement();
			style.setType("text/css");
			style.setHref(css);
			style.setRel("stylesheet");
			Element elt = (Element) d.getElementsByTagName("head").getItem(0);
			HeadElement head = HeadElement.as(elt);
			head.insertFirst(style);
		}
	}

    public void highlightToggle(boolean toggle, Resource resource){
        Element elem = getTextEditorWindow();
        JsUtils.toggleCssClass(elem, toggle, resource.getUri());
    }

    /**
     * Adds a new resource to the sidebar. This method is called when a new annotation is created.
     * @param uri uri of the resource
     */
    public void newAnnotationCreated(String uri){

        final GwtLoompServiceAsync loompService = (GwtLoompServiceAsync) Registry.get(OneClickAnnotator.LOOMP_SERVICE);

        loompService.load(uri, new AsyncCallback<BaseEntity>() {

            public void onSuccess(BaseEntity result) {
                GWT.log("OCA: load " + result);
                sidebar.addEntry((Resource)result);
            }

            public void onFailure(Throwable throwable) {
                GWT.log("OCA: ** failed to load entity");
            }
        });
    }
}
