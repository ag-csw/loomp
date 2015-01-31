<?php
// ars/models/ARS/Service/Result.php

/**
 * @see ARS_UIMA_Annotation
 */
require_once dirname(__FILE__) . '/../UIMA/Annotation.php';

/**
 * Annotation Recommender Service (ARS) result.
 * 
 * @uses       ARS_UIMA_Annotation
 * @category   ARS
 * @package    ARS
 * @subpackage Service
 * @copyright  Copyright (c) 2009 Patrick Jungermann, Freie Universität Berlin
 * @license    http://www.gnu.org/licenses/lgpl-3.0.html     LGPL, version 3.0
 * @author     Patrick Jungermann
 */
class ARS_Service_Result
{
	/**
	 * All annotations that was found.
	 * 
	 * @var array
	 * @see ARS_UIMA_Annotation
	 */
	private $_annotations = array();

	/**
	 * Constructor
	 * 
	 * @param   string  $data
	 * 		The XML result document.
	 * @return  ARS_Service_Result
	 */
	public function __construct($data)
	{
		$this->_analyze($data);

		return $this;
	}

	/**
	 * Analyzes the $data and creates all annotations that was found.
	 * 
	 * @param   string  $data
	 * 		The XML result document from the service.
	 * @return  ARS_Service_Result
	 */
	private function _analyze($data)
	{
		$namespaces = $this->_extractNamespaces($data);
		$members = $this->_getAllMembers($data);
		$annotations = array();

		preg_match_all('#<(\S+):(\S+)\s+[^>]*/>#', $data, $matches);
		for ($i = 0; $i < count($matches[0]); $i++)
		{
			$annotation = new ARS_UIMA_Annotation(
				$matches[2][$i],
				$matches[1][$i],
				$namespaces[$matches[1][$i]]
			);

			// check, if it is an annotation / member
			if (preg_match('#xmi:id="(\d+)"#', $matches[0][$i], $idMatch) && in_array($idMatch[1], $members))
			{
				// get all attributes
				preg_match_all('#\s+(\S+)="([^"]*)"#', $matches[0][$i], $attrs_matches);
				for ($k = 0; $k < count($attrs_matches[0]); $k++)
				{
					$splitted = split(':', $attrs_matches[1][$k]);
	
					$name = 1 == count($splitted) ? $splitted[0] : $splitted[1];
					$value = $attrs_matches[2][$k];
					$namespace = 1 == count($splitted) ? null : $splitted[0];
					$namespaceUrl = null == $namespace ? null : $namespaces[$namespace];
	
					$annotation->addAttribute($name, $value, $namespace, $namespaceUrl);
				}

				$annotations[(int) $idMatch[1]] = $annotation;
			}
		}

		// sort annotations by id (lower id => higher priority)
		ksort($annotations);
		$this->_annotations = array_values($annotations);

		return $this;
	}

	/**
	 * Extracts all namespaces used within the data / document.
	 * 
	 * @param   string  $data
	 * 		The text that has to be searched for namespaces.
	 * @return  ARS_Service_Result
	 */
	private function _extractNamespaces($data)
	{
		$namespaces = array();

		preg_match_all('#xmlns:(\S+)="([^"]*)"#', $data, $matches);
		for ($i = 0; $i < count($matches[0]); $i++)
		{
			$namespaces[$matches[1][$i]] = $matches[2][$i];
		}

		return $namespaces;
	}

	/**
	 * Returns the IDs of all members / annotations.
	 * 
	 * @param   string  $data
	 * 		The text that was returned from the service.
	 * @return  array  The IDs of all members.
	 */
	private function _getAllMembers($data)
	{
		$members = array();
		if (preg_match('#<cas:View[^>]*members="([^"]*)"[^>]*>#', $data, $matches))
		{
			$members = split(' ', $matches[1]);
		}

		return $members;
	}

	/**
	 * Returns the ($index)-th annotation (zero-based).
	 * 
	 * @param   integer  $index
	 * 		The index of the requested annotation.
	 * @return  ARS_UIMA_Annotation  The ($index)-th annotation.
	 */
	public function getAnnotation($index)
	{
		return $index < count($this->_annotations) ? $this->_annotations[$index] : null;
	}

	/**
	 * Returns the array of annotations (ARS_UIMA_Annotation).
	 * 
	 * @return  array  The annotations.
	 * @see ARS_UIMA_Annotation
	 */
	public function getAnnotations()
	{
		return $this->_annotations;
	}
}

?>