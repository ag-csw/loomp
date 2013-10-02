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

import grails.util.Environment
import groovyx.net.http.HttpResponseException
import loomp.oca.utils.TimeMeasuring
import org.codehaus.groovy.grails.commons.ConfigurationHolder

class HttpService {
	static transactional = false
	final timer = new TimeMeasuring()

	/**
	 * Call a given action at a given service and return the result.
	 *
	 * @param service
	 * 		name of the service (a path relative to the oca.loomp_api defined in the config
	 * @param action
	 * 		name of the action to invoke
	 * @param params
	 * 		parameter to be passed to the action
	 * @return a JSON string
	 */
	def doGet(service, action, params) {
		if (!service)
			throw new IllegalArgumentException("parameter service has to be specified")
		if (!action)
			throw new IllegalArgumentException("parameter action has to be specified")

		def json = null
		def path = "${getLoompServer()}/${getLoompPath()}/$service/$action"
		log.debug "** URL: $path; Params: $params"

		try {
			timer.takeStartTime()
			withHttp(uri: getLoompServer()) {
				json = params ? get(path: path, query: params) : get(path: path)
			}
			timer.takeEndTime()
			log.debug "... executed in ${timer.timeDiffInNanos()} ns"
		} catch (HttpResponseException e) {
			log.debug "** failed", e
		}
        if (service=="ars")
            log.debug "the respond is $json"
		return json
	}

	/**
	 * Call a given action at a given service and return the result.
	 *
	 * @param service
	 * 		name of the service (a path relative to the oca.loomp_api defined in the config
	 * @param action
	 * 		name of the action to invoke
	 * @param params
	 * 		parameter to be passed to the action
	 * @return a JSON string
	 */
	def doPost(service, action, params) {
		if (!service)
			throw new IllegalArgumentException("parameter service has to be specified")
		if (!action)
			throw new IllegalArgumentException("parameter action has to be specified")

		def json = null
		def path = "${getLoompServer()}/${getLoompPath()}/$service/$action"
		log.debug "** URL: $path; Params: $params"

		try {
			timer.takeStartTime()
			withHttp(uri: getLoompServer()) {
				json = params ? post(path: path, query: params) : post(path: path)
			}
			timer.takeEndTime()
			log.debug "... executed in ${timer.timeDiffInNanos()} ns"
		} catch (HttpResponseException e) {
			log.debug "** failed", e
		}

		return json
	}

	private getLoompServer() {
		return Environment.current == Environment.TEST ?
			"http://localhost:8181" :
			ConfigurationHolder.config.loomp.oca.loomp.server
	}

	private getLoompPath() {
		return ConfigurationHolder.config.loomp.oca.loomp.path
	}
}
