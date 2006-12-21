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

package org.eclipse.rap.rwt.internal.widgets.listkit;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.events.SelectionEvent;
import org.eclipse.rap.rwt.internal.widgets.*;
import org.eclipse.rap.rwt.lifecycle.*;
import org.eclipse.rap.rwt.widgets.List;
import org.eclipse.rap.rwt.widgets.Widget;
import com.w4t.engine.service.ContextProvider;


public class ListLCA extends AbstractWidgetLCA {
  

  // Property names, used when preserving values
  private static final String PROP_SELECTION = "selection";
  private static final String PROP_ITEMS = "items";
  
  private static final Integer DEFAULT_SINGLE_SELECTION = new Integer( -1 );
  private static final int[] DEFAULT_MULTI_SELECTION = new int[ 0 ];
  private static final String[] DEFAUT_ITEMS = new String[ 0 ];

  
  public void preserveValues( final Widget widget ) {
    List list = ( List  )widget;
    ControlLCAUtil.preserveValues( list );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( widget );
    adapter.preserve( Props.SELECTION_LISTENERS, 
                      Boolean.valueOf( SelectionEvent.hasListener( list ) ) );
    adapter.preserve( PROP_ITEMS, list.getItems() );
    preserveSelection( list );
  }

  public void readData( final Widget widget ) {
    List list = ( List )widget;
    String value = WidgetUtil.readPropertyValue( list, "selection" );
    if( value != null ) {
      String[] indiceStrings = value.split( "," );
      int[] indices = new int[ indiceStrings.length ];
      for( int i = 0; i < indices.length; i++ ) {
        indices[ i ] = Integer.valueOf( indiceStrings[ i ] ).intValue();
      }
      list.setSelection( indices );
    }
  }

  public void processAction( final Widget widget ) {
    List list = ( List )widget;
    HttpServletRequest request = ContextProvider.getRequest();
    String id = request.getParameter( JSConst.EVENT_WIDGET_SELECTED );
    if( WidgetUtil.getId( list ).equals( id ) ) {
      ControlLCAUtil.processSelection( ( List )widget, null );
    }
  }

  public void renderInitialization( final Widget widget ) throws IOException {
    List list = ( List )widget;
    JSWriter writer = JSWriter.getWriterFor( widget );
    Boolean multiSelection = isSingle( list ) ? Boolean.FALSE : Boolean.TRUE;
    writer.newWidget( "org.eclipse.rap.rwt.widgets.List", 
                      new Object[] { multiSelection } );
  }

  // TODO [rh] keep scroll position, even when exchanging items 
  public void renderChanges( final Widget widget ) throws IOException {
    List list = ( List )widget;
    ControlLCAUtil.writeChanges( list );
    writeItems( list );
    writeSelection( list );
    updateSelectionListeners( list );
  }

  public void renderDispose( final Widget widget ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( widget );
    writer.dispose();
  }


  ////////////////////////////////////////////////
  // helping methods for selection synchronization

  private static void preserveSelection( final List list ) {
    IWidgetAdapter adapter = WidgetUtil.getAdapter( list );
    Object selection;
    if( isSingle( list ) ) {
      selection = new Integer( list.getSelectionIndex() );
    } else {
      selection = list.getSelectionIndices();
    }
    adapter.preserve( PROP_SELECTION, selection );
  }

  private static void writeItems( final List list ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( list );
    if( WidgetUtil.hasChanged( list, PROP_ITEMS, list.getItems(), DEFAUT_ITEMS ) ) {
      
    }
    writer.set( PROP_ITEMS, new Object[]{ list.getItems() } );
  }

  private static void writeSelection( final List list ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( list );
    String prop = PROP_SELECTION;
    if( isSingle( list ) ) {
      Integer newValue = new Integer( list.getSelectionIndex() );
      Integer defValue = DEFAULT_SINGLE_SELECTION;
      if( WidgetUtil.hasChanged( list, prop, newValue, defValue )) {
        writer.call( "selectItem", new Object[] { newValue } );
      }
    } else {
      int[] newValue = list.getSelectionIndices();
      int[] defValue = DEFAULT_MULTI_SELECTION;
      // TODO [rh] ensure that WidgetUtil#hasChanged can deal with arrays
      if( WidgetUtil.hasChanged( list, prop, newValue, defValue ) ) {
        if( list.getSelectionCount() == list.getItemCount() ) {
          writer.call( "selectAll", null );
        } else {
          int[] selection = list.getSelectionIndices();
          Integer[] newSelection = new Integer[ selection.length ];
          for( int i = 0; i < newSelection.length; i++ ) {
            newSelection[ i ] = new Integer( selection[ i ] );
          }
          writer.call( "selectItems", new Object[] { newSelection } );
        }
      }
    }
  }
  
  private static void updateSelectionListeners( final List list ) 
    throws IOException
  {
    String prop = Props.SELECTION_LISTENERS;
    Boolean newValue = Boolean.valueOf( SelectionEvent.hasListener( list ) );
    Boolean defValue = Boolean.FALSE;
    if( WidgetUtil.hasChanged( list, prop, newValue, defValue ) ) {
      JSWriter writer = JSWriter.getWriterFor( list );
      String value = newValue.booleanValue() ? "action" : "state"; 
      writer.set( "changeSelectionNotification", value );
    }
  }

  private static boolean isSingle( final List list ) {
    return ( list.getStyle() & RWT.SINGLE ) != 0;
  }
}