/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH. All rights
 * reserved. This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Innoopract Informationssysteme GmbH - initial API and
 * implementation
 ******************************************************************************/

package org.eclipse.rap.demo.controls;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.*;

public class ToolBarTab extends ExampleTab {

  private ToolBar toolBar;
  private ToolItem dropDownItem;
  private int count = 0;

  public ToolBarTab( final CTabFolder topFolder ) {
    super( topFolder, "ToolBar" );
  }

  protected void createStyleControls( final Composite parent ) {
    // TODO [rst] Allow for vertical toolbars
//    createStyleButton( "HORIZONTAL", SWT.HORIZONTAL );
//    createStyleButton( "VERTICAL", SWT.VERTICAL );
    createStyleButton( "BORDER", SWT.BORDER );
    createStyleButton( "FLAT", SWT.FLAT );
    createVisibilityButton();
    createEnablementButton();
// TODO [rst] Uncomment when setting background works on toolbar
//    createBgColorButton();
    createFontChooser();
  }

  protected void createExampleControls( final Composite parent ) {
    parent.setLayout( new RowLayout() );
    ClassLoader loader = getClass().getClassLoader();
    Image imageNewFile = Image.find( "resources/newfile_wiz.gif", loader );
    Image imagenewFolder = Image.find( "resources/newfolder_wiz.gif", loader );
    Image imageNewProj = Image.find( "resources/newprj_wiz.gif", loader );
    Image imageSearch = Image.find( "resources/search_src.gif", loader );
    toolBar = new ToolBar( parent, getStyle() );
    registerControl( toolBar );
    ToolItem item1 = new ToolItem( toolBar, SWT.PUSH );
    item1.setText( "new" );
    item1.setImage( imageNewFile );
    ToolItem item2 = new ToolItem( toolBar, SWT.PUSH );
    item2.setText( "open" );
    item2.setImage( imagenewFolder );
    new ToolItem( toolBar, SWT.SEPARATOR );
    dropDownItem = new ToolItem( toolBar, SWT.DROP_DOWN );
    dropDownItem.setText( "select" );
    dropDownItem.setImage( imageNewProj );
    new ToolItem( toolBar, SWT.SEPARATOR );

    // Text item
    ToolItem itemText = new ToolItem( toolBar, SWT.SEPARATOR );
    Text text = new Text( toolBar, SWT.BORDER );
    text.setText( "A Text Field" );
    itemText.setControl( text );
    itemText.setWidth( 100 );

    ToolItem item4 = new ToolItem( toolBar, SWT.CHECK );
    item4.setImage( imageSearch );
    item4.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent event ) {
System.out.println( "check");
      }
    } );
    ToolItem item5 = new ToolItem( toolBar, SWT.RADIO );
    item5.setImage( imageSearch );
    ToolItem item6 = new ToolItem( toolBar, SWT.RADIO );
    item6.setImage( imageSearch );
    final Menu dropDownMenu = new Menu( toolBar.getShell(), SWT.POP_UP );
    for( int i = 0; i < 5; i++ ) {
      MenuItem item = new MenuItem( dropDownMenu, SWT.PUSH );
      item.setText( "Item " + count++ );
    }
    dropDownItem.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        if( event.detail == SWT.ARROW ) {
          Point point = new Point( event.x, event.y );
          dropDownMenu.setLocation( point );
          dropDownMenu.setVisible( true );
        }
      }
    } );
    ToolItem withoutImageIcon = new ToolItem( toolBar, SWT.PUSH );
    withoutImageIcon.setText( "w/o <image>" );
  }
}
