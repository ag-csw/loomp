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
package autoannotator

import org.apache.uima.UIMAFramework
import org.apache.uima.analysis_engine.AnalysisEngine
import org.apache.uima.analysis_engine.AnalysisEngineDescription
import org.apache.uima.cas.CAS
import org.apache.uima.cas.impl.XmiCasSerializer
import org.apache.uima.util.XMLInputSource

/**
 * Contains some helper methods.
 * 
 * @author Patrick Jungermann
 * @version 1.0
 */
public class ArsUtils {

	/**
	 * Returns the serialized XMI representation of a {@link org.apache.uima.cas.CAS} object as {@link String}.
	 *
	 * @param {CAS} cas
	 * 		The object that has to be serialized.
	 * @return The XMI representation as {@link String}.
	 * @author Patrick Jungermann
	 */
	static String casToXmi(CAS cas) {
		def xmiCasSerializer = new XmiCasSerializer(cas.typeSystem)
		def out = new ByteArrayOutputStream()
		xmiCasSerializer.serialize(cas, out)

		return out.toString('UTF-8')
	}

	/**
	 * Returns the {@link org.apache.uima.analysis_engine.AnalysisEngineDescription} object created
	 * of the file with the given filename.
	 *
	 * @param {String} filename
	 * 		The (absolute) filename of the descriptor file for an
	 * 		{@link org.apache.uima.analysis_engine.AnalysisEngine}.
	 * @return The {@link org.apache.uima.analysis_engine.AnalysisEngineDescription} object.
	 * @author Patrick Jungermann
	 */
	static AnalysisEngineDescription getAnalysisEngineDescription(String filename) {
		// parse AnalysisEngine descriptor
		XMLInputSource input = new XMLInputSource(filename)
		AnalysisEngineDescription desc = UIMAFramework.getXMLParser().parseAnalysisEngineDescription(input)

		return desc
	}

	/**
	 * Loads the {@link org.apache.uima.analysis_engine.AnalysisEngine} of the descriptor file given by its name
	 * and changes each parameter given as key to the corresponding value.
	 *
	 * @param {String} filename
	 * 		The (absolute) filename of the descriptor file.
	 * @param {Map} [changedConfigParaMap]
	 * 		The {@link Map} of config parameters that has to be changed,
	 * 		where the key represents the parameter name and the value
	 * 		for this key the new value, that has to be used.<br/>
	 * 		The value has to be of the correct type.<br/>
	 * 		Defaults to <code>[:]</code>.
	 * @return The {@link org.apache.uima.analysis_engine.AnalysisEngine} object.
	 * @author Patrick Jungermann
	 */
	static AnalysisEngine loadAnalysisEngine(String filename, Map changedConfigParaMap = [:]) {
		AnalysisEngineDescription desc = ArsUtils.getAnalysisEngineDescription(filename)

		// change the config parameters with the values for it within the given map.
		def newConfigParaKeys = changedConfigParaMap.keySet()
		def paraSettings = desc.analysisEngineMetaData.configurationParameterSettings.parameterSettings
		def configParas = desc.analysisEngineMetaData.configurationParameterDeclarations.configurationParameters

		paraSettings.each {item ->
			def paraType = configParas.find { it.name == item.name }?.type
			if (item.name in newConfigParaKeys && changedConfigParaMap[item.name]?.class?.simpleName == paraType) {
				item.value = changedConfigParaMap[item.name]
			}
		}

		// create AnalysisEngine
		AnalysisEngine analysisEngine = UIMAFramework.produceAnalysisEngine(desc)

		return analysisEngine
	}
}
