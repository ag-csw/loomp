<?php 

class Validate_PasswordConfirmation extends Zend_Validate_Abstract
{
    const NOT_MATCH = 'notMatch';

    protected $_messageTemplates = array(
        self::NOT_MATCH => 'Password confirmation does not match'
    );

    public function isValid($value, $context = null)
    {
        $value = (string) $value;
        $this->_setValue($value);

        if (is_array($context)) {
            if (isset($context['password'])
                && ($value == $context['password']))
            {
                return true;
            }
        } elseif (is_string($context) && ($value == $context)) {
            return true;
        }

        $this->_error(self::NOT_MATCH);
        return false;
    }
}


class AccountUpdateForm extends Zend_Form
{
    public function init()
    {
    	
    	$firstname = $this->addElement('text', 'firstname', array(
            'filters'    => array('StringTrim'),
            'required'   => true,
            'label'      => 'First name:',
        ));
        
        $lastname = $this->addElement('text', 'lastname', array(
            'filters'    => array('StringTrim'),
            'required'   => true,
            'label'      => 'Last name:',
        ));
        
        /*
        $email = $this->addElement('text', 'mail', array(
            'filters'    => array('StringTrim'),
            'validators' => array(
               'EmailAddress',        
               array('StringLength', false, array(6, 100)),       
               new Validate_Email(),
            ),
            'required'   => true,
            'label'      => 'Your E-Mail:',
        ));*/
        
        $orga = $this->addElement('text', 'organisation', array(
            'filters'    => array('StringTrim'),
            'required'   => false,
            'label'      => 'Organisation:',
        ));
    	
        $password = $this->addElement('password', 'password', array(
            'filters'    => array('StringTrim'),
            'validators' => array(
                array('StringLength', false, array(3, 20)),
            ),
            'label'      => 'New Password:',
        ));
        
        $password2 = $this->addElement('password', 'password2', array(
            'filters'    => array('StringTrim'),
            'validators' => array(
                array('StringLength', false, array(3, 20)),
                new Validate_PasswordConfirmation()
            ),
            'label'      => 'New Password (2x):',
        ));
        

        $login = $this->addElement('submit', 'update', array(
            'required' => false,
            'ignore'   => true,
            'label'    => 'Update',
            'class' => 'button',
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