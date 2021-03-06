**What is it?**

CuePointGrooveMachine is a simple instrument that takes any loop (usually a short breakbeat) and calculates 8 evenly-spaced cue-points which can then be triggered from a single octave of a MIDI keyboard. Typical drum slicers will slice a drum loop into discrete sections and only play the one section to which a MIDI note is mapped. CuePointGrooveMachine is different: rather than actually slicing the loop into chunks it simply starts playback of the loop at the corresponding cue-point. It then continues looping through the whole file until another cue-point is received or playback is stopped.

As a result, drum loops can be played and chopped up live much more easily than with a traditional sample slicer, since longer contiguous sections of the loop can be played through only a single keypress. Pattern variations can be created readily without losing the fluid feel of the original drum loop.

There are also 5 effects which can be activated on the fly: hard overdrive, cverb, filter sweep, bitcrusher and soft overdrive. These can be used to add further variation to your performance. Finally, a randomizer is also included. When activated it will trigger playback from a random cue-point at a rate equal to the cue-point spacing.

CuePointGrooveMachine was created with the goal of being fast, fun, accessible and simple to use. Any MIDI keyboard with a 2 octave range is sufficient to access all of its controls. Absolutely no other MIDI messages are required, so even the most minimally equipped keyboard will make a suitable controller. In addition to being simple to use, care was also taken to keep the patch simple to read and understand.

A quick video demo can be found [here](https://youtu.be/YxlrMwZb4fg).

**How do I set it up?**

Before using CuePointGrooveMachine, it is necessary to add the included `CuePointCalc.class` file to Max's java classpath. There are two ways to do this:

1) Move the `CuePointCalc.class` file to max's default mxj classpath. This path will vary depending on your system. See the [mxj reference](https://docs.cycling74.com/max5/refpages/max-ref/mxj.html) for info on where to find it.

2) Add the folder containing `CuePointCalc.class` as an additional path for Max to search. This is done through `Options` -> `File Preferences`.

In order to load any mxj object in Max, it is also necessary to have an appropriate JRE installed. The patch has been tested with the JRE obtained [here](https://www.oracle.com/ca-en/java/technologies/javase-jre8-downloads.html).

**How do I use it?**

Samples can either be selected from the drop-down menu or dragged directly onto the waveform display. Any `.wav` or `.aiff` files added to the `/samples` folder will appear in the drop-down menu so long as they were there when the patch was opened (otherwise you may bang the 'path' message at the top-left of the patch view to re-scan the folder).

The 8 cue-points are mapped to the white keys from C4 to C5. The 5 effects are mapped to the black keys from C#4 to A#4. Randomizer is activated by B3. A#3 deactivates the randomizer but continues regular playback. A3 stops all playback. A helpful onscreen guide is included in the presentation view. Note that the `kslider` used by the guide does not actually output MIDI notes, but is instead only used for visual feedback. Therefore, **a MIDI controller or other external MIDI source is required to use the patch.**

**How does it work?**

An audio buffer is filled by either the `dropfile` or a `umenu` selection. The file is also sent to an `~sfinfo` object which gets the file's duration in milliseconds.

This duration is sent to multiple locations. The most important among them is the `mxj CuePointCalc` object. This is where all of the work is done for mapping notes and triggering cue-points. It works by taking in the duration and dividing it by 8 to find the appropriate cue-point times. These times are then stored in an 8-slot array, where each slot represents one of the 8 keys to which the cue-points are mapped. When CuePointCalc receives a note-on for one of these 8 keys, it sends out a sequence of 3 messages that are received by the `groove~` object. The messages set the loop-start point to the cue-point, trigger the loop start, then immediately reset the loop-start point back to 0 so the loop can continue as normal.

The output of `groove~` is then routed to the `Effectors` subpatch. This is where the 5 effects are configured and routed. a `TogEdge` is responsible for activating/deactivating the wet signal of each effect when its corresponding key is pressed/released. The output of Effectors is then sent to `live.gain`.

For the randomizer the cue-point spacing is calculated and then wired to a `metro` that bangs CuePointCalc. CuePointCalc will activate a random cue-point whenever it receives a bang in its first inlet.

**Some Tips**

-  CuePointGrooveMachine works best with drum loops that are short (8 or 16 beats). Because the cue-points switch playback position immediately, cleanest results are obtained with dry loops. It is also desirable for the drum loop to be in 4/4 time, due to being split into 8ths.

-  Cheap midi keyboards are good! CuePointGrooveMachine is easiest to use with keyboards that have a shallow key travel and light action. If your keyboard is weighted and has a deep travel, you'll need to time your presses a bit early to compensate. Playback is also not velocity-sensitive. This is so that the loop's original dynamics are maintained.

-  The randomizer will trigger a random cue-point immediately when it is pressed. That means you should try to activate it on-beat for a smooth performance.

-  The effects are routed in order from left to right. The softer distortion is useful for bringing some life back into a bitcrushed/filtered signal, but be cautious when using it alongside the harder distortion.

-  Although results typically sound best when the loop's original tempo is respected, feel free to play through the cue-points a bit faster. It can lend a sort of "lazy" feeling to the hats.

-  For tighter timing in a non-live setting, consider driving CuePointGrooveMachine with your preferred sequencer.

-  The LPF sweep effect's rate is synced to the cue-point spacing. Its phase also resets on key press. Try activating it at different times within the loop to filter out different sections of the groove.

**Design Quirks**

-  Cue-points are not lined up with transients, but are instead naively placed at regular intervals along the file. While this can lead to some hits not lining up exactly with the cue-points if the drums aren't perfectly timed, I decided to keep this approach because of the continuous playback feature. If a cue-point were nudged off-grid to accomodate one drum hit, the rest of the loop would become shifted by the same amount until another cue-point is triggered.

-  The parameter settings for each of the effects were chosen by ear based on my own personal tastes (which is to say: they are not exactly subtle). Initially the effect-intensity was modulated by key-velocity, but I found this was a bit too hard to control precisely and felt that it became distracting while playing. Nicer sounding effects with more detailed controls may be a possible direction for my MUMT 307 project.

-  Because the cue-points change the playback position instantaneously some drum loops will have an audible click on cue-point change. I attempted to remedy this with a `line~` object that would ramp from 0 to 1 in a few ms whenever a new cuepoint was activated. While this would work if the ramp-time was high enough, it also resulted in a loss of transients in the drum hits that were lined up with the cue-point. As a result, I decided against using any ramp time. Clicks are avoidable if the loop is dry and tightly timed.

-  Occasionally the playhead on the waveform display may freeze up if the patch has been open for a long time. On my computer it takes about 40 minutes of randomized playback for this to occur. Playback of audio is not affected when this happens. I have not been able to find a cause for this issue, although I suspect it has to do with the `snapshot~` object, as raising its polling rate triggers the issue much sooner and more consistently.

I hope you enjoy messing about and mangling some drums with this tool!

Cheers,

-Navin

*Special thanks to Blu Mar Ten for the included samples. They are part of a sample pack freely available [here](https://www.blumarten.com/product/junglejungle-free-sample-pack/).*
