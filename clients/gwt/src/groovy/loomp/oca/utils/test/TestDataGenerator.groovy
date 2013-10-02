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
package loomp.oca.utils.test

import loomp.oca.client.model.Person
import loomp.oca.client.model.Document
import loomp.oca.client.model.ElementText
import loomp.oca.client.model.AnnotationSet
import loomp.oca.client.model.Annotation
import loomp.oca.client.model.Resource

/**
 * Class for creating test data.
 */
class TestDataGenerator {
	def uriGenerator = new TestUriGenerator()
	def random = new Random()

	final NUM_PERS = 10
	final NUM_DOCS = 30
	final MIN_ELEMS_PER_DOC = 0
	final MAX_ELEMS_PER_DOC = 6
	final NUM_ELEMS = 100
	final NUM_RESS = 30
	final MIN_PROPS_PER_RES = 0
	final MAX_PROPS_PER_RES = 6
	final NUM_ANNOSSET = 30
	final MIN_ANNOS_PER_ANNOSET = 1
	final MAX_ANNOS_PER_ANNOSET = 6

	def domain = "example.com"

	/**
	 * Generate an person instance having a generated URI.
	 *
	 * @return a test instance of Person
	 */
	def getPerson() {
		getPerson(null)
	}

	/**
	 * Generate an person instance having a given URI.
	 *
	 * @param uri
	 * 		a URI (null = generated URI)
	 * @return a test instance of Person
	 */
	def getPerson(URI uri) {
		def fn = getFirstName()
		def ln = getLastName()
		return new Person(
				uri: uri ? uri : getUri(TestUriGenerator.TYPE_DATA))
	}

	/**
	 * Generate #NUM_PERS person instances.
	 *
	 * @return a list of person instances
	 */
	def getPersons() {
		return getPersons(NUM_PERS)
	}

	/**
	 * Generate a given number of person instances.
	 *
	 * @param num
	 * 		number of person instances to be generated
	 * @return a list of person instances
	 */
	def getPersons(num) {
		if (!num)
			throw new NullPointerException("parameter num is null")

		def pers = []
		for (i in 1..num) {
			pers << getPerson()
		}
		return pers
	}

	/**
	 * Generate a document instance having between #MIN_ELEMS_PER_DOC and
	 * #MAX_ELEMS_PER_DOC elements.
	 *
	 * @return a document instance
	 */
	def getDocument() {
		return getDocument(MIN_ELEMS_PER_DOC, MAX_ELEMS_PER_DOC)
	}

	/**
	 * Generate a document instance with a generated URI having between minElem
	 * and maxElem elements.
	 *
	 * @param minElem
	 * 		minimum number of elements
	 * @param maxElem
	 * 		maximum number of elements
	 * @return a document instance
	 */
	def getDocument(minElem, maxElem) {
		getDocument(null, minElem, maxElem)
	}

	/**
	 * Generate a document instance with a given URI having between minElem
	 * and maxElem elements.
	 *
	 * @param uri
	 * 		a URI (null = generated URI)
	 * @param minElem
	 * 		minimum number of elements
	 * @param maxElem
	 * 		maximum number of elements
	 * @return a document instance
	 */
	def getDocument(URI uri, minElem, maxElem) {
		return getDocument(uri, minElem, maxElem, null)
	}

	/**
	 * Generate a document instance with a given URI having between minElem
	 * and maxElem elements.
	 *
	 * @param uri
	 * 		a URI (null = generated URI)
	 * @param minElem
	 * 		minimum number of elements
	 * @param maxElem
	 * 		maximum number of elements
	 * @param persons
	 * 		a set of persons to choose the creator from (null = generate an URI)
	 * @return a document instance
	 */
	def getDocument(URI uri, minElem, maxElem, persons) {
		if (minElem == null) minElem = MIN_ELEMS_PER_DOC
		if (maxElem == null) maxElem = MAX_ELEMS_PER_DOC

		if (minElem < 0 || maxElem < 0)
			throw new IllegalArgumentException("parameter minElem or maxElem is negative")
		if (minElem > maxElem)
			throw new IllegalArgumentException("minElem is greater than maxElem")

		def doc = new Document(
				uri: uri ? uri : getUri(TestUriGenerator.TYPE_DATA))

		// generate elements
		// TODO
		/*
		def num = minElem + random.nextInt(maxElem - minElem + 1)
		if (num > 0) {
			doc.elements = new URI[num]
			for (int i = 0; i < num; i++) {
				doc.elements[i] = getUri(TestUriGenerator.TYPE_DATA)
			}
		}
		*/
		return doc
	}

	/**
	 * Generate #NUM_DOCS document instances having between #MIN_ELEMS_PER_DOC and
	 * #MAX_ELEMS_PER_DOC elements.
	 *
	 * @return a list of document instances
	 */
	def getDocuments() {
		return getDocuments(NUM_DOCS, MIN_ELEMS_PER_DOC, MAX_ELEMS_PER_DOC)
	}

	/**
	 * Generate a given number of document instances having between #MIN_ELEMS_PER_DOC and
	 * #MAX_ELEMS_PER_DOC elements.
	 *
	 * @param num
	 * 		number of document instances to be generated
	 * @return a list of document instances
	 */
	def getDocuments(num) {
		return getDocuments(num, MIN_ELEMS_PER_DOC, MAX_ELEMS_PER_DOC)
	}

	/**
	 * Generate a given number of document instances having between #MIN_ELEMS_PER_DOC and
	 * #MAX_ELEMS_PER_DOC elements.
	 *
	 * @param num
	 * 		number of document instances to be generated
	 * @param persons
	 * 		a set of persons to choose the creator from (null = generate an URI)
	 * @return a list of document instances
	 */
	def getDocuments(num, persons) {
		return getDocuments(num, MIN_ELEMS_PER_DOC, MAX_ELEMS_PER_DOC, persons)
	}

	/**
	 * Generate a given number of document instances.
	 *
	 * @param num
	 * 		number of document instances to be generated
	 * @param minElem
	 * 		minimum number of elements
	 * @param maxElem
	 * 		maximum number of elements
	 * @return a list of document instances
	 */
	def getDocuments(num, minElem, maxElem) {
		return getDocuments(num, minElem, maxElem, null)
	}

	/**
	 * Generate a given number of document instances.
	 *
	 * @param num
	 * 		number of document instances to be generated
	 * @param minElem
	 * 		minimum number of elements
	 * @param maxElem
	 * 		maximum number of elements
	 * @param persons
	 * 		a set of persons to choose the creator from (null = generate an URI)
	 * @return a list of document instances
	 */
	def getDocuments(num, minElem, maxElem, persons) {
		if (!num)
			throw new NullPointerException("parameter num is null")

		def docs = []
		for (i in 1..num) {
			docs << getDocument(null, minElem, maxElem, persons)
		}
		return docs
	}

	/**
	 * Generate an element instance having a generated URI.
	 *
	 * @return an element instance of type text
	 */
	def getElementText() {
		return getElementText(null)
	}

	/**
	 * Generate an element instance having a given URI.
	 *
	 * @param uri
	 * 		a URI (null = generated URI)
	 * @return an element instance of type text
	 */
	def getElementText(uri) {
		return new ElementText(
				uri: uri ? uri : getUri(TestUriGenerator.TYPE_DATA),
				title: getTitle(), content: getText())
	}

	/**
	 * Generate #NUM_ELEMS element instances of type text.
	 *
	 * @return a list of element instances
	 */
	def getElementTexts() {
		return getElementTexts(NUM_ELEMS)
	}

	/**
	 * Generate a given number of element instances of type text.
	 *
	 * @param num
	 * 		number of element instances to be generated
	 * @return a list of element instances
	 */
	def getElementTexts(num) {
		if (!num)
			throw new NullPointerException("parameter num is null")

		def elems = []
		for (i in 1..num) {
			elems << getElementText()
		}
		return elems
	}

	/**
	 * Generate an annotation set instance having between #MIN_ANNOS_PER_ANNOSET and
	 * #MAX_ANNOS_PER_ANNOSET annotations.
	 *
	 * @return an annotation set instance
	 */
	def getAnnotationSet() {
		return getAnnotationSet(MIN_ANNOS_PER_ANNOSET, MAX_ANNOS_PER_ANNOSET)
	}

	/**
	 * Generate an annotation set instance having between minAnno and maxAnno annotations.
	 *
	 * @param minAnno
	 * 		minimum number of annotations
	 * @param maxAnno
	 * 		maximum number of annotations
	 * @return an annotation set instance
	 */
	def getAnnotationSet(minAnno, maxAnno) {
		return getAnnotationSet(minAnno, maxAnno, null)
	}

	/**
	 * Generate an annotation set instance having between minAnno and maxAnno annotations.
	 *
	 * @param minAnno
	 * 		minimum number of annotations
	 * @param maxAnno
	 * 		maximum number of annotations
	 * @param persons
	 * 		a set of persons to choose the creator from (null = generate an URI)
	 * @return an annotation set instance
	 */
	def getAnnotationSet(minAnno, maxAnno, persons) {
		if (minAnno == null) minAnno = MIN_ANNOS_PER_ANNOSET
		if (maxAnno == null) maxAnno = MAX_ANNOS_PER_ANNOSET

		if (minAnno < 0 || maxAnno < 0)
			throw new IllegalArgumentException("parameter minAnno or maxAnno is negative")
		if (minAnno > maxAnno)
			throw new IllegalArgumentException("minAnno is greater than maxAnno")

		def set = new AnnotationSet(
				uri: getUri(TestUriGenerator.TYPE_DATA), title: getTitle())

		// generate annotations
		def num = minAnno + random.nextInt(maxAnno - minAnno + 1)
		if (num > 0) {
			set.annotations = []
			for (int i = 0; i < num; i++) {
				set.annotations[i] = getAnnotation()
			}
		}
		return set
	}

	/**
	 * Generate #NUM_ANNOSSET annotation set instances having between #MIN_ANNOS_PER_ANNOSET and
	 * #MAX_ELEMS_PER_DOC annotations.
	 *
	 * @return a list of annotation set instances
	 */
	def getAnnotationSets() {
		return getAnnotationSets(NUM_ANNOSSET, MIN_ANNOS_PER_ANNOSET, MAX_ANNOS_PER_ANNOSET, null)
	}

	/**
	 * Generate a given number of annotation set instances having between #MIN_ANNOS_PER_ANNOSET and
	 * #MAX_ANNOS_PER_ANNOSET annotations.
	 *
	 * @param num
	 * 		number of annotation set instances to be generated
	 * @return a list of annotation set instances
	 */
	def getAnnotationSets(num) {
		return getAnnotationSets(num, MIN_ANNOS_PER_ANNOSET, MAX_ANNOS_PER_ANNOSET, null)
	}

	/**
	 * Generate a given number of annotation set instances having between #MIN_ANNOS_PER_ANNOSET and
	 * #MAX_ANNOS_PER_ANNOSET annotations.
	 *
	 * @param num
	 * 		number of annotation set instances to be generated
	 * @param persons
	 * 		a set of persons to choose the creator from (null = generate an URI)
	 * @return a list of annotation set instances
	 */
	def getAnnotationSets(num, persons) {
		return getAnnotationSets(num, MIN_ANNOS_PER_ANNOSET, MAX_ANNOS_PER_ANNOSET, persons)
	}

	/**
	 * Generate a given number of annotation set instances.
	 *
	 * @param num
	 * 		number of annotation set instances to be generated
	 * @param minAnno
	 * 		minimum number of annotations
	 * @param maxAnno
	 * 		maximum number of annotations
	 * @return a list of annotation set instances
	 */
	def getAnnotationSets(num, minElem, maxElem) {
		return getAnnotationSets(num, minElem, maxElem, null)
	}

	/**
	 * Generate a given number of annotation set instances.
	 *
	 * @param num
	 * 		number of annotation set instances to be generated
	 * @param minAnno
	 * 		minimum number of annotations
	 * @param maxAnno
	 * 		maximum number of annotations
	 * @param persons
	 * 		a set of persons to choose the creator from (null = generate an URI)
	 * @return a list of annotation set instances
	 */
	def getAnnotationSets(num, minElem, maxElem, persons) {
		if (!num)
			throw new NullPointerException("parameter num is null")

		def docs = []
		for (i in 1..num) {
			docs << getAnnotationSet(minElem, maxElem, persons)
		}
		return docs
	}

	/**
	 * Generate an annotation.
	 *
	 * @return an annotation
	 */
	def getAnnotation() {
		return getAnnotation(null)
	}

	/**
	 * Generate an annotation with a given URI.
	 *
	 * @param uri
	 * 		a URI (null = generated URI)
	 * @return an annotation
	 */
	def getAnnotation(uri) {
		return new Annotation(
				uri: uri ? uri : getUri(TestUriGenerator.TYPE_DATA), label: getLabel(), comment: getComment(),
				propertyUri: getProperty())
	}

	/**
	 * Generate a given number of annotations
	 *
	 * @return a list of annotations
	 */
	def getAnnotations(num) {
		if (!num)
			throw new NullPointerException("parameter num is null")

		def annos = []
		for (i in 1..num) {
			annos << getAnnotation()
		}
		return annos
	}

	/**
	 * Generate a resource instance having between #MIN_PROPS_PER_RES and
	 * #MAX_PROPS_PER_RES elements.
	 *
	 * @return a resource instance
	 */
	def getResource() {
		return getResource(MIN_PROPS_PER_RES, MAX_PROPS_PER_RES)
	}

	/**
	 * Generate a resource instance having between minElem and maxElem properties.
	 *
	 * @param minProp
	 * 		minimum number of properties
	 * @param maxProp
	 * 		maximum number of properties
	 * @return a resource instance
	 */
	def getResource(minProp, maxProp) {
		if (minProp == null) minProp = MIN_PROPS_PER_RES
		if (maxProp == null) maxProp = MAX_PROPS_PER_RES

		if (minProp < 0 || maxProp < 0)
			throw new IllegalArgumentException("parameter minElem or maxElem is negative")
		if (minProp > maxProp)
			throw new IllegalArgumentException("minElem is greater than maxElem")

		def res = new Resource(uri: getUri(TestUriGenerator.TYPE_DATA), label: getLabel())

		// generate some properties
		// TODO implement
		/*
		def num = minProp + random.nextInt(maxProp - minProp + 1)
		if (num > 0) {
			res.properties = new TypedPropertyValue[num]
			for (int i = 0; i < num; i++) {
				res.properties[i] = getTypedPropertyValue()
			}
		}
		*/
		return res
	}

	/**
	 * Generate #NUM_RESS resource instances having between #MIN_PROPS_PER_RES and
	 * #MAX_PROPS_PER_RES elements.
	 *
	 * @return a list of resource instances
	 */
	def getResources() {
		return getResources(NUM_RESS, MIN_PROPS_PER_RES, MAX_PROPS_PER_RES)
	}

	/**
	 * Generate a given number of resource instances having between #MIN_PROPS_PER_RES and
	 * #MAX_PROPS_PER_RES properties.
	 *
	 * @param num
	 * 		number of resource instances to be generated
	 * @return a list of resource instances
	 */
	def getResources(num) {
		return getResources(num, MIN_PROPS_PER_RES, MAX_PROPS_PER_RES)
	}

	/**
	 * Generate a given number of resource instances.
	 *
	 * @param num
	 * 		number of resource instances to be generated
	 * @return a list of resource instances
	 */
	def getResources(num, minElem, maxElem) {
		if (!num)
			throw new NullPointerException("parameter num is null")

		def ress = []
		for (i in 1..num) {
			ress << getResource(minElem, maxElem)
		}
		return ress
	}

	/**
	 * Generates an instance of TypedPropertyValue. The value is a literal with
	 * probability 0.5.
	 *
	 * @return a TypedPropertyValue
	 */
	def getTypedPropertyValue() {
		def isLiteral = random.nextBoolean()
		throw new UnsupportedOperationException("t.b.d.");
		/*
		return new TypedPropertyValue(
				property: getUri(TestUriGenerator.TYPE_DATA),
				value: isLiteral ? getTitle() : getUri(TestUriGenerator.TYPE_DATA),
				isLiteral: isLiteral)
		*/
	}

	//
	// HELPER METHODS
	//
	private getUri = {type ->
		uriGenerator.generateUri(getNs(), type)
	}

	private getFirstName = {
		FIRST_NAMES[random.nextInt(FIRST_NAMES.size())]
	}

	private getLastName = {
		LAST_NAMES[random.nextInt(LAST_NAMES.size())]
	}

	private getEmail = {fn, ln ->
		"$fn.$ln@$domain"
	}

	private getTitle = {
		TITLES[random.nextInt(TITLES.size())]
	}

	private getText = {
		TEXTS[random.nextInt(TEXTS.size())]
	}

	private getProperty = {
		URI.create(PROPERTIES[random.nextInt(PROPERTIES.size())])
	}

	private getDomainRange = {
		URI.create(DOMAIN_RANGE[random.nextInt(DOMAIN_RANGE.size())])
	}

	private getComment = {
		def c = TEXTS[random.nextInt(TEXTS.size())]
		return c.substring(0, Math.min(20 + random.nextInt(20), c.size()))
	}

	private getComments = {
		def comments = []
		LOCALE.each {
			def c = TEXTS[random.nextInt(TEXTS.size())]
			commenty << c.substring(0, Math.min(20 + random.nextInt(20), c.size()))
		}
		return comments
	}

	private getLabel = {
		return ANNOTATIONS[random.nextInt(ANNOTATIONS.size())]
	}

	private getLabels = {
		def labels = []
		LOCALE.each {
			labels <<  ANNOTATIONS[random.nextInt(ANNOTATIONS.size())]
		}
		return labels
	}

	private getNs = {
		"http://$domain"
	}

	//
	// DATA
	//
	static LOCALE = ["en", "fr-FR", "de-DE"]

	static ANNOTATIONS = ["title", "creator", "first name", "last name", "age", "city", "water", "river", "sea"]

	static FIRST_NAMES = [
			"Alice", "Bob", "Carol", "Dave", "Eve", "Oscar", "Peggy"
	]

	static LAST_NAMES = [
			"Smith", "Johnson", "Williams", "Jones", "Brown", "Miller", "Davis", "Moore", "Taylor", "Anderson"
	]

	static TITLES = [
			"As I Lay Dying", "A Time to Kill", "Cover Her Face", "The Cricket on the Hearth",
			"A Darkling Plain", "Death Be Not Proud", "In Dubious Battle", "The Mermaids Singing",
			"Moab Is My Washpot", "Mother Night", "Mr Standfast", "Number the Stars",
			"Of Mice and Men", "Everything is Illuminated", "Eyeless in Gaza", "Fame Is the Spur",
			"A Handful of Dust", "The Heart Is Deceitful Above All Things", "His Dark Materials", "The House of Mirth",
			"Little Hands Clapping", "Look Homeward, Angel", "Many Waters", "A Many-Splendoured Thing",
			"Noli Me Tangere", "Of Human Bondage", "Oh! To be in England", "Paths of Glory",
			"Some Buried Caesar", "Such, Such Were the Joys", "Surprised by Joy", "Tender Is the Night"
	]

	static DOMAIN_RANGE = [
			"http://xmlns.com/foaf/0.1/Agent", "http://xmlns.com/foaf/0.1/Organization",
			"http://xmlns.com/foaf/0.1/Person", "http://xmlns.com/foaf/0.1/Project"
	]

	static PROPERTIES = [
			"http://xmlns.com/foaf/0.1/mbox", "http://xmlns.com/foaf/0.1/sha1", "http://xmlns.com/foaf/0.1/homepage",
			"http://xmlns.com/foaf/0.1/accountName"
	]

	static TEXTS = [
			"""Wer reitet so spät durch Nacht und Wind?
Es ist der Vater mit seinem Kind.
Er hat den Knaben wohl in dem Arm,
Er faßt ihn sicher, er hält ihn warm.""",
			"""Mein Sohn, was birgst du so bang dein Gesicht?
Siehst Vater, du den Erlkönig nicht!
Den Erlenkönig mit Kron' und Schweif?
Mein Sohn, es ist ein Nebelstreif.""",
			"""Du liebes Kind, komm geh' mit mir!
Gar schöne Spiele, spiel ich mit dir,
Manch bunte Blumen sind an dem Strand,
Meine Mutter hat manch gülden Gewand.""",
			"""Mein Vater, mein Vater, und hörest du nicht,
Was Erlenkönig mir leise verspricht?
Sei ruhig, bleibe ruhig, mein Kind,
In dürren Blättern säuselt der Wind.""",
			"""Willst feiner Knabe du mit mir geh'n?
Meine Töchter sollen dich warten schön,
Meine Töchter führen den nächtlichen Reihn
Und wiegen und tanzen und singen dich ein.""",
			"""Mein Vater, mein Vater, und siehst du nicht dort
Erlkönigs Töchter am düsteren Ort?
Mein Sohn, mein Sohn, ich seh'es genau:
Es scheinen die alten Weiden so grau.""",
			"""Ich lieb dich, mich reizt deine schöne Gestalt,
Und bist du nicht willig, so brauch ich Gewalt!
Mein Vater, mein Vater, jetzt faßt er mich an,
Erlkönig hat mir ein Leids getan.""",
			"""Dem Vater grauset's, er reitet geschwind,
Er hält in den Armen das ächzende Kind,
Erreicht den Hof mit Mühe und Not,
In seinen Armen das Kind war tot.""",
			"""Fest gemauert in der Erden
Steht die Form aus Lehm gebrannt.
Heute muß die Glocke werden!
Frisch, Gesellen, seid zur Hand!
Von der Stirne heiß
Rinnen muß der Schweiß,
Soll das Werk den Meister loben!
Doch der Segen kommt von oben.""",
			"""Zum Werke, das wir ernst bereiten,
Geziemt sich wohl ein ernstes Wort;
Wenn gute Reden sie begleiten,
Dann fließt die Arbeit munter fort.
So laßt uns jetzt mit Fleiß betrachten,
Was durch schwache Kraft entspringt;
Den schlechten Mann muß man verachten,
Der nie bedacht, was er vollbringt.
Das ist's ja, was den Menschen zieret,
Und dazu ward ihm der Verstand,
Daß er im Herzen spüret,
Was er erschaffen mit seiner Hand.""",
			"""Nehmt Holz vom Fichtenstamme
Doch recht trocken laßt es sein,
Daß die eingepreßte Flamme
Schlage zu dem Schwalch hinein!
Kocht des Kupfers Brei!
Schnell das Zinn herbei,
Daß die zähe Glockenspeise
Fließe nach der rechten Weise!""",
			"""Was in des Dammes tiefer Grube
Die Hand mit Feuers Hilfe baut,
Hoch auf des Turmes Glockenstube,
Da wird es von uns zeugen laut.
Noch dauern wird's in späten Tagen
Und rühren vieler Menschen Ohr,
Und wird mit dem Betrübten klagen
Und stimmen zu der Andacht Chor.
Was unten tief dem Erdensohne
Das wechselnde Verhängnis bringt,
Das schlägt an die metallne Krone,
Die es erbaulich weiter klingt.""",
			"""Weiße Blasen seh' ich springen;
Wohl! die Massen sind im Fluß.
Laßt's mit Aschensalz durchdringen,
Das befördert schnell den Guß.
Auch vom Schaume rein
Muß die Mischung sein,
Daß vom reinlichen Metalle
Rein und voll die stimme schalle.""",
			"""Denn mit der Freude Feierklange
Begrüßt sie das geliebte Kind
Auf seines Lebens ersten Gange,
Den es in des Schlafes Arm beginnt.
Ihm ruhen noch im Zeitenschoße
Die schwarzen und die heitern Lose;
Der Mutterliebe zarte Sorgen
Bewachen seinen goldnen Morgen.
Die Jahre fliehen pfeilgeschwind.
Vom Mädchen reißt sich stolz der Knabe,
Er stürmt ins Leben wild hinaus,
Durchmißt die Welt am Wanderstabe,
Fremd kehrt er heim ins Vaterhaus.
Und herrlich in der Jugend Prangen,
Wie ein Gebild aus Himmelshöhn,
Mit züchtigen, verschämten Wangen,
Sieht er die Jungfrau vor sich stehn.
Da faßt ein namenloses Sehnen
Des Jünglings Herz, er irrt allein,
Aus seinen Augen brechen Tränen,
Er flieht der Brüder wilden Reihn.
Errötend folgt er ihren Spuren
Und ist von ihrem Gruß beglückt,
Das Schönste sucht er auf den Fluren,
Womit er seine Liebe schmückt.
O zarte Sehnsucht, süßes Hoffen,
Der ersten Liebe goldne Zeit,
Das Auge sieht den Himmel offen,
Es schwelgt das Herz in Seligkeit;
O daß sie ewig grünen bliebe,
Die schöne Zeit der jungen Liebe!""",
			"""Wie sich schon die Pfeifen bräunen!
Dieses Stäbchen tauch' ich ein:
Sehn wir's überglast erscheinen,
Wird's zum Gusse zeitig sein.
Jetzt, Gesellen, frisch!
Prüft mir das Gemisch,
Ob das Spröde mit dem Weichen
Sich vereint zum guten Zeichen.""",
			"""Denn wo das Strenge mit dem Zarten,
Wo Starkes sich und Mildes paarten,
Da gibt es einen guten Klang.
Drum prüfe, wer sich ewig bindet,
Ob sich das Herz zum Herzen findet!
Der Wahn ist kurz, die Reu' ist lang.
Lieblich in der Bräute Locken
Spielt der jungfräuliche Kranz,
Wenn die hellen Kirchenglocken
Laden zu des Festes Glanz.
Ach! des Lebens schönste Feier
Endigt auch den Lebensmai:
Mit dem Gürtel, mit dem Schleier
Reißt der schöne Wahn entzwei.
Die Leidenschaft flieht,
Die Liebe muß bleiben;
Die Blume verblüht,
Die fruchtmuß treiben.
Der Mann muß hinaus
In's feindliche Leben,
Muß wirken und streben
Und pflanzen und schaffen,
Erlisten, erraffen,
Muß wetten und wagen,
Das Glück zu erjagen.
Da strömet herbei die unendliche Gabe,
Es füllt sich der Speicher mit köstlicher Habe,
Die Räume wachsen, es dehnt sich das Haus.
Und drinnen waltet
Die züchtige Hausfrau,
Die Mutter der Kinder,
Und herrschet weise
Im häuslichen Kreise,
Und lehret die Mädchen
Und wehret den Knaben,
Und reget ohn' Ende
Die fleißigen Hände,
Und mehrt den Gewinn
Mit ordnendem Sinn,
Und füllet mit Schätzen die duftenden Laden,
Und dreht um die schnurrende Spindel den Faden,
Und sammelt im reinlich geglätteten Schrein
Die schimmernde Wolle, den schneeigen Lein,
Und füget zum Guten den Glanz und den Schimmer,
Und ruhet nimmer.""",
			"""Und der Vater mit frohem Blick
Von des Hauses weitschauendem Giebel
Überzählt sein blühendes Glück,
Siehet der Pfosten ragende Bäume,
Und der Scheunen gefüllte Räume,
Und die Speicher, vom Segen gebogen,
Und des Kornes bewegte Wogen,
Rühmt sich mit stolzem Mund:
Fest, wie der Erde Grund,
Gegen des Unglücks Macht
Steht mir des Hauses Pracht!
Doch mit des Geschickes Mächten
Ist kein ew'ger Bund zu flechten,
Und das Unglück schreitet schnell.""",
			"""Wohl! nun kann der Guß beginnen,
Schön gezacket ist der Bruch,
Doch bevor wir's lassen rinnen,
Betet einen frommen Spruch!
Stoßt den Zapfen aus!
Gott bewahr' das Haus!
Rauschend in des Henkels Bogen
Schießt's mit feuerbraunen Wogen.""",
			"""Wohltätig ist des Feuers Macht,
Wenn sie der Mensch bezähmt, bewacht,
Und was er bildet, was er schafft,
Das dankt er dieser Himmelskraft,
Wenn sie der Fessel sich entrafft,
Einhertritt auf der eignen Spur,
Die freie Tochter der Natur.
Wehe, wenn sie losgelassen,
Wachsend ohne Widerstand,
Durch die volkbelebten Gassen
Wälzt den ungeheuren Brand!
Denn die Elemente hassen
Das Gebild der Menschenhand.
Aus der Wolke
Quillt der Segen,
Strömt der Regen;
Aus der Wolke, ohne Wahl,
Zuckt der Strahl.
Hört ihr's wimmern hoch im Turm?
Das ist Sturm!
Rot, wie Blut,
Ist der Himmel;
Das ist nicht des Tages Glut!
Welch Getümmel
Straßen auf!
Dampf wallt auf!
Flackernd steigt die Feuersäule;
Durch der Straße lange Zeile
Wächst es fort mit Windeseile;
Kochend, wie aus Ofens Rachen,
Glühn die Lüfte, Balken krachen,
Pfosten stürzen, Fenster klirren,
Kinder jammern, Mütter irren,
Tiere wimmern
Unter Trümmern;
Alles rennet, rettet, flüchtet,
Taghell ist die Nacht gelichtet.
Durch die Hände lange Kette
Um die Wette
Fliegt der Eimer; hoch im Bogen
Spritzen Quellen Wasserwogen.
Heulend kommt der Sturm geflogen,
Der die Flamme brausend sucht;
Prasselnd in die dürre Frucht
Fällt sie, in des Speichers Räume,
In der Sparren dürre Bäume, Und als wollte sie im Wehen
Mit sich fort der Erde Wucht
Reißen in gewalt'ger Flucht,
Wächst sie in des Himmels Höhen
Riesengroß.
Hoffnungslos
Weicht der Mensch der Götterstärke:
Müßig sieht er seine Werke
Und bewundernd untergehn.""",
			"""Leergebrannt
Ist die Stätte,
Wilder Stürme rauhes Bette
In den öden Fensterhöhlen
Wohnt das Grauen,
Und des Himmels Wolken schauen
Hoch hinein.""",
			"""Einen Blick
Nach dem Grabe
Seiner Habe
Sendet noch der Mensch zurück ۃ
Greift fröhlich dann zum Wanderstabe.
Was des Feuers Wut ihm auch geraubt,
Ein süßer Trost ist ihm geblieben:
Er zählt die Häupter seiner Lieben,
Und sieh! ihm fehlt kein teures Haupt.""",
			"""In die Erd' ist's aufgenommen,
Glücklich ist die Form gefüllt;
Wird's auch schön zu Tage kommen,
Daß es Fleiß und Kunst vergilt?
Wenn der Guß mißlang?
Wenn die Form zersprang?
Ach! vielleicht, indem wir hoffen,
Hat uns Unheil schon getroffen""",
			"""Dem dunklen Schoß der heil'gen Erde
Vertrauen wir der Hände Tat,
Vertraut der Sämann seine Saat
Und hofft, daß sie entkeimen werde
Zum Segen, nach des Himmels Rat.
Noch köstlicheren Samen bergen
Wir trauernd in der Erde Schoß
Und hoffen, daß er aus den Särgen
Erblühen soll zu schönerm Los.""",
			"""Von dem Dome,
Schwer und bang,
Tönt die Glocke
Grabgesang.
Ernst begleiten ihre Trauerschläge
Einen Wanderer auf dem letzten Wege""",
			"""Ach! die Gattin ist's, die teure,
Ach! es ist die treue Mutter,
Die der schwarze Fürst der Schatten
Wegführt aus dem Arm des Gatten,
Aus der zarten Kinder Schar,
Die sie blühend ihm gebar,
Die sie an der treuen Brust
Wachsen sah mit Mutterlust ۃ
Ach! des Hauses zarte Bande
Sind gelöst auf immerdar;
Denn sie wohnt im Schattenlande,
Die des Hauses Mutter war;
Denn es fehlt ihr treues Walten,
Ihre Sorge wacht nicht mehr;
An verwaister Stätte schalten
Wird die Fremde, liebeleer.""",
			"""Bis die Glocke sich verkühlet,
Laßt die strenge Arbeit ruhn!
Wie im Laub der Vogel spielet,
Mag sich jeder gütlich tun.
Winkt der Sterne Licht,
Ledig aller Pflicht,
Hört der Bursch die Vesper schlagen;
Meister muß sich immer plagen.""",
			"""Munter fördert seine Schritte
Fern im wilden Forst der Wanderer
Nach der lieben Heimathütte.
Blökend ziehen heim die Schafe,
Und der Rinder
Breitgestirnte, glatte Scharen
Kommen brüllend,
Die gewohnten Ställe füllend.
Schwer herein
Schwankt der Wagen
Kornbeladen;
Bunt von Farben,
Auf den Garben
Liegt der Kranz,
Und das junge Volk der Schnitter
Fliegt im Tanz.
Markt und Straße werden stiller;
Um des Lichts gesell'ge Flamme
Sammeln sich die Hausbewohner,
Und das Stadttor schließt sich knarrend.
Schwarz bedecket
Sich die Erde;
Doch den sichern Bürger schrecket
Nicht die Nacht,
Die den Bösen gräßlich wecket;
Denn das Auge des Gesetzes wacht.""",
			"""Heil'ge Ordnung, segensreiche
Himmelstochter, die das Gleiche
Frei und leicht und freudig bindet,
Die der Städte Bau gegründet,
Die herein von den Gefilden
Rief den ungesell'gen Wilden,
Eintrat in der Menschen Hütten,
Sie gewöhnt zu sanften Sitten,
Und das teuerste der Bande
Wob, den Trieb zum Vaterlande!""",
			"""Tausend fleiß'ge Hände regen,
Helfen sich in munterm Bund,
Und in feurigem Bewegen
Werden alle Kräfte kund.
Meister rührt sich und Geselle
In der Freiheit heil'gem Schutz;
Jeder freut sich seiner Stelle,
Bietet dem Verächter Trutz.
Arbeit ist des Bürgers Zierde,
Segen ist der Mühe Preis:
Ehrt den König seine Würde,
Ehret uns der Hände Fleiß.""",
			"""Holder Friede,
Süße Eintracht,
Weilet, eilet
Freundlich über dieser Stadt!
Möge nie der Tag erscheinen,
Wo des rauhen Krieges Horden
Dieses stille Tal durchtoben;
Wo der Himmel,
Den des Abends sanfte Röte
Lieblich malt,
Von der Dörfer, von der Städte
Wildem Brande schrecklich strahlt!""",
			"""Nun zerbrecht mir das Gebäude,
Seine Absicht hat's erfüllt,
Daß sich Herz und Auge weide
An dem wohlgelungnen Bild.
Schwingt den Hammer, schwingt,
Bis der Mantel springt!
Wenn die Glock' soll auferstehen,
Muß die Form in Stücken gehen.""",
			"""Der Meister kann die Form zerbrechen
Mit weiser Hand, zur rechten Zeit;
Doch wehe, wenn in Flammenbächen
Das glüh'nde Erz sich selbst befreit!
Blindwütend mit des Donners Krachen
Zersprengt es das geborstne Haus,
Und wie aus offnem Höllenrachen
Speit es Verderben zündend aus.
Wo rohe Kräfte sinnlos walten,
Da kann sich kein Gebild gestalten;
Wenn sich die Völker selbst befrein,
Da kann die Wohlfahrt nicht gedeihn.""",
			"""Weh, wenn sich in dem Schoß der Städte
Der Feuerzunder still gehäuft,
Das Volk, zerreißend seine Kette,
Zur Eigenhilfe schrecklich greift!
Da zerret an der Glocke Strängen
Der Aufruhr, daß sie heulend schallt,
Und, nur geweiht zu Friedensklängen,
Die Losung anstimmt zur Gewalt.""",
			"""Freiheit und Gleichheit! hört man schallen;
Der ruh'ge Bürger greift zur Wehr,
Die Straßen füllen sich, die Hallen,
Und Würgerbanden ziehn umher.
Da werden Weiber zu Hyänen
Und treiben mit Entsetzen Scherz:
Noch zuckend, mit des Panthers Zähnen,
Zerreißen sie des Feindes Herz.
Nichts Heiliges ist mehr, es lösen
Sich alle Bande frommer scheu;
Der Gute räumt den Platz dem Bösen,
Und alle Laster walten frei.
Gefährlich ist's, den Leu zu wecken,
Verderblich ist des Tigers Zahn,
Jedoch der schrecklichste der Schrecken,
Das ist der Mensch in seinem Wahn.
Weh denen, die dem Ewigblinden
Des Lichtes Himmelsfackel leihn!
Sie strahlt ihm nicht, sie kann nur zünden,
Und äschert Städt' und Länder ein.""",
			"""Freude hat mit Gott gegeben!
Sehet! wie ein gold'ner Stern
Aus der Hülse, blank und eben,
Schält sich der metallne Kern.
Von dem Helm zum Kranz
Spielt's wie Sonnenglanz.
Auch des Wappens nette Schilder
Loben den erfahrnen Bilder.""",
			"""Herein! herein,
Gesellen alle, schließt den Reihen,
Daß wir die Glocke taufend weihen!
Concordia soll ihr Name sein.
Zur Eintracht, zu herzinnigem Vereine
Versammle sie die liebende Gemeine.""",
			"""Und dies sei fortan ihr Beruf,
Wozu der Meister sie erschuf:
Hoch über'm niedern Erdenleben
Soll sie im blauen Himmelszelt,
Die Nachbarin des Domes, schweben
Und grenzen an die Sternenwelt,
Soll eine Stimme sein von oben,
Wie der Gestirne helle Schar,
Die ihren Schöpfer wandelnd loben
Und führen das bekränzte Jahr.
Nur ewigen und ernsten Dingen
Sei ihr metallner Mund geweiht,
Und stündlich mit den schnellen Schwingen
Berühr' im Fluge sie die Zeit.
Dem Schicksal leihe sie die Zunge;
Selbst herzlos, ohne Mitgefühl,
Begleite sie mit ihrem Schwunge
Des Lebens wechselvolles Spiel.
Und wie der Klang im Ohr vergehet,
Der mächtig tönend ihr entschallt,
So lehre sie, daß nichts bestehet,
Daß alles Irdische verhallt.""",
			"""Jetzo mit der Kraft des Stranges
Wiegt die Glock' mir aus der Gruft,
Daß sie in das Reich des Klanges
Steige, in die Himmelsluft!
Ziehet, ziehet, hebt!
Sie bewegt sich, schwebt!
Freude dieser Stadt bedeute,
Friede sei ihr erst Geläute."""
	]
}
