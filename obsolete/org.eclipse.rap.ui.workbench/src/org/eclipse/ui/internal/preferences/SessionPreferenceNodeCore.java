/*******************************************************************************
 * Copyright (c) 2002-2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.ui.internal.preferences;

import org.eclipse.core.runtime.*;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.rwt.RWT;
import org.eclipse.rwt.internal.util.ParamCheck;
import org.eclipse.rwt.service.*;
import org.eclipse.ui.internal.WorkbenchPlugin;

/**
 * This class is the link between the SessionPreferenceNode hierarchy 
 * (application global) the RWT setting store (session specific).
 */
final class SessionPreferenceNodeCore {
  
  private final SessionPreferencesNode node;
  private ListenerList prefListeners; // ListenerList is thread safe

  /* tracks changes in RWT setting store and notifies the prefListeners */
  private ISettingStoreListener rwtListener;
  /* true to track RWT changes */
  private boolean trackChanges = true;
  /* ignore changes to this key for a short time */
  private String ignoreKey;
  
  SessionPreferenceNodeCore( final SessionPreferencesNode node ) {
    ParamCheck.notNull( node, "node" );
    this.node = node;
  }
  
  void addPreferenceChangeListener( IPreferenceChangeListener listener ) {
    if( listener != null ) {
      getListenerList().add( listener );
      setTrackRWTChanges( true );
    }
  }

  void removePreferenceChangeListener( IPreferenceChangeListener listener ) {
    if( listener != null ) {
      ListenerList list = getListenerList();
      list.remove( listener );
      if( list.isEmpty() ) {
        setTrackRWTChanges( false );
      }
    }
  }
  
  void firePreferenceEvent( final String key, 
                            final String oldValue, 
                            final String newValue )
  {
    if( prefListeners != null ) {
      final PreferenceChangeEvent event 
        = new PreferenceChangeEvent( node, key, oldValue, newValue );
      Object[] listeners = prefListeners.getListeners();
      for( int i = 0; i < listeners.length; i++ ) {
        final IPreferenceChangeListener listener 
          = ( IPreferenceChangeListener )listeners[ i ];
        ISafeRunnable op = new ISafeRunnable() {
          public void handleException( final Throwable exception ) {
            // logged by SafeRunner
          }
          public void run() throws Exception {
            listener.preferenceChange( event );
          }
        };
        SafeRunner.run( op );
      }
    }
  }
  
  void clear() {
    if( prefListeners != null ) {
      prefListeners.clear();
      prefListeners = null;
      setTrackRWTChanges( false );
    }
  }
  
  synchronized String put( final String uniqueKey,
                           final String value ) {
    ISettingStore store = RWT.getSettingStore();
    String result = store.getAttribute( uniqueKey );
    try {
      ignoreKey = uniqueKey;
      store.setAttribute( uniqueKey, value );
      ignoreKey = null;
    } catch( SettingStoreException exc ) {
      String msg = "Could not persist preference: " + uniqueKey;
      WorkbenchPlugin.log( msg, exc );
    }
    return result;
  }
  
  // helping methods
  //////////////////
  
  private synchronized ListenerList getListenerList() {
    if( prefListeners == null ) {
      prefListeners = new ListenerList( ListenerList.IDENTITY );
    }
    return prefListeners;
  }
  
  private synchronized void setTrackRWTChanges( final boolean doTrack ) {
    this.trackChanges = doTrack;
    if( trackChanges ) {
      if( rwtListener == null ) {
       rwtListener = new ISettingStoreListener() {
        public void settingChanged( ISettingStoreEvent event ) {
          if( trackChanges ) {
            String fullKey = event.getAttributeName();
            if( !fullKey.equals( ignoreKey ) ) {
              String absPath = node.absolutePath();
              if( fullKey.startsWith( absPath ) ) {
                String key = fullKey.substring( absPath.length() + 1 );
                String oldValue = event.getOldValue();
                String newValue = event.getNewValue();
                firePreferenceEvent( key, oldValue, newValue );
              }
            }
          }
        }
       };
     }
     RWT.getSettingStore().addSettingStoreListener( rwtListener );
   } else { // !trackChanges
     if( rwtListener != null ) {
       RWT.getSettingStore().removeSettingStoreListener( rwtListener );
     }
   }
  }
  
}
