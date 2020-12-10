**What is it?**

CuePointGrooveMachine is a simple instrument that takes any loop (usually a short breakbeat) and calculates 8 evenly-spaced cue-points which can then be triggered from a single octave of a MIDI keyboard.
Typical drum slicers will slice a drum loop into discrete sections and only play the one section to which a MIDI note is mapped. CuePointGrooveMachine is different: it simply starts playback of the audio file at the corresponding cue-point and then continues looping through the whole file.

As a result, drum loops can be played and chopped up live much more easily than with a traditional sample slicer, since it is not necessary to trigger each distinct drum hit with a different key. Pattern variations can be created readily without losing the fluid feel of the drum loop.

There are also 5 effects which can be activated on the fly: hard overdrive, cverb, filter sweep, bitcrusher and soft overdrive. These can be used to add further variations and dynamics to your performance. Finally, a randomizer is also included. When activated it will trigger playback from a random cue-point a rate equal to the cue-point spacing.

CuePointGrooveMachine was created with the goal of being fast, fun, accessible and simple to use. 
Any MIDI keyboard with a 2 octave range is sufficient to access all of its controls. Absolutely no other MIDI info is needed.

**How do I set it up?**

Before using CuePointGrooveMachine, it is necessary to add the included CuePointCalc.class file to Max's java classpath. There are two ways to do this:

1) Move the `.class` file to max's default classpath: On Windows this is found under `Program Files\Cycling '74\Max 8\resources\packages\max-mxj\java-classes\classes`, 
and on Mac this should* be located under `/Applications/Max*/java-doc/classes`. It is not necessary to include the `.java` file in the classpath.

2) Edit the `max.java.config` file under `java-doc` and add the folder containing `CuePointCalc.class` as an additional search path.

I recommend the first option, personally.

*I do not have a Mac to confirm this. Please let me know if the path is innacurate!

**How do I use it?**

Samples can either be selected from the drop-down menu or dragged directly onto the waveform display. Any `.wav` or `.aiff` files added to the `/samples` folder
will appear in the drop-down menu so long as they were there when the patch was opened (otherwise bang the "path" message at the top to rescan the folder).

The 8 cue-points are mapped to the C-Maj scale from C4 to C5. The 5 effects are mapped to the black keys from C#4 to A#4.
Randomizer is activated by B3. A#3 deactivates the randomizer but continues regular playback. A3 stops all playback.
A helpful onscreen guide is included in the presentation view.

**How does it work?**

An audio buffer is filled by either the dropfile or a menu selection. This is sent to an `~sfinfo` object which gets the file's duration in milliseconds.

This duration is sent to multiple locations, but the most important one is the `mxj CuePointCalc` object. This is where 
most of the work is done for mapping notes and triggering cue-points. It works by taking in the duration and dividing it by 8 to find the appropriate
cue-point times. These times are then stored in an 8-slot array, where each slot represents one of the 8 keys to which the cue-points are mapped.
When CuePointCalc receives a note-on for one of these 8 keys, it sends out a sequence of 3 messages that are received by the `groove~` object.
The messages set the loop-start point to the cue-point, trigger the loop start, then immediately reset the loop-start point back to 0 so the loop can continue as normal.

The output of `groove~` is then routed to the `Effectors` subpatch. This is where the 5 effects are configured. a `TogEdge` is responsible for activating/deactivating the wet signal of each effect when its corresponding key is pressed/released. The output of Effectors is then sent to `live.gain`.

For the randomizer, the cue-point spacing is also calculated, then wired to a `metro` that bangs CuePointCalc. CuePointCalc will activate a random cue-point Whenever it receives a bang in its first inlet.

**Some Tips**

-  CuePointGrooveMachine works best with drum loops that are short (8 or 16 beats). Because the cue-points switch playback position immediately, cleanest results
are obtained with dry loops. It is also desirable for the drum loop to be in 4/4 time, due to being split into 8ths.

-  Cheap midi keyboards are good! CuePointGrooveMachine is easiest to use with keyboards that have a shallow key travel and light action. If your keyboard
is weighted and has a deep travel, you'll need to time your presses a bit early to compensate.

-  The randomizer will trigger a random cue-point immediately when it is pressed. That means you should activate it on-beat.

-  The effects are routed in from left to right. The softer distortion is useful for bringing some life back into a bitcrushed/filtered signal, but be cautious
when using it alongside the harder distortion.

-  Although results typically sound best when the loop's original tempo is respected, feel free to play through the cue-points a bit faster. It can lend a sort of
"lazy" feeling to the hats.

-  For tighter timing in a non-live setting, consider driving the CuePointGrooveMachine with your preferred sequencer.

-  The LPF sweep effect is synced to the cue-point length. Its phase also resets on key press. Try activating it at different times within the loop to filter out different portions.

**Design Quirks**

-  Cue-points are not lined up with transients, and are instead only placed at regular intervals along the file.
While this can lead to some hits not lining up exactly with the cue-point if the drums aren't perfectly timed, 
I decided to keep this approach because the continuous playback feature. If the cue-point had been nudged off-grid to accomodate one drum hit,
the rest of the loop would become shifted by the same amount until another cue-point is triggered.

-  The parameter settings for each of the effects were chosen by ear based on my own personal tastes (which is to say: they are mostly not subtle). 
Initially the effect-intensity was modulated by key-velocity, but I found this was a bit too hard to control precisely and became distracting while playing.
Nicer sounding effects and tighter controls for them may be a possible direction for my MUMT 307 project.

-  Because the cue-points change the playback position instantaneousl some drum loops will have have an audible click on cue-point change. I attempted to remedy this with
a `line~` object that would ramp from 0 to 1 in a few ms whenever a new cuepoint was activated. While this would work if the ramp-time was high enough, it also resulted in a loss of transients in the drum hits that were lined up with the cue point. As a result, I decided against the ramp time. Clicks are avoidable if the loop is dry and tightly timed.

I hope you enjoy messing about and mangling some drums with this tool!

Cheers,

-Navin

*Thanks to Blu Mar Ten for the included samples. They are part of a sample pack freely available [here](https://www.blumarten.com/product/junglejungle-free-sample-pack/).*
