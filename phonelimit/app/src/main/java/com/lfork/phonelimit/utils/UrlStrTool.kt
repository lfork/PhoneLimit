package com.lfork.phonelimit.utils

import java.util.regex.Pattern

/**
 *
 * Created by 98620 on 2018/12/16.
 */
/**
 * 判断字符串是否为URL
 * @param urls 用户头像key
 * @return true:是URL、false:不是URL
 */
fun isHttpUrl(urls: String): Boolean {
    var isurl = false
    val regex = "(((https|http)?://)?([a-z0-9]+[.])|(www.))" + "\\w+[.|\\/]([a-z0-9]{0,})?[[.]([a-z0-9]{0,})]+((/[\\S&&[^,;\u4E00-\u9FA5]]+)+)?([.][a-z0-9]{0,}+|/?)"//设置正则表达式

    val pat = Pattern.compile(regex.trim { it <= ' ' })//比对
    val mat = pat.matcher(urls.trim { it <= ' ' })
    isurl = mat.matches()//判断是否匹配
    if (isurl) {
        isurl = true
    }
    return isurl
}

/*
**
* Extracts the domain name from `url`
* by means of String manipulation
* rather than using the [URI] or [URL] class.
*
* @param url is non-null.
* @return the domain name within `url`.
*/
fun getUrlDomainName(url: String): String {
    var domainName = url
    var index = domainName.indexOf("://")
    if (index != -1) {
        //keep everything after the"://"
        domainName = domainName.substring(index + 3)
    }
    index = domainName.indexOf('/')
    if (index != -1) {
        //keep everything before the '/'
        domainName = domainName.substring(0, index)
    }
    //check for and remove a preceding 'www'
    //followed by any sequence of characters (non-greedy)
    //followed by a '.'
    //from the beginning of the string
//    domainName = domainName.replaceFirst("^www.*?.".toRegex(), "")
    return domainName
}

