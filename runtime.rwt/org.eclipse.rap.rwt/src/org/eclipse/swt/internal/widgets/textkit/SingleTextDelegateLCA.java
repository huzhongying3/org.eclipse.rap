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

package org.eclipse.swt.internal.widgets.textkit;

import java.io.IOException;

import org.eclipse.rwt.internal.lifecycle.JSConst;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Text;

final class SingleTextDelegateLCA extends AbstractTextDelegateLCA {

  private static final String QX_TYPE = "qx.ui.form.TextField";

  static final String TYPE_POOL_ID
    = SingleTextDelegateLCA.class.getName();

  private static final String PROP_SELECTION_LISTENER = "selectionListener";

  private final static JSListenerInfo JS_SELECTION_LISTENER_INFO
    = new JSListenerInfo( JSConst.QX_EVENT_KEYDOWN,
                          "org.eclipse.swt.TextUtil.widgetDefaultSelected",
                          JSListenerType.ACTION );

  void preserveValues( final Text text ) {
    ControlLCAUtil.preserveValues( text );
    TextLCAUtil.preserveValues( text );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( text );
    adapter.preserve( PROP_SELECTION_LISTENER,
                      Boolean.valueOf( hasSelectionListener( text ) ) );
  }

  /* (intentionally non-JavaDoc'ed)
   * readData does not explicitly handle modifyEvents. They are fired implicitly
   * by updating the text property in TextLCAUtil.readText( Text ).
   */
  void readData( final Text text ) {
    // order is crucial: first read text then read what part of it is selected
    TextLCAUtil.readText( text );
    TextLCAUtil.readSelection( text );
    ControlLCAUtil.processSelection( text, null, false );
  }

  void renderInitialization( final Text text ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( text );
    writer.newWidget( QX_TYPE );
    ControlLCAUtil.writeStyleFlags( text );
    TextLCAUtil.writeHijack( text );
  }

  void renderChanges( final Text text ) throws IOException {
    ControlLCAUtil.writeChanges( text );
    TextLCAUtil.writeText( text );
    TextLCAUtil.writeReadOnly( text );
    TextLCAUtil.writeSelection( text );
    TextLCAUtil.writeTextLimit( text );
    TextLCAUtil.writeVerifyAndModifyListener( text );
    writeSelectionListener( text );
  }

  void renderDispose( final Text text ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( text );
    writer.dispose();
  }

  String getTypePoolId( final Text text ) throws IOException {
    return TYPE_POOL_ID;
  }

  void createResetHandlerCalls( final String typePoolId ) throws IOException {
    TextLCAUtil.resetSelection();
    TextLCAUtil.resetModifyListener();
    resetSelectionListener();
    TextLCAUtil.resetTextLimit();
    TextLCAUtil.resetReadOnly();
    TextLCAUtil.resetText();
    ControlLCAUtil.resetChanges();
    ControlLCAUtil.resetStyleFlags();
  }

  private static void writeSelectionListener( final Text text )
    throws IOException
  {
    JSWriter writer = JSWriter.getWriterFor( text );
    writer.updateListener( JS_SELECTION_LISTENER_INFO,
                           PROP_SELECTION_LISTENER,
                           hasSelectionListener( text ) );
  }

  private static boolean hasSelectionListener( final Text text ) {
    // Emulate SWT (on Windows) where a default button takes precedence over
    // a SelectionListener on a text field when both are on the same shell.
    Button defButton = text.getShell().getDefaultButton();
    boolean hasDefaultButton = defButton != null && defButton.isVisible();
    return !hasDefaultButton && SelectionEvent.hasListener( text );
  }

  private static void resetSelectionListener() throws IOException {
    JSWriter writer = JSWriter.getWriterForResetHandler();
    writer.removeListener( JS_SELECTION_LISTENER_INFO.getEventType(),
                           JS_SELECTION_LISTENER_INFO.getJSListener() );
  }
}