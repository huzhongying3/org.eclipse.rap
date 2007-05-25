/*******************************************************************************
 * Copyright (c) 2007 Innoopract Informationssysteme GmbH.
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
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.internal.lifecycle.RWTLifeCycle;
import org.eclipse.swt.lifecycle.*;
import org.eclipse.swt.widgets.*;
import com.w4t.Fixture;
import com.w4t.engine.requests.RequestParams;


public class TableItemLCA_Test extends TestCase {

  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
  
  public void testContentChanged() {
    Display display = new Display();
    Shell shell = new Shell( display );
    Table table = new Table( shell, SWT.NONE );
    TableColumn column0 = new TableColumn( table, SWT.NONE );
    TableItem item = new TableItem( table, SWT.NONE );
    
    // A non-initialized item returns true for itemContentChanged
    assertTrue( TableItemLCA.itemContentChanged( item ) );
    
    RWTFixture.markInitialized( item );
    TableItemLCA tableItemLCA = new TableItemLCA();
    // Adding a column -> itemContentChanged == true 
    tableItemLCA.preserveValues( item );
    new TableColumn( table, SWT.NONE );
    assertTrue( TableItemLCA.itemContentChanged( item ) );
    // Changing a text -> itemContentChanged == true 
    tableItemLCA.preserveValues( item );
    item.setText( 1, "def" );
    assertTrue( TableItemLCA.itemContentChanged( item ) );
    // Changing a text -> itemContentChanged == true 
    tableItemLCA.preserveValues( item );
    column0.setWidth( column0.getWidth() + 2 );
    assertTrue( TableItemLCA.itemContentChanged( item ) );
    // Changing nothing -> itemContentChanged == false 
    tableItemLCA.preserveValues( item );
    assertTrue( TableItemLCA.itemContentChanged( item ) );
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
  
  public void testWidgetSelectedWithCheck() throws IOException {
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
    RWTLifeCycle lifeCycle = new RWTLifeCycle();
    lifeCycle.execute();
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
}
