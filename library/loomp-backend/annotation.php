<?php

/**
 * Loomp backend API
 * Class for holding Vocabulary Annotations
 * @package Loomp_Backend
 * @author Hannes Muehleisen, Radoslaw Oldakowski
 * 
 * */

class Annotation {
	
	public $uri;
	public $label;
	public $description;
	public $type;
	public $annDomain;
	public $annRange;

	function __construct($uri, $label, $description, $type, $annDomain, $annRange) {
		
		$this->uri = $uri;
		$this->label = $label;
		$this->description = $description;
		$this->type = $type;
		$this->annDomain = $annDomain;
		$this->annRange = $annRange;
		
		return $this;
	}

	function getURI() {
		return $this->uri;
	}
	
	function getLabel() {
		return $this->label;
	}
	
	function getDescription() {
		return $this->description;
	}
	
	function getType() {
		return $this->type;
	}
	
	function getAnnDomain() {
		return $this->annDomain;	
	}
	
	function getAnnRange() {
		return $this->annRange;
	}
	
	// for debugging
	function _htmlOutput() {
		
		echo    "URI: " .$this->uri . "<br \>" .
			"label: " .$this->label . "<br \>" .
			"description: " .$this->description . "<br \>" .
			"type: " .$this->type . "<br \>" .
			"annDomain: " .$this->annDomain . "<br \>" .
			"annRange: " .$this->annRange ."<br \><br \>";
		
	}

}
?>
