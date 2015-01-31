<?php 

class FeedbackForm extends Zend_Form
{
    public function init()
    {
        $email = $this->addElement('text', 'mail', array(
            'filters'    => array('StringTrim'),
            'validators' => array(
               'EmailAddress',        
               array('StringLength', false, array(6, 100))
            ),
            'required'   => true,
            'label'      => 'Your E-Mail:',
        ));
        
         $email = $this->addElement('textarea', 'feedback', array(
            'filters'    => array('StringTrim'),
            'required'   => true,
            'label'      => 'Your Feedback:',
        ));
        

        $login = $this->addElement('submit', 'submit', array(
            'required' => false,
            'ignore'   => true,
            'label'    => 'Submit Feedback',
		'class' => 'button',
        ));
       
        $this->setDecorators(array(
            'FormElements',
            array('HtmlTag', array('tag' => 'dl', 'class' => 'zend_form')),
            array('Description', array('placement' => 'prepend')),
            'Form'
        ));
    }
}