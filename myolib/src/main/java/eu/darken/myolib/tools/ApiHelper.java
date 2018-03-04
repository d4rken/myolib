/*
 * Android Myo library by darken
 * Matthias Urhahn (matthias.urhahn@rwth-aachen.de)
 * mHealth - Uniklinik RWTH-Aachen.
 */
package eu.darken.myolib.tools;

import android.os.Build;

/**
 * Helper class for Api checks.
 */
public class ApiHelper {

    /**
     * @return if &gt;=21
     */
    public static boolean hasLolliPop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }
}
