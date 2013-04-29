// BridgeDb,
// An abstraction layer for identifier mapping services, both local and online.
//
// Copyright      2012  Christian Y. A. Brenninkmeijer
// Copyright      2012  OpenPhacts
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
package uk.ac.manchester.cs.server;

import java.util.List;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.log4j.Logger;
import uk.ac.manchester.cs.metadata.SpecificationsRegistry;
import uk.ac.manchester.cs.rdftools.VoidValidatorException;

/**
 *
 * @author Christian
 */
public class WsValidatorServer {
        
    static final Logger logger = Logger.getLogger(WsValidatorServer.class);

    public WsValidatorServer() {
        logger.info("Validator Server setup");
    }
            
    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response welcomeMessage(@Context HttpServletRequest httpServletRequest) throws VoidValidatorException {
        StringBuilder sb = new StringBuilder();

        sb.append("<?xml version=\"1.0\"?>");
        sb.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" "
                + "\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">");
        sb.append("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\">");
        sb.append("<meta http-equiv=\"content-type\" content=\"text/html; charset=ISO-8859-1\"/>");
        sb.append("<head><title>OPS Validator</title></head><body>");
        sb.append("<h1>Open PHACTS Validator</h1>");
        sb.append("<p>Welcome to the prototype of OpenPhacts Validator Service. </p>");
        appendValidationForm(sb, httpServletRequest);                     
        sb.append("</body></html>");
        return Response.ok(sb.toString(), MediaType.TEXT_HTML).build();
    }

    private void appendValidationForm(StringBuilder sb, HttpServletRequest httpServletRequest) throws VoidValidatorException {
     	sb.append("<form method=\"get\" action=\"");
        sb.append(httpServletRequest.getContextPath());
    	sb.append("/");
    	sb.append(WsValidationConstants.VALIDATE);
    	sb.append("\">");
    	sb.append("<fieldset>");
    	sb.append("<legend>Mapper</legend>");
    	sb.append("<p><label for=\"");
    	sb.append(WsValidationConstants.URI);
    	sb.append("\">Input URI</label>");
    	sb.append("<input type=\"text\" id=\"");
    	sb.append(WsValidationConstants.URI);
    	sb.append("\" name=\"");
    	sb.append(WsValidationConstants.URI);
    	sb.append("\" style=\"width:80%\"/></p>");
    	generateSpecificationsSelector(sb);
    	sb.append("<p><input type=\"submit\" value=\"Submit\"/></p>");
    	sb.append("<p>Note: If the new page does not open click on the address bar and press enter</p>");
    	sb.append("</fieldset></form>\n");
   }

    private void generateSpecificationsSelector(StringBuilder sb) throws VoidValidatorException {
		Set<String> names = SpecificationsRegistry.getSpecificationNames();
        sb.append("<p>");
    	sb.append(WsValidationConstants.SPECIFICATION);
        sb.append("<select name=\"");
    	sb.append(WsValidationConstants.SPECIFICATION);
    	sb.append("\">");
		for (String name : names) {
			sb.append("<option value=\"");
			sb.append(name);
			sb.append("\">");
			sb.append(name);
			sb.append("</option>");
		}
    	sb.append("</select>\n");
   }

}


