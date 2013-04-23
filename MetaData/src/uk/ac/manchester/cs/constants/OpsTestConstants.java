// BridgeDb,
// An abstraction layer for identifier mapping services, both local and online.
//
// Copyright 2006-2009  BridgeDb developers
// Copyright 2012-2013  Christian Y. A. Brenninkmeijer
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
package uk.ac.manchester.cs.constants;

import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

public class OpsTestConstants {

	private static final String PREFIX = "http://openphacts.cs.man.ac.uk:9090/Void/testOntology.owl#";
	
	public static final URI HAS_WEBSITE = new URIImpl(PREFIX + "hasWebsite");
	public static final URI HAS_PHONE_NUMBER = new URIImpl(PREFIX + "hasPhoneNumber");
	public static final URI HAS_BIRTHDATE = new URIImpl(PREFIX + "hasBirthdate");
	public static final URI HAS_HOUSE_NUMBER = new URIImpl(PREFIX + "hasHouseNumber");
	public static final URI HAS_POSTBOX = new URIImpl(PREFIX + "hasPostbox");
	public static final URI PARENT = new URIImpl(PREFIX + "Parent");
    
    
}
