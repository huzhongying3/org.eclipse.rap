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

package org.eclipse.rap.rwt.internal.widgets.coolbarkit;

import java.io.IOException;
import org.eclipse.rap.rwt.internal.widgets.*;
import org.eclipse.rap.rwt.lifecycle.*;
import org.eclipse.rap.rwt.widgets.CoolBar;
import org.eclipse.rap.rwt.widgets.Widget;


public class CoolBarLCA extends AbstractWidgetLCA {

  public void preserveValues( final Widget widget ) {
    CoolBar coolBar = ( CoolBar )widget;
    ControlLCAUtil.preserveValues( coolBar );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( coolBar );
    adapter.preserve( Props.LOCKED, Boolean.valueOf( coolBar.getLocked() ) );
  }

  public void readData( final Widget widget ) {
    // TODO [rh] implementation missing
  }
  
  public void renderInitialization( final Widget widget ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( widget );
    writer.newWidget( "qx.ui.layout.CanvasLayout" );
  }
  
  public void renderChanges( final Widget widget ) throws IOException {
    CoolBar coolBar = ( CoolBar )widget;
    ControlLCAUtil.writeChanges( coolBar );
}

  public void renderDispose( final Widget widget ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( widget );
    writer.dispose();
  }
}
