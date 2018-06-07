package com.example;

import org.robovm.apple.foundation.Foundation;
import org.robovm.apple.foundation.NSAutoreleasePool;
import org.robovm.apple.systemconfiguration.SCNetworkReachabilityFlags;
import org.robovm.apple.uikit.UIApplication;
import org.robovm.apple.uikit.UIApplicationDelegateAdapter;
import org.robovm.apple.uikit.UIApplicationLaunchOptions;
import org.robovm.apple.uikit.UIScreen;
import org.robovm.apple.uikit.UIWindow;
import org.robovm.pods.reachability.NetworkReachability;
import org.robovm.pods.reachability.NetworkReachabilityListener;

public class Main extends UIApplicationDelegateAdapter {
    private UIWindow window;
    private MyViewController rootViewController;

    static NetworkReachability internetReachability;
    static NetworkReachability wifiReachability;


    @Override
    public boolean didFinishLaunching(UIApplication application, UIApplicationLaunchOptions launchOptions) {
        // Set up the view controller.
        rootViewController = new MyViewController();

        // Create a new window at screen size.
        window = new UIWindow(UIScreen.getMainScreen().getBounds());
        // Set the view controller as the root controller for the window.
        window.setRootViewController(rootViewController);
        // Make the window visible.
        window.makeKeyAndVisible();

        internetReachability = NetworkReachability.forInternetConnection();
        internetReachability.setListener(new NetworkReachabilityListener() {
            @Override
            public void onReachable(NetworkReachability reachability) {
                Foundation.log("4G Reachable");
            }

            @Override
            public void onUnreachable(NetworkReachability reachability) {
                Foundation.log("4G UnReachable");
            }

            @Override
            public void onChange(NetworkReachability reachability, SCNetworkReachabilityFlags flags) {
                Foundation.log("4G onChange" + flags);
            }
        });
        internetReachability.startNotifier();

        wifiReachability = NetworkReachability.forLocalWiFi();
        wifiReachability.setListener(new NetworkReachabilityListener() {
            @Override
            public void onReachable(NetworkReachability reachability) {
                Foundation.log("Wifi Reachable");
            }

            @Override
            public void onUnreachable(NetworkReachability reachability) {
                Foundation.log("Wifi UnReachable");
            }

            @Override
            public void onChange(NetworkReachability reachability, SCNetworkReachabilityFlags flags) {
                Foundation.log("Wifi onChange" + flags);
            }
        });
        wifiReachability.startNotifier();

        return true;
    }

    public static void main(String[] args) {
        try (NSAutoreleasePool pool = new NSAutoreleasePool()) {
            UIApplication.main(args, null, Main.class);
        }
    }
}
