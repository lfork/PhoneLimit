package com.lfork.phonelimit.view.main.settings

/**
 * Created by L.Fork
 *
 * @author lfork@vip.qq.com
 * @date 2019/03/02 11:41
 */
object SettingsChangeManager {

    private val listeners = ArrayList<SettingsChangeListener>()

    fun addListener(listener: SettingsChangeListener){
        listeners.add(listener)
    }

    fun removeListener(listener: SettingsChangeListener){
        listeners.remove(listener)
    }


    fun notifyBackgroundChanged(){
        listeners.forEach {
            it.onBackgroundChanged()
        }
    }



    public interface SettingsChangeListener{
        fun onBackgroundChanged()
    }
}