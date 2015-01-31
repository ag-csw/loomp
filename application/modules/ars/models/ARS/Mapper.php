<?php
// ars/models/ARS/Mapper.php

/**
 * @see Zend_Registry
 */
require_once 'Zend/Registry.php';
/**
 * @see ARS_UIMA_Annotation
 */
require_once dirname(__FILE__) . '/UIMA/Annotation.php';
/**
 * @see ARS_LocatedAnnotation
 */
require_once dirname(__FILE__) . '/LocatedAnnotation.php';
// Loomp backend API functions
require_once 'loomp-backend/api.php';

/**
 * Mapper as singleton that maps the ARS_UIMA_Annotations
 * to ARS_LocatedAnnotations.
 * 
 * @uses       ARC2
 * @uses       ARC2_RDFParser
 * @uses       ARS_LocatedAnnotation
 * @uses       ARS_UIMA_Annotation
 * @uses       Zend_Registry
 * @category   ARS
 * @package    ARS
 * @copyright  Copyright (c) 2009 Patrick Jungermann, Freie Universität Berlin
 * @license    http://www.gnu.org/licenses/lgpl-3.0.html     LGPL, version 3.0
 * @author     Patrick Jungermann
 */
class ARS_Mapper
{
	/**
	 * The URI for the <code>owl:sameAs</code> statement.
	 * 
	 * @type string
	 */
	const SAME_AS = 'http://www.w3.org/2002/07/owl#sameAs';
	/**
	 * The annotations store.
	 * 
	 * @var array
	 */
	private $_annotations = array();
	/**
	 * The instance of the ARS_Mapper.
	 * 
	 * @var ARS_Mapper
	 */
	private static $_instance = null;
	/**
	 * The mappings store.
	 * 
	 * @var array
	 */
	private $_mappings = array();
	/**
	 * The store for the seeAlso mappings.
	 * 
	 * @var array
	 */
	private $_seeAlso_mappings = array();

	/**
	 * Constructor
	 * 
	 * @return  ARS_Mapper
	 */
	private function __construct()
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

		// get the registry
		$arsConfig = Zend_Registry::get('arsConfig');

		// include the ARC2 class file
		$arc2FilePath = $arsConfig->mapper->arc2->filePath;
		if ($arsConfig->mapper->arc2->relativeToMapper)
		{
			if ('/' != $arc2FilePath[0])
			{
				$arc2FilePath = '/' . $arc2FilePath;
			}
			$arc2FilePath = dirname(__FILE__) . $arc2FilePath;
		}
		/**
		 * @see ARC2
		 */
		require_once $arc2FilePath;

		// get the file path of the mappings file
		$mappingsFilePath = $arsConfig->mapper->mappingsRDF->filePath;
		if($arsConfig->mapper->mappingsRDF->relativeToMapper)
		{
			if ('/' != $mappingsFilePath[0])
			{
				$mappingsFilePath = '/' . $mappingsFilePath;
			}
			$mappingsFilePath = dirname(__FILE__) . $mappingsFilePath;
		}

		// get the file path of the seeAlso mappings file
		$seeAlsoMappingsFilePath = $arsConfig->mapper->seeAlsoMappingsRDF->filePath;
		if ($arsConfig->mapper->seeAlsoMappingsRDF->relativeToMapper)
		{
			if ('/' != $seeAlsoMappingsFilePath[0])
			{
				$seeAlsoMappingsFilePath = '/' . $seeAlsoMappingsFilePath;
			}
			$seeAlsoMappingsFilePath = dirname(__FILE__) . $seeAlsoMappingsFilePath;
		}

		// get RDF parser
		$parser = ARC2::getRDFParser();

		// handle the simple index for the mappings file
		$parser->parse($mappingsFilePath);
		$simpleIndex = $parser->getSimpleIndex();
		$this->_addAllMappings($simpleIndex);

		// handle the simple index for the seeAlso mappings file
		$parser->parse($seeAlsoMappingsFilePath);
		$simpleIndex = $parser->getSimpleIndex();
		$this->_addAllSeeAlsoMappings($simpleIndex);

		return $this;
	}

	/**
	 * Returns the string representation for the object.
	 * 
	 * @return  string  The string representation for the object.
	 */
	public function __toString()
	{
		return __CLASS__;
	}

	/**
	 * Adds all annotations from the given array of
	 * vocabularies to the internal store.
	 * 
	 * @param   array  $vocabularies
	 * 		The array of vocabularies.
	 * @return  ARS_Mapper
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

		// add base resource rdfs:Resource
		$resourceUri = RDF_SCHEMA_URI . 'Resource';
		$resAnnot = new Annotation(
			$resourceUri,
			$resourceUri,
			'General resource',
			'', '', ''
		);
		$this->_annotations[$resAnnot->getUri()] = $resAnnot;

		return $this;
	}

	/**
	 * Adds all mappings from the given simple index to the internal store.
	 *  
	 * @param   SimpleIndex  $simpleIndex
	 * 		The simple index, created from a mappings file.
	 * @return  ARS_Mapper
	 */
	private function _addAllMappings($simpleIndex)
	{
		foreach ($simpleIndex as $subject => $predicates)
		{
			foreach ($predicates as $predicate => $objects)
			{
				if ($predicate == self::SAME_AS)
				{
					foreach ($objects as $object)
					{
						if (preg_match('%(.+)/([^/]+)%', $object, $matches))
						{
							$splitted = split('#', $matches[2]);

							$namespace = $matches[1];
							$name = $splitted[0];
							$attribute = count($splitted) > 1 ? $splitted[1] : null;

							$this->_mappings[$namespace . '/' . $name][$attribute][] = $subject;
						}
					}
				}
			}
		}

		// remove duplicate entries
		foreach ($this->_mappings as &$entry)
		{
			foreach ($entry as &$attribute)
			{
				$attribute = array_unique($attribute);
			}
		}

		return $this;
	}

	/**
	 * Adds all mappings for the properties of the types to seeAlso.
	 * 
	 * @param   SimpleIndex  $simpleIndex
	 * 		The simple index, created from a seeAlso mappings file.
	 * @return  ARS_Mapper
	 */
	private function _addAllSeeAlsoMappings($simpleIndex)
	{
		foreach ($simpleIndex as $subject => $predicates)
		{
			foreach ($predicates as $predicate => $objects)
			{
				if ($predicate == self::SAME_AS)
				{
					foreach ($objects as $object)
					{
						if (preg_match('%(.+)/([^/]+)%', $object, $matches))
						{
							$splitted = split('#', $matches[2]);

							$namespace = $matches[1];
							$name = $splitted[0];
							$attribute = $splitted[1];

							$this->_seeAlso_mappings[$namespace . '/' . $name] = $attribute;
						}
					}
				}
			}
		}

		return $this;
	}

	/**
	 * Returns the instance of the mapper.
	 * 
	 * @return  ARS_Mapper  The instance.
	 */
	public static function getInstance()
	{
		if (null == self::$_instance)
		{
			self::$_instance = new ARS_Mapper();
		}

		return self::$_instance;
	}

	/**
	 * Returns all located Loomp annotations (ARS_LocatedAnnotation)
	 * for a given ARS_UIMA_Annotation.
	 * 
	 * @param   ARS_UIMA_Annotation  $uimaAnnotation
	 * 		The UIMA annotation.
	 * @return  array  The located loomp annotations (ARS_LocatedAnnotation).
	 */
	public function getLocatedLoompAnnotationsForARS_UIMA_Annotation($uimaAnnotation)
	{
		$loompAnnotations = array();
		$begin = $uimaAnnotation->hasAttribute('begin') ? $uimaAnnotation->getAttribute('begin') : null;
		$end = $uimaAnnotation->hasAttribute('end') ? $uimaAnnotation->getAttribute('end') : null;

		$namespaceUrl = $uimaAnnotation->getNamespaceUrl($uimaAnnotation->getNamespace());
		$full = $namespaceUrl ? $namespaceUrl . '/' : '';
		$full .= $uimaAnnotation->getName();
		
		$mappedAttributes = array_key_exists($full, $this->_mappings) ? $this->_mappings[$full] : null;
		if (!empty($mappedAttributes))
		{
			foreach ($mappedAttributes as $attribute => $uris)
			{
				if (empty($attribute) || $uimaAnnotation->hasAttribute($attribute))
				{
					foreach ($uris as $uri)
					{
						if (array_key_exists($uri, $this->_annotations))
						{
							$seeAlsoProp = array_key_exists($full, $this->_seeAlso_mappings) ? $this->_seeAlso_mappings[$full] : null;
							$seeAlso = $seeAlsoProp && $uimaAnnotation->hasAttribute($seeAlsoProp) ? $uimaAnnotation->getAttribute($seeAlsoProp) : null;
							$loompAnnotations[] = new ARS_LocatedAnnotation($this->_annotations[$uri], $begin, $end, null, $seeAlso);
						}
					}
				}
			}
		}

		return array_values(array_unique($loompAnnotations));
	}
}

?>