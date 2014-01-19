package ryebread761.lwinrecents;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.widget.FrameLayout;
import de.robv.android.xposed.IXposedHookInitPackageResources;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam;
import de.robv.android.xposed.callbacks.XC_LayoutInflated;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class Main implements IXposedHookLoadPackage, IXposedHookInitPackageResources, IXposedHookZygoteInit{
    private static final String PREF_NAME = "LWIRPrefs";
    public XSharedPreferences prefs;
    public FrameLayout frame;
    public Drawable stockBackground;
    
	@Override
	public void initZygote(StartupParam startupParam) throws Throwable {
		prefs = new XSharedPreferences("ryebread761.lwinrecents", PREF_NAME);
	}
    
    @Override
    public void handleInitPackageResources(InitPackageResourcesParam resparam) throws Throwable {
        if (!resparam.packageName.equals("com.android.systemui"))
            return;
        stockBackground = resparam.res.getDrawable(resparam.res.getIdentifier("status_bar_recents_background", "drawable", "com.android.systemui"));
        
        XC_LayoutInflated hook = new XC_LayoutInflated() {
            @Override
            public void handleLayoutInflated(LayoutInflatedParam liparam) throws Throwable {
                frame = (FrameLayout) liparam.view.findViewById(liparam.res.getIdentifier("recents_bg_protect", "id", "com.android.systemui"));
            }
        };
        
        resparam.res.hookLayout("com.android.systemui", "layout", "status_bar_recent_panel", hook);
        try {
        	resparam.res.hookLayout("com.android.systemui", "layout", "tw_status_bar_recent_panel", hook);
        } catch (Resources.NotFoundException e) {
        	
        } catch (Throwable t) {
        	
        }
    }
    
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
        XposedHelpers.findAndHookMethod("com.android.systemui.recent.RecentsActivity", lpparam.classLoader, "onResume", new XC_MethodHook() {
        	@SuppressWarnings("deprecation")
        	@Override
        	protected void afterHookedMethod(MethodHookParam param)
        			throws Throwable {
        		prefs.reload();
                if (prefs.getBoolean("custom_opacity", false)) { 
                    frame.setBackgroundDrawable(null);
                    frame.setBackgroundColor(Color.argb(prefs.getInt("custom_opacity_value", 0), 0, 0, 0));
                } else {
                	frame.setBackgroundDrawable(stockBackground);
                }
        	}
        });
    }
}
