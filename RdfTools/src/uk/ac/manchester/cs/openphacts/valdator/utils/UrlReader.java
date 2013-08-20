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
package uk.ac.manchester.cs.openphacts.valdator.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Properties;
import java.util.zip.GZIPInputStream;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPConnectionClosedException;
import org.apache.commons.net.ftp.FTPReply;
import uk.ac.manchester.cs.openphacts.valdator.rdftools.FtpListener;
import uk.ac.manchester.cs.openphacts.valdator.rdftools.VoidValidatorException;


/**
 *
 * @author Christian
 */
public class UrlReader {
    
    private final java.net.URI uri;;
    private String username;
    private String password;
    private final String base;
    private static String OPENPHACTS_GITHUB = "https://raw.github.com/openphacts/";
    private static String RAW_GITHUB = "https://raw.github.com";
    private static String HTML_GITHUB = "https://github.com";
    private static String BLOB = "blob/";
    private static String HTML_DROPBOX = "https://www.dropbox.com";
    private static String RAW_DROPBOX = "https://dl.dropbox.com";
    private static String method = "GET";
    private static final boolean useEpsvWithIPv4 = false; //keep
  
    private static File tempDirectory = null;
    private static boolean useDefaultTempDirectory = false;
    
    private static final String TEMP_DIRECTORY = "TempDirectory";
    private static final String USE_DEFAULT = "useDefault";
    
    
    static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(UrlReader.class);
     
    public UrlReader(String address) throws VoidValidatorException{
        try {
            java.net.URI tempUri = new URI(address);
            String path = tempUri.getPath();
            String host = tempUri.getHost();
            int hostEnd = address.indexOf(host) + host.length();
            if (path.isEmpty()){
                base = address.substring(0, hostEnd) + "#";
            } else {
                int pathEnd = address.indexOf(path, hostEnd) + path.length();
                base = address.substring(0, pathEnd) + "#";
            }
        } catch (URISyntaxException ex) {
           throw new VoidValidatorException("Unable to convert " + address + " to a URI");
        }
        String scrubbedAddress = scrub(address);
        try {
            uri = new URI(scrubbedAddress);
        } catch (URISyntaxException ex) {
           throw new VoidValidatorException("Unable to convert (scrubbed) " + scrubbedAddress + " to a URI");
        }
        AccountInfo info = AccountsReader.findByUri(scrubbedAddress);
        if (info != null){
            username = info.getLogin();
            password = info.getPassword();
        } else {
            username = null;
            password = null;
        }
    }
    
    public String getBase(){
        return base;
    }
    
    String getPath() {
        return uri.getPath();
    }

    public InputStream getInputStream() throws VoidValidatorException{
        String schema = uri.getScheme().toLowerCase();
        InputStream raw;
        if (schema.equals("https") || schema.equals("http")){
            raw = getHttpsInputStream();
        } else if (schema.equals("ftp")){
            raw = getFtpInputStream();
        } else {
            throw new VoidValidatorException ("Unexpected schema " + schema + " in " + uri);
        }
        String path = uri.getPath().toLowerCase();
        if (path.endsWith("gz")){
            try {
                return new GZIPInputStream(raw);
            } catch (IOException ex) {
                throw new VoidValidatorException("Unable to get gzip input stream ", ex);
            }
        }
        return raw;
        
    }
    
    private  static String scrub(String address){
        String scrubbedAddress = address;
        if (scrubbedAddress.startsWith(HTML_GITHUB)){
            scrubbedAddress = scrubbedAddress.replaceFirst(HTML_GITHUB, RAW_GITHUB);
            if (scrubbedAddress.contains(BLOB)){
                scrubbedAddress = scrubbedAddress.replaceFirst(BLOB, "");
            }
        }
        if (scrubbedAddress.startsWith(HTML_DROPBOX)){
            scrubbedAddress = scrubbedAddress.replaceFirst(HTML_DROPBOX, RAW_DROPBOX);
        }
        return scrubbedAddress;
    }
    
     private InputStream getHttpsInputStream() throws VoidValidatorException {
        String encodedAuthorization = null;
        if (username != null && password != null){
            String authorization = username + ':'  + password;
            encodedAuthorization = new String(Base64.encodeBase64(authorization.getBytes()));
        }
        HttpURLConnection connection = setupConnection(encodedAuthorization);
        try {
            return connection.getInputStream();
        } catch (IOException ex) {
            throw new VoidValidatorException ("Error getting inputstream " + uri, ex);
        }
    }
        
    //taken from https://github.com/kohsuke/github-api
    //http://opensource.org/licenses/mit-license.php
    private HttpURLConnection setupConnection(String encodedAuthorization) throws VoidValidatorException {
        try {
            URL url = uri.toURL();
            HttpURLConnection uc = (HttpURLConnection) url.openConnection();

            if (encodedAuthorization != null){
                // if the authentication is needed but no credential is given, try it anyway (so that some calls
                // that do work with anonymous access in the reduced form should still work.)
                // if OAuth token is present, it'll be set in the URL, so need to set the Authorization header
                uc.setRequestProperty("Authorization", "Basic " + encodedAuthorization);
            }

            try {
                uc.setRequestMethod(method);
            } catch (ProtocolException e) {
                // JDK only allows one of the fixed set of verbs. Try to override that
                Field $method = HttpURLConnection.class.getDeclaredField("method");
                $method.setAccessible(true);
                $method.set(uc,method);
            }
            //uc.setRequestProperty("Accept-Encoding", "gzip");
            return uc;
        } catch (Exception ex) {
            if (encodedAuthorization == null){
                throw new VoidValidatorException ("Error connection to "+ uri);
            } else {
                throw new VoidValidatorException ("Error connection to "+ uri + " could be a user name and password issue.");
            }
        }
    }

    private InputStream getFtpInputStream() throws VoidValidatorException{
        if (username == null){
            username = "anonymous";
            try {
                password = System.getProperty("user.name")+"@"+InetAddress.getLocalHost().getHostName();
            } catch (UnknownHostException ex) {
                throw new VoidValidatorException ("Unable to set up anonymous.",ex);
            }
        }
        FTPClient ftp = connect();
        login(ftp);
        return getInputStream(ftp);
    }
    
    private FTPClient connect() throws VoidValidatorException {
        FTPClient ftp = new FTPClient();

        //CB consider replacing with logger
        // suppress login details
        ftp.addProtocolCommandListener(new FtpListener(logger));

        try {
            int reply;
            if (uri.getPort() > 0) {
                ftp.connect(uri.getHost(), uri.getPort());
            } else {
                ftp.connect(uri.getHost());
            }
            logger.info("Connected to " + uri.getHost() + " on port " + (uri.getPort()>0 ? uri.getPort() : ftp.getDefaultPort()));

            // After connection attempt, you should check the reply code to verify
            // success.
            reply = ftp.getReplyCode();

            if (!FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect();
                throw new VoidValidatorException ("Unable to connect to FTP server " + uri);
            }
            return ftp;
        }
        catch (IOException ex) {
            disconnect(ftp);
            throw new VoidValidatorException ("Error to connect to FTP server " + uri, ex);
        }
     }

    private void disconnect(FTPClient ftp){
        if (ftp.isConnected()) {
            try {
                ftp.disconnect();
            } catch (IOException f) {
                logger.error("->Error on tfp dosconnect", f);
            }
        }
    }
    
    private void login(FTPClient ftp) throws VoidValidatorException {
        try {
            if (!ftp.login(username, password))
            {
                ftp.logout();
            } 
            logger.debug("Remote system is " + ftp.getSystemType());
        } catch (IOException ex) {
            disconnect(ftp);
            throw new VoidValidatorException ("Unable to log into FTP " + uri + " using username:" + username + " password:"+ password);
        }
    }
    
    private InputStream getInputStream(FTPClient ftp) throws VoidValidatorException
    {
        try
        {
            // in theory this should not be necessary as servers should default to ASCII
            // but they don't all do so - see NET-500
            ftp.setFileType(FTP.ASCII_FILE_TYPE);
            ftp.enterLocalPassiveMode();
            ftp.setUseEPSVwithIPv4(useEpsvWithIPv4);
            String path = uri.getPath();
            return ftp.retrieveFileStream(path);
        }
        catch (FTPConnectionClosedException ex)
        {
             disconnect(ftp);
             throw new VoidValidatorException("Server closed connection.", ex);
        }
        catch (IOException ex)
        {
            disconnect(ftp);
            throw new VoidValidatorException("IOEXception with Server ", ex);
        }
    } 
    
    public File getTempTextFile() throws VoidValidatorException{
        try {
            InputStream inputStream = this.getInputStream();
            InputStreamReader inputReader = new InputStreamReader(inputStream);
            BufferedReader inputBuffer = new BufferedReader(inputReader);
            String suffix = uri.getPath().substring(uri.getPath().lastIndexOf("."));
            File outputFile = File.createTempFile("CopiedFromUri", suffix, getTempDirectory());
            FileWriter outputWriter = new FileWriter(outputFile);
            outputFile.deleteOnExit();
            BufferedWriter outputBuffer = new BufferedWriter(outputWriter);
            String line;
            while ((line = inputBuffer.readLine()) != null) {
                outputBuffer.write(line + "\n");
            }
            inputBuffer.close();
            outputBuffer.flush();
            outputBuffer.close();
            return outputFile;
        } catch (IOException ex) {
            throw new VoidValidatorException("Error convert " + uri + " to file ", ex);
        }
    }

    public static void main(String[] args) throws Exception {
        UrlReader reader = new UrlReader("https://github.com/openphacts/ops-platform-setup/blob/master/void/drugbank_void.ttl");
        InputStream inputStream = reader.getInputStream();
        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));

        String inputLine;
        while ((inputLine = in.readLine()) != null)
            System.out.println(inputLine);
        in.close();   
    }

    private File getTempDirectory() throws VoidValidatorException {
        if (useDefaultTempDirectory){
            return null;
        }
        if (tempDirectory == null){
            Properties properties = PropertiesLoader.getProperties();
            String fileName = properties.getProperty(TEMP_DIRECTORY);
            if (fileName == null || fileName.equals(USE_DEFAULT)){
                useDefaultTempDirectory = true;
                return null;
            }
            tempDirectory = new File(fileName);
            if (!tempDirectory.isDirectory()){
                throw new VoidValidatorException("Property " + TEMP_DIRECTORY + " points to " + fileName + " which to a directory.");
            }
            if (!tempDirectory.canWrite()){
                throw new VoidValidatorException("Property " + TEMP_DIRECTORY + " points to " + fileName + " which does not provide write access.");
            }
        }
        return tempDirectory;
    }
 
}
