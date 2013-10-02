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

import loomp.model.Annotation
import loomp.model.AnnotationSet
import loomp.model.LocaleLiteralMap
import loomp.model.Resource
import loomp.oca.helper.ResourceView
import loomp.oca.utils.RenderUtils
import loomp.vocabulary.RDF
import loomp.vocabulary.RDFS

/**
 * @author rheese
 */
class ResourceService {
	def serverLoompService

	/** Cache of all annotation sets   */
	Map<AnnotationSet, List<Annotation>> annotationSets
	/** Cache of labels of all annotations <URI of an annotation property, labels>   */
	Map<URI, LocaleLiteralMap> annotationLabels

	/**
	 * Create a resource view for a given resource.
	 * @param resource
	 * 		a resource
	 * @param ling
	 * 		preferred language of the label
	 * @return a resource view
	 */
	def ResourceView getResourceView(Resource resource, String ling) {
		def view = new ResourceView(
				resource: resource,
				annotations: getAnnotations(resource),
				label: getLabel(resource, ling))

		// prepare properties of the resource for showing to a user
		view.annotated = new TreeMap<String, Set<String>>()
		view.otherLiterals = new TreeMap<String, Set<String>>()
		resource.props.each { tpv ->
			if (tpv.isLiteral) {
				// TODO getAnnotationLabel returns wrong values because an annotation is no longer uniquely identified by its annotation property, e.g., we have now several rdfs:label
				def label = getAnnotationLabel(tpv.property, ling)
//				if (label) {
				if (false) {
					// it is a property of an annotation
					if (!view.annotated.containsKey(label))
						view.annotated[label] = new TreeSet<String>()
					view.annotated[label] << (tpv.value instanceof LocaleLiteralMap ? tpv.value.getAnyLiteral(ling) : tpv.value)

				} else {
					// it is not a property of an annotation
					label = RenderUtils.localName(tpv.property)
					if (!view.otherLiterals.containsKey(label))
						view.otherLiterals[label] = new TreeSet<String>()
					view.otherLiterals[label] << (tpv.value instanceof LocaleLiteralMap ? tpv.value.getAnyLiteral(ling) : tpv.value)
				}
			}
		}

		return view
	}

	/** @see #getResourceView */
	def List<ResourceView> getResourceViews(Collection<Resource> resources, String ling) {
		def views = []
		resources.each { views << getResourceView(it, ling)}
		return views
	}

	/**
	 * Look for the annotations that were used to annotate this resource. They are
	 * identified by the combination of annotation property and type.
	 *
	 * @param resource
	 * 		a resource
	 * @return the type
	 */
	def List<Annotation> getAnnotations(Resource resource) {
		def types = resource.props.findAll {RDF.TYPE.equals(it.property)}.value
		def candidates = []
		def annotations = []
		log.info "Resource: $resource"
		log.info "** Types wanted: $types"
		getAnnotationSets().values().each { list ->
			log.info "** Annotations: $list.domain"
			candidates.addAll(list.findAll { (types.isEmpty() && !it.domain) || types.contains(it.domain) })
		}
		log.info "** Candidates: $candidates"
		resource.props.each { tpv ->
			annotations.addAll(candidates.findAll { it.property.equals(tpv.property)})
		}

		return annotations.unique()
	}

	/**
	 * Determine a label for a given resource. It returns null if no literal is present.
	 *
	 * Selection algorithm:
	 * 1) All literals of properties belonging to some annotation. If there is more than one
	 *    literal take the longest one.
	 * 2) Otherwise take rdfs:label property
	 * 3) should not happen :-)
	 *
	 * @param resource
	 * 		a resource
	 * @param ling
	 * 		preferred language of the label
	 * @return a label
	 */
	def String getLabel(resource, String ling) {
		if (!resource)
			throw new NullPointerException("resource is null")

		// step 1)
		def literalProps = resource.props.findAll { it.isLiteral && isAnnotationProperty(it.property) }
		if (!literalProps) {
			// step 2)
			literalProps = resource.props.findAll { it.isLiteral && RDFS.LABEL.equals(it.property) }
		}
		if (!literalProps) {
			// step 3)
			log.warn "No label could be determined for resource $resource.uri"
			return null
		}
		final labels = literalProps.collect { (it.value instanceof LocaleLiteralMap ? it.value.getAnyLiteral(ling) : it.value) }
		return labels ? labels.sort { -it.length() }.get(0) : null
	}

	def String getLabel(loomp.oca.client.model.Resource resource, String ling) {
		def labels = []
		// step 1)
		resource.literalProps.each { prop, literals ->
			// TODO include domain (resource types) into the query
			if (isAnnotationProperty(URI.create(prop)))
				labels.addAll(literals)
		}
		if (!labels) {
			// step 2)
			labels = resource.literalProps.get(RDFS.LABEL.toString())
		}
		if (!labels) {
			// step 3)
			log.warn "No label could be determined for resource $resource.uri"
			return null
		}
		return labels ? labels.sort { -it.length() }.get(0) : null
	}

	/**
	 * @param uri
	 * 		a URI
	 * @return true iff uri is the property of an annotation
	 */
	def isAnnotationProperty(URI uri) {
		return getAnnotationLabels().containsKey(uri)
	}

	/**
	 * Get all annotation sets.
	 *
	 * @return map of <annotation sets, annotations>
	 */
	private final Map<AnnotationSet, List<Annotation>> getAnnotationSets() {
		if (annotationSets == null) {
			def set = serverLoompService.loadAnnotationSets()
			annotationSets = [:]
			set.each {
				annotationSets.put(it, serverLoompService.loadAnnotations(it.uri))
			}
		}
		return annotationSets
	}

	/**
	 * @return map of <URI of annotation property, labels>
	 */
	private final Map<URI, LocaleLiteralMap> getAnnotationLabels() {
		if (annotationLabels == null) {
			annotationLabels = [:]
			getAnnotationSets().each { set, annotations ->
				annotations.each {
					annotationLabels.put(it.property, it.labels)
				}
			}
		}
		return annotationLabels
	}

	/**
	 * Look for a label of an annotation in a preferred language.
	 *
	 * @param uri
	 * 		URI of an annotation property
	 * @param ling
	 * 		preferred language
	 * @return a label if one exists
	 */
	private final String getAnnotationLabel(URI uri, String ling) {
		return getAnnotationLabels().get(uri)?.getAnyLiteral(ling)
	}

	/**
	 * Complete query to labels of annotations.
	 *
	 * @param query
	 * 		a query string
	 * @param ling
	 * 		preferred language
	 * @return annotations with matching labels
	 */
	def List<Annotation> completeAnnotation(String query, String ling) {
		if (!query)
			return []

		def result = []
		getAnnotationSets().each { set, annotations ->
			result.addAll(annotations.findAll {
				it.labels.getAnyLiteral(ling).toLowerCase().startsWith(query)
			})
		}
		return result
	}

	def List<Annotation> getAnnotations(Collection<URI> uris) {
		if (!uris)
			return []

		def result = []
		getAnnotationSets().each { set, annotations ->
			result << annotations.findAll { uris.contains(it.uri) }
		}
		return result
	}
}
