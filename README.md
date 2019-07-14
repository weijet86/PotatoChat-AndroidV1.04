# PotatoChat-AndroidV1.04
This is a simple websocket client chat app for Android written in Java.
This is an amatuer project written out of boredom and built for sharing with everyone as well as entertaining everyone.

Instruction of how to use
1) Download the entire file and unzipped it into Android Studio folder in your local disc.
2) Open the entire project in Android Studio.
3) Run "MainActivity" to build project in Android Studio.
4) When the app is open in Android Emulator/Android device. Click Client as the Server is redundant.
5) Run the Node JS server shared in another folder first.
6) Then log in with username and password to chat app.
7) Proceed to signing up for an account if you didn't have an account yet.
8) You could see all signed-in users after you have signed in to the chat app yourself.
9) Enjoy!

Instruction on how to change websocket URI
1) Navigate to file app->java->Util->ClientUni.java.
2) Go to line 74 in code, by default, this line comprises "uri=new URI("ws://10.0.2.2:8080");".
3) 10.0.2.2 is the IP address and 8080 is the port number for Android emulator's localhost.
4) If you wanted to use this client to connect to server outside of localhost machine, simply change the IP address and port number to the ones of the server.
