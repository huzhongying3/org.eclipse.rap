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

package org.eclipse.rap.rwt.resources;

import com.w4t.IResourceManager.RegisterOptions;

/**
 * TODO: [fappel] comment
 */
public interface IResource {
  ClassLoader getLoader();
  String getLocation();
  String getCharset();
  RegisterOptions getOptions();
  boolean isJSLibrary();
  boolean isExternal();
}
