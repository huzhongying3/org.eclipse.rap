/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.ui.internal.servlet;

import java.io.*;
import java.text.MessageFormat;
import org.eclipse.swt.SWT;
import org.eclipse.swt.lifecycle.DisplayUtil;
import org.eclipse.swt.resources.ResourceManager;
import com.w4t.*;
import com.w4t.engine.service.*;
import com.w4t.engine.service.BrowserSurvey.IIndexTemplate;
import com.w4t.util.browser.Default;

class IndexTemplate implements IIndexTemplate {

  private final static String FOLDER
    = IndexTemplate.class.getPackage().getName().replace( '.', '/' );
  private final static String INDEX_TEMPLATE = FOLDER + "/index.html";
  
  public InputStream getTemplateStream() throws IOException {
    InputStream result = loadTemplateFile();
    InputStreamReader isr = new InputStreamReader( result );
    BufferedReader reader = new BufferedReader( isr );
    String line = reader.readLine();
    StringBuffer content = new StringBuffer();
    while( line != null ) {
      content.append( line );
      content.append( "\n" );
      line = reader.readLine();
    }
    setDummyBrowser();
    try {
      String libs = getLibraries();
      BrowserSurvey.replacePlaceholder( content, "${libraries}", libs );
      String appScript = getAppScript();
      BrowserSurvey.replacePlaceholder( content, "${appscript}", appScript );
    } finally {
      removeDummyBrowser();
    }
    return new ByteArrayInputStream( content.toString().getBytes() );
  }

  private String getAppScript() throws IOException {
    fakeWriter();
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    HtmlResponseWriter writer = stateInfo.getResponseWriter();
    writer.startElement( HTML.SCRIPT, null );
    writer.writeText( "safd", null );
    writer.clearBody();
    try {
      // TODO: [fappel] this works only as long as only one display per
      //                session is supported...
      DisplayUtil.writeAppScript( "w1" );
      String result = getContent( writer );
      return result;
    } finally {
      restoreWriter();
    }
  }
  
  private void restoreWriter() {
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    String key = IndexTemplate.class.getName();
    HtmlResponseWriter writer
      = ( HtmlResponseWriter )stateInfo.getAttribute( key );
    stateInfo.setResponseWriter( writer );
  }

  private void fakeWriter() {
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    HtmlResponseWriter original = stateInfo.getResponseWriter();
    stateInfo.setAttribute( IndexTemplate.class.getName(), original );
    HtmlResponseWriter fake = new HtmlResponseWriter();
    stateInfo.setResponseWriter( fake );
  }

  private void setDummyBrowser() {
    String id = ServiceContext.DETECTED_SESSION_BROWSER;
    ContextProvider.getSession().setAttribute( id, new Default( true ) );
  }
  
  private void removeDummyBrowser() {
    String id = ServiceContext.DETECTED_SESSION_BROWSER;
    ContextProvider.getSession().setAttribute( id, null );
  }

  private String getLibraries() throws IOException {
    fakeWriter();
    try {
      DisplayUtil.writeLibraries();
      return getContent( ContextProvider.getStateInfo().getResponseWriter() );
    } finally {
      restoreWriter();
    }
  }

  private String getContent( final HtmlResponseWriter writer ) {
    StringBuffer msg = new StringBuffer();
    for( int i = 0; i < writer.getBodySize(); i ++ ) {
      msg.append( writer.getBodyToken( i ) );
    }
    return msg.toString();
  }

  private InputStream loadTemplateFile() throws IOException {
    InputStream result = null;
    IResourceManager manager = ResourceManager.getInstance();
    ClassLoader buffer = manager.getContextLoader();
    manager.setContextLoader( EngineConfigWrapper.class.getClassLoader() );
    try {        
      result = manager.getResourceAsStream( INDEX_TEMPLATE );
      if ( result == null ) {
        String text =   "Failed to load Browser Survey HTML Page. "
                      + "Resource {0} could not be found.";
        Object[] param = new Object[]{ INDEX_TEMPLATE };
        String msg = MessageFormat.format( text, param );
        throw new IOException( msg );
      }
    } finally {
      manager.setContextLoader( buffer );          
    }
    return result;
  }

  public void registerResources() throws IOException {
    IResourceManager manager = ResourceManager.getInstance();
    ClassLoader buffer = manager.getContextLoader();
    manager.setContextLoader( SWT.class.getClassLoader() );
    try {
      manager.register( "org/eclipse/swt/widgets/display/bg.gif" );
    } finally {
      manager.setContextLoader( buffer );
    }
  }
}