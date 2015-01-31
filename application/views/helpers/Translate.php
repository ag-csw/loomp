<?php

class Zend_View_Helper_Translate
{
    public function translate($str)
    {
        return Zend_Registry::getInstance()->translate->translate($str);
    }

}

?>
