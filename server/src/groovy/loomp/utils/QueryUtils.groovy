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

import org.apache.commons.lang.StringUtils
import org.apache.commons.lang.StringEscapeUtils
import grails.util.GrailsNameUtils

/**
 * Utilities to create parts of a query
 */
class QueryUtils {
	/**
	 * Create a graph pattern (including FILTERs) for a given variable using the
	 * properties and values of the map constraints. If constraint is null or empty
	 * then an empty string is returned.
	 *
	 * @param var
	 * 		name of a variable
	 * @param constraints
	 * 		a map of constraints
	 * @return a graph pattern as string
	 */
	static String createGraphPattern(var, Map constraints) {
		if (!var) throw new IllegalArgumentException("Parameter vas is null or empty")
		if (!constraints) return ""

		def s = new StringBuilder()
		def varCnt = 0
		constraints.each { prop, values ->
			if (values instanceof String) values = [values]
			def objVar = "?__var__$varCnt"
			s.append("$var ${prop ? JenaUtils.n2s(prop) : "?__pred__"} $objVar . ")
			s.append(createOrFilterEqual(objVar, values))
			s.append("\n")
			varCnt++
		}
		return s.toString()
	}

	/**
	 * Create a filter OR expression for a give variable (including '?'). The variable
	 * is tested on equality. String values will be escaped. If values is empty then an
	 * empty string is returned.
	 *
	 * @param var
	 * 		name of a variable
	 * @param values
	 * 		values that the variable can have
	 * @return a filter expression
	 * @throws IllegalArgumentException if var is null or empty
	 */
	static String createOrFilterEqual(var, values) {
		if (!var) throw new IllegalArgumentException("Parameter vas is null or empty")
		if (!values) return ""

		if (!values instanceof Collection) {
			values = [values]
		}

		def s = ""
		values.each { s += "$var = ${JenaUtils.n2s(it)} || " }
		return "FILTER (${StringUtils.removeEnd(s, " || ")}) ."
	}

	/**
	 * Create a triple pattern for each element in values in which the element is the
	 * subject. For each value new variables are used for predicate and object.
	 *
	 * @param values
	 * 		values that the variable can have
	 * @param graph
	 * 		URI or variable of the graph to be queried
	 * @return triple patterns
	 * @throws IllegalArgumentException if var is null or empty
	 */
	static String createSubjectPatterns(values, graph = null) {
		if (!values) return ""

		if (!values instanceof Collection) {
			values = [values]
		}

		def s = ""
		values.eachWithIndex { value, i ->
			s += "${JenaUtils.n2s(value)} ?p$i ?o$i .\n"
		}
		return withGraphClause(graph, s)
	}

	/**
	 * Enclose the pattern in a graph clause. If no graph is given then the pattern is
	 * returned. If pattern is empty or null then an empty string is returned.
	 *
	 * @param graph
	 * 		URI or variable of the graph to be queried
	 * @param pattern
	 * 		pattern to be enclosed
	 * @return a graph clause
	 */
	static String withGraphClause(graph, String pattern) {
		if (!graph) return pattern
		if (GrailsNameUtils.isBlank(pattern)) return ""

		final isVariable = graph.toString().charAt(0) == '?'
		return """
			GRAPH ${isVariable ? graph : "<$graph>"} {
				$pattern
			}"""
	}

	/**
	 * Create a filter OR expression for a give variable (including '?'). The values are
	 * used as regular expression on the variable. All values are used as they are. If
	 * values is empty then an empty string is returned.
	 *
	 * @param var
	 * 		name of a variable
	 * @param values
	 * 		values that the variable can have
	 * @param ignoreCase
	 * 		if set to true then the search is cases insensitive.
	 * @return a filter expression
	 * @throws IllegalArgumentException if var is null or empty
	 */
	static String createOrFilterRegex(var, values, ignoreCase) {
		if (!var) throw new IllegalArgumentException("Parameter vas is null or empty")
		if (!values) return ""

		def s = ""
		values.each { s += "regex($var, \"${it}\" ${ignoreCase ? ', "i"' : ''}) || " }
		return "FILTER (${StringUtils.removeEnd(s, " || ")}) ."
	}

	/**
	 * @see #createOrFilterRegex(Object, Object, Object) but the expressions are
	 * ANDed.
	 *
	 * @param var
	 * 		name of a variable
	 * @param values
	 * 		values that the variable can have
	 * @param ignoreCase
	 * 		if set to true then the search is cases insensitive.
	 * @return a filter expression
	 * @throws IllegalArgumentException if var is null or empty
	 */
	static String createAndFilterRegex(var, values, ignoreCase) {
		if (!var) throw new IllegalArgumentException("Parameter vas is null or empty")
		if (!values) return ""

		def s = ""
		values.each { s += "regex(str($var), \"${it}\" ${ignoreCase ? ', "i"' : ''}) && " }
		return "FILTER (${StringUtils.removeEnd(s, " && ")}) ."
	}

	/**
	 * Splits a search string into pieces using whitespaces. Words enclosed in '"' are
	 * not split.
	 *
	 * @param s
	 * 		a string
	 * @return a set of strings
	 */
	static splitSearchString(s) {
		def tokens = []
		def matcher = s =~ /"([^"]*)"/
		while (matcher.find()) {
			tokens << matcher.group(1)
		}

		// remove all phrases from the string
		tokens.addAll(s.replaceAll('"([^"]*)"', '').split())
		return tokens
	}

	/**
	 * Create the order by, limit, and offset part of a query, so that it can be
	 * appended to a query. limit and offset are only rendered if the values are
	 * not null. If sort is null then the variable '?uri' is used for sorting. If
	 * sortLocale is given then then the values of the sort property have to be
	 * literals and only the values of the given language are used for sorting.
	 *
	 * @param query
	 * 		a query string
	 * @param offset (in params)
	 *      where to start
	 * @param limit (in params)
	 *      how many
	 * @param sort (in params)
	 * 		URI of the property for sorting the result list (null = uri)
	 * @param sortLocale (in params)
	 * 		locale used for sorting
	 * @return modified query
	 */
	static limOffOrd(String query, params) {
		def s = """${query.substring(0, query.lastIndexOf('}'))}
				${params?.sort ? "OPTIONAL { ?uri <${params.sort}> ?sort . ${params?.sortLocale ? " FILTER (lang(?sort) = \"$params.sortLocale\") ." : ""} }" : ""}
			}
			ORDER BY ${params?.order != "desc" ? "ASC" : "DESC"}(${params?.sort ? "?sort" : "?uri"})
			LIMIT ${params?.max ? params.max : 100}
			${params?.offset ? "OFFSET $params.offset" : ""}"""
		return s
		// TODO insert system endpoint_limit
	}

	/**
	 * Create the part of a query to check if the variable '?uri' has one of the given
	 * types. If types is null or empty then an empty string is returned
	 *
	 * @param types
	 * 		a set of type URIs
	 * @return part of a query
	 */
	static typePart(types) {
		return types ? "?uri rdf:type ?type . ${QueryUtils.createOrFilterEqual("?type", types)}" : ""
	}
}
