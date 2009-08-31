/******************************************************************************* 
* Copyright (c) 2009 EclipseSource and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   EclipseSource - initial API and implementation
*******************************************************************************/ 
package org.eclipse.rap.interactiondesign.tests.impl;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;


public class ViewPartImpl extends ViewPart {

  public ViewPartImpl() {
  }

  public void createPartControl( Composite parent ) {
    IToolBarManager toolBarManager 
      = getViewSite().getActionBars().getToolBarManager();
    toolBarManager.add( new Action() {
      public void run() {
        System.out.println("action pressed");
      };
      
      public String getText() {
        return "An action";
      };
    } );
  }

  public void setFocus() {
  }
  

  
}
