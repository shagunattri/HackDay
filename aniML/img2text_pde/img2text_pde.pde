// Import OSC
import oscP5.*;
import netP5.*;

// Runway Host
String runwayHost = "127.0.0.1";

// Runway Port
int runwayPort = 57100;

OscP5 oscP5;
NetAddress myBroadcastLocation;

// The data coming in from Runway as an Object
JSONObject data;

void setup() {
  size(500, 300);
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

void draw() {
  background(0);
  
  // Display captions
  drawCaptions();
}


// A function to display the captions
void drawCaptions() {
  // Only if there are any captions 
  if (data != null) {
    String results = data.getString("caption");
    text(results, 100, 100);
  }
}

void connect() {
  OscMessage m = new OscMessage("/server/connect");
  oscP5.send(m, myBroadcastLocation);
}

void disconnect() {
  OscMessage m = new OscMessage("/server/disconnect");
  oscP5.send(m, myBroadcastLocation);
}

void keyPressed() {
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
void oscEvent(OscMessage theOscMessage) {
  if (!theOscMessage.addrPattern().equals("/data")) return;
  // The data is in a JSON string, so first we get the string value
  String dataString = theOscMessage.get(0).stringValue();

  // We then parse it as a JSONObject
  data = parseJSONObject(dataString);
}
