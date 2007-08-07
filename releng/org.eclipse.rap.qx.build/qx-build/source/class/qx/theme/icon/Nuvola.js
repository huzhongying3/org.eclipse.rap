/* ************************************************************************

   qooxdoo - the new era of web development

   http://qooxdoo.org

   Copyright:
     2004-2007 1&1 Internet AG, Germany, http://www.1and1.org

   License:
     LGPL: http://www.gnu.org/licenses/lgpl.html
     EPL: http://www.eclipse.org/org/documents/epl-v10.php
     See the LICENSE file in the project's top-level directory for details.

   Authors:
     * Sebastian Werner (wpbasti)
     * Andreas Ecker (ecker)

************************************************************************ */

/* ************************************************************************

#require(qx.io.Alias)
#resource(qx.icontheme:icon/Nuvola)

************************************************************************ */

/**
 * Nuvola
 * Author: David Vignoni (david@icon-king.com)
 * License: LGPL
 * Home: http://www.kde-look.org/content/show.php?content=5358
 */
qx.Theme.define("qx.theme.icon.Nuvola",
{
  title : "Nuvola",

  icons : {
    uri : qx.core.Setting.get("qx.resourceUri") + "/icon/Nuvola"
  }
});
