/*******************************************************************************
 * Copyright (c) 2002, 2009 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rwt.internal.lifecycle;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.internal.browser.Ie6;
import org.eclipse.rwt.internal.service.RequestParams;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;

public class RenderDispose_Test extends TestCase {

  private final PreserveWidgetsPhaseListener preserveWidgetsPhaseListener
    = new PreserveWidgetsPhaseListener();

  public void testDisposeNotYetInitialized() {
    // set up the test widget hierarchy
    Display display = new Display();
    final Composite shell = new Shell( display , SWT.NONE );
    String displayId = DisplayUtil.getAdapter( display ).getId();
    // first rendering: html document that contains the javaScript 'application'
    RWTLifeCycle lifeCycle = ( RWTLifeCycle )LifeCycleFactory.getLifeCycle();
    RWTFixture.executeLifeCycleFromServerThread( );
    // second rendering: initial markup that constructs the above created
    // widget hierarchy (display, shell and button)
    RWTFixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    RWTFixture.executeLifeCycleFromServerThread( );
    // create and dispose of the button
    RWTFixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    lifeCycle.addPhaseListener( new PhaseListener() {

      private static final long serialVersionUID = 1L;

      public void beforePhase( final PhaseEvent event ) {
        Button button = new Button( shell, SWT.PUSH );
        button.dispose();
      }

      public void afterPhase( final PhaseEvent event ) {
      }

      public PhaseId getPhaseId() {
        return PhaseId.RENDER;
      }
    } );
    RWTFixture.executeLifeCycleFromServerThread( );
    String expectedStart
      =   "var req = org.eclipse.swt.Request.getInstance();"
        + "req.setRequestCounter(";
    String allMarkup = Fixture.getAllMarkup();
    assertTrue( allMarkup.startsWith( expectedStart ) );
  }

  protected void setUp() throws Exception {
    RWTFixture.setUp();
    Fixture.fakeResponseWriter();
    Fixture.fakeBrowser( new Ie6( true, true ) );
    PhaseListenerRegistry.add( preserveWidgetsPhaseListener );
  }

  protected void tearDown() throws Exception {
    PhaseListenerRegistry.clear();
    RWTFixture.tearDown();
  }
}
