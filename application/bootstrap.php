<?php
// application/bootstrap.php
// 
// Step 1: APPLICATION CONSTANTS - Set the constants to use in this application.
// These constants are accessible throughout the application, even in ini 
// files. We optionally set APPLICATION_PATH here in case our entry point 
// isn't index.php (e.g., if required from our test suite or a script).
defined('APPLICATION_PATH')
    or define('APPLICATION_PATH', dirname(__FILE__));

defined('APPLICATION_ENVIRONMENT')
    or define('APPLICATION_ENVIRONMENT', 'development');
    
defined('CONFIG_FILE')
    or define('CONFIG_FILE', APPLICATION_PATH . '/config/app.ini');
defined('LOCAL_CONFIG_FILE')
    or define('LOCAL_CONFIG_FILE', APPLICATION_PATH . '/config/local_app.ini');
    

// Step 2: FRONT CONTROLLER - Get the front controller.
// The Zend_Front_Controller class implements the Singleton pattern, which is a
// design pattern used to ensure there is only one instance of
// Zend_Front_Controller created on each request.
$frontController = Zend_Controller_Front::getInstance();

// Step 3: CONTROLLER DIRECTORY SETUP - Point the front controller to your action
// controller directory.
$frontController->addModuleDirectory(APPLICATION_PATH . '/modules');
$frontController->addControllerDirectory(APPLICATION_PATH . '/controllers', 'default');

// Step 4: APPLICATION ENVIRONMENT - Set the current environment.
// Set a variable in the front controller indicating the current environment --
// commonly one of development, staging, testing, production, but wholly
// dependent on your organization's and/or site's needs.
$frontController->setParam('env', APPLICATION_ENVIRONMENT);

// LAYOUT SETUP - Setup the layout component
// The Zend_Layout component implements a composite (or two-step-view) pattern
// With this call we are telling the component where to find the layouts scripts.
Zend_Layout::startMvc(APPLICATION_PATH . '/layouts/scripts');

// VIEW SETUP - Initialize properties of the view object
// The Zend_View component is used for rendering views. Here, we grab a "global" 
// view instance from the layout object, and specify the doctype we wish to 
// use. In this case, XHTML1 Strict.
$view = Zend_Layout::getMvcInstance()->getView();
$view->doctype('XHTML1_STRICT');

// add login redirector
require_once APPLICATION_PATH . '/auth/AuthPlugin.php';
$frontController->registerPlugin(new AuthPlugin());

//
// CONFIGURATION - Setup the configuration object
// The Zend_Config_Ini component will parse the ini file, and resolve all of
// the values for the given section.  Here we will be using the section name
// that corresponds to the APP's Environment
$configuration = new Zend_Config_Ini(CONFIG_FILE, APPLICATION_ENVIRONMENT, true);

$arsConfig = new Zend_Config_Ini(
	APPLICATION_PATH . '/modules/ars/config/ars.ini',
	APPLICATION_ENVIRONMENT
);

if (file_exists(LOCAL_CONFIG_FILE) && is_readable(LOCAL_CONFIG_FILE)) {
	$localConfig = new Zend_Config_Ini(LOCAL_CONFIG_FILE, APPLICATION_ENVIRONMENT);
	$configuration->merge($localConfig);
}


// LOGGER
$logfile = APPLICATION_PATH."/".$configuration->log->path;
if (is_file($logfile) && !is_writeable($logfile)) die("Unable to write to log file " . $logfile);

$writer = new Zend_Log_Writer_Stream($logfile);
$writer->setFormatter(new Zend_Log_Formatter_Simple('%timestamp% %priorityName% %request% %remote%: %message%'."\n"));
$filter = new Zend_Log_Filter_Priority((int)$configuration->log->level);
$writer->addFilter($filter);

$logger = new Zend_Log($writer);
$logger->setEventItem('pid', getmypid());
$logger->setEventItem('request', $_SERVER['REQUEST_URI']);
$logger->setEventItem('remote', $_SERVER['REMOTE_ADDR']);


// TRANSLATIONS
$locale = new Zend_Locale();
Zend_Registry::set('Zend_Locale', $locale);

// default language when requested language is not available
$defaultlanguage = 'en';
define('DEFAULT_LANGUAGE','en');

$translate = new Zend_Translate(
    'csv',
    APPLICATION_PATH.'/languages/',
    null,
    array('scan' => Zend_Translate::LOCALE_FILENAME));

$translate->setOptions(array(
    'log'             => $logger,
    'logUntranslated' => true));


if (!$translate->isAvailable($locale->getLanguage())) {
    // not available languages are rerouted to another language
    $translate->setLocale(DEFAULT_LANGUAGE);
}


// REGISTRY - setup the application registry
// An application registry allows the application to store application 
// necessary objects into a safe and consistent (non global) place for future 
// retrieval.  This allows the application to ensure that regardless of what 
// happends in the global scope, the registry will contain the objects it 
// needs.
$registry = Zend_Registry::getInstance();
$registry->configuration = $configuration;
$registry->arsConfig = $arsConfig;
$registry->logger = $logger;
$registry->translate = $translate;
Zend_Registry::set('Zend_Translate', $translate);

// RAP main classes
require_once RDFAPI_INCLUDE_DIR . 'RdfAPI.php';

// RAP extensions
require_once RDFAPI_INCLUDE_DIR . PACKAGE_RDQL;
require_once RDFAPI_INCLUDE_DIR . 'vocabulary/DC_C.php';
require_once RDFAPI_INCLUDE_DIR . 'vocabulary/RDF_C.php';

// ARC
require_once LIBRARY_PATH . '/loomp-backend/lib/arc/ARC2.php';

// loomp vocabulary class
require_once LIBRARY_PATH . '/loomp-backend/LOOMP_C.php';

// load loomp api
require_once LIBRARY_PATH . '/loomp-backend/api.php';
require_once LIBRARY_PATH . '/loomp-backend/search.php';



define('BASE_URL', $configuration->server->path);
define('LOOMP_HOST', $configuration->server->host);
define('LOOMP_BASE_PATH', $configuration->loomp->base);

// Loomp URIs
define("LOOMP_MODEL_URI", LOOMP_BASE_PATH . "/loomp/dbModel/");
define("LOOMP_USER_URI_NS", LOOMP_BASE_PATH . "/users/");

if (!strstr($_SERVER['REQUEST_URI'], 'install')
			&& !strstr($_SERVER['REQUEST_URI'], 'error')) { 
	try {
		// set database connection and retrieve the rdf model
		$logger->debug("Initializing RDF store");
		$rdfStore = ModelFactory::getDbStore(
			$configuration->loomp->db->type, 
			$configuration->loomp->db->host,
			$configuration->loomp->db->name, 
			$configuration->loomp->db->user, 
			$configuration->loomp->db->pass);
		
		//die(LOOMP_MODEL_URI);
		$registry->rdfModel = $rdfStore->getModel(LOOMP_MODEL_URI);
		if ($registry->rdfModel ===	 false) {
			throw new Exception("Failed to initialize RDF store.");
		}
		
		$logger->debug("Initializing Loomp API");
		$registry->loompApi = new LoompApi();
		
		// Active Record stuff
		require_once RDFAPI_INCLUDE_DIR . 'util/adodb/adodb-active-record.inc.php';
		require_once APPLICATION_PATH . '/model/User.php';
		require_once APPLICATION_PATH . '/model/Access.php';

		ADOdb_Active_Record::SetDatabaseAdapter($rdfStore->getDbConn());
	}
	catch (Exception $e) {
		$logger->err("Init error: " . $e->getMessage());
		$logger->err($e->getTraceAsString());
		
		$loomp_path = "http://" . $_SERVER['HTTP_HOST'] . dirname($_SERVER['SCRIPT_NAME']) . "/error/nosetup";
		
		header("Location: ".$loomp_path);
		die();
	}
}
// CLEANUP - remove items from global scope
// This will clear all our local boostrap variables from the global scope of 
// this script (and any scripts that called bootstrap).  This will enforce 
// object retrieval through the Applications's Registry
unset($frontController, $view, $configuration, $arsConfig, $registry,$logger,$translate);

