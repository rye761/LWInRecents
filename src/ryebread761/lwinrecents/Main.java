package ryebread761.lwinrecents;

import android.graphics.Color;
import android.widget.FrameLayout;
import de.robv.android.xposed.IXposedHookInitPackageResources;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam;
import de.robv.android.xposed.callbacks.XC_LayoutInflated;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class Main implements IXposedHookLoadPackage, IXposedHookInitPackageResources{
    private static final String PREF_NAME = "LWIRPrefs";

    @Override
    public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable {
        if (!lpparam.packageName.equals("com.android.systemui"))
            return;
        XposedHelpers.findAndHookMethod("com.android.systemui.recent.RecentsActivity", lpparam.classLoader, "onStart", new XC_MethodHook() {

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                XposedHelpers.callMethod(param.thisObject, "updateWallpaperVisibility", true);
            }
        });
    }

    @Override
    public void handleInitPackageResources(InitPackageResourcesParam resparam) throws Throwable {
        if (!resparam.packageName.equals("com.android.systemui"))
            return;
        resparam.res.hookLayout("com.android.systemui", "layout", "status_bar_recent_panel", new XC_LayoutInflated() {
            @SuppressWarnings("deprecation")
            @Override
            public void handleLayoutInflated(LayoutInflatedParam liparam) throws Throwable {
                XSharedPreferences prefs = new XSharedPreferences("ryebread761.lwinrecents", PREF_NAME);
                if (prefs.getBoolean("custom_opacity", false)) {
                    FrameLayout frame = (FrameLayout) liparam.view.findViewById(liparam.res.getIdentifier("recents_bg_protect", "id", "com.android.systemui"));
                    frame.setBackgroundDrawable(null);
                    frame.setBackgroundColor(Color.argb(prefs.getInt("custom_opacity_value", 0), 0, 0, 0));
                }
            }
        });
    }

}
