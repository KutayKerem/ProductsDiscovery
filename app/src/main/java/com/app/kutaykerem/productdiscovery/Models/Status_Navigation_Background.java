package com.app.kutaykerem.productdiscovery.Models;

import android.view.Window;

import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import java.io.Serializable;

public class Status_Navigation_Background  implements Serializable {


    public static void setFullScreen(Window window){
        WindowCompat.setDecorFitsSystemWindows(window,false);
    }
    public static void lightStatusBar(Window window,Boolean isLight  ,Boolean isLightNav){
        WindowInsetsControllerCompat wic = new WindowInsetsControllerCompat(window,window.getDecorView());
        wic.setAppearanceLightStatusBars(isLight);
        wic.setAppearanceLightNavigationBars(isLightNav);
    }




}
