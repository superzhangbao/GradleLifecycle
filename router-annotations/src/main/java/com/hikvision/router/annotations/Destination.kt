package com.hikvision.router.annotations

/**
 *  author : zhangbao
 *  date : 8/25/21 11:39 PM
 *  description :
 */

@Target(AnnotationTarget.TYPE,AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
annotation class Destination(val url:String = "",val description:String)