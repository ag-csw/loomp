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

import com.hp.hpl.jena.rdf.model.Model
import com.hp.hpl.jena.rdf.model.ModelFactory
import loomp.model.DomainEntity
import loomp.utils.JenaUtils
import loomp.model.BaseEntity
import loomp.utils.JsonUtils

class GenericController {
	/**
	 * Render an object in a format defined by request.format.
	 *
	 * @param obj
	 * 		object to be rendered
	 * @throws IllegalArgumentException if the format is not supported
	 */
	protected renderObject(obj) {
		renderObject(obj, request.fmt)
	}

	/**
	 * Render an object in a given format.
	 *
	 * @param obj
	 * 		object to be rendered
	 * @param fmt
	 * 		format to be used for rendering
	 * @throws IllegalArgumentException if the format is not supported
	 */
	protected renderObject(obj, fmt) {
		def text
		switch (fmt) {
			case RenderFormat.JSON:
				text = JsonUtils.toJson(obj)
				break
			case RenderFormat.N3:
			case RenderFormat.RDF_XML:
			case RenderFormat.RDF_XML_ABBR:
				if (obj instanceof BaseEntity) {
					def os = new ByteArrayOutputStream()
					Model model = ModelFactory.createDefaultModel()
					obj.toModel.call(model, obj).write(os, fmt.rdfSyntax)
					text = os.toString("UTF-8")
				}
				else if (obj instanceof Collection) {
					// TODO writing all to memory will require lots of memory at some time. Maybe, we should write to disk
					Model model = ModelFactory.createDefaultModel()
					obj.each {
						it.toModel.call(model, it)
					}
					def tempFile = session.createTempFile("model_", ".$fmt.extension")
					def os = new FileOutputStream(tempFile)
					model.write(os, fmt.rdfSyntax)
					tempFile.withInputStream {
						response.setHeader("Content-disposition", "filename=${tempFile.getName()}")
						response.contentLength = it.available()
						response.contentType = fmt.contentType
						response.outputStream << it
					}
					return
					// text = os.toString("UTF-8")
				} else {
					renderBadRequest("Object $obj cannot be rendered as $fmt")
					return
				}
				break
			default:
				renderBadRequest("Unsupported format for rendering an object $fmt")
				return
		}
		render(text: text, contentType: fmt.contentType, encoding: "UTF-8")
	}

	/**
	 * Parse a given string being in a given format to an object. Currently, only JSON is supported.
	 *
	 * @param s
	 * 		string to be parsed
	 * @param fmt
	 * 		format to be used for parsing
	 * @return the parsed object
	 *
	 * @throws IllegalArgumentException
	 * 		if the format is not supported
	 */
	protected parseString(s, fmt = null) {
		switch (fmt) {
			case RenderFormat.JSON:
				return JsonUtils.fromJson(s)
			case RenderFormat.N3:
			case RenderFormat.RDF_XML:
			case RenderFormat.RDF_XML_ABBR:
				// TODO handle the case that s represents a resource
				def is = new ByteArrayInputStream(s.getBytes())
				Model model = ModelFactory.createDefaultModel()
				model.read(is, "")
				return JenaUtils.modelToEntity(model)
			default:
				render(status: HttpURLConnection.HTTP_BAD_REQUEST, text: "Unsupported format for parsing data $fmt")
		}
	}

	protected renderInternalError(message) {
		render(status: HttpURLConnection.HTTP_INTERNAL_ERROR, text: message)
	}

	protected renderBadRequest(message) {
		render(status: HttpURLConnection.HTTP_BAD_REQUEST, text: message)
	}
}
