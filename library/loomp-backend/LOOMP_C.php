<?php

/**
 * Loomp backend API
 * Implementation of the loomp voacabulary for the RDF API for PHP
 * @package Loomp_Backend
 * @author Hannes Muehleisen, Radoslaw Oldakowski
 *
 * */

class LOOMP {

    // Loomp namespace
    const LOOMP_NS = "http://www.loomp.org/loomp/0.1/";

// -------------------------------------------------------
// Loomp Classes
// -------------------------------------------------------

    function FRAGMENT() {

        return new Resource(self::LOOMP_NS . "Fragment");
    }

    function MASHUP() {

        return new Resource(self::LOOMP_NS . "Mashup");
    }

    function USER() {
        // nur ein dummy für den ns-namen
        return new Resource(self::LOOMP_NS . "User");
    }
    
    function ANNOTATION_SET() {
	
	return new Resource(self::LOOMP_NS . "AnnotationSet");
    }
    
    function ANNOTATION() {
	
	return new Resource(self::LOOMP_NS . "Annotation");
    }
    
    
// -------------------------------------------------------
// Loomp Properties
// -------------------------------------------------------
  /*  function CREATE_DATE() {

        return new Resource(self::LOOMP_NS . "createDate");
    }

    function MODIFY_DATE() {

        return new Resource(self::LOOMP_NS . "modifyDate");
    }*/

    function DESCRIBES() {

        return new Resource(self::LOOMP_NS . "describes");
    }

    function RDFA() {

        return new Resource(self::LOOMP_NS . "hasRdfaContent");
    }
    
    function TYPE() {

        return new Resource(self::LOOMP_NS . "type");
    }

    function CONTAINS() {

    	return new Resource(self::LOOMP_NS . "contains");
    }
    
    function REFERS_TO() {
	
	return new Resource(self::LOOMP_NS . "refersTo");
    }
    
    function ANNOTATION_TYPE() {
	
	return new Resource(self::LOOMP_NS . "annotationType");
    }
    
    function ANNOTATION_DOMAIN() {
	
	return new Resource(self::LOOMP_NS . "annotationDomain");
    }
    
    function ANNOTATION_RANGE() {
	
	return new Resource(self::LOOMP_NS . "annotationRange");
    }
}

?>