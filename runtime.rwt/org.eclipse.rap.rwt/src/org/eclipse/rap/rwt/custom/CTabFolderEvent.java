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

package org.eclipse.rap.rwt.custom;

import org.eclipse.rap.rwt.events.RWTEvent;
import org.eclipse.rap.rwt.graphics.Rectangle;
import org.eclipse.rap.rwt.widgets.Widget;
import com.w4t.Adaptable;

public class CTabFolderEvent extends RWTEvent {
  
  private static final int CLOSE = 0;
  private static final int MINIMIZE = 1;
  private static final int MAXIMIZE = 2;
  private static final int RESTORE = 3;
  private static final int SHOW_LIST = 4;
  private static final Class LISTENER = CTabFolder2Listener.class;
  
  public static CTabFolderEvent createClose( final CTabItem item ) {
    CTabFolderEvent result = new CTabFolderEvent( item.getParent(), CLOSE );
    result.item = item;
    result.doit = true;
    return result;
  }
  
  public static CTabFolderEvent createMinimize( final CTabFolder tabFolder ) {
    return new CTabFolderEvent( tabFolder, MINIMIZE );
  }
  
  public static CTabFolderEvent createMaximize( final CTabFolder tabFolder ) {
    return new CTabFolderEvent( tabFolder, MAXIMIZE );
  }
  
  public static CTabFolderEvent createRestore( final CTabFolder tabFolder ) {
    return new CTabFolderEvent( tabFolder, RESTORE );
  }
  
  public static CTabFolderEvent createShowList( final CTabFolder tabFolder,
                                                final Rectangle chevronRect ) {
    // TODO [rh] revise which fields have to be set
    CTabFolderEvent result = new CTabFolderEvent( tabFolder, SHOW_LIST );
    result.x = chevronRect.x;
    result.y = chevronRect.y;
    result.height = chevronRect.height;
    result.width = chevronRect.width;
    result.doit = true;
    return result;
  }
  
  public Widget item;
  public boolean doit;
  public int x;
  public int y;
  public int width;
  public int height;
  
  private CTabFolderEvent( final Object source, final int id ) {
    super( source, id );
  }

  protected void dispatchToObserver( final Object listener ) {
    switch( getID() ) {
      case CLOSE:
        ( ( CTabFolder2Listener )listener ).close( this );
      break;
      case MINIMIZE:
        ( ( CTabFolder2Listener )listener ).minimize( this );
      break;
      case MAXIMIZE:
        ( ( CTabFolder2Listener )listener ).maximize( this );
      break;
      case RESTORE:
        ( ( CTabFolder2Listener )listener ).restore( this );
      break;
      case SHOW_LIST:
        ( ( CTabFolder2Listener )listener ).showList( this );
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
                                  final CTabFolder2Listener listener )
  {
    addListener( adaptable, LISTENER, listener );
  }

  public static void removeListener( final Adaptable adaptable,
                                     final CTabFolder2Listener listener )
  {
    removeListener( adaptable, LISTENER, listener );
  }

  public static Object[] getListeners( final Adaptable adaptable ) {
    return getListener( adaptable, LISTENER );
  }
}
