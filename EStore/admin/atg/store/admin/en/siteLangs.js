/*
 * Reload languages for the selected site 
 */
function reloadLangs() {
  
  //list of sites
  var siteList = document.getElementById("siteList");
  
  // list of languages
  var langList = document.getElementById("langList");
  var currentSite = sites[siteList.selectedIndex];
  var currentLang;
  var currentLocale;
  
  // remove all languages from the list, 
  // we will add them later based on current site
  for(i = 0; i <= langList.length; i++) {
    langList.remove(i);
  }
  
  if(langList.selectedIndex >= 0) {
    langList.remove(langList.selectedIndex);
  }
  
  for(i = 0; i < currentSite.languages.length; i++) {
    var lang = document.createElement('option');
    lang.appendChild(document.createTextNode(getLanguageString(currentSite.languages[i])));
    lang.setAttribute('value',  currentSite.languages[i]);
    langList.appendChild(lang);
  }
}

/*
 * Return readable language string
 */
function getLanguageString(pLocale) {
  if(pLocale.indexOf("es_") != -1) {
    return "Spanish";
  } else if(pLocale.indexOf("en_") != -1) {
    return "English";
  } else if(pLocale.indexOf("de_") != -1) {
    return "German";
  } else {
    return pLocale;  
  }
}

/*
 * New Site object to hold list of languages
 */
function Site(pSiteId) {
  this.siteId = pSiteId;
  this.languages = new Array();
}

