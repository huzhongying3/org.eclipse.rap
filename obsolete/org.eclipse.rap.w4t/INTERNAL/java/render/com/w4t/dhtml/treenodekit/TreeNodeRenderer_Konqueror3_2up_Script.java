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
package com.w4t.dhtml.treenodekit;

/**
 * <p>
 * The renderer for org.eclipse.rap.dhtml.TreeNode on Konqueror 3.2 and later.
 * </p>
 */
public class TreeNodeRenderer_Konqueror3_2up_Script
  extends TreeNodeRenderer_DOM_Script
{

  String getVerticalAlign() {
    return "top";
  }
}