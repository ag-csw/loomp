<?php 

class Validate_Email extends Zend_Validate_Abstract
{
    const NOT_AVALIABLE = 'notAvailable';

    protected $_messageTemplates = array(
        self::NOT_AVALIABLE => 'Address is already taken'
    );

    public function isValid($value, $context = null)
    {
        $value = (string) $value;
        $this->_setValue($value);
		$u = new Model_User();
		$taken = $u->findByUser($value);
		if (!$taken) return true;
        else {
        	$this->_error(self::NOT_AVALIABLE);
        	return false;
       	}
    }
}


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


class RegisterForm extends Zend_Form
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
        
        $email = $this->addElement('text', 'mail', array(
            'filters'    => array('StringTrim'),
            'validators' => array(
               'EmailAddress',        
               array('StringLength', false, array(6, 100)),       
               new Validate_Email(),
            ),
            'required'   => true,
            'label'      => 'Your E-Mail:',
        ));
        
        $orga = $this->addElement('text', 'organisation', array(
            'filters'    => array('StringTrim'),
            'required'   => false,
            'label'      => 'Organisation:',
        ));
    	
        
        /*
        $username = $this->addElement('text', 'username', array(
            'filters'    => array('StringTrim', 'StringToLower'),
            'validators' => array(
                new Validate_Username(),
				'Alnum',
                array('StringLength', false, array(3, 20)),
            ),
            'required'   => true,
            'label'      => 'Desired username:',
            // TODO: check if username is taken!
        ));
		*/
        $password = $this->addElement('password', 'password', array(
            'filters'    => array('StringTrim'),
            'validators' => array(
                array('StringLength', false, array(3, 20)),
            ),
            'required'   => true,
            'label'      => 'Password:',
        ));
        
        $password2 = $this->addElement('password', 'password2', array(
            'filters'    => array('StringTrim'),
            'validators' => array(
                array('StringLength', false, array(3, 20)),
                new Validate_PasswordConfirmation()
            ),
            'required'   => true,
            'label'      => 'Password (2x):',
        ));
        
        
        

        $login = $this->addElement('submit', 'register', array(
            'required' => false,
            'ignore'   => true,
            'label'    => 'Register',
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