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
package loomp.oca.client.ui;

import com.google.gwt.core.client.GWT;
import loomp.oca.client.utils.LocaleUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/** Utility methods to handle namespaces within the editor content. */
public class NameSpace {

	private final static Map<String, String> nss = new HashMap<String, String>();

	//insert new namespaces into the nss map here
	static {
		nss.put("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
		nss.put("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
		nss.put("loomp", "http://www.loomp.org/loomp/0.1/");
		nss.put("owl", "http://www.w3.org/2002/07/owl#");
		nss.put("foaf", "http://xmlns.com/foaf/0.1/");
		nss.put("geo", "http://www.loomp.org/ontology/geography#");
		nss.put("event", "http://www.loomp.org/ontology/event#");
		nss.put("dbpedia", "http://dbpedia.org/resource/");
		nss.put("yago", "http://dbpedia.org/class/yago/");
	}

	//used to wrap around the editor text
	//the namespace span and to replace the annotation links
	//in with the namespaces
	public static String wrapText(String inside) {
		//opening span
		String ret = "<span xml:lang=\"" + LocaleUtils.getLocale() + "\"";

		//setting all namespaces
		Iterator<String> ksI = nss.keySet().iterator();
		for (String key : nss.keySet()) {
			ret += "\n xmlns:" + key + "=\"" + nss.get(key) + "\"";
		}

		//closing
		ret += ">";

		//adding the inside
		ret += insertNs(inside);

		//closign span
		ret += "</span>";
		return ret;
	}


	/**
	 * Eliminate a surrounding span that contains the namespace declarations. Any usage of
	 * namespaces in the text is replaced by its expanded form.
	 *
	 * @param wrapped
	 * 		a string wrapped in a span
	 * @return unwrapped text
	 */
	public static String unwrapText(String wrapped) {
		if (wrapped == null)
			return "";

		// TODO currently it is only a workaround
		GWT.log(wrapped);
		GWT.log("" + wrapped.startsWith("<span xml"));
		if (wrapped.startsWith("<span xml")) {
			wrapped = wrapped.replaceAll("^<span[^>]*>", "");
			wrapped = wrapped.replaceAll("</span>$", "");
		}
		GWT.log(wrapped);
		return replaceNs(wrapped);
	}

	private static String replaceNs(String input) {
		if (input == null || input.length() == 0)
			return "";
		for (String nextM : nss.keySet()) {
			input = input.replaceAll(nextM + ":", nss.get(nextM));
		}
		return input;
	}

	private static String insertNs(String input) {
		if (input == null || input.length() == 0)
			return "";
		for (String nextM : nss.keySet()) {
			input = input.replaceAll(nss.get(nextM), nextM + ":");
		}
		return input;
	}
}
