<?php 
// ars/controllers/RecommenderController.php

/**
 * RecommenderController that contains all recommendation actions.
 * 
 * @category   ARS
 * @copyright  Copyright (c) 2009 Patrick Jungermann, Freie Universität Berlin
 * @license    http://www.gnu.org/licenses/lgpl-3.0.html     LGPL, version 3.0
 * @author     Patrick Jungermann
 */ 
class ARS_RecommenderController extends Zend_Controller_Action 
{
	/**
	 * Renders the recommendations for annotations of a given text.<br/>
	 * Only accepts POST requests (otherwise sends 405) and needs
	 * the parameter <em>text</em>.
	 * 
	 * @return  void
	 */
	public function indexAction()
	{
    	// disable layouting etc.
    	$this->_helper->viewRenderer->setNoRender();
    	$this->_helper->layout->disableLayout();
    	$request = $this->getRequest();
		$response = $this->getResponse();

		// Check if we have a POST request
		if (!$request->isPost())
		{
			// send status code "405 Method Not Allowed"
			$response->setHttpResponseCode(405);
			$response->setHeader('Allow', 'POST', true);
			return;
		}

		$response->setHeader('Content-Type', 'application/json', true);

		// result array
		$result = array();

		// get the parameters
		$post = $request->getPost();
		$text = array_key_exists('text', $post) ? $post['text'] : null;
		if ($text)
		{
			// requirements
			/**
			 * @see ARS_AnnotationExtractor
			 */
			require_once dirname(__FILE__) . '/../models/ARS/AnnotationExtractor.php';
			/**
			 * @see ARS_Service
			 */
			require_once dirname(__FILE__) . '/../models/ARS/Service.php';
			/**
			 * @see ARS_Mapper
			 */
			require_once dirname(__FILE__) . '/../models/ARS/Mapper.php';

			// extract all existing annotations from the text
			$extractor = new ARS_AnnotationExtractor($text);
			$text = $extractor->getCleanedText();

			// annoations with position in reference to the cleaned text
			// init with the pre-annotations
			$locatedAnnotations = $extractor->getExtractedAnnotations();

			// perform the further request(s)
			// get the config of this module
			$arsConfig = Zend_Registry::get('arsConfig');

			// configure the service
			$arsService = new ARS_Service($arsConfig->service->url);

			$annotators       = $this->_convertConfigObjectToArray($arsConfig->service->annotators);
			$configParams     = $this->_convertConfigObjectToArray($arsConfig->service->configParams);
			$requiredParams   = $this->_convertConfigObjectToArray($arsConfig->service->requiredParams);
			$moreParams       = $this->_convertConfigObjectToArray($arsConfig->service->moreParams);
			$defaultPhpSerial = $arsConfig->service->defaultPhpParamSerialization;
			foreach ($annotators as $annotator)
			{
				if (ARS_Service::OPEN_CALAIS_KEY == $annotator)
				{
					$arsService->addOpenCalais($configParams[$annotator]);
				}
				elseif (ARS_Service::ZEMANTA_KEY == $annotator)
				{
					$arsService->addZemanta($configParams[$annotator]);
				}
				else
				{
					// add all annotators that are basically not implemented, but hopefully supported by the service
					$arsService->addAnnotator($annotator, $configParams[$annotator], $requiredParams[$annotator]);
				}
			}
			$arsService->setMoreParams($moreParams);

			// send the request and get the result
			$arsServiceResult = $arsService->recommend($text, $defaultPhpSerial);

			// map to the own annotations
			$mapper = ARS_Mapper::getInstance();
			$uimaAnnotations = $arsServiceResult != null ? $arsServiceResult->getAnnotations() : array();

			foreach ($uimaAnnotations as $uimaAnnotation)
			{
				$locatedAnnotations = array_merge(
					$locatedAnnotations,
					$mapper->getLocatedLoompAnnotationsForARS_UIMA_Annotation($uimaAnnotation)
				);
			}

			// handle/identify conflicts etc. and prepare the result
			$result['text'] = $text;
			$result['annotations'] = array();
			$numLocAnnots = count($locatedAnnotations);
			for ($i = 0; $i < $numLocAnnots; $i++)
			{
				$result['annotations'][$i] = array(
					'annotation' => $locatedAnnotations[$i],
					'conflicts'  => array()
				);
			}
			for ($i = 0; $i < $numLocAnnots - 1; $i++)
			{
				for ($k = $i + 1; $k < $numLocAnnots; $k++)
				{
					if ($this->_hasConflict($locatedAnnotations[$i], $locatedAnnotations[$k]))
					{
						// add "references" to the conflicts; used to identify the conflicts within JS
						$result['annotations'][$i]['conflicts'][] = $k;
						$result['annotations'][$k]['conflicts'][] = $i;
					}
				}
			}
		}

		// print the json response
		echo Zend_Json::encode($result);
	}

	/**
	 * Converts a config object of class Zend_Config (or its subclasses) to an array.
	 * 
	 * @param   Zend_Config  $config
	 * 		The config object that has to be converted.
	 * @return  array  The converted config.
	 */
	private function _convertConfigObjectToArray($config)
	{
		$array = array();
		if ($config instanceof Zend_Config)
		{
			foreach ($config as $key => $value)
			{
				$array[$key] = $value instanceof Zend_Config ? $this->_convertConfigObjectToArray($value) : $value;
			}
		}

		return $array;
	}

	/**
	 * Returns if the both annotations are in conflict to each other, because of their annotated text range.
	 * 
	 * @param   ARS_LocatedAnnotation  $annotationA
	 * 		One of the both annotations.
	 * @param   ARS_LocatedAnnotation  $annotationB
	 * 		The other one of the both annotations.
	 * @return  bool  True, if they are in conflict, otherwise false.
	 */
	private function _hasConflict($annotationA, $annotationB)
	{
		$hasConflict = true;
		if ($annotationA->getEnd() < $annotationB->getBegin() || $annotationB->getEnd() < $annotationA->getBegin())
		{
			// both annotations have no characters in common -> no conflict
			$hasConflict = false;
		}

		return $hasConflict;
	}
}

?>