<?php

/**
 * Loomp backend API
 * Class for holding Mashups, which hold Lists of Chunks
 * @package Loomp_Backend
 * @author Hannes Muehleisen, Radoslaw Oldakowski
 * 
 * */

class Mashup {
	// create if empty
	public $uri;
	// creator URI
	public $creatorId;
	// timestamps
	public $createDate;
	public $modifyDate;
	public $title;
	// Array of Fragment
	public $fragments;

	function __construct($uri, $creatorId, $createDate, $modifyDate, $title, $fragments) {
		$this->uri = $uri;
		$this->creatorId = $creatorId;
		$this->createDate = $createDate;
		$this->modifyDate = $modifyDate;
		$this->title = $title;
		$this->fragments = $fragments;
		return $this;
	}
	
	function findFragment($fUri) {
		foreach ($this->fragments as $fragment) {
			if ($fragment->getUri() == $fUri) return $fragment;
		}
		return null;
	}

	function getUri() {
		return $this->uri;
	}

	function getCreatorId() {
		return $this->creatorId;
	}
	
	function getCreateDate() {
		return $this->createDate;
	}
	
	function getModifyDate() {
		return $this->modifyDate;
	}
	
	function getTitle() {
		return $this->title;
	}
	
	function getFragments() {
		return $this->fragments;
	}
	
	function setModifyDate($new_modifyDate) {
		$this->modifyDate = $new_modifyDate;
	}
	
	function setTitle($new_title) {
		$this->title = $new_title;
	}
	
	function setUri($uri) {
		$this->uri = $uri;
	}
	
	function setFragments($new_fragments) {
		$this->fragments = $new_fragments;
	}
	
	function addFragment($f) {
		$this->fragments[] = $f;
	}
	
	
	// for debugging 
	function _htmlOutput() {
		
		echo	"MashupURI: " .$this->uri . "<br \>" .
			"creatorURI: " .$this->creatorId . "<br \>" .
			"createDate: " .$this->createDate . "<br \>" .
			"modifyDate: " .$this->modifyDate . "<br \>" .
			"title: " .$this->title . "<br \>" .
			"<B>fragments:</B> <br \>";
			
		foreach ($this->fragments as $f) {
			$f->_htmlOutput();
		}
	}

}
?>
