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
package org.eclipse.rwt.service;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.Fixture.TestResponse;
import org.eclipse.rwt.Fixture.TestServletOutputStream;
import org.eclipse.rwt.internal.service.BrowserSurvey;
import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.internal.service.BrowserSurvey.IIndexTemplate;
import org.eclipse.swt.RWTFixture;



public class ServiceHandler_Test extends TestCase {
  
  private final static String HANDLER_ID 
    = "org.eclipse.rwt.service.ServiceHandler_Test.CustomHandler";
  private final static String PROGRAMATIC_HANDLER_ID 
    = "programmaticServiceHandlerId";
  private static final String SERVICE_DONE = "service done";
  private static String log = "";
  
  public static class CustomHandler implements IServiceHandler {
    public void service() throws ServletException, IOException {
      log = SERVICE_DONE;
    }    
  }
  
  protected void setUp() throws Exception {
    RWTFixture.setUp();
    Fixture.createContext( false );
  }
  
  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
    Fixture.removeContext();
    log = "";
  }
  
  public void testCustomServiceHandler() throws Exception {
    Fixture.fakeRequestParam( IServiceHandler.REQUEST_PARAM, HANDLER_ID );
    ServiceManager.getHandler().service();
    assertEquals( SERVICE_DONE, log );
  }
  
  public void testProgramaticallyRegsiteredHandler() throws Exception {
    HttpServletResponse response = ContextProvider.getResponse();
    TestResponse testResponse = ( TestResponse )response;
    testResponse.setOutputStream( new TestServletOutputStream() );
    // Register
    ServiceManager.registerServiceHandler( PROGRAMATIC_HANDLER_ID, 
                                           new CustomHandler() );
    Fixture.fakeRequestParam( IServiceHandler.REQUEST_PARAM, 
                              PROGRAMATIC_HANDLER_ID );
    ServiceManager.getHandler().service();
    assertEquals( SERVICE_DONE, log );
    // Unregister
    BrowserSurvey.indexTemplate = new IIndexTemplate() {
      public InputStream getTemplateStream() throws IOException {
        return null;
      }
      public boolean modifiedSince() {
        return false;
      }
      public void registerResources() throws IOException {
      }
    };
    log = "";
    ServiceManager.unregisterServiceHandler( PROGRAMATIC_HANDLER_ID );
    ServiceManager.getHandler().service();
    assertEquals( "", log );
    BrowserSurvey.indexTemplate = null;
  }
}
