package com.plumcookingwine.plugins

import com.plumcookingwine.plugins.extensions.PluginExtension
import groovy.json.JsonSlurper
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.gradle.api.Plugin
import org.gradle.api.Project


class PublishPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {

        def uploadExt = project.extensions.create('upload', PluginExtension)

        project.task('upload') {

            doLast {

                def upcount = 0

                project.android.applicationVariants.all { variant ->

                    variant.outputs.each { output ->

                        if (upcount == 0) {
                            def pgyerApiKey = uploadExt.pgyerApiKey

                            if (pgyerApiKey.isEmpty()) {
                                println "pgyerApiKey is can not null"
                                return
                            }

                            def filePath = uploadExt.fileAbsolutePath

                            if (filePath.isEmpty()) {
                                println "fileAbsolutePath is can not null"
                                return
                            }

                            def file = new File(filePath)

                            if (!file.exists()) {
                                println "file is not exists" + filePath
                                return
                            }

                            OkHttpClient client = new OkHttpClient()
                            RequestBody requestBody = new MultipartBody.Builder()
                                    .setType(MultipartBody.FORM)
                                    .addFormDataPart("file", file.name,
                                    RequestBody.create(
                                            MediaType.parse("multipart/form-data"), file
                                    ))
                                    .addFormDataPart("_api_key", uploadExt.pgyerApiKey)
                                    .addFormDataPart("buildUpdateDescription", uploadExt.buildUpdateDescription)
                                    .build()

                            Request request = new Request.Builder()
                                    .header("Authorization", "Client-ID " + UUID.randomUUID())
                                    .url("http://www.pgyer.com/apiv2/app/upload")
                                    .post(requestBody)
                                    .build()

                            def response = client.newCall(request).execute()

                            if (response == null || response.body() == null) {
                                println "蒲公英上传结果失败"
                                return
                            }

                            def json = response.body().string()
                            response.close()
                            def jsonSlurper = new JsonSlurper()
                            def object = jsonSlurper.parseText(json)
                            def code = object.code
                            if (code != 0) {
                                println 'error ---> ' + object.message
                                return
                            }
                            println "upload success!" + "\n应用名称:" + object.data.buildName + "\n文件大小:" + object.data.buildFileSize + "\n版本号:" + object.data.buildVersion + "\n二维码下载地址:" + object.data.buildQRCodeURL + "\napp url:http://www.pgyer.com/" + object.data.buildShortcutUrl + "\n更新描述:" + object.data.buildUpdateDescription
                            upcount++
                        }
                    }
                }
            }
        }
    }
}
