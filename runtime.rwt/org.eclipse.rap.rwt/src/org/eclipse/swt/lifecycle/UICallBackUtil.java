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

package org.eclipse.swt.lifecycle;

import org.eclipse.swt.internal.lifecycle.UICallBackServiceHandler;
import org.eclipse.swt.internal.widgets.IDisplayAdapter;
import org.eclipse.swt.widgets.Display;

/**
 * A utility class that provides some static helper methods to perform
 * commonly needed tasks with respect to backround thread management.
 */
public class UICallBackUtil {
    
  /**
   * Sometimes a backround thread needs to access values that are stored
   * in the session object that started the thread. In particular these
   * values may be stored in session singletons. Accessing these singletons
   * directly from the background thread would fail. This method fakes the
   * missing request context and allows the runnable code to access those 
   * singletons.
   * 
   * @param display The display that is bound to the session that contains the
   *                data to which the current thread should get access.
   * @param runnable The runnable that contains the critical code that 
   *                 needs to have access to a request context.
   *        
   * @see <code>SessionSingletonBase</code>
   * @see <code>ContextProvider</code>
   */
  public static void runNonUIThreadWithFakeContext( final Display display,
                                                    final Runnable runnable )
  {
    UICallBackServiceHandler.runNonUIThreadWithFakeContext( display,
                                                            runnable,
                                                            false );
  }
  

  public static IDisplayAdapter getAdapter( final Display display ) {
    return ( IDisplayAdapter )display.getAdapter( IDisplayAdapter.class );
  }
  
  /**
   * To allow automatically UI-updates by server side background threads
   * activate the UICallBack mechanism. Call this method before the start of
   * a thread and {@link deactivateUICallBack} at the end. Each activation
   * needs a session unique identifier as a kind of reference pointer to be able
   * to decide when all background threads are finished.
   * 
   * <p>Note: this method can only be called in the UI-Thread of a RWT 
   *          application.</p>
   * 
   * @param id A session unique identifier to trace the activation and
   *           deactivation.
   *           
   * @see <code>Display#syncExcec</code>
   * @see <code>Display#asyncExcec</code>
   * @see <code>Display#getThread</code>
   * @see <code>Display#wake</code>
   */
  public static void activateUICallBack( final String id ) {
    UICallBackServiceHandler.activateUICallBacksFor( id );
  }
  
  /**
   * To allow automatically UI-updates by server side background threads
   * activate the UICallBack mechanism. Call {@link deactivateUICallBack} method
   * before the start of a thread and deactivateUICallBack at the end. Each 
   * activation needs a session unique identifier as a kind of reference pointer
   * to be able to decide when all background threads are finished.
   * 
   * <p>Note: this method can only be called in the UI-Thread of a RWT 
   *          application.</p>
   *          
   * @param id A session unique identifier to trace the activation and
   *           deactivation.
   *           
   * @see <code>Display#syncExcec</code>
   * @see <code>Display#asyncExcec</code>
   * @see <code>Display#getThread</code>
   * @see <code>Display#wake</code>
   */
  public static void deactivateUICallBack( final String id ) {
    UICallBackServiceHandler.deactivateUICallBacksFor( id );
  }
  
  private UICallBackUtil() {
    // prevent instance creation
  }
}
