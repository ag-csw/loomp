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
/*
 * Copyright (c) 2011.
 * Freie Universität Berlin, AG Corporate Semantic Web
 */

package loomp.tag

import com.eaio.util.text.HumanTime
import org.apache.commons.lang.StringUtils
import loomp.oca.utils.RenderUtils
import org.jsoup.Jsoup

/**
 * Tags to ease the pain of output.
 */
class LayoutTagLib {
	static namespace = 'lo'

	def groovyPagesTemplateEngine

	/**
	 * Render a text and abbreviate it if it is too long. The full text can be read in a modal box.
	 *
	 * @param length
	 * 		maximum length a text is rendered without a modal box (defaults to 100)
	 */
	def shortText = {attrs, body ->
		def length = attrs.length ? attrs.length as int : 100
		String fullText = body.call()
		def plainText = Jsoup.parse(fullText).text()
		if (plainText.length() <= length) {
			out << fullText
		} else {
			def shortText = StringUtils.abbreviate(plainText, length)
			def event = attrs.event ? attrs.event : 'onclick';
			def id = attrs.id ?: RenderUtils.getUniqueId()

			out << "<div id=\"${id}_short\" class=\"shortText\">${shortText} <a class=\"more\" href=\"javascript:void(0)\" ${event}=\"\$('#${id}_short').hide(); \$('#${id}_full').show();\"><span>${message(code: 'default.more.label')}</span></a></div>"
			out << "<div id=\"${id}_full\" class=\"shortText\" style=\"display: none;\">${fullText} <a class=\"less\" href=\"javascript:void(0)\" ${event}=\"\$('#${id}_full').hide(); \$('#${id}_short').show();\"><span>${message(code: 'default.less.label')}</span></a></div>"
		}
	}

	/**
	 * Formats a duration in a human readable format, e.g. 3 d 4 m 3 s. Either the
	 * parameter duration or date has to be passed. If a date is passed then the time difference
	 * between date and now is rendered.
	 *
	 * @param duration
	 * 		time duration in milliseconds
	 * @param date
	 * 		a date
	 * @param exactly
	 * 		if true then the duration is rendered exactly. Otherwise it is approximated
	 */
	def formatDuration = {attrs ->
		def duration
		if (attrs.duration) {
			duration = attrs.duration
		} else if (attrs.date) {
			def now = new Date().getTime()
			duration = now - attrs.date.getTime()
		} else return

		def humanTime = new HumanTime()
		out << (attrs.exactly == 'true' ? humanTime.exactly(duration) : humanTime.approximately(duration))
	}
}
