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
import loomp.model.db.Person
import loomp.model.db.Authority
import loomp.model.db.SystemParam
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import loomp.utils.ModelUtils
import loomp.model.*

class BootStrap {
	def authenticateService

	def init = {servletContext ->
		ModelUtils.addMethodsToModelClasses()
		addAdmin()
		addEndpoint()
	}

	def destroy = {
	}
	
	/**
	 * Add admin role and/or admin user if they do not exist in database.
	 */
	public addAdmin() {
		final ROLE_ADMIN = 'ROLE_ADMIN'
		def roleAdmin = Authority.findByAuthority(ROLE_ADMIN)
		if (roleAdmin) {
			log.info "Found admin role $roleAdmin"
		} else {
			roleAdmin = new Authority(authority: ROLE_ADMIN, description: 'administrators')
			roleAdmin = roleAdmin.save()
			if (roleAdmin) {
				log.info "Created role $ROLE_ADMIN"
			} else {
				log.error "Unable to create role $ROLE_ADMIN"
			}
		}

		def admins = roleAdmin.people
		if (admins) {
			log.info "Found admin users $admins"
		} else {
			final ADMIN_EMAIL = 'admin@loomp.org'
			def person = new Person(
					userRealName: 'Admin', email: ADMIN_EMAIL, enabled: true,
					passwd: authenticateService.encodePassword("loomp..api"))
			person.addToAuthorities(roleAdmin)
			if (person.save()) {
				log.info "Creating admin user $ADMIN_EMAIL"
			} else {
				log.error "Unable to create admin user $ADMIN_EMAIL"
			}
		}
	}

	/**
	 * Add a default value for an endpoint URL if it does not exist in database.
	 */
	public addEndpoint() {
		def loompCfg = ConfigurationHolder.config.loomp
		final DEFAULTS = [
				(SystemParam.ENDPOINT_QUERY_URL) : loompCfg.endpoint.query_url,
				(SystemParam.ENDPOINT_UPDATE_URL) : loompCfg.endpoint.update_url,
				(SystemParam.ENDPOINT_SPARQL_SYNTAX) : loompCfg.endpoint.sparql_syntax,
				(SystemParam.BASE_NS) : loompCfg.base_ns,
				(SystemParam.BASE_NS_VERSION) : loompCfg.base_ns,
				(SystemParam.ENDPOINT_LIMIT) : loompCfg.endpoint.limit,
				(SystemParam.VERSIONING) : loompCfg.versioning
		]

		DEFAULTS.each { name, dfltValue ->
			def systemParam = SystemParam.findByName(name)
			if (systemParam?.value) {
				log.info "Found system parameter $systemParam.name: $systemParam.value"
			} else {
				systemParam = new SystemParam(name: name, value: dfltValue.toString())
				if (systemParam.save()) {
					log.info "Added system parameter $name: $dfltValue"
				} else {
					log.error "Unable to create system parameter $name"
				}
			}
		}
	}
}
