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
package loomp.utils

import java.lang.reflect.Modifier
import loomp.model.TypeMapper
import loomp.model.VersionedUri
import loomp.utils.entityMapper.EntityMapper
import loomp.utils.json.DateDeserializer
import loomp.utils.json.DateSerializer
import loomp.utils.json.VersionedUriDeserializer
import loomp.utils.json.VersionedUriSerializer
import com.google.gson.*
import loomp.model.Resource
import loomp.utils.json.ResourceDeserializer

/**
 * Methods for converting instances of the domain model to and from JSON.
 */
class JsonUtils {
	/**
	 * Convert an object into JSON format.
	 *
	 * @param obj
	 * 		object to be converted
	 * @return JSON representation of the object
	 */
	static String toJson(obj) {
		return toJson(obj, null)
	}

	/**
	 * Convert an object into JSON format.
	 *
	 * @param obj
	 * 		object to be converted
	 * @return JSON representation of the object
	 */
	static String toJson(obj, EntityMapper mapper) {
		def entity = mapper ? mapper.convert(obj) : obj

		// advantages of gson over grails' JSON: does not encode null values and not include artificial property 'class'
		def gson = new GsonBuilder()
				.excludeFieldsWithModifiers(Modifier.TRANSIENT)
				.excludeFieldsWithoutExposeAnnotation()
				.registerTypeAdapter(Date.class, new DateSerializer())
				.registerTypeAdapter(VersionedUri.class, new VersionedUriSerializer()).create()

		// static fields are not converted, but we need the type information in the JSON string
		gson.toJson(entity)
	}

	/**
	 * Create an instance of a domain class or Resource from a JSON string. To decide
	 * on the domain class the json string has to contain an attribute type having the
	 * type URI of the domain class as value.
	 *
	 * @param json
	 * 		JSON string
	 * @return JSON representation of the object
	 * @throws IllegalArgumentException if the JSON string does not contain a type URI or
	 * 		he found type is unknown
	 */
	// TODO extend the implementation to recognize XSD datatypes type URIs

	static fromJson(String json) {
		return fromJson(json, null,null)
	}

	/**
	 * Create an instance from JSON string. If clazz is null then we try to guess the
	 * class (a subclass of BaseEntity).
	 *
	 * ATTENTION: This method can only be used if clazz extends the model classes of the model
	 * library.
	 *
	 * @param json
	 * 		JSON string
	 * @param clazz
	 * 		class contained in the JSON string (optional)
	 * @return JSON representation of the object
	 * @throws IllegalArgumentException if the JSON string does not contain a type URI or
	 * 		he found type is unknown
	 */
	static fromJson(String json, clazz) {
		if (!clazz)
			throw new IllegalArgumentException()

		// TODO check if clazz extends model classes (consider that clazz can be a Collection or something similar)
		def gson = new GsonBuilder()
				.registerTypeAdapter(Date.class, new DateDeserializer())
				.registerTypeAdapter(VersionedUri.class, new VersionedUriDeserializer())
				.registerTypeAdapter(Resource.class, new ResourceDeserializer())
				.create()
		return gson.fromJson(json, clazz)
	}

	static fromJson(String json, EntityMapper eMapper) {
		fromJson(json, null, eMapper)
	}

	static fromJson(String json, String expectedTypeUri, EntityMapper eMapper) {
		if (!json)
			return null
		def gson = new GsonBuilder()
				.registerTypeAdapter(Date.class, new DateDeserializer())
				.registerTypeAdapter(VersionedUri.class, new VersionedUriDeserializer())
				.registerTypeAdapter(Resource.class, new ResourceDeserializer())
				.create()
		return fromJsonInternal(new JsonParser().parse(json), gson, expectedTypeUri, eMapper)
	}

	private static fromJsonInternal(JsonElement jsonObj, Gson gson, String expectedTypeUri, EntityMapper eMapper) {
		if (jsonObj.isJsonArray()) {
			JsonArray jsonArr = jsonObj.getAsJsonArray()
			def entities = []
			jsonArr.each {
				try {
					entities << fromJsonInternal(it, gson, expectedTypeUri, eMapper)
				} catch (IOException e) {
					// empty
					// TODO Log error
					// TODO change Exception
				}
			}
			return entities
		} else {
			def clazz = loomp.model.Resource.class
			// if we have a type then we think that we have an entity of the domain model otherwise it is a Resource
			if (jsonObj.getAsJsonPrimitive("type")) {
				def typeUri = URI.create(jsonObj.getAsJsonPrimitive("type").getAsString())
				if (expectedTypeUri && typeUri != expectedTypeUri) {
					// TODO change Exception
					throw new Exception("Unexpected type in json")
				}
				clazz = TypeMapper.instance.getDomainClass(typeUri)
				if (!clazz) {
					throw new IllegalArgumentException("Unkown entity type URI $typeUri")
				}
				// because type is static member we have remove it before calling #fromJsonInternal
				jsonObj.remove("type")
			}
			return eMapper ? eMapper.convert(gson.fromJson(jsonObj, clazz)) : gson.fromJson(jsonObj, clazz)
		}
	}
}
