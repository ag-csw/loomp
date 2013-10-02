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

//XML parsing workaround


import autoannotator.ArsUtils
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import org.apache.uima.cas.CAS
import org.apache.uima.cas.FSIterator
import org.apache.uima.cas.Feature
import org.apache.uima.cas.FeatureStructure
import org.apache.uima.util.CasCreationUtils
import org.w3c.dom.Document
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import loomp.utils.JsonUtils

/**
 * "Web Service" that uses the internal annotation services
 * to annotate a given text and return it for further processing.
 * 
 * @author Patrick Jungermann
 * @version 1.0
 */
class ArsService {
	// services
	OpenCalaisAnnotatorService openCalaisAnnotatorService
	ZemantaAnnotatorService zemantaAnnotatorService

    boolean transactional = true

	/**
	 * Creates a {@link org.apache.uima.cas.CAS} object that could be used for all annotators.
	 *
	 * @param {List<String>} [annotatorServiceNames]
	 * 		The names of the used annotation services.<br/>
	 * 		Defaults to <code>['openCalais', 'zemanta']</code>.
	 * @return The {@link org.apache.uima.cas.CAS} object.
	 * @author Patrick Jungermann
	 */
	private CAS createCas(List<String> annotatorServiceNames = ['openCalais', 'zemanta']) {
		def descs = []
		annotatorServiceNames.each {name ->
			if ('openCalais' == name) {
				descs << ArsUtils.getAnalysisEngineDescription(openCalaisAnnotatorService.DESCRIPTOR_FILE)
			}
			else if ('zemanta' == name) {
				descs << ArsUtils.getAnalysisEngineDescription(zemantaAnnotatorService.DESCRIPTOR_FILE)
			}
		}

		CAS cas = CasCreationUtils.createCas(descs)

		return cas
	}

	/**
	 * Returns the required configuration parameters for a service given by its name.
	 *
	 * @return The names of the required configuration parameters.
	 * @author Patrick Jungermann
	 */
	public List<String> getRequiredConfigParams(String annotatorServiceName) {
		def required = []
		if ('openCalais' == annotatorServiceName) {
			required = openCalaisAnnotatorService.REQUIRED_CONFIG_PARAMS
		}
		else if ('zemanta' == annotatorServiceName) {
			required = zemantaAnnotatorService.REQUIRED_CONFIG_PARAMS
		}

		return required as List<String>
	}

	/**
	 * Annotates the text by using each of the listed annotators.
	 *
	 * @param {String} text
	 * 		The text that has to be analyzed.
	 * @param {List<String>} annotators
	 * 		The list of annotators that has to be used.<br/>
	 * 		Will be used in the same order as given.
	 * @param {Map<String, Map<String, Object>>} configParamsByAnnotator
	 * 		Contains the config parameters for each annotator.
	 * @return The {@link String} representation of the results.
	 * @author Patrick Jungermann
	 */
    String recommend(String text, List<String> annotators, Map<String, Map<String, Object>> configParamsByAnnotator) {
		CAS cas = createCas(annotators)
		cas.setDocumentText(text)

		// use each annotator to annotate the text
		def keys = configParamsByAnnotator.keySet()
		def key
		Map configParams
		annotators.each {annotator->
			if ('opencalais' == annotator?.toLowerCase()) {
				key = keys.find { 'opencalais' == it?.toLowerCase() }
				configParams = key && configParamsByAnnotator[key] ? (Map)configParamsByAnnotator[key] : [:]

				openCalaisAnnotatorService.recommend(cas, configParams)
			}
			else if ('zemanta' == annotator?.toLowerCase()) {
				key = keys.find { 'zemanta' == it?.toLowerCase() }
				configParams = key && configParamsByAnnotator[key] ? (Map)configParamsByAnnotator[key] : [:]

				zemantaAnnotatorService.recommend(cas, configParams)
			}
		}

		// serialize the CAS object
		//ArsUtils.
		String xmiResult = ArsUtils.casToXmi(cas)
		//def analysesEngineAnnotatedText=ArsUtils.getAnalysisEngineDescription(xmiResult)
		def ci=cas.getAnnotationIndex().iterator()
		def test=ci.next()
		def getAnnotationIndexBSP=cas.getAnnotationIndex()
		FSIterator iter = cas.getAnnotationIndex().iterator();
		int offset=0
    	// iterate
    	while (iter.isValid()) {
			FeatureStructure fs = iter.get();
			log.println("Feature names: "+fs.getType().getFeatures())

			//Gibt den type der Annotation
			def uimaAnnotationType= fs.getType()
			log.println("Feature type UIMA: "+uimaAnnotationType)

			//log.println("Feature type UIMA.Name : "+fs.getFeatureValue("de.fuberlin.ars.uima.annotator.calais.type.Entity"))

			//Wandelt dei UIMA Annotaion in eine Loomp Annotation um
			def annTypeLoomp=replaceWithLoompAnnotation(uimaAnnotationType.toString())
			log.println("Feature type Loomp: "+annTypeLoomp)

			//Gibt den value bereich aus
			def annotationSubjectText=fs.getCoveredText()
			log.println("Value: "+fs.getCoveredText())

			//Gibt den 'about' bereich aus
			if (annTypeLoomp!=null)
			{
				Feature feach=fs.getType().getFeatures()[3]
				def aboutDefinition=fs.getStringValue(feach)
				log.println("Feature about value: "+aboutDefinition)


				//this.decorateTextWithAnnotation(text,annTypeLoomp,annotationSubjectText)
				text=this.annotationInsert(text,fs.getBegin(),fs.getEnd(), aboutDefinition,
						annTypeLoomp,annotationSubjectText,offset)
				//offset is needed because the indexes of begin and end are shifted after each insert
				offset+=23+annTypeLoomp.length()+
						aboutDefinition.length()
				log.println("Annotated Text in loop: "+text)
			}

			//printFS(fs, aCAS, 0, aOut);
			iter.moveToNext();
    	}
		readXml(xmiResult)
		log.println("Annotated text: "+text)
		log.print (test)
		log.print (test.toString())
		//anotaionTextInsert(cas);
		//return JsonUtils.toJson(new loomp.model.ElementText(title:"sometitle",content:text))
        return text
    }

	//M.Faith earea
	public String replaceWithLoompAnnotation(String givenUri)
	{
	   switch(givenUri)
		{
		   case "de.fuberlin.ars.uima.annotator.calais.type.entity.Person":
			   	return "property=\"http://xmlns.com/foaf/0.1/firstName\" typeof=\"http://xmlns.com/foaf/0.1/Person\""
			   	break;
		   case "de.fuberlin.ars.uima.annotator.calais.type.entity.Position":
		   		return "http://xmlns.com/foaf/0.1/Position"
		   		break;
		   case "de.fuberlin.ars.uima.annotator.calais.type.entity.Country":
		   		return "http://xmlns.com/foaf/0.1/Country"
		   		break;		   
		   case "uima.tcas.DocumentAnnotation":
		   		return null
		   		break;

			}
	}
	public void decorateTextWithAnnotation(String text, String annotationUri,String textOfAnnotation)
	{
		if (annotationUri)
		{
			String annotatedTextPart="<foaf uri='"+annotationUri+"'>"+textOfAnnotation+"</foaf>"
			text.replace((CharSequence )textOfAnnotation,(CharSequence )annotatedTextPart)
		}
	}

	public static void readXml(String xmlResult) {

	  try {
	  /*File file = new File("c:\\MyXMLFile.xml");  */
	  DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	  DocumentBuilder db = dbf.newDocumentBuilder()
		InputStream is=new ByteArrayInputStream(xmlResult.getBytes())
	  Document doc = db.parse(is);
	  doc.getDocumentElement().normalize();
	  System.out.println("Root element " + doc.getDocumentElement().getNodeName());
	  NodeList nodeLst = doc.getElementsByTagName("entity:City");
	  System.out.println("Information of all employees");
	  def x=doc.getNodeName()
	  for (int s = 0; s < nodeLst.getLength(); s++) {

		Node fstNode = nodeLst.item(s);

		if (fstNode.getNodeType() == Node.ELEMENT_NODE) {

		}

	  }
	  } catch (Exception e) {
		e.printStackTrace();
	}
 	}

	private String annotationInsert(String mainText,int annotationStart,int annotationEnd,
								  String about,String annotationType,String annotationText,int offset)
	{
		//String resultInsert="\n<span "+"about='"+about+"'>\n"+"\t<span property='"+annotationType+"'>\n\t\t"+ annotationText+"\n\t</span>\n"+"</span>\n"
        String resultInsert="<span "+"about=\""+about+"\" "+annotationType+">"+ annotationText+"</span>"
		String startSub=mainText.substring(0,annotationStart+offset)
		String endSub=mainText.substring(annotationEnd+offset)
		mainText=startSub+ resultInsert +endSub
		log.println("Result of annotationInsert: "+mainText)
		return mainText
	}


}
