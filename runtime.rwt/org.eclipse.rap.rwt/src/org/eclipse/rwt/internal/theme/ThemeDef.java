/*******************************************************************************
 * Copyright (c) 2007-2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rwt.internal.theme;


public class ThemeDef {

  public final String name;

  public final String inherit;

  public final QxType defValue;

  public final String description;

  public String targetPath;

  public ThemeDef( final String name,
                   final String inherit,
                   final QxType defValue,
                   final String description )
  {
    this.name = name;
    this.inherit = inherit;
    this.defValue = defValue;
    this.description = description;
  }
}
