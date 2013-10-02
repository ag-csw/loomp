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

/**
 * Interface to represent the constants contained in resource bundle:
 * 	'L:/projects/csw/loomp/gwt/src/gwt/loomp/oca/client/OneClickAnnotatorConstants.properties'.
 */
public interface OneClickAnnotatorConstants extends com.google.gwt.i18n.client.Constants {
  
  /**
   * Translated "annotation set".
   * 
   * @return translated "annotation set"
   */
  @DefaultStringValue("annotation set")
  @Key("annotationSet.label")
  String annotationSet_label();

  /**
   * Translated "annotation sets".
   * 
   * @return translated "annotation sets"
   */
  @DefaultStringValue("annotation sets")
  @Key("entities.label")
  String entities_label();

  /**
   * Translated "An error occured".
   * 
   * @return translated "An error occured"
   */
  @DefaultStringValue("An error occured")
  @Key("error.heading")
  String error_heading();

  /**
   * Translated "N/A".
   * 
   * @return translated "N/A"
   */
  @DefaultStringValue("N/A")
  @Key("error.not_available")
  String error_not_available();

  /**
   * Translated "Auto".
   * 
   * @return translated "Auto"
   */
  @DefaultStringValue("Auto")
  @Key("oca.annotationSetGroup.btn.auto")
  String oca_annotationSetGroup_btn_auto();

  /**
   * Translated "Annotations".
   * 
   * @return translated "Annotations"
   */
  @DefaultStringValue("Annotations")
  @Key("oca.annotationSetGroup.heading")
  String oca_annotationSetGroup_heading();

  /**
   * Translated "An error occurred while saving content".
   * 
   * @return translated "An error occurred while saving content"
   */
  @DefaultStringValue("An error occurred while saving content")
  @Key("oca.dialog.save.failure")
  String oca_dialog_save_failure();

  /**
   * Translated "Content has been saved".
   * 
   * @return translated "Content has been saved"
   */
  @DefaultStringValue("Content has been saved")
  @Key("oca.dialog.save.success")
  String oca_dialog_save_success();

  /**
   * Translated "Version of loomp API".
   * 
   * @return translated "Version of loomp API"
   */
  @DefaultStringValue("Version of loomp API")
  @Key("oca.dialog.version.heading")
  String oca_dialog_version_heading();

  /**
   * Translated "[No label]".
   * 
   * @return translated "[No label]"
   */
  @DefaultStringValue("[No label]")
  @Key("oca.editor.no_label")
  String oca_editor_no_label();

  /**
   * Translated "[No title]".
   * 
   * @return translated "[No title]"
   */
  @DefaultStringValue("[No title]")
  @Key("oca.editor.no_title")
  String oca_editor_no_title();

  /**
   * Translated "Save".
   * 
   * @return translated "Save"
   */
  @DefaultStringValue("Save")
  @Key("oca.elementGroup.btn.save")
  String oca_elementGroup_btn_save();

  /**
   * Translated "Element".
   * 
   * @return translated "Element"
   */
  @DefaultStringValue("Element")
  @Key("oca.elementGroup.heading")
  String oca_elementGroup_heading();

  /**
   * Translated "Description".
   * 
   * @return translated "Description"
   */
  @DefaultStringValue("Description")
  @Key("oca.resourceChooser.description")
  String oca_resourceChooser_description();

  /**
   * Translated "Filter".
   * 
   * @return translated "Filter"
   */
  @DefaultStringValue("Filter")
  @Key("oca.resourceChooser.filter.label")
  String oca_resourceChooser_filter_label();

  /**
   * Translated "Choose an entity".
   * 
   * @return translated "Choose an entity"
   */
  @DefaultStringValue("Choose an entity")
  @Key("oca.resourceChooser.heading")
  String oca_resourceChooser_heading();

  /**
   * Translated "Information".
   * 
   * @return translated "Information"
   */
  @DefaultStringValue("Information")
  @Key("oca.resourceChooser.information")
  String oca_resourceChooser_information();

  /**
   * Translated "Create new entity".
   * 
   * @return translated "Create new entity"
   */
  @DefaultStringValue("Create new entity")
  @Key("oca.resourceChooser.newResource")
  String oca_resourceChooser_newResource();

  /**
   * Translated "Search".
   * 
   * @return translated "Search"
   */
  @DefaultStringValue("Search")
  @Key("oca.resourceChooser.search.button")
  String oca_resourceChooser_search_button();

  /**
   * Translated "Search manually".
   * 
   * @return translated "Search manually"
   */
  @DefaultStringValue("Search manually")
  @Key("oca.resourceChooser.search.caption")
  String oca_resourceChooser_search_caption();

  /**
   * Translated "Version".
   * 
   * @return translated "Version"
   */
  @DefaultStringValue("Version")
  @Key("oca.systemGroup.btn.version")
  String oca_systemGroup_btn_version();

  /**
   * Translated "System".
   * 
   * @return translated "System"
   */
  @DefaultStringValue("System")
  @Key("oca.systemGroup.heading")
  String oca_systemGroup_heading();
}
