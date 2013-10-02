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
package loomp.model.db

import org.codehaus.groovy.grails.plugins.springsecurity.Secured

/**
 * Controller for managing system parameters
 */

@Secured(['ROLE_ADMIN'])
class SystemUserController {
    static allowedMethods = [save: "POST", update: "POST"]

    def index = {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [personList: Person.list(params), personTotal: Person.count()]
    }

    def save = {
        def person = new Person(params)
		if (person.save(flush: true)) {
			flash.message = "${message(code: 'default.created.message', args: [message(code: 'person.label'), person.id])}"
			redirect(action: "index")
		}
		else {
			flash.error = "${message(code: 'default.error.created.message', args: [message(code: 'person.label'), person.id])}"
			render(view: "index", model: [person: person])
		}
    }

    def edit = {
        def person = Person.get(params.id)
        if (!person) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'person.label'), params.id])}"
            redirect(action: "index")
        }
        else {
            render(view: "index", model: [person: person])
        }
    }

    def update = {
        def person = Person.get(params.id)
        if (person) {
            if (params.version) {
                def version = params.version.toLong()
                if (person.version > version) {
                    person.errors.rejectValue("version", "default.optimistic.locking.failure", [message(code: 'person.label')] as Object[], "Another user has updated this Person while you were editing")
                    render(view: "index", model: [person: person])
                    return
                }
            }
            person.properties = params
            if (!person.hasErrors() && person.save(flush: true)) {
                flash.message = "${message(code: 'default.updated.message', args: [message(code: 'person.label'), person.id])}"
                redirect(action: "index", id: person.id)
            }
            else {
                render(view: "index", model: [person: person])
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'person.label'), params.id])}"
            redirect(action: "index")
        }
    }

    def delete = {
        def person = Person.get(params.id)
        if (person) {
            try {
                person.delete(flush: true)
                flash.message = "${message(code: 'default.deleted.message', args: [message(code: 'person.label'), params.id])}"
                redirect(action: "index")
            }
            catch (org.springframework.dao.DataIntegrityViolationException e) {
                flash.message = "${message(code: 'default.not.deleted.message', args: [message(code: 'person.label'), params.id])}"
                redirect(action: "index")
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'person.label'), params.id])}"
            redirect(action: "index")
        }
    }
}
