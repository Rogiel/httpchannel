package com.pmstation.shared.soap.client;

import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;

/**
 * This class was generated by the JAXWS SI. JAX-WS RI 2.1-02/02/2007 03:56
 * AM(vivekp)-FCS Generated source version: 2.1
 * 
 */
@WebServiceClient(name = "DesktopAppJax2Service", targetNamespace = "http://api.soap.shared.pmstation.com/", wsdlLocation = "file:///home/tinedel/projects/4shared-api/src/com/pmstation/shared/soap/client/DesktopApp.wsdl")
public class DesktopAppJax2Service extends Service {

	private final static URL DESKTOPAPPJAX2SERVICE_WSDL_LOCATION = DesktopAppJax2Service.class.getResource("DesktopApp.wsdl");;

	public DesktopAppJax2Service(URL wsdlLocation, QName serviceName) {
		super(wsdlLocation, serviceName);
	}

	public DesktopAppJax2Service() {
		super(DESKTOPAPPJAX2SERVICE_WSDL_LOCATION, new QName(
				"http://api.soap.shared.pmstation.com/",
				"DesktopAppJax2Service"));
	}

	/**
	 * 
	 * @return returns DesktopAppJax2
	 */
	@WebEndpoint(name = "DesktopAppJax2Port")
	public DesktopAppJax2 getDesktopAppJax2Port() {
		return (DesktopAppJax2) super.getPort(new QName(
				"http://api.soap.shared.pmstation.com/", "DesktopAppJax2Port"),
				DesktopAppJax2.class);
	}

	/**
	 * 
	 * @param features
	 *            A list of {@link javax.xml.ws.WebServiceFeature} to configure
	 *            on the proxy. Supported features not in the
	 *            <code>features</code> parameter will have their default
	 *            values.
	 * @return returns DesktopAppJax2
	 */
	@WebEndpoint(name = "DesktopAppJax2Port")
	public DesktopAppJax2 getDesktopAppJax2Port(WebServiceFeature... features) {
		return (DesktopAppJax2) super.getPort(new QName(
				"http://api.soap.shared.pmstation.com/", "DesktopAppJax2Port"),
				DesktopAppJax2.class, features);
	}

}