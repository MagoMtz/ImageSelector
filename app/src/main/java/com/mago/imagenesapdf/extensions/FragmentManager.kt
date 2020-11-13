package com.mago.imagenesapdf.extensions

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