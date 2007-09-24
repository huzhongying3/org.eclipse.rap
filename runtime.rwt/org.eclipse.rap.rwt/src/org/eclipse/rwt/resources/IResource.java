/*******************************************************************************
 * Copyright (c) 2002-2007 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rwt.resources;

import org.eclipse.rwt.resources.IResourceManager.RegisterOptions;

//* @since 1.0
public interface IResource {
  ClassLoader getLoader();
  String getLocation();
  String getCharset();
  RegisterOptions getOptions();
  boolean isJSLibrary();
  boolean isExternal();
}
