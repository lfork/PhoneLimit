package com.lfork.phonelimitadvanced.utils

import android.os.Environment
import android.util.Log

import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.io.Writer

/*
 * Created by 98620 on 2017/11/3.
 */

/**
 * @author 98620
 */
object FileHelper {

    private val TAG = "PersonsFileHelper"
    var ExternalCacheDirStringURL: String
    var SDRootPath: String

    var KeyPath: String? = null
    var ValuePath: String? = null
    private val IsDirInitialed = false

    init {
        SDRootPath = Environment.getExternalStorageDirectory().toString()
        ExternalCacheDirStringURL = Environment.getDownloadCacheDirectory().toString()
    }


    /**
     * @param data     传入需要写入文件的数据
     * @param filePath 传入文件路径
     * @return 文件操作成功返回true 否则 返回false
     */
    private fun write(data: String?, filePath: String): Boolean {
        if (data == null) {
            return false
        }
        //资源申请
        val file = File(filePath)
        var writer: Writer? = null
        try {
            file.createNewFile()
            // 这样写的话可以指定文本的编码格式 ***** 默认为utf-8
            writer = OutputStreamWriter(FileOutputStream(file))
            val bufferedWriter = BufferedWriter(writer)
            bufferedWriter.write(data)
            bufferedWriter.close()
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        } finally {
            //资源释放
            if (writer != null) {
                try {
                    writer.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
        }
        return true
    }

    fun load(filePath: String?): String? {


        if (filePath == null) {
            return null
        }
        val file = File(filePath)


        if (!file.exists()) {
            return null
        }
        val result = StringBuilder()

        //        byte[] buffer = new byte[3000000];


        var `in`: BufferedReader? = null
        try {
            // 当该文件不存在时再创建一个新的文件
            file.createNewFile()

            val fis = FileInputStream(file)
            //            int i = 0, temp = 0;
            //            temp = fis.read();
            //            while (temp != -1) {
            //                buffer[i] = (byte) temp;
            //                temp = fis.read();
            //            }

            // 这样写的话可以指定文本的编码格式 *****
            val isr = InputStreamReader(fis)

            `in` = BufferedReader(isr)

            // 这里是按照字符流进行的读取

            var str: String? = `in`.readLine()
            while (str != null) {
                result.append(str)
                str = `in`.readLine()
                //            result.append(Arrays.toString(buffer));
            }


        } catch (e: IOException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        } finally {
            if (`in` != null) {
                try {
                    `in`.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
        }

        return result.toString()

    }

    private fun delete(filePath: String): Boolean {
        return File(filePath).delete()
        //Java无法直接删除一个非空目录
    }

    /**
     * 把一个文件转化为字节
     *
     * @param file
     * @return byte[]
     * @throws Exception
     */
    @Throws(Exception::class)
    fun load(file: File?): ByteArray? {
        var bytes: ByteArray? = null
        if (file != null) {
            val `is` = FileInputStream(file)
            val length = file.length().toInt()
            if (length > Integer.MAX_VALUE)
            //当文件的长度超过了int的最大值
            {
                println("this file is max ")
                return null
            }
            bytes = ByteArray(length)
            var offset = 0
            var numRead = `is`.read(bytes, offset, bytes.size - offset)
            while (offset < bytes.size && numRead >= 0) {
                offset += numRead
                numRead = `is`.read(bytes, offset, bytes.size - offset)
            }
            //如果得到的字节长度和file实际的长度不一致就可能出错了
            if (offset < bytes.size) {
                println("file length is error")
                return null
            }
            `is`.close()
        }
        return bytes
    }

    fun write(data: ByteArray?, filePath: String): Boolean {
        if (data == null) {
            return false
        }
        var out: FileOutputStream? = null
        //资源申请
        val file = File(filePath)
        try {
            file.createNewFile()
            // 这样写的话可以指定文本的编码格式 ***** 默认为utf-8
            out = FileOutputStream(file)
            out.write(data)
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        } finally {
            if (out != null) {
                try {
                    out.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
            //资源释放


        }
        return true

    }

    fun listDirectory(path:String):String{
        val pathInfo = StringBuilder()

        val file = File(path)        //获取其file对象
        Log.d("Test", file.path)
        val fs = file.listFiles()    //遍历path下的文件和目录，放在File数组中
        fs?.forEach {
            pathInfo.append(it.name).append("\n")
        }
        return pathInfo.toString()
    }


}
