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
package loomp.dictionary

import loomp.model.Annotation
import loomp.model.Dictionary

class DictionaryEditorController {
	def uriService
	List dictionaryTitles
	List annotationTitles=new ArrayList()
	def defaultAction = 'addDictionary'

	
    def addDictionary = {
		log.print("new");

		loadDictionaryTitles()

		log.print("new end")

	//return ["annotationSetsTitles":annotationSetsTitles]
	}

	private loadDictionaryTitles()
	{
		dictionaryTitles=Dictionary.loadAll().collect{it?.title}
	}
	/*private loadAnnotatileTitles(String chosenSet)
	{
		//TODO:redundant to  annotationSetGet
		def aset=AnnotationSet.loadAll()
		def annotationSet=aset.find{it?.titles[pointerLocale].value==chosenSet}

		for(int i=0;i<annotationSet.annotations.size();i++)
		{
			annotationTitles.add(Annotation.load(annotationSet.annotations[i]).labels[pointerLocale].value)
		}
	}*/

	def  dictionaryGet =
	{   //TODO: also not nice should be passed via params
		loadDictionaryTitles()
		//TODO: das ist eklig - aber mus erst mal sein , wegen arrays im titles

		if (params?.chosenDic!='New')
		{
			//TODO: change loadAll to load uri, for that give a list of uris
			def adic=Dictionary.loadAll()
			def dictionary=adic.find{it?.title==params?.chosenDic}

			//loadAnnotatileTitles(params?.chosenSet)
			//TODO: change passing variables via params
			/*render(view:"index", model:[titleSet:annotationSet?.titles[pointerLocale].value,
					localeSet:chosenLocale,
					commentSet:annotationSet?.comments[pointerLocale].value,
					uriSet:annotationSet?.uri.toString(),annotationTitles:annotationTitles,
					annotationSets:aset,annotationSetsTitles:annotationSetsTitles,chosenSet:params?.chosenSet])*/
			render(view:"addDictionary", model:[titleDic:dictionary?.title,
					commentDic:dictionary?.comments,
					uriDic:dictionary?.uri.toString(),
					dictionarySets:adic,dictionaryTitles:dictionaryTitles,chosenDic:params?.chosenDic])
		}
		else	render(view:"addDictionary",model:[dictionarySets:adic,dictionaryTitles:dictionaryTitles])
	}

	/*def annotationGet =
	{
		//TODO: also not nice should be passed via params
		loadAnnotatileSetTitles()
		loadAnnotatileTitles(params?.chosenSet)

		//TODO: change loadAll to load uri, for that give a list of uris
		def aset=AnnotationSet.loadAll()
		def annotationSet=aset.find{it?.titles[pointerLocale].value==params?.chosenSet}
		//TODO: das ist eklig - aber mus erst mal sein , wegen arrays im titles
		if (params?.chosenAnnotation!='New')
		{
			def anAnnotation=Annotation.loadAll()
			log.print(params?.chosenAnnotation)
			def ananotation=anAnnotation.find{it?.labels[pointerLocale].value==params?.chosenAnnotation}




			render(view:"index", model:[
					label:ananotation?.labels[pointerLocale].value,
					comment:ananotation?.comments[pointerLocale].value,
					uri:ananotation?.uri.toString(),annotationTitles:annotationTitles,
					annotationSetsTitles:annotationSetsTitles,titleSet:params?.titleSet,
					chosenAnnotation:params?.chosenAnnotation, commentSet:params?.commentSet,
					uriSet:params?.uriSet,chosenSet:params?.chosenSet])
		}
		else	render(view:"index",model:[titleSet:annotationSet?.titles[pointerLocale].value,
					localeSet:chosenLocale,
					commentSet:annotationSet?.comments[pointerLocale].value,
					uriSet:annotationSet?.uri.toString(),annotationTitles:annotationTitles,
					annotationSetsTitles:annotationSetsTitles,chosenSet:params?.chosenSet,])
	}*/

	//inserting new, or edititng existing AnnotationsSets
	def editDictionary =
	{
		def titleDic= params?.titleDic ? params.titleDic as String : null
		//def localeSet= params?.localeSet ? params.localeSet as String : null
		def commentDic= params?.commentDic ? params.commentDic as String : null
		def uriDic= params?.uriDic ? params.uriDic as String : null

		if (titleDic !=null && commentDic !=null && uriDic!=null) {

			def adic=Dictionary.loadAll()
			def dictionary=adic.find{it?.title.value==params?.chosenSet}
			if(dictionary==null)
			{
				//inserting title and comments for Dictionary
				String title=titleDic;
				String comments=commentDic;

				def newdic = new Dictionary(
						uri:new java.net.URI(uriDic),title:title,
						comments:comments,dateCreated:new Date(),
						lastModified:new Date())
				newdic.save()
			}
			else
			{
				String title=titleDic
				String comments=commentDic

				dictionary.title=title
				dictionary.comments=comments
				dictionary.lastModified=new Date()

				dictionary.save()
			}

			redirect action: 'addDictionary'
		} else {
			flash.error = message(code: 'default.invalid.min.message', args: [message(code: 'default.number.label'), '0'])
			render view: 'addDictionary', model: [
					titleSet: params?.titleDic,
					commentDic:params?.commentDic,uriDic:params?.uriDic]
		}
	}

		//inserting new, or edititng existing AnnotationsSets
	/*def editAnnotation =
	{


		def label= params?.label ? params.label as String : null
		def comment= params?.comment ? params.comment as String : null
		def uri= params?.uri ? params.uri as String : null

		if (label !=null && comment !=null && uri!=null) {

			//TODO: change loadAll to load uri, for that give a list of uris
			def aset=AnnotationSet.loadAll()
			def annotationSet=aset.find{it?.titles[pointerLocale].value==params?.chosenSet}
			if(annotationSet.annotations.find{it==new java.net.URI(uri)}==null)
			{
				//inserting title and comments for AnnotationSet
				LangString[] labels=new LangString[1];
				labels[0]=new LangString(locale:"de",value:label)
				LangString[] comments=new LangString[1];
				comments[0]=new LangString(locale:"de",value:comment)

				Annotation annot=new Annotation(
						uri:new java.net.URI(uri),
						labels:labels,
						comments:comments,
						property:loomp.vocabulary.Loomp.annotationProperty,
						domain:loomp.vocabulary.Loomp.annotationDomain,
						range:loomp.vocabulary.Loomp.annotationRange,
						dateCreated:new Date(),
						lastModified:new Date()
				)
				annot.save()
				//TODO: ein Beweis wieso ein Array eine schlechte wahl ist
				log.print(annotationSet.annotations.size())
				//java.net.URI[] anns=new java.net.URI[6]
				def len=0;
				def annots=new ArrayList();
				for(;len<annotationSet.annotations.size()&&annotationSet.annotations[len]!=null;len++)
				{
					annots.add(annotationSet.annotations[len])
				}
				//System.arraycopy( annotationSet.annotations, 0, anns, 0, len )
                annots.add(new java.net.URI(uri))
				//annotationSet.annotations[len]=new java.net.URI(uri)
				annotationSet.annotations=annots
				log.print("here: "+ annotationSet.annotations.size())
				annotationSet.save()
			}
			else
			{
				  //inserting title and comments for AnnotationSet
				LangString[] labels=new LangString[1];
				labels[0]=new LangString(locale:"de",value:label)
				LangString[] comments=new LangString[1];
				comments[0]=new LangString(locale:"de",value:comment)
				def anAnnotation=Annotation.loadAll()
				log.print(params?.chosenAnnotation)
				def ananotation=anAnnotation.find{it?.uri==new java.net.URI(uri)}
				//ananotation.uri=new java.net.URI(uri);
				ananotation.labels=labels;
				ananotation.comments=comments;
				ananotation.property=loomp.vocabulary.Loomp.annotationProperty;
				ananotation.domain=loomp.vocabulary.Loomp.annotationDomain;
				ananotation.range=loomp.vocabulary.Loomp.annotationRange;
				ananotation.lastModified=new Date();

				ananotation.save()
			}


			/*flash.message =  message(code: 'default.created.message',
					args: ["$num ${message(code: num == 1 ? 'annotationSet.label' : 'annotationSets.label')}, ${persons.size()} ${message(code: persons.size() == 1 ? 'person.label' : 'persons.label')}, $numAnnos ${message(code: numAnnos == 1 ? 'annotation.label' : 'annotations.label')}"])
			*/
		/*	redirect action: 'index'
		} else {
			flash.error = message(code: 'default.invalid.min.message', args: [message(code: 'default.number.label'), '0'])
			render view: 'index', model: [
					title: params?.title,locale:params?.locale,
					comment:params?.comment,uri:params?.uri]
		}
	} */
}
