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
package loomp

import loomp.utils.JsonUtils

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

/**
 * Access point to the Web Service. Delegates the work to the <code>ArsService</code>.
 *
 * @author Patrick Jungermann
 * @version 1.0
 */
class ArsController extends GenericController {
	// services
	def arsService

	/** Sets the default action.   */
	def defaultAction = 'info'

    def recommendInput={}

    def testInput = {
        String link="/ars/recommend?"
        String textToAnnotate=params.get("textToAnnotate")
        String annotators=params.get("annotators")
        String zemantaApiKey=params.get("zemanta.apiKey")
        boolean openCalaisAllowDistribution= params.get("openCalais.allowDistribution")
        boolean openCalaisAllowSearch= params.get("openCalais.allowSearch")
        String openCalaisContentType=params.get("openCalais.contentType")
        String openCalaisLicenseID=params.get("openCalais.licenseID")

        switch (annotators)
        {
          case "zemanta":
            link+="annotators=zemanta&"+"zemanta.apiKey="+zemantaApiKey+"&text="+textToAnnotate
            break
          case "openCalais":
            link+="annotators=openCalais&"+"openCalais.licenseID="+openCalaisLicenseID+
              "&text="+textToAnnotate+"&openCalais.contentType="+openCalaisContentType+
              "&openCalais.allowSearch="+openCalaisAllowSearch+"&openCalais.allowDistribution="+
              openCalaisAllowDistribution
            break
          //TODO: something seems wrong about annotators being executed simultaneously
          case "all":
             link+="annotators=zemanta,openCalais&"+"zemanta.apiKey="+zemantaApiKey+"&text="+textToAnnotate+
              "&openCalais.licenseID="+openCalaisLicenseID+
              "&openCalais.contentType="+openCalaisContentType+
              "&openCalais.allowSearch="+openCalaisAllowSearch+"&openCalais.allowDistribution="+
              openCalaisAllowDistribution
            break
        }
		redirect uri: link
	}

	/**
	 * Displays an information site.
	 *
	 * @author Patrick Jungermann
	 */
	def info = {
    }

	/**
	 * Runs the Annotation Recommender Service (ARS).
	 *
	 * @author Patrick Jungermann
	 */
	def recommend = {

		if (!params.annotators) {
			// Bad Request
			return response.sendError(400, message(code: 'error.message.400.no.services'))
		}
		if (!params.text) {
			// Bad Request
			return response.sendError(400, message(code: 'error.message.400.no.text'))
		}
		String text = params.text
		List<String> annotators = params.annotators instanceof String ? [params.annotators] : params.annotators

		Map<String, Map<String, Object>> configParamsByAnnotator = [:]
		annotators.each {annotator ->
			if (params[annotator] && params[annotator] instanceof Map) {
				configParamsByAnnotator[annotator] = params[annotator]
			}

			// check, if there are all params needed for this annotator service
			def requiredParams = arsService.getRequiredConfigParams(annotator)
			requiredParams.each {requiredParam ->
				if (!configParamsByAnnotator?.get(annotator)?.get(requiredParam)) {
					// Bad Request
					return response.sendError(400, message(code: 'error.message.400.missing.parameter', args: [requiredParam, annotator]))
				}
			}
		}

		/*renderObejct(text: arsService.recommend(text, annotators, configParamsByAnnotator),
				contentType: RenderFormat.JSON, encoding: 'UTF-8')    */
        def formulatedRespond=arsService.recommend(text, annotators, configParamsByAnnotator)
        def toTextEl=new loomp.model.ElementText(title:"sometitle",content:formulatedRespond)
        //def json=JsonUtils.toJson(toTextEl)
        renderObject(toTextEl,RenderFormat.JSON)
	}

	/**
	 * Catch all request methods that are not allowed.
	 * 
	 * @author Patrick Jungermann
	 */
	def notAllowed = {
		return response.sendError(405)
	}
}
