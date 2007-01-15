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

package org.eclipse.rap.jface.resource;

import java.io.*;
import java.net.URL;
import org.eclipse.rap.rwt.graphics.Image;
import org.eclipse.rap.rwt.resources.ResourceManager;


class URLImageDescriptor extends ImageDescriptor {

  private final URL url;
  
  URLImageDescriptor( final URL url ) {
    this.url = url;
  }

  public Image createImage() {
    String path = url.toString();
    String schema = "bundleentry://";
    int pos = path.indexOf( schema );
    if( pos != -1 ) {
      path = path.substring( pos + schema.length() );
    }
    ResourceManager.getInstance().register( path, getStream() );
    return Image.find( path );
  }

  protected InputStream getStream() {
    BufferedInputStream result = null;
    try {
      result = new BufferedInputStream( url.openStream() );
    } catch( IOException e ) {
    }
    return result;
  }
}
