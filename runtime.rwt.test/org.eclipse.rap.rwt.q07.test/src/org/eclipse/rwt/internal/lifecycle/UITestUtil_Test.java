/*******************************************************************************
 * Copyright (c) 2007, 2009 Innoopract Informationssysteme GmbH.
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

import java.io.*;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.internal.service.RequestParams;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;


public class UITestUtil_Test extends TestCase {
  
  public void testIsValidId() {
    // Test with legal id
    assertTrue( UITestUtil.isValidId( "customId" ) );
    // Test with illegal id's
    assertFalse( UITestUtil.isValidId( null ) );
    assertFalse( UITestUtil.isValidId( "" ) );
    assertFalse( UITestUtil.isValidId( "1" ) );
    assertFalse( UITestUtil.isValidId( "$A" ) );
    assertFalse( UITestUtil.isValidId( "A$" ) );
    assertFalse( UITestUtil.isValidId( "A&B" ) );
    assertFalse( UITestUtil.isValidId( "A/8" ) );
  }
  
  public void testOverrideId() {
    Display display = new Display();
    Widget widget = new Shell( display );
    String customId = "customId";
    String generatedId = WidgetUtil.getId( widget );
    // ensure that generated id remains unchanged when UITests are disabled
    widget.setData( WidgetUtil.CUSTOM_WIDGET_ID, customId );
    assertEquals( generatedId, WidgetUtil.getId( widget ) );
    // ensure that custom id is taken into account when UITests are enabled
    UITestUtil.enabled = true;
    assertEquals( customId, WidgetUtil.getId( widget ) );
  }

  public void testWriteIds() throws IOException {
    UITestUtil.enabled = true;
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    String displayId = DisplayUtil.getId( display );
    // Request with not yet initialized widgets
    RWTFixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    RWTFixture.executeLifeCycleFromServerThread( );
    String markup = Fixture.getAllMarkup();
    assertTrue( markup.indexOf( "setHtmlId" ) != -1 );
    
    // Request with already initialized widgets
    RWTFixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    RWTFixture.executeLifeCycleFromServerThread( );
    markup = Fixture.getAllMarkup();
    assertTrue( markup.indexOf( "setHtmlId" ) == -1 );
    
    // Request with invalid id
    RWTFixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Label label = new Label( shell, SWT.NONE );
    label.setData( WidgetUtil.CUSTOM_WIDGET_ID, "a/8" );
    AbstractWidgetLCA lca = WidgetUtil.getLCA( label );
    try {
      lca.render( label );
      fail( "widget id contains illegal characters" );
    } catch( final IllegalArgumentException iae ) {
    }
  }
  
  public void testGetIdAfterDispose() {
    // set up test scenario
    UITestUtil.enabled = true;
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    // ensure that the overridden id is available after the widget was disposed
    // of - needed by render phase
    RWTFixture.fakePhase( PhaseId.PROCESS_ACTION );
    shell.setData( WidgetUtil.CUSTOM_WIDGET_ID, "customId" );
    assertEquals( "customId", WidgetUtil.getId( shell ) );
    shell.dispose();
    assertEquals( "customId", WidgetUtil.getId( shell ) );
  }
  
  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
    UITestUtil.enabled = false;
  }
}
