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
package loomp.utils.json

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import java.lang.reflect.Type
import loomp.model.Resource
import loomp.model.TypedPropertyValue
import loomp.model.LocaleLiteralMap

/**
 * Deserialize a resource. This has to be done manually since the value of a property may be
 * a LocaleLiteralMap.
 */

class ResourceDeserializer implements JsonDeserializer<Resource> {

	public Resource deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
		Resource resource = new Resource()

		JsonObject object = json.getAsJsonObject()
		resource.uri = context.deserialize(object.getAsJsonPrimitive("uri"), URI.class)
		resource.props = []
		for (JsonElement element : object.getAsJsonArray("props")) {
			JsonObject prop = element.getAsJsonObject()
			TypedPropertyValue tpv = new TypedPropertyValue()
			tpv.property = context.deserialize(prop.getAsJsonPrimitive("property"), URI.class)
			tpv.isLiteral = context.deserialize(prop.getAsJsonPrimitive("isLiteral"), Boolean.class)

			if (prop.get("value").isJsonObject()) {
				// we have a LocaleLiteralMap
				def value = prop.getAsJsonObject("value")
				tpv.value = new LocaleLiteralMap()
				for (Map.Entry<String, JsonElement> entry : value.entrySet()) {
					tpv.value.put(entry.key, entry.value.getAsString())
				}
			} else {
				// we have a String or a URI
				tpv.value = context.deserialize(prop.getAsJsonPrimitive("value"), tpv.isLiteral ? String.class : URI.class)
			}
			resource.props << tpv
		}
		return resource
	}
}
