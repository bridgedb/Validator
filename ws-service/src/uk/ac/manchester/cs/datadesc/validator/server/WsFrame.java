// OpenPHACTS RDF Validator,
// A tool for validating and storing RDF.
//
// Copyright 2012-2013  Christian Y. A. Brenninkmeijer
// Copyright 2012-2013  University of Manchester
// Copyright 2012-2013  OpenPhacts
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
package uk.ac.manchester.cs.datadesc.validator.server;


import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.log4j.Logger;
import uk.ac.manchester.cs.datadesc.validator.Validator;
import uk.ac.manchester.cs.datadesc.validator.ValidatorImpl;
import uk.ac.manchester.cs.datadesc.validator.rdftools.ExampleConstants;
import uk.ac.manchester.cs.datadesc.validator.rdftools.RdfFactory;
import uk.ac.manchester.cs.datadesc.validator.rdftools.RdfInterface;
import uk.ac.manchester.cs.datadesc.validator.rdftools.VoidValidatorException;


/**
 * This class provides the Reposnse Frame including Top and Sidebar 
 * 
 * @author Christian
 */
public class WsFrame extends WsValidatorServer implements FrameInterface{
    
    //protected final NumberFormat formatter;
        
    static final Logger logger = Logger.getLogger(WsFrame.class);

    public WsFrame() {
        super();
        RdfInterface rdfInterface;
        try {
            rdfInterface = RdfFactory.getFilebase();
            try {
                rdfInterface.loadURI(ExampleConstants.EXAMPLE_CONTEXT, null);
            } catch (Exception ex){
                logger.error(ex);
                //could be of line so just go on without example data.
            }
            rdfInterface.close();
            Validator validator = new ValidatorImpl(rdfInterface);
            super.setUp(rdfInterface, validator, this);
        } catch (VoidValidatorException ex) {
            logger.error("Initisation of Validation Service failed!", ex);
        }
        /*formatter = NumberFormat.getInstance();
        if (formatter instanceof DecimalFormat) {
            DecimalFormatSymbols dfs = new DecimalFormatSymbols();
            dfs.setGroupingSeparator(',');
            ((DecimalFormat) formatter).setDecimalFormatSymbols(dfs);
        }*/
    }
    
    public WsFrame(RdfInterface rdfInterface) {
        super();
        try {
            Validator validator = new ValidatorImpl(rdfInterface);
            super.setUp(rdfInterface, validator, this);
        } catch (VoidValidatorException ex) {
            logger.error("Initisation of Validation Service failed!", ex);
        }
    }
        
    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response welcomeMessage(@Context HttpServletRequest httpServletRequest) throws VoidValidatorException {
        return validateHome(httpServletRequest);
    }

    public StringBuilder topAndSide(String header, HttpServletRequest httpServletRequest) {
        StringBuilder sb = header(header);
        top(sb, header);      
        sideBar(sb, httpServletRequest);
        sb.append("<div id=\"content\">");
        return sb;
    }
    
    protected StringBuilder header(String header) {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\"?>");
        sb.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" ");
        sb.append("\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">");
        sb.append("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\">");
        sb.append("<meta http-equiv=\"content-type\" content=\"text/html; charset=ISO-8859-1\"/>");
        sb.append("<head>\n");
        sb.append("<title>");
        sb.append(header);
        sb.append("</title>\n");
        style(sb);
        toggler(sb);
        sb.append("</head><body>");
        return sb;
    }

    protected void style(StringBuilder sb) {
        sb.append("<style>\n");
        sb.append("#container { width: 100%; margin: 10px auto; background-color: #fff; color: #333; border: ");
            sb.append("1px solid gray; line-height: 130%; font-family: perpetua, garamond, serif; font-size: 110%; ");
            sb.append("min-width: 40em; }\n");
        sb.append("#top { padding: .5em; background-color: #808080; border-bottom: 1px solid gray; }\n");
        sb.append("#top h1 { padding: .25em .5em .25em .5em; margin-left: 200px; margin-bottom: 0; margin-right: 0; margin-top: 0 }\n");
        sb.append("#top a { text-decoration: none; color: #ffffff; }\n");
        sb.append("#navBar { float: left; width: 200px; margin: 0em; padding: 5px; min-width: 200px; border-right: 1px solid gray; min-height: 100%} \n");
        sb.append("#content { margin-left: 210px; border-left: 1px solid gray; padding: 1em; min-width: 20em; min-height: 500px; }\n");
        sb.append("#footer { clear:both; }\n");
        sb.append("fieldset {border: 1px solid #781351;width: 20em}\n");
        sb.append("legend { color: #fff; background: #ffa20c; border: 1px solid #781351; padding: 2px 6px }\n");
        sb.append("</style>\n");
        sb.append("<style type=\"text/css\">");
        sb.append("	.texthotlink, .texthotlink_hilight { width: 150px; font-size: 85%; padding: .25em; cursor: ");
            sb.append("pointer; color: black; font-family: Arial, sans-serif;	}\n");
        sb.append("	.texthotlink_hilight {background-color: #fff6ac;}\n");
        sb.append("		.menugroup { font-size: 150%; font-weight: bold; padding-top: .25em; }\n");
        sb.append("		input { background-color: #EEEEFF; } body, td { background-color: white; font-family: sans-serif; }\n");
        sb.append("	</style>\n");            
    }

    protected void toggler(StringBuilder sb) {
        sb.append("<script language=\"javascript\">\n");
        sb.append("		function getObj(id) {\n");
        sb.append("			return document.getElementById(id)\n");
        sb.append("		}\n");
        sb.append("		function DHTML_TextHilight(id) {\n");
        sb.append("			getObj(id).classNameOld = getObj(id).className;\n");
        sb.append("			getObj(id).className = getObj(id).className + \"_hilight\";\n");
        sb.append("		}\n");
        sb.append("		function DHTML_TextRestore(id) {\n");
        sb.append("			if (getObj(id).classNameOld != \"\")\n");
        sb.append("				getObj(id).className = getObj(id).classNameOld;\n");
        sb.append("		}\n");
        sb.append("     function getItem(id){\n");
        sb.append("         var itm = false;\n");
        sb.append("         if(document.getElementById)\n");
        sb.append("             itm = document.getElementById(id);\n");
        sb.append("         else if(document.all)\n");
        sb.append("             itm = document.all[id];\n");
        sb.append("         else if(document.layers)\n");
        sb.append("             itm = document.layers[id];\n");
        sb.append("         return itm;\n");
        sb.append("    }\n\n");
        sb.append("    function toggleItem(id)\n");
        sb.append("{\n");
        sb.append("    itm = getItem(id);\n");
        sb.append("    if(!itm)\n");
        sb.append("        return false;\n");
        sb.append("    if(itm.style.display == 'none')\n");
        sb.append("        itm.style.display = '';\n");
        sb.append("    else\n");
        sb.append("        itm.style.display = 'none';\n");
        sb.append("    return false;\n");
        sb.append("}\n\n");
        sb.append("function hideDetails()\n");
        sb.append("{\n");
        sb.append("     toggleItem('ops')\n");
        sb.append("     toggleItem('sparql')\n");
        sb.append("     return true;\n");
        sb.append("}\n\n");
        sb.append("</script>\n");
    }

     protected void top(StringBuilder sb, String header) {
        sb.append("<div id=\"container\">");
        sb.append("<div id=\"top\">");
        sb.append("<a href=\"http://www.cs.manchester.ac.uk/\">" +
        		"<img style=\"float: left; border: none; padding: 0px; margin: 0px;\" " +
        		"src=\"http://assets.manchester.ac.uk/logos/university-1.png\" " +
        		"alt=\"The University of Manchester\" height=\"50\"></img></a>");
        sb.append("<a href=\"http://www.openphacts.org/\">" +
        		"<img style=\"float: right; border: none; padding: 0px; margin: 0px;\" " +
        		"src=\"http://www.openphacts.org/images/stories/banner.jpg\" " +
        		"alt=\"Open PHACTS\" height=\"50\"></img></a>");
        sb.append("<h1>");
        sb.append(header);
        sb.append("</h1>");
        sb.append("</div>");   
    }
    
    protected void sideBar(StringBuilder sb, HttpServletRequest httpServletRequest) {
        sb.append("<div id=\"navBar\">");
        addSideBarMiddle(sb, httpServletRequest);
        sb.append("</div>\n");        
    }
    
    /**
     * Allows Super classes to add to the side bar
     */
    protected void addSideBarMiddle(StringBuilder sb, HttpServletRequest httpServletRequest) {
        addValidatorSideBar(sb, httpServletRequest);
    }
    
    /**
     * Allows Super classes to add to the side bar
     * /
    protected void addSideBarIMS(StringBuilder sb, HttpServletRequest httpServletRequest) throws BridgeDBException{
        sb.append("<div class=\"menugroup\">OPS Identity Mapping Service</div>");
        addSideBarItem(sb, "ims-home", "Home", httpServletRequest);
        String allMappingInfo = WsUriConstants.GET_MAPPING_INFO + "?" + WsUriConstants.LENS_URI + "=" + Lens.getAllLens();
        addSideBarItem(sb, allMappingInfo,"All Mappings Summary", httpServletRequest);
        addSideBarItem(sb,  WsUriConstants.GET_MAPPING_INFO, "Default Mappings Summary", httpServletRequest);
        String allGraphwiz = WsUriConstants.GRAPHVIZ + "?" + WsUriConstants.LENS_URI + "=" + Lens.getAllLens();
        addSideBarItem(sb, allGraphwiz, "All Mappings Graphviz",  httpServletRequest);
        addSideBarItem(sb, WsUriConstants.GRAPHVIZ, "Default Mappings Graphviz",  httpServletRequest);
        addSideBarItem(sb, WsUriConstants.LENS, "Lens",  httpServletRequest);
        addSideBarItem(sb, "ims-api", "IMS API", httpServletRequest);
    }*/

    /**
     * Adds an item to the SideBar for this service
     */
    @Override
    public void addSideBarItem(StringBuilder sb, String page, String name, HttpServletRequest httpServletRequest) {
        sb.append("\n<div id=\"menu");
        sb.append(page);
        sb.append("_text\" class=\"texthotlink\" ");
        sb.append("onmouseout=\"DHTML_TextRestore('menu");
        sb.append(page);
        sb.append("_text'); return true; \" ");
        sb.append("onmouseover=\"DHTML_TextHilight('menu");
        sb.append(page);
        sb.append("_text'); return true; \" ");
        sb.append("onclick=\"document.location = &quot;");
        sb.append(httpServletRequest.getContextPath());
        sb.append("/");
        sb.append(page);
        sb.append("&quot;;\">");
        sb.append(name);
        sb.append("</div>");
     }

    public void footerAndEnd(StringBuilder sb){
        sb.append("<div id=\"footer\">");
        sb.append("This site is run by <a href=\"https://wiki.openphacts.org/index.php/User:Christian\">Christian Brenninkmeijer</a>.");
        sb.append("\n<div></body></html>");
    }

    @Override
    //TODO use velocity here
    public String createHtmlPage(String title, String info, HttpServletRequest httpServletRequest) {
        StringBuilder sb = topAndSide(title,  httpServletRequest);
        sb.append(info);
        footerAndEnd(sb);
        return sb.toString();
    }

}


