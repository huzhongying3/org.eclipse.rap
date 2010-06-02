/*******************************************************************************
 * Copyright (c) 2002, 2010 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rwt.internal.service;

import org.eclipse.rwt.internal.lifecycle.HtmlResponseWriter;
import org.eclipse.rwt.internal.protocol.ProtocolMessageWriter;
import org.eclipse.rwt.service.IServiceStore;


public interface IServiceStateInfo extends IServiceStore {
  
  void setResponseWriter( final HtmlResponseWriter reponseWriter );    

  HtmlResponseWriter getResponseWriter();
  
  public void setProtocolMessageWriter( final ProtocolMessageWriter writer );
  
  public ProtocolMessageWriter getProtocolMessageWriter();
  
}