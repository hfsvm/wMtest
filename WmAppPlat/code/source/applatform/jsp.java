package applatform;


import java.io.Writer;

// -----( IS Java Code Template v1.2

// --- <<IS-START-IMPORTS>> ---
import com.softwareag.applatform.pls.is.web.jsp.JSPIfVarToken;
import com.softwareag.applatform.pls.is.web.jsp.JSPInvokeToken;
import com.softwareag.applatform.pls.is.web.jsp.JSPLoopToken;
import com.softwareag.applatform.pls.is.web.jsp.JSPManager;
import com.softwareag.applatform.pls.is.web.jsp.JSPScopeToken;
import com.softwareag.applatform.pls.is.web.jsp.JSPSwitchToken;
import com.softwareag.util.IDataMap;
import com.wm.app.b2b.server.InvokeState;
import com.wm.app.b2b.server.Server;
import com.wm.app.b2b.server.ServerClassLoader;
import com.wm.app.b2b.server.ServiceException;
import com.wm.app.b2b.server.User;
import com.wm.app.b2b.server.UserManager;
import com.wm.data.*;
import com.wm.lang.ns.NSName;
import com.wm.util.template.*;
// --- <<IS-END-IMPORTS>> ---




public final class jsp

{
	// ---( internal utility methods )---

	final static jsp _instance = new jsp();

	static jsp _newInstance() { return new jsp(); }

	static jsp _cast(Object o) { return (jsp)o; }

	// ---( server methods )---



	public static final void cleanupSession( IData pipeline)
		throws ServiceException
	{
		// --- <<IS-START(cleanupSession)>> ---
		// @sigtype java 3.5
		// [i] field:0:optional debug
	    // [i] field:0:required sessionId
		IDataMap pipeMap = new IDataMap(pipeline);
		boolean debug = getDebug( pipeMap );		
		String sessionId = getSessionId( pipeMap );
		
		boolean reporterRemoved = JSPManager.getInstance().removeReporter( sessionId );
		boolean tokensRemoved = JSPManager.getInstance().removeTemplateTokens( sessionId );
		if ( debug) {
			System.err.println(String.format("\n[%s]:%s", "cleanupSession", new java.util.Date()));			
			System.err.println(String.format("SessionId: %s, Reporter removed? %s, Template Token removed? %s", 
					sessionId, reporterRemoved, tokensRemoved));			
		}
		
		// --- <<IS-END>> ---
		
	}
	public static final void invokeTag (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(invokeTag)>> ---
		// @sigtype java 3.5
		// [i] field:0:optional debug
	    // [i] field:0:required sessionId
		// [i] field:0:required pageName
		// [i] field:0:required threadName
        // [i] field:0:required username
		// [i] field:0:required fullSvcName
		// [i] field:0:required newPipe
		// [i] field:0:required writer
		// [i] field:0:required action
		// [o] field:0:required error
		// [o] field:0:required errorMessage
    	

		IDataMap pipeMap = new IDataMap(pipeline);				
		IData request = pipeline;
		
		boolean debug = getDebug( pipeMap );		
		String sessionId = getSessionId( pipeMap );
		String pageName = getPageName( pipeMap );
		String threadName = getServiceThread( pipeMap );
		String action = getAction( pipeMap );
		String tagSvcName = getTagSvcName(pipeMap);		
		
		String username = pipeMap.getAsString("username"); //Constants.KEY_PIPELINE_USERNAME
		String fullSvcName = pipeMap.getAsString("fullSvcName");//Constants.KEY_PIPELINE_SERVICENAME
		IData newPipe = pipeMap.getAsIData("newPipe");
		if (newPipe == null) {
			IDataMap m = new IDataMap();
			newPipe = m.getIData();
		}
		if ( fullSvcName == null || fullSvcName.length() == 0 ) {
		    Server.logError((String)null, "serviceInterface/Name inputs are required");
		    throw new ServiceException("serviceInterface/Name inputs are required for invokeTagService");
		}
		
		Reporter reporter = JSPManager.getInstance().getReporter(sessionId, threadName);
		if ( reporter == null ) {
			reporter = JSPManager.getInstance().createReporter(sessionId, threadName, request);
		}			
		reporter.setWriter( getWriter( pipeMap ) );

		if ( debug ) {
			System.err.println(String.format("\n[%s]:%s", tagSvcName, new java.util.Date()));
			System.err.println(String.format("fullSvcName: %s", fullSvcName));
			//System.err.println(String.format("Reporter: %s...%s", reporter, reporter.debug()));
			System.err.println(String.format("SessionId: %s", sessionId));
			System.err.println(String.format("pageName: %s", pageName));
			System.err.println(String.format("serviceThread: %s", threadName));
			System.err.println(String.format("action: %s", action));
			System.err.println(String.format("Vars: username=%s, fullSvcName=%s, newPipe=%s", username, fullSvcName, newPipe ));
			System.err.println(String.format("currentToken: %s", JSPManager.getInstance().getTemplateToken(sessionId, getTokenKey(tagSvcName,threadName))));
		}
		   
		
		 
		JSPInvokeToken token = null;
		
		if ( "start".equals(action)) {
		
			token = new JSPInvokeToken(NSName.create(fullSvcName));
			
			// set up the InvokeState
			InvokeState state = initializeInvokeState();
			if ( InvokeState.getCurrentUser() == null) {
			
				User user = UserManager.getUser(username);
				InvokeState.setCurrentUser( user );
			}
			
		
			// evaluate the token
			// the service invocation happens here
			token.eval(reporter);
			JSPManager.getInstance().updateToken( sessionId, getTokenKey(tagSvcName,threadName), token);
								
			if ( token.isError()) {
				pipeMap.put("$error", true); // Constants.IS_ERROR_P
				pipeMap.put("$errorMessage", token.getErrorMessage()); // Constants.ERROR_MESSAGE_P
			}
			    
		} else if ( "end".equals(action)) {
			token = (JSPInvokeToken)JSPManager.getInstance().getTemplateToken(sessionId, getTokenKey(tagSvcName,threadName));
			token.end(reporter);

			if ( token.isError()) {
				pipeMap.put("$error", true); // Constants.IS_ERROR_P
				pipeMap.put("$errorMessage", token.getErrorMessage()); // Constants.ERROR_MESSAGE_P
			}

		}		
					
		// --- <<IS-END>> ---              
	}
	
	public static final void valueTag (IData pipeline)
	        throws ServiceException
	{
		
		// --- <<IS-START(valueTag)>> ---
		// @sigtype java 3.5
		// [i] field:0:optional debug
	    // [i] field:0:required sessionId
		// [i] field:0:required pageName
		// [i] field:0:required threadName
		// [i] field:0:required action
		// [i] field:0:required name
		// [i] field:0:required index
		// [i] field:0:required nullval
		// [i] field:0:required emptyval
		// [i] field:0:required encode
		// [i] field:0:required decode
		// [i] field:0:required decshift
		// [i] field:0:required decshow
		// [i] field:0:required var
    	// [i] field:0:required isStruct

		IDataMap pipeMap = new IDataMap(pipeline);				
		IData request = pipeline;

		boolean debug = getDebug( pipeMap );		
		String sessionId = getSessionId( pipeMap );
		String pageName = getPageName( pipeMap );
		String threadName = getServiceThread( pipeMap );
		String action = getAction( pipeMap );
		String tagSvcName = getTagSvcName(pipeMap);

		String name = pipeMap.getAsString( "name" ); // Constants.NAME_P
		int index = pipeMap.getAsInteger( "index" ); // Constants.INDEX_P
		String nullval = pipeMap.getAsString( "nullval" ); // Constants.NULL_VALUE_P
		String emptyval = pipeMap.getAsString( "emptyval" ); // Constants.EMPTY_VALUE_P
		int wmEncode = getEncode( pipeMap.getAsString( "encode") ); // Constants.WM_ENCODE_P
		int wmDecode = getDecode( pipeMap.getAsString( "decode") ); // Constants.WM_DECODE_P
		int decshift = pipeMap.getAsInteger( "decshift" ); // Constants.DEC_SHIFT_P
		int decshow = pipeMap.getAsInteger( "decshow" ); // Constants.DEC_SHOW_P
		String loopVar = pipeMap.getAsString( "var" ); // Constants.VARIABLE_P 
		boolean isLoopStruct = pipeMap.getAsBoolean( "isStruct" ); // Constants.STRUCT_P		
		
		ValueToken token = null;
		
		Reporter reporter = JSPManager.getInstance().getReporter( sessionId, threadName);
		if ( reporter == null ) {
			reporter = JSPManager.getInstance().createReporter(sessionId, threadName, request);
		}			
		reporter.setWriter( getWriter( pipeMap ) );

		if ( debug ) {
			System.err.println(String.format("\n[%s]:%s", tagSvcName, new java.util.Date()));
			//System.err.println(String.format("Reporter: %s...%s", reporter, reporter.debug()));
			System.err.println(String.format("SessionId: %s", sessionId));
			System.err.println(String.format("pageName: %s", pageName));
			System.err.println(String.format("serviceThread: %s", threadName));
			System.err.println(String.format("action: %s", action));
			System.err.println(String.format(
					"Vars: name=%s, index=%s, nullval=%s, emptyval=%s, encode=%s, decode=%s, decshift=%s, decshow=%s, loopVar=%s, isLoopStruct=%s", 
					 name, index, nullval, emptyval, wmEncode, wmDecode, decshift, decshow, loopVar, isLoopStruct ));
			System.err.println(String.format("currentToken: %s", JSPManager.getInstance().getTemplateToken(sessionId, getTokenKey(tagSvcName,threadName))));
		}

		if ( "start".equals(action )) {
			
		} else if ( "end".equals(action)) {
			token = new ValueToken(name, index, nullval, emptyval, wmEncode, decshift, decshow);	
			
			
			if ( loopVar != null ) {
				// figure out where to get encoding from 
				// usawco Note: the existing WmTomcat code also passed null. 
				// It looked like this parameter is no longer used in this method call, TODO - double-check
				token.evalLoopValue(reporter, null, loopVar, isLoopStruct);
				
			} else {
				token.eval(reporter);
			}			
			JSPManager.getInstance().updateToken( sessionId, getTokenKey(tagSvcName,threadName), token);
		}
		// --- <<IS-END>> ---           
	}
	
	public static final void switchTag (IData pipeline)
	        throws ServiceException
	{
		
		// --- <<IS-START(switchTag)>> ---
		// @sigtype java 3.5
		// [i] field:0:optional debug
	    // [i] field:0:required sessionId
		// [i] field:0:required pageName
		// [i] field:0:required threadName
		// [i] field:0:required action
		// [i] field:0:required variable
		
		IDataMap pipeMap = new IDataMap(pipeline);				
		IData request = pipeline;

		boolean debug = getDebug( pipeMap );		
		String sessionId = getSessionId( pipeMap );
		String pageName = getPageName( pipeMap );
		String threadName = getServiceThread( pipeMap );
		String action = getAction( pipeMap );
		String tagSvcName = getTagSvcName(pipeMap);
		
		String var = pipeMap.getAsString( "var" ); // Constants.VARIABLE_P 
		
		

		JSPSwitchToken token = null;

		Reporter reporter = JSPManager.getInstance().getReporter(sessionId, threadName);
		if ( reporter == null ) {
			reporter = JSPManager.getInstance().createReporter(sessionId, threadName, request);
		}			
		reporter.setWriter( getWriter( pipeMap ) );

		if ( debug ) {
			System.err.println(String.format("\n[%s]:%s", "switchTag", new java.util.Date()));
			//System.err.println(String.format("Reporter: %s...%s", reporter, reporter.debug()));
			System.err.println(String.format("SessionId: %s", sessionId));
			System.err.println(String.format("pageName: %s", pageName));
			System.err.println(String.format("serviceThread: %s", threadName));
			System.err.println(String.format("action: %s", action));
			System.err.println(String.format("Vars: var=%s", var ));
			System.err.println(String.format("currentToken: %s", JSPManager.getInstance().getTemplateToken(sessionId, getTokenKey(tagSvcName,threadName))));
		}

		if ( "start".equals(action)) {
			token = new JSPSwitchToken(var);
			token.eval(reporter);
			JSPManager.getInstance().updateToken( sessionId, getTokenKey(tagSvcName,threadName), token);
			token.getMatch(reporter, var);
			
		} else {
			
		}

		// --- <<IS-END>> ---
	}
	
	public static final void caseTag (IData pipeline)
	        throws ServiceException
	{
		
		// --- <<IS-START(caseTag)>> ---
		// @sigtype java 3.5
		// [i] field:0:optional debug
	    // [i] field:0:required sessionId
		// [i] field:0:required pageName
		// [i] field:0:required threadName
		// [i] field:0:required action
		// [i] field:0:required value
		
		IDataMap pipeMap = new IDataMap(pipeline);				
		IData request = pipeline;

		boolean debug = getDebug( pipeMap );		
		String sessionId = getSessionId( pipeMap );
		String pageName = getPageName( pipeMap );
		String threadName = getServiceThread( pipeMap );
		String action = getAction( pipeMap );
		String tagSvcName = getTagSvcName(pipeMap);
		
		String value = pipeMap.getAsString( "val" ); // Constants.VALUE_P 
		Reporter reporter = JSPManager.getInstance().getReporter( sessionId, threadName );	
		
		if ( reporter == null ) {
			throw new ServiceException("Missing reporter in case tag! No switch tag in jsp page?");
		}			
		reporter.setWriter( getWriter( pipeMap ) );
		TemplateToken token = JSPManager.getInstance().getTemplateToken( sessionId, tagSvcName );
		
		if ( debug ) {
			System.err.println(String.format("\n[%s]:%s", tagSvcName, new java.util.Date()));
			//System.err.println(String.format("Reporter: %s...%s", reporter, reporter.debug()));
			System.err.println(String.format("SessionId: %s", sessionId));
			System.err.println(String.format("pageName: %s", pageName));
			System.err.println(String.format("serviceThread: %s", threadName));
			System.err.println(String.format("action: %s", action));
			System.err.println(String.format("Vars: val=%s", value ));
			System.err.println(String.format("currentToken: %s", JSPManager.getInstance().getTemplateToken(sessionId, tagSvcName)));
		}
		
		
		if ( token == null) {
			throw new ServiceException("Missing switch token for switch tag!");
		}
		
		if ( !(token instanceof SwitchToken)) {
			throw new ServiceException("Parent tag is not a switch token!");
		}
		
		
		if ( "start".equals(action)) {
			boolean matches = ((SwitchToken)token).getMatch( reporter, value);
			pipeMap.put( "matches", matches); //Constants.MATCHES_P
		} else {
			
		}
		
		// --- <<IS-END>> ---
	}
	
	public static final void ifVarTag (IData pipeline)
	        throws ServiceException
	{
		
		// --- <<IS-START(ifVarTag)>> ---
		// @sigtype java 3.5
		// [i] field:0:optional debug
	    // [i] field:0:required sessionId
		// [i] field:0:required pageName
		// [i] field:0:required threadName
		// [i] field:0:required action
		// [i] field:0:required var
		// [i] field:0:required equals
		// [i] field:0:required vequals
		// [i] field:0:required isNull
		// [i] field:0:required notEmpty
		// [i] field:0:required matches

		IDataMap pipeMap = new IDataMap(pipeline);				
		IData request = pipeline;

		boolean debug = getDebug( pipeMap );		
		String sessionId = getSessionId( pipeMap );
		String pageName = getPageName( pipeMap );
		String threadName = getServiceThread( pipeMap );
		String action = getAction( pipeMap );
		String tagSvcName = getTagSvcName(pipeMap);
		
		String variable = pipeMap.getAsString( "var" ); // Constants.VARIABLE_P 
		String equals = pipeMap.getAsString( "equals" ); // Constants.EQUALS_P 
		String vequals = pipeMap.getAsString( "vequals" ); // Constants.VEQUALS_P 
		String matches = pipeMap.getAsString( "matches" ); // Constants.MATCHES_P 
		boolean isNull = pipeMap.getAsBoolean( "isNull" ); // Constants.IS_NULL_P
		boolean notEmpty = pipeMap.getAsBoolean( "notEmpty" ); // Constants.NOT_EMPTY_P 
		
		JSPIfVarToken token = null;
		Reporter reporter = JSPManager.getInstance().getReporter(sessionId, threadName );
		if ( reporter == null ) {
			reporter = JSPManager.getInstance().createReporter(sessionId, threadName, request);
		}			
		reporter.setWriter( getWriter( pipeMap ) );
		
		if ( debug ) {
			System.err.println(String.format("\n[%s]:%s", tagSvcName, new java.util.Date()));
			//System.err.println(String.format("Reporter: %s....%s", reporter, reporter.debug()));
			System.err.println(String.format("SessionId: %s", sessionId));
			System.err.println(String.format("pageName: %s", pageName));
			System.err.println(String.format("serviceThread: %s", threadName));
			System.err.println(String.format("action: %s", action));
			System.err.println(String.format("Vars: var=%s, equals=%s, vequals=%s, matches=%s, isNull=%s, notEmpty=%s", 
					variable, equals, vequals, matches, isNull, notEmpty ));
			System.err.println(String.format("currentToken: %s", JSPManager.getInstance().getTemplateToken( sessionId, getTokenKey(tagSvcName,threadName) )));
		}
		

		if ( "start".equals(action)) {
			
			token = new JSPIfVarToken( variable, isNull, notEmpty, equals, vequals, matches );			
			token.eval(reporter);
			JSPManager.getInstance().updateToken( sessionId, getTokenKey(tagSvcName,threadName), token);
			
			pipeMap.put( "result", token.getResult() ); // Constants.RESULT_P

			
		} else {
			
			token = (JSPIfVarToken)JSPManager.getInstance().getTemplateToken(sessionId, getTokenKey(tagSvcName,threadName));
			token.end( reporter );

			
		}
		// --- <<IS-END>> ---
	}

	public static final void loopSepTag (IData pipeline)
	        throws ServiceException
	{
		
		// --- <<IS-START(loopSepTag)>> ---
		// @sigtype java 3.5
		// [i] field:0:optional debug
	    // [i] field:0:required sessionId
		// [i] field:0:required pageName
		// [i] field:0:required threadName
		// [i] field:0:required action
		// [i] field:0:required sepString

		IDataMap pipeMap = new IDataMap(pipeline);				
		IData request = pipeline;

		boolean debug = getDebug( pipeMap );		
		String sessionId = getSessionId( pipeMap );
		String pageName = getPageName( pipeMap );
		String threadName = getServiceThread( pipeMap );
		String action = getAction( pipeMap );
		String tagSvcName = getTagSvcName(pipeMap);
		
		String loopSep = pipeMap.getAsString( "sepString" ); // Constants.SEP_STRING_P

		Reporter reporter = JSPManager.getInstance().getReporter(sessionId, threadName);
		if ( reporter == null ) {
			reporter = JSPManager.getInstance().createReporter(sessionId, threadName, request);
		}			
		reporter.setWriter( getWriter( pipeMap ) );

		if ( debug ) {
			System.err.println(String.format("\n[%s]:%s", tagSvcName, new java.util.Date()));
			//System.err.println(String.format("Reporter: %s...%s", reporter, reporter.debug()));
			System.err.println(String.format("SessionId: %s", sessionId));
			System.err.println(String.format("pageName: %s", pageName));			
			System.err.println(String.format("serviceThread: %s", threadName));
			System.err.println(String.format("action: %s", action));
			System.err.println(String.format("Vars: sepString=%s", loopSep ));
			System.err.println(String.format("currentToken: %s", JSPManager.getInstance().getTemplateToken(sessionId, getTokenKey(tagSvcName,threadName))));
		}

		if ( "start".equals(action)) {
			
		} else {

			if ( loopSep != null) {
				LoopSepToken lspToken = new LoopSepToken();
				lspToken.setSepString(loopSep);	
				lspToken.eval(reporter);
				JSPManager.getInstance().updateToken( sessionId, getTokenKey(tagSvcName,threadName), lspToken);
			}
		}
		// --- <<IS-END>> ---

	}


	public static final void loopTag (IData pipeline)
	        throws ServiceException
	{
		
		// --- <<IS-START(loopTag)>> ---
		// @sigtype java 3.5
		// [i] field:0:optional debug
	    // [i] field:0:required sessionId
		// [i] field:0:required pageName
		// [i] field:0:required threadName
		// [i] field:0:required action
		// [i] field:0:required variable
		// [i] field:0:required struct
		// [i] field:0:required xpriv

		IDataMap pipeMap = new IDataMap(pipeline);				
		IData request = pipeline;

		boolean debug = getDebug( pipeMap );		
		String sessionId = getSessionId( pipeMap );
		String pageName = getPageName( pipeMap );
		String threadName = getServiceThread( pipeMap );
		String action = getAction( pipeMap );
		String tagSvcName = getTagSvcName(pipeMap);

		String var = pipeMap.getAsString( "var" ); // Constants.VARIABLE_P 
		boolean isStruct = pipeMap.getAsBoolean( "struct" ); // Constants.IS_STRUCT_P
		boolean isXPriv = pipeMap.getAsBoolean( "xpriv" ); // Constants.IS_XPRIV_P
		
		Reporter reporter = null;
		reporter = JSPManager.getInstance().getReporter(sessionId, threadName);
		JSPLoopToken token = null;
		if ( reporter == null ) {
			reporter = JSPManager.getInstance().createReporter(sessionId, threadName, request);
		}
		reporter.setWriter( getWriter( pipeMap ) );						
		
		if ( debug ) {
			System.err.println(String.format("\n[%s]:%s",tagSvcName, new java.util.Date()));
			//System.err.println(String.format("Reporter: %s...%s", reporter, reporter.debug()));
			System.err.println(String.format("SessionId: %s", sessionId));
			System.err.println(String.format("pageName: %s", pageName));
			System.err.println(String.format("serviceThread: %s", threadName));
			System.err.println(String.format("action: %s", action));
			System.err.println(String.format("Vars: var=%s, struct=%s, xpriv=%s", var, isStruct, isXPriv ));
			System.err.println(String.format("currentToken: %s", JSPManager.getInstance().getTemplateToken(sessionId,getTokenKey(tagSvcName,threadName))));
		}
		if ( "start".equals(action)) {
			
			token = new JSPLoopToken(var, isStruct, isXPriv);
			token.eval(reporter);
			JSPManager.getInstance().updateToken( sessionId, getTokenKey(tagSvcName, threadName), token);

			
		} else if ( "end".equals(action)) {
			
			token = (JSPLoopToken)JSPManager.getInstance().getTemplateToken(sessionId, getTokenKey(tagSvcName, threadName));
			
			token.step(reporter);
			token.end(reporter);
			boolean isEmpty = token.isEmpty();
			pipeMap.put( "empty", isEmpty); // Constants.IS_EMPTY_P
			
		} else { //after body

			token = (JSPLoopToken)JSPManager.getInstance().getTemplateToken(sessionId, getTokenKey(tagSvcName, threadName));
			boolean hasMoreElements = token.hasMoreElements();
			if ( hasMoreElements) {
				token.end(reporter);
				token.step(reporter);				
			}
			pipeMap.put( "moreElements", hasMoreElements); // Constants.HAS_MORE_ELEMENTS_P
		}
		// --- <<IS-END>> ---
	}
	

	public static final void newLineTag (IData pipeline)
	        throws ServiceException
	{
		
		// --- <<IS-START(newLineTag)>> ---
		// @sigtype java 3.5
		// [i] field:0:optional debug
	    // [i] field:0:required sessionId
		// [i] field:0:required pageName
		// [i] field:0:required threadName
		// [i] field:0:required action
		// [i] field:0:required name

		IDataMap pipeMap = new IDataMap(pipeline);				
		IData request = pipeline;

		boolean debug = getDebug( pipeMap );		
		String sessionId = getSessionId( pipeMap );
		String pageName = getPageName( pipeMap );
		String threadName = getServiceThread( pipeMap );
		String action = getAction( pipeMap );
		String tagSvcName = getTagSvcName(pipeMap);
		
		String name = pipeMap.getAsString( "name" ); // Constants.NAME_P
		
		Reporter reporter = JSPManager.getInstance().getReporter(sessionId, threadName);
		if ( reporter == null ) {
			reporter = JSPManager.getInstance().createReporter(sessionId, threadName, request);
		}			
		reporter.setWriter( getWriter( pipeMap ) );

		if ( debug ) {
			System.err.println(String.format("\n[%s]:%s", tagSvcName, new java.util.Date()));
			//System.err.println(String.format("Reporter: %s...%s", reporter, reporter.debug()));
			System.err.println(String.format("SessionId: %s", sessionId));
			System.err.println(String.format("pageName: %s", pageName));
			System.err.println(String.format("serviceThread: %s", threadName));
			System.err.println(String.format("action: %s", action));
			System.err.println(String.format("Vars: name=%s", name ));
			System.err.println(String.format("currentToken: %s", JSPManager.getInstance().getTemplateToken(sessionId, getTokenKey(tagSvcName, threadName)) ));
		}
		
		if ( "start".equals(action)) {
			
		} else {
			
			NewlineToken token = new NewlineToken("");
			token.eval( reporter );
			JSPManager.getInstance().updateToken( sessionId, getTokenKey(tagSvcName, threadName), token);

		}
		// --- <<IS-END>> ---

	}

	
	public static final void renameTag (IData pipeline)
	        throws ServiceException
	{
		
		// --- <<IS-START(renameTag)>> ---
		// @sigtype java 3.5
		// [i] field:0:optional debug
	    // [i] field:0:required sessionId
		// [i] field:0:required pageName
		// [i] field:0:required threadName
		// [i] field:0:required action
		// [i] field:0:required souce
		// [i] field:0:required target
		// [i] field:0:required copy

		IDataMap pipeMap = new IDataMap(pipeline);				
		IData request = pipeline;

		boolean debug = getDebug( pipeMap );		
		String sessionId = getSessionId( pipeMap );
		String pageName = getPageName( pipeMap );
		String threadName = getServiceThread( pipeMap );
		String action = getAction( pipeMap );
		String tagSvcName = getTagSvcName(pipeMap);
		
		String source = pipeMap.getAsString( "sourceVar" ); // Constants.SOURCE_VARIABLE_P
		String target = pipeMap.getAsString( "targetVar" ); // Constants.TARGET_VARIABLE_P
		boolean copy = pipeMap.getAsBoolean( "copy" ); // Constants.COPY_P		

		
		RenameToken token = null;
		
		Reporter reporter = JSPManager.getInstance().getReporter(sessionId, threadName);
		if ( reporter == null ) {
			reporter = JSPManager.getInstance().createReporter(sessionId, threadName, request);
		}			
		reporter.setWriter( getWriter( pipeMap ) );

		if ( debug ) {
			System.err.println(String.format("\n[%s]:%s", tagSvcName, new java.util.Date()));
			//System.err.println(String.format("Reporter: %s...%s", reporter, reporter.debug()));
			System.err.println(String.format("SessionId: %s", sessionId));
			System.err.println(String.format("pageName: %s", pageName));
			System.err.println(String.format("serviceThread: %s", threadName));
			System.err.println(String.format("action: %s", action));
			System.err.println(String.format("Vars: sourceVar=%s, targetVar=%s, copy=%s", source, target, copy ));
			System.err.println(String.format("currentToken: %s", JSPManager.getInstance().getTemplateToken(sessionId, getTokenKey(tagSvcName, threadName))));
		}


		if ( "start".equals(action)) {
			
		} else {
			token = new RenameToken(source, target, copy);
			token.eval(reporter);
			JSPManager.getInstance().updateToken( sessionId, getTokenKey(tagSvcName, threadName), token);

		}
		// --- <<IS-END>> ---
	}

	public static final void scopeTag (IData pipeline)
	        throws ServiceException
	{
		
		// --- <<IS-START(scopeTag)>> ---
		// @sigtype java 3.5
		// [i] field:0:optional debug
	    // [i] field:0:required sessionId
		// [i] field:0:required pageName
		// [i] field:0:required threadName
		// [i] field:0:required action
		// [i] field:0:required recordName
		// [i] field:0:required options
		// [0] field:0:required valid

		IDataMap pipeMap = new IDataMap(pipeline);				
		IData request = pipeline;

		boolean debug = getDebug( pipeMap );		
		String sessionId = getSessionId( pipeMap );
		String pageName = getPageName( pipeMap );
		String threadName = getServiceThread( pipeMap );
		String action = getAction( pipeMap );
		String tagSvcName = getTagSvcName(pipeMap);

		String recordName = pipeMap.getAsString( "recordName" ); // Constants.RECORD_P
		String options = pipeMap.getAsString( "options" ); // Constants.OPTIONS_P

		JSPScopeToken token = null;
		
		Reporter reporter = JSPManager.getInstance().getReporter(sessionId, threadName);
		if ( reporter == null ) {
			reporter = JSPManager.getInstance().createReporter(sessionId, threadName, request);
		}			
		reporter.setWriter( getWriter( pipeMap ) );

		if ( debug ) {
			System.err.println(String.format("\n[%s]:%s", tagSvcName, new java.util.Date()));
			//System.err.println(String.format("Reporter: %s...%s", reporter, reporter.debug()));
			System.err.println(String.format("SessionId: %s", sessionId));
			System.err.println(String.format("pageName: %s", pageName));
			System.err.println(String.format("serviceThread: %s", threadName));
			System.err.println(String.format("action: %s", action));
			System.err.println(String.format("Vars: recordName=%s, options=%s", recordName, options ));
			System.err.println(String.format("currentToken: %s", JSPManager.getInstance().getTemplateToken(sessionId, getTokenKey(tagSvcName,threadName))));
		}

		

		if ( "start".equals(action)) {
			token = new JSPScopeToken( recordName, options );
			token.eval(reporter);
			JSPManager.getInstance().updateToken( sessionId, getTokenKey(tagSvcName, threadName), token);
			pipeMap.put( "valid", token.isValid() ); // Constants.VALID_P

		} else {
			token = (JSPScopeToken)JSPManager.getInstance().getTemplateToken(sessionId, getTokenKey(tagSvcName, threadName));
			token.end(reporter);
		}
		
		// --- <<IS-END>> ---
	}

	public static final void sysVarTag (IData pipeline)
	        throws ServiceException
	{
		
		// --- <<IS-START(sysVarTag)>> ---
		// @sigtype java 3.5
		// [i] field:0:optional debug
	    // [i] field:0:required sessionId
		// [i] field:0:required pageName
		// [i] field:0:required threadName
		// [i] field:0:required action
		// [i] field:0:required variable

		IDataMap pipeMap = new IDataMap(pipeline);				
		IData request = pipeline;

		boolean debug = getDebug( pipeMap );		
		String sessionId = getSessionId( pipeMap );
		String pageName = getPageName( pipeMap );
		String threadName = getServiceThread( pipeMap );
		String action = getAction( pipeMap );
		String tagSvcName = getTagSvcName(pipeMap);

		SysvarToken token = null;
		
		Reporter reporter = JSPManager.getInstance().getReporter(sessionId, threadName);
		if ( reporter == null ) {
			reporter = JSPManager.getInstance().createReporter(sessionId, threadName, request);
		}			
		reporter.setWriter( getWriter( pipeMap ) );

		if ( debug ) {
			System.err.println(String.format("\n[%s]:%s", tagSvcName, new java.util.Date()));
			//System.err.println(String.format("Reporter: %s...%s", reporter, reporter.debug()));
			System.err.println(String.format("SessionId: %s", sessionId));
			System.err.println(String.format("pageName: %s", pageName));
			System.err.println(String.format("serviceThread: %s", threadName));
			System.err.println(String.format("action: %s", action));
			System.err.println(String.format("Vars: %s", "none" ));
			System.err.println(String.format("currentToken: %s", JSPManager.getInstance().getTemplateToken(sessionId, getTokenKey(tagSvcName, threadName))));
		}
		
		if ( "start".equals(action)) {
			
		} else {
			String var = pipeMap.getAsString("var");//Constants.VARIABLE_P
						
			token = new SysvarToken(var);
			token.eval( reporter );
			
			JSPManager.getInstance().updateToken( sessionId, getTokenKey(tagSvcName, threadName), token);
			

		}
		// --- <<IS-END>> ---
	}

	public static final void usePipelineTag (IData pipeline)
	        throws ServiceException
	{
		
		// --- <<IS-START(usePipelineTag)>> ---
		// @sigtype java 3.5
		// [i] field:0:optional debug
	    // [i] field:0:required sessionId
		// [i] field:0:required pageName
		// [i] field:0:required threadName
		// [i] field:0:required action
		// [i] field:0:required name
		// [o] field:0:webm_pipe 

		IDataMap pipeMap = new IDataMap(pipeline);				
		IData request = pipeline;

		boolean debug = getDebug( pipeMap );		
		String sessionId = getSessionId( pipeMap );
		String pageName = getPageName( pipeMap );
		String threadName = getServiceThread( pipeMap );
		String action = getAction( pipeMap );
		String tagSvcName = getTagSvcName(pipeMap);

		String name = pipeMap.getAsString( "name" ); // Constants.NAME_P

		Reporter reporter = JSPManager.getInstance().getReporter(sessionId, threadName);		
		if ( reporter == null ) {
			reporter = JSPManager.getInstance().createReporter(sessionId, threadName, request);
		}			
		reporter.setWriter( getWriter( pipeMap ) );

		if ( debug ) {
			System.err.println(String.format("\n[%s]:%s", tagSvcName, new java.util.Date()));
			//System.err.println(String.format("Reporter: %s...%s", reporter, reporter.debug()));
			System.err.println(String.format("SessionId: %s", sessionId));
			System.err.println(String.format("pageName: %s", pageName));
			System.err.println(String.format("serviceThread: %s", threadName));
			System.err.println(String.format("action: %s", action));
			System.err.println(String.format("Vars: name=%s", name ));
			System.err.println(String.format("currentToken: %s", JSPManager.getInstance().getTemplateToken(sessionId, getTokenKey(tagSvcName, threadName))));
		}
		
		if ( "start".equals(action)) {
						
			pipeMap.put( "webm_pipe", reporter.getCurrent()); // Constants.WEB_PIPE_P 		
			JSPManager.getInstance().updateToken( sessionId, getTokenKey(tagSvcName, threadName), null);
			
		} else {
			
		}	

		// --- <<IS-END>> ---	
	}

		
    public static final void resolveClass (IData pipeline)
            throws ServiceException
    {
            // --- <<IS-START(resolveClass)>> ---
            // @sigtype java 3.5
            // [i] field:0:required className
       	    // [o] field:0:required clazz
            IDataMap pipeMap = new IDataMap(pipeline);
            String className = pipeMap.getAsString("className"); 

            try {
            	Class c = ServerClassLoader.locateClass(className);
                pipeMap.put("clazz", c);
            }
            catch ( Exception e ) {
                Server.logError(e);
                throw new ServiceException(e);
            }

            // --- <<IS-END>> ---

    }

	
	// --- <<IS-START-SHARED>> ---
	protected static InvokeState initializeInvokeState() {
	    		
	    InvokeState state = InvokeState.getCurrentState();
	    
	    if (state == null) {
	        
	        state = new InvokeState();
	        InvokeState.setCurrentState(state);
	    }
	    
	    return state;
	}
	
	protected static Writer getWriter(IDataMap pipeMap) {
		Writer writer = (Writer)pipeMap.get("jspWriter"); // Constants.JSP_WRITER_P
		if ( writer == null) {
		    Server.logError((String)null, "java.io.Writer from jsp page context is required for executeTagService");
		    throw new IllegalArgumentException("java.io.Writer from jsp page context is required for executeTagService");			
		}
		return writer;
	}
	
	/*
	 * Returns either  // Constants.START_ACTION or  Constants.END_ACTION
	 */
	protected static String getAction(IDataMap pipeMap) {
		String action = pipeMap.getAsString("action"); // Constants.ACTION_P
		if ( action == null) {
		    Server.logError((String)null, "action input is required");
		    throw new IllegalArgumentException("action input is required for executeTagService");
		}
		
		return action;
	}
	
	private static Boolean getDebug(IDataMap pipeMap) {
		String value = pipeMap.getAsString("debug", "false"); // Constants.DEBUG_P
		return Boolean.valueOf(value);
	}
	
	private static String getTagSvcName( IDataMap pipeMap) {
		return pipeMap.getAsString("tagService"); // Constants.TAG_SERVICE_P
	}
	private static String getSessionId(IDataMap pipeMap) {
				
		String sessionId = pipeMap.getAsString( "sessionId"); // Constants.SESSION_ID_P
		if ( sessionId == null) {
		    Server.logError((String)null, "sessionId is required");
		    throw new IllegalArgumentException("sessionId is required for executeTagService");			
		}
		return sessionId;
	}
	private static String getPageName( IDataMap pipeMap) {
		return pipeMap.getAsString("jspPageName"); // Constants.JSP_PAGE_KEY_P
	}
	
	private static String getServiceThread( IDataMap pipeMap) {
		return pipeMap.getAsString("serviceThread"); // Constants.SERVICE_THREAD_P
	}
	
	public static int getEncode( String value) {
		
		int wmEncoding = ValueToken.NONE;
		
		if ( value != null) {
			if (value.equals(ValueToken.ENCODING_NAME[ValueToken.BASE64_ENCODE])) 
			    wmEncoding = ValueToken.BASE64_ENCODE;
			
			else if (value.equals(ValueToken.ENCODING_NAME[ValueToken.B64UTF8_ENCODE])) 
			    wmEncoding = ValueToken.B64UTF8_ENCODE;
			
			else if (value.equals(ValueToken.ENCODING_NAME[ValueToken.URL_ENCODE])) 
			    wmEncoding = ValueToken.URL_ENCODE;
			
			else if (value.equals(ValueToken.ENCODING_NAME[ValueToken.XML_ATTR_ENCODE])) 
			    wmEncoding = ValueToken.XML_ATTR_ENCODE;
			
			else if (value.equals(ValueToken.ENCODING_NAME[ValueToken.XML_ENCODE])) 
			    wmEncoding = ValueToken.XML_ENCODE;
			
			else if (value.equals(ValueToken.ENCODING_NAME[ValueToken.UNICODE_ENCODE])) 
			    wmEncoding = ValueToken.UNICODE_ENCODE;
			
			else if (value.equals(ValueToken.ENCODING_NAME[ValueToken.CODE_ENCODE])) 
			    wmEncoding = ValueToken.CODE_ENCODE;
		}		
		return wmEncoding;
	}
	
	public static int getDecode(String value)
	{

		int wmEncoding = ValueToken.NONE;

		if ( value != null) {
			if (value.equals(ValueToken.ENCODING_NAME[ValueToken.BASE64_DECODE])) 
			    wmEncoding = ValueToken.BASE64_DECODE;
			
			else if (value.equals(ValueToken.ENCODING_NAME[ValueToken.B64UTF8_DECODE])) 
			    wmEncoding = ValueToken.B64UTF8_DECODE;
			
			else if (value.equals(ValueToken.ENCODING_NAME[ValueToken.URL_DECODE])) 
			    wmEncoding = ValueToken.URL_DECODE;
			
			else if (value.equals(ValueToken.ENCODING_NAME[ValueToken.XML_DECODE])) 
			    wmEncoding = ValueToken.XML_DECODE;
			
			else if (value.equals(ValueToken.ENCODING_NAME[ValueToken.HTML_DECODE])) 
			    wmEncoding = ValueToken.HTML_DECODE;
		}
		return wmEncoding;
	}
	
	
	/**
	 * Determines state of {@link Constants.DISABLE_JSP_PAGE_ISOLATION_KEY} by examining the pageScope.
	 * If false, the reporter data is isolated to each JSP page.
	 * If true, the reporter data is common to all tags running in a session.  This is the original behavior prior to 
	 * App Platform 10.5. 
	 * <p>
	 * This was added to support framesets where multiple pages invoke IS services concurrently against the same session.
	 * @param pageName
	 * 
	 */
	public static boolean isJspPageIsolationDisabled(String pageName) {
		boolean retFlag = false;
		if ( pageName != null) {
			retFlag = pageName.equals("_global_session_scope"); // Constants.JSP_PAGE_SCOPE_SESSION
		}
		return retFlag;
	}

	/**
	 * Return the key used with Tokens in the token map
	 */
	private static String getTokenKey(String tagSvcName, String threadName) {
		return (tagSvcName+"/"+threadName);
	}
	
	// same as JspUtils method
	private static String getPipeName() {
		return String.format("webm_pipe/%s", Thread.currentThread().getName());
	}
	
	// --- <<IS-END-SHARED>> ---
}

