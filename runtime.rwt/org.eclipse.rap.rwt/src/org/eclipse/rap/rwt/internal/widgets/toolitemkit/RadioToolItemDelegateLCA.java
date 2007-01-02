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

package org.eclipse.rap.rwt.internal.widgets.toolitemkit;

import java.io.IOException;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.events.SelectionEvent;
import org.eclipse.rap.rwt.internal.widgets.ItemLCAUtil;
import org.eclipse.rap.rwt.internal.widgets.Props;
import org.eclipse.rap.rwt.lifecycle.*;
import org.eclipse.rap.rwt.widgets.*;


final class RadioToolItemDelegateLCA extends ToolItemDelegateLCA {
  
  // tool item functions as defined in org.eclipse.rap.rwt.ToolItemUtil
  private static final String CREATE_RADIO 
    = "org.eclipse.rap.rwt.ToolItemUtil.createToolItemRadioButton";
  // radio functions as defined in org.eclipse.rap.rwt.ButtonUtil
  private static final String WIDGET_SELECTED 
    = "org.eclipse.rap.rwt.ButtonUtil.radioSelected";
  
  private final JSListenerInfo JS_LISTENER_INFO 
    = new JSListenerInfo( JSConst.QX_EVENT_CHANGE_SELECTED, 
                          WIDGET_SELECTED, 
                          JSListenerType.ACTION );
  
  void readData( final ToolItem toolItem ) {
    if( WidgetUtil.wasEventSent( toolItem, JSConst.EVENT_WIDGET_SELECTED ) ) {
      Control[] children = toolItem.getParent().getChildren();
      for( int i = 0; i < children.length; i++ ) {
        Widget child = children[ i ];
        if( ( child instanceof ToolItem )
            && ( ( child.getStyle() & RWT.RADIO ) != 0 ) )
        {
          ( ( ToolItem )child ).setSelection( false );
        }
      }
      toolItem.setSelection( true );
      processSelection( toolItem );
    }
  }

  void renderInitialization( final ToolItem toolItem ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( toolItem );
    ToolBar bar = toolItem.getParent();
    int myIndex = bar.indexOf( toolItem );
    ToolItem neighbour = null;
    if ( myIndex > 0 ) {
      neighbour = bar.getItem( myIndex - 1 );
      if( ( neighbour.getStyle() & RWT.RADIO ) == 0 ) {
        neighbour = null;
      }
    }
    Object[] args = new Object[] {
      WidgetUtil.getId( toolItem ),
      toolItem.getParent(),
      toolItem.getSelection() ? "true" : null,
      neighbour
    };
    writer.callStatic( CREATE_RADIO, args );
  }

  void renderChanges( final ToolItem toolItem ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( toolItem );
    ItemLCAUtil.writeChanges( toolItem );
    // TODO [rh] the JSConst.JS_WIDGET_SELECTED does unnecessarily send
    // bounds of the widget that was clicked -> In the SelectionEvent
    // for ToolItem the bounds are undefined
    writer.updateListener( "manager" ,
                           JS_LISTENER_INFO,
                           Props.SELECTION_LISTENERS,
                           SelectionEvent.hasListener( toolItem ) );
  }
}
