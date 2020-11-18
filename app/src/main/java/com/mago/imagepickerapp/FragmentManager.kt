package com.mago.imagepickerapp

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

/**
 * @author by jmartinez
 * @since 13/11/2020.
 */
internal fun FragmentManager.addFragment(frameId: Int, fragment: Fragment, tag: String) {
    this.beginTransaction().addToBackStack(tag)
        .add(frameId, fragment, tag)
        .commit()
}

internal fun FragmentManager.replaceFragment(frameId: Int, fragment: Fragment, tag: String) {
    this.beginTransaction().disallowAddToBackStack()
        .replace(frameId, fragment, tag)
        .commit()
}

internal fun FragmentManager.removeFragment(fragment: Fragment) {
    this.beginTransaction().remove(fragment)
        .commit()
}