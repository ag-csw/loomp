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

import com.hp.hpl.jena.datatypes.xsd.XSDDateTime
import com.hp.hpl.jena.graph.Node
import com.hp.hpl.jena.rdf.model.impl.LiteralImpl
import com.hp.hpl.jena.vocabulary.RDF
import loomp.model.DomainEntity
import loomp.model.LocaleLiteralMap
import loomp.model.TypeMapper
import loomp.model.TypedPropertyValue
import loomp.vocabulary.Loomp
import org.apache.commons.lang.ArrayUtils
import org.apache.commons.lang.StringEscapeUtils
import org.apache.commons.logging.LogFactory
import com.hp.hpl.jena.rdf.model.*

//import grails.converters.JSON -- not used, instead we use gson

/**
 * Util class for filling an instance with the data from an RDF model and to generate
 * a RDF model from an instance. The mapping from the instance to the model has to be
 * available as a hash map [<member> : <RDF property>] in a static member named
 * 'mapping'. To be able to distinguish between Literals and Resources the member have
 * to be of type Resource or Literal.
 */
class JenaUtils {
	private static def log = LogFactory.getLog(JenaUtils.class)

	/**
	 * Create an object from a given model. The model must contain a statement that
	 * indicate the type/domain class. There must not exist multiple distinct subjects
	 * having a rdf:type attribute. The mapping contained in the domain class is used
	 * to assign values to the members. If a member or a property is  not mapped then
	 * it is ignored.
	 *
	 * @param model
	 * 		model containing the data
	 * @return an instance of a domain class
	 */
	static modelToEntity = { Model model ->
		if (!model || model.isEmpty()) return null

		def entity = getDomainClassInstance(model)
		if (!entity) {
			log.error "Unable to create an instance from (non-empty) model"
			def iter = model.listStatements()
			while (iter.hasNext()) { log.error "*** ${iter.nextStatement()}" }
			return null
		}
		def collFields = getCollectionFieldNames(entity)
		def llmFields = getLocaleLiteralMapFieldNames(entity)

		log.debug "Converting model to object"
		entity.mapping.each {k, pred ->
			if (!isSpecialMapping(k)) {
				NodeIterator iter = model.listObjectsOfProperty(resource(entity.uri), property(pred))
				def value

				if (llmFields.contains(k)) {
					// The field k contains literals
					value = new LocaleLiteralMap()
					while (iter.hasNext()) {
						def literal = javaObject(iter.nextNode())
						// handle the case if we expect a LLM but get a simple string
						value.putAll(literal instanceof Map ? literal : new LocaleLiteralMap([null: literal]) )
					}
					if (value.isEmpty()) value = null
				} else if (collFields.contains(k) && !entity.mapping?.__ordered?.contains(k)) {
					// If k is ordered then javaObject() will return a list. So the else part does the job
					value = []
					while (iter.hasNext()) {
						value << javaObject(iter.nextNode())
					}
					if (value.isEmpty()) value = null
				} else {
					value = iter.hasNext() ? javaObject(iter.nextNode()) : null
				}
				entity."$k" = value
				log.trace "** $pred => $k: $value"
			}
		}
		return entity
	}

	/**
	 * Convert a collection of models to entities (@see #modelToEntity).
	 *
	 * @param models
	 * 		a collection of models
	 * @return a collection of entities
	 */
	static modelsToEntities = { Collection<Model> models ->
		def entities = []
		models.each {
			entities << modelToEntity.call(it)
		}
		return entities
	}

	/**
	 * Fill a model with the data of the object. If the object is null or empty then
	 * the model will not be changed. The URI of the object has to be set in advance.
	 *
	 * @param model
	 * 		model to insert the data of the object
	 * @param entity
	 * 		an object to be converted to RDF
	 * @return the model
	 * @throws IllegalArgumentException
	 * @throws NullPointerException
	 */
	static entityToModel = { Model model, DomainEntity entity ->
		if (!entity) return model

		if (!model) throw new NullPointerException("Parameter model is null")
		if (!entity.uri) throw new IllegalArgumentException("Object was not assigned a URI")
		if (!entity?.mapping) throw new IllegalArgumentException("Object does not contain a mapping between member and RDF properties")

		log.debug "Converting object to model"
		def collFields = getCollectionFieldNames(entity)

		entity.mapping.each {k, pred ->
			// ignore keys with special meaning
			def value = isSpecialMapping(k) ? null : entity."$k"
			if (value != null) {
				log.trace "** $k => $pred: $value"
				if (entity.mapping?.__ordered?.contains(k)) {
					// we have a sequence of values which are stored as RDFList
					def nodes = []
					value.each { nodes << node(it) }
					RDFList list = model.createList(nodes.iterator())
					model.add(resource(entity.uri), property(pred), list)
				} else {
					if (value instanceof LocaleLiteralMap) {
						// we have a literal
						value.each { locale, lit -> model.add(resource(entity.uri), property(pred), literal(locale, lit))}
					} else if (collFields.contains(k)) {
						// we a collection
						value.each { model.add(resource(entity.uri), property(pred), node(it)) }
					} else {
						model.add(resource(entity.uri), property(pred), node(value))
					}
				}
				// RDFa processing has to be done separately, because the generated statements have to be stored in a named graph
			}
		}
		return model
	}

	/** @return true iff key is a mapping with a special meaning, e.g., __ordered          */
	static isSpecialMapping(String key) { return key.startsWith("__") }

	/**
	 * Get the names of fields which are declared as Array or implement
	 * the collection interface. Super classes are also considered.
	 *
	 * @param clazz
	 * 		a class
	 * @return list of field names
	 */
	static getCollectionFieldNames(Class clazz) {
		def fields = clazz.declaredFields
		for (def c = clazz.getSuperclass(); c; c = c.getSuperclass()) {
			ArrayUtils.addAll(fields, c.declaredFields)
		}
		def collFieldNames = []
		fields.each {
			def type = it.type
			if (type.isArray() || type.interfaces.find { it == Collection.class })
				collFieldNames << it.name
		}
		return collFieldNames
	}

	static getCollectionFieldNames(Object o) {
		return getCollectionFieldNames(o.class)
	}

	/**
	 * Get the names of fields which are declared {@see LocaleLiteralMap}.
	 *
	 * @param clazz
	 * 		a class
	 * @return list of field names
	 */
	static getLocaleLiteralMapFieldNames(Class clazz) {
		def fields = clazz.declaredFields
		for (def c = clazz.getSuperclass(); c; c = c.getSuperclass()) {
			ArrayUtils.addAll(fields, c.declaredFields)
		}
		def collFieldNames = []
		fields.each {
			if (it.type == LocaleLiteralMap)
				collFieldNames << it.name
		}
		return collFieldNames
	}

	static getLocaleLiteralMapFieldNames(Object o) {
		return getLocaleLiteralMapFieldNames(o.class)
	}

	/**
	 * Creates an instance of Resource from an RDF model. The URI of the resource is
	 * guessed from the model @see #guessMainSubject. All statements having not the
	 * guessed URI as subject are ignored. Important: Lists as values of properties
	 * are currently not supported.
	 *
	 * @param model
	 * 		model to be converted
	 * @return a resource (null if the model is empty)
	 * @throws NullPointerException
	 */
	static modelToResource = { Model model ->
		if (!model) throw new NullPointerException("Parameter model is null")
		if (model.isEmpty()) return null

		// at this point there is at least one statement having a subject
		def uri = guessMainSubject(model)
		def properties = []
		def stIter = model.listStatements(JenaUtils.resource(uri), null, null)
		while (stIter.hasNext()) {
			Statement s = stIter.nextStatement()
			properties << new TypedPropertyValue(
					property: URI.create(s.getPredicate().getURI()),
					value: javaObject(s.getObject()),
					isLiteral: s.getObject().isLiteral())
		}
		return new loomp.model.Resource(uri: uri, props: properties)
	}

	/**
	 * Convert a collection of models to resources (@see #modelToResource).
	 *
	 * @param models
	 * 		a collection of models
	 * @return a collection of resources
	 */
	static modelsToResources = { Collection<Model> models ->
		def resources = []
		models.each {
			resources << modelToResource.call(it)
		}
		return resources
	}

	/**
	 * Create an RDF model from a given resource. If the resource has no properties the
	 * created model is empty.
	 *
	 * @param r
	 * 		a resource
	 * @param model
	 * 		a model to insert the statements
	 * @return the given model
	 * @throws NullPointerException
	 */
	static resourceToModel = { Model model, loomp.model.Resource resource ->
		if (!resource) throw new NullPointerException("Parameter resource is null")
		def subject = loomp.utils.JenaUtils.resource(resource.uri)
		resource.props.each {
			model.add(subject, property(it.property), node(it.value))
		}
		return model
	}

	/**
	 * Convert a model to a string so that it could be used in a sparql query. If
	 * the model is empty then an empty string is returned.
	 *
	 * @param model
	 * 		a model
	 * @return a string representation
	 * @throws NullPointerException
	 */
	static String modelToString(Model model) {
		if (!model) throw new NullPointerException("Parameter model is null")
		if (model.isEmpty()) return ""

		StringBuilder sb = new StringBuilder()
		BNodeGenerator bg = new BNodeGenerator()
		StmtIterator iter = model.listStatements()
		while (iter.hasNext()) {
			Statement s = iter.nextStatement()
			sb.append("${JenaUtils.n2s(bg, s.getSubject())} ${JenaUtils.n2s(bg, s.getPredicate())} ${JenaUtils.n2s(bg, s.getObject())} .\n")
		}
		return sb.toString().trim()
	}

	/**
	 * @see #n2s(Object, Object)
	 */
	static String n2s(node) {
		return n2s(null, node)
	}

	/**
	 * Generate a string representation of a node that can be used in a SPARQL update query.
	 * Strings will be escaped.
	 *
	 * @param generator
	 * 		a generator for creating identifiers of blank nodes
	 * @param node
	 * 		node to be converted
	 * @return string representation
	 */
	static String n2s(generator, node) {
		if (node instanceof URI) {
			return "<${node.toString()}>"
		}
		else if (node instanceof Resource) {
			if (node.isURIResource()) {
				return "<${node.getURI()}>"
			} else if (node.isAnon()) {
				if (!generator) {
					log.error("Cannot create string for blank node, because no generator for URIs is given.")
					return node.toString()
				}
				return "<${generator.getIdentifier(node)}>"
			}
		}
		else if (node instanceof String) {
			return node
		}
		else {
				// if a language is present then toString() returns the literal in a wrong format
				return node.language ?
					"\"${escapeSparql(node.getString())}\"@${node.language}" :
					"\"${escapeSparql(node.toString())}\""
		}
		return null
	}

	/**
	 * Create a Java object from an RDF node.
	 *
	 * @param node
	 * 		node to be converted
	 * @return an object
	 * @throws IllegalArgumentException if node cannot be converted to a Java object
	 */
	static Object javaObject(node) {
		if (node.canAs(RDFList.class)) {
			def list = node.as(RDFList.class).asJavaList()
			return list.collect { URI.create(it.getURI()) }.toArray()
		}
		else if (node.isURIResource()) {
			return URI.create(node.getURI())
		}
		else if (node.isLiteral()) {
			if (node.getLexicalForm().contains("^^")) {
				// This workaround is needed since Jena does not return typed literals but plain
				// literals. node.getValue() should have been enough.
				def tm = com.hp.hpl.jena.datatypes.TypeMapper.getInstance()
				def value = node.getLexicalForm().split("\\^\\^")
				def type = tm.getTypeByName(value[1])
				if (type) {
					def parsedValue = type.parse(value[0])
					if (parsedValue instanceof XSDDateTime) {
						return parsedValue.asCalendar().getTime()
					} else {
						return parsedValue
					}
				} else {
					log.warn "Unknown datatype ${value[1]}"
					return value[0]
				}
			} else if ((node.getLexicalForm() =~ "@(..|..-..)\$")) {
				// This is a workaround for detecting a language tag since Jena does not recognize them
				String s = node.getLexicalForm()
				def index = s.lastIndexOf("@")
				return new LocaleLiteralMap([(s.substring(index + 1)): s.substring(0, index)])
			} else {
				if (node.getValue() instanceof XSDDateTime) {
					return node.getValue().asCalendar().getTime()
				} else if (node.getLanguage()) {
					return new LocaleLiteralMap([(node.getLanguage()): node.getValue()])
				} else {
					return node.getValue()
				}
			}
		}
		else {
			log.error "Unable to assign a blank node to a member of an object $node; returning null instead"
			return null
		}
	}

	/**
	 * Create a resource from a value. If the value is already a resource then it is
	 * returned directly
	 *
	 * @param value
	 * 		value to be converted
	 * @return a resource
	 * @throws IllegalArgumentException if the value is not an instance of Resource, URI, or a String representing an URI
	 */
	static Resource resource(Object value) {
		if (value instanceof Resource) {
			return value
		} else if (value instanceof URI) {
			return ResourceFactory.createResource(value.toString())
		} else {
			throw new IllegalArgumentException("parameter value cannot be converted to a property")
		}
	}

	/**
	 * Create a property from a value. If the value is already a property then it is
	 * returned directly
	 *
	 * @param value
	 * 		value to be converted
	 * @return a property
	 * @throws IllegalArgumentException if the value is not an instance of Property, URI, or a String representing an URI
	 */
	static Property property(Object value) {
		if (value instanceof Property) {
			return value
		}
		else if (value instanceof URI) {
			return ResourceFactory.createProperty(value.toString())
		} else {
			throw new IllegalArgumentException("Unable to convert value to a property: $value")
		}
	}

	/**
	 * Create an RDF node depending on the type of the given value. If the value
	 * is not a resource then a typed isLiteral is created.
	 *
	 * @param value
	 * 		value to be converted
	 * @return an RDF node
	 */
	static RDFNode node(Object value) {
		if (value instanceof RDFNode) {
			return value
		}
		else if (value instanceof URI) {
			return ResourceFactory.createResource(value.toString())
		}
		else if (value instanceof Date) {
			// Jena does only recognize Calendar objects
			def cal = Calendar.getInstance()
			cal.setTime(value)
			return ResourceFactory.createTypedLiteral(cal)
		}
		else {
			return ResourceFactory.createTypedLiteral(value)
		}
	}

	static literal(String locale, String literal) {
		// ResourceFactory does not allow to create a literal with a language
		return new LiteralImpl(Node.createLiteral(literal, locale, false), null)
	}

	/**
	 * Retrieve a list of all subject resources from a model
	 *
	 * @param m
	 * 		a model
	 * @return list of resources
	 */
	static getSubjects(Model m) {
		def iter = m.listSubjects()
		def subjs = []
		while (iter.hasNext()) {
			subjs = iter.nextResource()
		}
		return subjs
	}

	/**
	 * Guess the main subject URI for a model, e.g., to be able to convert it to
	 * a resource. At the moment it is the first subject which is not a blank
	 * node.
	 *
	 * @param m
	 * 		a model
	 * @return a URI
	 */
	static guessMainSubject(Model m) {
		def iter = m.listSubjects()
		while (iter.hasNext()) {
			def r = iter.nextResource()
			if (r.isURIResource())
				return URI.create(r.getURI())
		}
	}

	/**
	 * Create an instance of a domain class (   {@link TypeMapper}   ) from a given model. The first
	 * statement that assigns a known type URI to a resource determines the type of the domain
	 * class. The URI of the instance is set to this subject.
	 *
	 * @param model
	 * 		a model
	 * @return an instance of a domain class
	 */
	static DomainEntity getDomainClassInstance(Model m) {
		def iter = m.listStatements(null, RDF.type, null)
		while (iter.hasNext()) {
			def st = iter.nextStatement()
			def clazz = TypeMapper.instance.getDomainClass(st.getResource().getURI())
			if (clazz) {
				def instance = clazz.newInstance()
				instance.uri = URI.create(st.getSubject().getURI())
				return instance
			}
		}
		return null
	}

	/**
	 * Escape a string or a collection of strings so that it can be used in a SPARQL query.
	 * The string is trimmed.
	 *
	 * @param coll
	 * 		string or a collection of strings
	 * @return collection of escaped strings
	 */
	static escapeSparql(s) {
		if (s instanceof String) {
			return StringEscapeUtils.escapeJava(s).replace("\\/", "/")
		} else {
			def escaped = []
			s.each { escaped << escapeSparql(it.trim())}
			return escaped
		}
	}
}
