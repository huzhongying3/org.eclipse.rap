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

package org.eclipse.swt.internal.widgets.combokit;

import java.io.IOException;
import java.util.ArrayList;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.internal.lifecycle.*;
import org.eclipse.rwt.internal.service.RequestParams;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.internal.events.ActivateAdapter;
import org.eclipse.swt.internal.events.ActivateEvent;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.widgets.*;

public class ComboLCA_Test extends TestCase {

  private static final String PROP_ITEMS = "items";
  private static final String PROP_SELECTION = "selection";

  public void testPreserveValues() {
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
    Combo combo = new Combo( shell, SWT.DEFAULT );
    RWTFixture.markInitialized( display );
    // Test preserving a combo with no items and (naturally) no selection
    RWTFixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( combo );
    String[] items = ( ( String[] )adapter.getPreserved( PROP_ITEMS ) );
    assertEquals( 0, items.length );
    assertEquals( new Integer( -1 ), adapter.getPreserved( PROP_SELECTION ) );
    assertEquals( new Integer( Combo.LIMIT ),
                  adapter.getPreserved( ComboLCA.PROP_TEXT_LIMIT ) );
    Object height = adapter.getPreserved( ComboLCA.PROP_MAX_LIST_HEIGHT );
    assertEquals( new Integer( ComboLCA.getMaxListHeight( combo ) ), height );
    Boolean hasListeners;
    hasListeners = ( Boolean )adapter.getPreserved( Props.SELECTION_LISTENERS );
    assertEquals( Boolean.FALSE, adapter.getPreserved( ComboLCA.PROP_EDITABLE ) );
    assertEquals( Boolean.FALSE, hasListeners );
    assertEquals( new Point( 0, 0 ),
                  adapter.getPreserved( ComboLCA.PROP_TEXT_SELECTION ) );
    // Test preserving combo with items were one is selected
    RWTFixture.clearPreserved();
    combo.add( "item 1" );
    combo.add( "item 2" );
    combo.select( 1 );
    SelectionListener selectionListener = new SelectionAdapter() {
    };
    combo.addSelectionListener( selectionListener );
    combo.addModifyListener( new ModifyListener(){

      public void modifyText( final ModifyEvent event ) {
      }} );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( combo );
    items = ( ( String[] )adapter.getPreserved( PROP_ITEMS ) );
    assertEquals( 2, items.length );
    assertEquals( "item 1", items[ 0 ] );
    assertEquals( "item 2", items[ 1 ] );
    assertEquals( new Integer( 1 ), adapter.getPreserved( PROP_SELECTION ) );
    height = adapter.getPreserved( ComboLCA.PROP_MAX_LIST_HEIGHT );
    assertEquals( new Integer( ComboLCA.getMaxListHeight( combo ) ), height );
    assertEquals( "item 2", adapter.getPreserved( Props.TEXT ) );
    hasListeners = ( Boolean )adapter.getPreserved( Props.SELECTION_LISTENERS );
    assertEquals( Boolean.TRUE, hasListeners );
    assertEquals( Boolean.FALSE, adapter.getPreserved( ComboLCA.PROP_EDITABLE ) );
    hasListeners
     = ( Boolean )adapter.getPreserved( ComboLCA.PROP_VERIFY_MODIFY_LISTENER );
    assertEquals( Boolean.TRUE, hasListeners );
    //control_listeners
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( combo );
    hasListeners = ( Boolean )adapter.getPreserved( Props.CONTROL_LISTENERS );
    assertEquals( Boolean.FALSE, hasListeners );
    RWTFixture.clearPreserved();
    combo.addControlListener( new ControlListener (){

      public void controlMoved( final ControlEvent e ) {
      }

      public void controlResized( final ControlEvent e ) {
      }});
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( combo );
    hasListeners = ( Boolean ) adapter.getPreserved( Props.CONTROL_LISTENERS );
    assertEquals( Boolean.TRUE, hasListeners );
    RWTFixture.clearPreserved();
    //foreground background font
    Color background = Graphics.getColor( 122, 33, 203 );
    combo.setBackground( background );
    Color foreground = Graphics.getColor( 211, 178, 211 );
    combo.setForeground( foreground );
    Font font = Graphics.getFont( "font", 12, SWT.BOLD );
    combo.setFont( font );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( combo );
    assertEquals( background, adapter.getPreserved( Props.BACKGROUND ) );
    assertEquals( foreground, adapter.getPreserved( Props.FOREGROUND ) );
    assertEquals( font, adapter.getPreserved( Props.FONT ) );
    RWTFixture.clearPreserved();
    //tooltiptext
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( combo );
    assertEquals( null, combo.getToolTipText() );
    RWTFixture.clearPreserved();
    combo.setToolTipText( "some text" );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( combo );
    assertEquals( "some text", combo.getToolTipText() );
    RWTFixture.clearPreserved();
    //tab_index
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( combo );
    assertTrue( adapter.getPreserved( Props.Z_INDEX ) != null );
    RWTFixture.clearPreserved();
    //activateListener
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( combo );
    hasListeners = (Boolean)adapter.getPreserved( Props.ACTIVATE_LISTENER );
    assertEquals( Boolean.FALSE, hasListeners );
    RWTFixture.clearPreserved();
    ActivateEvent.addListener( combo, new ActivateAdapter() {
    } );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( combo );
    hasListeners = ( Boolean ) adapter.getPreserved( Props.ACTIVATE_LISTENER );
    assertEquals( Boolean.TRUE, hasListeners );
    RWTFixture.clearPreserved();
    display.dispose();
  }

  public void testEditablePreserveValues(){
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
    Combo combo = new Combo( shell, SWT.NONE );
    RWTFixture.markInitialized( display );
    RWTFixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( combo );
    assertEquals( Boolean.TRUE , adapter.getPreserved( ComboLCA.PROP_EDITABLE ) );
    //activate_listeners   Focus_listeners
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( combo );
    Boolean focusListener
     = (Boolean)adapter.getPreserved( Props.FOCUS_LISTENER );
    assertEquals( Boolean.FALSE, focusListener );
    RWTFixture.clearPreserved();
    combo.addFocusListener( new FocusListener (){

      public void focusGained( final FocusEvent event ) {
      }

      public void focusLost( final FocusEvent event ) {
      }} );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( combo );
    Boolean hasListeners
     = ( Boolean ) adapter.getPreserved( Props.FOCUS_LISTENER );
    assertEquals( Boolean.TRUE, hasListeners );
    RWTFixture.clearPreserved();
    // textLimit
    combo.setTextLimit( 10 );
    RWTFixture.preserveWidgets();
    Integer textLimit
      = ( Integer )adapter.getPreserved( ComboLCA.PROP_TEXT_LIMIT );
    assertEquals( new Integer( 10 ), textLimit );
    display.dispose();
  }

  public void testRenderChanges() throws IOException {
    Fixture.fakeResponseWriter();
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    Combo combo = new Combo( shell, SWT.READ_ONLY );
    shell.open();
    RWTFixture.markInitialized( display );
    RWTFixture.markInitialized( combo );
    RWTFixture.clearPreserved();
    RWTFixture.preserveWidgets();
    ComboLCA comboLCA = new ComboLCA();
    combo.add( "item 1" );
    combo.add( "item 2" );
    comboLCA.renderChanges( combo );
    String expected;
    expected = "w.setItems( [ \"item 1\", \"item 2\" ] );";
    assertTrue( Fixture.getAllMarkup().endsWith( expected ) );
    Fixture.fakeResponseWriter();
    RWTFixture.clearPreserved();
    RWTFixture.preserveWidgets();
    combo.select( 1 );
    comboLCA.renderChanges( combo );
    expected = "w.select( 1 );";
    assertTrue( Fixture.getAllMarkup().endsWith( expected ) );
    Fixture.fakeResponseWriter();
    RWTFixture.clearPreserved();
    RWTFixture.preserveWidgets();
    comboLCA.renderChanges( combo );
    assertEquals( "", Fixture.getAllMarkup() );
  }

  public void testReadData() {
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
    final Combo combo = new Combo( shell, SWT.NONE );
    String comboId = WidgetUtil.getId( combo );
    // init combo items
    combo.add( "item 1" );
    combo.add( "item 2" );
    // read changed selection
    Fixture.fakeRequestParam( comboId + ".selectedItem", "1" );
    WidgetUtil.getLCA( combo ).readData( combo );
    assertEquals( 1, combo.getSelectionIndex() );
    // read changed selection and ensure that SelectionListener gets called
    final StringBuffer log = new StringBuffer();
    combo.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        assertSame( combo, event.getSource() );
        assertEquals( 0, event.detail );
        assertEquals( null, event.item );
        assertEquals( true, event.doit );
        log.append( "widgetSelected" );
      }
    } );
    RWTFixture.fakePhase( PhaseId.PROCESS_ACTION );
    Fixture.fakeRequestParam( comboId + ".selectedItem", "0" );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, comboId );
    WidgetUtil.getLCA( combo ).readData( combo );
    assertEquals( 0, combo.getSelectionIndex() );
    assertEquals( "widgetSelected", log.toString() );
    // read changed selection
    Fixture.fakeRequestParam( comboId + ".text", "abc" );
    Fixture.fakeRequestParam( comboId + ".selectionStart", "1" );
    Fixture.fakeRequestParam( comboId + ".selectionLength", "1" );
    WidgetUtil.getLCA( combo ).readData( combo );
    assertEquals( new Point( 1, 2 ), combo.getSelection() );
  }

  public void testReadText() {
    RWTLifeCycle lifeCycle = ( RWTLifeCycle )LifeCycleFactory.getLifeCycle();
    lifeCycle.addPhaseListener( new PreserveWidgetsPhaseListener() );
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    final Combo combo = new Combo( shell, SWT.BORDER );
    shell.open();
    RWTFixture.markInitialized( display );
    RWTFixture.markInitialized( shell );
    RWTFixture.markInitialized( combo );
    // test without verify listener
    RWTFixture.fakeNewRequest();
    String textId = WidgetUtil.getId( combo );
    String displayId = DisplayUtil.getId( display );
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( textId + ".text", "some text" );
    RWTFixture.executeLifeCycleFromServerThread();
    // ensure that no text and selection values are sent back to the client
    String markup = Fixture.getAllMarkup();
    assertEquals( -1, markup.indexOf( "w.setValue(" ) );
    assertEquals( "some text", combo.getText() );
    // test with verify listener
    final StringBuffer log = new StringBuffer();
    combo.addVerifyListener( new VerifyListener() {

      public void verifyText( VerifyEvent event ) {
        assertEquals( combo, event.widget );
        assertEquals( "verify me", event.text );
        assertEquals( 0, event.start );
        assertEquals( 9, event.end );
        log.append( event.text );
      }
    } );
    RWTFixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( textId + ".text", "verify me" );
    RWTFixture.executeLifeCycleFromServerThread();
    // ensure that no text and selection values are sent back to the client
    markup = Fixture.getAllMarkup();
    assertEquals( -1, markup.indexOf( "w.setValue(" ) );
    assertEquals( "verify me", combo.getText() );
    assertEquals( "verify me", log.toString() );
  }

  public void testTextSelectionWithVerifyEvent() {
    final java.util.List log = new ArrayList();
    // register preserve-values phase-listener
    RWTLifeCycle lifeCycle = ( RWTLifeCycle )LifeCycleFactory.getLifeCycle();
    lifeCycle.addPhaseListener( new PreserveWidgetsPhaseListener() );
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    final Combo combo = new Combo( shell, SWT.NONE );
    shell.open();
    String displayId = DisplayUtil.getId( display );
    String comboId = WidgetUtil.getId( combo );
    // ensure that selection is unchanged in case a verify-listener is
    // registered that does not change the text
    VerifyListener emptyVerifyListener = new VerifyListener() {
      public void verifyText( final VerifyEvent event ) {
        log.add( event );
      }
    };
    combo.addVerifyListener( emptyVerifyListener );
    RWTFixture.markInitialized( display );
    RWTFixture.markInitialized( shell );
    RWTFixture.markInitialized( combo );
    log.clear();
    RWTFixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( comboId + ".text", "verify me" );
    Fixture.fakeRequestParam( comboId + ".selectionStart", "1" );
    Fixture.fakeRequestParam( comboId + ".selectionLength", "0" );
    Fixture.fakeRequestParam( JSConst.EVENT_MODIFY_TEXT, comboId );
    RWTFixture.executeLifeCycleFromServerThread();
    // ensure that an empty verify listener does not lead to sending the
    // original text and selection values back to the client
    String markup = Fixture.getAllMarkup();
    assertEquals( -1, markup.indexOf( "w.setValue(" ) );
    assertEquals( -1, markup.indexOf( ".setSelection( w," ) );
    assertEquals( 1, log.size() );
    assertEquals( new Point( 1, 1 ), combo.getSelection() );
    assertEquals( "verify me", combo.getText() );
    combo.removeVerifyListener( emptyVerifyListener );
    // ensure that selection is unchanged in case a verify-listener changes
    // the incoming text within the limits of the selection
    combo.setText( "" );
    VerifyListener alteringVerifyListener = new VerifyListener() {
      public void verifyText( final VerifyEvent event ) {
        log.add( event );
        event.text = "verified";
      }
    };
    combo.addVerifyListener( alteringVerifyListener );
    log.clear();
    RWTFixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( comboId + ".text", "verify me" );
    Fixture.fakeRequestParam( comboId + ".selectionStart", "1" );
    Fixture.fakeRequestParam( comboId + ".selectionLength", "0" );
    Fixture.fakeRequestParam( JSConst.EVENT_MODIFY_TEXT, comboId );
    RWTFixture.executeLifeCycleFromServerThread( );
    assertEquals( 1, log.size() );
    assertEquals( new Point( 1, 1 ), combo.getSelection() );
    assertEquals( "verified", combo.getText() );
    combo.removeVerifyListener( alteringVerifyListener );
    // ensure that selection is adjusted in case a verify-listener changes
    // the incoming text in a way that would result in an invalid selection
    combo.setText( "" );
    alteringVerifyListener = new VerifyListener() {
      public void verifyText( final VerifyEvent event ) {
        log.add( event );
        event.text = "";
      }
    };
    combo.addVerifyListener( alteringVerifyListener );
    log.clear();
    RWTFixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( comboId + ".text", "verify me" );
    Fixture.fakeRequestParam( comboId + ".selectionStart", "1" );
    Fixture.fakeRequestParam( comboId + ".selectionLength", "0" );
    Fixture.fakeRequestParam( JSConst.EVENT_MODIFY_TEXT, comboId );
    RWTFixture.executeLifeCycleFromServerThread( );
    assertEquals( 1, log.size() );
    assertEquals( new Point( 0, 0 ), combo.getSelection() );
    assertEquals( "", combo.getText() );
    combo.removeVerifyListener( alteringVerifyListener );
  }

  public void testSelectionAfterRemoveAll() {
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
    final Combo combo = new Combo( shell, SWT.READ_ONLY );
    combo.add( "item 1" );
    combo.select( 0 );
    Button button = new Button( shell, SWT.PUSH );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent e ) {
        combo.removeAll();
        combo.add( "replacement for item 1" );
        combo.select( 0 );
      }
    } );

    String buttonId = WidgetUtil.getId( button );
    String displayId = DisplayUtil.getId( display );
    RWTLifeCycle lifeCycle = ( RWTLifeCycle )LifeCycleFactory.getLifeCycle();
    lifeCycle.addPhaseListener( new PreserveWidgetsPhaseListener() );

    // Execute life cycle once to simulate startup request
    RWTFixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    RWTFixture.executeLifeCycleFromServerThread();

    // Simulate button click that executes widgetSelected
    RWTFixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, buttonId );
    RWTFixture.executeLifeCycleFromServerThread();
    String expected = "w.select( 0 )";
    assertTrue( Fixture.getAllMarkup().indexOf( expected ) != -1 );
  }

  public void testTextLimit() throws IOException {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    final Combo combo = new Combo( shell, SWT.BORDER );
    ComboLCA lca = new ComboLCA();
    // run LCA one to dump the here uninteresting prolog
    Fixture.fakeResponseWriter();
    lca.renderChanges( combo );
    // Initially no textLimit must be rendered if the initial value is untouched
    Fixture.fakeResponseWriter();
    lca.renderChanges( combo );
    assertEquals( -1, Fixture.getAllMarkup().indexOf( "setTextLimit" ) );
    // Positive textLimit is written as setMaxLength( ... )
    Fixture.fakeResponseWriter();
    RWTFixture.markInitialized( combo );
    RWTFixture.clearPreserved();
    RWTFixture.preserveWidgets();
    combo.setTextLimit( 12 );
    lca.renderChanges( combo );
    String expected = "setTextLimit( 12 );";
    assertTrue( Fixture.getAllMarkup().indexOf( expected ) != -1 );
    // textLimit = Combo.LIMIT is tread as 'no limit'
    Fixture.fakeResponseWriter();
    RWTFixture.markInitialized( combo );
    RWTFixture.clearPreserved();
    RWTFixture.preserveWidgets();
    combo.setTextLimit( Combo.LIMIT );
    lca.renderChanges( combo );
    expected = "setTextLimit( null );";
    assertTrue( Fixture.getAllMarkup().indexOf( expected ) != -1 );
  }

  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
}