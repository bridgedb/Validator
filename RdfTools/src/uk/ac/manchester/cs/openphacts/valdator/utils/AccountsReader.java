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

import java.net.URL;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import uk.ac.manchester.cs.openphacts.valdator.rdftools.Reporter;
import uk.ac.manchester.cs.openphacts.valdator.rdftools.VoidValidatorException;

/**
 *
 * @author Christian
 */
public class AccountsReader {
    
    private static HashSet<AccountInfo> infos = null;

    private static final String URI = "uri";
    private static final String LOGIN = "login";
    private static final String PASSWORD = "password";
    private static final String ACCOUNT_PREFIX = "account.";
    
    private static void init() throws VoidValidatorException{
        if (infos != null){
            return;
        }
        Properties properties = PropertiesLoader.getProperties();
        infos = new HashSet<AccountInfo>();
        Set<String> keys = properties.stringPropertyNames();
        for (String key:keys){
            if (key.startsWith(ACCOUNT_PREFIX)){
                String[] parts = key.split("\\.");
                if (parts.length == 3){
                    AccountInfo info = null;
                    for (AccountInfo i:infos){
                        if (i.getName().equals(parts[1])){
                            info = i;
                        }
                    }
                    if (info == null){
                        info = new AccountInfo(parts[1]);
                    }
                    if (parts[2].equals(URI)){
                        info.setUri(properties.getProperty(key));
                    } else if (parts[2].equals(LOGIN)){
                        info.setLogin(properties.getProperty(key));
                    } else if (parts[2].equals(PASSWORD)){
                        info.setPassword(properties.getProperty(key));
                    } else {
                        throw new VoidValidatorException ("Unexpected " + ACCOUNT_PREFIX +  " property." + key );                    
                    }
                    infos.add(info);
                } else {
                    throw new VoidValidatorException ("Unexpected " + ACCOUNT_PREFIX +  " property. It should be three dot seperated parts." + key );
                }
            }
        }
   }
    
    public static AccountInfo findByUrl(URL url) throws VoidValidatorException {
        return findByUri(url.toString());
    }
    
    public static AccountInfo findByUri(String uri) throws VoidValidatorException {
        init();
        AccountInfo result = null;
        for (AccountInfo info:infos){
            if (info.getUri() != null && uri.startsWith(info.getUri())){
                if (result == null){
                    result = info;
                } else {
                    if (result.getUri().length() < info.getUri().length()){
                        result = info;
                    }
                    //else keep the result as it is already more precise.
                }
            }
        }
        return result;
    }
    
    public static void main(String[] args) throws Exception {
        init();
        for (AccountInfo info:infos){
            Reporter.println(info.toString());
        }
        Reporter.println("   ");
        AccountInfo info = findByUri("https://raw.github.com/openphacts/ops-platform-setup/master/void/drugbank_void.ttl");
        Reporter.println(info.toString());        
    }
}
