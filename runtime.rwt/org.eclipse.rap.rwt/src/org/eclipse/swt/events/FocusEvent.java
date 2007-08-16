/*******************************************************************************
 * Copyright (c) 2007 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.swt.events;

import org.eclipse.rwt.Adaptable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.internal.widgets.EventUtil;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;


/**
 * Instances of this class are sent as a result of widgets gaining and losing
 * focus.
 * 
 * @see FocusListener
 */
public final class FocusEvent extends TypedEvent {

  public static final int FOCUS_GAINED = SWT.FocusIn;
  public static final int FOCUS_LOST = SWT.FocusOut;
  
  private static final Class LISTENER = FocusListener.class;

  public FocusEvent( final Event event ) {
    this( ( Control )event.widget, event.type );
  }
  
  public FocusEvent( final Control source, final int id ) {
    super( source, id );
  }

  protected void dispatchToObserver( final Object listener ) {
    switch( getID() ) {
      case FOCUS_GAINED:
        ( ( FocusListener )listener ).focusGained( this );
      break;
      case FOCUS_LOST:
        ( ( FocusListener )listener ).focusLost( this );
        break;
      default:
        throw new IllegalStateException( "Invalid event handler type." );
    }
  }

  protected Class getListenerType() {
    return LISTENER;
  }

  protected boolean allowProcessing() {
    return EventUtil.isAccessible( widget );
  }

  public static void addListener( final Adaptable adaptable, 
                                  final FocusListener listener )
  {
    addListener( adaptable, LISTENER, listener );
  }

  public static void removeListener( final Adaptable adaptable, 
                                     final FocusListener listener )
  {
    removeListener( adaptable, LISTENER, listener );
  }
  
  public static boolean hasListener( final Adaptable adaptable ) {
    return hasListener( adaptable, LISTENER );
  }
  
  public static Object[] getListeners( final Adaptable adaptable ) {
    return getListener( adaptable, LISTENER );
  }
}
