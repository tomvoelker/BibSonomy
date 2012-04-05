import com.google.zxing.BinaryBitmap;
import com.google.zxing.BufferedImageLuminanceSource;
import com.google.zxing.client.result.ParsedResult;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.multi.qrcode.QRCodeMultiReader;
import com.google.zxing.Result;
import com.google.zxing.client.result.ResultParser;

import flash.display.BitmapData;

import flash.text.TextFormat;
	
import mx.containers.TitleWindow;
import spark.components.HGroup;
import mx.core.FlexGlobals;
import mx.core.mx_internal;
use namespace mx_internal;

import mx.controls.Text;
import mx.events.CloseEvent;
import mx.events.FlexEvent;
import mx.managers.PopUpManager;
	
import spark.components.mediaClasses.DynamicStreamingVideoSource;
		
import flash.events.Event;
import flash.events.TimerEvent;
import flash.media.Camera;
import flash.media.SoundChannel;
import flash.media.Video;
import flash.utils.Timer;
import flash.text.TextField;
	
import flash.geom.Rectangle;
	
import flash.external.ExternalInterface;
		
private var detectionRate:int = 10;
			
private var cam:Camera;
private var video:Video;
		
private var refreshTimer:Timer;
		
private var errorWindow:TitleWindow;

private var errorGroup:HGroup;

private var errorLabel:Text;

private var channel:SoundChannel;

private var myReader:QRCodeMultiReader;
		
[Bindable]
public var camWidth:int;
		
[Bindable]
public var camHeight:int;
		
[Bindable]
public var errorHeader:String;
		
[Bindable]
public var errorMessage:String;
		
private function init():void 
{	
	camWidth = FlexGlobals.topLevelApplication.parameters.dynamicWidth;
	camHeight = FlexGlobals.topLevelApplication.parameters.dynamicHeight;
			
	errorHeader = FlexGlobals.topLevelApplication.parameters.dynamicErrorHeader;
	errorMessage = FlexGlobals.topLevelApplication.parameters.dynamicErrorMessage;
	
	// initialize the webcam			
	cam = Camera.getCamera();
	
	myReader = new QRCodeMultiReader();
			
	if(cam != null)
	{				
		cam.setMode(camWidth,camHeight,25);
		
		video = new Video(cam.width, cam.height);
		video.attachCamera(cam);
		
		var matrix:Matrix = new Matrix();
		matrix.a = -1;
		matrix.tx = video.width;
		
		video.transform.matrix = matrix;
		
		theCam.addChild(video);

		refreshTimer = new Timer(1000/detectionRate);
		refreshTimer.addEventListener(TimerEvent.TIMER, decodeSnapshot);
		refreshTimer.start();
	}
			
	else
	{
		theCam.visible = false;
		displayErrorMessage();	
	}
}

private function decodeSnapshot(evt:TimerEvent):void
{
    // try to decode the current snapshpt
    var oldMatrix:Matrix = video.transform.matrix;
    
    var matrix:Matrix = new Matrix();
	matrix.a = -1;
	matrix.tx = video.width;
		
	video.transform.matrix = matrix;
    
    var bmd:BitmapData = new BitmapData(video.width, video.height);
    bmd.draw(video);
    
    video.transform.matrix = oldMatrix;
    
    this.decodeBitmapData(bmd, video.width, video.height);
}

public function decodeBitmapData(bmpd:BitmapData, width:int, height:int):void
{
	// create the container to store the image width and height in
	var lsource:BufferedImageLuminanceSource = new BufferedImageLuminanceSource(bmpd);
	// convert it to a binary bitmap
	var bitmap:BinaryBitmap = new BinaryBitmap(new HybridBinarizer(lsource));
	// get all the hints
				
	var res:Result = null;
	try
	{
		// try to decode the image
		res = myReader.decode(bitmap);
	}
	
	catch(e:*) 
	{
		
	}
	
	// did we find something?
	if(res != null)
	{
		// yes : parse the result
		var parsedResult:ParsedResult = ResultParser.parseResult(res);
		// get a formatted string and display it in our textarea
		var regex:RegExp = /^(((ht|f)tp(s?))\:\/\/)?(www.|[a-zA-Z].)[a-zA-Z0-9\-]+(\b|\.org)(\:[0-9]+)*\/+bibtex\/[a-fA-F0-9]*\/[a-zA-z0-9]+$/;
		if (regex.test( parsedResult.getDisplayResult()))
		{
			refreshTimer.stop();
		
			if (!previewBox.visible)
			{			
				if (ExternalInterface.available)
				{
					ExternalInterface.call("urlFromFlash",  parsedResult.getDisplayResult());
				}
					
				var picture:BitmapData = new BitmapData(theCam.width, theCam.height);
			
				//the BitmapData draws our theCam
				picture.draw(theCam);		
			
				//Our preview's source is a new Bitmap made of picture's BitmapData
				preview.source = new Bitmap(picture);
				
				channel = mySound.sound.play();
				channel.addEventListener(Event.SOUND_COMPLETE, stopSnap);
						
				//makes the previewBox visible, so we can see our picture
				previewBox.visible = true;
			
				//displays the flashLight
				flashLight.visible = true;
			
				//makes the flashLight go way
				flashLight.visible = false;
				
				var restartTimer:Timer = new Timer(2000, 1);
				restartTimer.addEventListener(TimerEvent.TIMER_COMPLETE, restart);
				restartTimer.start();
			}
		}
	}
}

private function restart(evt:TimerEvent):void
{
	previewBox.visible = false;
	refreshTimer.start();
}
		
private function stopSnap(evt:Event):void
{
	channel.stop();
}

private function displayErrorMessage():void
{				
	errorWindow = new TitleWindow();
	errorWindow.title = errorHeader;
	errorWindow.showCloseButton = false;
	errorWindow.explicitWidth = camWidth * 0.9;
	errorWindow.explicitHeight = camHeight * 0.9;
	errorWindow.horizontalScrollPolicy = "off";
	errorWindow.verticalScrollPolicy = "off";
	errorWindow.addEventListener(FlexEvent.INITIALIZE, createErrorMessage);
	errorWindow.setStyle("backgroundColor", "0xeeeeee");	
	
	errorLabel = new Text();
	errorLabel.text = errorMessage;
	errorLabel.explicitWidth = errorWindow.explicitWidth * 0.95;
	
	errorLabel.addEventListener(FlexEvent.CREATION_COMPLETE, resizeErrorLabel);
	errorWindow.addChild(errorLabel);
				
	PopUpManager.addPopUp(errorWindow, this, true);
	PopUpManager.centerPopUp(errorWindow);
}

private function resizeErrorLabel(evt:FlexEvent):void
{
	errorLabel.validateNow();
	
	var maxTextHeight:int = errorWindow.explicitHeight * 0.8;
	
	if (maxTextHeight != 0)
	{
		var text_field:TextField = errorLabel.mx_internal::getTextField();
		var f:TextFormat = text_field.getTextFormat();
		
		while (text_field.textHeight > maxTextHeight)
		{
			f.size = int(f.size) -1;
			text_field.setTextFormat(f);
		}
	}
	
}
		
private function createErrorMessage(evt:FlexEvent):void
{
	errorWindow.isPopUp = false;
}