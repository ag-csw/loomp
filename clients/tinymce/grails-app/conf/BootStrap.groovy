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
import de.fuberlin.loomp.domain.Role
import de.fuberlin.loomp.domain.Person
import grails.util.Environment
import de.fuberlin.loomp.domain.News

class BootStrap {
	final def ROLE_ADMIN = "ROLE_ADMIN"
	final def ROLE_USER = "ROLE_USER"

	def repositoryManager
	def authenticateService

	def init = {servletContext ->
		// roles that have to exist
		def roles = [
				new Role(authority: ROLE_USER, description: "unprivileged user"),
				new Role(authority: ROLE_ADMIN, description: "adminitrator")]
		// default admins (are only created if there is no admin in the database)
		def persons = [
				ROLE_ADMIN:
				[new Person(userRealName: "Administrator", email: "admin@ontonym.de", description: "Administrator",
						passwd: authenticateService.encodePassword("admin"), enabled: true)],
				ROLE_USER: []
		]
		// create roles
		roles.each {
			if (!Role.findByAuthority(it.authority)) {
				it.save(flush: true);
				log.debug("Created role $it.authority")
			}
		}

		persons.each {roleString, personList ->
			def role = Role.findByAuthority(roleString)
			if (role != null) {
				// only if there is no admin, create the default admins
				if (roleString != ROLE_ADMIN || (roleString == ROLE_ADMIN && !role.people?.empty)) {
					personList.each {
						if (Person.findByEmail(it.email) == null) {
							role.addToPeople(it)
							log.debug("Created person $it.email with role $roleString")
						}
					}
				}
			} else {
				log.error("Unable to retrieve role $roleString from database")
			}
		}
		createTestUser()
		createTestNews()
	}

	def destroy = {
		repositoryManager?.shutDown()
	}

	// create test users if in dev environment
	def createTestUser = {
		if (Environment.current == Environment.DEVELOPMENT) {
			def userRole = Role.findByAuthority(ROLE_USER)
			if (userRole != null) {
				// we are in dev mode, so create some customers
				for (n in 1..5) {
					def email = "c${n}@ontonym.de"
					if (!Person.findByEmail(email)) {
						userRole.addToPeople(
								userRealName: "Customer ${n}",
								email: email,
								passwd: authenticateService.encodePassword("c${n}"),
								enabled: true,
								description: "Test customer ${n}"
						)
						log.debug("Created test-user $email")
					}
				}
			} else {
				log.fatal("Unable to retrieve user role from database")
			}
		}
	}

	def createTestNews = {
		if (Environment.current == Environment.DEVELOPMENT) {
			def author = Person.findByEmail("admin@ontonym.de")
			for (n in 1..2) {
				def title = "News $n"
				def message = "Das ist eine Neuigkeit."
				if (!News.findByTitle(title)) {
					new News(title: title, message: message, author: author).save(flush:true)
					log.debug("Created test-news $title")
				}
			}
		}
	}
}
