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
package loomp.autoannotator
/*******************************************************************************
 * Copyright (c) 2009 Patrick Jungermann
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

import autoannotator.AnnotatorService
import autoannotator.ArsUtils
import de.fuberlin.ars.uima.annotator.calais.OpenCalaisAnnotator
import org.apache.uima.UIMAException
import org.apache.uima.analysis_engine.AnalysisEngine
import org.apache.uima.cas.CAS
import org.codehaus.groovy.grails.commons.ConfigurationHolder

/**
 * Recommends annotations for texts by using
 * the {@link OpenCalaisAnnotator}, a wrapper for
 * the OpenCalais Web Service.
 *
 * @author Patrick Jungermann
 * @version 1.0
 */
class OpenCalaisAnnotatorService implements AnnotatorService  {
    boolean transactional = true

	static final String DESCRIPTOR_FILE = ConfigurationHolder.config.ars.descriptor.dir + 'OpenCalaisAnnotator.xml'
	static final List<String> REQUIRED_CONFIG_PARAMS = ['licenseID']

	/**
	 * Annotates the text that was stored within the {@link org.apache.uima.cas.CAS} object
	 * by using the annotator defined within the <code>DESCRIPTOR_FILE</code>
	 * and add the new annotations to this object.
	 *
	 * @param {CAS} cas
	 * 		The {@link org.apache.uima.cas.CAS} object that contains the text.
	 * @param {Map} [configParams]
	 * 		Set of key (parameter name) and value (new parameter value) pairs
	 * 		that has to be used to change the annotators configuration.<br/>
	 * 		Defaults to <code>[:]</code>.
	 * @throws org.apache.uima.UIMAException
	 * @throws IOException
	 * @author Patrick Jungermann
	 */
	void recommend(CAS cas, Map configParams = [:]) throws UIMAException, IOException {
		try {
			// load AnalysisEngine
			AnalysisEngine seAnnotator = ArsUtils.loadAnalysisEngine(DESCRIPTOR_FILE, configParams)
			// analyze the text
			seAnnotator.process(cas)
			//seAnnotator.get
			// clean up
			seAnnotator.destroy()
		}
		catch (Exception e) {
			def out = new ByteArrayOutputStream()
			def ps = new PrintStream(out)
			e.printStackTrace(ps)
			log.error "Processing of OpenCalaisAnnotatorService.recommend failed: ${e}\n${out.toString()}"
		}
	}
}
