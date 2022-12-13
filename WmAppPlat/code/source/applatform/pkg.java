
package applatform;

// -----( IS Java Code Template v1.2
// --- <<IS-START-IMPORTS>> ---

import java.util.ArrayList;
import com.softwareag.applatform.pls.is.osgi.Metadata2Signatures;
import com.softwareag.applatform.pls.is.osgi.MethodMetadataImpl;
import com.softwareag.applatform.pls.is.osgi.OsgiService;
import com.softwareag.util.IDataMap;
import com.wm.app.b2b.server.ACLManager;
import com.wm.app.b2b.server.BaseService;
import com.wm.app.b2b.server.Package;
import com.wm.app.b2b.server.PackageState;
import com.wm.app.b2b.server.PackageManager;
import com.wm.app.b2b.server.PackageStore;
import com.wm.app.b2b.server.ServiceException;
import com.wm.app.b2b.util.ServerIf;
import com.wm.app.b2b.server.ACLManager;

import com.softwareag.util.IDataMap;
import com.wm.app.b2b.server.Server;
import com.wm.data.*;
import com.wm.lang.ns.NSInterface;
import com.wm.lang.ns.NSName;
import com.wm.lang.ns.NSNode;
import com.wm.lang.ns.NSRecord;
import com.wm.lang.ns.NSService;
import com.wm.lang.ns.NSSignature;

// --- <<IS-END-IMPORTS>> ---

public final class pkg
{
    // ---( internal utility methods )---
	
	final static pkg _instance = new pkg();

	static pkg _newInstance() { return new pkg(); }

	static pkg _cast(Object o) { return (pkg)o; }


    // Keep track of the current WmAppPlat IS package version
    static String wmAppPlatVersion = null;

    // ---( server methods )---

    public static final void startup(IData pipeline) throws ServiceException {
        // --- <<IS-START(startup)>> ---
        // @sigtype java 3.5
    	try {
    		System.err.println("applatform.pkg:startup... Starting WmAppPlat package");
            //initServiceACLs(new String[]{"applatform.services:registerService", "applatform.services:unregisterService"});
            setPackageFolderProtections();
    	} catch (Exception e) {
    		Server.logError(e);
    	}
        // --- <<IS-END>> ---

    }

    public static final void shutdown(IData pipeline) throws ServiceException {
        // --- <<IS-START(shutdown)>> ---
        // @sigtype java 3.5

    	try {
    		System.err.println("applatform.pkg:shutdown... Stopping WmAppPlat package");

    	} catch (Exception e) {
    		Server.logError(e);
    	}
        // --- <<IS-END>> ---

    }

    // --- <<IS-START-SHARED>> ---
    
    private static final String PARENT_PACKAGE_NAME = "WmAppPlat";

    private static void setAllAclTypes(String inTargetName, String inAclName) {

/* - what we were doing before ...
ACLManager.WEBM_ADMINISTRATORS_ACL, // browse
	            		ACLManager.INTERNAL_ACL,  // execute
	            		null, // ACLManager.WEBM_INTERNALDEV_ACL // read
	            		null, // ACLManager.WEBM_INTERNALDEV_ACL // write
	            		false);
*/                        

        // Execute, list, read & write ACLs; use deployer acl for all
        ACLManager.setAclGroup( inTargetName, inAclName );
        ACLManager.setBrowseAclGroup( inTargetName, inAclName );
        ACLManager.setReadAclGroup( inTargetName, inAclName );
        ACLManager.setWriteAclGroup( inTargetName, inAclName );
    }

    /**
     * Return the formatted package version string for the WmAppPlat package.
     */
    public static String getPackageVersion() {
    	if ( wmAppPlatVersion == null) {
    		String packageVersion = PARENT_PACKAGE_NAME+";9.10.0";
	    	try {
	    		Package pkg = PackageManager.getPackage(PARENT_PACKAGE_NAME);
	    		if ( pkg != null) {
	    			packageVersion = formattedPackageName(PARENT_PACKAGE_NAME, pkg.getVersion());
	    		}
	    	} catch (Exception e) { }
	    	wmAppPlatVersion = packageVersion;
    	}

    	return wmAppPlatVersion;
    }

    /**
     * Create a string that meets the expected format for a required IS package dependency as it exists in the manifest.
     */
    private static String formattedPackageName(String packageName, String rawVersion) {
    	String version = rawVersion;
    	if ( rawVersion != null) {
    		String[] pieces = rawVersion.split( "\\.");
    		StringBuilder builder = new StringBuilder();
    		for ( int idx = 0; idx < 3; idx++) {
    			if (idx>0) builder.append('.');
    			builder.append(pieces[idx].trim());
    		}
    		version = builder.toString();
    	}
    	return packageName+";"+version;
    }


    private static String getRequiredString(String key, IDataMap pipeMap) throws ServiceException {
    	String value = pipeMap.getAsString(key);
    	if ( value == null || value.length() == 0) {
    		String msg = String.format("%s is required input parameter", key);
    		throw new ServiceException(msg);
    	}
    	return value;
    }

    private static void log( String message) {
    	System.out.println(String.format(">>>[AppPlat Gateway] %s", message));
    }

    private static void setPackageFolderProtections() {
        // Execute, list, read & write ACLs
    	// package startup & shutdown
    	setAllAclTypes("applatform.pkg", ACLManager.WEBM_ADMINISTRATORS_ACL);
    	// register and unregister IS service projections of OSGi services in Designer
        setAllAclTypes("applatform.service", ACLManager.WEBM_DEVELOPERS_ACL);
        // webm taglib support permits invocation of anonymous services
        setAllAclTypes("applatform.jsp", "Anonymous" /*ACLManager.INTERNAL_ACL*/);
        setAllAclTypes("pub", "Anonymous" /*ACLManager.INTERNAL_ACL*/);
    }


    // --- <<IS-END-SHARED>> ---
}
