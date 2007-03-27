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
import org.eclipse.rap.rwt.internal.widgets.Props;
import org.eclipse.rap.rwt.lifecycle.JSConst;
import org.eclipse.rap.rwt.lifecycle.JSWriter;
import org.eclipse.rap.rwt.widgets.MenuItem;

final class MenuItemLCAUtil {
  
  public static void newItem( final MenuItem menuItem, final String jsClass )
    throws IOException
  {
    JSWriter writer = JSWriter.getWriterFor( menuItem );
    writer.newWidget( jsClass );
    writer.call( menuItem.getParent(), "add", new Object[]{ menuItem } );
  }
  
  public static void writeEnabled( final MenuItem menuItem ) throws IOException {
    Boolean newValue = Boolean.valueOf( menuItem.isEnabled() );
    JSWriter writer = JSWriter.getWriterFor( menuItem );
    writer.set( Props.ENABLED, JSConst.QX_FIELD_ENABLED, newValue, Boolean.TRUE );
  }
}
