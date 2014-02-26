/* Commerce Reference Store help */	
{
  "helpItem":
	[


		{"id":"ProductSelectTask.products.store","content":"Commerce Reference Store: Configure all ATG products required to support Commerce Reference Store. See the ATG Commerce Reference Store Installation and Configuration Guide for detailed information."},

		{"id":"AddOnSelectTask.addOns.storefront_demo","content":"Your installation will include one or more Commerce Reference Store demonstration storefronts. If you choose to configure Commerce Reference Store with the full complement of sample data, this option will result in three storefronts: ATG Store US, ATG Store Germany, and ATG Home. These are full-featured storefronts with catalogs, price lists, promotions, etc. If you choose to configure Commerce Reference Store with the minimum sample data required for start up, this option includes the ATG Basic storefront only, which is a barebones storefront without a catalog, price lists, and so on."},

		{"id":"AddOnSelectTask.addOns.cybersource","content":"Your installation will include Commerce Reference Store-specific extensions to ATG Commerce's CyberSource integration functionality."},


		{"id":"AddOnSelectTask.addOns.international","content":"Your installation will include the CommerceReferenceStore.Store.EStore.International module. This module is necessary for sites that will support multiple languages or multiple countries. If you do not install the International module, your production instance of Commerce Reference Store will include the English versions of ATG Store US and ATG Home only. You will not see ATG Store Germany or the Spanish translations for ATG Store US and ATG Home."},

		{"id":"AddOnSelectTask.addOns.fulfillment","content":"Your installation will be configured to use Commerce Reference Store fulfillment."},

		{"id":"AddOnSelectTask.addOns.storefront_no_publishing","content":"Use this option to include the CommerceReferenceStore.Store.Storefront.NoPublishing module in your configuration. This module includes all file-based assets, such as targeters and scenarios, for Commerce Reference Store and it allows you to see these assets in the running Commerce Reference Store application without performing a full deployment.\n\nNote that this option should only be used in development environments. Typically, file-based assets should be imported into a Publishing server then deployed to a Production server through the ATG Business Control Center. This best-practice process ensures that file-based assets are managed properly through the Content Administration's versioned file store. However, it also requires that you set up Content Administration and run a full deployment. For demonstration purposes, where you don't want the overhead of setting up Content Administration, you can choose to include the NoPublishing module, so that file-based assets appear in your application without a full deployment. This means, however, that the file-based assets are not accessible via the Business Control Center and cannot be easily removed from the site. For this reason, do not use this option for configurations that will ultimately be moved to a production environment."},

		{"id":"AddOnSelectTask.addOns.fluoroscope","content":"Use this option to include a tool for viewing site HTML pages that reveals key JSP elements involved in rendering those pages, such as page includes, servlet beans, scenario events and actions, and form fields."},

		{"id":"AddOnSelectTask.addOns.storefront-full-setup","content":"Use this option to configure Commerce Reference Store with a full complement of sample data."},

		{"id":"AddOnSelectTask.addOns.storefront-basic-setup","content":"Use this option to configure an instance of Commerce Reference Store that includes the bare minimum of sample data required for start up (ie, no catalogs, price lists, etc.)."},

		{"id":"/atg/dynamo/service/preview/Localhost_properties.hostName","content":"Enter the host name of the preview server."},

		{"id":"/atg/dynamo/service/preview/Localhost_properties.port","content":"Enter the port number of the preview server."},

		{"id":"AddOnSelectTask.addOns.mobileCRS","content":"Your installation will include the Mobile Reference Store, a version of Commerce Reference Store for web-enabled mobile devices."},

		{"id":"AddOnSelectTask.addOns.mobilecommerce-REST","content":"Your installation will include the REST Web Services provided by the Mobile Reference Store."},
    ]
}