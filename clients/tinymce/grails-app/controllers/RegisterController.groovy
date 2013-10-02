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
import de.fuberlin.loomp.domain.Person
import de.fuberlin.loomp.domain.Role

import org.springframework.security.providers.UsernamePasswordAuthenticationToken as AuthToken
import org.springframework.security.context.SecurityContextHolder as SCH

/**
 * Registration controller.
 */
class RegisterController {

	def authenticateService
	def daoAuthenticationProvider
	def emailerService

	static Map allowedMethods = [save: 'POST', update: 'POST']

	/**
	 * User Registration Top page.
	 */
	def index = {

		// skip if already logged in
		if (authenticateService.isLoggedIn()) {
			redirect action: show
			return
		}

		if (session.id) {
			def person = new Person()
			person.properties = params
			return [person: person]
		}

		redirect uri: '/'
	}

	/**
	 * User Information page for current user.
	 */
	def show = {

		// get user id from session's domain class.
		def user = authenticateService.userDomain()
		if (user) {
			render view: 'show', model: [person: Person.get(user.id)]
		}
		else {
			redirect action: index
		}
	}

	/**
	 * Edit page for current user.
	 */
	def edit = {

		def person
		def user = authenticateService.userDomain()
		if (user) {
			person = Person.get(user.id)
		}

		if (!person) {
			flash.message = "[Illegal Access] User not found with id ${params.id}"
			redirect action: index
			return
		}

		[person: person]
	}

	/**
	 * update action for current user's edit page
	 */
	def update = {

		def person
		def user = authenticateService.userDomain()
		if (user) {
			person = Person.get(user.id)
		}
		else {
			redirect action: index
			return
		}

		if (!person) {
			flash.message = "[Illegal Access] User not found with id ${params.id}"
			redirect action: index, id: params.id
			return
		}

		// if user want to change password. leave passwd field blank, passwd will not change.
		if (params.passwd && params.passwd.length() > 0
				&& params.repasswd && params.repasswd.length() > 0) {
			if (params.passwd == params.repasswd) {
				person.passwd = authenticateService.encodePassword(params.passwd)
			}
			else {
				person.passwd = ''
				flash.message = 'The passwords you entered do not match.'
				render view: 'edit', model: [person: person]
				return
			}
		}

		person.userRealName = params.userRealName
		person.email = params.email
		if (params.emailShow) {
			person.emailShow = true
		}
		else {
			person.emailShow = false
		}

		if (person.save()) {
			redirect action: show, id: person.id
		}
		else {
			render view: 'edit', model: [person: person]
		}
	 }

	/**
	 * Person save action.
	 */
	def save = {

		// skip if already logged in
		if (authenticateService.isLoggedIn()) {
			redirect action: show
			return
		}

		def person = new Person()
		person.properties = params

		def config = authenticateService.securityConfig
		def defaultRole = config.security.defaultRole

		def role = Role.findByAuthority(defaultRole)
		if (!role) {
			person.passwd = ''
			flash.message = 'Default Role not found.'
			render view: 'index', model: [person: person]
			return
		}

		if (params.captcha.toUpperCase() != session.captcha) {
			person.passwd = ''
			flash.message = 'Access code did not match.'
			render view: 'index', model: [person: person]
			return
		}

		if (params.passwd != params.repasswd) {
			person.passwd = ''
			flash.message = 'The passwords you entered do not match.'
			render view: 'index', model: [person: person]
			return
		}

		def pass = authenticateService.encodePassword(params.passwd)
		person.passwd = pass
		person.enabled = true
		person.emailShow = true
		person.description = ''
		if (person.save()) {
			role.addToPeople(person)
			if (config.security.useMail) {
				String emailContent = """You have signed up for an account at:

 ${request.scheme}://${request.serverName}:${request.serverPort}${request.contextPath}

 Here are the details of your account:
 -------------------------------------
 LoginName: ${person.username}
 Email: ${person.email}
 Full Name: ${person.userRealName}
 Password: ${params.passwd}
"""

				def email = [
					to: [person.email], // 'to' expects a List, NOT a single email address
					subject: "[${request.contextPath}] Account Signed Up",
					text: emailContent // 'text' is the email body
				]
				emailerService.sendEmails([email])
			}

			person.save(flush: true)

			def auth = new AuthToken(person.username, params.passwd)
			def authtoken = daoAuthenticationProvider.authenticate(auth)
			SCH.context.authentication = authtoken
			redirect uri: '/'
		}
		else {
			person.passwd = ''
			render view: 'index', model: [person: person]
		}
	}
}
