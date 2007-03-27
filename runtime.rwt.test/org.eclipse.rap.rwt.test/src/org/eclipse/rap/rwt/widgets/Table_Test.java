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

package org.eclipse.rap.rwt.widgets;

import junit.framework.TestCase;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.RWTFixture;
import org.eclipse.rap.rwt.graphics.Image;

public class Table_Test extends TestCase {

  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
  
  public void testInitialValues() {
    Display display = new Display();
    Shell shell = new Shell( display, RWT.NONE );
    Table table = new Table( shell, RWT.NONE );
    
    assertEquals( false, table.getHeaderVisible() );
    assertEquals( false, table.getLinesVisible() );
    assertEquals( 0, table.getSelectionCount() );
    assertEquals( 0, table.getSelectionIndices().length );
    assertEquals( 0, table.getSelection().length );
    assertEquals( 0, table.getTopIndex() );
  }
  
  public void testStyle() {
    Display display = new Display();
    Shell shell = new Shell( display, RWT.NONE );
    Table table = new Table( shell, RWT.NONE );
    assertTrue( ( table.getStyle() & RWT.H_SCROLL ) != 0 );
    assertTrue( ( table.getStyle() & RWT.V_SCROLL ) != 0 );
    assertTrue( ( table.getStyle() & RWT.SINGLE ) != 0 );

    table = new Table( shell, RWT.SINGLE | RWT.MULTI );
    assertTrue( ( table.getStyle() & RWT.SINGLE ) != 0 );

    table = new Table( shell, RWT.SINGLE | RWT.SINGLE );
    assertTrue( ( table.getStyle() & RWT.SINGLE ) != 0 );

    table = new Table( shell, RWT.SINGLE | RWT.MULTI );
    assertTrue( ( table.getStyle() & RWT.SINGLE ) != 0 );
  }

  public void testTableCreation() {
    Display display = new Display();
    Shell shell = new Shell( display, RWT.NONE );
    Table table = new Table( shell, RWT.NONE );
    assertEquals( 0, table.getItemCount() );
    assertEquals( 0, table.getItems().length );
    TableItem item0 = new TableItem( table, RWT.NONE );
    assertEquals( 1, table.getItemCount() );
    assertEquals( 1, table.getItems().length );
    assertEquals( item0, table.getItem( 0 ) );
    assertEquals( item0, table.getItems()[ 0 ] );
    try {
      table.getItem( 4 );
      fail( "Index out of bounds" );
    } catch( final IllegalArgumentException iae ) {
      // expected
    }
    assertSame( display, item0.getDisplay() );
    item0.dispose();
    assertEquals( 0, table.getItemCount() );
    assertEquals( 0, table.getItems().length );
    item0 = new TableItem( table, RWT.NONE );
    assertEquals( 1, table.getItemCount() );
    assertEquals( 0, table.getColumnCount() );
    assertEquals( 0, table.getColumns().length );
    TableColumn column0 = new TableColumn( table, RWT.NONE );
    assertEquals( 1, table.getColumnCount() );
    assertEquals( 1, table.getColumns().length );
    assertEquals( column0, table.getColumn( 0 ) );
    assertEquals( column0, table.getColumns()[ 0 ] );
    try {
      table.getColumn( 4 );
      fail( "Index out of bounds" );
    } catch( final IllegalArgumentException iae ) {
      // expected
    }
    assertSame( display, column0.getDisplay() );
    column0.dispose();
    assertEquals( 0, table.getColumnCount() );
    assertEquals( 0, table.getColumns().length );
    
    // search operation indexOf
    column0 = new TableColumn( table, RWT.NONE );
    TableColumn tableColumn1 = new TableColumn( table, RWT.NONE );
    assertEquals( 1, table.indexOf( tableColumn1 ) );
    TableItem tableItem1 = new TableItem( table, RWT.NONE );
    assertEquals( 1, table.indexOf( tableItem1 ) );
    
    // column width property
    assertEquals( 0, column0.getWidth() );
    column0.setWidth( 100 );
    assertEquals( 100, column0.getWidth() );
  }
  
  public void testHeaderHeight() {
    Display display = new Display();
    Shell shell = new Shell( display , RWT.NONE );
    Table table = new Table( shell, RWT.NONE );
    new TableColumn( table, RWT.NONE );
    assertEquals( 0, table.getHeaderHeight() );
    table.setHeaderVisible( true );
    assertTrue( table.getHeaderHeight() > 0 );
  }
  
  public void testTableItemTexts() {
    Display display = new Display();
    Shell shell = new Shell( display , RWT.NONE );
    Table table = new Table( shell, RWT.NONE );
    TableItem item = new TableItem( table, RWT.NONE );
    String text0 = "text0";
    String text1 = "text1";
    String text2 = "text2";
    String text3 = "text3";
    String text4 = "text4";
    String text5 = "text5";
    
    // test text for first column, the same as setText(String)
    item.setText( text0 );
    assertSame( text0, item.getText() );
    assertSame( text0, item.getText( 0 ) );
    item.setText( 0, text1 );
    assertSame( text1, item.getText() );
    assertSame( text1, item.getText( 0 ) );
    try {
      item.setText( 0, null );
      fail( "Parameter index must not be null." );
    } catch( final NullPointerException npe ) {
      // expected
    }
    
    // test text setting if no table column exists
    item.setText( 1, text1 );
    assertSame( "", item.getText( 1 ) );
    item.setText( 2, text1 );
    assertSame( "", item.getText( 2 ) );
    // test text setting if table column exists
    TableColumn column0 = new TableColumn( table, RWT.NONE );
    new TableColumn( table, RWT.NONE );
    TableColumn column2 = new TableColumn( table, RWT.NONE );
    item.setText( 2, text2 );
    assertSame( text2, item.getText( 2 ) );
    
    // test text retrievment after last column was disposed
    column2.dispose();
    assertSame( "", item.getText( 2 ) );
    column2 = new TableColumn( table, RWT.NONE );
    assertSame( "", item.getText( 2 ) );
    new TableColumn( table, RWT.NONE );
    item.setText( 3, text3 );
    assertSame( text3, item.getText( 3 ) );
    new TableColumn( table, RWT.NONE );
    assertSame( "", item.getText( 4 ) );
    String[] texts = new String[]{
      text0, text1, text2, text3, text4, text5
    };
    
    // test setting multiple texts at once
    for( int i = 0; i < texts.length; i++ ) {
      item.setText( i, texts[ i ] );
    }
    assertSame( text0, item.getText( 0 ) );
    assertSame( text1, item.getText( 1 ) );
    assertSame( text2, item.getText( 2 ) );
    assertSame( text3, item.getText( 3 ) );
    assertSame( text4, item.getText( 4 ) );
    assertSame( "", item.getText( 5 ) );
    
    // test disposal of column that is not the last one
    column2.dispose();
    assertSame( text0, item.getText( 0 ) );
    assertSame( text1, item.getText( 1 ) );
    assertSame( text3, item.getText( 2 ) );
    assertSame( text4, item.getText( 3 ) );
    assertSame( "", item.getText( 4 ) );
    column0.dispose();
    assertSame( text1, item.getText( 0 ) );
    assertSame( text3, item.getText( 1 ) );
    assertSame( text4, item.getText( 2 ) );
    assertSame( "", item.getText( 3 ) );
  }
  
  public void testTopIndex() {
    Display display = new Display();
    Shell shell = new Shell( display , RWT.NONE );
    Table table = new Table( shell, RWT.NONE );
    new TableItem( table, RWT.NONE );
    TableItem lastItem = new TableItem( table, RWT.NONE );

    // Set a value which is out of bounds
    int previousTopIndex = table.getTopIndex();
    table.setTopIndex( 10000 );
    assertEquals( previousTopIndex, table.getTopIndex() );
    
    // Set topIndex to the second item
    table.setTopIndex( 1 );
    assertEquals( 1, table.getTopIndex() );
    
    // Remove last item (whos index equals topIndex) -> must adjust topIndex
    table.setTopIndex( table.indexOf( lastItem ) );
    lastItem.dispose();
    assertEquals( 0, table.getTopIndex() );
  }
  
  public void testDisposeSelectedItem() {
    Display display = new Display();
    Shell shell = new Shell( display );
    Table table = new Table( shell, RWT.NONE );
    new TableColumn( table, RWT.NONE );
    TableItem item = new TableItem( table, RWT.NONE );
    
    table.setSelection( new TableItem[] { item } );
    item.dispose();
    assertEquals( 0, table.getSelectionCount() );
    assertEquals( 0, table.getSelection().length );
  }
  
  public void testRemoveAll() {
    Display display = new Display();
    Shell shell = new Shell( display );
    Table table = new Table( shell, RWT.NONE );
    new TableColumn( table, RWT.NONE );
    TableItem preDisposedItem = new TableItem( table, RWT.NONE );
    TableItem item0 = new TableItem( table, RWT.NONE );
    TableItem item1 = new TableItem( table, RWT.NONE );

    preDisposedItem.dispose();
    table.setSelection( 1 );
    table.setTopIndex( 1 );
    table.removeAll();
    assertEquals( -1, table.getSelectionIndex() );
    assertEquals( 0, table.getItemCount() );
    assertEquals( 0, table.getTopIndex() );
    assertTrue( item0.isDisposed() );
    assertTrue( item1.isDisposed() );
  }
  
  public void deselectAll() {
    Display display = new Display();
    Shell shell = new Shell( display );
    Table table = new Table( shell, RWT.NONE );
    new TableColumn( table, RWT.NONE );
    new TableItem( table, RWT.NONE );
    new TableItem( table, RWT.NONE );

    table.setSelection( 1 );
    table.deselectAll();
    assertEquals( -1, table.getSelectionIndex() );
  }
  
  public void testClear() {
    Display display = new Display();
    Shell shell = new Shell( display );
    Table table = new Table( shell, RWT.NONE );
    new TableColumn( table, RWT.NONE );
    TableItem item = new TableItem( table, RWT.NONE );

    item.setText( "abc" );
    item.setImage( Image.find( RWTFixture.IMAGE1 ) );
    table.clear( table.indexOf( item ) );
    assertEquals( "", item.getText() );
    assertEquals( null, item.getImage() );
    
    // Test clear with illegal arguments
    try {
      table.clear( 2 );
      fail( "Must throw exception when attempting to clear non-existing item" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }
}
