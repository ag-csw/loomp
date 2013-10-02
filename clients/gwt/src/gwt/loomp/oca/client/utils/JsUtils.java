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
package loomp.oca.client.utils;

import com.google.gwt.user.client.Element;
import loomp.oca.client.js.JsAnnotation;
import loomp.oca.client.js.JsTextSelection;

/**
 * Utilities for doing JS stuff on elements
 *
 * @author Hannes Muehleisen (hannes@muehleisen.org)
 */
public class JsUtils {

	/**
	 * Find annotations present enclosing, inside, or partial inside the
	 * current selection. Will does not work in IE.
	 *
	 * @param elem
	 * 		the iframe element
	 * @param selection
	 * 		a selection object obtained using getSelectedText()
	 * @return a complex JavaScript object containing four map entries: resource,
	 *         property, element and value
	 */
	public static native JsAnnotation getAnnotation(Element elem, JsTextSelection selection) /*-{
		var curAnnot = "";
		var annotSpan = null;
		var searchEle;
		//$wnd.alert("select1") ;
		// check child nodes for nested annots
		var children = selection.range.cloneContents().childNodes;
		for (var i in children) {
			searchEle = children[i];
			if (searchEle.nodeName != undefined && searchEle.nodeName.toLowerCase() == "span" && searchEle.getAttribute('property') != "") {
				// found nested annotation
				curAnnot = searchEle.getAttribute('property');
				annotSpan = searchEle;
				break;
			}
		}
		//$wnd.alert("select2:") ;
		var checkContainers = [selection.range.commonAncestorContainer,selection.range.startContainer,selection.range.endContainer];
		for (i in checkContainers) {
			searchEle = checkContainers[i];
			// check parent nodes for annots
			while (searchEle != undefined && curAnnot == "") {
				if (searchEle.nodeName.toLowerCase() == "span" && searchEle.getAttribute('property') != "") {
					// found parent annotation
					curAnnot = searchEle.getAttribute('property');
					annotSpan = searchEle;
					break;
				}
				searchEle = searchEle.parentNode;
			}
		}
		//$wnd.alert("select3") ;
		if (annotSpan == null) {
			// found no nested or parent annotation
			return null;
		}
		//$wnd.alert("select4") ;
		// create annotation object
		var annotationObject = new Object();
		annotationObject.resourceUri = annotSpan.getAttribute('about');
		annotationObject.propertyUri = annotSpan.getAttribute('property');
		annotationObject.domainUri = annotSpan.getAttribute('typeof');
		annotationObject.element = annotSpan;
		annotationObject.annotatedText = annotSpan.textContent;
		//$wnd.alert("select5") ;
		return annotationObject;
	}-*/;


	/**
	 * Update annotations present within or enclosing the current selection. If you
	 * want to remove an annotation then pass a null reference for the third parameter.
	 *
	 * @param elem
	 * 		the iframe element
	 * @param selection
	 * 		a selection object obtained using getSelectedText()
	 * @param annotation
	 * 		a annotation object obtained by getAnnotation() OR manually
	 * 		constructed. Only the fields "resourceUri" and "propertyUri" are required.
	 */
	public static native void updateAnnotation(Element elem, JsTextSelection selection, JsAnnotation annotation) /*-{

		var currentAnnotation = @loomp.oca.client.utils.JsUtils::getAnnotation(Lcom/google/gwt/user/client/Element;Lloomp/oca/client/js/JsTextSelection;)(elem, selection);

		//klujs: making the annotation not to dissapier
		//if only a part of the annotated text is selected
		//Start:

		// if currAnnotation is null, it means the selection is not inside an Annotation
		if (currentAnnotation != null) {
			//there is no sence to unannotatie some text inside the annotated text,
			//only when if to take out from beginning or end
			//if the selected text is equeal with annotated text then the Annotation has to be taken out
			//wholy. In scope is an other case: when the start position of selection is 0
			//it means the selection is in the beginning of the annotated text

			//$wnd.alert("smth"+currentAnnotation.annotatedText);
			if (currentAnnotation.annotatedText != selection.selectedText && selection.startPos == 0) {
				//$wnd.alert("start position: "+selection.startPos) ;
				//$wnd.alert("is not same") ;

				//inserting the text of the selextion before the annotated text
				currentAnnotation.element.parentNode.insertBefore($doc.createTextNode(selection.selectedText), currentAnnotation.element);
				//deleting the selection
				selection.range.deleteContents();
				return;
			}

			//when the end of the selected text matches with the lenght of
			//the annotated text, it means the selection is in the end of
			//the annotated text
			if (currentAnnotation.annotatedText != selection.selectedText && selection.endPos == currentAnnotation.annotatedText.length) {
				//$wnd.alert("start position: "+selection.startPos) ;

				//inserting the text of the selection before the node,
				//which follows the annotated text. NOTE: the text in the editor
				//ends allways with a brake, which means - the next node will never be null
				currentAnnotation.element.parentNode.insertBefore($doc.createTextNode(selection.selectedText), currentAnnotation.element.nextSibling);
				selection.range.deleteContents();
				return;
			}

			if (annotation == null) { // remove annotation
				currentAnnotation.element.parentNode.replaceChild($doc.createTextNode(currentAnnotation.annotatedText), currentAnnotation.element);
				return;
			}
		}
		//End

		// check if current selection is already annotated, if so, change it
		if (currentAnnotation != null) {
			var ele = currentAnnotation.element;
			ele.setAttribute('about', annotation.resourceUri);
			ele.setAttribute('property', annotation.propertyUri);
			if (annotation.domainUri != null)
				ele.setAttribute('typeof', annotation.domainUri);
			else
				ele.removeAttribute('typeof')
		}
		else { // else create new annotation
			var newAnnotNode = $doc.createElement('span');
			newAnnotNode.setAttribute('about', annotation.resourceUri);
			newAnnotNode.setAttribute('property', annotation.propertyUri);
			if (annotation.domainUri != null)
				newAnnotNode.setAttribute('typeof', annotation.domainUri);
			selection.range.surroundContents(newAnnotNode);
		}
	}-*/;

	/**
	 * Native JavaScript that returns the selected text and position of the start.
	 *
	 * @param elem
	 * 		element to inspect
	 * @return a complex JavaScript object containing four map entries: selectedText, startPos, endPos, length, range and node.
	 */
	public static native JsTextSelection getSelectedText(Element elem) /*-{
		var txt = "";
		var pos = 0;
		var range;
		var parentElement;
		var container;

		// handle various browsers' idea of selection

		if (elem.contentWindow.getSelection()) {
			//klujs:trimming selection
			//start:
			var sel = elem.contentWindow.getSelection();
			//$wnd.alert("selection: -"+sel.toString().substring(sel.toString().length-1,sel.toString().length)+"_");
			if (sel.toString().substring(sel.toString().length - 1, sel.toString().length) == " ") {
				if (sel.anchorNode == sel.focusNode) {
					if (sel.focusOffset > sel.anchorOffset) {
						sel.extend(sel.focusNode, sel.focusOffset - 1);
					}
					else if (sel.focusOffset < sel.anchorOffset) {
						sel.extend(sel.anchorOffset, sel.anchorOffset - 1);
					}
				}
				else if (sel.anchorNode > sel.focusNode) {
					sel.extend(sel.anchorNode, sel.anchorOffset - 1);
				}
				else if (sel.anchorNode < sel.focusNode) {
					sel.extend(sel.focusNode, sel.focusNode - 1);
				}
			}
			//end
			txt = elem.contentWindow.getSelection();
			pos = elem.contentWindow.getSelection().getRangeAt(0).startOffset;
			//$wnd.alert("here1 ");
		} else if (elem.contentWindow.document.getSelection) {
			txt = elem.contentWindow.document.getSelection();
			pos = elem.contentWindow.document.getSelection().getRangeAt(0).startOffset;
			//$wnd.alert("here2 ");
		} else if (elem.contentWindow.document.selection) {
			range = elem.contentWindow.document.selection.createRange();
			txt = range.text;
			parentElement = range.parentElement();
			container = range.duplicate();
			container.moveToElementText(parentElement);
			container.setEndPoint('EndToEnd', range);
			pos = container.text.length - range.text.length;
			//$wnd.alert("here3 ");
		}

		// find range & parent node
		range = txt.getRangeAt ? txt.getRangeAt(0) : txt.createRange();
		var node = range.commonAncestorContainer ? range.commonAncestorContainer : range.parentElement ? range.parentElement() : range.item(0);

		// compile selection object
		var selectionObject = new Object();
		selectionObject.selectedText = "" + txt;
		selectionObject.startPos = pos;
		selectionObject.endPos = pos + selectionObject.selectedText.length;
		selectionObject.length = selectionObject.selectedText.length;
		selectionObject.range = range;
		selectionObject.node = node;

		return selectionObject;
	}-*/;

	/**
	 * Native JavaScript that returns the selected text and position of the start.
	 *
	 * @param elem
	 * 		element to inspect
	 * @return a complex JavaScript object containing four map entries: selectedText, startPos, endPos, length, range and node.
	 */
	public static native void removeSelection(Element elem) /*-{

		if (elem.contentWindow.getSelection()) {
			elem.contentWindow.getSelection().removeAllRanges();
		}
	}-*/;

	/**
	 * Native JavaScript that returns the selected text and position of the start.
	 *
	 * @param elem
	 * 		element to inspect
	 * @return a complex JavaScript object containing four map entries: selectedText, startPos, endPos, length, range and node.
	 */
	public static native JsTextSelection getInnerText(Element elem) /*-{
		var txt = "";
		var pos = 0;
		var range;
		var parentElement;
		var container;

		// handle various browsers' idea of selection
		if (elem.contentWindow.getText()) {
			txt = elem.contentWindow.getSelection();
			pos = elem.contentWindow.getSelection().getRangeAt(0).startOffset;
		} else if (elem.contentWindow.document.getSelection) {
			txt = elem.contentWindow.document.getSelection();
			pos = elem.contentWindow.document.getSelection().getRangeAt(0).startOffset;
		} else if (elem.contentWindow.document.selection) {
			range = elem.contentWindow.document.selection.createRange();
			txt = range.text;
			parentElement = range.parentElement();
			container = range.duplicate();
			container.moveToElementText(parentElement);
			container.setEndPoint('EndToEnd', range);
			pos = container.text.length - range.text.length;
		}

		// find range & parent node
		range = txt.getRangeAt ? txt.getRangeAt(0) : txt.createRange();
		var node = range.commonAncestorContainer ? range.commonAncestorContainer : range.parentElement ? range.parentElement() : range.item(0);

		// compile selection object
		var selectionObject = new Object();
		selectionObject.selectedText = "" + txt;
		selectionObject.startPos = pos;
		selectionObject.endPos = pos + selectionObject.selectedText.length;
		selectionObject.length = selectionObject.selectedText.length;
		selectionObject.range = range;
		selectionObject.node = node;

		return selectionObject;
	}-*/;


	/**
	 * TODO:fast method
	 * Native JavaScript that returns the selected text and position of the start.
	 *
	 * @param elem
	 * 		element to inspect
	 * @return a complex JavaScript object containing four map entries: selectedText, startPos, endPos, length, range and node.
	 */
	public static native JsTextSelection setSelectedText(Element elem, String text) /*-{
		var txt = "";
		var pos = 0;
		var range;
		var parentElement;
		var container;

		// handle various browsers' idea of selection
		if (elem.contentWindow.getSelection) {
			txt = elem.contentWindow.getSelection();
			pos = elem.contentWindow.getSelection().getRangeAt(0).startOffset;
			elem.setClassName("x-html-editor-tb");
		} else if (elem.contentWindow.document.getSelection) {
			txt = elem.contentWindow.document.getSelection();
			pos = elem.contentWindow.document.getSelection().getRangeAt(0).startOffset;
		} else if (elem.contentWindow.document.selection) {
			range = elem.contentWindow.document.selection.createRange();
			txt = range.text;
			parentElement = range.parentElement();
			container = range.duplicate();
			container.moveToElementText(parentElement);
			container.setEndPoint('EndToEnd', range);
			pos = container.text.length - range.text.length;
		}

		// find range & parent node
		range = txt.getRangeAt ? txt.getRangeAt(0) : txt.createRange();
		var node = range.commonAncestorContainer ? range.commonAncestorContainer : range.parentElement ? range.parentElement() : range.item(0);

		// compile selection object
		var selectionObject = new Object();
		selectionObject.selectedText = "" + txt;
		selectionObject.startPos = pos;
		selectionObject.endPos = pos + selectionObject.selectedText.length;
		selectionObject.length = selectionObject.selectedText.length;
		selectionObject.range = range;
		selectionObject.node = node;

		return selectionObject;
	}-*/;

    /**
     * Toggles the css class at span elements with a certain uri. The css attribute "about" needs to equal the given
     * uri.
     */
    public static native void toggleCssClass(Element elem, boolean toggle, String uri)/*-{
        var spans = elem.contentWindow.document.getElementsByTagName("span");
        for (i in spans){
            if((spans[i].getAttribute("about")) == uri){

                if(toggle == true){
                spans[i].setAttribute("class", "highlighted");
                }
                else{
                    spans[i].removeAttribute("class");
                }
            }
        }
    }-*/;
}
