==============
Starting Up
==============

There are two options to run the project:

*	In order to run the project on Eclipse, create the project (adding
into the src folder) as usual. Then you need to add the three jar files
(jna, jna-platform, vlcj) in resource folder to the build path of the
project.

*	Execute from a jar file in the command line using
	java -jar Vamix.jar


==============
Introduction
==============

This program is based on the fact that you have one file to play, edit and
add text to it. 

The playback tab is there for premium viewing experience!
While the players in the editor and text tab are used to automatically
play the output file you have produced after doing a functionality


=============
Playback
=============

To play a video or audio file press the choose file button in the bottom left corner.
Select a file to play. 

There are typical play buttons such as play, pause, stop, fast forward and rewind in the button middle.

In the bottom left corner there are two buttons that adjustes the volume.
The button on the left mutes/unmutes the video
The button on the right lets you adjust the volume

There is also a camera button next to the choose file button. 
The camera button allows you to take a screen shot of the current playing video.
The images are saved to where the video file is located.


=============
Editor
=============

To edit a video file make sure you have selected a file first.

The player on the left is a preview player.
Once you have performed a functionality you can view the changes you have made with the player.

Basic functions:
All Basic functions output files are saved to where the input video file is located

Rotate -> Allows you to rotate the video based on a selected angle from the chooser.
Flip -> Allows you to either vertically flip or horizontally flip the video.
Fade -> Allows you to fade in from the start and fade out to the end for up to 59 seconds.

Advance functions:
All advance functions require you to enter an output name;

Extract Audio 
-> Takes out the audio from the video and saves it to a audio file.
-> You can specify a part of the video to extract the audio from by unticking the "extract audio from whole file"

Replace Audio
-> Removes the current audio from the video and replaces it with an input audio track.
-> Select an audio track with Choose File button
-> Give a name for the output file. You can also change the location of where the output file is by using the Save To button

Overlay Audio
-> Merges the current audio from the video with an input audio track.
-> Select an audio track with Choose File button
-> Give a name for the output file. You can also change the location of where the output file is by using the Save To button

Add Subtitles
->The Table on the right shows a list of subtitles that are currently in video
->The Area on the left allows you to add subtitles to the table

->The add subtitles button adds subtitles to the table once input has been given for the start time, end time and text has been entered
->The edit subtitles button allows you to edit a subtitles in the table.
->The save changes button will save the edits you have made to a selected subtitle.
->The delete subtitles button allows you to delete a subtitle from the table

->The save subtitles button allows you to save all the subtitles into a .ass file that is named the same as the video. 
   This will allow you to preview the subtitles using the player.
   Note this .ass file has to be in the same directory for you to preview the subtitles.
->The add stream button will add a subtitle stream to a video. 
   Meaning that the .ass file does not need to be in the same dirctory as the video.


============
Text
============

This allows you to add opening and closing text a video.
Once again the player is there to show you the end result.
The Text under the player shows you what the text looks like when you adjust the font,size of the font and the colour of the text

You start by entering text into the two text fields on the right 
Then select the duration you want for the opening and closing texts to last for.

Then you can adjust the colour, font and size of the font you want the text to be.

To preview the change you would then press the Add text button.
After the text has been added you can view the video on the player.

The save changes button will save changes made to the video to a new output video.
This will require you to name a output file.

The save project button will allow you to save the current project you are working on.
Note this will override any previous projects you have saved.

The load project button will allow you to load the previously saved project.



=============
Download
=============

This allows you to download open source media from the internet!
WARNING: If you attempt to download non-open source files, we will not be liable for any legal repercussions.
The default location of where the file is saved is your downloads folder

Enter a URL in the textbox.
You can select to play the video/audio file after the download has finished.
You MUST tick that the video/audio file is open source
Then press download and watch the magic of wget get the video from the web!

You can select a different place to save the video/audio file by changing the location via the save to button.
This will also allow you to change the name of the video/audio file

You can also pause the download while the file is being downloaded. This will keep the file.
To resume the download just press the download button again!
You can also cancel the download while the file is being download. This will delete the file.

===========
Thanks
===========
I hope you enjoy your experience with Vamix and create awesome content with this video audio mixer! =D
