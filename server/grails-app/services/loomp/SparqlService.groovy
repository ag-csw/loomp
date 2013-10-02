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

import com.hp.hpl.jena.query.QueryFactory
import com.hp.hpl.jena.query.Syntax
import com.hp.hpl.jena.rdf.model.Model
import loomp.model.db.SystemParam
import loomp.utils.QueryEngineHTTP
import loomp.utils.TimeMeasuring
import org.apache.http.HttpResponse
import org.apache.http.NameValuePair
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.message.BasicNameValuePair

class SparqlService {
    boolean transactional = false
	final timer = new TimeMeasuring()

	/**
	 * Execute a query on the default sparql endpoint using a http request.
	 *
	 * @param query
	 * 		a sparql query
	 * @param callable
	 * 		closure that is called (gets a result set)
	 * @return a result set
	 */
    def runSelectQuery(String query, Closure callable) {
		return runSelectQuery(queryUrl(), query, callable)
    }

	/**
	 * Execute a query on the default sparql endpoint using a http request.
	 *
	 * @param queryUrl
	 * 		url of an endpoint accepting sparql queries
	 * @param query
	 * 		a sparql query
	 * @param callable
	 * 		closure that is called (gets a result set)
	 * @return a result set
	 */
    def runSelectQuery(String queryUrl, String query, Closure callable) {
		log.info "Executing query $query at $queryUrl"
		timer.takeStartTime()
		def parsedQuery = QueryFactory.create(query, sparqlSyntax())
		// QueryExecutionFactory.sparqlService cannot be used, because the http connection cannot be closed 
		// def qe = QueryExecutionFactory.sparqlService(queryUrl, parsedQuery)
		def qe = new QueryEngineHTTP(queryUrl, parsedQuery)
		def rs = qe.execSelect()
		try {
			callable.call(rs)
		} finally {
			if (qe) qe.close()
			timer.takeEndTime()
			log.info "... executed in ${timer.timeDiffInNanos()} ns"
		}
    }

	/**
	 * Execute a query on the default sparql endpoint using a http request.
	 *
	 * @param query
	 * 		a sparql query
	 * @return a jena model
	 */
    def Model runDescribeQuery(String query) {
		return runDescribeQuery(queryUrl(), query)
    }

	/**
	 * Execute a query on the default sparql endpoint using a http request.
	 *
	 * @param queryUrl
	 * 		url of an endpoint accepting sparql queries
	 * @param query
	 * 		a sparql query
	 * @return a jena model
	 */
    def Model runConstructQuery(String queryUrl, String query) {
		log.info "Executing query $query at $queryUrl"
		timer.takeStartTime()
		def parsedQuery = QueryFactory.create(query, sparqlSyntax())
		// QueryExecutionFactory.sparqlService cannot be used, because the http connection cannot be closed 
		// def qe = QueryExecutionFactory.sparqlService(queryUrl, parsedQuery)
		def qe = new QueryEngineHTTP(queryUrl, parsedQuery)
		def model = qe.execConstruct()
		qe.close()
		timer.takeEndTime()
		log.info "... executed in ${timer.timeDiffInNanos()} ns"
		return model
    }

    /**
	 * Execute a query on the default sparql endpoint using a http request.
	 *
	 * @param query
	 * 		a sparql query
	 * @return a jena model
	 */
    def runConstructQuery(String query) {
		return runConstructQuery(queryUrl(), query)
    }

	/**
	 * Execute a query on the default sparql endpoint using a http request.
	 *
	 * @param queryUrl
	 * 		url of an endpoint accepting sparql queries
	 * @param query
	 * 		a sparql query
	 * @return a jena model
	 */
    def Model runDescribeQuery(String queryUrl, String query) {
		log.info "Executing query $query at $queryUrl"
		timer.takeStartTime()
		def parsedQuery = QueryFactory.create(query, sparqlSyntax())
		// QueryExecutionFactory.sparqlService cannot be used, because the http connection cannot be closed 
		// def qe = QueryExecutionFactory.sparqlService(queryUrl, parsedQuery)
		def qe = new QueryEngineHTTP(queryUrl, parsedQuery)
		def model = qe.execDescribe()
		qe.close()
		timer.takeEndTime()
		log.info "... executed in ${timer.timeDiffInNanos()} ns"
		return model
    }

	/**
	 * Execute a query on the default sparql endpoint using a http request.
	 *
	 * @param query
	 * 		a sparql query
	 * @return result of the query (a Boolean)
	 */
    def boolean runAskQuery(String query) {
		return runAskQuery(queryUrl(), query)
    }

	/**
	 * Execute a query on the default sparql endpoint using a http request.
	 *
	 * @param queryUrl
	 * 		url of an endpoint accepting sparql queries
	 * @param query
	 * 		a sparql query
	 * @return result of the query (a Boolean)
	 */
    def boolean runAskQuery(String queryUrl, String query) {
		log.info "Executing query $query at $queryUrl"
		timer.takeStartTime()
		def parsedQuery = QueryFactory.create(query, sparqlSyntax())
		// QueryExecutionFactory.sparqlService cannot be used, because the http connection cannot be closed 
		// def qe = QueryExecutionFactory.sparqlService(queryUrl, parsedQuery)
		def qe = new QueryEngineHTTP(queryUrl, parsedQuery)
		def result = qe.execAsk()
		qe.close()
		timer.takeEndTime()
		log.info "... executed in ${timer.timeDiffInNanos()} ns"
		return result
    }

	def boolean runUpdateQuery(String query) {
		return runUpdateQuery(updateUrl(), query)
	}

	/**
	 * Execute an update on the default sparql endpoint using a http request.
	 *
	 * @param updateUrl
	 * 		url of an endpoint accepting sparql updates
	 * @param query
	 * 		a sparql update query
	 * @return true if the update was successful, false otherwise
	 */
    def boolean runUpdateQuery(String updateUrl, String query) {
		def success = true
		// ARQ does not contain a special class for executing updates
		log.info "Executing update $query at $updateUrl"

		// put the update query into a "form"
		List<NameValuePair> formParams = new ArrayList<NameValuePair>()
		formParams.add(new BasicNameValuePair("request", query))
		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formParams, "UTF-8");

		// create the request
		HttpPost httpPost = new HttpPost(updateUrl);
		httpPost.setEntity(entity);

		// submit the form to the sparql endpoint
		// TODO maybe the connection handling can be improved by using only a single HTTP client
		DefaultHttpClient httpClient = new DefaultHttpClient()
		timer.takeStartTime()
		HttpResponse httpResp = httpClient.execute(httpPost)
		timer.takeEndTime()
		log.info "... executed in ${timer.timeDiffInNanos()} ns"
		if (httpResp.getStatusLine().getStatusCode() != 200) {
			log.error "== HTTP response indicates error ${httpResp.getStatusLine()}"
			success = false
		}
		httpClient.getConnectionManager().shutdown()
		return success
    }

	def queryUrl = {
		return SystemParam.findByName(SystemParam.ENDPOINT_QUERY_URL)?.value
	}
	def updateUrl = {
		return SystemParam.findByName(SystemParam.ENDPOINT_UPDATE_URL)?.value
	}
	def sparqlSyntax = {
		return Syntax.make(SystemParam.findByName(SystemParam.ENDPOINT_SPARQL_SYNTAX)?.value)
	}
}
