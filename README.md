==============
For Users
==============

This program is based on the fact that you have one file to play, edit and
add text to it. 

The playback tab is there for premium viewing experience!
While the players in the editor and text tab are used to automatically
play the output file you have produced after doing a functionality


==============
Playback Tab
==============
Allows you to play a video or audio file.

The Camera button will allow you to take pictures of the video you are
viewing. The picture taken will the the current time of the video.

You can skip to any point in time in the video by using the ticker.
Note to use the ticker you must click and drag it to the desired 
location.

==============
Editor Tab 
==============
Allows you to extract audio from a video, replace audio
a video, and overlay audio in a video.
Additional functionality include fades, rotation and flip

You have to select a file before you can use the functionalities.

When giving an audio input for replacing audio or overlaying audio
to a video please give sensible audio files that actually have audio.
 
If you don't the functionailty will clearly not work.

This also applies to giving an output file. When giving a output file
for extracting audio, give a valid audio file extension, we recommend
you use .mp3.
When giving a outputfile for replacing/overlaying audio, give a valid
video extension, we recommend you use .mp4
Or you can not give an extension and we will append an extension
for you!

Since rotating, flipping and adding fade to a video are basic filters,
I did not allow the user to give a output name but just appended what
they did to the original name as the output file.
These output files are stored in the same folder as the original files

==============
Text Tab 
==============
(partner did this so no idea what goes on here)

Allows you to overlay text over the video at the start and/or end of
the media file. The text will appear at center of the screen.

You must select a video/audio file before you can apply the text feature.

This feature is intended to be used for trailers. Applying text to
a long video file will take a long time to apply. Please note it can
take up to 2 minutes to apply text to a 10 minute file.

To use the text filter, enter the desired text into the start and end
text areas and select the start and end text duration in the
respective dropdown boxes. 

Note start and end text each have a set limit of 160 characters and 
5 newlines. You can also set your desired font, font size and font
color.The text preview on the bottom left of the window updates live
to any changes in text display settings. 

When you've selected your desired settings press "Add text" to apply the
text filter to the input video.
After the text is applied, the preview video will play the changed
file.Only the video player in the Text tab will play the changed file.

When you press "Save changes" and choose a valid new output file, the
new output file will be produced. 
If you want to save the current settings in the Text tab you can press
the "Save project" button. Saving your settings will overwrite any
previous saved settings you had. If you want to load your saved
settings press the "Load project" button.

==============
Download Tab
==============
Download allows you download OPEN SOURCE video/audio files from online.
If you attempt to download non-open source files, we will not be liable
for any legal repercussions.

To download and save a file you need to click the "Save to..." button
to select an output file. 
You also have the option to automatically play the downloaded file
immediately after the download completes. This allows you to you
manipulate file immediately after the download has been complete

==============
For Markers
==============

There are two options to run the project:

*	In order to run the project on Eclipse, create the project (adding
into the src folder) as usual. Then you need to add the three jar files
(jna, jna-platform, vlcj) in resource folder to the build path of the
project.

*	Execute from a jar file in the command line using
	java -jar Vamix.jar


===============
Known Errors
===============
There is a lack of error checking for file extensions.
overlay audio
	-> if the input is not an audio file it won't work
	-> if the output is an audio extension it will work but there
	   will be no video
replace audio
	-> if the input is not an audio file it won't work
	-> if the output is not a valid video extension it won't work
extract audio
	->if output is not an audio extension it won't work

General errors
Playback 
	-> Can't click the slider, has to drag to move the video
	-> Mostly likely the player will behave weird when the 
	   video is finished. Fastforwading might do this error
replace audio
	-> if the audio is shorter than video the remaining video will
	   have no audio while the video plays in the background
	-> if video is shorter than video the left over will be a black
	   screen while the audio plays

When there is an error, the file stil gets created

============================
Extra features going to add
============================
Triming of video
Subtitles
Watermark/logo
Speeding up/slowing down of video

=========================
Things "planning" to fix
=========================
All the Known Errors
Progressbar being more useful
Bit more error checking
