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

package org.eclipse.rap.rwt.internal.widgets.menuitemkit;

import java.io.IOException;
import org.eclipse.rap.rwt.lifecycle.JSWriter;
import org.eclipse.rap.rwt.widgets.MenuItem;


final class SeparatorMenuItemLCA extends MenuItemDelegateLCA {

  void preserveValues( final MenuItem menuItem ) {
    // do nothing
  }

  void readData( final MenuItem menuItem ) {
    // do nothing
  }
  
  void renderInitialization( final MenuItem menuItem ) throws IOException {
    newItem( menuItem, "qx.ui.menu.MenuSeparator" );
  }

  void renderChanges( final MenuItem menuItem ) throws IOException {
    // do nothing
  }
  
  void renderDispose( final MenuItem menuItem ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( menuItem );
    writer.dispose();
  }
}
