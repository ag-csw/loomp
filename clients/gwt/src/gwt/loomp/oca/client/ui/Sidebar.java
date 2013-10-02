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

import com.extjs.gxt.ui.client.event.*;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.google.gwt.core.client.GWT;
import loomp.oca.client.model.Resource;

import java.util.ArrayList;
import java.util.List;

/**
 * The sidebar displays all resources, which occur within the text. The 
 * resources are displayed as buttons, which can be clicked to highlight all
 * corresponding atoms.
 * User: Alexa Schlegel (alexa.schlegel@gmail.com)
 * Date: 30.04.12
 */
public class Sidebar extends ContentPanel implements Listener<ButtonEvent>{

    /**
     * This list contains all resources, which occur within the text (HTML
     * editor).
     */
    private List<Resource> resourceList;

    // reference to the editor
    private OcaEditor editor;

    public Sidebar(OcaEditor editor) {

        resourceList        = new ArrayList<Resource>();
        this.editor         = editor;

        this.setHeading("Sidebar");
	}

    /**
     * Initially fills the sidebar with existing resources.
     *
     * @param resourceList  list of all resources occurring in the text of the HTML editor
     */
    public void init(List<Resource> resourceList){
        for(Resource listElem : resourceList){
            addEntry(listElem);
        }
        GWT.log("Sidebar: Added " + resourceList.size() + " new entries.");
    }

    /**
     * Adds an entry to the sidebar.
     *
     * @param resource  resource to add to the sidebar
     */
    public void addEntry(Resource resource){

        GWT.log("Sidebar: ** addEntry " + resource);

        if(!resourceList.contains(resource)){

            SidebarButton button = new SidebarButton(resource);
            button.addListener(Events.Toggle, this);
            this.add(button);
            this.layout(true);
            resourceList.add(resource);
        }
    }

    public void handleEvent(ButtonEvent buttonEvent) {

        boolean toggle          = false;
        SidebarButton button    = (SidebarButton)buttonEvent.getButton();
        Resource resource       = button.getResource();

        if(button.isPressed()){
            toggle = true;
        }

        editor.highlightToggle(toggle, resource);
    }
}
