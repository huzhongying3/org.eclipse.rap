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
package com.w4t.webbordercomponentkit;

import java.io.IOException;

import com.w4t.*;
import com.w4t.webbordercomponentkit.types.WebBorderComponentType;

/** <p>the superclass of all Renderers that render 
 *  org.eclipse.rap.WebBorderComponent.</p>
 */
public abstract class WebBorderComponentRenderer extends DecoratorRenderer {
  
  protected void createDecoratorHead( final Decorator dec ) throws IOException {
    WebBorderComponent wbc = ( WebBorderComponent )dec;
    WebBorderComponentType borderType
      = WebBorderComponentType.newType( wbc.getBorderType(), wbc );
    borderType.createDecorationHeader( getResponseWriter() );
  }
  
  protected void createDecoratorFoot( final Decorator dec ) throws IOException {
    WebBorderComponent wbc = ( WebBorderComponent )dec;
    WebBorderComponentType borderType
      = WebBorderComponentType.newType( wbc.getBorderType(), wbc );
    borderType.createDecorationFooter( getResponseWriter() );
  }  
}
