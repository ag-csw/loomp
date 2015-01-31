<?php

/**
 * Loomp backend API
 * Class for holding Vocabularies, which hold Lists of Annotation
 * @package Loomp_Backend
 * @author Hannes Muehleisen, Radoslaw Oldakowski
 * 
 * */

class Vocabulary {
	
	// filename
	public $id;
	public $label;
	// rdfs:comment
	public $description;
	// Array of Annotation
	public $annotations;

	function __construct($id, $label, $description, $annotations) {
		
		$this->id = $id;
		$this->label = $label;
		$this->description = $description;
		$this->annotations = $annotations;
		
		return $this;
	}

	function getID() {
		return $this->id;
	}

	function getLabel() {
		return $this->label;
	}
	
	function getDescription() {
		return $this->description;
	}
	
	function getAnnotations() {
		return $this->annotations;
	}
	
	function _htmlOutput() {
		
		echo    "ID: " .$this->id . "<br \>" .
			"label: " .$this->label . "<br \>" .
			"description: " .$this->description . "<br \>" .
			"annotations:  <br \>-----------------<br \>";
			foreach ($this->annotations as $a) {
				$a->_htmlOutput();
			}
		
	}
}
?>
