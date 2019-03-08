package pro.avazquez;

import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.foundation.Foundation;
import org.robovm.apple.foundation.NSLocale;
import org.robovm.apple.foundation.NSString;
import org.robovm.apple.uikit.NSTextAlignment;
import org.robovm.apple.uikit.UIButton;
import org.robovm.apple.uikit.UIButtonType;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIControl;
import org.robovm.apple.uikit.UIControlState;
import org.robovm.apple.uikit.UIEvent;
import org.robovm.apple.uikit.UIFont;
import org.robovm.apple.uikit.UILabel;
import org.robovm.apple.uikit.UIView;
import org.robovm.apple.uikit.UIViewController;
import org.robovm.objc.annotation.CustomClass;
import org.robovm.objc.annotation.IBOutlet;

@CustomClass ("MyViewController")
public class MyViewController extends UIViewController {

    @IBOutlet UILabel languageName;
    @IBOutlet UILabel languageText;
    @IBOutlet UILabel languageCurrency;

    @Override
    public void viewDidLoad() {
        super.viewDidLoad();
        Foundation.log(NSLocale.getPreferredLanguages().toString());
        //NSLocalizedString(“HELLO_WORLD”, comment:“Hello world”)
        //NSString.getLocalizedString("HELLO_WORLD");
        languageCurrency.setText(NSString.getLocalizedString("Moneda"));
        languageName.setText(NSString.getLocalizedString("Idioma"));
        languageText.setText(NSString.getLocalizedString("Saludo"));
        }
        }
