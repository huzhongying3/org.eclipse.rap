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

package org.eclipse.rwt.service;

/**
 * <code>SessionStoreListener</code>s are
 * used to get notifcations before the session store is destroyed.                                session.
 */
public class SessionStoreEvent {
  
  private final ISessionStore sessionStore;
  
  /**
   * Creates a new instance of <code>SessionStoreEvent</code>.
   */
  public SessionStoreEvent( final ISessionStore sessionStore ) {
    this.sessionStore = sessionStore;
  }
  
  /**
   * Returns the <code>ISessionStore</code> that is about to be destroyed.                                session.
   */
  public ISessionStore getSessionStore() {
    return sessionStore;
  }
}
