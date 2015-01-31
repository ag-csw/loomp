<?php
// ars/models/ARS/LocatedAnnotation.php

/**
 * @see Annotation
 */
require_once 'loomp-backend/annotation.php';

/**
 * Extended (located) Loomp annotation.
 * 
 * @uses       Annotation
 * @category   ARS
 * @package    ARS
 * @copyright  Copyright (c) 2009 Patrick Jungermann, Freie Universität Berlin
 * @license    http://www.gnu.org/licenses/lgpl-3.0.html     LGPL, version 3.0
 * @author     Patrick Jungermann
 */
class ARS_LocatedAnnotation extends Annotation
{
	/**
	 * The URI to the resource that was described.
	 * 
	 * @var string
	 */
	public $about;
	/**
	 * The start of the annotated area within a referenced text.
	 * 
	 * @var integer
	 */
	public $begin;
	/**
	 * The start of the annotated area within a referenced text.
	 * 
	 * @var integer
	 */
	public $end;
	/**
	 * The URI to a resource that also contains more information about this one.
	 * 
	 * @var string
	 */
	public $seeAlso;

	/**
	 * Constructor
	 * 
	 * @param   Annotation  $annotation
	 * 		The Loomp annotation that has to be used as base.
	 * @param   integer     $begin
	 * 		The begin of the annotated area within the referenced text.
	 * @param   integer     $end
	 * 		The end of the annotated area within the referenced text.
	 * @param   string      $about[optional]
	 * 		The URI to the described resource.
	 * @param   string      $seeAlso[optional]
	 * 		The URI to an resource that contains more information about this one.
	 * @return  ARS_LocatedAnnotation
	 */
	public function __construct($annotation, $begin, $end, $about = null, $seeAlso = null)
	{
		parent::__construct(
			$annotation->getUri(),
			$annotation->getLabel(),
			$annotation->getDescription(),
			$annotation->getType(),
			$annotation->getAnnDomain(),
			$annotation->getAnnRange()
		);

		$this->begin = $begin;
		$this->end = $end;
		$this->about = $about;
		$this->seeAlso = $seeAlso;

		return $this;
	}

	/**
	 * Returns the string representation of the object.
	 * 
	 * @return  string  The string representation of the object.
	 */
	public function __toString()
	{
		return __CLASS__ . "($this->begin - $this->end; $this->uri; $this->about; $this->seeAlso)";
	}

	/**
	 * Returns the URI to the resource that was described.
	 * 
	 * @return  string  The URI to the resource.
	 */
	public function getAbout()
	{
		return $this->about;
	}

	/**
	 * Returns the begin of the annotated area withtin the referenced text.
	 * 
	 * @return  integer  The begin. 
	 */
	public function getBegin()
	{
		return $this->begin;
	}

	/**
	 * Returns the end of the annotated area within the referenced text.
	 * 
	 * @return  integer  The end.
	 */
	public function getEnd()
	{
		return $this->end;
	}
}

?>