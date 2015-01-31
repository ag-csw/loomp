<?php 

class DatabaseConfigForm extends Zend_Form {
    public function init() {
		$driver = $this->addElement('select', 'type', array(
            'filters'      => array('StringTrim'),
            'required'     => true,
            'multiOptions' => array('MYSQL' => 'MYSQL'),
            'label'        => 'DB Type:',
		));
    	
        $host = $this->addElement('text', 'host', array(
            'filters'    => array('StringTrim'),
            'validators' => array(
                array('StringLength', false, array(3, 20)),
            ),
            'required'   => true,
            'label'      => 'Host:',
        ));

        $database = $this->addElement('text', 'database', array(
            'filters'    => array('StringTrim'),
            'validators' => array(
                array('StringLength', false, array(2, 20)),
            ),
            'required'   => true,
            'label'      => 'Database:',
        ));

        $username = $this->addElement('text', 'username', array(
            'filters'    => array('StringTrim'),
            'validators' => array(
                array('StringLength', false, array(2, 20)),
            ),
            'required'   => true,
            'label'      => 'Username:',
        ));

        $password = $this->addElement('text', 'password', array(
            'filters'    => array('StringTrim'),
            'validators' => array(
                array('StringLength', false, array(3, 20)),
            ),
            'required'   => true,
            'label'      => 'Password:',
        ));

        $login = $this->addElement('submit', 'login', array(
            'required' => false,
            'ignore'   => true,
            'label'    => 'Save',
			'class'    => 'button',
        ));
        
        // We want to display a 'failed authentication' message if necessary;
        // we'll do that with the form 'description', so we need to add that
        // decorator.
        $this->setDecorators(array(
            'FormElements',
            array('HtmlTag', array('tag' => 'dl', 'class' => 'zend_form')),
            array('Description', array('placement' => 'prepend')),
            'Form'
        ));
    }
}