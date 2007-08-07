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

package org.eclipse.swt.internal.widgets.menukit;

import java.io.IOException;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.lifecycle.*;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;

final class PopupMenuLCA extends MenuDelegateLCA {

  private static final String SHOW_MENU 
    = "org.eclipse.swt.MenuUtil.showMenu";
  
  void preserveValues( final Menu menu ) {
    // TODO [rh] extract method and move to MenuLCAUtil
    IWidgetAdapter adapter = WidgetUtil.getAdapter( menu );
    adapter.preserve( Props.ENABLED, Boolean.valueOf( menu.getEnabled() ) );
    MenuLCAUtil.preserveMenuListener( menu );
  }
  
  void readData( final Menu menu ) {
    MenuLCAUtil.readMenuEvent( menu );
  }
  
  void renderInitialization( final Menu menu ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( menu );
    writer.newWidget( "qx.ui.menu.Menu" );
    writer.call( "addToDocument", null );
  }

  void renderChanges( final Menu menu ) throws IOException {
    if( menu.isVisible() ) {
      JSWriter writer = JSWriter.getWriterFor( menu );
      Rectangle bounds = menu.getBounds();
      Object[] args = new Object[] {
        menu,
        new Integer( bounds.x ),
        new Integer( bounds.y )
      };
      writer.callStatic( SHOW_MENU, args );
      menu.setVisible( false );  
    }
    MenuLCAUtil.writeEnabled( menu );
    MenuLCAUtil.writeMenuListener( menu );
    MenuLCAUtil.writeUnhideMenu( menu );
  }
  
  public static void writeEnabled( final Control control )
    throws IOException
  {
    Boolean newValue = Boolean.valueOf( control.isEnabled() );
    JSWriter writer = JSWriter.getWriterFor( control );
    writer.set( Props.ENABLED, JSConst.QX_FIELD_ENABLED, newValue, Boolean.TRUE );
  }

}
