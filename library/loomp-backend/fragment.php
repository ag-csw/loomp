<?php

/**
 * Loomp backend API
 * Class for holding Chunks
 * @package Loomp_Backend
 * @author Hannes Muehleisen, Radoslaw Oldakowski
 * 
 * */
 
class Fragment {
	// create if empty
	public $uri;
	// creator URI
	public $creatorId;
	// timestamps
	public $createDate;
	public $modifyDate;
	public $title;
	// RDFA String
	public $content;
	// Type, can be text or sparql
	public $type;

	function __construct($uri, $creatorId, $createDate, $modifyDate, $title, $content, $type="text") {
		$this->uri = $uri;
		$this->creatorId = $creatorId;
		$this->createDate = $createDate;
		$this->modifyDate = $modifyDate;
		$this->title = $title;
		$this->setContent($content);
		$this->type = $type;
		return $this;
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
	
	function getContent() {
		return $this->content;
	}
	
	function getSaveContent() {
		return $this->content;
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
	
	function setContent($new_content) {
		$this->content = $new_content;
	}
	
	function setType($newType) {
		$this->type = $newType;
	}
	
	function getType() {
		return $this->type;
	}
	
	// for debugging
	function _htmlOutput() {
		
		echo    "fragmentURI: " .$this->uri . "<br \>" .
			"creatorURI: " .$this->creatorId . "<br \>" .
			"createDate: " .$this->createDate . "<br \>" .
			"modifyDate: " .$this->modifyDate . "<br \>" .
			"title: " .$this->title . "<br \>" .
			"type: " .$this->type . "<br \>" .
			"content: " .$this->content ."<br \><br \>";
	}
}

class Fragment_text extends Fragment {}

class Fragment_sparql extends Fragment {
	private $sparqlQuery;
	private $template;
	private $endpoint;
	
	function setSparqlQuery($newQuery) {
		$this->sparqlQuery = $newQuery;
	}
	
	function getSparqlQuery() {
		return $this->sparqlQuery;
	}
	
	function setTemplate($newTemplate) {
		$this->template = $newTemplate;
	}
	
	function getTemplate() {
		return $this->template;
	}
	
	function setContent($newContent) {
		if (!is_array($newContent)) $newContent = unserialize($newContent);
		$this->sparqlQuery = $newContent['sparql'];
		$this->template = $newContent['template'];
		$this->endpoint = $newContent['endpoint'];
		$this->content = array('sparql' => $this->sparqlQuery,'template' => $this->template,'endpoint' => $this->endpoint);
	}
	
	function getContent() {
		$model = Zend_Registry::getInstance()->rdfModel;
		$ret = "";
		try {
			if (trim($this->endpoint) == "") {// use local endpoint
				$result = $model->sparqlQuery($this->getSparqlQuery());
			}
			else {
				$c = ModelFactory::getSparqlClient($this->endpoint);
		   		$c->setOutputFormat("array");
		   		$q = new ClientQuery();
		   		$q->query($this->sparqlQuery);
		   		$result = $c->query($q);
			}
			foreach ($result as $line) {
				$tinst = $this->template;
				foreach ($line as $binding => $value) $tinst = str_replace($binding,$value->getLabel(),$tinst);
				$ret .= $tinst."\n";
			}
		}
		
		catch (Exception $e) {
			return "### Error for Sparql query '".$this->getSparqlQuery()."'. : ".$e->getMessage()." ### ";
		}
		return $ret;
	}
	
	function getSaveContent() {
		return serialize($this->content);
	}
	
	
	
}
?>
