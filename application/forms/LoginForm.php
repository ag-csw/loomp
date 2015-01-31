<?php 

class LoginForm extends Zend_Form
{
    public function init()
    {
        $username = $this->addElement('text', 'username', array(
            'filters'    => array('StringTrim', 'StringToLower'),
            'validators' => array(
                array('StringLength', false, array(3, 100)),
            ),
            'required'   => true,
            'label'      => 'Your E-Mail address:',
        ));

        $password = $this->addElement('password', 'password', array(
            'filters'    => array('StringTrim'),
            'validators' => array(
                array('StringLength', false, array(3, 20)),
            ),
            'required'   => true,
            'label'      => 'Your Password:',
        ));

        $login = $this->addElement('submit', 'login', array(
            'required' => false,
            'ignore'   => true,
            'label'    => 'Login',
		'class' => 'button',
        ));
        
        $next = $this->addElement('hidden', 'nextf', array(
            'value' => '',
		'class' => 'hidden',
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