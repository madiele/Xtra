package com.github.exact7.xtra.ui.main

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.exact7.xtra.R
import com.github.exact7.xtra.model.LoggedIn
import com.github.exact7.xtra.model.NotValidated
import com.github.exact7.xtra.model.User
import com.github.exact7.xtra.repository.AuthRepository
import com.github.exact7.xtra.ui.login.LoginActivity
import com.github.exact7.xtra.util.Event
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import javax.inject.Inject

class MainViewModel @Inject constructor(
        private val authRepository: AuthRepository
): ViewModel() {

    private val _user = MutableLiveData<User>()
    val user: LiveData<User>
        get() = _user

    private val playerMaximized = MutableLiveData<Boolean>()
    private val _isNetworkAvailable = MutableLiveData<Event<Boolean>>()
    val isNetworkAvailable: LiveData<Event<Boolean>>
        get() = _isNetworkAvailable

    val isPlayerMaximized: Boolean
        get() = playerMaximized.value == true

    var isPlayerOpened = false
        private set
    private val _checkedValidity = MutableLiveData<Boolean>()
    val checkedValidity: LiveData<Boolean>
        get() = _checkedValidity

    private val compositeDisposable = CompositeDisposable()

    fun setUser(user: User) {
        if (_user.value == null) {
            _user.value = user.let {
                if (it is NotValidated) {
                    if (checkedValidity.value != true) {
                        it
                    } else {
                        LoggedIn(it)
                    }
                } else {
                    it
                }
            }
        }
    }

    fun onMaximize() {
        playerMaximized.value = true
    }

    fun onMinimize() {
        if (playerMaximized.value != false)
            playerMaximized.value = false
    }

    fun onPlayerStarted() {
        isPlayerOpened = true
        playerMaximized.value = true
    }

    fun onPlayerClosed() {
        isPlayerOpened = false
        playerMaximized.value = false
    }

    fun setNetworkAvailable(available: Boolean) {
        if (_isNetworkAvailable.value?.peekContent() != available) {
            _isNetworkAvailable.value = Event(available)
        }
    }

    fun validate(activity: Activity) {
        val user = user.value
        if (user is NotValidated && checkedValidity.value != true) {
            authRepository.validate(user.token)
                    .subscribe({
                        _checkedValidity.value = true
                        _user.value = LoggedIn(user)
                    }, {
                        _checkedValidity.value = true
                        with(activity) {
                            Toast.makeText(this, getString(R.string.token_expired), Toast.LENGTH_LONG).show()
                            startActivityForResult(Intent(this, LoginActivity::class.java).putExtra("expired", true), 2) //TODO if player don't start <- dont need this TODO anymore?
                        }
                    })
                    .addTo(compositeDisposable)
        }
    }

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }
}