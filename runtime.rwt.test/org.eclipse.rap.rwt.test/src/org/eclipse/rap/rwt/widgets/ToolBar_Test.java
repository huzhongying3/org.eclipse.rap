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
import org.eclipse.rap.rwt.graphics.Image_Test;

public class ToolBar_Test extends TestCase {

  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }

  public void testToolBarCreation() {
    Display display = new Display();
    Shell shell = new Shell( display , RWT.NONE );
    ToolBar toolBar = new ToolBar( shell, RWT.VERTICAL );
    assertEquals( 0, toolBar.getItemCount() );
    assertEquals( 0, toolBar.getItems().length );
    ToolItem item0 = new ToolItem( toolBar, RWT.CHECK );
    assertEquals( 1, toolBar.getItemCount() );
    assertEquals( 1, toolBar.getItems().length );
    assertEquals( item0, toolBar.getItem( 0 ) );
    assertEquals( item0, toolBar.getItems()[ 0 ] );
    try {
      toolBar.getItem( 4 );
      fail( "Index out of bounds" );
    } catch( final IllegalArgumentException iae ) {
      // expected
    }
    assertSame( display, item0.getDisplay() );
    item0.dispose();
    assertEquals( 0, toolBar.getItemCount() );
    assertEquals( 0, toolBar.getItems().length );
    item0 = new ToolItem( toolBar, RWT.CHECK );
    assertEquals( 1, toolBar.getItemCount() );
    
    // search operation indexOf
    ToolItem item1 = new ToolItem( toolBar, RWT.PUSH );
    ToolItem item2 = new ToolItem( toolBar, RWT.RADIO );
    item2.setImage(Image.find( Image_Test.IMAGE1 ) );
    assertSame( Image.find( Image_Test.IMAGE1 ), item2.getImage() );
    assertEquals( 1, Image.size() );
    assertEquals( 3, toolBar.getItemCount() );
    assertEquals( 3, toolBar.getItems().length );
    assertEquals( 1, toolBar.indexOf( item1 ) );
    assertEquals( item1, toolBar.getItem( 1 ) );
    assertEquals( item2, toolBar.getItem( 2 ) );
    ToolItem item3 = new ToolItem( toolBar, RWT.SEPARATOR );
    item3.setImage(Image.find( Image_Test.IMAGE2 ) );
    assertNull( item3.getImage() );
    assertEquals( 2, Image.size() );
  }

  public void testToolItemTexts() {
    Display display = new Display();
    Shell shell = new Shell( display , RWT.NONE );
    ToolBar table = new ToolBar( shell, RWT.NONE );
    ToolItem item = new ToolItem( table, RWT.NONE );
    String text0 = "text0";
    String text1 = "text1";
    
    item.setText( text0 );
    assertSame( text0, item.getText() );
    item.setText( text1 );
    assertSame( text1, item.getText() );
  }
}
