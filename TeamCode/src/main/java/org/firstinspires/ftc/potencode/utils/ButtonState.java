package org.firstinspires.ftc.potencode.utils;

public class ButtonState {
    public boolean buttonState = false;
    private boolean downLastUpdate = false;

    public void update(boolean state) {
        // if the button is pressed make sure it was not pressed last loop
        // then update the button state and say not to update it again next loop
        if (state && !downLastUpdate) {
            buttonState = !buttonState;
            downLastUpdate = true;
        }
        else if (!state && downLastUpdate) { // no longer down, and was down, then is not down, so next time pressed will go into first condition
            downLastUpdate = false;
        }
    }

}