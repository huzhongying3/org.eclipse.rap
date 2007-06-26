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

package org.eclipse.swt.widgets;

import java.util.Arrays;
import junit.framework.TestCase;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.TreeEvent;
import org.eclipse.swt.events.TreeListener;
import org.eclipse.swt.graphics.Image;

public class Tree_Test extends TestCase {
  
  public void testGetItemsAndGetItemCount() {
    Display display = new Display();
    Composite shell = new Shell( display , SWT.NONE );
    Tree tree = new Tree( shell, SWT.NONE );
    assertEquals( 0, tree.getItemCount() );
    assertEquals( 0, tree.getItems().length );
    TreeItem item = new TreeItem( tree, SWT.NONE );
    assertEquals( 1, tree.getItemCount() );
    assertEquals( 1, tree.getItems().length );
    assertSame( item, tree.getItems()[ 0 ] );
  }
  
  public void testImage() {
    Display display = new Display();
    Composite shell = new Shell( display , SWT.NONE );
    Tree tree = new Tree( shell, SWT.NONE );
    assertEquals( 0, Image.size() );
    TreeItem item1 = new TreeItem( tree, SWT.NONE );
    item1.setImage(Image.find( RWTFixture.IMAGE1 ) );
    assertSame( Image.find( RWTFixture.IMAGE1 ), item1.getImage() );
    assertEquals( 1, Image.size() );
    TreeItem item2 = new TreeItem( tree, SWT.NONE );
    item2.setImage(Image.find( RWTFixture.IMAGE2 ) );
    assertSame( Image.find( RWTFixture.IMAGE2 ), item2.getImage() );
    assertEquals( 2, Image.size() );
  }
  
  public void testStyle() {
    Display display = new Display();
    Composite shell = new Shell( display , SWT.NONE );
    
    Tree tree1 = new Tree( shell, SWT.NONE );
    assertTrue( ( tree1.getStyle() & SWT.SINGLE ) != 0 );
    assertTrue( ( tree1.getStyle() & SWT.H_SCROLL) != 0 );
    assertTrue( ( tree1.getStyle() & SWT.V_SCROLL ) != 0 );
    
    Tree tree2 = new Tree( shell, SWT.SINGLE );
    assertTrue( ( tree2.getStyle() & SWT.SINGLE ) != 0 );
    assertTrue( ( tree2.getStyle() & SWT.H_SCROLL) != 0 );
    assertTrue( ( tree2.getStyle() & SWT.V_SCROLL ) != 0 );

    Tree tree3 = new Tree( shell, SWT.MULTI );
    assertTrue( ( tree3.getStyle() & SWT.MULTI ) != 0 );
    assertTrue( ( tree3.getStyle() & SWT.H_SCROLL) != 0 );
    assertTrue( ( tree3.getStyle() & SWT.V_SCROLL ) != 0 );

    Tree tree4 = new Tree( shell, SWT.SINGLE | SWT.MULTI );
    assertTrue( ( tree4.getStyle() & SWT.SINGLE ) != 0 );
    assertTrue( ( tree4.getStyle() & SWT.H_SCROLL) != 0 );
    assertTrue( ( tree4.getStyle() & SWT.V_SCROLL ) != 0 );
  }

  public void testItemHierarchy() {
    Display display = new Display();
    Composite shell = new Shell( display , SWT.NONE );
    Tree tree = new Tree( shell, SWT.NONE );
    TreeItem item = new TreeItem( tree, SWT.NONE );
    TreeItem subItem = new TreeItem( item, SWT.NONE );
    assertEquals( 1, item.getItems().length );
    assertEquals( null, item.getParentItem() );
    assertEquals( tree, item.getParent() );
    assertEquals( subItem, item.getItems()[ 0 ] );
    assertEquals( tree, subItem.getParent() );
    assertEquals( item, subItem.getParentItem() );
    assertEquals( 0, subItem.getItems().length );
  }

  public void testDispose() {
    Display display = new Display();
    Composite shell = new Shell( display , SWT.NONE );
    Tree tree = new Tree( shell, SWT.NONE );
    TreeItem item = new TreeItem( tree, SWT.NONE );
    TreeItem subItem = new TreeItem( item, SWT.NONE );
    tree.dispose();
    assertEquals( true, item.isDisposed() );
    assertEquals( true, subItem.isDisposed() );
    assertEquals( 0, tree.getItemCount() );
    assertEquals( 0, item.getItemCount() );
    assertEquals( 0, subItem.getItemCount() );
  }
  
  public void testDisposeSelected() {
    Display display = new Display();
    Composite shell = new Shell( display , SWT.NONE );
    Tree tree = new Tree( shell, SWT.MULTI );
    TreeItem item = new TreeItem( tree, SWT.NONE );
    TreeItem subItem = new TreeItem( item, SWT.NONE );
    TreeItem otherItem = new TreeItem( tree, SWT.NONE );
    tree.setSelection( new TreeItem[] { subItem, otherItem } );
    item.dispose();
    assertEquals( true, item.isDisposed() );
    assertEquals( true, subItem.isDisposed() );
    assertEquals( false, otherItem.isDisposed() );
    assertEquals( 1, tree.getSelectionCount() );
    assertSame( otherItem, tree.getSelection()[ 0 ] );
  }
  
  public void testIndexOf() {
    Display display = new Display();
    Composite shell = new Shell( display , SWT.NONE );
    Tree tree = new Tree( shell, SWT.NONE );
    TreeItem item = new TreeItem( tree, SWT.NONE );
    assertEquals( 0, tree.indexOf( item ) );
    
    item.dispose();
    try {
      tree.indexOf( item );
      fail( "Must not allow to call indexOf for disposed item" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
    try {
      tree.indexOf( null );
      fail( "Must not allow to call indexOf for null" );
    } catch( NullPointerException e ) {
      // expected
    }
    
    Tree anotherTree = new Tree( shell, SWT.NONE );
    TreeItem anotherItem = new TreeItem( anotherTree, SWT.NONE );
    assertEquals( -1, tree.indexOf( anotherItem ) );
  }
  
  public void testExpandCollapse() {
    final StringBuffer log = new StringBuffer();
    Display display = new Display();
    Composite shell = new Shell( display , SWT.NONE );
    Tree tree = new Tree( shell, SWT.NONE );
    tree.addTreeListener( new TreeListener() {
      public void treeCollapsed( final TreeEvent e ) {
        log.append( "collapsed" );
      }
      public void treeExpanded( final TreeEvent e ) {
        log.append( "expanded" );
      }
    } );
    TreeItem item = new TreeItem( tree, SWT.NONE );
    TreeItem subItem = new TreeItem( item, SWT.NONE );
    TreeItem childlessItem = new TreeItem( tree, SWT.NONE );
    
    // ensure initial state
    assertEquals( false, item.getExpanded() );
    // ensure changing works 
    item.setExpanded( true );
    assertEquals( true, item.getExpanded() );
    // test that items without sub-items cannot be expanded 
    childlessItem.setExpanded( true );
    assertEquals( false, childlessItem.getExpanded() );
    // test that an item is still expanded when it has no more sub items
    item.setExpanded( true );
    subItem.dispose();
    assertEquals( true, item.getExpanded() );
    // ensure that calling setExpanded does not rase any events
    assertEquals( "", log.toString() );
  }
  
  public void testSelectionForSingle() {
    Display display = new Display();
    Composite shell = new Shell( display , SWT.NONE );
    Tree tree = new Tree( shell, SWT.SINGLE );
    
    // Verify that an empty tree has an empty selection  
    assertEquals( 0, tree.getItemCount() );
    assertTrue( Arrays.equals( new TreeItem[ 0 ], tree.getSelection() ) );
    assertEquals( 0, tree.getSelectionCount() );

    // Verify that adding a TreeItem does not change the current selection
    TreeItem treeItem1 = new TreeItem( tree, SWT.NONE );
    assertEquals( 1, tree.getItemCount() );
    assertTrue( Arrays.equals( new TreeItem[ 0 ], tree.getSelection() ) );
    assertEquals( 0, tree.getSelectionCount() );
    
    // Test selecting a single treItem
    tree.setSelection( treeItem1 );
    assertEquals( 1, tree.getSelectionCount() );
    TreeItem[] expected = new TreeItem[] { treeItem1 };
    assertTrue( Arrays.equals( expected, tree.getSelection() ) );
    
    // Verify that getSelection returns a safe copy
    tree.setSelection( treeItem1 );
    TreeItem[] selection = tree.getSelection();
    selection[ 0 ] = null;
    assertSame( treeItem1, tree.getSelection()[ 0 ] );
    
    // Test that null-argument leads to an exception
    try {
      tree.setSelection( ( TreeItem )null );
      fail( "must not allow setSelection( null )" );
    } catch( NullPointerException e ) {
      // expected
    }
    
    // Test de-selecting all items
    tree.setSelection( new TreeItem[ 0 ] );
    assertEquals( 0, tree.getSelectionCount() );
    assertEquals( 0, tree.getSelection().length );
    
    // Test selecting a single treeItem with setSelection(TreeItem[])
    tree.setSelection( new TreeItem[] { treeItem1 } );
    assertEquals( 1, tree.getSelectionCount() );
    assertSame( treeItem1, tree.getSelection()[ 0 ] );
    
    // Test that setSelection(TreeItem[]) copies the argument
    TreeItem[] newSelection = new TreeItem[] { treeItem1 };
    tree.setSelection( newSelection );
    newSelection[ 0 ] = null;
    assertSame( treeItem1, tree.getSelection()[ 0 ] );

    // Test that calling setSelection(TreeItem[]) with a null-argument leads 
    // to an exception
    try {
      tree.setSelection( (org.eclipse.swt.widgets.TreeItem[] )null );
      fail( "must not allow setSelection( null )" );
    } catch( NullPointerException e ) {
      // expected
    }
    
    // Test calling setSelection(TreeItem[]) with more than one element
    TreeItem treeItem2 = new TreeItem( tree, SWT.NONE );
    tree.setSelection( new TreeItem[] { treeItem2, treeItem1, null } );
    assertEquals( 0, tree.getSelectionCount() );
    assertTrue( Arrays.equals( new TreeItem[ 0 ], tree.getSelection() ) );
    
    // Test calling setSelection(TreeItem[]) with one null-element
    // -> must not change current selection
    tree.setSelection( new TreeItem[] { treeItem1 } );
    tree.setSelection( new TreeItem[] { null } );
    assertEquals( 1, tree.getSelectionCount() );
    assertSame( treeItem1, tree.getSelection()[ 0 ] );
    
    // Verify that selecting a disposed of item throws an exception
    try {
      tree.setSelection( treeItem1 );
      TreeItem disposedItem = new TreeItem( tree, SWT.NONE );
      disposedItem.dispose();
      tree.setSelection( disposedItem );
      fail( "Must not allow to select diposed of tree item." );
    } catch( IllegalArgumentException e ) {
      // ensure that the previously set selection is not changed
      assertSame( treeItem1, tree.getSelection()[ 0 ] );
    }
  }
  
  public void testSelectionForMulti() {
    Display display = new Display();
    Composite shell = new Shell( display , SWT.NONE );
    Tree tree = new Tree( shell, SWT.MULTI );

    // Verify that an empty tree has an empty selection  
    assertEquals( 0, tree.getItemCount() );
    assertTrue( Arrays.equals( new TreeItem[ 0 ], tree.getSelection() ) );
    assertEquals( 0, tree.getSelectionCount() );

    // Verify that adding a TreeItem does not change the current selection
    TreeItem treeItem1 = new TreeItem( tree, SWT.NONE );
    assertEquals( 1, tree.getItemCount() );
    assertTrue( Arrays.equals( new TreeItem[ 0 ], tree.getSelection() ) );
    assertEquals( 0, tree.getSelectionCount() );
  
    // Test de-selecting all items
    tree.setSelection( new TreeItem[ 0 ] );
    assertEquals( 0, tree.getSelectionCount() );
    assertEquals( 0, tree.getSelection().length );
    
    // Ensure that setSelection( item ) does the same as 
    // setSelection( new TreeItem[] { item } )
    tree.setSelection( treeItem1 );
    TreeItem selected1 = tree.getSelection()[ 0 ];
    tree.setSelection( new TreeItem[] { treeItem1 } );
    TreeItem selected2 = tree.getSelection()[ 0 ];
    assertSame( selected1, selected2 );
    
    // Select two treeItems
    TreeItem treeItem2 = new TreeItem( tree, SWT.NONE );
    tree.setSelection( new TreeItem[] { treeItem1, treeItem2 } );
    assertEquals( 2, tree.getSelectionCount() );
    TreeItem[] expected = new TreeItem[] { treeItem1, treeItem2 };
    assertTrue( Arrays.equals( expected, tree.getSelection() ) );

    // Test calling setSelection(TreeItem[]) with one null-element
    // -> must not change current selection
    tree.setSelection( new TreeItem[] { treeItem1 } );
    tree.setSelection( new TreeItem[] { null } );
    assertEquals( 1, tree.getSelectionCount() );
    assertSame( treeItem1, tree.getSelection()[ 0 ] );

    // Test calling setSelection(TreeItem[]) with only null-elements
    // -> must not change current selection
    tree.setSelection( new TreeItem[] { treeItem1 } );
    tree.setSelection( new TreeItem[] { null, null } );
    assertEquals( 1, tree.getSelectionCount() );
    assertSame( treeItem1, tree.getSelection()[ 0 ] );

    // Select two treeItems, with some null-elements in between
    tree.setSelection( new TreeItem[ 0 ] );
    tree.setSelection( new TreeItem[] { null, treeItem1, null, treeItem2 } );
    assertEquals( 2, tree.getSelectionCount() );
    expected = new TreeItem[] { treeItem1, treeItem2 };
    assertTrue( Arrays.equals( expected, tree.getSelection() ) );

    // Verify that selecting a disposed of item throws an exception
    try {
      tree.setSelection( treeItem1 );
      TreeItem disposedItem = new TreeItem( tree, SWT.NONE );
      disposedItem.dispose();
      tree.setSelection( new TreeItem[] { treeItem1, disposedItem } );
      fail( "Must not allow to select diposed of tree item(s)." );
    } catch( IllegalArgumentException e ) {
      // ensure that the previously set selection is not changed
      assertSame( treeItem1, tree.getSelection()[ 0 ] );
    }
  }

  public void testSelectAllForSingle() {
    Display display = new Display();
    Composite shell = new Shell( display , SWT.NONE );
    Tree tree = new Tree( shell, SWT.SINGLE );
    TreeItem item1 = new TreeItem( tree, SWT.NONE );
    new TreeItem( item1, SWT.NONE );
    TreeItem item2 = new TreeItem( tree, SWT.NONE );
    tree.setSelection( item2 );
    tree.selectAll();
    assertSame( item2, tree.getSelection()[ 0 ] );
  }
  
  public void testSelectAllForMulti() {
    Display display = new Display();
    Composite shell = new Shell( display , SWT.NONE );
    Tree tree = new Tree( shell, SWT.MULTI );
    TreeItem item1 = new TreeItem( tree, SWT.NONE );
    TreeItem item11 = new TreeItem( item1, SWT.NONE );
    TreeItem item2 = new TreeItem( tree, SWT.NONE );
    tree.selectAll();
    assertTrue( contains( tree.getSelection(), item1 ) );
    assertTrue( contains( tree.getSelection(), item11 ) );
    assertTrue( contains( tree.getSelection(), item2 ) );
  }
  
  public void testDeselectAll() {
    Display display = new Display();
    Composite shell = new Shell( display , SWT.NONE );
    Tree tree = new Tree( shell, SWT.MULTI );
    TreeItem treeItem1 = new TreeItem( tree, SWT.NONE );
    TreeItem treeItem2 = new TreeItem( tree, SWT.NONE );
    tree.setSelection( new TreeItem[] { treeItem1, treeItem2 } );
    tree.deselectAll();
    assertEquals( 0, tree.getSelectionCount() );
    assertEquals( 0, tree.getSelection().length );
  }
  
  public void testRemoveAll() {
    Display display = new Display();
    Composite shell = new Shell( display , SWT.NONE );
    Tree tree = new Tree( shell, SWT.MULTI );
    
    // Test removeAll on empty tree
    tree.removeAll();
    assertEquals( 0, tree.getItemCount() );
    assertEquals( 0, tree.getSelection().length );
    
    // Test removeAll on populated tree
    new TreeItem( tree, SWT.NONE );
    TreeItem treeItem = new TreeItem( tree, SWT.NONE );
    TreeItem subTreeItem = new TreeItem( treeItem, SWT.NONE );
    tree.setSelection( treeItem );
    tree.removeAll();
    assertEquals( 0, tree.getItemCount() );
    assertEquals( 0, tree.getSelection().length );
    assertEquals( true, treeItem.isDisposed() );
    assertEquals( true, subTreeItem.isDisposed() );
  }

  public void testShowItem() {
    Display display = new Display();
    Composite shell = new Shell( display , SWT.NONE );
    Tree tree = new Tree(shell, SWT.SINGLE);
    try {
      tree.showItem(null);
      fail("No exception thrown for item == null");
    }
//    catch (IllegalArgumentException e) {
    catch (NullPointerException e) {
    }
    
    int number = 20;
    TreeItem[] items = new TreeItem[number];
    for (int i = 0; i < number; i++) {
      items[i] = new TreeItem(tree, 0);
    }
    for(int i=0; i<number; i++)
      tree.showItem(items[i]);

    tree.removeAll();

    tree = new Tree(shell, SWT.MULTI);
    //showing somebody else's items
    
    items = new TreeItem[number];
    for (int i = 0; i < number; i++) {
      items[i] = new TreeItem(tree, 0);
    }

    Tree tree2 = new Tree(shell, 0);
    TreeItem[] items2 = new TreeItem[number];
    for (int i = 0; i < number; i++) {
      items2[i] = new TreeItem(tree2, 0);
    }

    for(int i=0; i<number; i++)
      tree.showItem(items2[i]);

    tree.removeAll();
  }
  
  public void test_getParentItem() {
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
    Tree tree = new Tree( shell, SWT.SINGLE );
    assertNull( tree.getParentItem() );
  }
  
  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }

  private static boolean contains( final TreeItem[] items, 
                                   final TreeItem item ) 
  {
    boolean result = false;
    for( int i = 0; !result && i < items.length; i++ ) {
      if( item == items[ i ] ) {
        result = true;
      }
    }
    return result;
  }
}
