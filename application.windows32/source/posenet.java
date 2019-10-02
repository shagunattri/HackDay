import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import oscP5.*; 
import netP5.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class posenet extends PApplet {

// Import OSC



// Runway Host
String runwayHost = "127.0.0.1";

// Runway Port
int runwayPort = 57100;

int width = 400;
int height = 350;

OscP5 oscP5;
NetAddress myBroadcastLocation;

// This array will hold all the humans detected
JSONObject data;
JSONArray humans;

// This are the pair of body connections we want to form. 
// Try creating new ones!
String[][] connections = {
  {"nose", "leftEye"},
  {"leftEye", "leftEar"},
  {"nose", "rightEye"},
  {"rightEye", "rightEar"},
  {"rightShoulder", "rightElbow"},
  {"rightElbow", "rightWrist"},
  {"leftShoulder", "leftElbow"},
  {"leftElbow", "leftWrist"}, 
  {"rightHip", "rightKnee"},
  {"rightKnee", "rightAnkle"},
  {"leftHip", "leftKnee"},
  {"leftKnee", "leftAnkle"}
};

public void setup() {
  
  frameRate(25);

  OscProperties properties = new OscProperties();
  properties.setRemoteAddress("127.0.0.1", 57200);
  properties.setListeningPort(57200);
  properties.setDatagramSize(99999999);
  properties.setSRSP(OscProperties.ON);
  oscP5 = new OscP5(this, properties);

  // Use the localhost and the port 57100 that we define in Runway
  myBroadcastLocation = new NetAddress(runwayHost, runwayPort);

  connect();

  fill(255);
  stroke(255);
}

public void draw() {
  background(0);
  // Choose between drawing just an ellipse
  // over the body parts or drawing connections
  // between them. or both!
  drawParts();
}


// A function to draw humans body parts as circles
public void drawParts() {
  // Only if there are any humans detected
  if (data != null) {
    humans = data.getJSONArray("poses");
    for(int h = 0; h < humans.size(); h++) {
      JSONArray keypoints = humans.getJSONArray(h);
      // Now that we have one human, let's draw its body parts
      for (int k = 0; k < keypoints.size(); k++) {
        // Body parts are relative to width and weight of the input
        JSONArray point = keypoints.getJSONArray(k);
        float x = point.getFloat(0);
        float y = point.getFloat(1);
        ellipse(x * width, y * height, 10, 10);
      }
    }
  }
}

public void connect() {
OscMessage m = new OscMessage("/server/connect");
  oscP5.send(m, myBroadcastLocation);
}

public void disconnect() {
  OscMessage m = new OscMessage("/server/disconnect");
  oscP5.send(m, myBroadcastLocation);
}

public void keyPressed() {
  switch(key) {
    case('c'):
      /* connect to Runway */
      connect();
      break;
    case('d'):
      /* disconnect from Runway */
      disconnect();
      break;

  }
}

// OSC Event: listens to data coming from Runway
public void oscEvent(OscMessage theOscMessage) {
  if (!theOscMessage.addrPattern().equals("/data")) return;
  // The data is in a JSON string, so first we get the string value
  String dataString = theOscMessage.get(0).stringValue();

  // We then parse it as a JSONObject
  data = parseJSONObject(dataString);
}
  public void settings() {  size(400, 350); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "--present", "--window-color=#666666", "--stop-color=#cccccc", "posenet" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
