package com.lfork.phonelimitadvanced.data.appinfo

import com.lfork.phonelimitadvanced.LimitApplication
import com.lfork.phonelimitadvanced.LimitApplication.Companion.executeAsyncDataTask
import com.lfork.phonelimitadvanced.LimitApplication.Companion.isFirstOpen
import com.lfork.phonelimitadvanced.data.DataCallback
import com.lfork.phonelimitadvanced.data.common.db.LimitDatabase


/**
 *
 * Created by 98620 on 2018/12/8.
 */
object AppInfoRepository {
    private var mAppInfoDao: AppInfoDao = LimitDatabase.getDataBase().appInfoDao()

    var whiteNameList = HashSet<String>()

    // 【做同步】每一次onResume都需要把数据进行同步：获取App信息列表 -> 对比 -> 删掉/或插入不存在的App
    //只插入，不删除，但是当客户端获取到icon为空时，说明该app已经被卸载 。此时就不显示这个app即可
    fun getAllAppInfo(callback: DataCallback<List<AppInfo>>) {

        executeAsyncDataTask {

            val latestApps: List<AppInfo> = LimitApplication.App.getOrInitAllAppsInfo()
            latestApps.forEach {
                safeInsertAppInfo(it)
            }
            //如果存在话就不管了
            callback.succeed(mAppInfoDao.getAll())
        }
        //应对突然被卸载的APP：如果查不到图标就不显示这个APP了
    }

    fun getWhiteNameApps(callback: DataCallback<List<AppInfo>>) {

        executeAsyncDataTask {
            if (isFirstOpen) {
                LimitApplication.App.getOrInitAllAppsInfo().forEach {
                    safeInsertAppInfo(it)
                }
            }

            whiteNameList.clear()
            val data = mAppInfoDao.getWhiteNameApps()

            data.forEach {
                whiteNameList.add(it.packageName)
            }

            whiteNameList.add("com.lfork.phonelimitadvanced")

            callback.succeed(data)
        }
    }

    fun update(appInfo: AppInfo, callback: DataCallback<String>) {
        executeAsyncDataTask {
            val result = mAppInfoDao.update(appInfo)
            if (result > 0) {
                callback.succeed("操作成功")
            } else {
                callback.succeed("操作失败")
            }
        }
    }

    fun updateAll(vararg appInfo: AppInfo, callback: DataCallback<String>) {
        executeAsyncDataTask {
            mAppInfoDao.update(*appInfo)
        }
    }

    @Synchronized
    private fun safeInsertAppInfo(it:AppInfo){
        if (mAppInfoDao.getAppInfo(it.packageName) == null) {
            //不存在就插入 ：应对突然新安装的APP
            mAppInfoDao.insert(it)
        }
    }

//    private class insertAsyncTask internal constructor(private val mAsyncTaskDao: AppInfoDao) :
//        AsyncTask<AppInfo, Void, Void>() {
//
//        override fun doInBackground(vararg params: AppInfo): Void? {
//            mAsyncTaskDao.insert(params[0])
//            return null
//        }
//    }
}