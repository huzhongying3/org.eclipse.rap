/*******************************************************************************
 * Copyright (c) 2007-2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.tableitemkit;

import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.internal.lifecycle.*;
import org.eclipse.rwt.internal.service.RequestParams;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.*;


public class TableItemLCA_Test extends TestCase {

  protected void setUp() throws Exception {
    RWTFixture.setUp();
    Fixture.fakePhase( PhaseId.RENDER );
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
  
  public void testItemTextWithoutColumn() throws IOException {
    Display display = new Display();
    Shell shell = new Shell( display );
    Table table = new Table( shell, SWT.NONE );
    TableItem item = new TableItem( table, SWT.NONE );
    // Ensure that even though there are no columns, the first text of an item
    // will be rendered
    Fixture.fakeResponseWriter();
    TableItemLCA tableItemLCA = new TableItemLCA();
    RWTFixture.markInitialized( item );
    tableItemLCA.preserveValues( item );
    item.setText( "newText" );
    tableItemLCA.renderChanges( item );
    String expected = "w.setTexts( [ \"newText\" ] )";
    assertTrue( Fixture.getAllMarkup().indexOf( expected ) != -1 );
  }
  
  public void testWidgetSelectedWithCheck() {
    final SelectionEvent[] events = new SelectionEvent[ 1 ];
    Display display = new Display();
    Shell shell = new Shell( display );
    final Table table = new Table( shell, SWT.CHECK );
    TableItem item1 = new TableItem( table, SWT.NONE );
    final TableItem item2 = new TableItem( table, SWT.NONE );
    table.setSelection( 0 );
    table.addSelectionListener( new SelectionListener() {
      public void widgetSelected( final SelectionEvent event ) {
        events[ 0 ] = event;
      }
      public void widgetDefaultSelected( final SelectionEvent event ) {
        fail( "unexpected event: widgetDefaultSelected" );
      }
    } );
    // Simulate request that comes in after item2 was checked (but not selected)
    RWTFixture.fakeNewRequest();
    String displayId = DisplayUtil.getId( display );
    String item2Id = WidgetUtil.getId( item2 );
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( item2Id + ".checked", "true" );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, item2Id );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED_DETAIL, "check" );
    RWTFixture.executeLifeCycleFromServerThread( );
    assertNotNull( "SelectionEvent was not fired", events[ 0 ] );
    assertEquals( table, events[ 0 ].getSource() );
    assertEquals( item2, events[ 0 ].item );
    assertEquals( true, events[ 0 ].doit );
    assertEquals( 0, events[ 0 ].x );
    assertEquals( 0, events[ 0 ].y );
    assertEquals( 0, events[ 0 ].width );
    assertEquals( 0, events[ 0 ].height );
    assertEquals( 1, table.getSelectionCount() );
    assertEquals( item1, table.getSelection()[ 0 ] );
  }
  
  public void testDisposeSelected() {
    final boolean[] executed = { false };
    Display display = new Display();
    Shell shell = new Shell( display );
    final Table table = new Table( shell, SWT.CHECK );
    new TableItem( table, SWT.NONE );
    new TableItem( table, SWT.NONE );
    new TableItem( table, SWT.NONE );
    table.setSelection( 2 );
    Button button = new Button( shell, SWT.PUSH );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent e ) {
        table.remove( 1, 2 );
        executed[ 0 ] = true;
      }
    } );
    
    RWTFixture.fakeNewRequest();
    String displayId = DisplayUtil.getId( display );
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    String buttonId = WidgetUtil.getId( button );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, buttonId );
    RWTFixture.executeLifeCycleFromServerThread( );
    assertTrue( executed[ 0 ] );
  }
  
  public void testDispose() throws IOException {
    Display display = new Display();
    Shell shell = new Shell( display );
    final Table table = new Table( shell, SWT.CHECK );
    TableItem itemOnlyDisposed = new TableItem( table, SWT.NONE );
    TableItem itemWithTableDisposed = new TableItem( table, SWT.NONE );
    
    RWTFixture.markInitialized( table );
    RWTFixture.markInitialized( itemOnlyDisposed );
    RWTFixture.markInitialized( itemWithTableDisposed );
    
    // Test that when a single items is disposed, its JavaScript dispose
    // function is called
    itemOnlyDisposed.dispose();
    AbstractWidgetLCA lca = WidgetUtil.getLCA( itemWithTableDisposed );
    Fixture.fakeResponseWriter();
    lca.renderDispose( itemOnlyDisposed );
    String expected 
      = "var wm = org.eclipse.swt.WidgetManager.getInstance();" 
      + "var w = wm.findWidgetById( \"w3\" );" 
      + "w.dispose();";
    assertEquals( expected, Fixture.getAllMarkup() );

    // Test that when the whole Tables is dipsosed of, the TableItems dispose
    // function is *not* called
    table.dispose();
    lca = WidgetUtil.getLCA( itemWithTableDisposed );
    Fixture.fakeResponseWriter();
    lca.renderDispose( itemWithTableDisposed );
    assertEquals( "", Fixture.getAllMarkup() );
    assertTrue( itemWithTableDisposed.isDisposed() );
  }
  
  public void testWriteChangesForVirtualItem() throws IOException {
    Display display = new Display();
    Shell shell = new Shell( display );
    Table table = new Table( shell, SWT.VIRTUAL );
    table.setItemCount( 100 );
    // Ensure that nothing is written for an item that is virtual and whose
    // cached was false and remains unchanged while processing the life cycle
    TableItem item = table.getItem( 0 );
    table.clear( 0 );
    TableItemLCA lca = new TableItemLCA();
    Fixture.fakeResponseWriter();
    RWTFixture.markInitialized( item );
    // Ensure that nothing else than the 'checked' property gets preserved
    lca.preserveValues( item );
    IWidgetAdapter itemAdapter = WidgetUtil.getAdapter( item );
    assertEquals( Boolean.FALSE, 
                  itemAdapter.getPreserved( TableItemLCA.PROP_CACHED ) );
    assertNull( itemAdapter.getPreserved( TableItemLCA.PROP_TEXTS ) );
    assertNull( itemAdapter.getPreserved( TableItemLCA.PROP_IMAGES ) );
    assertNull( itemAdapter.getPreserved( TableItemLCA.PROP_CHECKED ) );
    // ... and no markup is generated for a uncached item that was already
    // uncached when entering the life cycle
    lca.renderChanges( item );
    assertEquals( "", Fixture.getAllMarkup() );
  }
  
  public void testGetForegrounds() {
    Display display = new Display();
    Shell shell = new Shell( display );
    Table table = new Table( shell, SWT.NONE );
    TableItem item = new TableItem( table, SWT.NONE );
    Color red = display.getSystemColor( SWT.COLOR_RED );
    // simple case: no explicit colors at all
    Color[] foregrounds = TableItemLCA.getForegrounds( item );
    assertEquals( null, foregrounds[ 0 ] );
    // set foreground on table but not on item
    table.setForeground( red );
    foregrounds = TableItemLCA.getForegrounds( item );
    assertEquals( null, foregrounds[ 0 ] );
    // clear foreground on table and set on item
    table.setForeground( null );
    item.setForeground( red );
    foregrounds = TableItemLCA.getForegrounds( item );
    assertEquals( red, foregrounds[ 0 ] );
  }

  public void testGetBackground() {
    Display display = new Display();
    Shell shell = new Shell( display );
    Table table = new Table( shell, SWT.NONE );
    TableItem item = new TableItem( table, SWT.NONE );
    Color red = display.getSystemColor( SWT.COLOR_RED );
    // simple case: no explicit colors at all
    Color[] backgrounds = TableItemLCA.getBackgrounds( item );
    assertEquals( null, backgrounds[ 0 ] );
    // set background on table but not on item
    table.setBackground( red );
    backgrounds = TableItemLCA.getBackgrounds( item );
    assertEquals( null, backgrounds[ 0 ] );
    // clear background on table and set on item
    table.setBackground( null );
    item.setBackground( red );
    backgrounds = TableItemLCA.getBackgrounds( item );
    assertEquals( red, backgrounds[ 0 ] );
  }
}
