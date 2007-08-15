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
 * 
 * @version 0.7.15
 * @author Andreas Herz
 * @constructor
 */
function Workflow(/*:String*/id)
{
   /** @private */
   this.gridWidthX = 10; /*:int*/
   /** @private */
   this.gridWidthY = 10; /*:int*/
   /** @private */
   this.enableSnapToGrid = false;

   // Ein Workflow enthält Elemente
   //
   /** @private */
   this.figures     = new Object();
   /** @private */
   this.lines       = new Object();
   /** @private */
   this.commonPorts = new Object();
   /** @private */
   this.dropTargets = new Object();
   /** @private */
   this.compartments = new Object(); // objects which can manage children
   /** @private */
   this.selectionListeners = new Object();
   /** @private */
   this.dialogs = new Object();

   /** @private */
   this.toolPalette = null;
   /** @private */
   this.dragging = false;

   /** @private **/
   this.commandStack = new CommandStack();

   /** @private */
   this.oldScrollPosLeft = 0;
   this.oldScrollPosTop  = 0;

   /** @private */
   this.currentSelection = null;            // primary selection
   /** @private */
   this.currentSelections = new Array();    // secondary selection
 
   /** @private */
   this.currentMenu = null; /*:Menu*/

   /** @private */
   this.connectionLine    = new Line();
   /** @private */
   this.resizeHandleStart = new LineStartResizeHandle(this); // 
   /** @private */
   this.resizeHandleEnd   = new LineEndResizeHandle(this);

   /** @private */
   this.resizeHandle1 = new ResizeHandle(this,1); // 1 = LEFT TOP
   /** @private */
   this.resizeHandle2 = new ResizeHandle(this,2); // 2 = CENTER_TOP
   /** @private */
   this.resizeHandle3 = new ResizeHandle(this,3); // 3 = RIGHT_TOP
   /** @private */
   this.resizeHandle4 = new ResizeHandle(this,4); // 4 = RIGHT_MIDDLE
   /** @private */
   this.resizeHandle5 = new ResizeHandle(this,5); // 5 = RIGHT_BOTTOM
   /** @private */
   this.resizeHandle6 = new ResizeHandle(this,6); // 6 = CENTER_BOTTOM
   /** @private */
   this.resizeHandle7 = new ResizeHandle(this,7); // 7 = LEFT_BOTTOM
   /** @private */
   this.resizeHandle8 = new ResizeHandle(this,8); // 8 = LEFT_MIDDLE

   Canvas.call(this,id);

   /** @private */
   this.setPanning(false); // flag for the panning feature of the workflow canvas

  if(this.html!=null)
  {
    this.html.style.backgroundImage="url(grid_10.png)";
    /*
     * Determine which method to use to add the event handler.
     */
    oThis = this;
    // Firefox seems to need to have the tabindex="0" property set to some value 
    // so it knows this Div or Span is keyboard selectable. That allows the keyboard 
    // event to be triggered. It is not so dumb - you might want to trap Delete or 
    // Insert keys on a table etc. 
    this.html.tabIndex="0";
    var contextMenu = function ()
    {
      var oEvent = arguments[0] || window.event;
      var diffX = oEvent.clientX - oThis.html.offsetLeft;
      var diffY = oEvent.clientY - oThis.html.offsetTop;
      var scrollLeft= document.body.parentNode.scrollLeft;
      var scrollTop = document.body.parentNode.scrollTop;
      if(oThis.getBestFigure(diffX+scrollLeft, diffY+scrollTop)!=null)
         return;
      oThis.onContextMenu(diffX+scrollLeft, diffY+scrollTop);
    };
    this.html.oncontextmenu=function(){return false;};
    var oThis = this;
    var keyDown=function(event)
    {
//      event.cancelBubble = true;
//      event.returnValue = false;
      var ctrl = event.ctrlKey;
      oThis.onKeyDown(event.keyCode, ctrl);
    }
    var mouseDown = function()
    {
      var oEvent = arguments[0] || window.event;
      var diffX = oEvent.clientX - oThis.html.offsetLeft;
      var diffY = oEvent.clientY - oThis.html.offsetTop;
      var scrollLeft= document.body.parentNode.scrollLeft;
      var scrollTop = document.body.parentNode.scrollTop;
      oThis.onMouseDown(diffX+scrollLeft, diffY+scrollTop);
    }
    var mouseUp = function()
    {
      var oEvent = arguments[0] || window.event;
      if(oThis.currentMenu!=null)
      {
         oThis.removeFigure(oThis.currentMenu);
         oThis.currentMenu=null;
      }
      if(oEvent.button==2)
         return;
      var diffX = oEvent.clientX - oThis.html.offsetLeft;
      var diffY = oEvent.clientY - oThis.html.offsetTop;
      var scrollLeft= document.body.parentNode.scrollLeft;
      var scrollTop = document.body.parentNode.scrollTop;
      oThis.onMouseUp(diffX+scrollLeft, diffY+scrollTop);
    }
    var mouseMove = function()
    {
      var oEvent = arguments[0] || window.event;
      if(Drag.currentHover!=null)
      {
          var oDropEvent = new DragDropEvent();
          oDropEvent.initDragDropEvent("mouseleave", false, oThis);
          Drag.currentHover.dispatchEvent(oDropEvent);
          Drag.currentHover = null;
      }
      else
      {
         var diffX = oEvent.clientX - oThis.html.offsetLeft;
         var diffY = oEvent.clientY - oThis.html.offsetTop;
         var scrollLeft= document.body.parentNode.scrollLeft;
         var scrollTop = document.body.parentNode.scrollTop;
         oThis.onMouseMove(diffX+scrollLeft, diffY+scrollTop);
      }
    }
    if (this.html.addEventListener) 
    {
        this.html.addEventListener("contextmenu", contextMenu, false);
        this.html.addEventListener("mousemove", mouseMove, false);
        this.html.addEventListener("mouseup", mouseUp, false);
        this.html.addEventListener("mousedown", mouseDown, false);
        this.html.addEventListener("keydown", keyDown, false);
    } 
    else if (this.html.attachEvent) 
    {
        this.html.attachEvent("oncontextmenu", contextMenu);
        this.html.attachEvent("onmousemove", mouseMove);
        this.html.attachEvent("onmousedown", mouseDown);
        this.html.attachEvent("onmouseup", mouseUp);
        this.html.attachEvent("onkeydown", keyDown);
    } 
    else 
    {
        throw new Error("Open-jACOB.Graphics not supported in this browser.");
    }
  }
}


// Workflow leitet von Canvas ab
//
Workflow.prototype = new Canvas;
/** @private **/
Workflow.prototype.type="Workflow";

/**
 * This method will be called from the framework of the document has been scrolled.
 * The canvas/workflow adjust the dialog/window element which has been a fixed position.
 *
 * @final
 * @private
 **/
Workflow.prototype.onScroll=function()
{

  var scrollLeft= document.body.parentNode.scrollLeft;
  var scrollTop = document.body.parentNode.scrollTop;

  var diffLeft =   scrollLeft - this.oldScrollPosLeft;
  var diffTop  =   scrollTop - this.oldScrollPosTop;

  var figures = this.getFigures();
  for(key in figures)
  {
    var figure = figures[key];
    if(figure!=null && figure.hasFixedPosition && figure.hasFixedPosition()==true)
      figure.setPosition(figure.getX()+diffLeft, figure.getY()+diffTop);
  }

  this.oldScrollPosLeft= scrollLeft;
  this.oldScrollPosTop = scrollTop;
}

Workflow.prototype.setPanning=function(/*:boolean*/flag)
{
  this.panning= flag;
  if(flag)
    this.html.style.cursor="move";
  else
    this.html.style.cursor=null;
}

/**
 * You can scroll the view to a well defined position.
 *
 * @param {int} x the new X scroll position
 * @param {int} y the Y scroll position
**/
Workflow.prototype.scrollTo=function(/*:int*/ x, /*:int*/y, /*:boolean*/ fast)
{
  if(fast)
  {
   document.body.parentNode.scrollLeft= x;
   document.body.parentNode.scrollTop = y;
  }
  else
  {
   var steps= 40;
   var xStep = (x-document.body.parentNode.scrollLeft)/steps;
   var yStep = (y-document.body.parentNode.scrollTop)/steps;
   
   var oldX = document.body.parentNode.scrollLeft;
   var oldY = document.body.parentNode.scrollTop;
   for(var i=0;i<steps;i++)
   {
      document.body.parentNode.scrollLeft= oldX + (xStep*i);
      document.body.parentNode.scrollTop = oldY + (yStep*i);
   }
  }
}


/**
 *
 * @param {Dialog} dialog The dialog to show.
 * @param {int} x The x position.
 * @param {int} y The y position.
 **/
Workflow.prototype.showDialog=function(/*:Dialog*/dialog ,/*:int*/ xPos ,/*:int*/ yPos)
{
  if(xPos)
    this.addFigure(dialog,xPos, yPos);
  else
    this.addFigure(dialog,200, 100);

  this.dialogs[dialog.id]=dialog;
}

/**
 * @param {Menu} menu The menu to show.
 * @param {int} x The x position.
 * @param {int} y The y position.
 * @private
 **/
Workflow.prototype.showMenu=function(/*:Menu*/menu ,/*:int*/ xPos ,/*:int*/ yPos)
{
 if(this.menu!=null)
 {
   this.html.removeChild(this.menu.getHTMLElement());
   this.menu.setWorkflow();
 }

 this.menu =menu;
 if(this.menu!=null)
 {
   this.menu.setWorkflow(this);
   this.menu.setPosition(xPos,yPos);

   this.html.appendChild(this.menu.getHTMLElement());
   this.menu.paint();
  }
}

/**
 * Callback method for the context menu interaction.
 * Don't override this method! Implement getContextMenu instead.
 *
 * @see #getContextMenu
 * @private
 * @final
 * @param {int} x The absolute x coordinate of the right mouse button click
 * @param {int} y The absolute y coordinate of the right mouse button click
 **/
Workflow.prototype.onContextMenu=function(/*:int*/ x, /*:int*/y)
{
    var menu = this.getContextMenu();
    if(menu!=null)
      this.showMenu(menu,x,y);
}

/**
 * @returns null or the Menu object for this figure.
 * @type Menu
 **/
Workflow.prototype.getContextMenu=function()
{
   return null;
}

/**
 * Set the tool window of this canvas. At the moment a workflow instance can only handle one tool window instance.
 * @param {Window} toolWindow The tool window of the canvas
 **/
Workflow.prototype.setToolWindow=function(/*:Window*/ toolWindow)
{
  this.toolPalette = toolWindow;
  this.addFigure(toolWindow,20,20);
  this.dialogs[toolWindow.id]=toolWindow;
}

/**
 * Set the tool window of this canvas. At the moment a workflow instance can only handle one tool window instance.
 * @param {Window} toolWindow The tool window of the canvas
 **/
Workflow.prototype.setSnapToGrid=function(/*:boolean*/ flag)
{
  this.enableSnapToGrid = flag;
}

/**
 * Set the tool window of this canvas. At the moment a workflow instance can only handle one tool window instance.
 * @param {Window} toolWindow The tool window of the canvas
 **/
Workflow.prototype.setGridWidth=function(/*:int*/ dx,/*:int*/ dy)
{
   this.gridWidthX = dx;
   this.gridWidthY = dy;
}

/**
 * Add a figure at the hands over position.
 *
 * @param {Figure} figure The figure to add.
 * @param {int} x The x position.
 * @param {int} y The y position.
 **/
Workflow.prototype.addFigure=function(/*:Figure*/ figure ,/*:int*/ xPos, /*:int*/ yPos)
{
  figure.setWorkflow(this);
  Canvas.prototype.addFigure.call(this,figure,xPos,yPos);

  var oThisWorkflow = this;
  // Compartments must be stored in an additional structure
  //
  if(figure.isCompartment()==true)
  {
    this.compartments[figure.id] = figure;
  }

  if(figure.isLine()==true)
  {
    this.lines[figure.id]=figure
  }
  else
  {
    this.figures[figure.id]=figure;
    figure.draggable.addEventListener("dragend", function (oEvent)
    {
//      var figure = oThisWorkflow.figures[oEvent.target.element.id];
    });
    figure.draggable.addEventListener("dragstart", function (oEvent)
    {
      var figure = oThisWorkflow.figures[oEvent.target.element.id];
      if(figure==null)
        return;

      if(figure.isSelectable()==false)
        return;

      oThisWorkflow.showResizeHandles(figure);
      oThisWorkflow.setCurrentSelection(figure);
    });
    figure.draggable.addEventListener("drag", function (oEvent)
    {
      var figure = oThisWorkflow.figures[oEvent.target.element.id];
      if(figure == null)
        return;
      if(figure.isSelectable()==false)
        return;

      oThisWorkflow.moveResizeHandles(figure);
    });
  }
  this.setDocumentDirty();
  return figure;
}

/**
 * Remove a figure from the canvas.
 *
 * @param {Figure} figure The figure to remove
 *
 **/
Workflow.prototype.removeFigure = function(/*:Figure*/ figure)
{
    Canvas.prototype.removeFigure.call(this, figure);

    figure.setWorkflow(null);

    if(figure.isCompartment()==true)
       this.compartments[figure.id] = null;

    if(figure.isConnector()==true)
       figure.disconnect();

    if(this.currentSelection == figure)
      this.setCurrentSelection(null);

    this.figures[figure.id]=null;
    this.lines[figure.id]=null;
    this.dialogs[figure.id]=null;

    this.setDocumentDirty();
}

/**
 * @private
 **/
Workflow.prototype.getBestCompartmentFigure=function(/*:int*/ x, /*:int*/ y, /*:Figure*/ figureToIgnore)
{
  var result = null;
  var figures = this.getFigures();
  for(key in figures)
  {
    var figure = figures[key];
    if(figure!=null && figure.isCompartment()==true && figure.isOver(x,y)==true && figure!=figureToIgnore)
    {
        if(result==null)
           result = figure;
        else if(result.getZOrder() < figure.getZOrder())
           result = figure;
    }
  }
  return result;
}

/**
 * @private
 **/
Workflow.prototype.getBestFigure=function(/*:int*/ x, /*:int*/ y, /*:Figure*/ figureToIgnore)
{
  var result = null;
  var figures = this.getFigures();
  for(key in figures)
  {
    var figure = figures[key];
    if(figure!=null && figure.isOver(x,y)==true && figure!=figureToIgnore)
    {
        if(result==null)
           result = figure;
        else if(result.getZOrder() < figure.getZOrder())
           result = figure;
    }
  }
  return result;
}

/**
 * @private
 **/
Workflow.prototype.getFigure=function(/*:String*/ id)
{
  return this.figures[id];
}


/**
 * @private
 **/
Workflow.prototype.getFigures=function()
{
  return this.figures;
}

/**
 * @type Document
 **/
Workflow.prototype.getDocument=function()
{
  return new Document(this);
}

/**
 * @see Window#onSelectionChanged
 * @param {Window} w The window which will be notified if an object has been selected
 **/
Workflow.prototype.addSelectionListener=function(/*:Window*/ w)
{
  this.selectionListeners[w.id] = w;
}

/**
 * @see Window#onSelectionChanged
 * @param {Window} w The window which will be removed from the slection eventing
 **/
Workflow.prototype.removeSelectionListener=function(/*:Window*/ w )
{
  this.selectionListeners[w.id]=null;
}

/**
 * Set the current selected figure in the workflow canvas.
 *
 * @param {Figure} figure The new selection.
 **/
Workflow.prototype.setCurrentSelection=function(/*:Figure*/ figure )
{
  if(figure==null)
  {
    this.hideResizeHandles();
    this.hideLineResizeHandles();
  }

  this.currentSelection = figure;

  // Testen ob eine Linie getroffen wurde
  //
  for(key in this.selectionListeners)
  {
    var w = this.selectionListeners[key];
    if(w!=null && w.onSelectionChanged)
      w.onSelectionChanged(this.currentSelection);
  }
}


/**
 * Returns the current selected figure in the canvas.
 *
 * @param {Figure} figure The new selection.
 **/
Workflow.prototype.getCurrentSelection=function()
{
  return this.currentSelection;
}

/**
 *
 **/
Workflow.prototype.getLines=function()
{
  return this.lines;
}

/**
 * @param {Port} port The new port which has beend added to the workflow canvas.
 * @private
 **/
Workflow.prototype.registerPort = function(/*:Port*/ port )
{
  this.commonPorts[port.id]=port;

  // Alle ELemente haben die gleichen DropTargets
  //
  port.draggable.targets= this.dropTargets;
  this.dropTargets[port.id]=port.dropable;
}

/**
 * @param {Port} p The port which has been removed from the workflow canvas.
 * @private
 **/
Workflow.prototype.unregisterPort = function(/*:Port*/ port )
{
  port.targets=null;
  this.commonPorts[port.id]=null;
  this.dropTargets[port.id]=null;
}
/**
 * Returns the command stack for the canvas. Required for undo/redo  support.
 *
 * @type CommandStack
 **/
Workflow.prototype.getCommandStack = function()
{
  return this.commandStack;
}

/**
 * @private
 **/
Workflow.prototype.showConnectionLine=function(/*:int*/ x1  ,/*:int*/ y1 ,/*:int*/ x2,/*:int*/ y2 )
{
  this.connectionLine.setStartPoint(x1,y1);
  this.connectionLine.setEndPoint(x2,y2);
  if(this.connectionLine.canvas==null)
  {
    //this.trace(x1+","+y1+"=>"+x2+","+y2);
    Canvas.prototype.addFigure.call(this,this.connectionLine);
  }
}

/**
 * @private
 **/
Workflow.prototype.hideConnectionLine=function()
{
  if(this.connectionLine.canvas!=null)
    Canvas.prototype.removeFigure.call(this,this.connectionLine);
}

/**
 * @param {Line} line The line for the resize handles.
 * @private
 **/
Workflow.prototype.showLineResizeHandles=function(/*:Line*/ figure )
{
  var resizeWidthHalf = this.resizeHandleStart.getWidth()/2;
  var resizeHeightHalf= this.resizeHandleStart.getHeight()/2;
  Canvas.prototype.addFigure.call(this,this.resizeHandleStart,figure.getStartX()-resizeWidthHalf,figure.getStartY()-resizeWidthHalf);
  Canvas.prototype.addFigure.call(this,this.resizeHandleEnd,figure.getEndX()-resizeWidthHalf,figure.getEndY()-resizeWidthHalf);
  this.resizeHandleStart.setCanDrag(figure.isResizeable());
  this.resizeHandleEnd.setCanDrag(figure.isResizeable());
  if(figure.isResizeable())
  {
    var green = new Color(0,255,0);
    this.resizeHandleStart.setBackgroundColor(green);
    this.resizeHandleEnd.setBackgroundColor(green);
  }
  else
  {
    this.resizeHandleStart.setBackgroundColor(null);
    this.resizeHandleEnd.setBackgroundColor(null);
  }
}

/**
 * @private
 **/
Workflow.prototype.hideLineResizeHandles=function()
{
  if(this.resizeHandleStart.canvas!=null)
    Canvas.prototype.removeFigure.call(this,this.resizeHandleStart);
  if(this.resizeHandleEnd.canvas!=null)
    Canvas.prototype.removeFigure.call(this,this.resizeHandleEnd);
}

/**
 * @private
 **/
Workflow.prototype.showResizeHandles=function(/*:Figure*/ figure)
{
  this.hideLineResizeHandles();
  this.hideResizeHandles();
  var resizeWidth = this.resizeHandle1.getWidth();
  var resizeHeight= this.resizeHandle1.getHeight();
  var objHeight   = figure.getHeight();
  var objWidth    = figure.getWidth();
  var xPos = figure.getX();
  var yPos = figure.getY();
  Canvas.prototype.addFigure.call(this,this.resizeHandle1,xPos-resizeWidth,yPos-resizeHeight);
  Canvas.prototype.addFigure.call(this,this.resizeHandle3,xPos+objWidth,yPos-resizeHeight);
  Canvas.prototype.addFigure.call(this,this.resizeHandle5,xPos+objWidth,yPos+objHeight);
  Canvas.prototype.addFigure.call(this,this.resizeHandle7,xPos-resizeWidth,yPos+objHeight);

  this.resizeHandle1.setCanDrag(figure.isResizeable());
  this.resizeHandle3.setCanDrag(figure.isResizeable());
  this.resizeHandle5.setCanDrag(figure.isResizeable());
  this.resizeHandle7.setCanDrag(figure.isResizeable());
  if(figure.isResizeable())
  {
    var green = new Color(0,255,0);
    this.resizeHandle1.setBackgroundColor(green);
    this.resizeHandle3.setBackgroundColor(green);
    this.resizeHandle5.setBackgroundColor(green);
    this.resizeHandle7.setBackgroundColor(green);
  }
  else
  {
    this.resizeHandle1.setBackgroundColor(null);
    this.resizeHandle3.setBackgroundColor(null);
    this.resizeHandle5.setBackgroundColor(null);
    this.resizeHandle7.setBackgroundColor(null);
  }

  if(figure.isStrechable() && figure.isResizeable())
  {
    Canvas.prototype.addFigure.call(this,this.resizeHandle2,xPos+(objWidth/2),yPos-resizeHeight);
    Canvas.prototype.addFigure.call(this,this.resizeHandle4,xPos+objWidth,yPos+(objHeight/2)-(resizeHeight/2));
    Canvas.prototype.addFigure.call(this,this.resizeHandle6,xPos+(objWidth/2),yPos+objHeight);
    Canvas.prototype.addFigure.call(this,this.resizeHandle8,xPos-resizeWidth,yPos+(objHeight/2)-(resizeHeight/2));
  }
}

/**
 * @private
 **/
Workflow.prototype.hideResizeHandles=function()
{
  if(this.resizeHandle1.canvas!=null)
    Canvas.prototype.removeFigure.call(this,this.resizeHandle1);
  if(this.resizeHandle2.canvas!=null)
    Canvas.prototype.removeFigure.call(this,this.resizeHandle2);
  if(this.resizeHandle3.canvas!=null)
    Canvas.prototype.removeFigure.call(this,this.resizeHandle3);
  if(this.resizeHandle4.canvas!=null)
    Canvas.prototype.removeFigure.call(this,this.resizeHandle4);
  if(this.resizeHandle5.canvas!=null)
    Canvas.prototype.removeFigure.call(this,this.resizeHandle5);
  if(this.resizeHandle6.canvas!=null)
    Canvas.prototype.removeFigure.call(this,this.resizeHandle6);
  if(this.resizeHandle7.canvas!=null)
    Canvas.prototype.removeFigure.call(this,this.resizeHandle7);
  if(this.resizeHandle8.canvas!=null)
    Canvas.prototype.removeFigure.call(this,this.resizeHandle8);
}

/**
 * @private
 **/
Workflow.prototype.moveResizeHandles=function(/*:Figure*/ figure)
{
  var resizeWidth = this.resizeHandle1.getWidth();
  var resizeHeight= this.resizeHandle1.getHeight();
  var objHeight   = figure.getHeight();
  var objWidth    = figure.getWidth();
  var xPos = figure.getX();
  var yPos = figure.getY();
  this.resizeHandle1.setPosition(xPos-resizeWidth,yPos-resizeHeight);
  this.resizeHandle3.setPosition(xPos+objWidth,yPos-resizeHeight);
  this.resizeHandle5.setPosition(xPos+objWidth,yPos+objHeight);
  this.resizeHandle7.setPosition(xPos-resizeWidth,yPos+objHeight);
  if(figure.isStrechable())
  {
    this.resizeHandle2.setPosition(xPos+(objWidth/2),yPos-resizeHeight);
    this.resizeHandle4.setPosition(xPos+objWidth,yPos+(objHeight/2)-(resizeHeight/2));
    this.resizeHandle6.setPosition(xPos+(objWidth/2),yPos+objHeight);
    this.resizeHandle8.setPosition(xPos-resizeWidth,yPos+(objHeight/2)-(resizeHeight/2));
  }
}

/**
 * @private
 **/
Workflow.prototype.onMouseDown=function(/*:int*/ x, /*:int*/ y)
{
  this.dragging = true;
  this.mouseDownPosX = x;
  this.mouseDownPosY = y;

  // testen ob ein tool aktive ist und diese Aktion
  // an das Tool weiter leiten
  //
  if(this.toolPalette!=null && this.toolPalette.getActiveTool()!=null)
  {
    this.toolPalette.getActiveTool().execute(x,y);
  }

  this.setCurrentSelection(null);
  this.showMenu(null);

  // Testen ob eine Linie getroffen wurde
  //
  for(key in this.lines)
  {
    var line = this.lines[key];
    if(line!=null && line.containsPoint(x,y) && line.isSelectable())
    {
      this.hideResizeHandles();
      this.setCurrentSelection(line);
      this.showLineResizeHandles(this.currentSelection);
      break;
    }
  }
}

/**
 * @private
 **/
Workflow.prototype.onMouseUp=function(/*:int*/ x ,/*:int*/ y)
{
  this.dragging = false;
}

/**
 * @private
 **/
Workflow.prototype.onMouseMove=function(/*:int*/ x ,/*:int*/ y)
{
  if(this.dragging==true && this.panning==true)
  {
   var diffX = x-this.mouseDownPosX;
   var diffY = y-this.mouseDownPosY;

   // set the new viewpoint
   //
   this.scrollTo(document.body.parentNode.scrollLeft-diffX,  document.body.parentNode.scrollTop-diffY,true);

   // adjust all palletes and toolbars
   //
   this.onScroll();
  }
}

/**
 * @private
 **/
Workflow.prototype.onKeyDown=function( /*:int*/ keyCode, /*:boolean*/ ctrl)
{
  // "Figure" löscht sich selbst, da dies den KeyDown Event empfangen
  // kann. Bei einer Linie geht dies leider nicht, und muss hier abgehandelt werden.
  //
  if(keyCode==46 && this.currentSelection!=null && this.currentSelection.isDeleteable())
     this.commandStack.execute(new CommandDelete(this.currentSelection));
  else if(keyCode==90 && ctrl)
     this.commandStack.undo();
  else if(keyCode==89 && ctrl)
     this.commandStack.redo();
}

/**
 * @private
 **/
Workflow.prototype.setDocumentDirty=function()
{
  for(key in this.dialogs)
  {
    var d = this.dialogs[key];
    if(d!=null && d.onSetDocumentDirty)
      d.onSetDocumentDirty();
  }
}


/** 
   Snap to grid, double version. Epsilon is added to the gridsnap value,
 * so that isXAligned(x,margin) always implies that snapToGridX(x,margin)
 * will snap.
 * @param x  position to snap
 * @param gridsnapx  snap margin, 0.0 means no snap
 **/
Workflow.prototype.snapToGridX=function( x) 
{
   if(this.enableSnapToGrid==false)
     return x;
   return this.gridWidthX*Math.floor(((x + this.gridWidthX/2.0) / this.gridWidthX));
}

/** 
 * Snap to grid, double version. Epsilon is added to the gridsnap value,
 * so that isYAligned(y,margin) always implies that snapToGridY(y,margin)
 * will snap.
 * @param y  position to snap
 * @param gridsnapy  snap margin, 0.0 means no snap   
 **/
Workflow.prototype.snapToGridY=function ( y)
{
   if(this.enableSnapToGrid==false)
     return y;
   return this.gridWidthY* Math.floor(((y + this.gridWidthY/2.0) / this.gridWidthY));
}
