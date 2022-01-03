package com.theost.workchat.ui.widgets

import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import vivid.money.elmslie.android.screen.ElmDelegate
import vivid.money.elmslie.android.screen.ElmScreen
import vivid.money.elmslie.android.storeholder.LifecycleAwareStoreHolder
import vivid.money.elmslie.android.storeholder.StoreHolder

abstract class ElmBottomSheetFragment<Event : Any, Effect : Any, State : Any> : BottomSheetDialogFragment(),
    ElmDelegate<Event, Effect, State> {

    @Suppress("LeakingThis", "UnusedPrivateMember")
    private val elm = ElmScreen(this, lifecycle) { requireActivity() }

    protected val store
        get() = storeHolder.store

    override val storeHolder: StoreHolder<Event, Effect, State> by fastLazy {
        LifecycleAwareStoreHolder(lifecycle, ::createStore)
    }
}

internal fun <T> fastLazy(initializer: () -> T) = lazy(LazyThreadSafetyMode.NONE) { initializer() }