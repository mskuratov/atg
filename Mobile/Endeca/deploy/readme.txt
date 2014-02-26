CRS-M deployment instructions:
==============================
NOTE:
  This is the development type of CRS deployment process to Endeca: the instruction will be changed for production type
deployment. This instruction also assumes you already deployed full CRS application to Endeca, using
"<ATG_WORKSPACE_HOME>\B2CStore\Storefront\deploy\deploy.xml" descriptor file (<ATG_WORKSPACE_HOME> is the ATG workspace root).

Deploy CRS-M part to Endeca, as follows:
  <Endeca_ToolsAndFrameworks_HOME>\deployment_template\bin\deploy.bat --app <ATG_WORKSPACE_HOME>\B2CStore\Mobile\Endeca\deploy\deploy.xml
(<Endeca_ToolsAndFrameworks_HOME> is the Endeca "ToolsAndFrameworks" module root)
  1. Press "y" on "Continue?" prompt.
  2. Press "n" on "Install base deployment?"
     (you don't need the base deployment as you already done it while deploying the full CRS).
  3. Enter a short name for your application (this is the same name you used to deploy full CRS).
  4. Enter deployment directory (this is the same directory you used to deploy full CRS).
  5. Enter EAC port (confirm default value, 8888).

At the end of successful deployment, you'll see the "Application successfully deployed." message in console.
Now all the CRS and CRS-M pages and content structure are in Experience Manager (i.e. you do NOT need to create
CRS-M pages and content in Experience Manager).
After that, all you need is to run the following scripts from deployed Endeca application home directory:
  1. initialize_services.bat
  2. set_editors_config.bat
  3. set_templates.bat

CRS-M Experience Manager configuration notes:
---------------------------------------------
  1. If you want "Show More..." and "Show Less..." links to be displayed for each facet group, select
"Content\Mobile\Shared\Default Guided Navigation", then in "Content Editor" select
"Guided Navigation\navigation" and then mark "Enable 'More' Link" checkbox for each customized dimension.
Do the same actions for "Content\Mobile\Shared\Brand Landing page Guided Navigation" content.
