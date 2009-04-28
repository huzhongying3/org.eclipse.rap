/*******************************************************************************
 * Copyright (c) 2002, 2009 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.swt.internal.widgets.linkkit;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.rwt.internal.lifecycle.JSConst;
import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.internal.widgets.ILinkAdapter;
import org.eclipse.swt.widgets.*;

/**
 * Life-cycle adapter for the Link widget
 */
public class LinkLCA extends AbstractWidgetLCA {

  private static final String QX_TYPE = "qx.ui.layout.HorizontalBoxLayout";

//  private static final String TYPE_POOL_ID = LinkLCA.class.getName();

  private static final String JS_LINK_UTIL = "org.eclipse.swt.LinkUtil";
  private static final String JS_FUNC_INIT = JS_LINK_UTIL + ".init";
  private static final String JS_FUNC_ADD_LINK = JS_LINK_UTIL + ".addLink";
  private static final String JS_FUNC_ADD_TEXT = JS_LINK_UTIL + ".addText";
  private static final String JS_FUNC_ADD_STATE = JS_LINK_UTIL + ".addState";
  private static final String JS_FUNC_REMOVE_STATE
    = JS_LINK_UTIL + ".removeState";
  private static final String JS_FUNC_CLEAR = JS_LINK_UTIL + ".clear";
  private static final String JS_FUNC_DESTROY = JS_LINK_UTIL + ".destroy";
  private static final String JS_FUNC_SET_SELECTION_LISTENER
    = JS_LINK_UTIL + ".setSelectionListener";

  private static final String PROP_TEXT = "text";
  private static final String PROP_VARIANT = "variant";
  static final String PROP_SEL_LISTENER = "selectionListener";

  public void preserveValues( final Widget widget ) {
    Link link = ( Link )widget;
    ControlLCAUtil.preserveValues( link );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( widget );
    adapter.preserve( PROP_TEXT, link.getText() );
    Boolean newValue = Boolean.valueOf( SelectionEvent.hasListener( link ) );
    adapter.preserve( PROP_SEL_LISTENER, newValue );
    WidgetLCAUtil.preserveCustomVariant( link );
  }

  public void readData( final Widget widget ) {
    Link link = ( Link )widget;
    processSelectionEvent( link );
    ControlLCAUtil.processMouseEvents( link );
    ControlLCAUtil.processKeyEvents( link );
  }

  public void renderInitialization( final Widget widget ) throws IOException {
    Link link = ( Link )widget;
    JSWriter writer = JSWriter.getWriterFor( link );
    writer.newWidget( QX_TYPE );
    writer.set( JSConst.QX_FIELD_APPEARANCE, "link" );
    Object[] args = new Object[] { widget };
    writer.callStatic( JS_FUNC_INIT, args );
    ControlLCAUtil.writeStyleFlags( link );
  }

  public void renderChanges( final Widget widget ) throws IOException {
    Link link = ( Link )widget;
    ControlLCAUtil.writeChanges( link );
    writeSelectionListener( link );
    writeText( link );
    writeCustomVariant( link );
  }

  public void renderDispose( final Widget widget ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( widget );
    Object[] args = new Object[]{ widget };
    writer.callStatic( JS_FUNC_DESTROY, args );
    writer.dispose();
  }

  public void createResetHandlerCalls( final String typePoolId )
    throws IOException
  {
    ControlLCAUtil.resetStyleFlags();
    JSWriter writer = JSWriter.getWriterForResetHandler();
    Object[] args = new Object[]{ JSWriter.WIDGET_REF };
    writer.callStatic( JS_FUNC_CLEAR, args );
  }

  public String getTypePoolId( final Widget widget ) {
    // see https://bugs.eclipse.org/bugs/show_bug.cgi?id=226651
//    return TYPE_POOL_ID;
    return null;
  }

  private static void writeSelectionListener( final Link link )
    throws IOException
  {
    Boolean newValue = Boolean.valueOf( SelectionEvent.hasListener( link ) );
    Boolean defValue = Boolean.FALSE;
    String prop = PROP_SEL_LISTENER;
    if( WidgetLCAUtil.hasChanged( link, prop, newValue, defValue ) ) {
      JSWriter writer = JSWriter.getWriterFor( link );
      Object[] args = new Object[]{ link, newValue };
      writer.callStatic( JS_FUNC_SET_SELECTION_LISTENER, args );
    }
  }

  private static void writeText( final Link link ) throws IOException {
    String newValue = link.getText();
    if( WidgetLCAUtil.hasChanged( link, PROP_TEXT, newValue, "" ) ) {
      JSWriter writer = JSWriter.getWriterFor( link );
      Object[] args = new Object[]{ link };
      writer.callStatic( JS_FUNC_CLEAR, args );
      ILinkAdapter adapter
        = ( ILinkAdapter )link.getAdapter( ILinkAdapter.class );
      String displayText = adapter.getDisplayText();
      Point[] offsets = adapter.getOffsets();
      int length = displayText.length();
      int pos = 0;
      for( int i = 0; i < offsets.length; i++ ) {
        int start = offsets[ i ].x;
        int end = offsets[ i ].y + 1;
        // before link
        if( pos < start ) {
          writeNormalText( link, displayText.substring( pos, start ) );
        }
        // link itself
        if( start < end ) {
          writeLinkText( link, displayText.substring( start, end ), i );
        }
        pos = end;
      }
      // after last link
      if( pos < length ) {
        writeNormalText( link, displayText.substring( pos, length ) );
      }
    }
  }

  private static void writeNormalText( final Link link, final String text )
    throws IOException
  {
    JSWriter writer = JSWriter.getWriterFor( link );
    Object[] args = new Object[] {
      link,
      // TODO [rst] mnemonics are already parsed by Link#parse()
      //            Revise when we're going to support underline once
      WidgetLCAUtil.escapeText( text, false )
    };
    writer.callStatic( JS_FUNC_ADD_TEXT, args );
  }

  private static void writeLinkText( final Link link,
                                     final String text,
                                     final int index )
    throws IOException
  {
    JSWriter writer = JSWriter.getWriterFor( link );
    Object[] args = new Object[] {
      link,
      // TODO [rst] mnemonics are already parsed by Link#parse()
      //            Revise when we're going to support underline once
      WidgetLCAUtil.escapeText( text, false ),
      new Integer( index )
    };
    writer.callStatic( JS_FUNC_ADD_LINK, args );
  }

  private static void processSelectionEvent( final Link link ) {
    String eventId = JSConst.EVENT_WIDGET_SELECTED;
    if( WidgetLCAUtil.wasEventSent( link, eventId ) ) {
      HttpServletRequest request = ContextProvider.getRequest();
      String indexStr
        = request.getParameter( JSConst.EVENT_WIDGET_SELECTED_INDEX );
      int index = Integer.parseInt( indexStr );
      ILinkAdapter adapter
        = ( ILinkAdapter )link.getAdapter( ILinkAdapter.class );
      String[] ids = adapter.getIds();
      if( index < ids.length ) {
        SelectionEvent event
          = new SelectionEvent( link, null, SelectionEvent.WIDGET_SELECTED );
        event.text = ids[ index ];
        event.processEvent();
      }
    }
  }

  private static void writeCustomVariant( final Link link )
    throws IOException
  {
    IWidgetAdapter adapter = WidgetUtil.getAdapter( link );
    String oldValue = ( String )adapter.getPreserved( PROP_VARIANT );
    String newValue = WidgetUtil.getVariant( link );
    if( WidgetLCAUtil.hasChanged( link, PROP_VARIANT, newValue, null ) ) {
      JSWriter writer = JSWriter.getWriterFor( link );
      Object[] args = new Object[] { link, "variant_" + oldValue };
      if( oldValue != null ) {
        writer.callStatic( JS_FUNC_REMOVE_STATE, args );
      }
      if( newValue != null ) {
        args = new Object[] { link, "variant_" + newValue };
        writer.callStatic( JS_FUNC_ADD_STATE, args );
      }
    }
  }
}
