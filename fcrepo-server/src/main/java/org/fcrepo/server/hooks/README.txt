Fedora API-M Hooks configuration
--------------------------------

Configuration is done in fedora.fcfg, see the following example:

  <module role="fedora.server.hooks.APIHooks" class="fedora.server.hooks.APISOAPHooksImpl">
    <comment>Configures callbacks that are triggered upon API calls.</comment>
    <param name="soapuri" value="http://www.webserver.com/Phaidra::Hooks::APIHooks"/>
    <param name="soapproxy" value="http://www.webserver.com/apihooks.cgi"/>
    <param name="soapmethod" value="phaidraHook"/>
    <param name="modifyDatastreamByValue" value="true"/>
    <param name="modifyDatastreamByReference" value="true"/>
    <param name="addDatastream" value="true"/>
  </module>

Mandatory parameters are:
*) soapuri: the URI ("namespace") which will be used for the SOAP calls
*) soapproxy: the URL to the executable CGI (or Servlet etc) for the SOAP request
*) soapmethod: the SOAP method that will be called

By default callback hooks for API-M methods are disabled. You have to
enable notifications by setting a parameter in fedora.fcfg for each
API-M call that you want to get notified of. Just supply the name of
the API-M call.

The SOAP method will be called with the method name and the PID as parameters.
Some callbacks supply additional parameters like the name of the datastream
modified (for modifyDatastreamBy*, purgeDatastream, etc).