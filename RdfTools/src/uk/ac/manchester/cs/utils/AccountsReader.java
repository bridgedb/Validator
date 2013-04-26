/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.manchester.cs.utils;

import java.net.URL;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import uk.ac.manchester.cs.rdftools.VoidValidatorException;

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
        Properties properties = ConfigReader.getProperties();
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
            System.out.println(info);
        }
        System.out.println("   ");
        AccountInfo info = findByUri("https://raw.github.com/openphacts/ops-platform-setup/master/void/drugbank_void.ttl");
        System.out.println(info);        
    }
}
