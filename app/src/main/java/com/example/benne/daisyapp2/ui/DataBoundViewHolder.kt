package com.example.benne.daisyapp2.ui

import android.databinding.*
import android.support.v7.widget.*
import com.example.benne.daisyapp2.*
import com.example.benne.daisyapp2.dataBindings.*


/**
 * Created by benne on 13/01/2018.
 */
class DataBoundViewHolder(
    private val bindings: ViewDataBinding)
    : RecyclerView.ViewHolder(bindings.root) {

    fun bind(item : Any, listener: UserActionListener? = null) {
        if (listener != null) {
            bindings.setVariable(BR.listener, listener)
        }

        bindings.setVariable(BR.item, item)
        bindings.executePendingBindings()
    }
}