/**
 * 
 */
package org.eclipse.rap.demo.controls;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.events.*;
import org.eclipse.rap.rwt.graphics.Image;
import org.eclipse.rap.rwt.graphics.Point;
import org.eclipse.rap.rwt.layout.RowData;
import org.eclipse.rap.rwt.layout.RowLayout;
import org.eclipse.rap.rwt.widgets.*;

public class ToolBarTab extends ExampleTab {

  private ToolBar toolBar;
  private SelectionAdapter dropDownListener;
  private ToolItem dropDownItem;
  private int count = 0;
  private Menu dropDownMenu;

  public ToolBarTab( TabFolder folder ) {
    super( folder, "ToolBar" );
  }

  void createStyleControls() {
    createStyleButton( "BORDER" );
    createStyleButton( "FLAT" );
    createVisibilityButton();
    createEnablementButton();
    createFontChooser();
    Button changeDDListenerButton = new Button( styleComp, RWT.PUSH );
    changeDDListenerButton.setText( "Change DropDown Listener" );
    changeDDListenerButton.setLayoutData( new RowData( 150, 25 ) );
    changeDDListenerButton.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent event ) {
        changeDropDownListener();
      }
    } );
  }

  void createExampleControls( final Composite top ) {
    top.setLayout( new RowLayout() );
    ClassLoader loader = getClass().getClassLoader();
    Image imageNewFile = Image.find( "resources/newfile_wiz.gif", loader );
    Image imagenewFolder = Image.find( "resources/newfolder_wiz.gif", loader );
    Image imageNewProj = Image.find( "resources/newprj_wiz.gif", loader );
    Image imageSearch = Image.find( "resources/search_src.gif", loader );
    toolBar = new ToolBar( top, getStyle() );
    toolBar.setLayoutData( new RowData( 300, 50 ) );
    registerControl( toolBar );
    ToolItem item1 = new ToolItem( toolBar, RWT.PUSH );
    item1.setText( "new" );
    item1.setImage( imageNewFile );
    ToolItem item2 = new ToolItem( toolBar, RWT.PUSH );
    item2.setText( "open" );
    item2.setImage( imagenewFolder );
    new ToolItem( toolBar, RWT.SEPARATOR );
    dropDownItem = new ToolItem( toolBar, RWT.DROP_DOWN );
    dropDownItem.setText( "select" );
    dropDownItem.setImage( imageNewProj );
    new ToolItem( toolBar, RWT.SEPARATOR );
    ToolItem item4 = new ToolItem( toolBar, RWT.CHECK );
    item4.setImage( imageSearch );
    ToolItem item5 = new ToolItem( toolBar, RWT.RADIO );
    item5.setImage( imageSearch );
    ToolItem item6 = new ToolItem( toolBar, RWT.RADIO );
    item6.setImage( imageSearch );
  }

  private void changeDropDownListener() {
    if( dropDownMenu != null ) {
      dropDownMenu.dispose();
    }
    dropDownMenu = new Menu( toolBar.getShell(), RWT.POP_UP );
    for( int i = 0; i < 5; i++ ) {
      MenuItem item = new MenuItem( dropDownMenu, RWT.PUSH );
      item.setText( "Item " + count++ );
    }
    dropDownListener = new SelectionAdapter() {
      public void widgetSelected( SelectionEvent event ) {
        if( event.detail == RWT.ARROW ) {
          Point point = new Point( event.x, event.y );
          // point = toolbar.getDisplay().map( toolBar, null, point );
          dropDownMenu.setLocation( point );
          dropDownMenu.setVisible( true );
        }
      }
    };
    dropDownItem.addSelectionListener( dropDownListener );
  }
}
