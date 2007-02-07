/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH. All rights
 * reserved. This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Innoopract Informationssysteme GmbH - initial API and
 * implementation
 ******************************************************************************/
package org.eclipse.rap.ui.entrypoint;

import org.eclipse.rap.rwt.graphics.Point;
import org.eclipse.rap.ui.IWorkbenchWindow;

public interface IWorkbenchWindowConfigurer {

  IWorkbenchWindow getWindow();
  IActionBarConfigurer getActionBarConfigurer();
  
  public String getTitle();
  public void setTitle(String title);
  
  boolean getShowCoolBar();
  void setShowCoolBar( boolean show );
  
  boolean getShowPerspectiveBar();

  Point getInitialSize();
  void setInitialSize( Point initialSize );
}
