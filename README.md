# fake-location

To change location using adb command line:

$ adb shell

//To start faking location, lagitute: float number (-90, 90), longitute: float number (-180, 180)
 
$ am start -n com.linwu.fakelocation/.SetActivity --ef lat {Latitute} --ef long {Longitute}

//To stop faking location

$ am start -n com.linwu.fakelocation/.StopActivity
