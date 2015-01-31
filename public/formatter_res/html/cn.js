var actRes;

var myMenuItems = [
  {
    name: 'Concept Navigation',
    className: 'navigate', 
    callback: function(e) {
	  var tagName = e.element().getAttribute("about").toLowerCase();
      //var ele = document.getElementById("conList");
      new Ajax.Updater('conList', '../fragment/list', {method: 'post', postBody:'resource='+ tagName});
      //ele.innerHTML = "<a href='"+tagName+"'>"+tagName+"</a>";
	  lb_cn.open();
    }
  },{
    name: 'Browse Data',
    className: 'data', 
    callback: function(e) {
      var tagName = e.element().getAttribute("about").toLowerCase();
      var ele = document.getElementById("actRes");
      ele.innerHTML = "<a href='"+tagName+"'>"+tagName+"</a>";
      window.location.href = tagName;
      //lb_dn.open();
    }
  }
]

var lb_cn;
var lb_dn;

function initCN(){
	
	new Proto.Menu({
		selector: 'span', // context menu will be shown when element with id of "contextArea" is clicked
		className: 'menu desktop', // this is a class which will be attached to menu container (used for css styling)
		menuItems: myMenuItems // array of menu items
	})
	
	lb_cn = new Lightbox('ConceptNavigation');
	lb_dn = new Lightbox('DataNavigation');
}