<?php
// ars/models/ARS/UIMA/Annotation.php

/**
 * UIMA Annotation representation.
 *
 * @category   ARS
 * @package    ARS
 * @subpackage UIMA
 * @copyright  Copyright (c) 2009 Patrick Jungermann, Freie Universität Berlin
 * @license    http://www.gnu.org/licenses/lgpl-3.0.html     LGPL, version 3.0
 * @author     Patrick Jungermann
 */
class ARS_UIMA_Annotation
{
	/**
	 * The attributes of the annotation.
	 * 
	 * @var array
	 */
	private $_attributes = array();
	/**
	 * Name of the annotation.
	 * 
	 * @var string
	 */	private $_name;
	/**
	 * The namespace (alias) of the annotation.
	 * 
	 * @var string
	 */
	private $_namespace;
	/**
	 * The URLs of all used namespaces.
	 * 
	 * @var array
	 */
	private $_namespaces = array();

	/**
	 * Constructor
	 * 
	 * @param   string  $name
	 * 		The name of the annotation.
	 * @param   string  $namespaceUrl[optional]
	 * 		The URL of namespace of the annotation. 
	 * @param   string  $namespace[optional]
	 * 		The namespace (alias) of the annotation.
	 * @return  ARS_UIMA_Annotation
	 */
	public function __construct($name, $namespace = null, $namespaceUrl = null)
	{
		$this->_name = $name;
		if (!empty($namespace) && !empty($namespaceUrl))
		{
			$this->_namespace = $namespace;
			$this->_namespaces[$namespace] = $namespaceUrl;
		}

		return $this;
	}

	/**
	 * Adds an attribute for the annoation.
	 * 
	 * @param   string  $name
	 * 		The name of the attribute.
	 * @param   string  $value
	 * 		The value of the attribute.
	 * @param   string  $namespace[optional]
	 * 		The namespace of the attribute.
	 * @param   string  $namespaceUrl[optional]
	 * 		The URL of the namespace.
	 * @return  ARS_UIMA_Annotation
	 */
	public function addAttribute($name, $value, $namespace = null, $namespaceUrl = null)
	{
		$attr = array();
		$fullname = $name;

		if (!empty($namespace))
		{
			$nsUrl = array_key_exists($namespace, $this->_namespaces) ? $this->_namespaces[$namespace] : '';
			if (empty($nsUrl))
			{
				$this->setNamespaceUrl($namespace, $namespaceUrl);
			}

			$fullname = $namespace . ':' . $fullname;
			$attr['namespace'] = $namespace;
		}

		$attr['name'] = $name;
		$attr['value'] = $value;

		$this->_attributes[$fullname] = $attr;

		return $this;
	}

	/**
	 * Adds an array of attributes for the annotation.
	 * 
	 * @see ARS_UIMA_Annotation->addAttribute()
	 * @param   array  $attributes
	 * 		The attributes, where each contains the needed data to add it.
	 * @return  ARS_UIMA_Annotation 
	 */
	public function addAttributes($attributes)
	{
		foreach ((array)$attributes as $attribute)
		{
			if (array_key_exists('name', $attribute) && !empty($attribute['name'])
				&& array_key_exists('value', $attribute) && !empty($attribute['value']))
			{
				$namespace = null;
				$namespaceUrl = null;
				if (array_key_exists('namespace', $attribute) && !empty($attribute['namespace'])
					&& array_key_exists('namespaceUrl', $attribute) && !empty($attribute['namespaceUrl']))
				{
					$namespace = $attribute['namespace'];
					$namespaceUrl = $attribute['namespaceUrl'];
				}

				$this->addAttribute($attribute['name'], $attribute['value'], $namespace, $namespaceUrl);
			}
		}

		return $this;
	}

	/**
	 * Clears the array of attributes of the annotation.
	 * 
	 * @return  ARS_UIMA_Annotation
	 */
	public function clearAttributes()
	{
		$this->_attributes = array();

		return $this;
	}

	/**
	 * Returns the attribute or null.
	 * 
	 * @param   string  $fullname
	 * @return  array  The attribute or null.
	 */
	public function getAttribute($fullname)
	{
		$attr = $this->_attributes[$fullname];

		return empty($attr) && !array_key_exists('value', $attr) ? null : $attr['value'];
	}

	/**
	 * Returns all attributes that are of the given name.
	 * 
	 * @param   string  $name
	 * 		The name for that attributes have to be searched.
	 * @return  array  The found attributes.
	 */
	public function getAttributesByName($name)
	{
		$attrs = array();

		foreach ($this->_attributes as $attr)
		{
			if (array_key_exists('name', $attr) && $name == $attr['name'])
			{
				$attrs[] = $attr;
			}
		}

		return $attrs;
	}

    /**
     * Returns the name of the annotation.
     * 
     * @return  string  The name of the annotation.
     */
    public function getName()
    {
        return $this->_name;
    }
    
    /**
     * Returns the namespace of the annotation.
     * 
     * @return  string  The namespace of the annotation.
     */
    public function getNamespace()
    {
        return $this->_namespace;
    }
    
    /**
     * Returns the URL of the given namespace.
     * 
     * @param   string  $namespace
     * 		The namespace, for which the URL is requested.
     * @return  string  The URL of the namespace.
     */
    public function getNamespaceUrl($namespace)
    {
        return array_key_exists($namespace, $this->_namespaces) ? $this->_namespaces[$namespace] : null;
    }
    
	/**
	 * Returns, if the annotation has an attribute with the given name.
	 * 
	 * @param   string  $fullname
	 * 		The name of the attribute inclusive namespace.
	 * @return  boolean TRUE, if the annotation has an attribute
	 * 		with the given name, otherwise FALSE.
	 */
	public function hasAttribute($fullname)
	{
		return array_key_exists($fullname, $this->_attributes) && !empty($this->_attributes[$fullname]);
	}

	/**
	 * Sets the array of attributes of the annotation to the array of attributes.
	 * 
	 * @param   array  $attributes
	 * 		The attributes, where each contains the needed data to add it.
	 * @return  ARS_UIMA_Annotation
	 */
	public function setAttributes($attributes)
	{
		$this->clearAttributes();
		$this->addAttributes((array)$attributes);

		return $this;
	}

    /**
     * Sets name of the annotation.
     * 
     * @param   string  $name
     * 		The name for the annotation.
     * @return  ARS_UIMA_Annotation
     */
    public function setName($name)
    {
        $this->_name = $name;

		return $this;
    }
    
    /**
     * Sets the namespace of the annotation.
     * 
     * @param   string  $namespace
     * 		The namespace for the annotation.
	 * @param   string  $namespaceUrl
	 * 		The URL of the namespace. 
     * @return  ARS_UIMA_Annotation
     */
    public function setNamespace($namespace, $namespaceUrl)
    {
    	if (!empty($namespace) && !empty($namespaceUrl))
		{
			$this->_namespace = $namespace;
			$this->_namespaces[$namespace] = $namespaceUrl;
		}

		return $this;
    }
    
    /**
     * Sets the URL of the namespace.
     * 
     * @param   string  $namespace
     * 		The namespace, for which the URL has to be set.
     * @param   string  $namespaceUrl
     * 		The URL of the namespace.
     * @return  ARS_UIMA_Annotation
     */
    public function setNamespaceUrl($namespace, $namespaceUrl)
    {
    	if (!empty($namespace) && !empty($namespaceUrl))
		{
			$this->_namespaces[$namespace] = $namespaceUrl;
		}

		return $this;
    }
}

?>