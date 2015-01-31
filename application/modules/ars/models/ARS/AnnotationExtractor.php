<?php
// ars/models/ARS/AnnotationExtractor.php

/**
 * @see ARS_LocatedAnnotation
 */
require_once dirname(__FILE__) . '/LocatedAnnotation.php';
// Loomp backend API functions
require_once 'loomp-backend/api.php';

/**
 * Extracts existing annotations from a text
 * and returns (located) Loomp annotation.
 * 
 * @uses       Annotation
 * @uses       ARS_LocatedAnnotation
 * @category   ARS
 * @package    ARS
 * @copyright  Copyright (c) 2009 Patrick Jungermann, Freie Universität Berlin
 * @license    http://www.gnu.org/licenses/lgpl-3.0.html     LGPL, version 3.0
 * @author     Patrick Jungermann
 */
class ARS_AnnotationExtractor
{
	/**
	 * The Loomp annotations.
	 * 
	 * @var array
	 */
	private $_annotations = array();
	/**
	 * The cleaned text (without the annotations).
	 * 
	 * @var string
	 */
	private $_cleanedText = '';
	/**
	 * The annotations (ARS_LocatedAnnotation) extracted from the text.
	 * 
	 * @var array
	 * @see ARS_LocatedAnnotation
	 */
	private $_extractedAnnotations = array();

	/**
	 * Constructor
	 * 
	 * @param   string  $text
	 * 		The text from which annotations has to be extracted.
	 * @return  ARS_AnnotationExtractor
	 */
	public function __construct($text)
	{
		// get all loomp vocabularies and add their annotations
		$loompApi = new LoompApi();
		$vocabList = $loompApi->getVocabularies();
		$vocabs = array();
		foreach ($vocabList as $vocab)
		{
			$vocabs[] = $loompApi->getVocabulary( $vocab['ID'] );
		}
		$this->_addAllAnnotations( $vocabs );

		// extract the annotations contained within the text
		$this->_extractAnnotations( $text );

		return $this;
	}

	/**
	 * Adds all annotations from the given array of
	 * vocabularies to the internal store.
	 * 
	 * @param   array  $vocabularies
	 * 		The array of vocabularies.
	 * @return  ARS_AnnotationsExtractor.
	 */
	private function _addAllAnnotations($vocabularies)
	{
		foreach ($vocabularies as $vocabulary)
		{
			foreach ($vocabulary->getAnnotations() as $annotation)
			{
				$this->_annotations[$annotation->getUri()] = $annotation;
			}
		}

		return $this;
	}

	/**
	 * Extracts all annotations found in the text.
	 * 
	 * @param   string  $text
	 * 		The text, from which the annotations has to be extracted. 
	 * @return  ARS_AnnotationsExtractor
	 */
	private function _extractAnnotations($text)
	{
		$pattern = '#<span[^>]*?(?:(?:property|about)="[^>]*?){2}>([\s\S]*?)</span>#';
		preg_match_all($pattern, $text, $matches);

		for ($i = 0; $i < count($matches[0]); $i++)
		{
			$begin = strpos($text, $matches[0][$i]);
			$end = $begin + strlen($matches[1][$i]) - 1;
			$text = str_replace($matches[0][$i], $matches[1][$i], $text);

			$attrs[$i] = array(
				'about'		=> null,
				'property'	=> null
			);
			preg_match_all('#((?:property|about))="([^"]*?)"#', $matches[0][$i], $attr_matches);
			for ($k = 0; $k < count($attr_matches[0]); $k++)
			{
				$attrs[$i][$attr_matches[1][$k]] = $attr_matches[2][$k];
			}

			if ($attrs[$i]['property'] && array_key_exists($attrs[$i]['property'], $this->_annotations))
			{
				// store the extracted annotation
				$annotation = $this->_annotations[$attrs[$i]['property']];
				$this->_extractedAnnotations[] = new ARS_LocatedAnnotation($annotation, $begin, $end, $attrs[$i]['about']);
			}
		}

		// store the cleaned text
		$this->_cleanedText = $text;

		return $this;
	}

	/**
	 * Returns the cleaned text (text without the annotations).
	 * 
	 * @return  string The cleaned text.
	 */
	public function getCleanedText()
	{
		return $this->_cleanedText;
	}

	/**
	 * Returns all extracted annotations found in the text.
	 * 
	 * @return  array  All extracted annotations.
	 * @see ARS_LocatedAnnotation
	 */
	public function getExtractedAnnotations()
	{
		return $this->_extractedAnnotations;
	}
}

?>