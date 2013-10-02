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

import loomp.model.Annotation
import loomp.model.AnnotationSet
import loomp.model.command.AnnotationCommand
import loomp.model.command.AnnotationSetCommand
import loomp.model.LocaleLiteralMap
import loomp.model.ResourceType
import grails.util.GrailsNameUtils
import loomp.utils.JsonUtils
import loomp.model.Resource
import loomp.GenericController

class AnnotationEditorController extends GenericController {
	def uriService
	def loompService

	def index = {
		return [annotationSets: AnnotationSet.loadAll()]
	}

	def newAnnotationSetAjax = {
		def cmd = new AnnotationSetCommand()
		render view: 'editAnnotationSetAjax', model: [cmd: cmd, isNew: true]
	}

	def editAnnotationSetAjax = {
		if (!params.uri) {
			render text: "No URI of an annotation set given"
			return false
		}

		def locale = params.locale
		def annotationSet = AnnotationSet.load(params.uri)
		if (!annotationSet) {
			render text: "No annotation set found with given URI: $params.uri"
			return false
		}

		def cmd = new AnnotationSetCommand()
		cmd.uri = annotationSet.uri
		if (locale) {
			cmd.locale = locale
			cmd.label = annotationSet.labels?."$locale"
			cmd.comment = annotationSet.comments?."$locale"
		}

		def annotations = []
		annotationSet.annotations.each {
			def a = Annotation.load(it)
			if (a) annotations << a
		}

		return [cmd: cmd, annotations: annotations]
	}

	def saveAnnotationSet = {
		AnnotationSetCommand cmd ->
		if (!cmd.validate()) {
			redirect action: 'newAnnotationAjax', params: [uri: params.asUri, locale: params.locale]
			return
		}

		def annotationSet = new AnnotationSet()
		assign(annotationSet, cmd)
		annotationSet.save()

		redirect action: 'index'
	}

	def updateAnnotationSet = {
		AnnotationSetCommand cmd ->
		if (!cmd.validate()) {
			redirect action: 'newAnnotationSetAjax'
			return
		}

		def annotationSet = AnnotationSet.load(cmd.uri)
		assign(annotationSet, cmd)
		annotationSet.save()

		// Update AnnotationSet
		redirect action: 'index'
	}


	def deleteAnnotationSet = {
		if (!params.uri) {
			flash.error = "No URI of an annotation set given"
			redirect action: 'index'
		}

		def annotationSet = AnnotationSet.load(params.uri)
		if (!annotationSet) {
			flash.error = "No annotation set found with given URI: $params.uri"
			redirect action: 'index'
		}

		annotationSet.delete()
		annotationSet.annotations.each {
			loompService.deleteData(it, Annotation.type)
		}

		redirect action: 'index'
	}

	def newAnnotationAjax = {
		def cmd = new AnnotationCommand()
		render view: 'editAnnotationAjax', model: [cmd: cmd, asUri: params.asUri, locale: params.locale, isNew: true]
	}

	def editAnnotationAjax = {
		if (!params.uri) {
			render text: "No URI of an annotation given"
			return false
		}

		if (!params.locale) {
			render text: "No locale given"
			return false
		}

		def locale = params.locale
		def annotation = Annotation.load(params.uri)
		if (!annotation) {
			render text: "No annotation found with given URI: $params.uri"
			return false
		}

		def cmd = new AnnotationCommand()
		cmd.uri = annotation.uri
		cmd.locale = locale
		cmd.label = annotation.labels?."$locale"
		cmd.comment = annotation.comments?."$locale"
		cmd.propertyUri = annotation.property
		cmd.domainUri = annotation.domain
		cmd.rangeUri = annotation.range

		return [cmd: cmd, asUri: params.asUri, locale: params.locale]
	}

	def saveAnnotationAjax = {
		AnnotationCommand cmd ->
		if (!cmd.validate()) {
			redirect action: 'newAnnotationAjax', params: [uri: params.asUri, locale: params.locale]
			return
		}

		def annotation = new Annotation()
		assign(annotation, cmd)
		annotation.save()

		// Update AnnotationSet
		if (!params.asUri) {
			flash.error = "No URI of an annotation set given"
			redirect action: 'index'
			return
		}

		def annotationSet = AnnotationSet.load(params.asUri)
		if (!annotationSet) {
			flash.error = "No annotation set found with given URI: $params.asUri"
			redirect action: 'index'
			return
		}

		if (!annotationSet.annotations)
			annotationSet.annotations = []
		annotationSet.annotations.add(annotation.uri)
		annotationSet.save()

		redirect action: 'editAnnotationSetAjax', params: [uri: params.asUri, locale: params.locale]
	}

	def updateAnnotationAjax = {
		AnnotationCommand cmd ->
		if (!cmd.validate()) {
			redirect action: 'newAnnotationAjax', params: [uri: params.asUri, locale: params.locale]
			return
		}

		def annotation = Annotation.load(cmd.uri)
		assign(annotation, cmd)
		annotation.save()

		// Update AnnotationSet
		redirect action: 'editAnnotationSetAjax', params: [uri: params.asUri, locale: params.locale]
	}

	def deleteAnnotationAjax = {
		if (!params.uri) {
			flash.error = "No URI of an annotation given"
			redirect action: 'index'
		}

		def annotation = Annotation.load(params.uri)
		if (!annotation) {
			flash.error = "No annotation found with given URI: $params.uri"
			redirect action: 'index'
			return
		}

		annotation.delete()

		// Update AnnotationSet
		if (!params.asUri) {
			flash.error = "No URI of an annotation set given"
			redirect action: 'index'
			return
		}

		def annotationSet = AnnotationSet.load(params.asUri)
		if (!annotationSet) {
			flash.error = "No annotation set found with given URI: $params.asUri"
			redirect action: 'index'
			return
		}

		annotationSet.annotations.remove(URI.create(params.uri))
		annotationSet.save()

		redirect action: 'editAnnotationSetAjax', params: [uri: params.asUri, locale: params.locale]
	}

	/**
	 * Assign the values of the cmd object to the annotation set object.
	 */
	private assign(AnnotationSet annotationSet, AnnotationSetCommand cmd) {
		if (!annotationSet.uri)
			annotationSet.uri = cmd.uri ? URI.create(cmd.uri) : uriService.generateUri(ResourceType.ANNOTATION)
		if (!annotationSet.labels) annotationSet.labels = new LocaleLiteralMap()
		annotationSet.labels.put(cmd.locale, cmd.label)
		if (GrailsNameUtils.isBlank(cmd.comment)) {
			if (!annotationSet.comments) annotationSet.comments = new LocaleLiteralMap()
			annotationSet.comments.put(cmd.locale, cmd.comment)
		}
	}

	/**
	 * Assign the values of the cmd object to the annotation object.
	 */
	private assign(Annotation annotation, AnnotationCommand cmd) {
		if (!annotation.uri)
			annotation.uri = cmd.uri ? URI.create(cmd.uri) : uriService.generateUri(ResourceType.ANNOTATION)
		if (!annotation.labels) annotation.labels = new LocaleLiteralMap()
		annotation.labels.put(cmd.locale, cmd.label)
		if (GrailsNameUtils.isBlank(cmd.comment)) {
			if (!annotation.comments) annotation.comments = new LocaleLiteralMap()
			annotation.comments.put(cmd.locale, cmd.comment)
		}
		annotation.property = URI.create(cmd.propertyUri)
		annotation.domain = GrailsNameUtils.isBlank(cmd.domainUri) ? null : URI.create(cmd.domainUri)
		annotation.range = GrailsNameUtils.isBlank(cmd.rangeUri) ? null : URI.create(cmd.rangeUri)
	}
}
