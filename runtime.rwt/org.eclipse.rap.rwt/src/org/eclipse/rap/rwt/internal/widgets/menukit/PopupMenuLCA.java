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

package org.eclipse.rap.rwt.internal.widgets.menukit;

import java.io.IOException;
import org.eclipse.rap.rwt.graphics.Rectangle;
import org.eclipse.rap.rwt.lifecycle.JSWriter;
import org.eclipse.rap.rwt.widgets.Menu;


final class PopupMenuLCA extends MenuDelegateLCA {

  private static final String SHOW_MENU 
    = "org.eclipse.rap.rwt.MenuUtil.showMenu";
  
  void preserveValues( final Menu menu ) {
  }
  
  void readData( Menu menu ) {
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
  }
}
