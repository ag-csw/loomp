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
// locations to search for config files that get merged into the main config
// config files can either be Java properties files or ConfigSlurper scripts

// grails.config.locations = [ "classpath:${appName}-config.properties",
//                             "classpath:${appName}-config.groovy",
//                             "file:${userHome}/.grails/${appName}-config.properties",
//                             "file:${userHome}/.grails/${appName}-config.groovy"]

// if(System.properties["${appName}.config.location"]) {
//    grails.config.locations << "file:" + System.properties["${appName}.config.location"]
// }

grails.project.groupId = appName // change this to alter the default package name and Maven publishing destination
grails.mime.file.extensions = true // enables the parsing of file extensions from URLs into the request format
grails.mime.use.accept.header = false
grails.mime.types = [html: ['text/html', 'application/xhtml+xml'],
		xml: ['text/xml', 'application/xml'],
		text: 'text/plain',
		js: 'text/javascript',
		rss: 'application/rss+xml',
		atom: 'application/atom+xml',
		css: 'text/css',
		csv: 'text/csv',
		all: '*/*',
		json: ['application/json', 'text/json'],
		form: 'application/x-www-form-urlencoded',
		multipartForm: 'multipart/form-data'
]
// The default codec used to encode data with ${}
grails.views.default.codec = "none" // none, html, base64
grails.views.gsp.encoding = "UTF-8"
grails.converters.encoding = "UTF-8"
// enable Sitemesh preprocessing of GSP pages
grails.views.gsp.sitemesh.preprocess = true
// scaffolding templates configuration
grails.scaffolding.templates.domainSuffix = 'Instance'

// Set to false to use the new Grails 1.2 JSONBuilder in the render method
grails.json.legacy.builder = false
// enabled native2ascii conversion of i18n properties files
grails.enable.native2ascii = true
// whether to install the java.util.logging bridge for sl4j. Disable fo AppEngine!
grails.logging.jul.usebridge = true
// packages to include in Spring bean scanning
grails.spring.bean.packages = []

grails.views.javascript.library = "jquery"

// set per-environment serverURL stem for creating absolute links
environments {
	production {
		grails.serverURL = "http://demo.loomp.org/oca"

		loomp.oca.loomp.server="http://demo.loomp.org"
		loomp.oca.loomp.path="api"

		log4j = {
			appenders {
				file name: 'loomp-oca', file: '/var/lib/loomp/logs/loomp-oca.log'
				file name: 'stacktrace', file: '/var/lib/loomp/logs/stack_trace.log', append: false
			}
			root {
				error 'loomp-oca'
				additivity = true
			}
		}
	}
	development {
		grails.serverURL = "http://localhost:8080/${appName}"

		loomp.oca.loomp.server="http://localhost:8181"
		loomp.oca.loomp.path="loomp"

		logDir = "out/log"
		log4j = {
			appenders {
				file name: 'loomp-oca', file: "$logDir/loomp - oca.log"
				file name: 'stacktrace', file: "$logDir/stack_trace.log ", append: false
			}
			root {
				error 'loomp-oca', 'stdout'
				additivity = true
			}
			debug 'grails.app'
			// Append to previous line to debug HTTP messages
			// , 'org.apache.http.headers', 'org.apache.http.wire'
		}
	}
	test {
		grails.serverURL = "http://localhost:8080/${appName}"

		loomp.oca.loomp.server="http://localhost:8181"
		loomp.oca.loomp.path="loomp"

		logDir = "out/log"
		log4j = {
			appenders {
				file name: 'loomp-oca', file: 'loomp-oca.log'
				file name: 'stacktrace', file: "$logDir/stack_trace.log", append: false
			}
			root {
				error 'loomp-oca', 'stdout'
				additivity = true
			}
			debug 'grails.app'
		}
	}
}

// log4j configuration
log4j = {
	// Example of changing the log pattern for the default console
	// appender:
	//
	//appenders {
	//    console name:'stdout', layout:pattern(conversionPattern: '%c{2} %m%n')
	//}


	error 'org.codehaus.groovy.grails.web.servlet',  //  controllers
			'org.codehaus.groovy.grails.web.pages', //  GSP
			'org.codehaus.groovy.grails.web.sitemesh', //  layouts
			'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
			'org.codehaus.groovy.grails.web.mapping', // URL mapping
			'org.codehaus.groovy.grails.commons', // core / classloading
			'org.codehaus.groovy.grails.plugins', // plugins
			'org.codehaus.groovy.grails.orm.hibernate', // hibernate integration
			'org.springframework',
			'org.hibernate',
			'net.sf.ehcache.hibernate'

	warn 'org.mortbay.log'
}

//log4j.logger.org.springframework.security='off,stdout'
