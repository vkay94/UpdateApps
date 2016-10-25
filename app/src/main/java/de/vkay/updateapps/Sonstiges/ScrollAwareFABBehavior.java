package de.vkay.updateapps.Sonstiges;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;

import de.vkay.updateapps.AppUebersicht.AUFeedback;

/**
 * Ist dafür zuständig, dass der FAB beim Runterscrollen verschwindet und wieder auftaucht
 * Setze dabei layout_behavior in XML für den FAB als Pfad (vkay.updateapps.Sonstiges. ... )
 */
public class ScrollAwareFABBehavior extends FloatingActionButton.Behavior {

    public ScrollAwareFABBehavior(Context context, AttributeSet attrs) {
        super();
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout,
                                       FloatingActionButton child, View directTargetChild, View target, int nestedScrollAxes) {
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL ||
                super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target,
                        nestedScrollAxes);
    }

    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child,
                               View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed,
                dyUnconsumed);

        if (dyConsumed > 5 && child.getVisibility() == View.VISIBLE) {
            child.hide();
        } else if (dyConsumed < -10 && child.getVisibility() != View.VISIBLE && AUFeedback.fabVisible) {
            child.show();
        }
    }
}
