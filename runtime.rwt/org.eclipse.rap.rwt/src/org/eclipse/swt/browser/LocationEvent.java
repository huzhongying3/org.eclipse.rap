/*******************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.swt.browser;

import org.eclipse.swt.events.TypedEvent;
import com.w4t.Adaptable;


/**
 * A <code>LocationEvent</code> is sent by a {@link Browser} to
 * {@link LocationListener}'s when the <code>Browser</code>
 * navigates to a different URL. This notification typically 
 * occurs when the application navigates to a new location with 
 * {@link Browser#setUrl(String)} or when the user activates a
 * hyperlink.
 * 
 * @since 1.0
 */
public class LocationEvent extends TypedEvent {
  
  public static final int CHANGING = 0;
  public static final int CHANGED = 1;
  
  private static final Class LISTENER = LocationListener.class;
  
  /** current location */
	public String location;
	
//	/**
//	 * A flag indicating whether the location opens in the top frame
//	 * or not.
//	 */
//	public boolean top;
	
	/**
	 * A flag indicating whether the location loading should be allowed.
	 * Setting this field to <code>false</code> will cancel the operation.
	 */
	public boolean doit = true;

  public LocationEvent( final Object source, 
                        final int id, 
                        final String location ) 
  {
    super( source, id );
    this.location = location;
  }

  protected void dispatchToObserver( final Object listener ) {
    switch( getID() ) {
      case CHANGING:
        ( ( LocationListener )listener ).changing( this );
      break;
      case CHANGED:
        ( ( LocationListener )listener ).changed( this );
      break;
      default:
        throw new IllegalStateException( "Invalid event handler type." );
    }
  }

  protected Class getListenerType() {
    return LISTENER;
  }

  public static boolean hasListener( final Adaptable adaptable ) {
    return hasListener( adaptable, LISTENER );
  }
  
  public static void addListener( final Adaptable adaptable,
                                  final LocationListener listener )
  {
    addListener( adaptable, LISTENER, listener );
  }

  public static void removeListener( final Adaptable adaptable,
                                     final LocationListener listener )
  {
    removeListener( adaptable, LISTENER, listener );
  }

  public static Object[] getListeners( final Adaptable adaptable ) {
    return getListener( adaptable, LISTENER );
  }
}
