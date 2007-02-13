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
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.events.SelectionEvent;
import org.eclipse.rap.rwt.internal.widgets.*;
import org.eclipse.rap.rwt.lifecycle.*;
import org.eclipse.rap.rwt.widgets.*;


public class ListLCA extends AbstractWidgetLCA {

  // Property names, used when preserving values
  private static final String PROP_SELECTION = "selection";
  private static final String PROP_ITEMS = "items";
  private static final String PROP_FOCUS_INDEX = "focusIndex";
  
  private static final Integer DEFAULT_SINGLE_SELECTION = new Integer( -1 );
  private static final int[] DEFAULT_MULTI_SELECTION = new int[ 0 ];
  private static final String[] DEFAUT_ITEMS = new String[ 0 ];
  private static final Integer DEFAULT_FOCUS_INDEX = new Integer( -1 );

  
  public void preserveValues( final Widget widget ) {
    List list = ( List  )widget;
    ControlLCAUtil.preserveValues( list );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( widget );
    adapter.preserve( Props.SELECTION_LISTENERS, 
                      Boolean.valueOf( SelectionEvent.hasListener( list ) ) );
    adapter.preserve( PROP_ITEMS, list.getItems() );
    adapter.preserve( PROP_FOCUS_INDEX, new Integer( list.getFocusIndex() ) );
    preserveSelection( list );
  }

  public void readData( final Widget widget ) {
    List list = ( List )widget;
    String value = WidgetLCAUtil.readPropertyValue( list, "selection" );
    if( value != null ) {
      String[] indiceStrings;
      if( "".equals( value ) ) {
        indiceStrings = new String[ 0 ];
      } else {
        indiceStrings = value.split( "," );
      }
      int[] indices = new int[ indiceStrings.length ];
      for( int i = 0; i < indices.length; i++ ) {
        indices[ i ] = Integer.parseInt( indiceStrings[ i ] );
      }
      list.setSelection( indices );
    }
    readFocusIndex( list );
    ControlLCAUtil.processSelection( list, null, true );
  }

  public void renderInitialization( final Widget widget ) throws IOException {
    List list = ( List )widget;
    JSWriter writer = JSWriter.getWriterFor( list );
    Boolean multiSelection = isSingle( list ) ? Boolean.FALSE : Boolean.TRUE;
    writer.newWidget( "org.eclipse.rap.rwt.widgets.List", 
                      new Object[] { multiSelection } );
    ControlLCAUtil.writeStyleFlags( list );
    writeOverflow( list );
  }

  // TODO [rh] keep scroll position, even when exchanging items 
  public void renderChanges( final Widget widget ) throws IOException {
    List list = ( List )widget;
    // order of writeChanges, writeItems, writeSelection, writeFocus is crucial
    ControlLCAUtil.writeChanges( list );
    writeItems( list );
    writeSelection( list );
    writeFocusIndex( list );
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
    String[] items = list.getItems();
    if( WidgetLCAUtil.hasChanged( list, PROP_ITEMS, items, DEFAUT_ITEMS ) ) {
      writer.set( PROP_ITEMS, new Object[]{ items } );
    }
  }

  private static void writeSelection( final List list ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( list );
    String prop = PROP_SELECTION;
    if( isSingle( list ) ) {
      Integer newValue = new Integer( list.getSelectionIndex() );
      Integer defValue = DEFAULT_SINGLE_SELECTION;
      if( WidgetLCAUtil.hasChanged( list, prop, newValue, defValue )) {
        writer.call( "selectItem", new Object[] { newValue } );
      }
    } else {
      int[] newValue = list.getSelectionIndices();
      int[] defValue = DEFAULT_MULTI_SELECTION;
      if( WidgetLCAUtil.hasChanged( list, prop, newValue, defValue ) ) {
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
    if( WidgetLCAUtil.hasChanged( list, prop, newValue, defValue ) ) {
      JSWriter writer = JSWriter.getWriterFor( list );
      String value = newValue.booleanValue() ? "action" : "state"; 
      writer.set( "changeSelectionNotification", value );
    }
  }

  ///////////////////////////////////////////
  // Helping methods to maintain focused item

  private static void readFocusIndex( final List list ) {
    String paramValue = WidgetLCAUtil.readPropertyValue( list, "focusIndex" );
    if( paramValue != null ) {
      int focusIndex = Integer.parseInt( paramValue );
      Object adapter = list.getAdapter( IListAdapter.class );
      IListAdapter listAdapter = ( IListAdapter )adapter;
      listAdapter.setFocusIndex( focusIndex );
    }
  }

  private static void writeFocusIndex( final List list ) throws IOException {
    String prop = PROP_FOCUS_INDEX;
    Integer newValue = new Integer( list.getFocusIndex() );
    if( WidgetLCAUtil.hasChanged( list, prop, newValue, DEFAULT_FOCUS_INDEX ) ) {
      JSWriter writer = JSWriter.getWriterFor( list );
      writer.call( "focusItem", new Object[] { newValue} );
    }
  }

  private static void writeOverflow( final List list ) throws IOException {
    boolean scrollX = ( list.getStyle() & RWT.H_SCROLL ) != 0;
    boolean scrollY = ( list.getStyle() & RWT.V_SCROLL ) != 0;
    String overflow;
    if( scrollX && scrollY ) {
      overflow = "auto";
    } else if( scrollX ) {
      overflow = "scrollX";
    } else if( scrollY ) {
      overflow = "scrollY";
    } else {
      overflow = "hidden";
    }
    JSWriter writer = JSWriter.getWriterFor( list );
    writer.set( "overflow", overflow );
  }

  private static boolean isSingle( final List list ) {
    return ( list.getStyle() & RWT.SINGLE ) != 0;
  }
}