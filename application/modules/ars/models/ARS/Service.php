<?php
// ars/models/ARS/Service.php

/**   
 * @see Zend_Service_Abstract
 */
require_once 'Zend/Service/Abstract.php';
/**
 * @see ARS_Service_Result
 */
require_once dirname(__FILE__) . '/Service/Result.php';


/**
 * Annotation Recommender Service (ARS) implementation.
 * 
 * @uses       ARS_Service_Result
 * @uses       Zend_Service_Abstract
 * @category   ARS
 * @package    ARS
 * @copyright  Copyright (c) 2009 Patrick Jungermann, Freie Universität Berlin
 * @license    http://www.gnu.org/licenses/lgpl-3.0.html     LGPL, version 3.0
 * @author     Patrick Jungermann
 */
class ARS_Service extends Zend_Service_Abstract
{
	/**
	 * The Web Service's URL.
	 * 
	 * @var string
	 */
	private $_serverUrl;

	/**
	 * The name / value of the "annotations" parameter.
	 * 
	 * @staticvar string
	 */
	private static $_PARAM_NAME_ANNOTATORS = 'annotators';
	/**
	 * The annotator services used within the following requests.
	 * 
	 * @var array
	 */
	private $_annotators = array();
	/**
	 * The configuration paramters for each annotator service
	 * used within the following requests.
	 * 
	 * @var array
	 */
	private $_configParams = array();
	/**
	 * Additonal parameters for the request, needed by the Web Service.
	 * 
	 * @var array
	 */
	private $_moreParams = array();

	/**
	 * The key / name of the OpenCalais annotator service.
	 * 
	 * @type string
	 */
	const OPEN_CALAIS_KEY = 'openCalais';
	/**
	 * The names of the parameters that are required for the OpenCalais annotator service.
	 * 
	 * @staticvar array
	 */
	private static $_OPEN_CALAIS_REQUIRED_PARAMS = array('licenseID');

	/**
	 * The key / name of the Zemanta annotator service.
	 * 
	 * @type string
	 */
	const ZEMANTA_KEY = 'zemanta';
	/**
	 * The names of the parameters that are required for the Zemanta annotator service.
	 * 
	 * @staticvar array
	 */
	private static $_ZEMANTA_REQUIRED_PARAMS = array('apiKey');

    /**
     * Constructor
     *
     * @param   string  $serverUrl
     * 		The Web Service's URL
     * @return  ARS_Service
     */
    public function __construct($serverUrl)
    {
        $this->setServerUrl($serverUrl);

		return $this;
    }

	/**
	 * Adds the annotator with the specified name and the
	 * given configuration to the used ones.<br/>
	 * If it was already one of the used ones, the old
	 * configuration will be overridden.
	 * 
	 * @param   string  $name
	 * 		The name of the annotator.
	 * @param   array   $configParams
	 * 		The configuration parameter for this annotator.
	 * @param   array   $requiredParams
	 * 		The names of the required configuration parameters.
	 * @return  ARS_Service
	 */
	private function _addAnnotator($name, $configParams, $requiredParams)
	{
		// validate the config params
		foreach ((array)$requiredParams as $requiredParam)
		{
			if (!$configParams[$requiredParam])
			{
				throw new Zend_Service_Exception("The configuration parameter [$requiredParam] is mandatory.");
			}
		}

		// set the config params
		$this->_configParams[$name] = array();
		foreach ((array)$configParams as $key => $value)
		{
			// correct the key, if prefix was already set
			if (0 === stripos($key, $name . '.'))
			{
				$key = substr($key, 0, strlen($name) + 1);
			}
			$this->_configParams[$name][$key] = $value;
		}

		// add the annotator
		if (!in_array($name, $this->_annotators))
		{
			$this->_annotators[] = $name;
		}

		return $this;
	}

	/**
	 * Prepares and returns the raw post data.
	 * 
	 * @param   array   $data
	 * 		The data that has to be prepared.
	 * @param   string  $separator[optional]
	 * 		The separator that has to be used for the key-value pairs.
	 * @param   string  $key[optional]
	 * 		The parent key.
	 * @return  string  The raw post data.
	 */
	private function _prepareRawPostData($data, $separator = '', $key = '')
	{
		$ret = array();

		foreach((array)$data as $k => $v) {
			if(!empty($key))
			{
				$k = is_int($k) ? $key : $key . '.' . $k;
			}

			if(is_array($v) || is_object($v))
			{
				array_push($ret, $this->_prepareRawPostData($v, $separator, $k));
			}
			else
			{
				array_push($ret, urlencode($k) . '=' . urlencode($v));
			}
		}

		if(empty($separator))
		{
			$separator = ini_get("arg_separator.output");
		}

		return implode($separator, $ret);
	}

	/**
	 * Removes the annotator with the specified name from the used ones,
	 * inclusive the related configuration parameter.
	 * 
	 * @param   string  $name
	 * 		The name of the annotator.
	 * @return  ARS_Service 
	 */
	private function _removeAnnotator($name)
	{
		$key = array_search($name, $this->_annotators);
		if (null != $key){
			$this->_annotators = array_splice($this->_annotators, $key, 1);
		}

		unset($this->_configParams[$name]);

		return $this;
	}

	/**
	 * Returns, if the annotator is currently used or not.
	 * 
	 * @return  boolean  TRUE, if it is currently used, otherwise FALSE. 
	 */
	private function _usesAnnotator($name)
	{
		return in_array($name, $this->_annotators);
	}

	/**
	 * Adds the OpenCalais annotator with the given configuration to the used ones.<br/>
	 * If it was already one of the used ones, the old configuration will be overridden.
	 * 
	 * @param   array  $configParams
	 * 		The configuration parameters for the OpenCalais annotator.
	 * @return  ARS_Service
	 */
	public function addOpenCalais($configParams)
	{
		$this->_addAnnotator(self::OPEN_CALAIS_KEY, (array)$configParams, self::$_OPEN_CALAIS_REQUIRED_PARAMS);

		return $this;
	}

	/**
	 * Adds the Zemanta annotator with the given configuration to the used ones.<br/>
	 * If it was already one of the used ones, the old configuration will be overridden.
	 * 
	 * @param   array  $configParams
	 * 		The configuration parameters for the Zemanta annotator.
	 * @return  ARS_Service
	 */
	public function addZemanta($configParams)
	{
		$this->_addAnnotator(self::ZEMANTA_KEY, (array)$configParams, self::$_ZEMANTA_REQUIRED_PARAMS);

		return $this;
	}

	/**
	 * Returns the Web Service's URL.
	 * 
	 * @return  string  The Web Service's URL. 
	 */
	public function getServerUrl()
	{
	 	return $this->_serverUrl;
	}

	/**
	 * Sends the request to the server that recommends
	 * annotations for the given text.
	 * 
	 * @param   string   $text
	 * 		The text that has to be analyzed.
	 * @param   boolean  $defaultPhpParamSerialization[optional]
	 * 		If true, the default serialization of parameters normally
	 * 		used in PHP will be used for this request, otherwise the
	 * 		Java/Grails specific implementation will be used (default behavior).
	 * @param   string   $charset[optional]
	 * 		The charset of the text. Defaults to UTF-8.
	 * @return  ARS_Service_Result  The result object.
	 */
	public function recommend($text, $defaultPhpParamSerialization = false, $charset = 'UTF-8')
	{
        $client = self::getHttpClient();
        $client->setUri($this->_serverUrl);

        $client->setHeaders(array(
            Zend_Http_Client::CONTENT_TYPE => Zend_Http_Client::ENC_URLENCODED . '; charset=' . $charset
        ));
        $client->setMethod(Zend_Http_Client::POST);

		// set the data
		$params = array_merge(
			$this->_moreParams,
			$this->_configParams,
			array(self::$_PARAM_NAME_ANNOTATORS => $this->_annotators)
		);
		$params['text'] = $text;

		if ($defaultPhpParamSerialization) {
			$client->setParameterPost($params);
		}
		else {
			$rawPostData = $this->_prepareRawPostData($params, '&');
	        $client->setRawData($rawPostData, Zend_Http_Client::ENC_URLENCODED);
		}

        $response = $client->request();
		$result = 200 != $response->getStatus() ? null : new ARS_Service_Result($response->getBody());

		return $result;
	}

	/**
	 * Removes the OpenCalais annotator from the used ones,
	 * inclusive the related configuration parameter.
	 * 
	 * @return  ARS_Service 
	 */
	public function removeOpenCalais()
	{
		$this->_removeAnnotator(self::OPEN_CALAIS_KEY);

		return $this;
	}

	/**
	 * Removes the Zemanta annotator from the used ones,
	 * inclusive the related configuration parameter.
	 * 
	 * @return  ARS_Service 
	 */
	public function removeZemanta()
	{
		$this->_removeAnnotator(self::ZEMANTA_KEY);

		return $this;
	}

	/**
	 * Sets the additional parameters for the service.
	 * 
	 * @param   array  $moreParams
	 * 		The additional parameters with their values.
	 * @return  ARS_Service
	 */
	public function setMoreParams($moreParams)
	{
		$this->_moreParams = $moreParams;

		return $this;
	}

	/**
	 * Sets the Web Service's URL.
	 * 
	 * @param   string  $serverUrl
	 * 		The new URL.
	 * @return  ARS_Service 
	 */
	public function setServerUrl($serverUrl)
	{
		$this->_serverUrl = $serverUrl;

		return $this;
	}

	/**
	 * Returns, if the OpenCalais annotator is currently used or not.
	 * 
	 * @return  boolean  TRUE, if Zemanta is currently used, otherwise FALSE. 
	 */
	public function usesOpenCalais()
	{
		return $this->_usesAnnotator(self::OPEN_CALAIS_KEY);
	}

	/**
	 * Returns, if the Zemanta annotator is currently used or not.
	 * 
	 * @return  boolean  TRUE, if Zemanta is currently used, otherwise FALSE. 
	 */
	public function usesZemanta()
	{
		return $this->_usesAnnotator(self::ZEMANTA_KEY);
	}
}

?>