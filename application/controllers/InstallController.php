<?php

class InstallController extends LoompAction {
	protected $db;
	
	/**
	 * Login form for accessing the install page
	 */	
	public function getInstallLoginForm() {
		require_once APPLICATION_PATH . '/forms/InstallLoginForm.php';
		$form = new InstallLoginForm();
		$form->setAction($this->view->url(array('controller'=>'install','action' => 'loginprocess')));
		return $form;
	}
	
	public function getInstallSetPasswordForm() {
		require_once APPLICATION_PATH . '/forms/InstallSetPasswordForm.php';
		$form = new InstallSetPasswordForm();
		$form->setAction($this->view->url(array('controller'=>'install','action' => 'setpasswordprocess')));
		return $form;
	}
	
	/**
	 * If the install password is empty render a form for entering a new password.
	 * Otherwise render the login form.
	 */
	public function indexAction() {
		$serverConfig = Zend_Registry::getInstance()->configuration->install;
		$this->view->hasInstallPassword = !empty($serverConfig->password);
		if (empty($serverConfig->password)) {
			$this->view->form = $this->getInstallSetPasswordForm();
		} else {
			$this->view->form = $this->getInstallLoginForm();
		}
	}
	
	/**
	 * Set a new password for the install tool
	 */
	public function setpasswordprocessAction() {
		$request = $this->getRequest();
		// Check if we have a POST request
		if (!$request->isPost()) {
			return $this->_helper->redirector('index');
		}

		// Get our form and validate it
		$form = $this->getInstallSetPasswordForm();
		if (!$form->isValid($request->getPost())) {
			// Invalid entries
			$this->view->form = $form;
			return $this->render('index'); // re-render the login form
		}

		$installSession = new Zend_Session_Namespace('Loomp_Install');
		$installSession->loggedIn = true;
		$installSession->installPassword = md5($form->getValue('password'));

		return $this->_helper->redirector('database');
	}
	
	/**
	 * Process the login data
	 */
	public function loginprocessAction() {
		$request = $this->getRequest();
		// Check if we have a POST request
		if (!$request->isPost()) {
			return $this->_helper->redirector('index');
		}

		// Get our form and validate it
		$form = $this->getInstallLoginForm();
		if (!$form->isValid($request->getPost())) {
			// Invalid entries
			$this->view->form = $form;
			return $this->render('index'); // re-render the login form
		}

		$serverConfig = Zend_Registry::getInstance()->configuration->install;
		// TODO use md5 hash 
		if ($serverConfig->password != md5($form->getValue('password'))) {
			$this->addFlashMessage('Invalid password');
			$this->view->form = $form;
			return $this->render('index');
		}
		
		$installSession = new Zend_Session_Namespace('Loomp_Install');
		$installSession->loggedIn = true;
		return $this->_helper->redirector('database');
	}

	/**
	 * Form for configuring the database.
	 */
	public function getDatabaseConfigForm() {
		require_once APPLICATION_PATH . '/forms/DatabaseConfigForm.php';
		$form = new DatabaseConfigForm();
		$form->setAction($this->view->url(array('controller'=>'install','action' => 'dbprocess')));

		// set known values
		$dbConfig = Zend_Registry::getInstance()->configuration->loomp->db;
		$form->getElement('type')->setValue($dbConfig->type);
		$form->getElement('host')->setValue($dbConfig->host);
		$form->getElement('database')->setValue($dbConfig->name);
		$form->getElement('username')->setValue($dbConfig->user);
		$form->getElement('password')->setValue($dbConfig->pass);

		return $form;
	}
	
	/**
	 * View the database config form
	 */
	public function databaseAction() {
		$installSession = new Zend_Session_Namespace('Loomp_Install');
		$this->view->loggedIn = $installSession->loggedIn;
		$installSession = new Zend_Session_Namespace('Loomp_Install');
		$this->view->loggedIn = $installSession->loggedIn;
		if (!$this->view->loggedIn) {
			return $this->_helper->redirector('index');
		}
		// check if the config file is readable
		if (!is_writable(LOCAL_CONFIG_FILE) && !is_writable(dirname(LOCAL_CONFIG_FILE))) {
			$this->getLog()->err("Cannot write local config file");
			$this->addFlashMessage('Cannot write file ' . LOCAL_CONFIG_FILE);
			$this->view->form = $this->getDatabaseConfigForm();
			return $this->render('database');
		} else {
			$this->view->form = $this->getDatabaseConfigForm();
		}
	}
	
	/**
	 * Process the entered database configuration
	 */
	public function dbprocessAction() {
		$installSession = new Zend_Session_Namespace('Loomp_Install');
		$this->view->loggedIn = $installSession->loggedIn;
		if (!$this->view->loggedIn) {
			return $this->_helper->redirector('index');
		}
		$request = $this->getRequest();
		// Check if we have a POST request
		if (!$request->isPost()) {
			return $this->_helper->redirector('database');
		}

		// Get our form and validate it
		$form = $this->getDatabaseConfigForm();
		if (!$form->isValid($request->getPost())) {
			// Invalid entries
			$this->view->form = $form;
			return $this->render('database'); // re-render the login form
		}
		
		$dbConfig = $form->getValues();
		$this->view->dbConfig = $form->getValues();
		try {
			// set system db configuration to form data in order to test connection
			$curConf = Zend_Registry::getInstance()->configuration->loomp->db;
			$curConf->type = $dbConfig['type'];
			$curConf->host = $dbConfig['host'];
			$curConf->name = $dbConfig['database'];
			$curConf->user = $dbConfig['username'];
			$curConf->pass = $dbConfig['password'];
			
			$this->getDbConnection();
			
			// write connection parameter to local config
			$this->resetLocalConfigFile();
			$localConfig = new Zend_Config_Ini(LOCAL_CONFIG_FILE, APPLICATION_ENVIRONMENT, true);
			$localConfig->__set("loomp.db.type", $dbConfig['type']);
			$localConfig->__set("loomp.db.host", $dbConfig['host']);
			$localConfig->__set("loomp.db.name", $dbConfig['database']);
			$localConfig->__set("loomp.db.user", $dbConfig['username']);
			$localConfig->__set("loomp.db.pass", $dbConfig['password']);

			$request = new Zend_Controller_Request_Http();
			$localConfig->__set("server.path", $request->getBaseUrl());
			$localConfig->__set("server.host", $_SERVER['HTTP_HOST']);
			$localConfig->__set("rap.path", $request->getBaseUrl() . "/data");
			$localConfig->__set("loomp.base", (isset($_SERVER['HTTPS']) ? 'https://' : 'http://') . $_SERVER['HTTP_HOST'] .  $request->getBaseUrl());
			
			$localConfig->__set("install.password", $installSession->installPassword);
			
			$writer = new Zend_Config_Writer_Ini();
			$writer->write(LOCAL_CONFIG_FILE, $localConfig, true);
			$this->getLog()->debug("Local config has been written to disk");

			$config = Zend_Registry::getInstance()->configuration;
			$config->merge($localConfig);
		} catch (Exception $e) {
			$this->addFlashMessage("Local configuration file could not be written.");
			$this->getLog()->err("Error while writing local configuration: " . $e->getMessage());
			return $this->_helper->redirector('database');
		}
		
		return $this->_helper->redirector('update');
	}
	
	//
	// CREATE AND UPDATE DATABASE TABLES
	public function updateAction() {
		$installSession = new Zend_Session_Namespace('Loomp_Install');
		$this->view->loggedIn = $installSession->loggedIn;
		if (!$this->view->loggedIn) {
			return $this->_helper->redirector('index');
		}
		// form for updating the database
		$dbForm = new Zend_Form();
		$dbForm->addElement('submit', 'login', array(
            'required' => false,
            'ignore'   => true,
            'label'    => 'Create database tables',
			'class'    => 'button'));
		$dbForm->setAction($this->view->url(array('controller'=>'install','action' => 'updateprocess')));
		$this->view->dbForm = $dbForm;

		// form for creating default model
		$modelForm = new Zend_Form();
		$modelForm->addElement('submit', 'login', array(
            'required' => false,
            'ignore'   => true,
            'label'    => 'Create default model',
			'class'    => 'button'));
		$modelForm->setAction($this->view->url(array('controller'=>'install','action' => 'updatemodelprocess')));
		$this->view->modelForm = $modelForm;

		$dbVersion = $this->getDbVersion();
		$this->view->dbVersion = $dbVersion;
		$sqlFiles = $this->getSqlFileList($dbVersion);
		$this->view->sqlFiles = $sqlFiles;
		if (empty($sqlFiles)) {
			// we have an up-to-data database, check for the correct model URI
			$this->view->hasModelUri = $this->checkForDfltModel();
		}
	}
	
	/**
	 * Create tables and default values.
	 */
	public function updateprocessAction() {
		$sqlFiles = $this->getSqlFileList($this->getDbVersion());
		if (!$this->executeSqlFiles($sqlFiles)) {
			$this->getLog()->err("Error occurred while updating database.");
			$this->_helper->redirector('update');
		}		
		if (!$this->insertDefaultValues()) {
			$this->getLog()->err("Error occurred while inserting default values.");
			$this->_helper->redirector('update');
		}
		$this->getLog()->debug("Database has been updated successfully");
		return $this->_helper->redirector('finished');
	}
	
	/**
	 * Create the defaul model only.
	 */
	public function updatemodelprocessAction() {
		if (!$this->insertDefaultModel()) {
			$this->addFlashMessage("Unable to create default model.");
			$this->getLog()->err("Error occurred while inserting default values.");
			$this->_helper->redirector('update');
		}
		return $this->_helper->redirector('finished');
	}
	
	public function finishedAction() {
		$installSession = new Zend_Session_Namespace('Loomp_Install');
		$this->view->loggedIn = $installSession->loggedIn;
		if ($this->view->loggedIn) {
			$this->view->dbVersion = $this->getDbVersion();
		}
	}
	
	/**	
	 * @return [float] the current version of the database, 0.0 if no tables exists, or a
	 * 		negative value if the version is unknown, e.g., no value for sys_parameter db_version	
	 * @access protected
	 */
	protected function getDbVersion() {
		$db = $this->getDbConnection();
		try {
			$result = &$db->execute("SELECT value FROM sys_parameter WHERE name = 'db_version'");
		} catch (exception $e) {
			return 0.0;
		}
		
		if (!$result) {
			// table sys_parameter does not exist
			return 0.0;
		} else if ($result->rowCount() == 0) {
			// table sys_parameter exists but is empty => we don't know the version 
			return -1.0;
		} else {
			return $result->fields[0];
		}
	}
	
	/**	
	 * @return direct connection to the underlying database
	 * @access protected	
	 */
	protected function getDbConnection() {
		$dbConfig = Zend_Registry::getInstance()->configuration->loomp->db;
		$dbStore = ModelFactory::getDbStore($dbConfig->type, $dbConfig->host,
					$dbConfig->name, $dbConfig->user, $dbConfig->pass);
		return $dbStore->getDbConnection();
	}
	
	/**
	 * The function ensures that at least an empty local config file exists. If it does not exists it will be created.
	 */
	protected function resetLocalConfigFile() {
		if (file_exists(LOCAL_CONFIG_FILE)) {
			unlink(LOCAL_CONFIG_FILE);
		}
		$fh = fopen(LOCAL_CONFIG_FILE, "w");
		fwrite($fh, '[' .  APPLICATION_ENVIRONMENT . ']');
		$this->getLog()->debug("Reseted local config file.");
	}

	/**	
	 * @param decimal
	 * 				dbVersion the current version of the database
	 * @return [array of files] a list of files that has to be executed to update the database to current version
	 * @access protected
	 */
	protected function getSqlFileList($dbVersion) {
		$files = array();
		if ($dbVersion == 0.0) {
			$files[] = 'createTables.sql';
		}
		
		// read the names of all sql files from the directory that have a version largen than $dbVersion
		if ($handle = opendir($this->getSqlFileDir())) {
		    while (false !== ($file = readdir($handle))) {
				if (preg_match("/.*_(?<version>\d+.\d+).sql/i", $file, $match) 
						&& $match['version'] > $dbVersion) {
					$files[] = $file;
				}
		    }
	    	closedir($handle);
		} else {
			$this->getLog()->err('Error while reading list of SQL files from directory ' . $this->getSqlFileDir());
		}
		
		return $files;
	}
	
	/**
	 * Execute the statements contained in the given file.
	 * @param array file
	 * 			an sql file
	 * @access protected
	 */
	 protected function executeSqlFiles(&$files) {
		$this->getLog()->debug("List of SQL files: ". implode(", ", $files));

	 	if (!is_array($files)) {
	 		$this-getLog()->err('executeSqlFile(): parameter has to be an array');
	 		return;
	 	}
	 	
		$this->getLog()->debug("Get database connection");
	 	$db = $this->getDbConnection();
	 	$dir = $this->getSqlFileDir();
	 	$success = false;
	 	
	 	foreach($files as $f) {
	 		$absFile = $dir . '/' .$f;
			$this->getLog()->debug("Processing file " . $absFile);
			$lines = file($absFile);
			$sql = "";
			foreach ($lines as $line) {
				if(trim($line) == "" || strpos($line, "--") !== false)
					continue;
				$sql .= $line;
				if (strrpos($sql, ";") !== false) {
					// trim and remove the ; at the end
					$sql = substr(trim($sql), 0, -1);
					$this->getLog()->debug("Executing ... " . str_replace("\n", "", $sql));
					if ($db->execute($sql) === false) {
						$this->getLog()->debug("Failure: " . $db->ErrorMsg());
						$success = false;
						break;
					}
					$this->getLog()->debug("Success.");
					$success = true;
					$sql = "";
				}
			} 
	 	}
	 	return $success;
	}
	
	/**
	 * @return [boolean] true iff a model exists
	 * @access protected
	 */
	protected function checkForDfltModel() {
		$db = $this->getDbConnection();
		$this->getLog()->debug("Looking up the model URI: " . LOOMP_MODEL_URI);
		$result = &$db->GetOne("SELECT modelURI FROM models WHERE modelURI = ?", array(LOOMP_MODEL_URI));
		if ($result === false || !$result) {
			$this->getLog()->debug("* Not found.");
			return false;
		} else {
			$this->getLog()->debug("* Found.");
			return true;
		}
	} 
	
	/**
	 * Create default model and an admin user using the server configuration.
	 * @access protected
	 */
	protected function insertDefaultValues() {
		$success = false;
		$success &= $this->insertDefaultModel();
		$success &= $this->insertDefaultAdmin();
		return $success;
	}

	/**
	 * Create default an admin user using the server configuration.
	 * @access protected
	 */
	protected function insertDefaultAdmin() {
		$adminUser = LOOMP_USER_URI_NS . "admin";
		$adminPass = "97424609272684e6f0f1aaaef42d3724";
		$adminEmail = "admin@loomp.org";
		
		$this->getLog()->debug("Get database connection");
	 	$db = $this->getDbConnection();
		$values = array($adminEmail, date('Y-m-d H:i:s'), $adminPass, '', 1, 10, $adminUser, 'Super', 'User', 'NULL');
		
		$sql =	"INSERT INTO `users` (`email`, `registered`, `password`, `activation`, `active`, `userlevel`, `uri`, `firstname`, `lastname`, `organisation`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		$this->getLog()->debug("Creating admin user: $sql");
		if ($db->execute($sql, $values) === false) {
			$this->getLog()->debug("* Failure: Unable to create the default admin user: " . $db->ErrorMsg());
			return false;
		}
		$this->getLog()->debug("* Success.");
		
		return true;
	 }
	 
	/**
	 * Create default model using the server configuration.
	 * @access protected
	 */
	protected function insertDefaultModel() {
		$this->getLog()->debug("Get database connection");
	 	$db = $this->getDbConnection();
		
		$id =& $db->GetOne('SELECT MAX(modelID) FROM models');
		$values = array(++$id, LOOMP_MODEL_URI, '');
		
		$sql =	"INSERT INTO `models` (`modelID`, `modelURI`, `baseURI`) VALUES (?, ?, ?)";
		$this->getLog()->debug("Creating default model: $sql");
		$this->getLog()->debug("* Values: " . print_r($values, true));
		if ($db->execute($sql, $values) === false) {
			$this->getLog()->debug("* Failure: Unable to create database record: " . $db->ErrorMsg());
			return false;
		}
		$this->getLog()->debug("* Success.");
		return true;
	 }

	 /**	
	  * Depending on the type of database return the absolute path to the directory
	  * containing the corresponding SQL files.
	  * @return String path of the directory	
	  * @access protected
	  */
	 protected function getSqlFileDir() {
		return APPLICATION_PATH . '/database/' .
				strtolower(Zend_Registry::getInstance()->configuration->loomp->db->type);
	 }
}

?>
