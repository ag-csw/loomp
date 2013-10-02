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

import com.hp.hpl.jena.query.QuerySolution
import com.hp.hpl.jena.query.ResultSet
import com.hp.hpl.jena.rdf.model.Model
import com.hp.hpl.jena.rdf.model.ModelFactory
import com.hp.hpl.jena.rdf.model.RDFList
import com.hp.hpl.jena.vocabulary.DC
import com.hp.hpl.jena.vocabulary.RDF
import loomp.model.TypeMapper
import loomp.utils.JenaUtils
import loomp.utils.QueryUtils
import loomp.vocabulary.Loomp
import com.hp.hpl.jena.rdf.model.Property
import org.apache.commons.lang.StringUtils

/**
 * Service for accessing the data model of loomp, e.g., Document, Element.
 */
class LoompService {
	final String DBPEDIA_GRAPH = "http://dbpedia.org"

	boolean transactional = false
	def sparqlService
	def uriService

	/**
	 * List the object values (e.g., resources and literals) of a uri. If a set of
	 * properties are given then only these properties will be considered.
	 *
	 * @param uri
	 * 		a URI
	 * @param properties
	 * 		a set of property URIs
	 * @param offset (in params)
	 *      where to start
	 * @param max (in params)
	 *      how many
	 * @param sort (in params)
	 * 		URI of the property for sorting the result list (null = object values)
	 * @param sortLocale (in params)
	 * 		locale used for sorting
	 * @return a set of values
	 */
	def listObjects(uri, properties, params) {
		if (!uri) throw new NullPointerException("uri is null")
		log.info "Retrieving type for $uri"
		final query = QueryUtils.limOffOrd("""
			PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
			SELECT DISTINCT ?uri
			WHERE {
				<$uri> ?p ?uri .
				${QueryUtils.createOrFilterEqual("?p", properties)}
			}""", params)
		sparqlService.runSelectQuery(query, { ResultSet rs ->
			def values = []
			while (rs.hasNext()) {
				def qs = rs.nextSolution()
				values << JenaUtils.javaObject(qs.get("uri"))
			}
			return values ? values : null  // from closure
		})
	}

	/**
	 * Get the version number of uri, e.g., extract the version of the value of hasVersion
	 * and increase the number by one. If the uri has multiple hasVersion properties then
	 * only the first one is considered.
	 *
	 * @param uri
	 * 		a URI
	 * @return version number or null if unknown
	 */
	def getVersion(uri) {
		def v = listObjects(uri, Loomp.dbVersion, [:])
		return v ? v[0].toString() as Long : null
	}

	/**
	 * Lookup the types of a given URI. Null is returned if the type is unknown
	 *
	 * @param uri
	 * 		a URI
	 * @param offset (in params) 
	 *      where to start
	 * @param max (in params)
	 *      how many
	 * @param sort (in params)
	 * 		URI of the property for sorting the result list (null = uri)
	 * @return a list URIs or null
	 */
	def getTypes(uri, params) {
		return listObjects(uri, RDF.type, params)
	}

	/**
	 * Lookup the types of an entity identified by URI. Null is returned if the
	 * type is unknown or the URI does not refer to an entity.
	 *
	 * @param uri
	 * 		a uri
	 * @return type URI of an entity or null
	 */
	def URI getEntityType(uri) {
		def type = null
		getTypes(uri, [:]).each {
			if (TypeMapper.instance.getDomainClass(it)) {
				type = it
				return // leave the closure of each
			}
		}
		return type
	}

	/**
	 * Retrieve all statements with a given uri as subject.
	 *
	 * @param uri
	 * 		uri of the data
	 * @param graphUri
	 * 		graph to be queried
	 * @return a model
	 * @throws NullPointerException
	 * @throws IllegalArgumentException
	 */
	def Model getData(uri, graphUri = null) {
		return getData(uri, null, graphUri)
	}

	/**
	 * Retrieve all statements with a given uri as subject. If a type URI is
	 * provided then ensure that the subject has the given type.
	 *
	 * @param uri
	 * 		uri of the data
	 * @param typeUri
	 * 		uri of a type (optional)
	 * @param graphUri
	 * 		graph to be queried
	 * @return a model
	 * @throws NullPointerException
	 */
	def getData(uri, typeUri, graphUri) {
		if (!uri) throw new NullPointerException("uri is null")
		if (graphUri) {
			log.info "Retrieving data of $uri only from $graphUri"
			final query = """
				PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
				SELECT ?p ?o
				WHERE {
					GRAPH <$graphUri> {
						<$uri> ?p ?o .
						${typeUri ? "<$uri> rdf:type <$typeUri> ." : ""}
					}
				}"""
			def model = ModelFactory.createDefaultModel()
			sparqlService.runSelectQuery(query, { ResultSet rs ->
				def s = model.createResource(uri.toString())
				while (rs.hasNext()) {
					QuerySolution qs = rs.next()
					model.add(s, qs.get("p").as(Property.class), qs.get("o"))
				}
			})
			return model
		} else {
			log.info "Retrieving data of $uri"
			final query = """
				PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
				DESCRIBE <$uri>
				WHERE {
					${typeUri ? "<$uri> rdf:type <$typeUri> ." : ""}
				}"""
			return sparqlService.runDescribeQuery(query)
		}
	}

	/**
	 * Retrieve all statement with a given uri as subject. Additionally, retrieve
	 * all statements of the resources referenced by the given properties. If a
	 * type URI is provided then ensure that the subject has the given type.
	 *
	 * Only non-empty models are returned.
	 *
	 * @param uri
	 * 		uri of the data
	 * @param properties
	 * 		a set of properties
	 * @param typeUri
	 * 		uri of a type (optional)
	 * @return a set of models
	 */
	def getDataFull(uri, properties, typeUri) {
		def models = []
		Model parent = getData(uri, typeUri)
		models << parent
		properties.each { prop ->
			def iter = parent.listObjectsOfProperty(JenaUtils.property(prop))
			while (iter.hasNext()) {
				def node = iter.nextNode()
				if (node.isLiteral()) continue
				if (node.canAs(RDFList.class)) {
					def list = node.as(RDFList.class).asJavaList()
					list.each { models << getData(it.getURI()) }
				} else {
					models << getData(node.getURI())
				}
			}
		}
		return models.findAll { !it.isEmpty() }
	}

	/**
	 * Generic method to obtain resources which satisfy a given map of property value
	 * constraints. If property or [values] of the properties is null or empty then it
	 * is treated as variable. Only one key in the map may be null. A value must be of
	 * type java.net.URI if it should match a resource (otherwise it is treated as String).
	 *
	 * @param constraints
	 * 		a map of <property, [values]>
	 * @param offset (in params)
	 *      where to start
	 * @param max (in params)
	 *      how many
	 * @param sort (in params)
	 * 		URI of the property for sorting the result list (null = uri)
	 * @param sortLocale (in params)
	 * 		locale used for sorting
	 * @return a list of models, each representing an entity
	 * @throws NullPointerException
	 */
	def getAllData(Map constraints, params) {
		if (!constraints) throw new NullPointerException("constraints is null")
		log.info "Retrieving data by contraints $constraints"
		final query = QueryUtils.limOffOrd("""
			PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
			SELECT DISTINCT ?uri
			WHERE {
				${QueryUtils.createGraphPattern("?uri", constraints)}
			}""", params)
		sparqlService.runSelectQuery(query, { ResultSet rs ->
			def models = []
			while (rs.hasNext()) {
				QuerySolution qs = rs.next()
				models << getData(qs.get("uri"))
			}
			return models.findAll { !it.isEmpty() }  // from closure
		})
	}

	/**
	 * Lookup a given URI and return the newest documents from that location.
	 *
	 * Only non-empty models are returned.
	 *
	 * @param uri
	 * 		uri of the data
	 * @param typeUri
	 * 		uri of a type
	 * @param count
	 *      count from the newest to return
	 * @return a list of models, each representing an entity
	 * @throws NullPointerException
	 * TODO: Check, assuming dc:modified is used to find newest docs
	 */
	def getNewestData(uri, typeUri, count) {
		if (!uri) throw new NullPointerException("uri is null")
		if (!typeUri) throw new NullPointerException("typeUri is null")
		log.info "Retrieving data of $uri"
		final query = """
			PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
			PREFIX dc:  <http://purl.org/dc/elements/1.1/>
			DESCRIBE ?uri
			WHERE {
				?uri rdf:type c .
				?uri dc:modified ?date .
			}
			ORDER BY DESC (?date)
			${count ? "LIMIT $count" : ""}"""
		def models = []
		def rs = sparqlService.runDescribeQuery(query)
		while (rs.hasNext()) {
			QuerySolution qs = rs.next()
			models << getData(qs.get("uri"), typeUri)
		}
		return models.findAll { !it.isEmpty() }
	}

	/**
	 * Get all resources referencing a given URI. If typeUri is set then
	 * the retrieved resources must have that type.
	 *
	 * Only non-empty models are returned.
	 *
	 * @param uri
	 * 		URI of the object resource
	 * @param property
	 * 		URI of a property
	 * @param types
	 * 		a set of type URI. One of these the child must have (optional)
	 * @param offset (in params)
	 *      where to start
	 * @param max (in params)
	 *      how many
	 * @param sort (in params)
	 * 		URI of the property for sorting the result list (null = uri)
	 * @param sortLocale (in params)
	 * 		locale used for sorting
	 * @return list of element URIs
	 * @throws NullPointerException
	 */
	def getByPropertyAndResource(uri, property, types, params) {
		if (!uri) throw new NullPointerException("uri is null")
		log.info "Retrieving containing elements of $uri"
		final query = QueryUtils.limOffOrd("""
			PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
			SELECT DISTINCT ?uri
			WHERE {
				?uri <$property> <$uri> .
				${QueryUtils.typePart(types)}
			}""", params)
		sparqlService.runSelectQuery(query, { ResultSet rs ->
			def models = []
			while (rs.hasNext()) {
				QuerySolution qs = rs.next()
				models << getData(qs.get("uri"))
			}
			return models.findAll { !it.isEmpty() }  // from closure
		})
	}

	/**
	 * Get all elements that contain a given resource URIs and resources with given annotations.
	 *
	 * Only non-empty models are returned.
	 *
	 * @param uris
	 * 		URIs of resources
	 * @param auris
	 * 		URIs of annotations
	 * @param offset (in params)
	 *      where to start
	 * @param max (in params)
	 *      how many
	 * @param sort (in params)
	 * 		URI of the property for sorting the result list (null = uri)
	 * @param sortLocale (in params)
	 * 		locale used for sorting
	 * @return set of element URIs
	 * @throws NullPointerException
	 */
	def getEntitiesByResources(uris, auris, params) {
		if (!uris && !auris) throw new NullPointerException("One of uris and auris must not be null")

		log.info "Retrieving elements containing resource $uris and annotations $auris"
		final query = QueryUtils.limOffOrd("""
			PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
			PREFIX loomp: <${Loomp.NS}>
			SELECT DISTINCT ?uri
			WHERE {
				${createAndAnnotationFilter(auris, "?uri")}
				${QueryUtils.createSubjectPatterns(uris, "?uri")}
			}""", params)
		sparqlService.runSelectQuery(query, { ResultSet rs ->
			def models = []
			while (rs.hasNext()) {
				QuerySolution qs = rs.next()
				models << getData(qs.get("uri"))
			}
			return models.findAll { !it.isEmpty() }  // from closure
		})
	}

	def getEntitiesByResourcesCount(uris, auris) {
		if (!uris && !auris) throw new NullPointerException("One of uris and auris must not be null")

		log.info "Counting elements containing resource $uris and annotations $auris"
		final query = """
			PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
			PREFIX loomp: <${Loomp.NS}>
			SELECT count(DISTINCT ?uri)
			WHERE {
				${createAndAnnotationFilter(auris, "?uri")}
				${QueryUtils.createSubjectPatterns(uris, "?uri")}
			}"""
		sparqlService.runSelectQuery(query, { ResultSet rs ->
			QuerySolution qs = rs.next()
			return JenaUtils.javaObject(qs.getLiteral(".1"))
		})
	}

	/**
	 * Create the part of a query that ensures that returned resources are annotated with all
	 * given annotations.
	 *
	 * @param uris
	 * 		URIs of annotations
	 * @param graph
	 * 		URI or variable of the graph to be queried
	 * @return
	 */
	private final createAndAnnotationFilter(Collection uris, graph) {
		def outer = ""
		def inner = ""
		uris.eachWithIndex {uri, i ->
			outer += "<$uri> loomp:annotationProperty ?ap${i} .\n"
			outer += "OPTIONAL { <$uri> loomp:annotationDomain ?ad${i} . }\n"
			inner += "?as${i} ?ap${i} ?ao${i} .\n"
			inner += "OPTIONAL { ?as${i} rdf:type ?domain${i} . }\n"
			inner += "FILTER ((bound(?ad${i}) && bound(?domain${i}) && ?ad${i} = ?domain${i}) || !bound(?ad${i})) . \n"
		}

		return """
			${outer}
			${QueryUtils.withGraphClause(graph, inner)}"""
	}

	/**
	 * Create the part of a query that ensures that returned resources have at least one
	 * of the a given annotations. If collections is null or empty then the generated clause
	 * ensures that ?p is bound to an annotation property.
	 *
	 * SubjVar = ?s; PropVar = ?p; ObjVar = ?o
	 *
	 * @param uris
	 * 		URIs of annotations
	 * @param graph
	 * 		URI or variable of the graph to be queried
	 * @return
	 */
	private final createOrAnnotationFilter(Collection uris, graph) {
		def outer = ""
		def filter = ""
		if (!uris) {
			outer += "?as loomp:annotationProperty ?ap .\n"
			outer += "OPTIONAL { ?as loomp:annotationDomain ?ad . }\n"
			filter += "(?p = ?ap && (!bound(?ad) || ?od = ?ad)) || "
		} else {
			uris.eachWithIndex {uri, i ->
				outer += "<$uri> loomp:annotationProperty ?ap${i} .\n"
				outer += "OPTIONAL { <$uri> loomp:annotationDomain ?ad${i} . }\n"
				filter += "(?p = ?ap${i} && (!bound(?ad${i}) || ?od = ?ad${i})) || "
			}
		}

		return """
			${outer}
			${QueryUtils.withGraphClause(graph, "?uri ?p ?o . OPTIONAL { ?uri rdf:type ?od . }\n")}
			FILTER (${StringUtils.removeEnd(filter, " || ")}) ."""
	}

	/**
	 * Get all entities that have a given list entry in a given property. If
	 * an type URI is given then the parent entities are restricted to that type.
	 *
	 * Only non-empty models are returned.
	 *
	 * @param uri
	 * 		uri of the data
	 * @param property
	 * 		URI of the property to be accessed
	 * @param types
	 * 		a set of type URI. One of these the child must have (optional)
	 * @param offset (in params)
	 *      where to start
	 * @param max (in params)
	 *      how many
	 * @param sort (in params)
	 * 		URI of the property for sorting the result list (null = uri)
	 * @param sortLocale (in params)
	 * 		locale used for sorting
	 * @return set of element URIs
	 * @throws NullPointerException
	 */
	def getParentsByListEntry(uri, property, types, params) {
		if (!uri) throw new NullPointerException("uri is null")
		if (!property) throw new NullPointerException("property is null")

		log.info "Retrieving containing elements of $uri"
		// namespace list and list handling is very specific to Jena
		final query = QueryUtils.limOffOrd("""
			PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
			PREFIX list: <http://jena.hpl.hp.com/ARQ/list#>
			SELECT DISTINCT ?uri
			WHERE {
				?uri <$property> ?elements .
				?elements list:member <$uri> . 
				${QueryUtils.typePart(types)}
			}""", params)
		sparqlService.runSelectQuery(query, { ResultSet rs ->
			def models = []
			while (rs.hasNext()) {
				QuerySolution qs = rs.next()
				models << getData(qs.get("uri"))
			}
			return models.findAll { !it.isEmpty() }  // from closure
		})
	}

	/**
	 * Get all list entries of a given property of an entity without loading
	 * the parent entity itself. If childConstraints are given then only the
	 * children are retrieved satisfying the constraints (@see getAllData).
	 *
	 * Only non-empty models are returned.
	 *
	 * @param uri
	 * 		URI of the parent entity
	 * @param property
	 * 		URI of the property to be accessed
	 * @param childConstraints
	 * 		constraints that the child has to satisfy
	 * @param offset (in params)
	 *      where to start
	 * @param max (in params)
	 *      how many
	 * @param sort (in params)
	 * 		URI of the property for sorting the result list (null = uri)
	 * @param sortLocale (in params)
	 * 		locale used for sorting
	 * @return set of "child" entities
	 * @throws NullPointerException
	 */
	// TODO introduce parentConstraints
	def getListEntriesByParent(uri, property, Map childConstraints, params) {
		if (!uri) throw new NullPointerException("uri is null")
		if (!property) throw new NullPointerException("property is null")

		log.info "Retrieving child entities of $uri"
		// namespace list and list handling is very specific to Jena
		final query = QueryUtils.limOffOrd("""
			PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
			PREFIX list: <http://jena.hpl.hp.com/ARQ/list#>
			SELECT DISTINCT ?uri
			WHERE {
				<$uri> <$property> ?elements .
				?elements list:member ?uri .
				${QueryUtils.createGraphPattern("?uri", childConstraints)}
			}""", params)
		return sparqlService.runSelectQuery(query, { ResultSet rs ->
			def models = []
			while (rs.hasNext()) {
				QuerySolution qs = rs.next()
				models << getData(qs.get("uri"))
			}
			return models.findAll { !it.isEmpty() }  // from closure
		})
	}

	/**
	 * Get the resources that are contained in the given entity, e.g., all subjects
	 * of the named graph uri.
	 *
	 * @param uri
	 * 		URI of the entity
	 * @param childConstraints
	 * 		constraints applied to the retrieved resources
	 * @param graphOnly
	 * 		if set to true then only the statements of the named graph are retrieved
	 * @param offset (in params)
	 *      where to start
	 * @param max (in params)
	 *      how many
	 * @param sort (in params)
	 * 		URI of the property for sorting the result list (null = uri)
	 * @param sortLocale (in params)
	 * 		locale used for sorting
	 * @return a list of resources
	 */
	def getContainedResources(uri, Map childConstraints, params, graphOnly = false) {
		if (!uri) throw new NullPointerException("uri is null")

		log.info "Retrieving resources contained in entity $uri"
		final query = QueryUtils.limOffOrd("""
			PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
			SELECT DISTINCT ?uri
			WHERE { GRAPH <$uri> {
					?uri ?p ?o
					${QueryUtils.createGraphPattern("?uri", childConstraints)}
				}
			}""", params)
		return sparqlService.runSelectQuery(query, { ResultSet rs ->
			def models = []
			while (rs.hasNext()) {
				QuerySolution qs = rs.next()
				models << getData(qs.get("uri"), graphOnly ? uri : null)
			}
			return models.findAll { !it.isEmpty() }  // from closure
		})
	}

	/**
	 * Search a given string in the database. If properties is not empty then only
	 * the given properties are searched. If types is not empty then only entities of
	 * the given types are searched.
	 *
	 * Only non-empty models are returned.
	 *
	 * @param search
	 * 		a search string
	 * @param properties
	 * 		a set of property URIs
	 * @param types
	 * 		a set of type URIs
	 * @param inclExtern
	 * 		include external data sources
	 * @param offset (in params)
	 *      where to start
	 * @param max (in params)
	 *      how many
	 * @param sort (in params)
	 * 		URI of the property for sorting the result list (null = uri)
	 * @param sortLocale (in params)
	 * 		locale used for sorting
	 * @return a set of models
	 */
	// Note: 'search' seems to be a reserved word in grails

	def searchIt(String search, Collection properties, types, boolean inclExtern, params) {
		if (!search) return []
		log.info "Searching data for $search"
		def escapedSearchTokens = JenaUtils.escapeSparql(QueryUtils.splitSearchString(search))
		final query = QueryUtils.limOffOrd("""
				PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
				SELECT DISTINCT ?uri
				WHERE {
					GRAPH ?g {
						?uri ?p ?o .
						${QueryUtils.createAndFilterRegex("?o", escapedSearchTokens, true)}
						${QueryUtils.typePart(types)}
						${QueryUtils.createOrFilterEqual("?p", properties)}
					}
					${inclExtern ? "" : "FILTER (?g != <$DBPEDIA_GRAPH>)" }
				}""", params)
		sparqlService.runSelectQuery(query, { ResultSet rs ->
			def models = []
			while (rs.hasNext()) {
				QuerySolution qs = rs.next()
				models << getData(qs.get("uri"))
			}
			return models.findAll { !it.isEmpty() }  // from closure
		})
	}

	def searchItCount(String search, Collection properties, types, boolean inclExtern) {
		if (!search) return []
		log.info "Searching data for $search"
		def escapedSearchTokens = JenaUtils.escapeSparql(QueryUtils.splitSearchString(search))
		final query = """
				PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
				SELECT count(DISTINCT ?uri)
				WHERE {
					GRAPH ?g {
						?uri ?p ?o .
						${QueryUtils.createAndFilterRegex("?o", escapedSearchTokens, true)}
						${QueryUtils.typePart(types)}
						${QueryUtils.createOrFilterEqual("?p", properties)}
					}
					${inclExtern ? "" : "FILTER (?g != <$DBPEDIA_GRAPH>)" }
				}"""
		sparqlService.runSelectQuery(query, { ResultSet rs ->
			QuerySolution qs = rs.next()
			return JenaUtils.javaObject(qs.getLiteral(".1"))
		})
	}

	/**
	 * Search a given string in the database. If properties is not empty then only
	 * the given properties are searched. If types is not empty then only entities of
	 * the given types are searched.
	 *
	 * Only non-empty models are returned.
	 *
	 * @param search
	 * 		a search string
	 * @param annotations
	 * 		a set of annotation URIs
	 * @param inclExtern
	 * 		include external data sources
	 * @param offset (in params)
	 *      where to start
	 * @param max (in params)
	 *      how many
	 * @param sort (in params)
	 * 		URI of the property for sorting the result list (null = uri)
	 * @param sortLocale (in params)
	 * 		locale used for sorting
	 * @return a set of models
	 */
	// Note: 'search' seems to be a reserved word in grails

	def searchResources(String search, Collection annotations, boolean inclExtern, params) {
		if (!search) return []
		log.info "Searching data for $search"
		def escapedSearchTokens = JenaUtils.escapeSparql(QueryUtils.splitSearchString(search))
		final query = QueryUtils.limOffOrd("""
				PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
				PREFIX loomp: <${Loomp.NS}>
				SELECT DISTINCT ?uri
				WHERE {
					${createOrAnnotationFilter(annotations, "?g")}
					${QueryUtils.createAndFilterRegex("?o", escapedSearchTokens, true)}
					${inclExtern ? "" : "FILTER (?g != <$DBPEDIA_GRAPH>)" }
				}""", params)
		sparqlService.runSelectQuery(query, { ResultSet rs ->
			def models = []
			while (rs.hasNext()) {
				QuerySolution qs = rs.next()
				models << getData(qs.get("uri"))
			}
			return models.findAll { !it.isEmpty() }  // from closure
		})
	}

	def searchResourcesCount(String search, Collection annotations, boolean inclExtern) {
		if (!search) return []
		log.info "Searching data for $search"
		def escapedSearchTokens = JenaUtils.escapeSparql(QueryUtils.splitSearchString(search))
		final query = """
				PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
				PREFIX loomp: <${Loomp.NS}>
				SELECT count(DISTINCT ?uri)
				WHERE {
					${createOrAnnotationFilter(annotations, "?g")}
					${QueryUtils.createAndFilterRegex("?o", escapedSearchTokens, true)}
					${inclExtern ? "" : "FILTER (?g != <$DBPEDIA_GRAPH>)" }
				}"""
		sparqlService.runSelectQuery(query, { ResultSet rs ->
			QuerySolution qs = rs.next()
			return JenaUtils.javaObject(qs.getLiteral(".1"))
		})
	}

	/**
	 * Get the annotations of the resources that would be found by #search.
	 *
	 * Only non-empty models are returned.
	 *
	 * @param search
	 * 		a search string
	 * @param properties
	 * 		a set of property URIs
	 * @param types
	 * 		a set of RDF nodes or URIs
	 * @param offset (in params)
	 *      where to start
	 * @param max (in params)
	 *      how many
	 * @param sort (in params)
	 * 		URI of the property for sorting the result list (null = uri)
	 * @param sortLocale (in params)
	 * 		locale used for sorting
	 * @return a set of models
	 */
	def annotationsOfSearch(String search, Collection properties, types, params) {
		if (!search) return []
		log.info "Searching data for $search"
		def escapedSearchTokens = JenaUtils.escapeSparql(QueryUtils.splitSearchString(search))
		final query = QueryUtils.limOffOrd("""
				PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
				PREFIX loomp: <${Loomp.NS}>
				SELECT DISTINCT ?uri
				WHERE {
					?uri rdf:type loomp:Annotation .
					?uri loomp:annotationProperty ?ap .
					OPTIONAL {
						?uri loomp:annotationDomain ?ad .
					}
					GRAPH ?g {
						?s ?ap ?o .
						OPTIONAL {
							?s rdf:type ?domain .
						}
						${QueryUtils.createAndFilterRegex("?o", escapedSearchTokens, true)}
						${QueryUtils.typePart(types)}
						${QueryUtils.createOrFilterEqual("?ap", properties)}
						FILTER ((bound(?ad) && bound(?domain) && ?ad = ?domain) || !bound(?ad)) .
					}
					FILTER (?g != <$DBPEDIA_GRAPH>)
				}""", params)
		sparqlService.runSelectQuery(query, { ResultSet rs ->
			def models = []
			while (rs.hasNext()) {
				QuerySolution qs = rs.next()
				models << getData(qs.get("uri"))
			}
			return models.findAll { !it.isEmpty() }  // from closure
		})
	}

	/**
	 * Search text elements for the given search string.
	 *
	 * @param search
	 * 		a seach string
	 * @param offset (in params)
	 *      where to start
	 * @param max (in params)
	 *      how many
	 * @param sort (in params)
	 * 		URI of the property for sorting the result list (null = uri)
	 * @param sortLocale (in params)
	 * 		locale used for sorting
	 * @return a set of models representing text elements
	 */
	def searchElementText(String search, params) {
		return searchIt(search, [DC.title, Loomp.hasRdfaContent], [Loomp.ElementText], params)
	}

	def searchElementTextCount(String search) {
		return searchItCount(search, [DC.title, Loomp.hasRdfaContent], [Loomp.ElementText])
	}

	/**
	 * Save a resource. If the resource does not exist then a new one will be created.
	 *
	 * @param uri
	 * 		uri of the data
	 * @param model
	 * 		the model containing the data
	 * @param graphUri
	 * 		URI of the graph to store the instance (null => default graph)
	 * @return uri of the saved data or null if save operation failed
	 */
	def saveData(uri, Model model, graphUri = null) {
		return !uri || isSubject(uri) ? updateData(uri, model, graphUri) : insertData(uri, model, graphUri)
	}

	/**
	 * Change an existing resource. If uri is null (and graphUri is not) then all statements
	 * are deleted from the named graph. Otherwise, only the statements with uri as subject are
	 * removed.
	 *
	 * @param uri
	 * 		uri of the data
	 * @param model
	 * 		the model containing the data
	 * @param graphUri
	 * 		URI of the graph to store the instance (null => default graph)
	 * @return uri of the saved data or null if save operation failed
	 * @throws NullPointerException
	 */
	def updateData(uri, Model model, graphUri = null) {
		if (!uri && !graphUri) throw new NullPointerException("Uri is null")
		if (!model) throw new NullPointerException("Model is null")

		log.info(graphUri ? "Modifying data of $uri in named graph $graphUri" : "Modifying data of entity $uri")
		// TODO handle two concurrent updates, e.g., compare the lastModified of the entity contained in the given model with the lastModified of the entity in the SPARQL endpoint
		// TODO look if there is a way to lock a resource or triples in SPARQL Update or joseki
		def query
		if (graphUri) {
			// performing a delete on a named graph does not seem to work :-(
			if (!clear(graphUri)) {
				log.error "Unable to clear named graph $graphUri"
				return null
			}
			query = """
				INSERT DATA INTO <$graphUri> {
					${JenaUtils.modelToString(model)}
				}"""
		} else {
			query = """
				MODIFY DELETE { <$uri> ?p ?o . }
				INSERT {
					${JenaUtils.modelToString(model)}
				} WHERE { <$uri> ?p ?o . }"""
		}
		return sparqlService.runUpdateQuery(query) ? uri : null
	}

	/**
	 * Create an new statements.
	 *
	 * @param data
	 * 		data in RDF pattern syntax
	 * @param graphUri
	 * 		URI of the graph to store the instance (null => default graph)
	 * @return true if the update was successful, false otherwise
	 * @throws NullPointerException
	 */
	def insertData(Model model, graphUri = null) {
		log.info "Inserting bulk data"
		// TODO handle prefixes appropriately: Should the data only contain absolute URIs?
		final query = """
			INSERT DATA ${graphUri ? "INTO <$graphUri>" : ""} {
				${JenaUtils.modelToString(model)}
			}"""
		return sparqlService.runUpdateQuery(query)
	}

	/**
	 * Create an new statements.
	 *
	 * @param uri
	 * 		uri of the data
	 * @param data
	 * 		data in RDF pattern syntax
	 * @param graphUri
	 * 		URI of the graph to store the instance (null => default graph)
	 * @return uri of the saved data or null if save operation failed
	 * @throws NullPointerException
	 */
	def insertData(uri, Model model, graphUri = null) {
		if (!uri)
			throw new NullPointerException("uri is null")

		log.info(graphUri ? "Inserting data into named graph $graphUri" : "Inserting data for entity $uri")
		// TODO handle prefixes appropriately: Should the data only contain absolute URIs?
		if (graphUri) {
			if (!createGraph(graphUri)) {
				log.error "Unable to create named graph $graphUri"
				return null
			}
		}

		final query = """
			INSERT DATA ${graphUri ? "INTO <$graphUri>" : ""} {
				${JenaUtils.modelToString(model)}
			}"""
		// TODO handle an exception if another user inserted this URI just before inserting the given model
		return sparqlService.runUpdateQuery(query) ? uri : null
	}

	/**
	 * Create a named graph with a given URI.
	 * @param uri
	 * 		uri of the graph
	 * @return true iff successful
	 */
	def createGraph(uri) {
		if (!uri)
			throw new NullPointerException("uri is null")

		final query = "CREATE SILENT GRAPH <$uri>"
		return sparqlService.runUpdateQuery(query)
	}

	/**
	 * Create a resource that is attached (e.g., <entity> <containsResource> <resource>)
	 * to an entity. This method is not intended to create entities - use #saveData()
	 * instead.
	 *
	 * @param uri
	 * 		uri of the data
	 * @param typeUri
	 * 		type of the resource
	 * @param entityUri
	 * 		entity that contains the resource
	 * @return URI of the resource if successful, otherwise null
	 */
	def createResource(uri, typeUri, entityUri) {
		def model = ModelFactory.createDefaultModel()
		model.add(JenaUtils.resource(uri), RDF.type, JenaUtils.resource(typeUri))
		model.add(JenaUtils.resource(entityUri), JenaUtils.property(Loomp.containsResource), JenaUtils.resource(uri))
		log.info "Create resource $uri of type $typeUri contained in $entityUri"
		insertData(uri, model)
	}

	/**
	 * Delete all statements with the given URI as subject and the graph named uri.
	 *
	 * @param uri
	 * 		uri of the data
	 * @return true if successful
	 * @throws NullPointerException
	 */
	def deleteData(uri) {
		if (!uri) throw new NullPointerException("uri is null")
		log.info "Deleting data of $uri"
		if (!dropGraph(uri))
			log.error "Error while dropping named graph $uri"
		final query = "DELETE { ?s ?p ?o } WHERE { ?s ?p ?o . FILTER ( ?s = <$uri> )}"
		return sparqlService.runUpdateQuery(query)
	}

	/**
	 * Delete all statements with the given URI as subject having a given type and the graph named uri.
	 *
	 * @param uri
	 * 		of the data
	 * @param typeUri
	 * 		uri of a type
	 * @return true if successful
	 * @throws NullPointerException
	 */
	def deleteData(uri, typeUri) {
		if (!uri) throw new NullPointerException("uri is null")
		if (!typeUri) throw new NullPointerException("typeUri is null")
		log.info "Deleting data of $uri; Type $typeUri"
		if (!dropGraph(uri))
			log.error "Error while dropping named graph $uri"
		final query = """
			PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
			DELETE { ?s ?p ?o }
			WHERE {
				<$uri> rdf:type <$typeUri> ;
					?p ?o .
			}"""
		return sparqlService.runUpdateQuery(query)
	}

	/**
	 * Drop the named graph uri.
	 *
	 * @param uri
	 * 		uri of the named graph
	 * @return true if successful
	 * @throws NullPointerException
	 */
	def dropGraph(uri) {
		if (!uri) throw new NullPointerException("uri is null")
		log.info "Dropping named graph $uri"
		final query = "DROP SILENT GRAPH <$uri>"
		return sparqlService.runUpdateQuery(query)
	}

	/**
	 * Drop all graphs from the database.
	 */
	def dropAllGraphs() {
		final query = "SELECT DISTINCT ?g WHERE { GRAPH ?g { ?s ?p ?o } }"
		return sparqlService.runSelectQuery(query, { ResultSet rs ->
			while (rs.hasNext()) {
				QuerySolution qs = rs.nextSolution()
				dropGraph(qs.getResource("g"))
			}
		})
	}

	/**
	 * Count the triples on the SPARQL endpoint.
	 *
	 * @param typeUri
	 * 		uri of a type
	 * @return triples count
	 */
	def countData(typeUri) {
		if (!typeUri) throw new NullPointerException("typeUri is null")
		log.info "Counting data of type $typeUri"
		final query = """
			PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
			SELECT count(*)
			WHERE {
				?s rdf:type <$typeUri> .
			}
			"""
		// there should always be a result
		sparqlService.runSelectQuery(query, { rs ->
			def qs = rs.nextSolution()
			return qs.getLiteral(qs.varNames().next()).getValue()  // from closure
		})
	}

	/**
	 * Count the list entries of a given property of an entity without loading
	 * the parent entity itself. If typeUri is given then the counted entries
	 * must have that type.
	 *
	 * @param uri
	 * 		URI of the parent entity
	 * @param property
	 * 		URI of the property to be accessed
	 * @param childConstraints
	 * 		constraints that the child has to satisfy
	 * @return number of "child" entities
	 * @throws NullPointerException
	 */
	def countEntriesByParent(uri, property, childConstraints) {
		if (!uri) throw new NullPointerException("uri is null")
		if (!property) throw new NullPointerException("property is null")

		log.info "Counting child entities of $uri"
		// namespace list and list handling is very specific to Jena
		final query = """
			PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
			PREFIX list: <http://jena.hpl.hp.com/ARQ/list#>
			SELECT count(*)
			WHERE {
				<$uri> <$property> ?elements .
				?elements list:member ?uri .
				${QueryUtils.createGraphPattern("?uri", childConstraints)}
			}"""
		// there should always be a result
		return sparqlService.runSelectQuery(query, { ResultSet rs ->
			def qs = rs.nextSolution()
			return qs.getLiteral(qs.varNames().next()).getValue()  // from closure
		})
	}

	/**
	 * Check if a URI already exists.
	 *
	 * @param uri
	 * 		a URI (will not be validated)
	 * @return true iff a URI exists
	 */
	def existsUri(uri) {
		if (!uri) throw new NullPointerException("uri is null")

		final query = "ASK WHERE { ?s ?p ?o . FILTER (?s = <$uri> || ?p = <$uri> || ?o = <$uri>) }"
		return sparqlService.runAskQuery(query)
	}

	/**
	 * Check if a URI exists and is the subject of a statement.
	 *
	 * @param uri
	 * 		a URI (will not be validated)
	 * @return true iff  uri is a subject of a statement
	 */
	def isSubject(uri) {
		if (!uri) throw new NullPointerException("uri is null")

		final query = "ASK WHERE { <$uri> ?p ?o . }"
		return sparqlService.runAskQuery(query)
	}

	/**
	 * Check if a URI exists and is the predicate of a statement.
	 *
	 * @param uri
	 * 		a URI (will not be validated)
	 * @return true iff  uri is a predicate of a statement
	 */
	def isPredicate(uri) {
		if (!uri) throw new NullPointerException("uri is null")

		final query = "ASK WHERE { ?s <$uri> ?o . }"
		return sparqlService.runAskQuery(query)
	}

	/**
	 * Check if a URI exists and is the object of a statement.
	 *
	 * @param uri
	 * 		a URI (will not be validated)
	 * @return true iff  uri is a object of a statement
	 */
	def isObject(uri) {
		if (!uri) throw new NullPointerException("uri is null")

		final query = "ASK WHERE { ?s ?p <$uri> . }"
		return sparqlService.runAskQuery(query)
	}

	/**
	 * Delete all statements on the configured SPARQL endpoint.
	 *
	 * @return true if successful
	 */
	def clear(graphUri = null) {
		log.info "Deleting all data from SPARQL endpoint "
		final query = "CLEAR ${graphUri ? "GRAPH <$graphUri>" : ""}"
		return sparqlService.runUpdateQuery(query)
	}
}
