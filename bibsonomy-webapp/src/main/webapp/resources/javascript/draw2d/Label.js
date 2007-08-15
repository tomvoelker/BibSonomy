/* This notice must be untouched at all times.

Open-jACOB Draw2D
The latest version is available at
http://www.openjacob.org

Copyright (c) 2006 Andreas Herz. All rights reserved.
Created 5. 11. 2006 by Andreas Herz (Web: http://www.freegroup.de )

LICENSE: LGPL

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License (LGPL) as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA,
or see http://www.gnu.org/copyleft/lesser.html
*/

/**
 * @version 0.7.15
 * @author Andreas Herz
 * @param {String} msg The text of the label object
 * @constructor
 */
function Label(/*:String*/ msg)
{
  /** @private **/
  this.msg = msg;
  /** @private **/
  this.bgColor = null;
  /** @private **/
  this.color = new Color(0,0,0);
  /** @private **/
  this.fontSize= 10;
  /** @private **/
  this.textNode = null;
  this.linkNode = null;
  /** @private **/
  this.align = "center";
  Figure.call(this);
}

Label.prototype = new Figure;
/** @private **/
Label.prototype.type="Label";

/**
 * @private
 **/
Label.prototype.createHTMLElement=function()
{
    var item = Figure.prototype.createHTMLElement.call(this);
    this.textNode = document.createTextNode(this.msg);
    item.appendChild(this.textNode);
    item.style.color=this.color.getHTMLStyle();
    item.style.fontSize=this.fontSize+"pt";
    item.style.width="auto";
    item.style.height="auto";
//    item.style.padding="2px";
    item.style.paddingLeft="3px";
    item.style.paddingRight="3px";
    item.style.textAlign=this.align;

    if(this.bgColor!=null)
      item.style.backgroundColor=this.bgColor.getHTMLStyle();
    return item;
}

/**
 * @returns Returns always false in the case of a Label.
 * @type boolean
 **/
Label.prototype.isResizeable=function()
{
  return false;
}

Label.prototype.setWordwrap=function(/*:boolean*/ flag)
{
  this.html.style.whiteSpace=flag?"wrap":"nowrap";
}

/**
 * @param {int} w The new width of the figure
 * @param {int} h The new height of the figure
 **/
/*
Label.prototype.setDimension=function( w, h)
{
  // ignore: Das Label bestimmt seine Breite/Höhe selbst.
}
*/

/**
 * 
 * @param {String} align The new align of the label ["left", "center", "right"]
 **/
Label.prototype.setAlign=function( /*:String*/ align)
{
  // ignore: Das Label bestimmt seine Breite/Höhe selbst.
  this.align = align;
  this.html.style.textAlign=align;
}

/**
 *
 **/
Label.prototype.setBackgroundColor= function(color /*:Color*/)
{
  this.bgColor = color;
  if(this.bgColor!=null)
    this.html.style.backgroundColor=this.bgColor.getHTMLStyle();
  else
    this.html.style.backgroundColor="transparent";
}

/**
 * @param {Color} color The new font color of the label.
 **/
Label.prototype.setColor= function(color /*:Color*/)
{
  this.color = color;
  this.html.style.color = this.color.getHTMLStyle();
}

/**
 * @param {int} size The new font size in <code>pt</code>
 **/
Label.prototype.setFontSize= function(size /*:int*/)
{
  this.fontSize = size;
  this.html.style.fontSize = this.fontSize+"pt";
}

Label.prototype.getWidth=function()
{
  try
  {
    return parseInt(getComputedStyle(this.html,'').getPropertyValue("width"));
  }
  catch(e)
  {
    return (this.html.clientWidth);
  }
  return 100;
}

Label.prototype.getHeight=function()
{
  try
  {
    return parseInt(getComputedStyle(this.html,'').getPropertyValue("height"));
  }
  catch(e)
  {
    return (this.html.clientHeight);
  }
  return 30;
}

/**
 * @param {String} text The new text for the label.
 **/
Label.prototype.setText=function(text /*String*/)
{
  this.msg = text;
  this.html.removeChild(this.textNode);
  this.textNode = document.createTextNode(this.msg);
  this.html.appendChild(this.textNode);
}

Label.prototype.setLinkText=function(text /*String*/, link /*String*/)
{
  this.msg = text;
  this.html.removeChild(this.textNode);
  var href = document.createAttribute("href");
  href.nodeValue = link;
  this.linkNode = document.createElement("a");
  this.linkNode.setAttributeNode(href)
  this.textNode = document.createTextNode(this.msg);
  this.linkNode.appendChild(this.textNode);
  this.html.appendChild(this.linkNode);
}

Label.prototype.setStyledText=function(text /*String*/)
{
  this.msg = text;
  this.html.removeChild(this.textNode);
  this.textNode = document.createElement("div");
  this.textNode.style.whiteSpace="nowrap";
  this.textNode.innerHTML=text;
  this.html.appendChild(this.textNode);
}
