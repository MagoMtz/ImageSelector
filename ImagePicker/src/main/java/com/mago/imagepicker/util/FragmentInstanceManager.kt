package com.mago.imagepicker.util

import androidx.fragment.app.Fragment

/**
 * @author by jmartinez
 * @since 13/11/2020.
 */
class FragmentInstanceManager {

    companion object {
        private val fragmentsMap = hashMapOf<String, Fragment>()
    }

    fun findFragmentByTag(tag: String): Fragment? = fragmentsMap[tag]


    fun addFragmentInstance(tag: String, fragment: Fragment) {
        fragmentsMap[tag] = fragment
    }

    fun removeFragmentInstance(tag: String) {
        fragmentsMap.remove(tag)
    }


}