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

import org.codehaus.groovy.grails.plugins.springsecurity.Secured
import com.hp.hpl.jena.rdf.model.Model
import com.hp.hpl.jena.rdf.model.ModelFactory
import loomp.test.TestDataGenerator

/**
 * Controller for administrating loomp
 */
@Secured(['ROLE_ADMIN'])
class AdminController {
	def authenticateService
	def loompService
	def uriService

	def index = { }

	def system = { }

	/**
	 * Remove all data from the endpoint.
	 */
	def clearEndpoint = {
		loompService.clear()
		loompService.dropAllGraphs()
		flash.message = "All data has been deleted from "
		redirect action: "system"
	}

	def upload = { }

	/**
	 * Parse a RDF file which can be uploaded by the user and load the data to the RDF store. Right now
	 * choosing an RDF store is not possible, instead the default RDF Store (localhost) is chosen
	 *
	 * @param delete
	 * 		If "yes" then all data is deleted from the endpoint before loading the new one.
	 */
	def uploadFile = {
		if (params?.clear == "yes") {
			loompService.clear(params.graphUri)
			log.info "RDF store has been cleared on user request"
		}
		def file = request.getFile('file')
		try {
			Model model = ModelFactory.createDefaultModel()
			model.read(file.getInputStream(), "")
			loompService.insertData(model, params.graphUri)
			log.info "User ${authenticateService.userDomain().id} uploaded file ${file.getOriginalFilename()}"
			flash.message = "File uploaded ${file.getOriginalFilename()}"
		} catch (Exception e) {
			log.error "Error while uploading file", e
			flash.error = e.getMessage()
		}
		redirect action: "system"
	}

	def generate = { }

	/**
	 * Generate a number of document instances including referenced persons and elements.
	 *
	 * @param num
	 * 		number of documents to be created
	 * @param clear
	 * 		If "yes" then all data is deleted from the endpoint before creating documents.
	 */
	def genDocuments = {
		def num = params?.num ? params.num as Integer : null
		if (num && num > 0) {
			if (params?.clear == "yes") {
				loompService.clear()
				log.info "RDF store has been cleared on user request"
			}
			def generator = new TestDataGenerator()
			generator.uriGenerator = uriService
			def persons = generator.getPersons(Math.ceil(num / 2))
			persons.each { pers ->
				log.debug "Saving person $pers.uri"
				if (pers.save()) log.trace "*** success"
				else log.error "*** failed"
			}

			def docs = generator.getDocuments(num, persons)
			def numElems = 0
			docs.each { doc ->
				log.debug "Saving document $doc.uri"
				if (doc.save()) log.trace "*** success"
				else log.error "*** failed"

				// Create the entities related to doc
				doc.elements.each { elem ->
					if (createElementText(elem, doc.creator, generator)) numElems++
				}
			}
			flash.message =  message(code: 'default.created.message',
					args: ["$num ${message(code: num == 1 ? 'document.label' : 'documents.label')}, ${persons.size()} ${message(code: persons.size() == 1 ? 'person.label' : 'persons.label')}, $numElems ${message(code: numElems == 1 ? 'element.label' : 'elements.label')}"])
			redirect action: 'index'
		} else {
			flash.error = message(code: 'default.invalid.min.message', args: [message(code: 'default.number.label'), '0'])
			render view: 'generate', model: [num: params?.num]
		}
	}

	/**
	 * Create a elementText with a given URI.
	 *
	 * @param uri
	 * 		a URI
	 * @param creatorUri
	 * 		URI of the creator
	 * @param generator
	 * 		generator for creating the text element
	 * @return generated instance of text element
	 */
	private createElementText(URI uri, creatorUri, generator) {
		def element = null
		// TODO later we could also check if the type of the resource is correct
		if (!loompService.isSubject(uri)) {
			log.debug "Saving text element $uri"
			element = generator.getElementText(uri).save()
			element.creator = creatorUri
			if (element) log.trace "*** success"
			else log.error "*** failed"
		}
		return element
	}

	/**
	 * Generate a number of annotation sets including referenced persons and annotations.
	 *
	 * @param num
	 * 		number of annotations sets to be created
	 * @param clear
	 * 		If "yes" then all data is deleted from the endpoint before creating documents.
	 */
	def genAnnotations = {
		def num = params?.num ? params.num as Integer : null
		if (num && num > 0) {
			if (params?.clear == "yes") {
				loompService.clear()
				log.info "RDF store has been cleared on user request"
			}
			def generator = new TestDataGenerator()
			generator.uriGenerator = uriService
			def persons = generator.getPersons(Math.ceil(num / 2))
			persons.each { pers ->
				log.debug "Saving person $pers.uri"
				if (pers.save()) log.trace "*** success"
				else log.error "*** failed"
			}

			def docs = generator.getAnnotationSets(num, persons)
			def numAnnos = 0
			docs.each { set ->
				log.debug "Saving document $set.uri"
				if (set.save()) log.trace "*** success"
				else log.error "*** failed"

				// Create the entities related to doc
				set.annotations.each { anno ->
					if (createAnnotation(anno, set.creator, generator)) numAnnos++
				}
			}
			flash.message =  message(code: 'default.created.message',
					args: ["$num ${message(code: num == 1 ? 'annotationSet.label' : 'annotationSets.label')}, ${persons.size()} ${message(code: persons.size() == 1 ? 'person.label' : 'persons.label')}, $numAnnos ${message(code: numAnnos == 1 ? 'annotation.label' : 'annotations.label')}"])
			redirect action: 'index'
		} else {
			flash.error = message(code: 'default.invalid.min.message', args: [message(code: 'default.number.label'), '0'])
			render view: 'generate', model: [num: params?.num]
		}
	}

	/**
	 * Create a annotation with a given URI.
	 *
	 * @param uri
	 * 		a URI
	 * @param creatorUri
	 * 		URI of the creator
	 * @param generator
	 * 		generator for creating the text element
	 * @return generated instance of text element
	 */
	private createAnnotation(URI uri, creatorUri, generator) {
		def annotation = null
		// TODO later we could also check if the type of the resource is correct
		if (!loompService.isSubject(uri)) {
			log.debug "Saving annotation $uri"
			annotation = generator.getAnnotation(uri).save()
			annotation.creator = creatorUri
			if (annotation) log.trace "*** success"
			else log.error "*** failed"
		}
		return annotation
	}

	/**
	 * Generate a number of resources.
	 *
	 * @param num
	 * 		number of resources to be created
	 * @param clear
	 * 		If "yes" then all data is deleted from the endpoint before creating documents.
	 */
	def genResources = {
		def num = params?.num ? params.num as Integer : null
		if (num && num > 0) {
			if (params?.clear == "yes") {
				loompService.clear()
				log.info "RDF store has been cleared on user request"
			}
			def generator = new TestDataGenerator()
			def resources = generator.getResources(num)
			resources.each { res ->
				log.debug "Saving resource $res.uri"
				if (res.save()) log.trace "*** success"
				else log.error "*** failed"
			}
			flash.message =  message(code: 'default.created.message',
					args: ["$num ${message(code: num == 1 ? 'resource.label' : 'resources.label')}"])
			redirect action: 'index'
		} else {
			flash.error = message(code: 'default.invalid.min.message', args: [message(code: 'default.number.label'), '0'])
			render view: 'generate', model: [num: params?.num]
		}
	}
}
