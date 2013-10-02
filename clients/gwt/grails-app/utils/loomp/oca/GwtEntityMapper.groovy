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
package loomp.oca

import loomp.model.LocaleLiteralMap
import loomp.utils.entityMapper.EntityMapper
import loomp.oca.client.model.*
import loomp.model.TypedPropertyValue
import loomp.oca.client.utils.LocaleUtils

/**
 * User: M.Faith
 */
class GwtEntityMapper implements EntityMapper<BaseEntity, Resource> {
	/** language to be used when converting literals */
	protected String locale

	/**
	 * Create an entity mapper using the default language
	 */
	public GwtEntityMapper() {
		this(null)
	}

	/**
	 * Create an entity mapper using the given language. If language is null then
	 * the default language is used.
	 *
	 * @param locale
	 * 		a locale
	 */
	public GwtEntityMapper(String locale) {
		this.locale = locale ?: LocaleUtils.DFLT_LOCALE
	}

	BaseEntity convert(loomp.model.BaseEntity entity) {
		if (!entity)
			throw new NullPointerException("Entity is null")

		BaseEntity converted
		switch (entity) {
			case loomp.model.Annotation:
				converted = new Annotation(
						label: entity.labels?."$locale",
						comment: entity.comments?."$locale",
						propertyUri: entity.property,
                        domainUri:entity.domain)
				break;
			case loomp.model.AnnotationSet:
				converted = new AnnotationSet(
						title: entity.labels?."$locale",
						comment: entity.comments?."$locale")
				break;
			case loomp.model.ElementText:
				converted = new ElementText(
						title: entity.title,
						content: entity.content)
				break;
			default:
				throw new UnsupportedOperationException("Class ${entity.getClass()} is not supported")
		}

		// copy common properties
		converted.uri = entity.uri
		converted.creator = entity.creator
		converted.lastModified = entity.lastModified
		converted.dateCreated = entity.dateCreated

		return converted
	}

	Resource convert(loomp.model.Resource resource) {
		if (!resource)
			throw new NullPointerException("resource is null")

		def converted = new Resource(uri: resource.uri)

		resource.props.each { tpv ->
			def property = tpv.property.toString()
			switch (tpv.value) {
				case String:
					converted.literalProps[property] = [tpv.value]
					break;
				case LocaleLiteralMap:
					converted.literalProps[property] = [tpv.value.getAnyLiteral(locale)]
					break;
				case URI:
					converted.uriProps[property] = [tpv.value.toString()]
					break;

				case java.util.Collection:
					def literals = []
					def uris = []
					tpv.value.each { item ->
						switch (item) {
							case String:
								literals << item
								break;
							case LocaleLiteralMap:
								literals << item.getAnyLiteral(locale)
								break;
							default:
								uris << item.toString()
						}
					}
					converted.literalProps[property] << literals
					converted.uriProps[property] << uris
					break;
				default:
					throw new UnsupportedOperationException("Unable to convert value of class ${tpv.value.getClass()}")
			}

		}
		return converted
	}

	loomp.model.BaseEntity convert(BaseEntity entity) {
		if (!entity)
			throw new NullPointerException("Entity is null")

		loomp.model.BaseEntity converted
		switch (entity) {
			case Annotation:
			case AnnotationSet:
				throw new UnsupportedOperationException("Annotation sets and and annotations are currently not supported")
				break;
			case ElementText:
				converted = new loomp.model.ElementText(
						title: entity.title,
						content: entity.content)
				break;
			default:
				throw new UnsupportedOperationException("Class ${entity.getClass()} is not supported")
		}

		// copy common properties
		converted.uri = entity.uri?.toURI()
		converted.creator = entity.creator?.toURI()
		converted.lastModified = entity.lastModified
		converted.dateCreated = entity.dateCreated

		return converted
	}

	// TODO handle the case when a property has resources and literals as values
	loomp.model.Resource convert(Resource resource) {
		if (!resource)
			throw new NullPointerException("Entity is null")
		def converted = new loomp.model.Resource(uri: resource.uri.toURI())

		converted.props = []
		resource.literalProps.each { prop, values ->
			values.each { literal ->
				converted.props << new TypedPropertyValue(property: prop.toURI(), value: literal, isLiteral: true)
			}
		}
		resource.uriProps.each { prop, values ->
			values.each { literal ->
				converted.props << new TypedPropertyValue(property: prop.toURI(), value: literal.toURI(), isLiteral: false)
			}
		}
		return converted
	}

	String getLanguage() {
		return locale;
	}

	void setLaunguage(String locale) {
		this.locale = locale
	}
}
