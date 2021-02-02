package com.example.benne.daisyapp2.ui

import androidx.databinding.*
import androidx.appcompat.widget.*
import com.example.benne.daisyapp2.*
import com.example.benne.daisyapp2.dataBindings.*


/**
 * Created by benne on 13/01/2018.
 */
class DataBoundViewHolder(
    private val bindings: ViewDataBinding)
    : androidx.recyclerview.widget.RecyclerView.ViewHolder(bindings.root) {

    fun bind(item : Any, listener: UserActionListener? = null) {
        if (listener != null) {
            bindings.setVariable(BR.listener, listener)
        }
        bindings.setVariable(BR.item, item)
        bindings.executePendingBindings()
    }
}