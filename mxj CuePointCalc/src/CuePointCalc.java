import com.cycling74.max.*;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A simple mxj class meant to calculate a set evenly spaced cue points based on the length of a loop
 * and trigger the corresponding cue point based on note pressed. The cue point is sent to a groove~ object, which
 * then has its loop point reset. Because playback is continuous, the left inlet receives only the note number,
 * and not take any other midi info. A bang in left inlet triggers random note start.
 * @author Navin K.
 */
public class CuePointCalc extends MaxObject
{
    private final int[] CUEPOINTNOTES = {72, 74, 76, 77, 79, 81, 83, 84};
    private final int STOP = 69;
    private final float[] arrCuePoints = new float[8];

    private static final String[] INLET_ASSIST = new String[] {
            "MIDI notes B3 - C5 (white keys only, triggers output), bang for random", //inlet 0
            "Duration of of audio file (in ms)" //inlet 1
    };
    private static final String[] OUTLET_ASSIST = new String[] {
            "Messages for Cue Points", //outlet 0
    };

    /**
     * @param args unused
     */
    public CuePointCalc(Atom[] args) {
        declareInlets(new int[]{DataTypes.INT, DataTypes.FLOAT});
        declareOutlets(new int[]{DataTypes.MESSAGE});

        setInletAssist(INLET_ASSIST);
        setOutletAssist(OUTLET_ASSIST);

        createInfoOutlet(false);
        Arrays.fill(arrCuePoints, 0);
    }

    /**
     * Posts a message to console when a message is received at either inlet
     * @param input unused
     * @param args unused
     */
    public void anything(String input, Atom[] args) {
        post("This isnt a message I can do anything with");
    }

    /**
     * calls this.anything() if a list is received at either inlet
     * @param list
     */
    public void list(Atom[] list) {
        anything("", list); //any list is invalid
    }

    /**
     * triggers random cuepoint or posts message if wrong inlet
     */
    public void bang() {
        int inletNum = getInlet();
        if (inletNum == 0) {
            triggerCuePoint(ThreadLocalRandom.current().nextInt(0, arrCuePoints.length));
        }
        else { post("Banging me wont do anything useful"); }
    }

    /**
     * activates whenever an integer is received at an inlet.
     * @param intReceived integer value received
     */
    public void inlet(int intReceived) {
        int inletNum = getInlet();
        if (inletNum == 1) { setCuePoints(intReceived); } //setCuePoints will accept integers just fine
        else { //inletNum == 0
            if (intReceived == STOP) {
                sendStop();
                return;
            }
            for (int idx = 0; idx < CUEPOINTNOTES.length; idx++) {
                if (intReceived == CUEPOINTNOTES[idx]) {
                    triggerCuePoint(idx);
                    return;
                }
            }
        }
    }

    /**
     * activates whenever an integer is received at an inlet.
     * @param floatReceived float value received
     */
    public void inlet(float floatReceived) {
        int inletNum = getInlet();
        if (inletNum == 1) { setCuePoints(floatReceived); }
        //no 'else' here - sending a float to inlet 0 is meaningless
    }

    /**
     * Sends the messages to set loop start, at certain cue point, trigger loop and then reset loopstart to 0
     * @param cueIndex index of cue point to start looping from
     */
    public void triggerCuePoint(int cueIndex) {
        outlet(0, "loopstart", arrCuePoints[cueIndex]);
        outlet(0, "startloop");
        outlet(0, "loopstart", 0);
    }

    /**
     * Calculates the cue points to each be 1/8th of a given file duration.
     * @param duration duration of file in ms
     */
    public void setCuePoints(float duration) {
        float slice = duration / 8f;
        for (int idx = 0; idx < arrCuePoints.length; idx++) {
            arrCuePoints[idx] = slice * idx;
        }
        post("cue point times are " + Arrays.toString(arrCuePoints));
    }

    /**
     * Sends a stop message. should trigger whenever B3 is pressed.
     */
    public void sendStop() {
        outlet(0, "stop");
    }
}