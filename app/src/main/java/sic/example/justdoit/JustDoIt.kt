package sic.example.justdoit

import android.app.Application
import io.realm.Realm

class JustDoIt : Application() {
    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
    }
}