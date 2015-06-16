/* ============================================================================
 (c) Copyright 2014 Hewlett-Packard Development Company, L.P.
Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights to
use, copy, modify, merge,publish, distribute, sublicense, and/or sell copies of
the Software, and to permit persons to whom the Software is furnished to do so,
subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
============================================================================ */

package org.hp.samples;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import argo.jdom.JdomParser;
import argo.jdom.JsonNode;
import argo.jdom.JsonRootNode;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;

import flexjson.JSONSerializer;

public class MysqlServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private Logger logger;
    
    private Logger getLogger() {
    	
    	if(logger == null) {
    		logger = Logger.getLogger(this.getClass());
    	}
    	
    	return logger;
    	
    }
    
    /**
     *  invoked when user submits some text they want to store
     */
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/plain");
	    response.setStatus(200);
	    
	    PrintWriter writer = response.getWriter();
	    if(request.getServletPath().equals("/addContent")) {
		    try { 
		    	
			    Connection dbConnection = getConnection();
			    String contents = request.getParameter("contents");
			    getLogger().debug("beginning to insert  "+contents+" into database");
			    String insert = "insert into text_data(contents) values('"+contents+"')";
			    Statement stmt = dbConnection.createStatement();
			    stmt.execute(insert);
			    getLogger().debug("finished inserting "+contents+" into database");
			    response.sendRedirect(request.getContextPath());
			    
		    } catch(Exception ex) {
		    	
		    	getLogger().error(ex);
		    	ex.printStackTrace();
		    	
		    	response.setStatus(500);
		    }
	    } else {
	    	String errStr = "current post handler only handles /addContent, not "+request.getServletPath(); 
	    	getLogger().error(errStr);
	    	response.setStatus(404);
	    }
	    
	    writer.close();
	    
	    
    }
            
    /**
     * basic GET handler, used to handle /allData requests only        
     */
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/json");
        response.setStatus(200);
        PrintWriter writer = response.getWriter();
        

        if(request.getServletPath().equals("/allData")) {
	        try {
		        Connection dbConnection = getConnection();
		        getLogger().debug("about to retrieve current contents from database");
		        List<TableContents> allContents  = getAllContents(dbConnection);
		        
		        JSONSerializer jsonSer = new JSONSerializer();
		        String str = jsonSer.serialize(allContents);
		        getLogger().debug("contents from db serialized to "+str);
		        writer.write(str); 
		        dbConnection.close();
	        } catch(Exception ex) {
	        	//TODO: log
	        	response.setStatus(500);
	        }
        } else {
        	String errStr = "current get handler only handles /allData, not "+request.getServletPath(); 
        	getLogger().error(errStr);
	    	response.setStatus(404);
        	
        }
        writer.close();
    }

    /**
     * pulls table contents into TableContents array
     * @param dbConnection
     * @return List<TableContents>
     */
	private List<TableContents> getAllContents(Connection dbConnection)  {
		List<TableContents> allContents = new ArrayList<TableContents>();
		try { 
		    
		    String getContents = "select text_id, contents from text_data";
		    Statement stmt = dbConnection.createStatement();
		    ResultSet rs = stmt.executeQuery(getContents);
		    
		    while(rs.next()) {
		    	Integer id = rs.getInt("text_id");
		    	String contents = rs.getString("contents");
		    			
		    	allContents.add(new TableContents(id,contents));
		    }
		    
		    
	    } catch(SQLException ex) {
	    	
	    	ex.printStackTrace();
	    	// TODO: log
	    	
	    }
		
		return allContents;
	}


	/**
	 * uses defined env var VCAP_SERVICES, serializes it as a URI
	 * @return a Connection object
	 * @throws Exception
	 */
	private Connection getConnection() throws Exception {
		Connection dbConnection = null;
        
         
        getLogger().debug("Connecting to MySQL using mysql settings in VCAP_SERVICES environment variable...");
        String vcap_services = System.getenv("VCAP_SERVICES");
        
        if (vcap_services != null && vcap_services.length() > 0) {
            try {
            	// Use a JSON parser to get the info we need from  the
                // VCAP_SERVICES environment variable. This variable contains
                // credentials for all services bound to the application.
                // In this case, MySQL is the only bound service.
                JsonRootNode root = new JdomParser().parse(vcap_services);

                JsonNode mysqlNode = root.getNode("mysql");
                JsonNode credentials = mysqlNode.getNode(0).getNode("credentials");

                // Grab login info for MySQL from the credentials node
                String dbname = credentials.getStringValue("name");
                String hostname = credentials.getStringValue("hostname");
                String user = credentials.getStringValue("user");
                String password = credentials.getStringValue("password");
                String port = credentials.getNumberValue("port");

                String dbUrl = "jdbc:mysql://" + hostname + ":" + port + "/" + dbname;

                
                Class.forName("com.mysql.jdbc.Driver");
                dbConnection = DriverManager.getConnection(dbUrl, user, password);
                
            
            } catch (Exception e) {
        		getLogger().error(e);
	            throw(e);
        	}
        }
        
        
        return(dbConnection);
	}
    
   
}
